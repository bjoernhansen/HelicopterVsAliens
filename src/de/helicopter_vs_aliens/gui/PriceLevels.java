package de.helicopter_vs_aliens.gui;

public enum PriceLevels
{
    VERY_CHEAP,
    CHEAP,
    REGULAR,
    EXPENSIVE (8),
    EXTORTIONATE (6);


    private static final String[] keySuffixes = {"veryCheap", "cheap", "medium", "expensive", "extortionate"};

    private int maxUpgradeLevel = 10;

    private String dictionaryKey;


    PriceLevels()
    {
        initializeDictionaryKey();
    }

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
        return values().length;
    }

    public int getMaxUpgradeLevel()
    {
        return this.maxUpgradeLevel;
    }

    public String getDictionaryKey()
    {
        return dictionaryKey;
    }

    private void initializeDictionaryKey()
    {
        dictionaryKey = "priceLevels." + keySuffixes[ordinal()];
    }
}