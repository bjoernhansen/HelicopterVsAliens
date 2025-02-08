package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class EnemyMissileUpdateController implements ManageablePaintableUpdateController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public EnemyMissileUpdateController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    @Override
    public void updateAll()
    {
        var paintableController = gameRessourceProvider.getManageablePaintableService();
        paintableController.forEachActiveEntity(ManageablePaintableGroupType.ENEMY_MISSILE,
                                                EnemyMissile::update);
        paintableController.removeIf(ManageablePaintableGroupType.ENEMY_MISSILE,
                                     EnemyMissile::isOutOfSight);
    }
}
