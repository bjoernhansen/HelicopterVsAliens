package de.helicopter_vs_aliens.gui;

import java.awt.*;

public enum PriceLevel
{
    VERY_CHEAP,
    CHEAP,
    REGULAR,
    EXPENSIVE (8),
    EXTORTIONATE (6);
    
    
    private static final String[]
            key_suffixes = {"veryCheap", "cheap", "medium", "expensive", "extortionate"};

    private static final PriceLevel[]
            defensiveCopyOfValues = values();
    
    private static final float[]
            UPGRADE_LEVEL_COST_FACTORS = {500, 2000, 6000, 16000, 36000, 80000, 176000, 368000, 792000};
        
    private static final float[]
            PRICE_FACTORS = {0.375f, 0.75f, 1f, 1.5f, 2.5f};
    
    private static final Color
            costsColor[] =  {   new Color(130, 255, 130),   // very cheap
                                new Color (210, 255, 180),  // cheap
                                new Color (255, 210,   0),  // regular
                                new Color (255, 165, 120),  // expensive
                                new Color (255, 115, 105)}; // extortionate
    
    static
    {
        for(PriceLevel priceLevel : PriceLevel.values())
        {
            priceLevel.dictionaryKey = "priceLevels." + key_suffixes[priceLevel.ordinal()];
        }
    }
    
    private int maxUpgradeLevel = 10;
    private String dictionaryKey;


    PriceLevel(){}

    PriceLevel(int maxUpgradeLevel)
    {
        this();
        this.maxUpgradeLevel = maxUpgradeLevel;
    }

    public static PriceLevel getMaximium()
    {
        return EXTORTIONATE;
    }

    public static int size()
    {
        return getValues().length;
    }

    public static PriceLevel[] getValues()
    {
        return defensiveCopyOfValues;
    }

    public int getMaximumUpgradeLevel()
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
    
    public int getBaseUpgradeCosts(int upgradeLevel)
    {
        return (int)(this.getPriceFactor() * UPGRADE_LEVEL_COST_FACTORS[upgradeLevel-1]);
    }
    
    private float getPriceFactor()
    {
        return PRICE_FACTORS[this.ordinal()];
    }
}