package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.events.MouseEvent;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.missile.Missile;


import java.util.Map;
import java.util.Queue;

import static de.helicopter_vs_aliens.model.explosion.ExplosionType.ORDINARY;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.STUNNING;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;


public final class Orochi extends Helicopter
{
    private static final float
        EXTRA_MISSILE_DAMAGE_FACTOR = 1.03f;    // Orochi-Klasse: Faktor, um den sich die Schadenswirkung von Raketen erhöht wird
    
    private boolean
        hasRadarDevice,         // = true: Helikopter verfügt über eine Radar-Vorrichtung
        isNextMissileStunner;   // = true: die nächste abgeschossene Rakete wird eine Stopp-Rakete
    
    
    @Override
    public HelicopterType getType()
    {
        return OROCHI;
    }
    
    @Override
    public ExplosionType getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
    {
        if (stunningMissile)
        {
            return STUNNING;
        }
        return ORDINARY;
    }
    
    @Override
    void setSpellCosts()
    {
        this.spellCosts = OROCHI.getSpellCosts() - 2 * (this.getUpgradeLevelOf(StandardUpgradeType.ENERGY_ABILITY) - 1);
    }
    
    @Override
    public void updateUnlockedHelicopters()
    {
        if (!KAMAITACHI.hasReachedLevel20())
        {
            Window.unlock(PEGASUS);
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
        if (this.numberOfCannons < 3)
        {
            this.numberOfCannons = 2;
        }
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
    public void updateEnergyAbility()
    {
        super.updateEnergyAbility();
        this.setSpellCosts();
    }
    
    @Override
    public void tryToUseEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        if (!this.isNextMissileStunner)
        {
            Audio.play(Audio.stunActivated);
            this.isNextMissileStunner = true;
        }
    }
    
    @Override
    public void useEnergyAbility(GameRessourceProvider gameRessourceProvider){}
    
    @Override
    boolean canRegenerateEnergy()
    {
        return super.canRegenerateEnergy()
                && !this.isNextMissileStunner;
    }
    
    @Override
    boolean isShootingStunningMissile()
    {
        // TODO boolsche Methode, die mehr macht als einen boolean zu berechnen --> ändern
        if (this.isNextMissileStunner && this.hasEnoughEnergyForAbility())
        {
            this.consumeSpellCosts();
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
    public void stopMenuEffect()
    {
        this.isNextMissileStunner = false;
    }
    
    @Override
    public void rightMouseButtonReleaseAction(MouseEvent mouseEvent, double scalingFactor)
    {
        this.isNextMissileStunner = false;
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        this.isNextMissileStunner = false;
    }

    // TODO redundanter Code in Roch-Klasse
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
    // TODO Großteil des Codes nach Missile und Redundanzen damit auflösen subklassenspezischen Code in eigene Methode -> vgl. Klasse Roch
    public void inactivate(Map<CollectionSubgroupType, Queue<Missile>> missiles, Missile missile)
    {
        if(missile.sister[0] == null && missile.sister[1] == null)
        {
            if(missile.kills + missile.sisterKills > 1)
            {
                int nonFailedShots = (missile.kills > 0 ? 1 : 0) + missile.nrOfHittingSisters;
                if(nonFailedShots == 1)
                {
                    Events.extraReward(missile.kills + missile.sisterKills, missile.earnedMoney, 0.25f, 0.0f, 0.25f);
                }
                if(nonFailedShots == 2)
                {
                    Events.extraReward(missile.kills + missile.sisterKills, missile.earnedMoney, 1.5f, 0.0f, 1.5f);
                }
                else if(nonFailedShots == 3)
                {
                    Events.extraReward(missile.kills + missile.sisterKills, missile.earnedMoney, 4f, 0.0f, 4f);
                }
                else assert false;
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
        super.inactivate(missiles, missile);
    }
    
    public boolean isNextMissileStunner()
    {
        return isNextMissileStunner;
    }
}