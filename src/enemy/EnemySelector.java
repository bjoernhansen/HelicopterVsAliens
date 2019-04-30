package enemy;

import java.awt.font.NumericShaper;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class EnemySelector
{
    private NavigableMap<Integer, RangeTypePair> map;

    private static final int[] borders  = { 0, 3, 10, 25, 35, 75, 135, 310, 485, 660, 835, 2175, 3740, 3960, 9710,
                                            15235, 20760, 26285, 31810};

    EnemySelector()
    {
        map = new TreeMap<>();
        EnemyTypes[] values = EnemyTypes.values();
        for(int i = 0; i < borders.length-1; i++)
        {
            map.put(borders[i], new RangeTypePair(borders[i+1]-1, values[i]));
        }
    }

    public EnemyTypes getType(int key)
    {
        Map.Entry<Integer, RangeTypePair> entry = map.floorEntry(key);
        return entry.getValue().enemyType;
    }

    private static class RangeTypePair
    {
        final int upper;
        final EnemyTypes enemyType;

        RangeTypePair(int upper, EnemyTypes enemyType)
        {
            this.upper = upper;
            this.enemyType = enemyType;
        }
    }
}