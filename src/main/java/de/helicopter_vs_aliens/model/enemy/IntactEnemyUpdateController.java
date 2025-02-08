package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;


public class IntactEnemyUpdateController implements ManageablePaintableUpdateController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public IntactEnemyUpdateController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    @Override
    public void updateAll()
    {
        ManageablePaintableService manageablePaintableService = gameRessourceProvider.getManageablePaintableService();
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
