package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.window.WindowPainter;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenSubCancelButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

import java.awt.Color;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;


public class StartScreenMenuWindowPainter extends WindowPainter
{
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Window window)
    {
        super.paint(graphicsAdapter, window);
        paintStartScreenMenu(graphicsAdapter);
    }
    
    void paintStartScreenMenu(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Color.white);
        graphicsAdapter.setFont(fontProvider.getPlain(29));
    
        graphicsAdapter.drawString(this.getHeadline(), 40, 55);
    
        GraphicalEntities.paintFrameLine(graphicsAdapter, 26, 67, 971);
        GraphicalEntities.paintFrame(graphicsAdapter, 26, 21, 971, 317);
        
        // die Buttons
        if(!showOnlyCancelButton())
        {
            StartScreenMenuButtonType.getValues()
                                     .forEach(buttonSpecifier -> Window.buttons.get(buttonSpecifier)
                                                                              .paint(graphicsAdapter));
        }
        Window.buttons.get(StartScreenSubCancelButtonType.CANCEL)
                      .paint(graphicsAdapter);
    }
    
    String getHeadline()
    {
        return Window.buttons.get(Window.page).getPrimaryLabel();
    }
    
    private static boolean showOnlyCancelButton()
    {
        return !Window.buttons.get(StartScreenMenuButtonType.BUTTON_2).isVisible();
    }
    
    void paintHelicopterInStartScreenMenu(GraphicsAdapter graphicsAdapter)
    {
        Helicopter startScreenSubHelicopter = Window.helicopterDummies.get(HelicopterType.getValues().get(Window.page.ordinal()-2));
        HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(startScreenSubHelicopter.getClass());
        helicopterPainter.startScreenSubPaint(graphicsAdapter, startScreenSubHelicopter);
    }
}
