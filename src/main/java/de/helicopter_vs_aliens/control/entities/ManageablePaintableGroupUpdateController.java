package de.helicopter_vs_aliens.control.entities;

import java.util.EnumMap;
import java.util.Map;


class ManageablePaintableGroupUpdateController
{
    private final ManageablePaintableService manageablePaintableService;
    
    private final Map<ManageablePaintableGroupType, ManageablePaintableUpdateController>
        updateController = new EnumMap<>(ManageablePaintableGroupType.class);
    
    ManageablePaintableGroupUpdateController(ManageablePaintableService manageablePaintableService) {
        this.manageablePaintableService = manageablePaintableService;
        ManageablePaintableGroupType.getValues()
                                    .forEach(this::putUpdateControllerIntoMapFor);
    }
    
    private void putUpdateControllerIntoMapFor(ManageablePaintableGroupType groupType)
    {
        var paintableUpdateController = groupType.getUpdateControllerSupplier().apply(manageablePaintableService);
        updateController.put(groupType, paintableUpdateController);
    }
    
    void updateAll(ManageablePaintableGroupType groupType)
    {
        updateController.get(groupType).updateAll();
    }
}
