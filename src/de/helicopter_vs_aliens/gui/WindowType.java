package de.helicopter_vs_aliens.gui;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum WindowType
{
    INFORMATIONS,
    DESCRIPTION,
    SETTINGS,
    CONTACT,
    HELICOPTER_TYPES,
    HIGHSCORE,
    GAME,
    REPAIR_SHOP,
    STARTSCREEN,
    SCORESCREEN;


    private final static Set<WindowType>
        MAIN_MENU_WINDOWS = Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(GAME, REPAIR_SHOP, SCORESCREEN))),
        NON_SETTINGS_START_SCREEN_MENU_WINDOWS = Collections.unmodifiableSet(EnumSet.of(INFORMATIONS, DESCRIPTION, CONTACT, HELICOPTER_TYPES, HIGHSCORE));
    
    String buttonLabelKeyPrefix;
    
    
    WindowType()
    {
        this.buttonLabelKeyPrefix = "buttonLabel.startScreenMenu." + this.name().toLowerCase() + ".";
    }
    
    public static Set<WindowType> getNonSettingsStartScreenMenuWindows(){
        return NON_SETTINGS_START_SCREEN_MENU_WINDOWS;
    }
    
    public boolean isMainMenuWindow()
    {
        return MAIN_MENU_WINDOWS.contains(this);
    }
    
    public boolean isNonSettingsStartScreenMenuWindow()
    {
        return NON_SETTINGS_START_SCREEN_MENU_WINDOWS.contains(this);
    }
    
    public String getButtonLabelKeyPrefix(){
        return buttonLabelKeyPrefix;
    }
}
