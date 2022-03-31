package de.helicopter_vs_aliens.util;

import java.io.Serializable;
import java.util.PriorityQueue;
import java.util.Queue;

public class SizeLimitedPriorityQueue<E> implements Serializable
{
    private final Queue<E>
        queue = new PriorityQueue<>();
    
    private final int
        maxSize;
    
    public SizeLimitedPriorityQueue(int maxSize){
        this.maxSize = maxSize;
    }
    
    public void add(E element){
        queue.add(element);
        if(queue.size() > maxSize)
        {
            queue.poll();
        }
    }
    
    public Queue<E> asQueue()
    {
        return queue;
    }
}
