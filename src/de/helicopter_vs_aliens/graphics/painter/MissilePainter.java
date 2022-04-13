package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

public class MissilePainter extends Painter<Missile>
{
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Missile missile)
    {
        int x = missile.getPaintBounds().x;
        int y = missile.getPaintBounds().y;
        int height = missile.getPaintBounds().height;
        int width = missile.getPaintBounds().width;        
        
        graphicsAdapter.setColor(Colorations.red);
        graphicsAdapter.fillRect(
                x + (missile.speed >= 0 ? 0 : width + 3),
                y - 2,
                2,
                height + 4);
        
        graphicsAdapter.fillRect(
                x + (missile.speed >= 0 ? 2 : width + 1),
                y - 1,
                2,
                height + 2);
        
        graphicsAdapter.fillRect(
                x + (missile.speed >= 0 ? 4 : 1),
                y,
                width,
                height);
        
        graphicsAdapter.setColor(Colorations.pink);
        graphicsAdapter.fillRect(
                x + (missile.speed >= 0 ? 2 : 0),
                y + 1,
                width + 3,
                height - 2);
        
        graphicsAdapter.setColor(Color.yellow);
        graphicsAdapter.fillRect(
                x + (missile.speed >= 0 ? -3 : width + 5),
                y,
                3,
                height);
        
        graphicsAdapter.setColor(Colorations.translucentWhite);
        graphicsAdapter.fillRect((int) (x + width
                        * (missile.speed >= 0 ? -missile.speed / 5 : 1)
                        + (missile.speed >= 0 ? -6 : 11)),
                y,
                (int) (0.2 * Math.abs(missile.speed) * width),
                height);
    }
}