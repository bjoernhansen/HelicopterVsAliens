package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;


public class SceneryObjectUpdateController implements ManageablePaintableUpdateController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public SceneryObjectUpdateController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    @Override
    public void updateAll()
    {
        ManageablePaintableService manageablePaintableService = gameRessourceProvider.getManageablePaintableService();
        if(Scenery.isBackgroundMoving)
        {
            manageablePaintableService.forEachActiveEntity(ManageablePaintableGroupType.SCENERY_OBJECT,
                                                           SceneryObject::move);
        }
        manageablePaintableService.forEachActiveEntityIf(ManageablePaintableGroupType.SCENERY_OBJECT,
                                                         SceneryObject::isOutOfSight,
                                                         SceneryObject::clearImage);
        // TODO eventuell könnte noch eine postSet Methode ins Interface aufgenommen werden für das clear image,
        manageablePaintableService.removeIf(ManageablePaintableGroupType.SCENERY_OBJECT,
                                            SceneryObject::isOutOfSight);
    }
}
