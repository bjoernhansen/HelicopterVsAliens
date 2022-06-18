package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.ActiveGameEntitiesProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO finish implementation

public final class ActiveGameEntityManager implements ActiveGameEntitiesProvider
{
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
        return null;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<Missile>> getMissiles()
    {
        return null;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<Explosion>> getExplosions()
    {
        return null;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<EnemyMissile>> getEnemyMissiles()
    {
        return null;
    }
    
    @Override
    public Map<CollectionSubgroupType, Queue<PowerUp>> getPowerUps()
    {
        return null;
    }
}
