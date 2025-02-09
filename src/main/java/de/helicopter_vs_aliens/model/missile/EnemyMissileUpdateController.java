package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class EnemyMissileUpdateController implements ManageablePaintableUpdateController
{
    private final ManageablePaintableService manageablePaintableService;
    
    public EnemyMissileUpdateController(ManageablePaintableService manageablePaintableService) {
        this.manageablePaintableService = manageablePaintableService;
    }
    
    @Override
    public void updateAll()
    {
        manageablePaintableService.forEachActiveEntity(ManageablePaintableGroupType.ENEMY_MISSILE,
                                                EnemyMissile::update);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.ENEMY_MISSILE,
                                     EnemyMissile::isOutOfSight);
    }
}
