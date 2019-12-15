package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.util.Coloration;

import java.awt.*;


public class PowerUpPainter extends Painter<PowerUp>
{
    @Override
    void paint(Graphics2D g2d, PowerUp powerUp)
    {
        paint(g2d, powerUp, powerUp.paintBounds.x);
    }
    
    public void paint(Graphics2D g2d, PowerUp powerUp, int x)
    {
        paint(	g2d,
                x,
                powerUp.paintBounds.y,
                powerUp.paintBounds.width,
                powerUp.paintBounds.height,
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
        g2d.setPaint(Coloration.dimColor(surfaceColor, 0.75f));
        g2d.drawRoundRect(x, y, width, height, 12, 12);
    }
}