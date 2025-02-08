package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class MissileUpdateController implements ManageablePaintableUpdateController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public MissileUpdateController(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    @Override
    public void updateAll()
    {
        ManageablePaintableService manageablePaintableService = gameRessourceProvider.getManageablePaintableService();
        
        manageablePaintableService.forEachActiveEntity(ManageablePaintableGroupType.MISSILE,
                                                       Missile::update);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.MISSILE,
                                                         Missile::hasStopped,
                                                         Missile::inactivate);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.MISSILE,
                                            Missile::hasStopped);
    }
}
