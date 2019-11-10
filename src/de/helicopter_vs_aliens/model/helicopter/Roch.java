package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.MyColor;

import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.TimesOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.WindowTypes.STARTSCREEN;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.JUMBO;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.PEGASUS;

public final class Roch extends Helicopter
{
    public static final int
        JUMBO_MISSILE_COSTS = 25000,
        ROCH_SECOND_CANNON_COSTS = 225000;
    
    private static final int
        POWER_SHIELD_ACTIVATION_THRESHOLD = 75,
        REDUCED_ENERGY_DRAIN = 10;

    private static final float
        JUMBO_MISSILE_DAMAGE_FACTOR = 2.36363637f,	// Faktor, um den sich die Schadenswirkung der Raketen erhöht, nachdem das Jumbo-Raketen-Spezial-Upgrade erworben wurde
        POWER_SHIELD_E_LOSS_RATE = -0.06f,
        REDUCED_BASE_PROTECTION_FACTOR = 0.65f;
        
    private boolean
        hasJumboMissiles,	        // = true: Helikopter verschießt Jumbo-Raketen
        isPowerShieldActivated;	    // = true: Power-Shield ist aktiviert

    
    @Override
    public HelicopterTypes getType()
    {
        return ROCH;
    }
    
    @Override
    public int getPiercingWarheadsCosts()
    {
         return CHEAP_SPECIAL_COSTS;
    }

    @Override
    public ExplosionTypes getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
    {
        if(this.hasJumboMissiles){return JUMBO;}
        else{return ORDINARY;}
    }
    
    @Override
    public void obtainSomeUpgrades()
    {
        this.hasPiercingWarheads = true;
        super.obtainSomeUpgrades();
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasJumboMissiles;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasJumboMissiles = true;
        this.setCurrentBaseFirepower();
    }

    @Override
    public void updateUnlockedHelicopters()
    {
        if(!Events.reachedLevelTwenty[PEGASUS.ordinal()])
        {
            Menu.unlock(KAMAITACHI);
        }
    }

    @Override
    public boolean isEnergyAbilityActivatable()
    {
        return this.energy >= POWER_SHIELD_ACTIVATION_THRESHOLD && this.hasEnoughEnergyForAbility();
    }

    @Override
    public void tryToUseEnergyAbility(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
        if(this.isPowerShieldActivated)
        {
            this.shutDownPowerShield();
        }
        else
        {
            super.tryToUseEnergyAbility(powerUp, explosion);
        }
    }

    @Override
    public void useEnergyAbility(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
        this.turnOnPowerShield();
    }

    private void turnOnPowerShield()
    {
        Audio.play(Audio.shieldUp);
        this.isPowerShieldActivated = true;
    }

    @Override
    public void beAffectedByCollisionWith(Enemy enemy,
                                          Controller controller,
                                          boolean playCollisionSound)
    {
        if(!this.isPowerShieldProtected(enemy))
        {
            super.beAffectedByCollisionWith(enemy, controller, playCollisionSound);
            if(this.isPowerShieldActivated)
            {
                this.shutDownPowerShield();
                this.energy = 0;
            }
        }
        else
        {
            this.energy
                    -= this.hasUnlimitedEnergy()
                    ? 0.0
                    : this.spellCosts * enemy.collisionDamage(this);
            if(this.isInvincible())
            {
                if(playCollisionSound){Audio.play(Audio.shieldUp);}
            }
            else if(playCollisionSound){Audio.play(Audio.explosion1);}
        }
    }

    @Override
    float calculateEnergyRegenerationRate()
    {
        return this.isPowerShieldActivated
                ? this.hasUnlimitedEnergy()
                    ? 0
                    : POWER_SHIELD_E_LOSS_RATE
                : this.regenerationRate;
    }
    
    @Override
    void resetFifthSpecial()
    {
        this.hasJumboMissiles = false;
    }
    
    @Override
    public float getMissileDamageFactor()
    {
        return this.hasJumboMissiles
                ? JUMBO_MISSILE_DAMAGE_FACTOR
                : STANDARD_MISSILE_DAMAGE_FACTOR;
    }
    
