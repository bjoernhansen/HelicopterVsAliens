package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.ActiveGameEntitiesProvider;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO finish implementation

public final class ActiveGameEntityManager implements ActiveGameEntitiesProvider
{
    // TODO Verwaltung anders lösen, vermutlich mit den erstellten Klassen im Packet control/entities
    // TODO hier auch nicht die SceneryObjects ehemals BackGroundObject vergessen
    
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
    
    
    private static ActiveGameEntityManager
        instance;
    
    
    public static ActiveGameEntityManager getInstance()
    {
        instance = Optional.ofNullable(instance)
                           .orElseGet(ActiveGameEntityManager::new);
        return instance;
    }
    
    private ActiveGameEntityManager()
    {
        initializeLists();
    }
    
    private void initializeLists()
    {
        // TODO alle Listen von inaktivierten überführen in GameEntityRecycler, auch die BackgroundObjects berücksichtigen
        // TODO die Verwaltung der Listen für aktive in eine eigene Klasse überführen
        // TODO keine LinkedList verwenden, lieber ArrayDeque
        CollectionSubgroupType.getStandardSubgroupTypes()
                              .forEach(standardSubgroupTypes -> {
            this.enemies.put(		   				standardSubgroupTypes, new ArrayDeque<>());
            this.missiles.put(	   					standardSubgroupTypes, new ArrayDeque<>());
            this.explosions.put(	   				standardSubgroupTypes, new ArrayDeque<>());
            this.sceneryObjects.put(	            standardSubgroupTypes, new ArrayDeque<>());
            this.enemyMissiles.put( 				standardSubgroupTypes, new ArrayDeque<>());
            this.powerUps.put(	   					standardSubgroupTypes, new ArrayDeque<>());
        });
        this.enemies.put(CollectionSubgroupType.DESTROYED, new ArrayDeque<>());
    }
    
    
    
    private final Map<GameEntityGroupType, Queue<GroupTypeOwner>>
        gameEntityQueues = Arrays.stream(GameEntityGroupType.values())
                                 .collect(Collectors.toUnmodifiableMap(Function.identity(), groupType -> new ArrayDeque<>()));
    
    public void add(GroupTypeOwner groupTypeOwner)
    {
        gameEntityQueues.get(groupTypeOwner.getGroupType())
                        .add(groupTypeOwner);
    }
    
    public void forEachOfGroupType(GameEntityGroupType gameEntityGroupType, Consumer<? super GroupTypeOwner> action)
    {
        gameEntityQueues.get(gameEntityGroupType)
                        .forEach(action);
    }
    
    public void remove(GroupTypeOwner groupTypeOwner)
    {
        gameEntityQueues.get(groupTypeOwner.getGroupType())
                        .remove(groupTypeOwner);
    }
    
    public void removeEachOfGroupTypeIf(GroupTypeOwner groupTypeOwner, Predicate<? super GroupTypeOwner> filter)
    {
        gameEntityQueues.get(groupTypeOwner.getGroupType())
                        .removeIf(filter);
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
}
