package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.PowerUpPainter;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;

import java.awt.Point;


public class DescriptionWindowPainter extends StartScreenMenuWindowPainter
{
    private static final int
        Y_OFFSET = 35;
    
    private static final Point
        POSITION = new Point(52, 120);
    
    @Override
    void paintStartScreenMenu(GraphicsAdapter graphicsAdapter)
    {
        super.paintStartScreenMenu(graphicsAdapter);
    
        if(Window.page == StartScreenMenuButtonType.BUTTON_6)
        {
            PowerUpPainter powerUpPainter = GraphicsManager.getInstance()
                                                           .getPainter(PowerUp.class);
            for (PowerUpType powerUpType : PowerUpType.getValues())
            {
                powerUpPainter.paint(
                    graphicsAdapter,
                    POSITION.x,
                    POSITION.y + powerUpType.getMenuPosition() * Y_OFFSET,
                    Window.POWER_UP_SIZE,
                    Window.POWER_UP_SIZE,
                    powerUpType.getSurfaceColor(),
                    powerUpType.getCrossColor());
            }
        }
    }
}
