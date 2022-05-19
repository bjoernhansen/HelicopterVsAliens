package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.JUMBO;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.ORDINARY;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PEGASUS;

public final class Roch extends Helicopter
{
    public static final int
        ROCH_SECOND_CANNON_COSTS = 225000;
    
    private static final int
        JUMBO_MISSILE_COSTS = 25000,
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
    public HelicopterType getType()
    {
        return ROCH;
    }
    
    @Override
    public int getPiercingWarheadsCosts()
    {
         return CHEAP_SPECIAL_COSTS;
    }

    @Override
    public ExplosionType getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
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
        if(!PEGASUS.hasReachedLevel20())
        {
            Window.unlock(KAMAITACHI);
        }
    }

    @Override
    public boolean isEnergyAbilityActivatable()
    {
        return this.battery.getCurrentCharge() >= POWER_SHIELD_ACTIVATION_THRESHOLD && this.hasEnoughEnergyForAbility();
    }

    @Override
    public void tryToUseEnergyAbility(Controller controller)
    {
        if(this.isPowerShieldActivated)
        {
            this.shutDownPowerShield();
        }
        else
        {
            super.tryToUseEnergyAbility(controller);
        }
    }

    @Override
    public void useEnergyAbility(Controller controller)
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
                                          GameRessourceProvider gameRessourceProvider,
                                          boolean playCollisionSound)
    {
        if(!this.isPowerShieldProtected(enemy))
        {
            super.beAffectedByCollisionWith(enemy, gameRessourceProvider, playCollisionSound);
            if(this.isPowerShieldActivated)
            {
                this.shutDownPowerShield();
                this.battery.discharge();
            }
        }
        else
        {
            float energyConsumption = this.collisionPowerShieldConsumption(enemy);
            this.battery.drain(energyConsumption);
            
            if(this.isInvincible())
            {
                if(playCollisionSound){Audio.play(Audio.shieldUp);}
            }
            else if(playCollisionSound){Audio.play(Audio.explosion1);}
        }
    }
    
    private float collisionPowerShieldConsumption(Enemy enemy)
    {
        return this.hasUnlimitedEnergy()
                ? 0.0f
                : this.spellCosts * enemy.collisionDamage(this);
    }
    
    @Override
    float getRegenerationRate()
    {
        return this.isPowerShieldActivated
                ? this.hasUnlimitedEnergy()
                    ? 0
                    : POWER_SHIELD_E_LOSS_RATE
                : super.getRegenerationRate();
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
            this.battery.drain(this.missileDamagePowerShieldConsumption());
        }
        else
        {
            if(this.isPowerShieldActivated)
            {
                this.shutDownPowerShield();
                this.battery.discharge();
            }
            super.takeMissileDamage();
        }
    }
    
    private float missileDamagePowerShieldConsumption()
    {
        return  this.hasUnlimitedEnergy()
                ? 0.0f
                : this.getProtectionFactor()
                  * ENEMY_MISSILE_DAMAGE_FACTOR
                  * this.spellCosts;
    }
    
    private boolean canAbsorbMissileDamage()
    {
        return this.isPowerShieldActivated && this.hasEnoughEnergyForMissileDamageAbsorption();
    }
    
    private boolean hasEnoughEnergyForMissileDamageAbsorption()
    {
        return this.battery.getCurrentCharge() >= this.getProtectionFactor()
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
        if(Window.effectTimer[this.getType().ordinal()] == 30)
        {
            Audio.play(Audio.plasmaOff);
        }
    }
    
    @Override
    public void stopMenuEffect()
    {
        this.isPowerShieldActivated = false;
    }
    
    @Override
    public void update(Controller controller)
    {
        super.update(controller);
        if(this.isPowerShieldActivated && this.battery.isDischarged())
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
                || this.battery.getCurrentCharge() >= this.spellCosts * enemy.collisionDamage(this));
    }
    
    @Override
    float getStaticChargeEnergyDrain()
    {
        return this.isPowerShieldActivated
                ? REDUCED_ENERGY_DRAIN
                : super.getStaticChargeEnergyDrain();
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
    public float getBaseProtectionFactor(boolean canExplode)
    {
        return this.isPowerShieldActivated && canExplode
                ? REDUCED_BASE_PROTECTION_FACTOR
                : super.getBaseProtectionFactor(canExplode);
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
    
    @Override
    public boolean hasKillCountingMissiles()
    {
        return true;
    }
    
    @Override
    public void inactivate(Map<CollectionSubgroupType, LinkedList<Missile>> missiles, Missile missile)
    {
        if(missile.sister[0] == null && missile.sister[1] == null)
        {
            if(missile.kills + missile.sisterKills > 1)
            {
                Events.extraReward(missile.kills + missile.sisterKills, missile.earnedMoney, 0.5f, 0.75f, 3.0f);
            }
        }
        else if(missile.kills + missile.sisterKills > 0)
        {
            for(int j = 0; true; j++)
            {
                if(missile.sister[j] != null)
                {
                    missile.sister[j].earnedMoney += missile.earnedMoney;
                    missile.sister[j].sisterKills += missile.kills + missile.sisterKills;
                    missile.sister[j].nrOfHittingSisters += ((missile.kills > 0 ? 1 : 0) + missile.nrOfHittingSisters);
                    break;
                }
            }
        }
        for(int j = 0; j < 2; j++)
        {
            if(missile.sister[j] != null)
            {
                if(missile.sister[j].sister[0] == missile){
                    missile.sister[j].sister[0] = null;}
                else if(missile.sister[j].sister[1] == missile){
                    missile.sister[j].sister[1] = null;}
                else assert false;
            }
        }
        missiles.get(INACTIVE).add(missile);
    }
    
    public boolean isPowerShieldActivated()
    {
        return isPowerShieldActivated;
    }
    
    @Override
    public int getFifthSpecialCosts()
    {
        return JUMBO_MISSILE_COSTS;
    }
    
    @Override
    boolean canRegenerateEnergy()
    {
        return super.canRegenerateEnergy()
                && !this.isPowerShieldActivated;
    }
    
    @Override
    public int getLastCannonCost()
    {
        return Roch.ROCH_SECOND_CANNON_COSTS;
    }
}