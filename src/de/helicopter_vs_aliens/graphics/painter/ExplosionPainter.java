package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.explosion.Explosion;

import java.awt.BasicStroke;


public class ExplosionPainter extends Painter<Explosion>
{
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Explosion explosion)
    {
        graphicsAdapter.setPaint(explosion.getColor());
        graphicsAdapter.setStroke(new BasicStroke((int)(1+(explosion.getBroadness()-1)*(1-explosion.getProgress()[0]))));
        explosion.ellipse.setFrameFromCenter(explosion.getCenter().getX(),
                                             explosion.getCenter().getY(),
                                      explosion.getCenter().getX() - (explosion.getProgress()[1] * explosion.getMaxRadius()),
                                      explosion.getCenter().getY() - (explosion.getProgress()[1] * explosion.getMaxRadius()));
        graphicsAdapter.draw(explosion.ellipse);
        graphicsAdapter.setStroke(new BasicStroke(1));
    }
}
