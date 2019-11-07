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
    
    
    private static final String[]
            KEY_SUFFIXES = {"rotorSystem", "missileDrive", "plating", "firepower", "fireRate", "energyAbility"};
        
    static
    {
        for(StandardUpgradeTypes standardUpgradeType : StandardUpgradeTypes.values())
        {
            standardUpgradeType.dictionaryKey = "upgrades.standard." + KEY_SUFFIXES[standardUpgradeType.ordinal()];
        }
    }
    
    private String
            dictionaryKey;
       
    
    public String getDictionaryKey()
    {
        return dictionaryKey;
    }
    
    private static final StandardUpgradeTypes[]
            defensiveCopyOfValues = values();
    
    private HelicopterTypes
            privilegedHelicopter;
    
    
    StandardUpgradeTypes(HelicopterTypes privilegedHelicopter)
    {
        this.privilegedHelicopter = privilegedHelicopter;
    }
    
    public static final StandardUpgradeTypes[] getValues()
    {
        return defensiveCopyOfValues;
    }
    
    public static int size()
    {
        return getValues().length;
    }

    HelicopterTypes getPrivilegedHelicopter()
    {
        return this.privilegedHelicopter;
    }
}