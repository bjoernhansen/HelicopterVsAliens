package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class MissileUpdateController implements ManageablePaintableUpdateController
{
    private final ManageablePaintableService manageablePaintableService;
    
    public MissileUpdateController(ManageablePaintableService manageablePaintableService)
    {
        this.manageablePaintableService = manageablePaintableService;
    }
    
    @Override
    public void updateAll()
    {
        manageablePaintableService.forEachActiveEntity(ManageablePaintableGroupType.MISSILE,
                                                       Missile::update);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.MISSILE,
                                                         Missile::hasStopped,
                                                         Missile::inactivate);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.MISSILE,
                                            Missile::hasStopped);
    }
}
