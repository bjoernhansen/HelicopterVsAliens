package de.helicopter_vs_aliens.model.powerup;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class PowerUpUpdateController implements ManageablePaintableUpdateController
{
    private final ManageablePaintableService manageablePaintableService;
    
    public PowerUpUpdateController(ManageablePaintableService manageablePaintableService) {
        this.manageablePaintableService = manageablePaintableService;
    }
    
    @Override
    public void updateAll()
    {
        manageablePaintableService.forEachActiveEntity(ManageablePaintableGroupType.POWER_UP, PowerUp::update);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.POWER_UP, PowerUp::wasCollected);
    }
}
