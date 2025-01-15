package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
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
import java.util.function.Predicate;

// TODO finish implementation

public final class ActiveManageablePaintableController implements ActiveManageablePaintableProvider
{
    // TODO Verwaltung anders lösen, eventuell wie in der Klasse PaintableEntitySupplier
    // TODO ggf. ist auch eine Zusammenführung oder eine Verwaltung über eine übergeordnete Klasse denkbar
    // TODO sobald die inaktiven hier nicht mehr nötig sind, kann der Umbau beginnen
    // TODO die ungenutzten Methoden in dieser Klasse kommen dann ggf. zum Einsatz
    // TODO hier auch nicht die SceneryObjects ehemals BackGroundObject vergessen
    // TODO das lässt sich vielleicht auch über eine Map abbilden , eine EnumMap (PaintableEntityGroupType) von EnumMaps (Collection Subgroup)
    
    private final GameRessourceProvider
        gameRessourceProvider;
    
    // TODO ggf. ist es möglich nur noch einen Queue-Typ Queue<ManageablePaintable> zu haben
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
    
    
    private final Map<ManageablePaintableGroupType, Queue<? extends ManageablePaintable>>
        paintableQueues = new EnumMap<>(ManageablePaintableGroupType.class);
    
    
    public ActiveManageablePaintableController(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
        paintableQueues.put(ManageablePaintableGroupType.INTACT_ENEMY, intactEnemies);
        paintableQueues.put(ManageablePaintableGroupType.DESTROYED_ENEMY, destroyedEnemies);
        paintableQueues.put(ManageablePaintableGroupType.MISSILE, missiles);
        paintableQueues.put(ManageablePaintableGroupType.EXPLOSION, explosions);
        paintableQueues.put(ManageablePaintableGroupType.SCENERY_OBJECT, sceneryObjects);
        paintableQueues.put(ManageablePaintableGroupType.ENEMY_MISSILE, enemyMissiles);
        paintableQueues.put(ManageablePaintableGroupType.POWER_UP, powerUps);
    }
    
    // TODO eventuell ist es möglich all diese Methoden zusammenzuführen durch eine Methode getManageablePaintable(ManageablePaintableGroupType groupType)
    
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
    
    public Queue<ManageablePaintable> getPaintableEntities(ManageablePaintableGroupType groupType,
                                                          Predicate<? super ManageablePaintable> condition)
    // TODO verwenden oder entfernen
    {
        return paintableQueues.get(groupType)
                              .stream()
                              .filter(condition)
                              .collect(Collectors.toCollection(ArrayDeque::new));
    }*/
    
    public void clearActiveEntities(ManageablePaintableGroupType groupType)
    {
        Queue<? extends ManageablePaintable> groupTypeOwners = paintableQueues.get(groupType);
        gameRessourceProvider.storeAllManageablePaintableInstances(groupTypeOwners);
        groupTypeOwners.clear();
    }
    
    public int numberOfActiveEntities(ManageablePaintableGroupType groupType)
    {
        return paintableQueues.get(groupType)
                              .size();
    }
    
    public void forEachActiveEntity(ManageablePaintableGroupType groupType, Consumer<? super ManageablePaintable> action)
    {
        paintableQueues.get(groupType)
                       .forEach(action);
    }
    
    public void removeIf(ManageablePaintableGroupType manageablePaintableGroupType,
                         Predicate<? super ManageablePaintable> removeCondition)
    {
        Queue<? extends ManageablePaintable> manageablePaintables = paintableQueues.get(manageablePaintableGroupType);
        manageablePaintables.stream()
                            .filter(removeCondition)
                            .forEach(gameRessourceProvider::storeManageablePaintable);
        manageablePaintables.removeIf(removeCondition);
    }
    
    public <T extends ManageablePaintable> T activatePaintableEntity(ManageablePaintableFactory<T> factory)
    {
        T manageablePaintable = gameRessourceProvider.getNewManageablePaintableInstance(factory);
        switch(manageablePaintable.getGroupType())
        {
            case INTACT_ENEMY -> intactEnemies.add((Enemy)manageablePaintable);
            case DESTROYED_ENEMY -> destroyedEnemies.add((Enemy)manageablePaintable);
            case MISSILE -> missiles.add((Missile)manageablePaintable);
            case EXPLOSION -> explosions.add((Explosion)manageablePaintable);
            case SCENERY_OBJECT -> sceneryObjects.add((SceneryObject)manageablePaintable);
            case ENEMY_MISSILE -> enemyMissiles.add((EnemyMissile)manageablePaintable);
            case POWER_UP -> powerUps.add((PowerUp)manageablePaintable);
        }
        return manageablePaintable;
    }
}
