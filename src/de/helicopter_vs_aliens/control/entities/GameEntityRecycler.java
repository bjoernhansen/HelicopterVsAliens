package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.Paintable;

import java.util.*;


public class GameEntityRecycler
{
    private final Map<Class, Queue<? extends Paintable>>
        recyclingQueues = new HashMap<>();
    
    private final GameEntityProvider
        gameEntityProvider = new GameEntityProvider();
    
    
    public <T extends Paintable> T retrieve(Class<T> classOfGameEntity)
    {
        Queue <T> gameEntityQueue = (Queue<T>)recyclingQueues.get(classOfGameEntity);
        if(gameEntityQueue == null || gameEntityQueue.isEmpty())
        {
            return gameEntityProvider.makeEntityOf(classOfGameEntity);
        }
        return gameEntityQueue.poll();
    }
    
    public <T extends Paintable> void store(T gameEntity)
    {
        Queue <T> gameEntityQueue = (Queue<T>)recyclingQueues.get(gameEntity.getClass());
        if(gameEntityQueue == null)
        {
            gameEntityQueue = new LinkedList<>();
            recyclingQueues.put(gameEntity.getClass(), gameEntityQueue);
        }
        gameEntityQueue.add(gameEntity);
    }
    
    public <T extends Paintable> void storeAll(List<T> gameEntities)
    {
        if(gameEntities.isEmpty())
        {
            return;
        }
        Queue <T> gameEntityQueue = (Queue<T>)recyclingQueues.get(gameEntities.getClass());
        if(gameEntityQueue == null)
        {
            gameEntityQueue = new LinkedList<>();
            recyclingQueues.put(gameEntities.getClass(), gameEntityQueue);
        }
        gameEntityQueue.addAll(gameEntities);
    }
    
    public <T extends Paintable> int sizeOf(Class<T> classOfGameEntity)
    {
        Queue <T> gameEntityQueue = (Queue<T>)recyclingQueues.get(classOfGameEntity);
        if(gameEntityQueue == null)
        {
            return 0;
        }
        return gameEntityQueue.size();
    }
}