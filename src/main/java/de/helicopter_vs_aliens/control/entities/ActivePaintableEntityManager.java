package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO finish implementation

public final class ActivePaintableEntityManager implements ActivePaintableEntityProvider
{
    // TODO Verwaltung anders lösen, eventuell wie in der Klasse PaintableEntitySupplier
    // ggf. ist auch eine Zusammenführung oder eine Verwaltung über eine übergeordnete Klasse denkbar
    // TODO sobald die inaktiven hier nicht mehr nötig sind, kann der Umbau beginnen
    // TODO die ungenutzten Methoden in dieser Klasse kommen dann ggf. zum Einsatz
    // TODO hier auch nicht die SceneryObjects ehemals BackGroundObject vergessen
    // TODO das lässt sich vielleicht auch über eine Map abbilden , eine EnumMap (PaintableEntityGroupType) von EnumMaps (Collection Subgroup)
    
    private final Map<CollectionSubgroupType, Queue<Enemy>>
        enemies = new EnumMap<>(CollectionSubgroupType.class);
    
    private final Map<CollectionSubgroupType, Queue<Missile>>
        missiles = new EnumMap<>(CollectionSubgroupType.class);
    
    private final Map<CollectionSubgroupType, Queue<Explosion>>
        explosions = new EnumMap<>(CollectionSubgroupType.class);
    
    private final Map<CollectionSubgroupType, Queue<SceneryObject>>
        sceneryObjects = new EnumMap<>(CollectionSubgroupType.class);
    
    private final Map<CollectionSubgroupType, Queue<EnemyMissile>>
        enemyMissiles = new EnumMap<>(CollectionSubgroupType.class);
    
    private final Map<CollectionSubgroupType, Queue<PowerUp>>
        powerUps = new EnumMap<>(CollectionSubgroupType.class);
    
    
    private static ActivePaintableEntityManager
        instance;
    
    public static ActivePaintableEntityManager getInstance()
    {
        instance = Optional.ofNullable(instance)
                           .orElseGet(ActivePaintableEntityManager::new);
        return instance;
    }
    
    private ActivePaintableEntityManager()
    {
        initializeLists();
    }
    
    private void initializeLists()
    {
        // TODO alle Listen von inaktivierten überführen in PaintableEntityRecycler, auch die BackgroundObjects berücksichtigen
        // TODO die Verwaltung der Listen für aktive in eine eigene Klasse überführen
        CollectionSubgroupType.getStandardSubgroupTypes()
                              .forEach(standardSubgroupTypes -> {
                                  enemies.put(standardSubgroupTypes, new ArrayDeque<>());
                                  missiles.put(standardSubgroupTypes, new ArrayDeque<>());
                                  explosions.put(standardSubgroupTypes, new ArrayDeque<>());
                                  sceneryObjects.put(standardSubgroupTypes, new ArrayDeque<>());
                                  enemyMissiles.put(standardSubgroupTypes, new ArrayDeque<>());
                                  powerUps.put(standardSubgroupTypes, new ArrayDeque<>());
                              });
        enemies.put(CollectionSubgroupType.DESTROYED, new ArrayDeque<>());
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<Enemy>> getEnemies()
    {
        return enemies;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<Missile>> getMissiles()
    {
        return missiles;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<Explosion>> getExplosions()
    {
        return explosions;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<SceneryObject>> getSceneryObjects()
    {
        return sceneryObjects;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<EnemyMissile>> getEnemyMissiles()
    {
        return enemyMissiles;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<PowerUp>> getPowerUps()
    {
        return powerUps;
    }
    
    public void clearExplosions()
    {
        // TODO Implementieren - vergleiche wie das bei Enemies gelöst wurde
        // gameRessourceProvider.getExplosions().get(CollectionSubgroupType.INACTIVE).addAll(gameRessourceProvider.getExplosions().get(CollectionSubgroupType.ACTIVE));
        // gameRessourceProvider.getExplosions().get(CollectionSubgroupType.ACTIVE).clear();
    }
    
    // nur Vorbereitung für späteren Umbau
    // TODO Umbau
    private final Map<PaintableEntityGroupType, Queue<GroupTypeOwner>>
        paintableEntityQueues = Arrays.stream(PaintableEntityGroupType.values())
                                      .collect(Collectors.toUnmodifiableMap(Function.identity(),
                                                                            groupType -> new ArrayDeque<>()));
    
    public void add(GroupTypeOwner groupTypeOwner)
    // TODO verwenden oder entfernen
    {
        paintableEntityQueues.get(groupTypeOwner.getGroupType())
                             .add(groupTypeOwner);
    }
    
    public void forEachOfGroupType(PaintableEntityGroupType paintableEntityGroupType, Consumer<? super GroupTypeOwner> action)
    // TODO verwenden oder entfernen
    {
        paintableEntityQueues.get(paintableEntityGroupType)
                             .forEach(action);
    }
    
    public void remove(GroupTypeOwner groupTypeOwner)
    // TODO verwenden oder entfernen
    {
        paintableEntityQueues.get(groupTypeOwner.getGroupType())
                             .remove(groupTypeOwner);
    }
    
    public void removeEachOfGroupTypeIf(GroupTypeOwner groupTypeOwner, Predicate<? super GroupTypeOwner> filter)
    // TODO verwenden oder entfernen
    {
        paintableEntityQueues.get(groupTypeOwner.getGroupType())
                             .removeIf(filter);
    }
    
    public Collection<GroupTypeOwner> getPaintableEntities(PaintableEntityGroupType groupType)
    // TODO verwenden oder entfernen
    {
        return paintableEntityQueues.get(groupType);
    }
    
    public Collection<GroupTypeOwner> getPaintableEntities(PaintableEntityGroupType groupType, Predicate<? super GroupTypeOwner> condition)
    // TODO verwenden oder entfernen
    {
        return paintableEntityQueues.get(groupType)
                                    .stream()
                                    .filter(condition)
                                    .toList();
    }
}
