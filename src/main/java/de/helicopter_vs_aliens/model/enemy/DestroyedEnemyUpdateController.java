package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;


public class DestroyedEnemyUpdateController implements ManageablePaintableUpdateController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public DestroyedEnemyUpdateController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    @Override
    public void updateAll()
    {
        ManageablePaintableService manageablePaintableService = gameRessourceProvider.getManageablePaintableService();
        manageablePaintableService.forEachActiveEntity(ManageablePaintableGroupType.DESTROYED_ENEMY,
                                                       Enemy::updateDead);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.DESTROYED_ENEMY,
                                                         Enemy::hasCollisionWithHelicopter,
                                                         Enemy::collision);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.DESTROYED_ENEMY,
                                                         Enemy::isMarkedForRemoval,
                                                         Enemy::clearImage);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.DESTROYED_ENEMY,
                                            Enemy::isMarkedForRemoval);
    }
}
