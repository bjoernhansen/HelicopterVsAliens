package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.GradientPaint;


public abstract class StandardEnemyPainter <T extends StandardEnemy> extends EnemyPainter<T>
{
    GradientPaint
        gradientColor;
        
    @Override
    protected void paintBlinkingElements(GraphicsAdapter graphicsAdapter, T enemy)
    {
        this.paintCockpitWindow(graphicsAdapter);
    }
    
    private void paintCockpitWindow(GraphicsAdapter graphicsAdapter)
    {
        int x =  getEnemy().getPaintBounds().x;
        
        int y = (int) (   getEnemy().getPaintBounds().y
                        + getEnemy().getPaintBounds().height *getCockpitWindowHeightFactor());
        
        Color color = getEnemy().alpha != 255
                        ? Colorations.setAlpha(Colorations.variableRed, getEnemy().alpha)
                        : Colorations.variableRed;
                
        paintCockpitWindow(
            graphicsAdapter,
            x,
            y,
            color,
            getEnemy().getNegativeDirectionX(),
            false);
    }
    
    protected void paintCockpitWindow(GraphicsAdapter graphicsAdapter, int x, int y, Color color, int directionX, boolean getarnt)
    {
        setWindowColor(graphicsAdapter, color, getarnt);
    }
    
    private void setWindowColor(GraphicsAdapter graphicsAdapter, Color color, boolean getarnt)
    {
        Enemy enemy = getEnemy();
        if(color == null && !getarnt)
        {
            graphicsAdapter.setColor(enemy.isLivingBoss()
                ? (enemy.alpha == 255
                ? Colorations.variableRed
                : Colorations.setAlpha(Colorations.variableRed, enemy.alpha))
                : (enemy.alpha == 255
                ? Colorations.windowBlue
                : Colorations.setAlpha(Colorations.windowBlue, enemy.alpha)));
        }
        else
        {
            graphicsAdapter.setColor(color);
        }
    }
    
    void paintVessel(GraphicsAdapter graphicsAdapter, int offsetX, int offsetY,
                             int directionX, Color color, boolean getarnt,
                             boolean imagePaint,
                             Color mainColorLight,
                             Color mainColorDark,
                             Color cannonColor,
                             Color inactiveNozzleColor)
    {
        Enemy enemy = getEnemy();
        paintBackgroundComponents(graphicsAdapter, offsetX, offsetY, directionX, mainColorLight, cannonColor, enemy);
        paintExhaustPipe(graphicsAdapter, offsetX, offsetY, directionX, mainColorDark, inactiveNozzleColor);
        
        if(Color.red.equals(color) || !enemy.isLivingBoss())
        {
            paintCockpitWindow(
                graphicsAdapter,
                offsetX,
                (int)(offsetY + enemy.getPaintBounds().height * getCockpitWindowHeightFactor()),
                Color.red.equals(color) ? Colorations.cloakedBossEye : null,
                directionX,
                getarnt && !imagePaint);
        }
        
        if(SHOW_RED_FRAME){
            graphicsAdapter.setColor(Color.red);
            graphicsAdapter.drawRect(offsetX, offsetY, (int)(enemy.getWidth() - 1), (int)(enemy.getHeight() - 1));
        }
    }
    
    protected void paintBackgroundComponents(GraphicsAdapter graphicsAdapter, int offsetX, int offsetY, int directionX,
                                             Color mainColorLight, Color cannonColor, Enemy enemy)
    {
        this.paintAirframe(graphicsAdapter, mainColorLight, offsetX, offsetY, directionX);
        this.paintCannon(graphicsAdapter, offsetX, offsetY, directionX, cannonColor);
    }
    
    protected abstract float getCockpitWindowHeightFactor();
    
    // Malen des Rumpfes
    protected void paintAirframe(GraphicsAdapter graphicsAdapter, Color mainColorLight,
                               int offsetX, int offsetY, int directionX)
    {
        setAirframeColor(graphicsAdapter, offsetY, mainColorLight);
    }
    
    private void setAirframeColor(GraphicsAdapter graphicsAdapter, int offsetY,
                                  Color mainColorLight)
    {
        Enemy enemy = getEnemy();
        this.gradientColor = new GradientPaint(
            0,
            offsetY + getGradientColorHeightFactor() * enemy.getPaintBounds().height,
            mainColorLight,
            0,
            offsetY + enemy.getPaintBounds().height,
            Colorations.adjustBrightness(mainColorLight, 0.5f),
            true);
        
        graphicsAdapter.setPaint(this.gradientColor);
    }
    
    protected abstract float getGradientColorHeightFactor();
    
    @Override
    protected void paintEnemy(GraphicsAdapter graphicsAdapter,
                              T enemy,
                              int directionX,
                              Color color,
                              boolean isImagePaint,
                              int offsetX, int offsetY,
                              Color mainColorLight, Color mainColorDark, Color barColor, Color inactiveNozzleColor)
    {
        paintVessel(	graphicsAdapter,
                        offsetX,
                        offsetY,
                        directionX,
                        color,
                        enemy.isCloaked(),
                        isImagePaint,
                        mainColorLight,
                        mainColorDark,
                        barColor,
                        inactiveNozzleColor);
    }
    
    void paintEngine(GraphicsAdapter graphicsAdapter,
                     int x, int y,
                     float width, float height,
                     float xShift, float yShift,
                     int directionX,
                     Color color2, Color color4)
    {
        if(color2 != null)
        {
            paintPipe(graphicsAdapter, x, y, width, height, xShift, 				  yShift, directionX, color2, false);
        }
        paintPipe(graphicsAdapter, x, y, 0.05f, height, xShift + width - 0.05f, yShift, directionX, color4, true);
    }
    
    private void paintPipe(GraphicsAdapter graphicsAdapter,
                           int x, int y,
                           float width, float height,
                           float xShift, float yShift,
                           int directionX, Color color, boolean isExhaust)
    {
        graphicsAdapter.setPaint(new GradientPaint(
            0,
            y + (yShift + 0.05f)  * getEnemy().getPaintBounds().height,
            color,
            0,
            y + (yShift + height) * getEnemy().getPaintBounds().height,
            Colorations.adjustBrightness(color, 0.5f),
            true));
        
        graphicsAdapter.fillRoundRect(
            (int) (x + (directionX == 1
                        ? xShift
                        : 1f - xShift - width)	* getEnemy().getPaintBounds().width),
            (int) (y + 	yShift 			   	* getEnemy().getPaintBounds().height),
            (int) (		width  				   	* getEnemy().getPaintBounds().width),
            (int) (		height  			   	* getEnemy().getPaintBounds().height),
            (int) ((isExhaust ? 0f : height/2) * getEnemy().getPaintBounds().width),
            (int) ((isExhaust ? 0f : height  ) * getEnemy().getPaintBounds().height)  );
    }
}
