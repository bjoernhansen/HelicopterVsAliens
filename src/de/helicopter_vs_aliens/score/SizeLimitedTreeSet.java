package de.helicopter_vs_aliens.score;

import java.io.Serializable;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Consumer;

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
    
    void add(E element)
    {
        navigableSet.add(element);
        if(navigableSet.size() > maxSize)
        {
            navigableSet.pollLast();
        }
    }
    
    public void forEach(Consumer<? super E> action)
    {
        navigableSet.forEach(action);
    }
}
