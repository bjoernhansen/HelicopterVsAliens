package de.helicopter_vs_aliens.gui.menu;

import de.helicopter_vs_aliens.gui.WindowType;

import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;

public class MenuManager
{
    public static WindowType
        window = START_SCREEN;	// legt das aktuelle Spiel-Menü fest; siehe interface Constants
    
    private final Map<WindowType, Menu>
        menus = new EnumMap<>(WindowType.class);
    
    public MenuManager()
    {
        WindowType.getValues().forEach(windowType -> menus.put(windowType, windowType.getMenuInstance()));
    }
    
    public void paintMenu(Graphics2D g2D)
    {
        menus.get(window).paint(g2D);
    }
}