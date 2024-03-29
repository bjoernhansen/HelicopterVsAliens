package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.boss.Healer;

import java.awt.BasicStroke;
import java.awt.Color;

public class HealerPainter extends CargoPainter<Healer>
{
    @Override
    protected void paintEnemy(GraphicsAdapter graphicsAdapter,
                              int directionX,
                              boolean isCompletelyCloakedImagePaint,
                              boolean isImagePaint,
                              int offsetX, int offsetY,
                              Color mainColorLight,
                              Color mainColorDark,
                              Color barColor,
                              Color inactiveNozzleColor)
    {
        super.paintEnemy(graphicsAdapter, directionX, isCompletelyCloakedImagePaint, isImagePaint, offsetX, offsetY, mainColorLight, mainColorDark, barColor, inactiveNozzleColor);
    
        // das rote Kreuz
        Enemy enemy = getEnemy();
        paintRedCross(  graphicsAdapter,
            (int) (offsetX + (directionX == 1
                ? 0.7f * enemy.getPaintBounds().width
                : (1 - 0.7f - 0.18f) * enemy.getPaintBounds().width)),
            (int) (offsetY + 0.6f * enemy.getPaintBounds().height),
            (int) (0.18f * enemy.getPaintBounds().width));
    }
    
    private static void paintRedCross(GraphicsAdapter graphicsAdapter, int x, int y, int height)
    {
        graphicsAdapter.setColor(Color.red);
        graphicsAdapter.setStroke(new BasicStroke(height/5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        graphicsAdapter.drawLine(x + height/2, y + height/5, x + height/2, y + (4 * height)/5);
        graphicsAdapter.drawLine(x + height/5, y + height/2, x + (4 * height)/5, y + height/2);
        graphicsAdapter.setStroke(new BasicStroke(1));
        //graphicsAdapter.drawRect(x, y, height, height);
    }
}
