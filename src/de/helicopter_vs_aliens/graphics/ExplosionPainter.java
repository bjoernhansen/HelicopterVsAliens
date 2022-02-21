package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.model.explosion.Explosion;

import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;


public class ExplosionPainter extends Painter<Explosion>
{
    public static void paintAll(Graphics2D g2d,
                                EnumMap<CollectionSubgroupType,
                                LinkedList<Explosion>> explosion)
    {
        for(Explosion exp : explosion.get(ACTIVE))
        {
            exp.paint(g2d);
        }
    }
    
    @Override
    void paint(Graphics2D g2d, Explosion explosion)
    {
        g2d.setPaint(explosion.getColor());
        g2d.setStroke(new BasicStroke((int)(1+(explosion.getBroadness()-1)*(1-explosion.getProgress()[0]))));
        explosion.ellipse.setFrameFromCenter(explosion.getCenter().getX(),
                                             explosion.getCenter().getY(),
                                      explosion.getCenter().getX() - (explosion.getProgress()[1] * explosion.getMaxRadius()),
                                      explosion.getCenter().getY() - (explosion.getProgress()[1] * explosion.getMaxRadius()));
        g2d.draw(explosion.ellipse);
        g2d.setStroke(new BasicStroke(1));
    }
}