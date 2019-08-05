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
}