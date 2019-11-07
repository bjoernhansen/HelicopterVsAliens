package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.PriceLevels;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public enum HelicopterTypes
{
    PHOENIX,
    ROCH,
    OROCHI
    {
        @Override
        public List<HelicopterTypes> getUnlockerTypes()
        {
            return OROCHI_UNLOCKER;
        }
    },
    KAMAITACHI
    {
        @Override
        public List<HelicopterTypes> getUnlockerTypes()
        {
            return KAMAITACHI_UNLOCKER;
        }
    },
    PEGASUS
    {
        @Override
        public List<HelicopterTypes> getUnlockerTypes()
        {
            return PEGASUS_UNLOCKER;
        }
    },
    HELIOS
    {
        @Override
        public boolean isUnlocked()
        {
            return Events.hasAnyBossBeenKilledBefore();
        }

        @Override
        public int getUpgradeCosts(int upgradeTypeNumber)
        {
            StandardUpgradeTypes standardUpgradeType = StandardUpgradeTypes.values()[upgradeTypeNumber];
            HelicopterTypes privilegedHelicopter = standardUpgradeType.getPrivilegedHelicopter();
            int bestNonFinalMainBossKill = Events.getBestNonFinalMainBossKillBy(privilegedHelicopter);
            return PriceLevels.getMaximium().ordinal() - bestNonFinalMainBossKill;
        }
    };

    private static final int
        SPELL_COSTS[]= {50, 30, 20, 200, 75, 250},  // Energiekosten für das Energie-Upgrade
        // TODO integer Max_VALUE anders lösen
        EFFECT_TIMES[] = {55, 85, 80, 100, Integer.MAX_VALUE, 65},
    
        // Upgrade-Kosten-Level (0 - sehr günstig bis 4 - sehr teuer) für die Standard-Upgrades
        COSTS[][] = {   {4, 2, 0, 1, 2, 3},	// Phoenix
                        {1, 3, 4, 0, 4, 2},	// Roch
                        {0, 0, 1, 2, 3, 4}, // Orochi
                        {2, 1, 3, 4, 0, 1}, // Kamaitachi
                        {3, 4, 2, 3, 1, 0}, // Pegasus
                        {2, 2, 2, 2, 2, 2}};// Helios

    private static final Color [][]
            helicopterColor = {
                   {new Color(110, 100,  45),
                    new Color(110, 100,  80),
                    new Color(180, 160, 100),
                    new Color(190, 170,  90)},
                   {new Color(120,  45, 130),
                    new Color(115,  85, 125),
                    new Color(167, 101, 196),
                    new Color(201, 131, 235)},
                   {new Color(180,  80,   0),
                    new Color(150, 110,  60),
                    new Color(240, 110,  60),
                    new Color(255, 170,  85)},
                   {new Color(150,  30,  30),
                    new Color(150,  75,  85),
                    new Color(225,  65,  75),
                    new Color(255, 130, 130)},
                   {new Color(  0, 125, 125),
                    new Color( 85, 125, 125),
                    new Color(  0, 170, 170),
                    new Color(  0, 230, 230)},
                   {new Color(225, 225, 225),
                    new Color(180, 175, 150),
                    new Color(125, 125, 125),
                    new Color(110, 110, 110)}};
    
        
    private static final HelicopterTypes[]
            defensiveCopyOfValues = values();
    
    private static final List<HelicopterTypes>
        NO_UNLOCKER = Collections.unmodifiableList(new ArrayList<>()),
        OROCHI_UNLOCKER = Collections.unmodifiableList(Arrays.asList(PHOENIX, PEGASUS)),
        KAMAITACHI_UNLOCKER = Collections.unmodifiableList(Arrays.asList(ROCH, PEGASUS)),
        PEGASUS_UNLOCKER = Collections.unmodifiableList(Arrays.asList(OROCHI, KAMAITACHI));
        
    static
    {
        for(HelicopterTypes helicopterType : HelicopterTypes.getValues())
        {
            helicopterType.fifthSpecialdictionaryKey = "upgrades.special.fifth." + helicopterType.getDesignation();
        }
    }
    
    private String fifthSpecialdictionaryKey;
    
    
    public static int size()
    {
        return getValues().length;
    }
    
    public static final HelicopterTypes[] getValues()
    {
        return defensiveCopyOfValues;
    }

    public List<HelicopterTypes> getUnlockerTypes()
    {
        return NO_UNLOCKER;
    }
    
    public static HelicopterTypes getDefault()
    {
        return HELIOS;
    }
    
    public String getDesignation()
    {
        return this.name().toLowerCase();
    }

    int getSpellCosts()
    {
        return SPELL_COSTS[this.ordinal()];
    }
    
    public int getEffectTime()
    {
        return EFFECT_TIMES[this.ordinal()];
    }
    
    // TODO langfristig sollte diese Methode Price Level enum type zurück geben
    public int getUpgradeCosts(int standardUpgradeType)
    {
        return COSTS[this.ordinal()][standardUpgradeType];
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
    
    public String getFifthSpecialdictionaryKey()
    {
        return fifthSpecialdictionaryKey;
    }
}