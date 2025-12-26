package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.BUSTER;

public class EnemyMissilePainter extends Painter<EnemyMissile>
{
    @Override
    public void paint(EnemyMissile enemyMissile)
    {
        GraphicsAdapter graphicsAdapter = getGraphicsAdapter();
        graphicsAdapter.setColor(enemyMissile.getVariableColor());
        graphicsAdapter.fillOval( (int)enemyMissile.getLocation().getX(),
                                  (int)enemyMissile.getLocation().getY(),
                                  enemyMissile.getDiameter(),
                                  enemyMissile.getDiameter());
        graphicsAdapter.setColor((enemyMissile.getType() == BUSTER) ? Color.orange : Color.white);
        graphicsAdapter.drawOval( (int)enemyMissile.getLocation().getX(),
                                  (int)enemyMissile.getLocation().getY(),
                                  enemyMissile.getDiameter(),
                                  enemyMissile.getDiameter());
    }
}