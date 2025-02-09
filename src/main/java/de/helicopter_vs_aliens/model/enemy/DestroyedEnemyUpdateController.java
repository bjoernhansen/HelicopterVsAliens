package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class DestroyedEnemyUpdateController implements ManageablePaintableUpdateController
{
    private final ManageablePaintableService manageablePaintableService;
    
    public DestroyedEnemyUpdateController(ManageablePaintableService manageablePaintableService) {
        this.manageablePaintableService = manageablePaintableService;
    }
    
    @Override
    public void updateAll()
    {
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
