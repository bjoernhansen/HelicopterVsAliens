package de.helicopter_vs_aliens.model.helicopter;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.ROCH;

public final class Roch extends Helicopter
{
    public static final int
        JUMBO_MISSILE_COSTS = 25000,
        ROCH_SECOND_CANNON_COSTS = 225000;
    
    
    @Override
    public HelicopterTypes getType()
    {
        return HelicopterTypes.ROCH;
    }
    
    @Override
    public int getPiercingWarheadsCosts()
    {
         return CHEAP_SPECIAL_COSTS;
    }
}