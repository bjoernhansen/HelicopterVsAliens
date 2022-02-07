package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.PriceLevel;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;


public enum HelicopterType
{
    PHOENIX(Phoenix::new),
    ROCH(Roch::new),
    OROCHI(Orochi::new)
    {
        @Override
        public List<HelicopterType> getUnlockerTypes()
        {
            return OROCHI_UNLOCKER;
        }
    },
    KAMAITACHI(Kamaitachi::new)
    {
        @Override
        public List<HelicopterType> getUnlockerTypes()
        {
            return KAMAITACHI_UNLOCKER;
        }
    },
    PEGASUS(Pegasus::new)
    {
        @Override
        public List<HelicopterType> getUnlockerTypes()
        {
            return PEGASUS_UNLOCKER;
        }
    },
    HELIOS(Helios::new)
    {
        @Override
        public boolean isUnlocked()
        {
            return Events.hasAnyBossBeenKilledBefore();
        }

        @Override
        public PriceLevel getPriceLevelFor(StandardUpgradeType standardUpgradeType)
        {
            HelicopterType privilegedHelicopter = standardUpgradeType.getPrivilegedHelicopter();
            int bestNonFinalMainBossKill = Events.getBestNonFinalMainBossKillBy(privilegedHelicopter);
            return PriceLevel.getValues()[PriceLevel.getMaximium().ordinal() - bestNonFinalMainBossKill];
        }
    };

    private static final int[]
        SPELL_COSTS= {50, 30, 20, 200, 75, 250},  // Energiekosten für das Energie-Upgrade
        // TODO integer Max_VALUE anders lösen
        EFFECT_TIMES = {55, 85, 80, 100, Integer.MAX_VALUE, 65};
    
    private static final int[][]
        // Upgrade-Kosten-Level (0 - sehr günstig bis 4 - sehr teuer) für die Standard-Upgrades
        COSTS = {   {4, 2, 0, 1, 2, 3},	// Phoenix
                        {1, 3, 4, 0, 4, 2},	// Roch
                        {0, 0, 1, 2, 3, 4}, // Orochi
                        {2, 1, 3, 4, 0, 1}, // Kamaitachi
                        {3, 4, 2, 3, 1, 0}, // Pegasus
                        {2, 2, 2, 2, 2, 2}};// Helios

    private static final Color [][]
            helicopterColor = {
                   // Phoenix
                   {new Color(110, 100,  45),
                    new Color(110, 100,  80),
                    new Color(180, 160, 100),
                    new Color(190, 170,  90)},
                   // Roch
                   {new Color(120,  45, 130),
                    new Color(115,  85, 125),
                    new Color(167, 101, 196),
                    new Color(201, 131, 235)},
                   // Orochi
                   {new Color(180,  80,   0),
                    new Color(150, 110,  60),
                    new Color(240, 110,  60),
                    new Color(255, 170,  85)},
                   // Kamaitachi
                   {new Color(150,  30,  30),
                    new Color(150,  75,  85),
                    new Color(225,  65,  75),
                    new Color(255, 130, 130)},
                   // Pegasus
                   {new Color(  0, 125, 125),
                    new Color( 85, 125, 125),
                    new Color(  0, 170, 170),
                    new Color(  0, 230, 230)},
                   // Helios
                   {new Color(225, 225, 225),
                    new Color(180, 175, 150),
                    new Color(125, 125, 125),
                    new Color(110, 110, 110)}};
    
        
    private static final HelicopterType[]
        defensiveCopyOfValues = values();
    
    private static final List<HelicopterType>
        NO_UNLOCKER = Collections.unmodifiableList(new ArrayList<>()),
        OROCHI_UNLOCKER = Collections.unmodifiableList(Arrays.asList(PHOENIX, PEGASUS)),
        KAMAITACHI_UNLOCKER = Collections.unmodifiableList(Arrays.asList(ROCH, PEGASUS)),
        PEGASUS_UNLOCKER = Collections.unmodifiableList(Arrays.asList(OROCHI, KAMAITACHI));
    
    // Die Kosten mancher Upgrades weichen für manche Helicopterklassen vom Standard ab.
    // Die HashMap ADDITIONAL_STANDARD_UPGRADE_COSTS enthält die Modifikationswerte.
    private static final Map<String, Integer>
        ADDITIONAL_STANDARD_UPGRADE_COSTS = setAdditionalCosts();
    
