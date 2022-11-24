package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.control.BossLevel;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;


public class RecordTimeManager implements Serializable
{
    private Map<HelicopterType, Map<BossLevel, Long>>
        recordTimes = getEmptyRecordTimesMap();
    
    private boolean
        isEmpty = true;
    
    
    private Map<HelicopterType, Map<BossLevel, Long>> getEmptyRecordTimesMap()
    {
        Map<HelicopterType, Map<BossLevel, Long>> emptyRecordTimesMap = new EnumMap<>(HelicopterType.class);
        
        HelicopterType.getValues()
                      .forEach(helicopterType -> {
                              Map<BossLevel, Long> bossLevelTimes = new EnumMap<>(BossLevel.class);
                              BossLevel.getValues()
                                       .forEach(bossLevel -> bossLevelTimes.put(bossLevel, 0L));
                              emptyRecordTimesMap.put(helicopterType, bossLevelTimes);
                          }
                      );
        
        return emptyRecordTimesMap;
    }
    
    public void eraseRecordTimes()
    {
        recordTimes = getEmptyRecordTimesMap();
        isEmpty = true;
    }
    
    public void saveRecordTime(HelicopterType helicopterType, BossLevel bossLevel, long newRecordTime)
    {
        long oldRecordTime = helicopterType.getRecordTime(bossLevel);
        long nextRecordTime = helicopterType.hasPassed(bossLevel)
                              ? Math.min(oldRecordTime, newRecordTime)
                              : newRecordTime;
        recordTimes.get(helicopterType).put(bossLevel, nextRecordTime);
        isEmpty = false;
    }
    
    public Long getRecordTime(HelicopterType helicopterType, BossLevel bossLevel)
    {
        return recordTimes.get(helicopterType)
                          .get(bossLevel);
    }
    
    public boolean isEmpty()
    {
        return isEmpty;
    }
    
    public boolean hasAnyBossBeenKilledBefore()
    {
        return HelicopterType.getValues()
                             .stream()
                             .anyMatch(HelicopterType::hasDefeatedFirstBoss);
    }
}