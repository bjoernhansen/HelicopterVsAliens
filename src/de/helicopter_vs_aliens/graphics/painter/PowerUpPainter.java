package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


public class PowerUpPainter extends Painter<PowerUp>
{
    @Override
    public void paint(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter, PowerUp powerUp)
    {
        paint(g2d, powerUp, powerUp.getPaintBounds().x);
    }
    
    public void paint(Graphics2D g2d, PowerUp powerUp, int x)
    {
        paint(	g2d,
                x,
                powerUp.getPaintBounds().y,
                powerUp.getPaintBounds().width,
                powerUp.getPaintBounds().height,
                powerUp.getSurfaceColor(),
                powerUp.getCrossColor());
    }
    
    public void paint(Graphics2D g2d, int x, int y, int width, int height, Color surfaceColor, Color crossColor)
    {
        g2d.setPaint(surfaceColor);
        g2d.fillRoundRect(x, y, width, height, 12, 12);
        g2d.setStroke(new BasicStroke(width/5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2d.setPaint(crossColor);
        g2d.drawLine(x + width/2, y + height/5, x + width/2, y + (4 * height)/5);
        g2d.drawLine(x + width/5, y + height/2, x + (4 * width)/5, y + height/2);
        g2d.setStroke(new BasicStroke(1));
        g2d.setPaint(Colorations.dimColor(surfaceColor, 0.75f));
        g2d.drawRoundRect(x, y, width, height, 12, 12);
    }
}