    private final String
        fifthSpecialDictionaryKey;
    
    private final Supplier<? extends Helicopter>
        instance;
    
    
    HelicopterType(Supplier<? extends Helicopter> instance)
    {
        this.instance = instance;
        this.fifthSpecialDictionaryKey = "upgrades.special.fifth." + this.getDesignation();
    }
    
    public static int size()
    {
        return getValues().length;
    }
    
    public static HelicopterType[] getValues()
    {
        return defensiveCopyOfValues;
    }

    public List<HelicopterType> getUnlockerTypes()
    {
        return NO_UNLOCKER;
    }
    
    public static HelicopterType getDefault()
    {
        return HELIOS;
    }
    
    public String getDesignation()
    {
        return this.name().toLowerCase();
    }

    public int getSpellCosts()
    {
        return SPELL_COSTS[this.ordinal()];
    }
    
    public int getEffectTime()
    {
        return EFFECT_TIMES[this.ordinal()];
    }

    public int getMaximumUpgradeLevelFor(StandardUpgradeType standardUpgradeType)
    {
        return this.getPriceLevelFor(standardUpgradeType).getMaximumUpgradeLevel();
    }

    public PriceLevel getPriceLevelFor(StandardUpgradeType standardUpgradeType)
    {
        return PriceLevel.getValues()[COSTS[this.ordinal()][standardUpgradeType.ordinal()]];
    }

    public int getInitialUpgradeLevelFor(StandardUpgradeType standardUpgradeType)
    {
        return this.getPriceLevelFor(standardUpgradeType).isCheap() ? 2 : 1;
    }

    public boolean isUnlocked()
    {
        return getUnlockerTypes().isEmpty()
            || Events.reachedLevelTwenty[getUnlockerTypes().get(0).ordinal()]
            || Events.reachedLevelTwenty[getUnlockerTypes().get(1).ordinal()];
    }

    public Color getStandardPrimaryHullColor()
    {
        return helicopterColor[this.ordinal()][0];
    }

    public Color getStandardSecondaryHullColor()
    {
        return helicopterColor[this.ordinal()][1];
    }

    public Color getPlatedPrimaryHullColor()
    {
        return helicopterColor[this.ordinal()][2];
    }

    public Color getPlatedSecondaryHullColor()
    {
        return helicopterColor[this.ordinal()][3];
    }
    
    public String getFifthSpecialDictionaryKey()
    {
        return fifthSpecialDictionaryKey;
    }
    
    private static Map<String, Integer> setAdditionalCosts()
    {
        HashMap<String, Integer> additionalCosts = new HashMap<> ();
        
        // Roch
        additionalCosts.put("103", -250);
        additionalCosts.put("136", 126000);
        additionalCosts.put("137", 502000);
        additionalCosts.put("141", 250);
        additionalCosts.put("144", 63000);
        additionalCosts.put("145", 251000);
        
        // Orochi
        additionalCosts.put("203", -250);
        additionalCosts.put("231", 250);
        additionalCosts.put("236", 113000);
        additionalCosts.put("237", 450000);
        additionalCosts.put("241", 250);
        additionalCosts.put("244", 56000);
        additionalCosts.put("245", 225000);
        
        // Kamaitachi
        additionalCosts.put("331", 250);
        additionalCosts.put("336", 40000);
        additionalCosts.put("337", 160000);
        additionalCosts.put("341", 250);
        additionalCosts.put("344", 20000);
        additionalCosts.put("345", 80000);
        
        // Pegasus
        additionalCosts.put("403", -250);
        additionalCosts.put("436", 74000);
        additionalCosts.put("437", 299000);
        additionalCosts.put("444", 37000);
        additionalCosts.put("445", 150000);
        
        return Collections.unmodifiableMap(new HashMap<>(additionalCosts));
    }
    
    public int getAdditionalCosts(StandardUpgradeType standardUpgradeType, int upgradeLevel)
    {
        String key = String.format("%d%d%d", this.ordinal(), this.getPriceLevelFor(standardUpgradeType).ordinal(), upgradeLevel);
        return Optional.ofNullable(ADDITIONAL_STANDARD_UPGRADE_COSTS.get(key)).orElse(0);
    }
    
    public Helicopter makeInstance()
    {
        return instance.get();
    }
}