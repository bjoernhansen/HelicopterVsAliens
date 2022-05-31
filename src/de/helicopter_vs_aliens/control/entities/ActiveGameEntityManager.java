package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ActiveGameEntityProvider;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ActiveGameEntityManager implements ActiveGameEntityProvider
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
    

    
    @Override
    public Map<CollectionSubgroupType, LinkedList<Enemy>> getEnemies()
    {
        return null;
    }
    
    @Override
    public Map<CollectionSubgroupType, LinkedList<Missile>> getMissiles()
    {
        return null;
    }
    
    @Override
    public Map<CollectionSubgroupType, LinkedList<Explosion>> getExplosions()
    {
        return null;
    }
    
    @Override
    public Map<CollectionSubgroupType, LinkedList<EnemyMissile>> getEnemyMissiles()
    {
        return null;
    }
    
    @Override
    public Map<CollectionSubgroupType, LinkedList<PowerUp>> getPowerUps()
    {
        return null;
    }
}
