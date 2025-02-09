package de.helicopter_vs_aliens.model.explosion;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class ExplosionUpdateController implements ManageablePaintableUpdateController
{
    private final ManageablePaintableService manageablePaintableService;
    
    public ExplosionUpdateController(ManageablePaintableService manageablePaintableService) {
        this.manageablePaintableService = manageablePaintableService;
    }
    
    @Override
    public void updateAll()
    {
        // TODO der PaintableEntityController könnte ggf. eine Hilfsklasse zurückgeben, die dann bereits typ spezifisch ist, so müsste nicht jedes mal wieder der Group-Type übergeben werden
        manageablePaintableService.forEachActiveEntity(ManageablePaintableGroupType.EXPLOSION,
                                                       Explosion::update);
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.EXPLOSION,
                                                         Explosion::hasExpired,
                                                         Explosion::onExplosionEnd);
        manageablePaintableService.removeIf(ManageablePaintableGroupType.EXPLOSION,
                                            Explosion::hasExpired);
    }
}
