package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;


public class MissileController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public MissileController(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
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
