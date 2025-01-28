package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.control.timer.VariableTimer;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;


public final class Pegasus extends Helicopter
{
    private static final int[]
        SHIFT_TIME = {225, 185, 151, 124, 102, 83, 68, 56, 45, 37, 30, 25};
    
    private static final int
        EMP_TIMER_DURATION = 67;
    
    public Explosion
        // TODO sollte private werden
        empWave;			// Pegasus-Klasse: Referenz auf zuletzt ausgelöste EMP-Schockwelle
    
    private int
        shiftTime;                  // Zeit [frames], die verstreichen muss, bis der Interphasengenerator aktiviert wird

    private final Timer
        empTimer = new Timer(EMP_TIMER_DURATION);   // Timer stellt sicher, dass eine Mindestzeit zwischen zwei ausgelösten EMPs liegt

    private final VariableTimer
        interphaseGeneratorTimer = new VariableTimer(shiftTime);   // Zeit [frames] seit der letzten Offensiv-Aktion; bestimmt, ob der Interphasengenerator aktiviert ist

    private boolean
        hasInterphaseGenerator;		// = true: Helikopter verfügt über einen Interphasen-Generator
    
    
    Pegasus(GameRessourceProvider gameRessourceProvider)
    {
        super(gameRessourceProvider);
    }
    
    @Override
    public HelicopterType getType()
    {
        return HelicopterType.PEGASUS;
    }

    @Override
    void updateTimer()
    {
        //if(empTimer > 0){empTimer--;}
        super.updateTimer();
        if(hasInterphaseGenerator && !isDamaged)
        {
            updateInterphaseGenerator();
        }
    }

    @Override
    public boolean hasFifthSpecial()
    {
        return hasInterphaseGenerator;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        hasInterphaseGenerator = true;
        adjustFireRate(hasBoostedFireRate());
    }

    @Override
    public void updateUnlockedHelicopters()
    {
        if(!HelicopterType.PHOENIX.hasReachedLevel20())
        {
            Window.unlock(HelicopterType.OROCHI);
        }
        else if(!HelicopterType.ROCH.hasReachedLevel20())
        {
            Window.unlock(HelicopterType.KAMAITACHI);
        }
    }

    @Override
    public boolean isEnergyAbilityActivatable()
    {
        return !empTimer.isActive() && hasEnoughEnergyForAbility();
    }

    @Override
    public void useEnergyAbility()
    {
        releaseEMP(getGameRessourceProvider());
    }

    private void releaseEMP(GameRessourceProvider gameRessourceProvider)
    {
        empTimer.start();
        consumeSpellCosts();
        Audio.play(Audio.emp);
        gameRessourceProvider.getExplosionController()
                             .start(
                                 (int)(getX()
                                     + (isMovingLeft
                                     ? FOCAL_POINT_X_LEFT
                                     : FOCAL_POINT_X_RIGHT)),
                                 (int)(getY()
                                     + FOCAL_POINT_Y_EXP),
                                 ExplosionType.EMP,
                                 false);
        restartInterphaseGenerator();
    }

    private void restartInterphaseGenerator()
    {
        interphaseGeneratorTimer.start(shiftTime);
    }

    @Override
    public boolean canBeStoppedByTractorBeam()
    {
        return isInPhase()
               && super.canBeStoppedByTractorBeam();
    }

    @Override
    public ExplosionType getCurrentExplosionTypeOfMissiles()
    {
        if(isShootingStunningMissile()){return ExplosionType.PHASE_SHIFT;}
        return ExplosionType.ORDINARY;
    }

    @Override
    public void adjustFireRate(boolean poweredUp)
    {
        super.adjustFireRate(poweredUp);
        if(hasInterphaseGenerator)
        {
            shiftTime = shiftTime(calculateSumOfFireRateBooster(poweredUp));
        }
    }
    
    private static int shiftTime(int n)
    {
        if(n > 1 && n < 14){return SHIFT_TIME[n-2];}
        return 500;
    }

    @Override
    void shoot()
    {
        super.shoot();
        if(hasInterphaseGenerator)
        {
            Audio.phaseShift.stop();
            restartInterphaseGenerator();
        }
    }

    @Override
    boolean isShootingStunningMissile()
    {
        return !isInPhase();
    }
    
    @Override
    void resetFifthSpecial()
    {
        hasInterphaseGenerator = false;
    }
    
    @Override
    public boolean basicCollisionRequirementsSatisfied(Enemy enemy)
    {
        return isInPhase()
                && super.basicCollisionRequirementsSatisfied(enemy);
    }

    public boolean isInPhase()
    {
        return interphaseGeneratorTimer.isActive() || !hasInterphaseGenerator || WindowManager.window != WindowType.GAME;
    }

    @Override
    public void crash()
    {
        if(hasInterphaseGenerator){Audio.phaseShift.stop();}
        super.crash();
    }
    
    @Override
    public boolean isFifthSpecialOnMaximumStrength()
    {
        return hasMaximumUpgradeLevelFor(StandardUpgradeType.FIRE_RATE);
    }
    
    @Override
    public boolean canBeHit()
    {
        return isInPhase();
    }
    
    private void updateInterphaseGenerator()
    {
        if(interphaseGeneratorTimer.hasExpired())
        {
            Audio.play(Audio.phaseShift);
            if(tractor != null){stopTractor();}
        }
    }
    
    @Override
    public boolean isLocationAdaptionApproved(Enemy enemy)
    {
        return  super.isLocationAdaptionApproved(enemy)
                && isInPhase();
    }
    
    @Override
    public void initMenuEffect(int position)
    {
        super.initMenuEffect(position);
        // TODO empWave sollte nach Menu ausgelagert werden, da es nur hier verwendet wird - wirklich? Überprüfen!
        empWave = getGameRessourceProvider().getExplosionController().createStartScreenExplosion(position);
    }
    
    @Override
    public void stopMenuEffect()
    {
        empWave = null;
    }
    
    @Override
    public String getTypeSpecificDebuggingOutput()
    {
        return String.format("emp Timer: %d; phaseShift Timer: %d", empTimer.getTimeLeft(), interphaseGeneratorTimer.getTimeLeft());
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        empTimer.reset();
        empWave = null;
        restartInterphaseGenerator();
    }
    
    @Override
    public void prepareForMission()
    {
        super.prepareForMission();
        restartInterphaseGenerator();
    }
    
    @Override
    void generalInitialization()
    {
        super.generalInitialization();
        empWave = null;
    }
    
    @Override
    public void typeSpecificActionOn(Enemy enemy)
    {
        enemy.checkForEmpStrike(this);
    }
    
    public void rewardAndCountEmpKill(int reward)
    {
        empWave.countKill();
        empWave.increaseRewardBy(reward);
    }
    
    @Override
    public void handleExplosionEnd()
    {
        empWave = null;
    }
}