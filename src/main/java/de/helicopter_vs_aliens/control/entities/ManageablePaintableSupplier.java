package de.helicopter_vs_aliens.control.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public final class ManageablePaintableSupplier
{
    private final Map<Class<? extends ManageablePaintable>, ManageablePaintableStore<ManageablePaintable>>
        entityStores = new HashMap<>();
    
    public <T extends ManageablePaintable> T retrieve(ManageablePaintableFactory<T> factory)
    {
        Class<? extends T> paintableEntityClass = factory.getCorrespondingClass();
        ManageablePaintable paintableEntity = getManageablePaintableStore(paintableEntityClass).retrieve(factory);
        return paintableEntityClass.cast(paintableEntity);
    }
    
    public void store(ManageablePaintable paintableEntity)
    {
        getManageablePaintableStore(paintableEntity.getClass()).store(paintableEntity);
    }
    
    public void storeAll(Queue<? extends ManageablePaintable> gameEntities)
    {
        gameEntities.forEach(this::store);
    }
 
    public <T extends ManageablePaintable> int sizeOf(Class<T> classOfPaintableEntity)
    {
        return getManageablePaintableStore(classOfPaintableEntity).size();
    }
    
    private ManageablePaintableStore<ManageablePaintable> getManageablePaintableStore(Class<? extends ManageablePaintable> classOfPaintableEntity)
    {
        return entityStores.computeIfAbsent(classOfPaintableEntity, paintableEntityClass -> new ManageablePaintableStore<>());
    }
}
