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
    
    private static final float[][]
            magnitudes  = { {3f, 3.4f, 3.8f, 4.2f, 4.8f, 5.4f, 6.0f, 6.8f, 7.6f, 8.5f},     // ROTOR_SYSTEM
                            {13, 16, 19, 22, 25, 28, 31, 34, 37, 40},                       // MISSILE_DRIVE
                            {1.5f, 2.6f, 4.0f, 5.6f, 7.7f, 9.6f, 11.8f, 14.2f, 17.0f, 20f}, // PLATING
                            {2, 3, 6, 10, 17, 28, 46, 75, 122, 198},                        // FIREPOWER
                            {80, 64, 51, 41, 33, 26, 21, 17, 13, 11, 9, 7, 6, 5, 4},        // FIRE_RATE
                            {0, 100, 190, 270, 340, 400, 450, 490, 520, 540}};              // ENERGY_ABILITY
    
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
    
    public float getMagnitude(int level)
    {
        if(level > 0 && level <= magnitudes[this.ordinal()].length)
        {
            return magnitudes[this.ordinal()][level-1];
        }
        else return 0;
    }
}