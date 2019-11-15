package de.helicopter_vs_aliens.gui;

import java.awt.*;

public enum PriceLevels
{
    VERY_CHEAP,
    CHEAP,
    REGULAR,
    EXPENSIVE (8),
    EXTORTIONATE (6);
    
    
    private static final String[]
            key_suffixes = {"veryCheap", "cheap", "medium", "expensive", "extortionate"};

    private static final PriceLevels[]
            defensiveCopyOfValues = values();
    
    private static final Color
            costsColor[] =  {   new Color(130, 255, 130),   // very cheap
                                new Color (210, 255, 180),  // cheap
                                new Color (255, 210,   0),  // regular
                                new Color (255, 165, 120),  // expensive
                                new Color (255, 115, 105)}; // extortionate
    
    static
    {
        for(PriceLevels priceLevel : PriceLevels.values())
        {
            priceLevel.dictionaryKey = "priceLevels." + key_suffixes[priceLevel.ordinal()];
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
    
    public Color getColor()
    {
        return costsColor[this.ordinal()];
    }
    
    public boolean isCheap()
    {
        return this == CHEAP || this == VERY_CHEAP;
    }
}