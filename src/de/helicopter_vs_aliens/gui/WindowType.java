package de.helicopter_vs_aliens.gui;

import de.helicopter_vs_aliens.gui.menu.GameMenu;
import de.helicopter_vs_aliens.gui.menu.Menu;
import de.helicopter_vs_aliens.gui.menu.RepairShopMenu;
import de.helicopter_vs_aliens.gui.menu.ScoreScreenMenu;
import de.helicopter_vs_aliens.gui.menu.StartScreenMenu;
import de.helicopter_vs_aliens.gui.menu.StartScreenSubMenu;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum WindowType
{
    INFORMATION(StartScreenSubMenu::new, "information."),
    DESCRIPTION(StartScreenSubMenu::new, "description."),
    SETTINGS(StartScreenSubMenu::new, "settings."),
    CONTACT(StartScreenSubMenu::new, "contact."),
    HELICOPTER_TYPES(StartScreenSubMenu::new, "helicopterTypes."),
    HIGH_SCORE(StartScreenSubMenu::new, "highScore."),
    GAME(GameMenu::new),
    REPAIR_SHOP(RepairShopMenu::new),
    START_SCREEN(StartScreenMenu::new),
    SCORE_SCREEN(ScoreScreenMenu::new);
    
    private static final List<WindowType>
        VALUES = List.of(values());
    
    public static final String
        START_OF_START_SCREEN_MENU_BUTTON_LABEL_PREFIX = "buttonLabel.startScreenMenu.";
    
    private final static Set<WindowType>
        MAIN_MENU_WINDOWS = Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(GAME, REPAIR_SHOP, SCORE_SCREEN))),
        NON_SETTINGS_START_SCREEN_MENU_WINDOWS = Collections.unmodifiableSet(EnumSet.of(INFORMATION, DESCRIPTION, CONTACT, HELICOPTER_TYPES, HIGH_SCORE));

    
    private String
        buttonLabelKeyPrefix;
    
    private final Supplier<? extends Menu>
        instance;
    
    
    WindowType(Supplier<? extends Menu> instance)
    {
        this.instance = instance;
    }
    
    WindowType(Supplier<? extends Menu> instance, String endOfButtonLabelPrefix)
    {
        this.instance = instance;
        this.buttonLabelKeyPrefix = START_OF_START_SCREEN_MENU_BUTTON_LABEL_PREFIX + endOfButtonLabelPrefix;
    }
    
    public static List<WindowType> getValues()
    {
        return VALUES;
    }
    
    public static Set<WindowType> getNonSettingsStartScreenMenuWindows()
    {
        return NON_SETTINGS_START_SCREEN_MENU_WINDOWS;
    }
    
    public boolean isMainMenuWindow()
    {
        return MAIN_MENU_WINDOWS.contains(this);
    }
    
    public String getButtonLabelKeyPrefix(){
        return buttonLabelKeyPrefix;
    }
    
    public Menu makeMenuInstance()
    {
        return instance.get();
    }
}
