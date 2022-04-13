package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;


public class PowerUpPainter extends Painter<PowerUp>
{
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, PowerUp powerUp)
    {
        paint(graphicsAdapter, powerUp, powerUp.getPaintBounds().x);
    }
    
    public void paint(GraphicsAdapter graphicsAdapter, PowerUp powerUp, int x)
    {
        paint(	graphicsAdapter,
                x,
                powerUp.getPaintBounds().y,
                powerUp.getPaintBounds().width,
                powerUp.getPaintBounds().height,
                powerUp.getSurfaceColor(),
                powerUp.getCrossColor());
    }
    
    public void paint(GraphicsAdapter graphicsAdapter, int x, int y, int width, int height, Color surfaceColor, Color crossColor)
    {
        graphicsAdapter.setPaint(surfaceColor);
        graphicsAdapter.fillRoundRect(x, y, width, height, 12, 12);
        graphicsAdapter.setStroke(new BasicStroke((float)(width/5), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        graphicsAdapter.setPaint(crossColor);
        graphicsAdapter.drawLine(x + width/2, y + height/5, x + width/2, y + (4 * height)/5);
        graphicsAdapter.drawLine(x + width/5, y + height/2, x + (4 * width)/5, y + height/2);
        graphicsAdapter.setStroke(new BasicStroke(1));
        graphicsAdapter.setPaint(Colorations.dimColor(surfaceColor, 0.75f));
        graphicsAdapter.drawRoundRect(x, y, width, height, 12, 12);
    }
}