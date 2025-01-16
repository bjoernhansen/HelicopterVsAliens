package de.helicopter_vs_aliens.control.entities;

import java.util.HashMap;
import java.util.Map;

final class ManageablePaintableSupplier
{
    private final Map<Class<? extends ManageablePaintable>, ManageablePaintableStore<ManageablePaintable>>
        entityStores = new HashMap<>();
    
    private final DependencyInjector
        dependencyInjector;
    
    
    ManageablePaintableSupplier(DependencyInjector dependencyInjector)
    {
        this.dependencyInjector = dependencyInjector;
    }
    
    <T extends ManageablePaintable> T retrieve(ManageablePaintableFactory<T> factory)
    {
        Class<? extends T> paintableEntityClass = factory.getCorrespondingClass();
        ManageablePaintable paintableEntity = getManageablePaintableStore(paintableEntityClass).retrieve(factory);
        return paintableEntityClass.cast(paintableEntity);
    }
    
    void store(ManageablePaintable paintableEntity)
    {
        getManageablePaintableStore(paintableEntity.getClass()).store(paintableEntity);
    }
    
    void storeAll(Iterable<? extends ManageablePaintable> gameEntities)
    {
        gameEntities.forEach(this::store);
    }
 
    <T extends ManageablePaintable> int sizeOf(Class<T> classOfPaintableEntity)
    {
        return getManageablePaintableStore(classOfPaintableEntity).size();
    }
    
    private ManageablePaintableStore<ManageablePaintable> getManageablePaintableStore(Class<? extends ManageablePaintable> classOfPaintableEntity)
    {
        return entityStores.computeIfAbsent(classOfPaintableEntity, paintableEntityClass -> new ManageablePaintableStore<>(dependencyInjector));
    }
}
