package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.events.MouseEvent;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.missile.Grantable;
import de.helicopter_vs_aliens.model.missile.Missile;

import static de.helicopter_vs_aliens.model.explosion.ExplosionType.ORDINARY;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.STUNNING;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;


public final class Orochi extends Helicopter
{
    private static final float
        EXTRA_MISSILE_DAMAGE_FACTOR = 1.03f;    // Orochi-Klasse: Faktor, um den sich die Schadenswirkung von Raketen erhöht wird
    
    private boolean
        hasRadarDevice;         // = true: Helikopter verfügt über eine Radar-Vorrichtung
    
    private boolean
        isNextMissileStunner;   // = true: die nächste abgeschossene Rakete wird eine Stopp-Rakete
    
    
    @Override
    public HelicopterType getType()
    {
        return OROCHI;
    }
    
    @Override
    public ExplosionType getCurrentExplosionTypeOfMissiles()
    {
        if(isShootingStunningMissile())
        {
            return STUNNING;
        }
        return ORDINARY;
    }
    
    @Override
    void setSpellCosts()
    {
        spellCosts = OROCHI.getSpellCosts() - 2 * (getUpgradeLevelOf(StandardUpgradeType.ENERGY_ABILITY) - 1);
    }
    
    @Override
    public void updateUnlockedHelicopters()
    {
        if(!KAMAITACHI.hasReachedLevel20())
        {
            Window.unlock(PEGASUS);
        }
    }
    
    @Override
    void getMaximumNumberOfCannons()
    {
        numberOfCannons = 3;
    }
    
    @Override
    public void obtainSomeUpgrades()
    {
        if(numberOfCannons < 3)
        {
            numberOfCannons = 2;
        }
        super.obtainSomeUpgrades();
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return hasRadarDevice;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        hasRadarDevice = true;
    }
    
    @Override
    public boolean hasAllCannons()
    {
        return numberOfCannons == 3;
    }
    
    @Override
    public void updateEnergyAbility()
    {
        super.updateEnergyAbility();
        setSpellCosts();
    }
    
    @Override
    public void tryToUseEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        if(!isNextMissileStunner)
        {
            Audio.play(Audio.stunActivated);
            isNextMissileStunner = true;
        }
    }
    
    @Override
    public void useEnergyAbility(GameRessourceProvider gameRessourceProvider) {}
    
    @Override
    boolean canRegenerateEnergy()
    {
        return super.canRegenerateEnergy()
            && !isNextMissileStunner;
    }
    
    @Override
    boolean isShootingStunningMissile()
    {
        return isNextMissileStunner && hasEnoughEnergyForAbility();
    }
    
    @Override
    protected void consumeEnergyForShoot()
    {
        if(isNextMissileStunner && hasEnoughEnergyForAbility())
        {
            consumeSpellCosts();
        }
    }
    
    @Override
    void resetFifthSpecial()
    {
        hasRadarDevice = false;
    }
    
    @Override
    public boolean canDetectCloakedVessels()
    {
        return hasRadarDevice;
    }
    
    @Override
    public float getMissileDamageFactor()
    {
        return numberOfCannons == 3 ? EXTRA_MISSILE_DAMAGE_FACTOR : STANDARD_MISSILE_DAMAGE_FACTOR;
    }
    
    @Override
    public void initMenuEffect(int i)
    {
        super.initMenuEffect(i);
        isNextMissileStunner = true;
    }
    
    @Override
    public void stopMenuEffect()
    {
        isNextMissileStunner = false;
    }
    
    @Override
    public void rightMouseButtonReleaseAction(MouseEvent mouseEvent, double scalingFactor)
    {
        isNextMissileStunner = false;
    }
    
    @Override
    public void resetStateTypeSpecific()
    {
        isNextMissileStunner = false;
    }
    
    @Override
    public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill)
    {
        if(missile != null)
        {
            missile.creditItselfOrCompanionOn(enemy, hasPiercingWarheads);
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
        missile.setBackStatistics();
    }
    
    public boolean isNextMissileStunner()
    {
        return isNextMissileStunner;
    }
    
    @Override
    public Grantable getMultipleHitsExtraReward(Missile missile)
    {
        return missile::grantExtraRewardForNonFailedShots;
    }
}