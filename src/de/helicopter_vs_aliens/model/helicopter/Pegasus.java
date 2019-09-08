package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.MyMath;

import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.FIRE_RATE;

public final class Pegasus extends Helicopter
{
    static final int[]
            INTERPHASE_GENERATOR_ALPHA = {110, 70}; // Alpha-Wert zum Zeichnen des Helikopters bei Tag- und Nachtzeit nach einem Dimensionssprung
        
    private boolean
        hasInterphaseGenerator;				// = true: Helikopter verfügt über einen Interphasen-Generator
    
    
    @Override
    public HelicopterTypes getType()
    {
        return HelicopterTypes.PEGASUS;
    }

    @Override
    void updateTimer()
    {
        if(this.empTimer > 0){this.empTimer--;}
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
        return this.empTimer == 0 && this.hasEnoughEnergyForAbility();
    }

    @Override
    public void useEnergyAbility(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
        this.releaseEMP(explosion);
    }

    private void releaseEMP(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
        this.empTimer = 67;
        this.energy -= this.hasUnlimitedEnergy() ? 0 : this.spellCosts;
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
        this.interphaseGeneratorTimer = 0;
    }

    @Override
    public boolean canBeTractored()
    {
        return this.interphaseGeneratorTimer <= this.shiftTime
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
            this.shiftTime
                    = MyMath.shiftTime( this.levelOfUpgrade[FIRE_RATE.ordinal()]
                    + (poweredUp ? FIRE_RATE_POWERUP_LEVEL : 0));
        }
    }

    @Override
    void shoot(EnumMap<CollectionSubgroupTypes, LinkedList<Missile>> missiles)
    {
        super.shoot(missiles);
        if(this.hasInterphaseGenerator)
        {
            Audio.phaseShift.stop();
            this.interphaseGeneratorTimer = 0;
        }
    }

    @Override
    boolean isShootingStunningMissile()
    {
        if(this.interphaseGeneratorTimer > this.shiftTime)
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
    public void resetState(boolean resetStartPos)
    {
        super.resetState(resetStartPos);
        this.empTimer = 0;
    }
    
    @Override
    public boolean basicCollisionRequirementsSatisfied(Enemy e)
    {
        return this.interphaseGeneratorTimer <= this.shiftTime
                && super.basicCollisionRequirementsSatisfied(e);
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
        return this.hasMaxUpgradeLevel[StandardUpgradeTypes.FIRE_RATE.ordinal()];
    }
    
    @Override
    public boolean canBeHit()
    {
        return interphaseGeneratorTimer <= this.shiftTime;
    }
    
    private void updateInterphaseGenerator()
    {
        this.interphaseGeneratorTimer++;
        if(this.interphaseGeneratorTimer == this.shiftTime + 1)
        {
            Audio.play(Audio.phaseShift);
            if(this.tractor != null){this.stopTractor();}
        }
    }
    
    @Override
    public boolean isLocationAdaptionApproved(Enemy enemy)
    {
        return  super.isLocationAdaptionApproved(enemy)
                && this.interphaseGeneratorTimer <= this.shiftTime;
    }
    
    @Override
    public void initMenuEffect(int position)
    {
        super.initMenuEffect(position);
        // TODO empWave sollte nach Menu ausgelagert werden, da es nur hier verwendet wird
        this.empWave = Explosion.createStartscreenExplosion(position);
    }
    
    @Override
    public void stoptMenuEffect()
    {
        this.empWave = null;
    }
}