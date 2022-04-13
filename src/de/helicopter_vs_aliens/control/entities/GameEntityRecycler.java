package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.Paintable;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;


public class GameEntityRecycler
{
    private final Map<Class<? extends GameEntity>, Queue<? extends GameEntity>>
        recyclingQueues = new HashMap<>();
    
    private final GameEntityProvider
        gameEntityProvider = new GameEntityProvider();
    
    
    public <T extends GameEntity> T retrieve(Class<T> classOfGameEntity)
    {
        String test = "";
        T gameEntity = (T) recyclingQueues.computeIfAbsent(classOfGameEntity, c -> new LinkedList<T>())
                                          .poll();
        if(gameEntity == null)
        {
            return gameEntityProvider.makeEntityOf(classOfGameEntity);
        }
        return gameEntity;
    }
    
    public <T extends GameEntity> void store(T gameEntity)
    {
        Queue<T> gameEntityQueue = (Queue<T>)recyclingQueues.computeIfAbsent(gameEntity.getClass(), c -> new LinkedList<T>());
        gameEntityQueue.add(gameEntity);
    }
    
    public <T extends GameEntity> void storeAll(Queue<T> gameEntities)
    {
        gameEntities.forEach(this::store);
    }
    
    public <T extends GameEntity> int sizeOf(Class<T> classOfGameEntity)
    {
        return Objects.requireNonNull(recyclingQueues.putIfAbsent(classOfGameEntity, new LinkedList<T>()))
                      .size();
    }
}