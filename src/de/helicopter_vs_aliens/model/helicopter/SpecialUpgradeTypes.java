package de.helicopter_vs_aliens.model.helicopter;

public enum SpecialUpgradeTypes {
    SPOTLIGHT,
    GOLIATH_PLATING,
    PIERCING_WARHEADS,
    EXTRA_CANNONS,
    FIFTH_SPECIAL;
    
    private static final String[] KEY_SUFFIXES = {"spotlight", "goliath", "warheads", "secondCannon", "fifth"};
    
    private String dictionaryKey;
    
    static
    {
        for(SpecialUpgradeTypes specialUpgradeType : SpecialUpgradeTypes.values())
        {
            specialUpgradeType.dictionaryKey = "upgrades.special." + KEY_SUFFIXES[specialUpgradeType.ordinal()];
        }
    }

    
    public String getDictionaryKey()
    {
        return dictionaryKey;
    }
}