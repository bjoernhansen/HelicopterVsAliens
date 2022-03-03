package de.helicopter_vs_aliens.gui;

import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.menu.GameMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.RepairShopMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.ScoreScreenMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.StartScreenMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.start_screen_sub.ContactMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.start_screen_sub.DescriptionMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.start_screen_sub.HelicopterTypesMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.start_screen_sub.HighScoreMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.start_screen_sub.InformationMenuPainter;
import de.helicopter_vs_aliens.graphics.painter.menu.start_screen_sub.SettingsMenuPainter;
import de.helicopter_vs_aliens.gui.menu.ContactMenu;
import de.helicopter_vs_aliens.gui.menu.DescriptionMenu;
import de.helicopter_vs_aliens.gui.menu.GameMenu;
import de.helicopter_vs_aliens.gui.menu.HelicopterTypesMenu;
import de.helicopter_vs_aliens.gui.menu.HighScoreMenu;
import de.helicopter_vs_aliens.gui.menu.InformationMenu;
import de.helicopter_vs_aliens.gui.menu.Menu;
import de.helicopter_vs_aliens.gui.menu.RepairShopMenu;
import de.helicopter_vs_aliens.gui.menu.ScoreScreenMenu;
import de.helicopter_vs_aliens.gui.menu.SettingsMenu;
import de.helicopter_vs_aliens.gui.menu.StartScreenMenu;
import de.helicopter_vs_aliens.model.Paintable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum WindowType
{
    INFORMATION(InformationMenu.class,
                InformationMenu::new,
                InformationMenuPainter::new,
                "information."),
    
    DESCRIPTION(DescriptionMenu.class,
                DescriptionMenu::new,
                DescriptionMenuPainter::new,
                "description."),
    
    SETTINGS(SettingsMenu.class,
             SettingsMenu::new,
             SettingsMenuPainter::new,
             "settings."),
    
    CONTACT(ContactMenu.class,
            ContactMenu::new,
            ContactMenuPainter::new,
            "contact."),
    
    HELICOPTER_TYPES(HelicopterTypesMenu.class,
                     HelicopterTypesMenu::new,
                     HelicopterTypesMenuPainter::new,
                     "helicopterTypes."),
    
    HIGH_SCORE(HighScoreMenu.class,
               HighScoreMenu::new,
               HighScoreMenuPainter::new,
               "highScore."),
    
    GAME(GameMenu.class,
         GameMenu::new,
         GameMenuPainter::new,
        ""),
    
    REPAIR_SHOP(RepairShopMenu.class,
                RepairShopMenu::new,
                RepairShopMenuPainter::new,
                ""),
    
    START_SCREEN(StartScreenMenu.class,
                 StartScreenMenu::new,
                 StartScreenMenuPainter::new,
                 ""),
    
    SCORE_SCREEN(ScoreScreenMenu.class,
                 ScoreScreenMenu::new,
                 ScoreScreenMenuPainter::new,
                 "");
    
    private static final List<WindowType>
        VALUES = List.of(values());
    
    public static final String
        START_OF_START_SCREEN_MENU_BUTTON_LABEL_PREFIX = "buttonLabel.startScreenSub.";
    
    private final static Set<WindowType>
        MAIN_MENU_WINDOWS = Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(GAME, REPAIR_SHOP, SCORE_SCREEN))),
        NON_SETTINGS_START_SCREEN_MENU_WINDOWS = Collections.unmodifiableSet(EnumSet.of(INFORMATION, DESCRIPTION, CONTACT, HELICOPTER_TYPES, HIGH_SCORE));

    
    private final String
        buttonLabelKeyPrefix;
    
    private final Supplier<? extends Menu>
        menuInstance;
    
    private final Supplier<? extends Painter<? extends Paintable>>
        painterInstance;
       
    private final Class<? extends Menu>
        menuClass;
    
    WindowType(Class<? extends Menu> menuClass,
               Supplier<? extends Menu> menuInstance,
               Supplier<? extends Painter<? extends Paintable>> painterInstance,
               String endOfButtonLabelPrefix)
    {

        this.menuClass = menuClass;
        this.menuInstance = menuInstance;
        this.painterInstance = painterInstance;
        this.buttonLabelKeyPrefix = START_OF_START_SCREEN_MENU_BUTTON_LABEL_PREFIX + endOfButtonLabelPrefix;
    }
    
    public static List<WindowType> getValues()
    {
        return VALUES;
    }
    
    public static Set<WindowType> getNonSettingsStartScreenSubWindows()
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
    
    public Menu getMenuInstance()
    {
        return menuInstance.get();
    }
    
    public Painter<? extends Paintable> makePainterInstance()
    {
        return painterInstance.get();
    }
    
    public Class<? extends Menu> getMenuClass()
    {
        return menuClass;
    }
}