    @Override
    public void takeMissileDamage()
    {
        if(this.canAbsorbMissileDamage())
        {
            Audio.play(Audio.shieldUp);
            this.energy -= this.hasUnlimitedEnergy()
                ? 0
                : this.getProtectionFactor()
                    * ENEMY_MISSILE_DAMAGE_FACTOR
                    * this.spellCosts;
        }
        else
        {
            if(this.isPowerShieldActivated)
            {
                this.shutDownPowerShield();
                this.energy = 0;
            }
            super.takeMissileDamage();
        }
    }
    
    private boolean canAbsorbMissileDamage()
    {
        return this.isPowerShieldActivated
                && this.hasEnoughEnergyForMissileDamageAbsorption();
    }
    
    private boolean hasEnoughEnergyForMissileDamageAbsorption()
    {
        return this.energy >= this.getProtectionFactor()
                                * ENEMY_MISSILE_DAMAGE_FACTOR
                                * this.spellCosts
                || this.hasUnlimitedEnergy();
    }
    
    @Override
    public void crash()
    {
        if(this.isPowerShieldActivated){this.shutDownPowerShield();}
        super.crash();
    }
    
    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        this.isPowerShieldActivated = true;
    }
    
    @Override
    public void updateMenuEffect()
    {
        super.updateMenuEffect();
        if(Menu.effectTimer[this.getType().ordinal()] == 30)
        {
            Audio.play(Audio.plasmaOff);
        }
    }
    
    @Override
    public void stoptMenuEffect()
    {
        this.isPowerShieldActivated = false;
    }
    
    @Override
    void paintComponents(Graphics2D g2d, int left, int top)
    {
        super.paintComponents(g2d, left, top);
        if(this.isPowerShieldActivated)
        {
            this.paintPowerShield(g2d, left, top);
        }
    }
    
    private void paintPowerShield(Graphics2D g2d, int left, int top)
    {
        g2d.setColor(MyColor.shieldColor[Menu.window == STARTSCREEN ? NIGHT.ordinal() : Events.timeOfDay.ordinal()]);
        g2d.fillOval(left+(this.hasLeftMovingAppearance() ? -9 : 35), top+19, 96, 54);
    }
    
    @Override
    public void update(EnumMap<CollectionSubgroupTypes, LinkedList<Missile>> missile,
                       EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
        super.update(missile, explosion);
        if(this.isPowerShieldActivated && this.energy == 0)
        {
            this.shutDownPowerShield();
        }
    }
    
    void shutDownPowerShield()
    {
        Audio.play(Audio.plasmaOff);
        this.isPowerShieldActivated = false;
    }
    
    public boolean isPowerShieldProtected(Enemy enemy)
    {
        return this.isPowerShieldActivated
            && (this.hasUnlimitedEnergy()
            || this.energy
            >= this.spellCosts * enemy.collisionDamage(this));
    }
    
    @Override
    float getEnergyDrain()
    {
        return this.isPowerShieldActivated
                ? REDUCED_ENERGY_DRAIN
                : super.getEnergyDrain();
    }
    
    @Override
    void slowDown()
    {
        if(!this.isPowerShieldActivated)
        {
            super.slowDown();
        }
    }
    
    @Override
    public float getBaseProtectionFactor(boolean isExplodable)
    {
        return this.isPowerShieldActivated && isExplodable
                ? REDUCED_BASE_PROTECTION_FACTOR
                : super.getBaseProtectionFactor(isExplodable);
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        this.isPowerShieldActivated = false;
    }

    @Override
    public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill)
    {
        if(missile != null)
        {
            if(missile.kills > 0
                    && this.hasPiercingWarheads
                    && (     Missile.canTakeCredit(missile.sister[0], enemy)
                    || Missile.canTakeCredit(missile.sister[1], enemy)))
            {
                if(Missile.canTakeCredit(missile.sister[0], enemy))
                {
                    missile.sister[0].credit();
                }
                else if(Missile.canTakeCredit(missile.sister[1], enemy))
                {
                    missile.sister[1].credit();
                }
            }
            else
            {
                missile.credit();
            }
        }
    }
}