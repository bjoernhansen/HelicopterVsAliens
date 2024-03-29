package de.helicopter_vs_aliens.gui;

import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.window.GameWindowPainter;
import de.helicopter_vs_aliens.graphics.painter.window.RepairShopWindowPainter;
import de.helicopter_vs_aliens.graphics.painter.window.ScoreScreenWindowPainter;
import de.helicopter_vs_aliens.graphics.painter.window.StartScreenWindowPainter;
import de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu.DescriptionWindowPainter;
import de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu.HelicopterTypesWindowPainter;
import de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu.HighScoreWindowPainter;
import de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu.SettingsWindowPainter;
import de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu.StartScreenMenuWindowPainter;
import de.helicopter_vs_aliens.gui.window.ContactWindow;
import de.helicopter_vs_aliens.gui.window.DescriptionWindow;
import de.helicopter_vs_aliens.gui.window.GameWindow;
import de.helicopter_vs_aliens.gui.window.HelicopterTypesWindow;
import de.helicopter_vs_aliens.gui.window.HighScoreWindow;
import de.helicopter_vs_aliens.gui.window.InformationWindow;
import de.helicopter_vs_aliens.gui.window.RepairShopWindow;
import de.helicopter_vs_aliens.gui.window.ScoreScreenWindow;
import de.helicopter_vs_aliens.gui.window.SettingsWindow;
import de.helicopter_vs_aliens.gui.window.StartScreenWindow;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.Paintable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum WindowType
{
    INFORMATION(InformationWindow.class,
                InformationWindow::new,
                StartScreenMenuWindowPainter::new,
                "information."),
    
    DESCRIPTION(DescriptionWindow.class,
                DescriptionWindow::new,
                DescriptionWindowPainter::new,
                "description."),
    
    SETTINGS(SettingsWindow.class,
             SettingsWindow::new,
             SettingsWindowPainter::new,
             "settings."),
    
    CONTACT(ContactWindow.class,
            ContactWindow::new,
            StartScreenMenuWindowPainter::new,
            "contact."),
    
    HELICOPTER_TYPES(HelicopterTypesWindow.class,
                     HelicopterTypesWindow::new,
                     HelicopterTypesWindowPainter::new,
                     "helicopterTypes."),
    
    HIGH_SCORE(HighScoreWindow.class,
               HighScoreWindow::new,
               HighScoreWindowPainter::new,
               "highScore."),
    
    GAME(GameWindow.class,
         GameWindow::new,
         GameWindowPainter::new,
        ""),
    
    REPAIR_SHOP(RepairShopWindow.class,
                RepairShopWindow::new,
                RepairShopWindowPainter::new,
                ""),
    
    START_SCREEN(StartScreenWindow.class,
                 StartScreenWindow::new,
                 StartScreenWindowPainter::new,
                 ""),
    
    SCORE_SCREEN(ScoreScreenWindow.class,
                 ScoreScreenWindow::new,
                 ScoreScreenWindowPainter::new,
                 "");
    
    private static final List<WindowType>
        VALUES = List.of(values());
    
    public static final String
        START_OF_START_SCREEN_MENU_BUTTON_LABEL_KEY_PREFIX = "buttonLabel.startScreenSub.";

    public static final String
        START_OF_START_SCREEN_MENU_TEXT_KEY_PREFIX = "menuText.startScreenSub.";


    private static final Set<WindowType>
        MAIN_WINDOWS = Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(GAME, REPAIR_SHOP, SCORE_SCREEN)));
    
    private static final Set<WindowType>
        NON_SETTINGS_START_SCREEN_MENU_WINDOWS = Collections.unmodifiableSet(EnumSet.of(INFORMATION, DESCRIPTION, CONTACT, HELICOPTER_TYPES, HIGH_SCORE));

    
    private final String
        buttonLabelKeyPrefix;

    private final String
        startScreenMenuTextKeyPrefix;
    
    private final Supplier<? extends Window>
        menuInstance;
    
    private final Supplier<? extends Painter<? extends Paintable>>
        painterInstance;
       
    private final Class<? extends Window>
        menuClass;


    WindowType(Class<? extends Window> menuClass,
               Supplier<? extends Window> menuInstance,
               Supplier<? extends Painter<? extends Paintable>> painterInstance,
               String endOfKeyPrefix)
    {
        this.menuClass = menuClass;
        this.menuInstance = menuInstance;
        this.painterInstance = painterInstance;
        this.buttonLabelKeyPrefix = START_OF_START_SCREEN_MENU_BUTTON_LABEL_KEY_PREFIX + endOfKeyPrefix;
        this.startScreenMenuTextKeyPrefix = START_OF_START_SCREEN_MENU_TEXT_KEY_PREFIX + endOfKeyPrefix;
    }
    
    public static List<WindowType> getValues()
    {
        return VALUES;
    }
    
    public static Set<WindowType> getNonSettingsStartScreenSubWindows()
    {
        return NON_SETTINGS_START_SCREEN_MENU_WINDOWS;
    }
    
    public boolean isMainWindow()
    {
        return MAIN_WINDOWS.contains(this);
    }
    
    public String getButtonLabelKeyPrefix(){
        return buttonLabelKeyPrefix;
    }

    public String getStartScreenMenuTextKeyPrefix(){
        return startScreenMenuTextKeyPrefix;
    }
    
    public Window getMenuInstance()
    {
        return menuInstance.get();
    }
    
    // TODO eingeschränkter Wildcard-Typ als Rückgabewert sollte immer vermieden werden (siehe Effective Java)
    public Painter<? extends Paintable> makePainterInstance()
    {
        return painterInstance.get();
    }
    
    // TODO eingeschränkter Wildcard-Typ als Rückgabewert sollte immer vermieden werden (siehe Effective Java)
    public Class<? extends Window> getMenuClass()
    {
        return menuClass;
    }
}
