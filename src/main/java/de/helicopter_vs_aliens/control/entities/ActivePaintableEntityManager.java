package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.PaintableEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;

import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

// TODO finish implementation

public final class ActivePaintableEntityManager implements ActivePaintableEntityProvider
{
    // TODO Verwaltung anders lösen, eventuell wie in der Klasse PaintableEntitySupplier
    // ggf. ist auch eine Zusammenführung oder eine Verwaltung über eine übergeordnete Klasse denkbar
    // TODO sobald die inaktiven hier nicht mehr nötig sind, kann der Umbau beginnen
    // TODO die ungenutzten Methoden in dieser Klasse kommen dann ggf. zum Einsatz
    // TODO hier auch nicht die SceneryObjects ehemals BackGroundObject vergessen
    // TODO das lässt sich vielleicht auch über eine Map abbilden , eine EnumMap (PaintableEntityGroupType) von EnumMaps (Collection Subgroup)
    
    private final GameRessourceProvider
        gameRessourceProvider;
    
    private final Queue<Enemy>
        intactEnemies = new ArrayDeque<>();
    
    private final Queue<Enemy>
        destroyedEnemies = new ArrayDeque<>();
    
    private final Queue<Missile>
        missiles = new ArrayDeque<>();
    
    private final Queue<Explosion>
        explosions = new ArrayDeque<>();
    
    private final Queue<SceneryObject>
        sceneryObjects = new ArrayDeque<>();
    
    private final Queue<EnemyMissile>
        enemyMissiles = new ArrayDeque<>();
    
    private final Queue<PowerUp>
        powerUps = new ArrayDeque<>();
    
    
    private final Map<PaintableEntityGroupType, Queue<? extends PaintableEntity>>
        paintableEntityQueues = new EnumMap<>(PaintableEntityGroupType.class);
    
    
    public ActivePaintableEntityManager(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
        paintableEntityQueues.put(PaintableEntityGroupType.INTACT_ENEMY, intactEnemies);
        paintableEntityQueues.put(PaintableEntityGroupType.DESTROYED_ENEMY, destroyedEnemies);
        paintableEntityQueues.put(PaintableEntityGroupType.MISSILE, missiles);
        paintableEntityQueues.put(PaintableEntityGroupType.EXPLOSION, explosions);
        paintableEntityQueues.put(PaintableEntityGroupType.SCENERY_OBJECT, sceneryObjects);
        paintableEntityQueues.put(PaintableEntityGroupType.ENEMY_MISSILE, enemyMissiles);
        paintableEntityQueues.put(PaintableEntityGroupType.POWER_UP, powerUps);
    }
    
    @Override
    public Queue<Enemy> getIntactEnemies()
    {
        return intactEnemies;
    }
    
    @Override
    public Queue<Enemy> getDestroyedEnemies()
    {
        return destroyedEnemies;
    }
    
    @Override
    public Queue<Missile> getMissiles()
    {
        return missiles;
    }
    
    @Override
    public Queue<Explosion> getExplosions()
    {
        return explosions;
    }
    
    @Override
    public Queue<SceneryObject> getSceneryObjects()
    {
        return sceneryObjects;
    }
    
    @Override
    public Queue<EnemyMissile> getEnemyMissiles()
    {
        return enemyMissiles;
    }
    
    @Override
    public Queue<PowerUp> getPowerUps()
    {
        return powerUps;
    }
    
    // nur Vorbereitung für späteren Umbau
    // TODO Umbau
  /*  private final Map<PaintableEntityGroupType, Queue<GroupTypeOwner>>
        paintableEntityQueues = Arrays.stream(PaintableEntityGroupType.values())
                                      .collect(Collectors.toUnmodifiableMap(Function.identity(),
                                                                            groupType -> new ArrayDeque<>()));

    public void add(GroupTypeOwner groupTypeOwner)
    {
        paintableEntityQueues.get(groupTypeOwner.getGroupType())
                             .add(groupTypeOwner);
    }
    
    public void forEachOfGroupType(PaintableEntityGroupType paintableEntityGroupType,
                                   Consumer<? super GroupTypeOwner> action)
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
    
    public Collection<GroupTypeOwner> getPaintableEntities(PaintableEntityGroupType groupType,
                                                           Predicate<? super GroupTypeOwner> condition)
    // TODO verwenden oder entfernen
    {
        return paintableEntityQueues.get(groupType)
                                    .stream()
                                    .filter(condition)
                                    .toList();
    }*/
    
    public void clearActiveEntities(PaintableEntityGroupType groupType)
    {
        Queue<? extends PaintableEntity> groupTypeOwners = paintableEntityQueues.get(groupType);
        gameRessourceProvider.storeAllPaintableEntities(groupTypeOwners);
        groupTypeOwners.clear();
    }
    
    public int numberOfActiveEntities(PaintableEntityGroupType groupType)
    {
        return paintableEntityQueues.get(groupType).size();
    }
    
    public void forEachActiveEntity(PaintableEntityGroupType groupType, Consumer<PaintableEntity> action)
    {
        paintableEntityQueues.get(groupType).forEach(action);
    }
/*
    public <T extends PaintableEntity> T activatePaintable(PaintableEntityFactory<T> factory)
    {
        T newPaintableEntityInstance = gameRessourceProvider.getNewPaintableEntityInstance(factory);
        paintableEntityQueues.get(newPaintableEntityInstance.)
    
        getGameRessourceProvider().getActivePaintableEntityManager()
                                  .getEnemyMissiles()
                                  .add(enemyMissile);
        
        
        return newPaintableEntityInstance;
    }*/
}
