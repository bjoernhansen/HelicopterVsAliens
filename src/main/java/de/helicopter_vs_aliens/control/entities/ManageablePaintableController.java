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
    
    private final GameRessourceProvider
        gameRessourceProvider;
    
    private final ManageablePaintableSupplier
        manageablePaintableSupplier;
    
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
    
    
    public ManageablePaintableController(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
        DependencyInjector dependencyInjector = DependencyInjector.instanceFor(gameRessourceProvider);
        manageablePaintableSupplier = new ManageablePaintableSupplier(dependencyInjector);
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
    
    public void forEachActiveEntity(ManageablePaintableGroupType groupType, Consumer<? super ManageablePaintable> action)
    {
        paintableQueues.get(groupType)
                       .forEach(action);
    }
    
    public void forEachActiveEntityIf(ManageablePaintableGroupType groupType,
                                      Predicate<? super ManageablePaintable> applyCondition,
                                      Consumer<? super ManageablePaintable> action)
    {
        paintableQueues.get(groupType)
                       .stream()
                       .filter(applyCondition)
                       .forEach(action);
    }
    
    public void removeIf(ManageablePaintableGroupType groupType,
                         Predicate<? super ManageablePaintable> removeCondition)
    {
        Queue<? extends ManageablePaintable> manageablePaintables = paintableQueues.get(groupType);
        manageablePaintables.stream()
                            .filter(removeCondition)
                            .forEach(gameRessourceProvider.getManageablePaintableController()::store);
        manageablePaintables.removeIf(removeCondition);
    }
    
    public void removeEnemyToBackgroundIf(Predicate<? super ManageablePaintable> removeCondition)
    {
        Queue<? extends ManageablePaintable> intactEnemies = paintableQueues.get(ManageablePaintableGroupType.INTACT_ENEMY);
        intactEnemies.stream()
                     .filter(removeCondition)
                     .forEach(enemy -> destroyedEnemies.add((Enemy)enemy));
        intactEnemies.removeIf(removeCondition);
    }
    
    
    public <T extends ManageablePaintable> T activateEntity(ManageablePaintableFactory<T> factory)
    {
        T manageablePaintable = manageablePaintableSupplier.retrieve(factory);
        switch(manageablePaintable.getGroupType())
        {
            case INTACT_ENEMY, DESTROYED_ENEMY -> intactEnemies.add((Enemy)manageablePaintable);
            case MISSILE -> missiles.add((Missile)manageablePaintable);
            case EXPLOSION -> explosions.add((Explosion)manageablePaintable);
            case SCENERY_OBJECT -> sceneryObjects.add((SceneryObject)manageablePaintable);
            case ENEMY_MISSILE -> enemyMissiles.add((EnemyMissile)manageablePaintable);
            case POWER_UP -> powerUps.add((PowerUp)manageablePaintable);
        }
        return manageablePaintable;
    }
  
    // TODO sollte zukünftig private sein und nur noch in dieser Klasse verwendet werden. --> wird aufgerufen, wenn in dieser Klasse eine Entity aus den aktiven verschwindet
    public void store(ManageablePaintable manageablePaintable)
    {
        manageablePaintableSupplier.store(manageablePaintable);
    }
    
    public boolean isMajorBossActive()
    {
        return paintableQueues.get(ManageablePaintableGroupType.INTACT_ENEMY)
                              .stream()
                              .findFirst()
                              .filter(e -> ((Enemy)e).getType()
                                                     .isMajorBoss())
                              .isPresent();
    }
}
