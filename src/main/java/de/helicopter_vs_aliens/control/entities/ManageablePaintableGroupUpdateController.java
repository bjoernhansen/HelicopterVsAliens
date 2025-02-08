package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;

import java.util.EnumMap;
import java.util.Map;


class ManageablePaintableGroupUpdateController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    private final Map<ManageablePaintableGroupType, ManageablePaintableUpdateController>
        updateController = new EnumMap<>(ManageablePaintableGroupType.class);
    
    ManageablePaintableGroupUpdateController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
        ManageablePaintableGroupType.getValues()
                                    .forEach(this::putUpdateControllerIntoMapFor);
    }
    
    private void putUpdateControllerIntoMapFor(ManageablePaintableGroupType groupType)
    {
        var paintableUpdateController = groupType.getUpdateControllerSupplier().apply(gameRessourceProvider);
        updateController.put(groupType, paintableUpdateController);
    }
    
    void updateAll(ManageablePaintableGroupType groupType)
    {
        updateController.get(groupType).updateAll();
    }
}
