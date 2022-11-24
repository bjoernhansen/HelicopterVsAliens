package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.control.BossLevel;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public final class ScoreScreenTimes implements Serializable
{
    private final Map<BossLevel, Long>
        scoreScreenTimes = new EnumMap<>(BossLevel.class);
    
    
    public void put(BossLevel bossLevel, Long value)
    {
        scoreScreenTimes.put(bossLevel, value);
    }
    
    public Long get(BossLevel bossLevel)
    {
        return scoreScreenTimes.get(bossLevel);
    }
    
    public Long getTotalPlayingTime()
    {
        return scoreScreenTimes.get(BossLevel.FINAL_BOSS);
    }
    
    public void setTotalPlayingTime(Long totalPlayingTime)
    {
        scoreScreenTimes.put(BossLevel.FINAL_BOSS, totalPlayingTime);
    }
    
    public void clear()
    {
        scoreScreenTimes.clear();
    }
}