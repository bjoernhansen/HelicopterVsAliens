package de.helicopter_vs_aliens.model.helicopter;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;

public enum StandardUpgradeType
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
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.values())
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
    
    private static final StandardUpgradeType[]
            defensiveCopyOfValues = values();
    
    private HelicopterType
            privilegedHelicopter;
    
    
    StandardUpgradeType(HelicopterType privilegedHelicopter)
    {
        this.privilegedHelicopter = privilegedHelicopter;
    }
    
    public static final StandardUpgradeType[] getValues()
    {
        return defensiveCopyOfValues;
    }
    
    public static int size()
    {
        return getValues().length;
    }

    HelicopterType getPrivilegedHelicopter()
    {
        return this.privilegedHelicopter;
    }
}