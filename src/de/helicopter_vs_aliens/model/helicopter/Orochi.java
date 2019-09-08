package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.MyColor;

import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.STUNNING;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;


public final class Orochi extends Helicopter
{
    private static final float
        EXTRA_MISSILE_DAMAGE_FACTOR = 1.03f; 	// Orochi-Klasse: Faktor, um den sich die Schadenswirkung von Raketen erhöht wird
        
    private boolean
        hasRadarDevice;	 // = true: Helikopter verfügt über eine Radar-Vorrichtung
    
    
    @Override
    public HelicopterTypes getType()
    {
        return OROCHI;
    }

    @Override
    public ExplosionTypes getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
    {
        if(stunningMissile){return STUNNING;}
        return ORDINARY;
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
    
    @Override
    void resetFifthSpecial()
    {
        this.hasRadarDevice = false;
    }
    
    @Override
    public boolean canDetectCloakedVessels()
    {
        return this.hasRadarDevice;
    }
    
    @Override
    public float getMissileDamageFactor()
    {
        return this.numberOfCannons == 3 ? EXTRA_MISSILE_DAMAGE_FACTOR : STANDARD_MISSILE_DAMAGE_FACTOR;
    }
    
    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        this.isNextMissileStunner = true;
    }
    
    @Override
    public void stoptMenuEffect()
    {
        this.isNextMissileStunner = false;
    }
    
    @Override
    Color getInputColorCannon()
    {
        if( this.isNextMissileStunner
            && (this.energy >= this.spellCosts
            || this.hasUnlimitedEnergy()))
        {
            return MyColor.variableBlue;
        }
        return super.getInputColorCannon();
    }
    
    @Override
    void paintCannons(Graphics2D g2d, int left, int top)
    {
        super.paintCannons(g2d, left, top);
        if(this.numberOfCannons == 3)
        {
            g2d.setPaint(this.gradientCannon2and3);
            g2d.fillRoundRect(left+(this.hasLeftMovingAppearance() ? 38 : 37), top+41, 47, 6, 6, 6);
            g2d.setPaint(this.gradientCannonHole);
            g2d.fillOval(left+(this.hasLeftMovingAppearance() ? 39 : 80), top+42, 3, 4);
        }
    }
    
    @Override
    public void resetState(boolean resetStartPos)
    {
        super.resetState(resetStartPos);
        this.isNextMissileStunner = false;
    }
}