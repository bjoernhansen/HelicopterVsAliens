package de.helicopter_vs_aliens.util;

import java.util.EnumMap;
import java.util.Map;


public class EnumTable<C extends Enum<C>, R extends Enum<R>, V>
{
    private final Map<C, Map<R, V>>
        columns;

    private final Class<R>
        rowKeyType;


    public EnumTable(Class<C> columnKeyType, Class<R> rowKeyType)
    {
        columns = new EnumMap<>(columnKeyType);
        this.rowKeyType = rowKeyType;
    }

    public V get(C column, R row)
    {
        return getRowMap(column).get(row);
    }

    public void put(C column, R row, V value)
    {
        getRowMap(column).put(row, value);
    }

    private Map<R, V> getRowMap(C column)
    {
        return columns.computeIfAbsent(column, c -> new EnumMap<R, V>(rowKeyType));
    }

    public void clear()
    {
        columns.clear();
    }
}
