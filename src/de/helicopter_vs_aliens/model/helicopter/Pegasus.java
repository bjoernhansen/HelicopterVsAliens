package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.control.timer.VariableTimer;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Coloration;

import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;
import static de.helicopter_vs_aliens.gui.WindowType.STARTSCREEN;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.FIRE_RATE;


public final class Pegasus extends Helicopter
{
    private static final int[]
        INTERPHASE_GENERATOR_ALPHA = {110, 70}, // Alpha-Wert zum Zeichnen des Helikopters bei Tag- und Nachtzeit nach einem Dimensionssprung
        SHIFT_TIME = {225, 185, 151, 124, 102, 83, 68, 56, 45, 37, 30, 25};
    
    private static final int
        EMP_TIMER_DURATION = 67;

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
    }

    @Override
    public void updateUnlockedHelicopters()
    {
        if(!Events.reachedLevelTwenty[PHOENIX.ordinal()])
        {
            Menu.unlock(OROCHI);
        }
        else if(!Events.reachedLevelTwenty[ROCH.ordinal()])
        {
            Menu.unlock(KAMAITACHI);
        }
    }

    @Override
    public boolean isEnergyAbilityActivatable()
    {
        return !this.empTimer.isActive() && this.hasEnoughEnergyForAbility();
    }

    @Override
    public void useEnergyAbility(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
    {
        this.releaseEMP(explosion);
    }

    private void releaseEMP(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
    {
        this.empTimer.start();
        this.currentEnergy -= this.hasUnlimitedEnergy() ? 0 : this.spellCosts;
        Audio.play(Audio.emp);
        Explosion.start(explosion,
                this,
                (int)(this.bounds.getX()
                        + (this.isMovingLeft
                        ? FOCAL_PNT_X_LEFT
                        : FOCAL_PNT_X_RIGHT)),
                (int)(this.bounds.getY()
                        + FOCAL_PNT_Y_EXP),
                EMP,
                false);
        this.restartInterphaseGenerator();
    }

    private void restartInterphaseGenerator()
    {
        this.interphaseGeneratorTimer.start(this.shiftTime);
    }

    @Override
    public boolean canBeTractored()
    {
        return this.isInPhase()
                && super.canBeTractored();
    }

    @Override
    public ExplosionTypes getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
    {
        if(stunningMissile){return PHASE_SHIFT;}
        return ORDINARY;
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
    void shoot(EnumMap<CollectionSubgroupType, LinkedList<Missile>> missiles)
    {
        super.shoot(missiles);
        if(this.hasInterphaseGenerator)
        {
            Audio.phaseShift.stop();
            this.restartInterphaseGenerator();
        }
    }

    @Override
    boolean isShootingStunningMissile()
    {
        if(!this.isInPhase())
        {
            return true;
        }
        return false;
    }
    
    @Override
    void resetFifthSpecial()
    {
        this.hasInterphaseGenerator = false;
    }
    
    @Override
    public boolean basicCollisionRequirementsSatisfied(Enemy e)
    {
        return this.isInPhase()
                && super.basicCollisionRequirementsSatisfied(e);
    }

    private boolean isInPhase()
    {
        return this.interphaseGeneratorTimer.isActive() || !this.hasInterphaseGenerator || Menu.window != GAME;
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
        return this.hasMaximumUpgradeLevelFor(FIRE_RATE);
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
        this.empWave = Explosion.createStartscreenExplosion(position);
    }
    
    @Override
    public void stoptMenuEffect()
    {
        this.empWave = null;
    }
    
    @Override
    void determineInputColors()
    {
        super.determineInputColors();
        if(!this.isInPhase())
        {
            // TODO HashMap daraus machen und hier verwenden
            this.inputColorCannon = Coloration.setAlpha(this.inputColorCannon, INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputColorHull =   Coloration.setAlpha(this.inputColorHull, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputColorWindow = Coloration.setAlpha(this.inputColorWindow, INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()] );
            this.inputColorFuss1 =  Coloration.setAlpha(this.inputColorFuss1, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputColorFuss2 =  Coloration.setAlpha(this.inputColorFuss2, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputGray = 		Coloration.setAlpha(this.inputGray, 		INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputLightGray = 	Coloration.setAlpha(this.inputLightGray, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputLamp = 		Coloration.setAlpha(this.inputLamp, 		INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
        }
    }
    
    @Override
    void paintComponents(Graphics2D g2d, int left, int top)
    {
        super.paintComponents(g2d, left, top);
        
        // EMP wave animation in start menu
        if(Menu.window == STARTSCREEN
            && Menu.effectTimer[PEGASUS.ordinal()] > 0
            && this.empWave != null)
        {
            if(this.empWave.time >= this.empWave.maxTime)
            {
                this.empWave = null;
            }
            else
            {
                this.empWave.update();
                this.empWave.paint(g2d);
            }
        }
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
}