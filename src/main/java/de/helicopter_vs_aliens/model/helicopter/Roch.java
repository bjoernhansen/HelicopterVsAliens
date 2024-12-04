package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.missile.Grantable;
import de.helicopter_vs_aliens.model.missile.Missile;

import static de.helicopter_vs_aliens.model.explosion.ExplosionType.JUMBO;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.ORDINARY;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;


public final class Roch extends Helicopter
{
    public static final int
        ROCH_SECOND_CANNON_COSTS = 225000;
    
    private static final int
        JUMBO_MISSILE_COSTS = 25000;
    
    private static final int
        POWER_SHIELD_ACTIVATION_THRESHOLD = 75;
    
    private static final int
        REDUCED_ENERGY_DRAIN = 10;
    
    private static final float
        JUMBO_MISSILE_DAMAGE_FACTOR = 2.36363637f;
    
    private static final float
        POWER_SHIELD_E_LOSS_RATE = -0.06f;
    
    private static final float
        REDUCED_BASE_PROTECTION_FACTOR = 0.65f;
    
    
    private boolean
        hasJumboMissiles;
    
    private boolean
        isPowerShieldActivated;        // = true: Power-Shield ist aktiviert
    
    
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
    public ExplosionType getCurrentExplosionTypeOfMissiles()
    {
        if(hasJumboMissiles)
        {
            return JUMBO;
        }
        else
        {
            return ORDINARY;
        }
    }
    
    @Override
    public void obtainSomeUpgrades()
    {
        hasPiercingWarheads = true;
        super.obtainSomeUpgrades();
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return hasJumboMissiles;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        hasJumboMissiles = true;
        setCurrentBaseFirepower();
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
        return battery.getCurrentCharge() >= POWER_SHIELD_ACTIVATION_THRESHOLD && hasEnoughEnergyForAbility();
    }
    
    @Override
    public void tryToUseEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        if(isPowerShieldActivated)
        {
            shutDownPowerShield();
        }
        else
        {
            super.tryToUseEnergyAbility(gameRessourceProvider);
        }
    }
    
    @Override
    public void useEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        turnOnPowerShield();
    }
    
    private void turnOnPowerShield()
    {
        Audio.play(Audio.shieldUp);
        isPowerShieldActivated = true;
    }
    
    @Override
    public void beAffectedByCollisionWith(Enemy enemy,
                                          GameRessourceProvider gameRessourceProvider,
                                          boolean playCollisionSound)
    {
        if(!isPowerShieldProtected(enemy))
        {
            super.beAffectedByCollisionWith(enemy, gameRessourceProvider, playCollisionSound);
            if(isPowerShieldActivated)
            {
                shutDownPowerShield();
                battery.discharge();
            }
        }
        else
        {
            float energyConsumption = collisionPowerShieldConsumption(enemy);
            battery.drain(energyConsumption);
            
            if(isInvincible())
            {
                if(playCollisionSound)
                {
                    Audio.play(Audio.shieldUp);
                }
            }
            else if(playCollisionSound)
            {
                Audio.play(Audio.explosion1);
            }
        }
    }
    
    private float collisionPowerShieldConsumption(Enemy enemy)
    {
        return hasUnlimitedEnergy()
            ? 0.0f
            : spellCosts * enemy.collisionDamage();
    }
    
    @Override
    float getRegenerationRate()
    {
        return isPowerShieldActivated
            ? powerShieldEnergyConsumptionRate()
            : super.getRegenerationRate();
    }
    
    private float powerShieldEnergyConsumptionRate()
    {
        return hasUnlimitedEnergy()
            ? 0
            : POWER_SHIELD_E_LOSS_RATE;
    }
    
    @Override
    void resetFifthSpecial()
    {
        hasJumboMissiles = false;
    }
    
    @Override
    public float getMissileDamageFactor()
    {
        return hasJumboMissiles
            ? JUMBO_MISSILE_DAMAGE_FACTOR
            : STANDARD_MISSILE_DAMAGE_FACTOR;
    }
    
    @Override
    public void takeMissileDamage()
    {
        if(canAbsorbMissileDamage())
        {
            Audio.play(Audio.shieldUp);
            battery.drain(missileDamagePowerShieldConsumption());
        }
        else
        {
            if(isPowerShieldActivated)
            {
                shutDownPowerShield();
                battery.discharge();
            }
            super.takeMissileDamage();
        }
    }
    
    private float missileDamagePowerShieldConsumption()
    {
        return hasUnlimitedEnergy()
            ? 0.0f
            : getProtectionFactor()
            * ENEMY_MISSILE_DAMAGE_FACTOR
            * spellCosts;
    }
    
    private boolean canAbsorbMissileDamage()
    {
        return isPowerShieldActivated && hasEnoughEnergyForMissileDamageAbsorption();
    }
    
    private boolean hasEnoughEnergyForMissileDamageAbsorption()
    {
        return battery.getCurrentCharge() >= getProtectionFactor()
            * ENEMY_MISSILE_DAMAGE_FACTOR
            * spellCosts
            || hasUnlimitedEnergy();
    }
    
    @Override
    public void crash()
    {
        if(isPowerShieldActivated)
        {
            shutDownPowerShield();
        }
        super.crash();
    }
    
    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        isPowerShieldActivated = true;
    }
    
    @Override
    public void updateMenuEffect()
    {
        super.updateMenuEffect();
        if(Window.effectTimer[getType().ordinal()] == 30)
        {
            Audio.play(Audio.plasmaOff);
        }
    }
    
    @Override
    public void stopMenuEffect()
    {
        isPowerShieldActivated = false;
    }
    
    @Override
    public void update(GameRessourceProvider gameRessourceProviderontroller)
    {
        super.update(gameRessourceProviderontroller);
        if(isPowerShieldActivated && battery.isDischarged())
        {
            shutDownPowerShield();
        }
    }
    
    void shutDownPowerShield()
    {
        Audio.play(Audio.plasmaOff);
        isPowerShieldActivated = false;
    }
    
    public boolean isPowerShieldProtected(Enemy enemy)
    {
        return isPowerShieldActivated
            && (hasUnlimitedEnergy()
            || battery.getCurrentCharge() >= spellCosts * enemy.collisionDamage());
    }
    
    @Override
    float getStaticChargeEnergyDrain()
    {
        return isPowerShieldActivated
            ? REDUCED_ENERGY_DRAIN
            : super.getStaticChargeEnergyDrain();
    }
    
    @Override
    void slowDown()
    {
        if(!isPowerShieldActivated)
        {
            super.slowDown();
        }
    }
    
    @Override
    public float getBaseProtectionFactor(boolean canExplode)
    {
        return isPowerShieldActivated && canExplode
            ? REDUCED_BASE_PROTECTION_FACTOR
            : super.getBaseProtectionFactor(canExplode);
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        isPowerShieldActivated = false;
    }
    
    @Override
    public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill)
    {
        if(missile != null)
        {
            missile.creditItselfOrSisterOn(enemy, hasPiercingWarheads);
        }
    }
    
    @Override
    public boolean hasKillCountingMissiles()
    {
        return true;
    }
    
    @Override
    void resetMissile(Missile missile)
    {
        super.resetMissile(missile);
        missile.setBackKillCounter();
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
            && !isPowerShieldActivated;
    }
    
    @Override
    public int getLastCannonCost()
    {
        return Roch.ROCH_SECOND_CANNON_COSTS;
    }
    
    @Override
    public Grantable getMultipleHitsExtraReward(Missile missile)
    {
        return missile::grantExtraRewardForMultipleKillsWithSingleShot;
    }
}