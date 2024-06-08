package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.PaintableEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public final class PaintableEntitySupplier
{
    private final Map<Class<? extends PaintableEntity>, PaintableEntityStore<PaintableEntity>>
        entityStores = new HashMap<>();
    
    public  <T extends PaintableEntity> T retrieve(PaintableEntityFactory<T> factory)
    {
        Class<? extends T> paintableEntityClass = factory.getCorrespondingClass();
        PaintableEntity paintableEntity = getPaintableEntityStore(paintableEntityClass).retrieve(factory);
        return paintableEntityClass.cast(paintableEntity);
    }
    
    public void store(PaintableEntity paintableEntity)
    {
        getPaintableEntityStore(paintableEntity.getClass()).store(paintableEntity);
    }
    
    public <T extends PaintableEntity> void storeAll(Queue<T> gameEntities)
    {
        gameEntities.forEach(this::store);
    }
 
    public <T extends PaintableEntity> int sizeOf(Class<T> classOfPaintableEntity)
    {
        return getPaintableEntityStore(classOfPaintableEntity).size();
    }
    
    private PaintableEntityStore<PaintableEntity> getPaintableEntityStore(Class<? extends PaintableEntity> classOfPaintableEntity)
    {
        return entityStores.computeIfAbsent(classOfPaintableEntity, paintableEntityClass -> new PaintableEntityStore<>());
    }
}
