package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.util.function.Consumer;
import java.util.function.Predicate;


public interface ManageablePaintableService
{
    void clearActiveEntities(ManageablePaintableGroupType groupType);
    
    int numberOfActiveEntities(ManageablePaintableGroupType groupType);
    
    boolean isEmptyFor(ManageablePaintableGroupType groupType);
    
    <T extends ManageablePaintable> int numberOfInactiveEntities(Class<T> classOfManageablePaintable);
    
    <T extends ManageablePaintable> void forEachActiveEntity(
        ManageablePaintableGroupType groupType,
        Consumer<T> action);
    
    <T extends ManageablePaintable> void forEachActiveEntityIf(ManageablePaintableGroupType groupType,
                                                                      Predicate<T> applyCondition,
                                                                      Consumer<T> action);
    
    <T extends ManageablePaintable> void removeIf(ManageablePaintableGroupType groupType,
                                                         Predicate<T> removeCondition);
    
    void removeEnemyToBackgroundIf(Predicate<Enemy> removeCondition);
    
    <T extends ManageablePaintable> T activateEntity(ManageablePaintableFactory<T> factory);
    
    boolean isMajorBossActive();
    
    boolean isPrimaryEnemyQualifying(Predicate<Enemy> selectionCondition);
}
