package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;


public class EnemyMissileController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public EnemyMissileController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    public void updateAll()
    {
        var paintableController = gameRessourceProvider.getManageablePaintableService();
        paintableController.forEachActiveEntity(ManageablePaintableGroupType.ENEMY_MISSILE,
                                                EnemyMissile::update);
        paintableController.removeIf(ManageablePaintableGroupType.ENEMY_MISSILE,
                                     EnemyMissile::isOutOfSight);
    }
}
