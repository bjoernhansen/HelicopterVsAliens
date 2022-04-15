package de.helicopter_vs_aliens.control;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum BossLevel
{
    BOSS_1(1),
    BOSS_2(2),
    BOSS_3(3),
    BOSS_4(4),
    FINAL_BOSS(5);
    
    
    private static final int
        LEVEL_PER_BOSS = 10;
    
    private static final List<BossLevel>
        VALUES = List.of(values());
    
    private static final Map<Integer, BossLevel>
        BOSS_LEVEL_MAP = Arrays.stream(BossLevel.values())
                             .collect(Collectors.toUnmodifiableMap(BossLevel::getNextLevel, Function.identity()));
    
    private static final Set<BossLevel>
        NON_FINAL_BOSS_LEVEL = Set.copyOf(EnumSet.range(BOSS_1, BOSS_4));
    
    
    private final int
        nextLevel,
        bossNr;
    
    BossLevel(int bossNr)
    {
        this.bossNr = bossNr;
        this.nextLevel = bossNr * LEVEL_PER_BOSS + 1;
    }
    
    public static List<BossLevel> getValues()
    {
        return VALUES;
    }
    
    public static BossLevel getCurrentBossLevel()
    {
        return BOSS_LEVEL_MAP.get(Events.level);
    }
    
    private int getNextLevel(){
        return nextLevel;
    }
    
    public static Set<BossLevel> getNonFinalBossLevel()
    {
        return NON_FINAL_BOSS_LEVEL;
    }
    
    public int getBossNr()
    {
        return bossNr;
    }
}