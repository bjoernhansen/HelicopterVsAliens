package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.control.Events;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.PHOENIX;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.ROCH;

public final class Helios extends Helicopter
{
    @Override
    public HelicopterTypes getType()
    {
        return HelicopterTypes.HELIOS;
    }
    
    @Override
    public int getGoliathCosts()
    {
        return Events.recordTime[PHOENIX.ordinal()][4] != 0 ? Phoenix.GOLIATH_COSTS : STANDARD_GOLIATH_COSTS;
    }
    
    @Override
    public int getPiercingWarheadsCosts()
    {
        return Events.recordTime[ROCH.ordinal()][4] != 0 ? CHEAP_SPECIAL_COSTS : STANDARD_GOLIATH_COSTS;
    }
    
    @Override
    int getUpgradeCosts(int i)
    {
        return heliosCosts(i);
    }
}