package de.helicopter_vs_aliens.control.entities;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

final class ManageablePaintableStore<T extends ManageablePaintable>
{
    private final DependencyInjector
        dependencyInjector;
    
    private final Queue<T>
        entities = new ArrayDeque<>();
    
    
    ManageablePaintableStore(DependencyInjector dependencyInjector)
    {
        this.dependencyInjector = dependencyInjector;
    }
    
    T retrieve(ManageablePaintableFactory<? extends T> factory)
    {
        return Optional.ofNullable(entities.poll())
                       .orElseGet(() -> makeInstanceWithDependenciesUsing(factory));
    }
    
    private T makeInstanceWithDependenciesUsing(ManageablePaintableFactory<? extends T> factory)
    {
        T paintableEntity = factory.makeInstance();
        dependencyInjector.injectDependenciesFor(paintableEntity);
        return paintableEntity;
    }
    
    void store(T paintableEntity)
    {
        entities.add(paintableEntity);
    }
    
    int size()
    {
        return entities.size();
    }
}
