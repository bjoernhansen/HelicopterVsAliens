package de.helicopter_vs_aliens.gui;

public enum PriceLevels
{
    VERY_CHEAP,
    CHEAP,
    REGULAR,
    EXPENSIVE (8),
    EXTORTIONATE (6);


    private static final String[] KEY_SUFFIXES = {"veryCheap", "cheap", "medium", "expensive", "extortionate"};

    private int maxUpgradeLevel = 10;


    PriceLevels()
    {
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
        return "priceLevels." + KEY_SUFFIXES[ordinal()];
    }
}