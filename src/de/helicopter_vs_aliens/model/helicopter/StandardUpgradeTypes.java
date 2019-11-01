package de.helicopter_vs_aliens.model.helicopter;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;

public enum StandardUpgradeTypes
{
    ROTOR_SYSTEM (OROCHI),
    MISSILE_DRIVE (OROCHI),
    PLATING (PHOENIX),
    FIREPOWER (ROCH),
    FIRE_RATE (KAMAITACHI),
    ENERGY_ABILITY (PEGASUS);


    private HelicopterTypes privilegedHelicopter;


    public static int size()
    {
        return values().length;
    }

    StandardUpgradeTypes(HelicopterTypes privilegedHelicopter)
    {
        this.privilegedHelicopter = privilegedHelicopter;
    }

    HelicopterTypes getPrivilegedHelicopter()
    {
        return this.privilegedHelicopter;
    }
}