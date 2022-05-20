package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.model.enemy.EnemyType;

import java.util.*;

public final class EnemySelector
{
    private static final int
        OVERHANG_INDEX_FACTOR = 5525;
    
    private static final List<Integer>
        BORDERS = List.of(0, 3, 10, 25, 35, 75, 135, 310, 485, 660, 835, 2175, 3740, 3960, 9710);
        
    private final NavigableMap<Integer, EnemyType>
        map;
    
    EnemySelector()
    {
        List<EnemyType> enemyTypes = EnemyType.getRandomSelectionTypes();
        NavigableMap<Integer, EnemyType> tempMap = new TreeMap<>();
        for(int i = 0; i < enemyTypes.size(); i++)
        {
            tempMap.put(getBorderValue(i), enemyTypes.get(i));
        }
        map = Collections.unmodifiableNavigableMap(tempMap);
    }

    public EnemyType getType(int key)
    {
        return map.floorEntry(key)
                  .getValue();
    }
    
    private int getBorderValue(int index)
    {
        if(index < BORDERS.size())
        {
            return BORDERS.get(index);
        }
        int overhangIndex = index - BORDERS.size() + 1;
        int lastIndex = BORDERS.size()-1;
        return BORDERS.get(lastIndex) + overhangIndex * OVERHANG_INDEX_FACTOR;
    }
}