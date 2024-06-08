package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.PaintableEntity;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

final class PaintableEntityStore<T extends PaintableEntity>
{
    private static final DependencyInjector
        dependencyInjector = DependencyInjector.getInstance();
    
    private final Queue<T>
        entities = new ArrayDeque<>();
    
    
    public T retrieve(PaintableEntityFactory<? extends T> factory)
    {
        return Optional.ofNullable(entities.poll())
                       .orElseGet(() -> makeInstanceWithDependenciesUsing(factory));
    }
    
    private T makeInstanceWithDependenciesUsing(PaintableEntityFactory<? extends T> factory)
    {
        T paintableEntity = factory.makeInstance();
        dependencyInjector.injectDependenciesFor(paintableEntity);
        return paintableEntity;
    }
    
    public void store(T paintableEntity)
    {
        entities.add(paintableEntity);
    }
    
    public int size()
    {
        return entities.size();
    }
}
