package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.STANDARD;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.STUNNING;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;


public final class Orochi extends Helicopter
{
    @Override
    public HelicopterTypes getType()
    {
        return OROCHI;
    }

    @Override
    public ExplosionTypes getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
    {
        if(stunningMissile){return STUNNING;}
        return STANDARD;
    }

    @Override
    void setSpellCosts()
    {
        this.spellCosts = OROCHI.getSpellCosts() - 2 * (this.levelOfUpgrade[ENERGY_ABILITY.ordinal()] - 1);
    }

    @Override
    public void updateUnlockedHelicopters()
    {
        if(!Events.reachedLevelTwenty[KAMAITACHI.ordinal()])
        {
            Menu.unlock(PEGASUS);
        }
    }

    @Override
    void getMaximumNumberOfCannons()
    {
        this.numberOfCannons = 3;
    }
    
    @Override
    public void obtainSomeUpgrades()
    {
        if(this.numberOfCannons < 3){this.numberOfCannons = 2;}
        super.obtainSomeUpgrades();
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasRadarDevice;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasRadarDevice = true;
    }
    
    @Override
    public boolean hasAllCannons()
    {
        return this.numberOfCannons == 3;
    }

    @Override
    public void upgradeEnergyAbility()
    {
        super.upgradeEnergyAbility();
        this.setSpellCosts();
    }

    @Override
    public void tryToUseEnergyAbility(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
        if(!this.isNextMissileStunner)
        {
            Audio.play(Audio.stunActivated);
            this.isNextMissileStunner = true;
        }
    }

    @Override
    boolean canRegenerateEnergy()
    {
        return !this.isDamaged && !this.isNextMissileStunner;
    }

    @Override
    boolean isShootingStunningMissile()
    {
        if(this.isNextMissileStunner
                && (this.energy >= this.spellCosts
                    || this.hasUnlimitedEnergy()))
        {
            this.energy -= this.hasUnlimitedEnergy()
                ? 0
                : this.spellCosts;
            return true;
        }
        return false;
    }
}