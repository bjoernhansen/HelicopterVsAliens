package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableService;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableUpdateController;


public class SceneryObjectUpdateController implements ManageablePaintableUpdateController
{
    private final ManageablePaintableService manageablePaintableService;
    
    public SceneryObjectUpdateController(ManageablePaintableService manageablePaintableService) {
        this.manageablePaintableService = manageablePaintableService;
    }
    
    @Override
    public void updateAll()
    {
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
