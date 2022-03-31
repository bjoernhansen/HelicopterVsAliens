package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.graphics.painter.window.WindowPainter;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenSubCancelButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

import java.awt.Color;
import java.awt.Graphics2D;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;


public class StartScreenMenuWindowPainter extends WindowPainter
{
    @Override
    public void paint(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter, Window window)
    {
        super.paint(g2d, graphics2DAdapter, window);
        paintStartScreenMenu(g2d, graphics2DAdapter);
    }
    
    void paintStartScreenMenu(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter)
    {
        g2d.setColor(Color.white);
        g2d.setFont(fontProvider.getPlain(29));
    
        g2d.drawString(this.getHeadline(), 40, 55);
    
        GraphicalEntities.paintFrameLine(g2d, 26, 67, 971);
        GraphicalEntities.paintFrame(g2d, 26, 21, 971, 317);
        
        // die Buttons
        if(!showOnlyCancelButton())
        {
            StartScreenMenuButtonType.getValues()
                                     .forEach(buttonSpecifier -> Window.buttons.get(buttonSpecifier)
                                                                              .paint(g2d, graphics2DAdapter));
        }
        Window.buttons.get(StartScreenSubCancelButtonType.CANCEL)
                      .paint(g2d, graphics2DAdapter);
    }
    
    String getHeadline()
    {
        return Window.buttons.get(Window.page).getPrimaryLabel();
    }
    
    private static boolean showOnlyCancelButton()
    {
        return !Window.buttons.get(StartScreenMenuButtonType.BUTTON_2).isVisible();
    }
    
    void paintHelicopterInStartScreenMenu(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter)
    {
        Helicopter startScreenSubHelicopter = Window.helicopterDummies.get(HelicopterType.getValues().get(Window.page.ordinal()-2));
        HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(startScreenSubHelicopter.getClass());
        helicopterPainter.startScreenSubPaint(g2d, graphics2DAdapter, startScreenSubHelicopter);
    }
}
