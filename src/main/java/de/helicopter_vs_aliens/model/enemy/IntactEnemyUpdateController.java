package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class IntactEnemyUpdateController implements ManageablePaintableUpdateController
{
    private final ManageablePaintableService manageablePaintableService;
    
    public IntactEnemyUpdateController(ManageablePaintableService manageablePaintableService) {
        this.manageablePaintableService = manageablePaintableService;
    }
    
    @Override
    public void updateAll()
    {
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.INTACT_ENEMY,
                                                         Enemy::isIntactAndNotForRemoval,
                                                         Enemy::update);
        manageablePaintableService.removeEnemyToBackgroundIf(Enemy::isDestroyed);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.INTACT_ENEMY,
                                                         Enemy::shouldBeRemoved,
                                                         Enemy::clearImage);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.INTACT_ENEMY,
                                            Enemy::shouldBeRemoved);
    }
}
