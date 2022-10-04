package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;

import java.awt.Color;

public class CargoPainter <T extends StandardEnemy> extends StandardEnemyPainter<T>
{
    @Override
    protected float getCockpitWindowHeightFactor()
    {
        return 0.125f;
    }
    
    @Override
    protected void paintAirframe(GraphicsAdapter graphicsAdapter, Color mainColorLight,
                                 int offsetX, int offsetY, int directionX)
    {
        super.paintAirframe(graphicsAdapter, mainColorLight, offsetX, offsetY, directionX);
        
        Enemy enemy = getEnemy();
        graphicsAdapter.fillOval(
            (int)(offsetX + 0.02f * enemy.getPaintBounds().width),
            (int)(offsetY + 0.1f * enemy.getPaintBounds().height),
            (int)(0.96f * enemy.getPaintBounds().width),
            (int)(0.9f  * enemy.getPaintBounds().height));
    
        graphicsAdapter.fillRect(
            (int)(offsetX + (directionX == 1 ? 0.05f : 0.35f) * enemy.getPaintBounds().width),
            (int)(offsetY + 0.094f * enemy.getPaintBounds().height),
            (int)(0.6f * enemy.getPaintBounds().width),
            (int)(0.333f * enemy.getPaintBounds().height));
    
        graphicsAdapter.fillRoundRect(
            (int) (offsetX + (directionX == 1 ? 0.05f : 0.35f) * enemy.getPaintBounds().width),
            (int) (offsetY + 0.031 * enemy.getPaintBounds().height),
            (int) (0.6f * enemy.getPaintBounds().width),
            (int) (0.125f * enemy.getPaintBounds().height),
            (int) (0.6f * enemy.getPaintBounds().width),
            (int) (0.125f * enemy.getPaintBounds().height));
    
        // Rückflügel
        graphicsAdapter.fillArc(
            (int)(offsetX + (directionX == 1 ? 0.5f * enemy.getPaintBounds().width : 0)),
            (int)(offsetY - 0.3f * enemy.getPaintBounds().height),
            (int)(0.5f * enemy.getPaintBounds().width),
            enemy.getPaintBounds().height,
            directionX == 1 ? -32 : 155,
            57);
    }
    
    @Override
    protected float getGradientColorHeightFactor()
    {
        return 0.375f;
    }
    
    @Override
    protected void paintBackgroundComponents(GraphicsAdapter graphicsAdapter, int offsetX, int offsetY, int directionX, Color mainColorLight, Color cannonColor, Enemy enemy)
    {
        this.paintRoof(graphicsAdapter, cannonColor, offsetX, offsetY, directionX);
        super.paintBackgroundComponents(graphicsAdapter, offsetX, offsetY, directionX, mainColorLight, cannonColor, enemy);
    }
    
    private void paintRoof(GraphicsAdapter graphicsAdapter, Color roofColor, int offsetX,
                           int offsetY, int directionX)
    {
        graphicsAdapter.setPaint(roofColor);
        
        Enemy enemy = getEnemy();
        graphicsAdapter.fillRoundRect(	(int) (offsetX + (directionX == 1 ? 0.05f :  0.35f) * enemy.getPaintBounds().width),
            offsetY,
            (int) (0.6f   * enemy.getPaintBounds().width),
            (int) (0.125f * enemy.getPaintBounds().height),
            (int) (0.6f   * enemy.getPaintBounds().width),
            (int) (0.125f * enemy.getPaintBounds().height));
    }
    
    @Override
    protected void paintCockpitWindow(GraphicsAdapter graphicsAdapter, int x, int y, Color color, int directionX, boolean getarnt)
    {
        super.paintCockpitWindow(graphicsAdapter, x, y, color, directionX, getarnt);
        
        Enemy enemy = getEnemy();
        graphicsAdapter.fillArc(
            (int) (x + (directionX == 1 ? 0.1 : 0.6) * enemy.getPaintBounds().width),
            y,
            (int) (0.3f   * enemy.getPaintBounds().width),
            (int) (0.333f * enemy.getPaintBounds().height),
            directionX == 1 ? 90 : 0,
            90);
    }
    
    @Override
    protected void paintCannon(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color inputColor)
    {
        paintBar(
            graphicsAdapter,
            x,
            (int) (y + 0.48f * getEnemy().getPaintBounds().height),
            getEnemy().getPaintBounds().width,
            getEnemy().getPaintBounds().height,
            0, 0, 0.1f, 0.04f, 0.6f,
            directionX, true, inputColor);
    }
    
    @Override
    protected void paintExhaustPipe(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color color2, Color color4)
    {
        paintEngine(graphicsAdapter, x, y, 0.45f, 0.17f, 0.45f, 0.22f, directionX, color2, color4);
        paintEngine(graphicsAdapter, x, y, 0.45f, 0.17f, 0.25f, 0.70f, directionX, color2, color4);
    }
}
