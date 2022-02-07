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
        g2d.setColor(Colorations.red);
        g2d.fillRect(missile.paintBounds.x
                        + (missile.speed >= 0 ? 0 : missile.paintBounds.width + 3),
                missile.paintBounds.y - 2,
                2,
                missile.paintBounds.height + 4);
        
        g2d.fillRect(missile.paintBounds.x
                        + (missile.speed >= 0 ? 2 : missile.paintBounds.width + 1),
                missile.paintBounds.y - 1,
                2,
                missile.paintBounds.height + 2);
        
        g2d.fillRect(missile.paintBounds.x
                        + (missile.speed >= 0 ? 4 : 1),
                (missile.paintBounds.y),
                (missile.paintBounds.width),
                (missile.paintBounds.height));
        
        g2d.setColor(Colorations.pink);
        g2d.fillRect(missile.paintBounds.x + (missile.speed >= 0 ? 2 : 0),
                missile.paintBounds.y + 1,
                missile.paintBounds.width + 3,
                missile.paintBounds.height - 2);
        
        g2d.setColor(Color.yellow);
        g2d.fillRect(missile.paintBounds.x
                        + (missile.speed >= 0 ? -3 : missile.paintBounds.width + 5),
                (missile.paintBounds.y),
                3,
                (missile.paintBounds.height));
        
        g2d.setColor(Colorations.translucentWhite);
        g2d.fillRect((int) (missile.paintBounds.x + missile.paintBounds.width
                        * (missile.speed >= 0 ? -missile.speed / 5 : 1)
                        + (missile.speed >= 0 ? -6 : 11)),
                (missile.paintBounds.y),
                (int) (0.2 * Math.abs(missile.speed) * missile.paintBounds.width),
                (missile.paintBounds.height));
    }
}