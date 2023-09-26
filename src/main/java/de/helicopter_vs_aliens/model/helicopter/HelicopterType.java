package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.BossLevel;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HeliosPainter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.KamaitachiPainter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.OrochiPainter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.PegasusPainter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.PhoenixPainter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.RochPainter;
import de.helicopter_vs_aliens.gui.PriceLevel;

import javax.sound.sampled.Clip;
import java.awt.Color;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;


public enum HelicopterType implements GameEntityFactory<Helicopter>
{
    PHOENIX(
        Phoenix.class,
        Phoenix::new,
        PhoenixPainter::new,
        Unlocker::getEmpty,
        Audio::getTeleport1),
    
    ROCH(
        Roch.class,
        Roch::new,
        RochPainter::new,
        Unlocker::getEmpty,
        Audio::getShieldUp),
    
    OROCHI(
        Orochi.class,
        Orochi::new,
        OrochiPainter::new,
        Unlocker::getForOrochi,
        Audio::getStunActivated),
    
    KAMAITACHI(
        Kamaitachi.class,
        Kamaitachi::new,
        KamaitachiPainter::new,
        Unlocker::getForKamaitachi,
        Audio::getPlasmaOn),
    
    PEGASUS(
        Pegasus.class,
        Pegasus::new,
        PegasusPainter::new,
        Unlocker::getForPegasus,
        Audio::getEmp),
    
    HELIOS(
        Helios.class,
        Helios::new,
        HeliosPainter::new,
        Unlocker::getEmpty,
        Audio::getShieldUp)
    {
        @Override
        public boolean isUnlocked()
        {
            return Events.recordTimeManager.hasAnyBossBeenKilledBefore();
        }

        @Override
        public PriceLevel getPriceLevelFor(StandardUpgradeType standardUpgradeType)
        {
            HelicopterType privilegedHelicopter = standardUpgradeType.getPrivilegedHelicopter();
            int bestNonFinalMainBossKill = Events.getNonFinalMainBossKillCountOf(privilegedHelicopter);
            return PriceLevel.getValues()[PriceLevel.getMaximum().ordinal() - bestNonFinalMainBossKill];
        }
    };
    
    public static final String
        FIFTH_SPECIAL_KEY_PREFIX = "upgrades.special.fifth.";
    
    private static final int[]
        SPELL_COSTS= {50, 30, 20, 200, 75, 250},  // Energiekosten für das Energie-Upgrade
        // TODO integer Max_VALUE anders lösen
        EFFECT_TIMES = {55, 85, 80, 100, Integer.MAX_VALUE, 65};
    
