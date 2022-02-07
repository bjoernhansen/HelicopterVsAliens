package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;

import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.BUSTER;

public class EnemyMissilePainter extends Painter<EnemyMissile>
{
    @Override
    void paint(Graphics2D g2d, EnemyMissile enemyMissile)
    {
        g2d.setColor(enemyMissile.getVariableColor());
        g2d.fillOval((int)enemyMissile.getLocation().getX(),
                (int)enemyMissile.getLocation().getY(),
                enemyMissile.getDiameter(),
                enemyMissile.getDiameter());
        g2d.setColor(enemyMissile.getType() == BUSTER ? Color.orange : Color.white);
        g2d.drawOval((int)enemyMissile.getLocation().getX(),
                (int)enemyMissile.getLocation().getY(),
                enemyMissile.getDiameter(),
                enemyMissile.getDiameter());
    }
    
    public static void
    paintAll(Graphics2D g2d, EnumMap<CollectionSubgroupType, LinkedList<EnemyMissile>> enemyMissile)
    {
        for(EnemyMissile em : enemyMissile.get(ACTIVE))
        {
            em.paint(g2d);
        }
    }
}