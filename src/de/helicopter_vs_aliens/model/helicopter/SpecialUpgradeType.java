package de.helicopter_vs_aliens.model.helicopter;

public enum SpecialUpgradeType
{
    SPOTLIGHT,
    GOLIATH_PLATING,
    PIERCING_WARHEADS,
    EXTRA_CANNONS,
    FIFTH_SPECIAL;
    
    private static final String[]
        KEY_SUFFIXES = {"spotlight", "goliath", "warheads", "secondCannon", "fifth"};
    
    private static final SpecialUpgradeType[]
            defensiveCopyOfValues = values();
    
    private String
        dictionaryKey;
    
    
    static
    {
        for(SpecialUpgradeType specialUpgradeType : SpecialUpgradeType.getValues())
        {
            specialUpgradeType.dictionaryKey = "upgrades.special." + KEY_SUFFIXES[specialUpgradeType.ordinal()];
        }
    }
    
    public String getDictionaryKey()
    {
        return dictionaryKey;
    }
    
    public static SpecialUpgradeType[] getValues()
    {
        return defensiveCopyOfValues;
    }
}