package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.GameEntity;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class GameEntityManager
{
    private final Map<Class<? extends GameEntity>, Queue<GameEntity>>
        entities = new HashMap<>();
    
    
    private <T extends GameEntity> T getGameEntity(GameEntityFactory<T> factory)
    {
        @SuppressWarnings("unchecked")
        T gameEntity = (T) Optional.ofNullable(entities.computeIfAbsent(factory.getCorrespondingClass(), gameEntityClass -> new ArrayDeque<>())
                                                       .poll())
                                   .orElseGet(factory::makeInstance);
        return gameEntity;
    }

    public void recycle(GameEntity gameEntity)
    {
        entities.computeIfAbsent(gameEntity.getClass(), gameEntityClass -> new ArrayDeque<>())
                .add(gameEntity);
    }
    
    public void recycleAll(Queue<GameEntity> gameEntities)
    {
        gameEntities.forEach(this::recycle);
    }
}
