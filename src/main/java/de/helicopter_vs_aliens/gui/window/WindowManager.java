package de.helicopter_vs_aliens.gui.window;

import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;

import java.util.EnumMap;
import java.util.Map;

public class WindowManager
{
    public static WindowType
        window = WindowType.START_SCREEN;	// legt das aktuelle Spiel-Menü fest; siehe interface Constants
    
    private final Map<WindowType, Window>
        windows = new EnumMap<>(WindowType.class);
    
    public WindowManager()
    {
        WindowType.getValues().forEach(windowType -> windows.put(windowType, windowType.getMenuInstance()));
    }
    
    public void paintWindow(GraphicsAdapter graphicsAdapter)
    {
        windows.get(window).paint(graphicsAdapter);
    }
}