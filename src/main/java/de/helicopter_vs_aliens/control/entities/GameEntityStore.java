package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.GameEntity;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

final class GameEntityStore<T extends GameEntity>
{
    private final static DependencyInjector
        dependencyInjector = DependencyInjector.getInstance();
    
    private final Queue<T>
        entities = new ArrayDeque<>();
    
    
    public T retrieve(GameEntityFactory<? extends T> factory)
    {
        return Optional.ofNullable(entities.poll())
                       .orElseGet(() -> makeInstanceWithDependenciesUsing(factory));
    }
    
    private T makeInstanceWithDependenciesUsing(GameEntityFactory<? extends T> factory)
    {
        T gameEntity = factory.makeInstance();
        dependencyInjector.injectDependenciesFor(gameEntity);
        return gameEntity;
    }
    
    public void store(T gameEntity)
    {
        entities.add(gameEntity);
    }
    
    public int size()
    {
        return entities.size();
    }
}
