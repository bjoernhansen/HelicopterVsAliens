package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.GameEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public final class GameEntitySupplier
{
    private final Map<Class<? extends GameEntity>, GameEntityStore<GameEntity>>
        entityStores = new HashMap<>();
    
    public  <T extends GameEntity> T retrieve(GameEntityFactory<T> factory)
    {
        Class<? extends T> gameEntityClass = factory.getCorrespondingClass();
        GameEntity gameEntity = getGameEntityStore(gameEntityClass).retrieve(factory);
        return gameEntityClass.cast(gameEntity);
    }
    
    public void store(GameEntity gameEntity)
    {
        getGameEntityStore(gameEntity.getClass()).store(gameEntity);
    }
    
    public <T extends GameEntity> void storeAll(Queue<T> gameEntities)
    {
        gameEntities.forEach(this::store);
    }
 
    public <T extends GameEntity> int sizeOf(Class<T> classOfGameEntity)
    {
        return getGameEntityStore(classOfGameEntity).size();
    }
    
    private GameEntityStore<GameEntity> getGameEntityStore(Class<? extends GameEntity> classOfGameEntity)
    {
        return entityStores.computeIfAbsent(classOfGameEntity, gameEntityClass -> new GameEntityStore<>());
    }
}
