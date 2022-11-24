package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.control.timer.VariableTimer;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;

import java.util.Map;
import java.util.Queue;


public final class Pegasus extends Helicopter
{
    private static final int[]
        SHIFT_TIME = {225, 185, 151, 124, 102, 83, 68, 56, 45, 37, 30, 25};
    
    private static final int
        EMP_TIMER_DURATION = 67;
    
    public Explosion
        empWave;			// Pegasus-Klasse: Referenz auf zuletzt ausgelöste EMP-Schockwelle
    
    private int
        shiftTime;                  // Zeit [frames], die verstreichen muss, bis der Interphasengenerator aktiviert wird

    private final Timer
        empTimer = new Timer(EMP_TIMER_DURATION);   // Timer stellt sicher, dass eine Mindestzeit zwischen zwei ausgelösten EMPs liegt

    private final VariableTimer
        interphaseGeneratorTimer = new VariableTimer(this.shiftTime);   // Zeit [frames] seit der letzten Offensiv-Aktion; bestimmt, ob der Interphasengenerator aktiviert ist

    private boolean
        hasInterphaseGenerator;		// = true: Helikopter verfügt über einen Interphasen-Generator
    
    
    
    @Override
    public HelicopterType getType()
    {
        return HelicopterType.PEGASUS;
    }

    @Override
    void updateTimer()
    {
        //if(this.empTimer > 0){this.empTimer--;}
        super.updateTimer();
        if(this.hasInterphaseGenerator && !this.isDamaged)
        {
            this.updateInterphaseGenerator();
        }
    }

    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasInterphaseGenerator;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasInterphaseGenerator = true;
        this.adjustFireRate(this.hasBoostedFireRate());
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
        return !this.empTimer.isActive() && this.hasEnoughEnergyForAbility();
    }

    @Override
    public void useEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        this.releaseEMP(gameRessourceProvider.getExplosions());
    }

    private void releaseEMP(Map<CollectionSubgroupType, Queue<Explosion>> explosions)
    {
        this.empTimer.start();
        this.consumeSpellCosts();
        Audio.play(Audio.emp);
        Explosion.start(explosions,
                this,
                (int)(this.getX()
                        + (this.isMovingLeft
                        ? FOCAL_POINT_X_LEFT
                        : FOCAL_POINT_X_RIGHT)),
                (int)(this.getY()
                        + FOCAL_POINT_Y_EXP),
                ExplosionType.EMP,
                false);
        this.restartInterphaseGenerator();
    }

    private void restartInterphaseGenerator()
    {
        this.interphaseGeneratorTimer.start(this.shiftTime);
    }

    @Override
    public boolean canBeStoppedByTractorBeam()
    {
        return this.isInPhase()
               && super.canBeStoppedByTractorBeam();
    }

    @Override
    public ExplosionType getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
    {
        if(stunningMissile){return ExplosionType.PHASE_SHIFT;}
        return ExplosionType.ORDINARY;
    }

    @Override
    public void adjustFireRate(boolean poweredUp)
    {
        super.adjustFireRate(poweredUp);
        if(this.hasInterphaseGenerator)
        {
            this.shiftTime = shiftTime(this.calculateSumOfFireRateBooster(poweredUp));
        }
    }
    
    public static int shiftTime(int n)
    {
        if(n > 1 && n < 14){return SHIFT_TIME[n-2];}
        return 500;
    }

    @Override
    void shoot(GameRessourceProvider gameRessourceProvider)
    {
        super.shoot(gameRessourceProvider);
        if(this.hasInterphaseGenerator)
        {
            Audio.phaseShift.stop();
            this.restartInterphaseGenerator();
        }
    }

    @Override
    boolean isShootingStunningMissile()
    {
        return !this.isInPhase();
    }
    
    @Override
    void resetFifthSpecial()
    {
        this.hasInterphaseGenerator = false;
    }
    
    @Override
    public boolean basicCollisionRequirementsSatisfied(Enemy enemy)
    {
        return this.isInPhase()
                && super.basicCollisionRequirementsSatisfied(enemy);
    }

    public boolean isInPhase()
    {
        return this.interphaseGeneratorTimer.isActive() || !this.hasInterphaseGenerator || WindowManager.window != WindowType.GAME;
    }

    @Override
    public void crash()
    {
        if(this.hasInterphaseGenerator){Audio.phaseShift.stop();}
        super.crash();
    }
    
    @Override
    public boolean isFifthSpecialOnMaximumStrength()
    {
        return this.hasMaximumUpgradeLevelFor(StandardUpgradeType.FIRE_RATE);
    }
    
    @Override
    public boolean canBeHit()
    {
        return this.isInPhase();
    }
    
    private void updateInterphaseGenerator()
    {
        if(this.interphaseGeneratorTimer.hasExpired())
        {
            Audio.play(Audio.phaseShift);
            if(this.tractor != null){this.stopTractor();}
        }
    }
    
    @Override
    public boolean isLocationAdaptionApproved(Enemy enemy)
    {
        return  super.isLocationAdaptionApproved(enemy)
                && this.isInPhase();
    }
    
    @Override
    public void initMenuEffect(int position)
    {
        super.initMenuEffect(position);
        // TODO empWave sollte nach Menu ausgelagert werden, da es nur hier verwendet wird - wirklich? Überprüfen!
        this.empWave = Explosion.createStartScreenExplosion(position);
    }
    
    @Override
    public void stopMenuEffect()
    {
        this.empWave = null;
    }
    
    @Override
    public String getTypeSpecificDebuggingOutput()
    {
        return String.format("emp Timer: %d; phaseShift Timer: %d", this.empTimer.getTimeLeft(), this.interphaseGeneratorTimer.getTimeLeft());
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        this.empTimer.reset();
        this.empWave = null;
        this.restartInterphaseGenerator();
    }
    
    @Override
    public void prepareForMission()
    {
        super.prepareForMission();
        this.restartInterphaseGenerator();
    }
    
    @Override
    void generalInitialization()
    {
        super.generalInitialization();
        this.empWave = null;
    }
    
    @Override
    public void typeSpecificActionOn(Enemy enemy, GameRessourceProvider gameRessourceProvider)
    {
        enemy.checkForEmpStrike(gameRessourceProvider, this);
    }
}