package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;

import java.awt.Color;

public class TitPainter <T extends StandardEnemy> extends StandardEnemyPainter<T>
{
    @Override
    protected float getCockpitWindowHeightFactor()
    {
        return 0.067f;
    }
    
    @Override
    protected void paintAirframe(GraphicsAdapter graphicsAdapter, Color mainColorLight,
                                 int offsetX, int offsetY, int directionX)
    {
        setAirframeColor(graphicsAdapter, offsetY, mainColorLight);
        
        Enemy enemy = getEnemy();
        graphicsAdapter.fillArc(
            offsetX,
            (int) (offsetY - 0.333f * enemy.getPaintBounds().height - 2),
            enemy.getPaintBounds().width,
            enemy.getPaintBounds().height, 180, 180);
    
        graphicsAdapter.fillArc((int)(
            offsetX + (directionX == 1 ? 0.2f * enemy.getPaintBounds().width : 0)),
            (int)( offsetY - 0.667f * enemy.getPaintBounds().height),
            (int)(			 0.8f   * enemy.getPaintBounds().width),
            (int)(			 1.667f * enemy.getPaintBounds().height), 180, 180);
    }
    
    @Override
    protected float getGradientColorHeightFactor()
    {
        return 0.25f;
    }
    
    @Override
    protected void paintBackgroundComponents(GraphicsAdapter graphicsAdapter, int offsetX, int offsetY, int directionX, Color mainColorLight, Color cannonColor, Enemy enemy)
    {
        super.paintBackgroundComponents(graphicsAdapter, offsetX, offsetY, directionX, mainColorLight, cannonColor, enemy);
        this.paintVerticalStabilizer(graphicsAdapter, offsetX, offsetY, directionX);
    }
    
    private void paintVerticalStabilizer(GraphicsAdapter graphicsAdapter,
                                         int offsetX, int offsetY,
                                         int directionX)
    {
        graphicsAdapter.setPaint(this.gradientColor);
        
        Enemy enemy = getEnemy();
        graphicsAdapter.fillArc((int)(offsetX + (directionX == 1 ? 0.4f : 0.1f) * enemy.getPaintBounds().width),
            (int)(offsetY - 						   0.917f * enemy.getPaintBounds().height),
            (int)(0.5f * enemy.getPaintBounds().width),
            2 * enemy.getPaintBounds().height, directionX == 1 ? 0 : 160, 20);
    }
    
    @Override
    protected void paintCockpitWindow(GraphicsAdapter graphicsAdapter, int x, int y, Color color, int directionX, boolean getarnt)
    {
        setWindowColor(graphicsAdapter, color, getarnt);
        
        Enemy enemy = getEnemy();
        int arcX = (int) (x + (directionX == 1 ? 0.25f : 0.55f) * enemy.getPaintBounds().width);
    
        graphicsAdapter.fillArc(
            arcX,
            y,
            (int) (0.2f   * enemy.getPaintBounds().width),
            (int) (0.267f * enemy.getPaintBounds().height),
            180,
            180);
    }
    
    @Override
    protected void paintCannon(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color inputColor)
    {
        GraphicalEntities.paintBar(
            graphicsAdapter,
            x,	y,
            getEnemy().getPaintBounds().width, getEnemy().getPaintBounds().height,
            0.02f, 0.007f,
            0.167f, 0.04f, 0.6f,
            directionX,
            true,
            inputColor);
    }
    
    @Override
    protected void paintExhaustPipe(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color mainColor, Color nozzleColor)
    {
        paintEngine(graphicsAdapter, x, y, 0.45f, 0.27f, 0.5f, 0.4f, directionX, mainColor, nozzleColor);
    }
}
