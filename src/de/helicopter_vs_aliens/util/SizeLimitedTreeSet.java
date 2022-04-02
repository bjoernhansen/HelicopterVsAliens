package de.helicopter_vs_aliens.util;

import java.io.Serializable;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public class SizeLimitedTreeSet<E extends Comparable<E>> implements Serializable
{
    private final NavigableSet<E>
        navigableSet = new TreeSet<>();
    
    private final int
        maxSize;
    
    public SizeLimitedTreeSet(int maxSize)
    {
        this.maxSize = maxSize;
    }
    
    public void add(E element)
    {
        navigableSet.add(element);
        if(navigableSet.size() > maxSize)
        {
            navigableSet.pollLast();
        }
    }
    
    public Set<E> asSet()
    {
        return navigableSet;
    }
}
