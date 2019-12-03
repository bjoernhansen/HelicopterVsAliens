package de.helicopter_vs_aliens.model.enemy;

import java.util.*;

public class EnemySelector
{
    private NavigableMap<Integer, RangeTypePair>
        map = new TreeMap<>();
    
    private static final List<Integer>
        BORDERS = Collections.unmodifiableList(
            Arrays.asList(0, 3, 10, 25, 35, 75, 135, 310, 485, 660, 835, 2175, 3740, 3960, 9710, 15235, 20760, 26285, 31810));

    EnemySelector()
    {
        EnemyType[] values = EnemyType.getValues();
        for(int i = 0; i < BORDERS.size()-1; i++)
        {
            map.put(BORDERS.get(i), new RangeTypePair(BORDERS.get(i+1)-1, values[i]));
        }
    }

    public EnemyType getType(int key)
    {
        Map.Entry<Integer, RangeTypePair> entry = map.floorEntry(key);
        return entry.getValue().enemyType;
    }

    private static class RangeTypePair
    {
        final int upper;
        final EnemyType enemyType;

        RangeTypePair(int upper, EnemyType enemyType)
        {
            this.upper = upper;
            this.enemyType = enemyType;
        }
    }
}