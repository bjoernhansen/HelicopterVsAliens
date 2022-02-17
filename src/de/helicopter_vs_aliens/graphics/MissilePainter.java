package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;

public class MissilePainter extends Painter<Missile>
{
    public static void paintAllMissiles(Graphics2D g2d, Controller controller)
    {
        for(Missile missile : controller.missiles.get(ACTIVE))
        {
            missile.paint(g2d);
        }
    }
    
    @Override
    void paint(Graphics2D g2d, Missile missile)
    {
        int x = missile.getPaintBounds().x;
        int y = missile.getPaintBounds().y;
        int height = missile.getPaintBounds().height;
        int width = missile.getPaintBounds().width;        
        
        g2d.setColor(Colorations.red);
        g2d.fillRect(
                x + (missile.speed >= 0 ? 0 : width + 3),
                y - 2,
                2,
                height + 4);
        
        g2d.fillRect(
                x + (missile.speed >= 0 ? 2 : width + 1),
                y - 1,
                2,
                height + 2);
        
        g2d.fillRect(
                x + (missile.speed >= 0 ? 4 : 1),
                y,
                width,
                height);
        
        g2d.setColor(Colorations.pink);
        g2d.fillRect(
                x + (missile.speed >= 0 ? 2 : 0),
                y + 1,
                width + 3,
                height - 2);
        
        g2d.setColor(Color.yellow);
        g2d.fillRect(
                x + (missile.speed >= 0 ? -3 : width + 5),
                y,
                3,
                height);
        
        g2d.setColor(Colorations.translucentWhite);
        g2d.fillRect((int) (x + width
                        * (missile.speed >= 0 ? -missile.speed / 5 : 1)
                        + (missile.speed >= 0 ? -6 : 11)),
                y,
                (int) (0.2 * Math.abs(missile.speed) * width),
                height);
    }
}