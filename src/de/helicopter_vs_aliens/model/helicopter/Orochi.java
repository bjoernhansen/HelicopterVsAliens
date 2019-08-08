package de.helicopter_vs_aliens.model.helicopter;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;

public final class Orochi extends Helicopter
{
    @Override
    public HelicopterTypes getType()
    {
        return OROCHI;
    }
    
    @Override
    void setSpellCosts()
    {
        this.spellCosts = OROCHI.getSpellCosts() - 2 *(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()]-1);
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
}