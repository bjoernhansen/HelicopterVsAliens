package de.helicopter_vs_aliens.control.entities;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

final class ManageablePaintableStore<T extends ManageablePaintable>
{
    private static final DependencyInjector
        dependencyInjector = DependencyInjector.getInstance();
    
    private final Queue<T>
        entities = new ArrayDeque<>();
    
    
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
