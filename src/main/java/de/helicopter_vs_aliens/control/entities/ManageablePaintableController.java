package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

// TODO finish implementation

public final class ManageablePaintableController implements ActiveManageablePaintableProvider
{
    // TODO Verwaltung anders lösen, eventuell wie in der Klasse PaintableEntitySupplier
    // TODO ggf. ist auch eine Zusammenführung oder eine Verwaltung über eine übergeordnete Klasse denkbar
    // TODO sobald die inaktiven hier nicht mehr nötig sind, kann der Umbau beginnen
    // TODO die ungenutzten Methoden in dieser Klasse kommen dann ggf. zum Einsatz
    // TODO hier auch nicht die SceneryObjects ehemals BackGroundObject vergessen
    // TODO das lässt sich vielleicht auch über eine Map abbilden , eine EnumMap (PaintableEntityGroupType) von EnumMaps (Collection Subgroup)
    // TODO API erstellen, durch die Anfragen wie folgt möglich sind:
    // TODO ManageablePaintableController manageablePaintableController = getGameRessourceProvider().getManageablePaintableController();
    // TODO manageablePaintableController.manageType(ManageablePaintableGroupType.SCENERY_OBJECT).forEach( ... ).forEachHaving( ... ).do( ... ).forEachHaving( ... ).do( ... ).removeIf( ... ).execute();
    
    private final ManageablePaintableSupplier
        manageablePaintableSupplier;
    
    private final Map<ManageablePaintableGroupType, Queue<ManageablePaintable>>
        paintableQueues;
    
    public ManageablePaintableController(GameRessourceProvider gameRessourceProvider)
    {
        DependencyInjector dependencyInjector = DependencyInjector.instanceFor(gameRessourceProvider);
        manageablePaintableSupplier = new ManageablePaintableSupplier(dependencyInjector);
        paintableQueues = Collections.unmodifiableMap(createPaintableQueuesEnumMap());
    }
    
    private static Map<ManageablePaintableGroupType, Queue<ManageablePaintable>> createPaintableQueuesEnumMap()
    {
        EnumMap<ManageablePaintableGroupType, Queue<ManageablePaintable>> tempPaintableQueues = new EnumMap<>(
            ManageablePaintableGroupType.class);
        ManageablePaintableGroupType.getValues().forEach(type -> tempPaintableQueues.put(type, new ArrayDeque<>()));
        return tempPaintableQueues;
    }
    
    public void clearActiveEntities(ManageablePaintableGroupType groupType)
    {
        Queue<? extends ManageablePaintable> groupTypeOwners = paintableQueues.get(groupType);
        manageablePaintableSupplier.storeAll(groupTypeOwners);
        groupTypeOwners.clear();
    }
    
    public int numberOfActiveEntities(ManageablePaintableGroupType groupType)
    {
        return paintableQueues.get(groupType)
                              .size();
    }
    
    public boolean isEmptyFor(ManageablePaintableGroupType groupType)
    {
        return numberOfActiveEntities(groupType) == 0;
    }
    
    public <T extends ManageablePaintable> int numberOfInactiveEntities(Class<T> classOfManageablePaintable)
    {
        return manageablePaintableSupplier.sizeOf(classOfManageablePaintable);
    }
    
    public <T extends ManageablePaintable> void forEachActiveEntity(
        ManageablePaintableGroupType groupType,
        Consumer<T> action)
    {
        paintableQueues.get(groupType)
                       .stream()
                       .map(groupType.<T>getBaseClass()::cast)
                       .forEach(action);
    }
    
    public <T extends ManageablePaintable> void forEachActiveEntityIf(ManageablePaintableGroupType groupType,
                                                                      Predicate<T> applyCondition,
                                                                      Consumer<T> action)
    {
        paintableQueues.get(groupType)
                       .stream()
                       .map(groupType.<T>getBaseClass()::cast)
                       .filter(applyCondition)
                       .forEach(action);
    }
    
    public <T extends ManageablePaintable> void removeIf(ManageablePaintableGroupType groupType,
                                                         Predicate<T> removeCondition)
    {
        @SuppressWarnings("unchecked")
        Collection<T> manageablePaintables = (Collection<T>)paintableQueues.get(groupType);
        manageablePaintables
            .stream()
            .map(groupType.<T>getBaseClass()::cast)
            .filter(removeCondition)
            .forEach(manageablePaintableSupplier::store);
        manageablePaintables.removeIf(removeCondition);
    }
    
    public void removeEnemyToBackgroundIf(Predicate<? super Enemy> removeCondition)
    {
        Queue<ManageablePaintable> intactEnemies = paintableQueues.get(ManageablePaintableGroupType.INTACT_ENEMY);
        intactEnemies.stream()
                     .map(Enemy.class::cast)
                     .filter(removeCondition)
                     .forEach(paintableQueues.get(ManageablePaintableGroupType.DESTROYED_ENEMY)::add);
        intactEnemies.removeIf(intactEnemy -> removeCondition.test((Enemy)intactEnemy));
    }
    
    public <T extends ManageablePaintable> T activateEntity(ManageablePaintableFactory<T> factory)
    {
        T manageablePaintable = manageablePaintableSupplier.retrieve(factory);
        ManageablePaintableGroupType groupType = manageablePaintable.getGroupType();
        Queue<ManageablePaintable> paintableQueue = paintableQueues.get(groupType);
        
        // TODO die configure und post deactivation Methoden sollten noch ins Interface und verwendet werden
        // TODO wenn alle Enemies auf nicht destroyed zurückgestellt werden, kann die fallunterscheidung wegfallen
        
        if(groupType == ManageablePaintableGroupType.DESTROYED_ENEMY)
        {
            paintableQueues.get(ManageablePaintableGroupType.INTACT_ENEMY)
                           .add(manageablePaintable);
        }
        else {
            paintableQueue.add(manageablePaintable);
        }
        
        return manageablePaintable;
    }
    
    public boolean isMajorBossActive()
    {
        return isPrimaryEnemyQualifying(e -> e.getType().isMajorBoss());
    }
    
    public boolean isPrimaryEnemyQualifying(Predicate<Enemy> selectionCondition)
    {
        return paintableQueues.get(ManageablePaintableGroupType.INTACT_ENEMY)
                              .stream()
                              .findFirst()
                              .filter(e -> selectionCondition.test((Enemy)e))
                              .isPresent();
    }
}
