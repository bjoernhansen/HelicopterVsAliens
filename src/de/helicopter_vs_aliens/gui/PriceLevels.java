package de.helicopter_vs_aliens.gui;

public enum PriceLevels
{
    VERY_CHEAP,
    CHEAP,
    REGULAR,
    EXPENSIVE (8),
    EXTORTIONATE (6);
    
    
    private static final String[]
            KEY_SUFFIXES = {"veryCheap", "cheap", "medium", "expensive", "extortionate"};

    private static final PriceLevels[]
            defensiveCopyOfValues = values();

    static
    {
        for(PriceLevels priceLevel : PriceLevels.values())
        {
            priceLevel.dictionaryKey = "priceLevels." + KEY_SUFFIXES[priceLevel.ordinal()];
        }
    }
    
    private int maxUpgradeLevel = 10;
    private String dictionaryKey;


    PriceLevels(){}

    PriceLevels (int maxUpgradeLevel)
    {
        this();
        this.maxUpgradeLevel = maxUpgradeLevel;
    }

    public static PriceLevels getMaximium()
    {
        return EXTORTIONATE;
    }

    public static int size()
    {
        return getValues().length;
    }

    public static PriceLevels[] getValues()
    {
        return defensiveCopyOfValues;
    }

    public int getMaxUpgradeLevel()
    {
        return this.maxUpgradeLevel;
    }

    public String getDictionaryKey()
    {
        return this.dictionaryKey;
    }
}