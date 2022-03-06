package de.helicopter_vs_aliens.graphics;

import java.awt.*;
import java.util.Objects;

public class Graphics2DAdapter extends AbstractGraphicsAdapter<Graphics2D>
{
    public Graphics2DAdapter(Graphics2D graphics2D)
    {
        super(graphics2D);
    }
    
    /*
    
        g2d.setColor(Color.red);
        g2d.draw(helicopter.getBounds());
        g2d.fillOval((int) helicopter.location.getX() - 2, (int) helicopter.location.getY() - 2, 4, 4);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(left+(this.hasLeftMovingAppearance() ? 39 : 83), top+14, left+(this.hasLeftMovingAppearance() ? 39 : 83), top+29);
        g2d.setPaint(this.gradientFuss2);
        g2d.fillRoundRect(left+(this.hasLeftMovingAppearance() ? 25 : 54), top+70, 43, 5, 5, 5);
        g2d.fillRect(left+(this.hasLeftMovingAppearance() ? 92 : -7), top+31, 37,  8);
        g2d.fillArc (left+(this.hasLeftMovingAppearance() ? 34 : 23), top+11, 65, 40, 180, 180);
       
        
        
        
     */
}
