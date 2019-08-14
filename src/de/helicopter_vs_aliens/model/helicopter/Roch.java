package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.MyMath;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.JUMBO;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.STANDARD;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.PEGASUS;

public final class Roch extends Helicopter
{
    public static final int
        JUMBO_MISSILE_COSTS = 25000,
        ROCH_SECOND_CANNON_COSTS = 225000;

    private static final int
        POWER_SHIELD_ACTIVATION_THRESHOLD = 75;

    private static final float
        JUMBO_MISSILE_DMG_FACTOR = 2.36363637f,	// Faktor, um den sich die Schadenswirkung der Raketen erhöht, nachdem das Jumbo-Raketen-Spezial-Upgrade erworben wurde
        POWER_SHIELD_E_LOSS_RATE = -0.06f;


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
        if(this.hasJumboMissiles()){return JUMBO;}
        else{return STANDARD;}
    }

    public boolean hasJumboMissiles()
    {
        return missileDamageFactor == JUMBO_MISSILE_DMG_FACTOR;
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
        return this.hasJumboMissiles();
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.missileDamageFactor = JUMBO_MISSILE_DMG_FACTOR;
        this.currentFirepower = (int)(this.missileDamageFactor * MyMath.dmg(this.levelOfUpgrade[StandardUpgradeTypes.FIREPOWER.ordinal()]));
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
    boolean canRegenerateEnergy()
    {
        return !this.isDamaged && !this.isPowerShieldActivated;
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
}