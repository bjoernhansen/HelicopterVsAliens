package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.GradientPaint;


public abstract class StandardEnemyPainter <T extends StandardEnemy> extends EnemyPainter<T>
{
    private static final boolean
        SHOW_RED_FRAME = false;
    
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
        
        Color color = getEnemy().isCloakingDeviceActive()
                        ? Colorations.setAlpha(Colorations.variableRed, getEnemy().getAlpha())
                        : Colorations.variableRed;
                
        paintCockpitWindow(
            graphicsAdapter,
            x,
            y,
            color,
            getEnemy().getNegativeDirectionX(),
            false);
    }
    
    protected abstract void paintCockpitWindow(GraphicsAdapter graphicsAdapter, int x, int y, Color color, int directionX, boolean getarnt);
    
    
    protected void setWindowColor(GraphicsAdapter graphicsAdapter, Color color, boolean getarnt)
    {
        if(color == null && !getarnt)
        {
            graphicsAdapter.setColor(getWindowColor());
        }
        else
        {
            graphicsAdapter.setColor(color);
        }
    }
    
    private Color getWindowColor()
    {
        Color baseColor = getEnemy().isIntactBoss()
                            ? Colorations.variableRed
                            : Colorations.windowBlue;
        
        return getEnemy().isCloakingDeviceActive()
                ? Colorations.setAlpha(baseColor, getEnemy().getAlpha())
                : baseColor;
    }
    
    // TODO Name der Methode passend?
    protected void paintBackgroundComponents(GraphicsAdapter graphicsAdapter, int offsetX, int offsetY, int directionX,
                                             Color mainColorLight, Color cannonColor, Enemy enemy)
    {
        this.paintAirframe(graphicsAdapter, mainColorLight, offsetX, offsetY, directionX);
        this.paintCannon(graphicsAdapter, offsetX, offsetY, directionX, cannonColor);
    }
    
    protected abstract float getCockpitWindowHeightFactor();
    
    // Malen des Rumpfes
    protected abstract void paintAirframe(GraphicsAdapter graphicsAdapter, Color mainColorLight,
                                          int offsetX, int offsetY, int directionX);
    
    protected void setAirframeColor(GraphicsAdapter graphicsAdapter, int offsetY,
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
                              int directionX,
                              boolean isCompletelyCloakedImagePaint,
                              boolean isImagePaint,
                              int offsetX, int offsetY,
                              Color mainColorLight,
                              Color mainColorDark,
                              Color barColor,
                              Color inactiveNozzleColor)
    {
        paintBackgroundComponents(graphicsAdapter, offsetX, offsetY, directionX, mainColorLight, barColor, getEnemy());
        paintExhaustPipe(graphicsAdapter, offsetX, offsetY, directionX, mainColorDark, inactiveNozzleColor);
    
        if( isCompletelyCloakedImagePaint || !getEnemy().hasGlowingEyes() || getEnemy().isDestroyed())
        {
            paintCockpitWindow(
                graphicsAdapter,
                offsetX,
                (int)(offsetY + getEnemy().getPaintBounds().height * getCockpitWindowHeightFactor()),
                isCompletelyCloakedImagePaint ? Colorations.cloakedBossEye : null,
                directionX,
                getEnemy().isCompletelyCloaked() && !isImagePaint);
        }
    
        if(SHOW_RED_FRAME){
            graphicsAdapter.setColor(Color.red);
            graphicsAdapter.drawRect(offsetX, offsetY, (int)(getEnemy().getWidth() - 1), (int)(getEnemy().getHeight() - 1));
        }
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
            paintPipe(graphicsAdapter, x, y, width, height, xShift, yShift, directionX, color2, false);
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
    
    @Override
    protected void paintAnimatedElements(GraphicsAdapter graphicsAdapter)
    {
        super.paintAnimatedElements(graphicsAdapter);
    
        // blinking enemy cannon
        if(getEnemy().hasBlinkingCannon())
        {
            this.paintCannon(
                graphicsAdapter,
                getEnemy().getPaintBounds().x, getEnemy().getPaintBounds().y,
                getEnemy().getNegativeDirectionX(),
                getBlinkingCannonColor());
        }
    }
    
    private Color getBlinkingCannonColor()
    {
        return getEnemy().isCloakingDeviceActive()
                ? Colorations.setAlpha(Colorations.variableGreen, getEnemy().getAlpha())
                : Colorations.variableGreen;
    }
}
