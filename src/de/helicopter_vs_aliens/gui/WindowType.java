package de.helicopter_vs_aliens.gui;

import de.helicopter_vs_aliens.model.enemy.EnemyType;

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
        START_SCREEN_MENU_WINDOWS = Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(STARTSCREEN, GAME, REPAIR_SHOP, SCORESCREEN)));
    
    public boolean isMainMenuWindow()
    {
        return MAIN_MENU_WINDOWS.contains(this);
    }
    
    public static Set<WindowType> getStartScreenMenuWindows(){
        return START_SCREEN_MENU_WINDOWS;
    }
}
