package de.helicopter_vs_aliens.model.powerup;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class PowerUpUpdateController implements ManageablePaintableUpdateController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public PowerUpUpdateController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    @Override
    public void updateAll()
    {
        var paintableController = gameRessourceProvider.getManageablePaintableService();
        paintableController.forEachActiveEntity(ManageablePaintableGroupType.POWER_UP, PowerUp::update);
        paintableController.removeIf(ManageablePaintableGroupType.POWER_UP, PowerUp::wasCollected);
    }
}