    private static final int[][]
        // Upgrade-Kosten-Level (0 - sehr günstig bis 4 - sehr teuer) für die Standard-Upgrades
        COSTS = {       {4, 2, 0, 1, 2, 3},	// Phoenix
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
    
    private static final List<HelicopterType>
        VALUES = List.of(values());
        
    private static final Set<HelicopterType>
        NORMAL_MODE_HELICOPTERS = Set.copyOf(EnumSet.range(PHOENIX, PEGASUS));
    
    // Die Kosten mancher Upgrades weichen für manche Helikopter-Klassen vom Standard ab.
    // Die HashMap ADDITIONAL_STANDARD_UPGRADE_COSTS enthält die Differenzen zu den vom Standard abweichenden Werten.
    private static final Map<String, Integer>
        ADDITIONAL_STANDARD_UPGRADE_COSTS = setAdditionalCosts();
    
    private final int
        number;
    
    private final String
        fifthSpecialDictionaryKey;
    
    private final Supplier<? extends Helicopter>
        instanceSupplier;
    
    private final Supplier<? extends Painter<? extends Helicopter>>
        painterInstanceSupplier;
    
    private final Class<? extends Helicopter>
        helicopterClass;
    
    private final Supplier<List<HelicopterType>>
        unlockerTypes;
    
    private final Supplier<Clip>
        specialSound;
       
    
    HelicopterType(Class<? extends Helicopter> helicopterClass,
                   Supplier<? extends Helicopter> instanceSupplier,
                   Supplier<? extends Painter<? extends Helicopter>> painterInstanceSupplier,
                   Supplier<List<HelicopterType>> unlockerTypes,
                   Supplier<Clip> specialSound)
    {
        this.number = ordinal()+1;
        this.helicopterClass = helicopterClass;
        this.instanceSupplier = instanceSupplier;
        this.painterInstanceSupplier = painterInstanceSupplier;
        this.fifthSpecialDictionaryKey = FIFTH_SPECIAL_KEY_PREFIX + this.getDesignation();
        this.unlockerTypes = unlockerTypes;
        this.specialSound = specialSound;
    }
    
    public static int count()
    {
        return VALUES.size();
    }
    
    public static List<HelicopterType> getValues()
    {
        return VALUES;
    }

    public List<HelicopterType> getUnlockerTypes()
    {
        return this.unlockerTypes.get();
    }
    
    public static Set<HelicopterType> getNormalModeHelicopters(){
        return NORMAL_MODE_HELICOPTERS;
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
               || getUnlockerTypes().stream().anyMatch(HelicopterType::hasReachedLevel20);
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
    
    // TODO in eigene Klasse auslagern: AdditionalCostsProvider
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
        
        return Map.copyOf(additionalCosts);
    }
    
    public int getAdditionalCosts(StandardUpgradeType standardUpgradeType, int upgradeLevel)
    {
        String key = String.format("%d%d%d", this.ordinal(), this.getPriceLevelFor(standardUpgradeType).ordinal(), upgradeLevel);
        return Optional.ofNullable(ADDITIONAL_STANDARD_UPGRADE_COSTS.get(key)).orElse(0);
    }
    
    @Override
    public Helicopter makeInstance()
    {
        return instanceSupplier.get();
    }
    
    @Override
    // TODO eingeschränkter Wildcard-Typ als Rückgabewert sollte immer vermieden werden (siehe Effective Java)
    public Class<? extends Helicopter> getCorrespondingClass()
    {
        return helicopterClass;
    }
    
    // TODO eingeschränkter Wildcard-Typ als Rückgabewert sollte immer vermieden werden (siehe Effective Java)
    public Painter<? extends Helicopter> makePainterInstance()
    {
        return painterInstanceSupplier.get();
    }
    
    public boolean hasDefeatedFinalBoss()
    {
        return hasPassed(BossLevel.FINAL_BOSS);
    }
    
    public boolean hasDefeatedFirstBoss()
    {
        return hasPassed(BossLevel.BOSS_1);
    }
    
    public boolean hasPassed(BossLevel bossLevel)
    {
        return !this.getRecordTime(bossLevel).equals(0L);
    }
    
    public Long getRecordTime(BossLevel bossLevel)
    {
        return Events.recordTimeManager.getRecordTime(this, bossLevel);
    }
    
    public int getNumber(){
        return this.number;
    }
    
    public boolean hasReachedLevel20()
    {
        return Events.helicoptersThatReachedLevel20.contains(this);
    }
    
    public Clip getSpecialSound()
    {
        return specialSound.get();
    }
    
    private static class Unlocker
    {
        private static final List<HelicopterType>
            EMPTY = Collections.emptyList(),
            FOR_OROCHI =List.of(HelicopterType.PHOENIX, HelicopterType.PEGASUS),
            FOR_KAMAITACHI = List.of(HelicopterType.ROCH, HelicopterType.PEGASUS),
            FOR_PEGASUS = List.of(HelicopterType.OROCHI, HelicopterType.KAMAITACHI);
        
        static List<HelicopterType> getEmpty()
        {
            return EMPTY;
        }
        
        static List<HelicopterType> getForOrochi()
        {
            return FOR_OROCHI;
        }
        
        static List<HelicopterType> getForKamaitachi()
        {
            return FOR_KAMAITACHI;
        }
        
        public static List<HelicopterType> getForPegasus()
        {
            return FOR_PEGASUS;
        }
    }
}