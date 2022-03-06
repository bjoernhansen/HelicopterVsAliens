package de.helicopter_vs_aliens.gui.window;

import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.gui.WindowType;

import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;

public class WindowManager
{
    public static WindowType
        window = START_SCREEN;	// legt das aktuelle Spiel-Menü fest; siehe interface Constants
    
    private final Map<WindowType, Window>
        windows = new EnumMap<>(WindowType.class);
    
    public WindowManager()
    {
        WindowType.getValues().forEach(windowType -> windows.put(windowType, windowType.getMenuInstance()));
    }
    
    public void paintWindow(Graphics2D g2D, Graphics2DAdapter graphics2DAdapter)
    {
        windows.get(window).paint(g2D, graphics2DAdapter);
    }
    
    
}