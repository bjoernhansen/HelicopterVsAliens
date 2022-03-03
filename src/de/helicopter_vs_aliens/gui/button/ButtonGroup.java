package de.helicopter_vs_aliens.gui.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ButtonGroup
{
    COMPLETE(   StartScreenButtonType.getValues(),
                StartScreenSubButtonType.getValues(),
                StartScreenSubCancelButtonType.getValues(),
                MainMenuButtonType.getValues(),
                GroundButtonType.getValues(),
                LeftSideRepairShopButtonType.getValues(),
                StandardUpgradeButtonType.getValues(),
                SpecialUpgradeButtonType.getValues()),
    
    START_SCREEN(  StartScreenButtonType.getValues()),
    
    START_SCREEN_MENU(  StartScreenSubButtonType.getValues(),
                        StartScreenSubCancelButtonType.getValues()),
    
    IN_GAME( MainMenuButtonType.getValues(),
             GroundButtonType.getValues()),
    
    REPAIR_SHOP( LeftSideRepairShopButtonType.getValues(),
                 StandardUpgradeButtonType.getValues(),
                 SpecialUpgradeButtonType.getValues());
    
    
    private final List<ButtonSpecifier> buttonSpecifiers;
    
    
    ButtonGroup(List<ButtonSpecifier> ... specifierLists)
    {
        List<ButtonSpecifier> tempList = new ArrayList<>();
        Arrays.asList(specifierLists).forEach(tempList::addAll);
        buttonSpecifiers = Collections.unmodifiableList(tempList);
    }
    
    public List<ButtonSpecifier> getButtonSpecifiers()
    {
        return buttonSpecifiers;
    }
}
