package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.barrier.Barrier;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.GradientPaint;


public class BarrierPainter <T extends Barrier> extends EnemyPainter<T>
{
    private static final float
        BORDER_SIZE = 0.23f,
        EYE_SIZE = 0.08f;
    
    private static final boolean
        SHOW_TESTING_INFO = false;
    
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, T enemy)
    {
        super.paint(graphicsAdapter, enemy);
        if (SHOW_TESTING_INFO)
        {
            paintTestingInfo(graphicsAdapter);
        }
    }
    
    private void paintTestingInfo(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Color.red);
        graphicsAdapter.drawString(getTestingInfo(), (int)getEnemy().getX(), (int)getEnemy().getY());
    }
    
    protected String getTestingInfo()
    {
        return    "Stun:   " + getEnemy().stunningTimer + " ; "
                + "Snooze: " + getEnemy().getSnoozeTimer() + " ; ";
    }
    
    @Override
    protected void paintUncloaked(GraphicsAdapter graphicsAdapter, Helicopter helicopter, int g2DSel)
    {
        super.paintUncloaked(graphicsAdapter, helicopter, g2DSel);
        if(!getEnemy().isDestroyed())
        {
            paintRotor(graphicsAdapter);
        }
    }
    
    @Override
    protected void paintCannon(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color inputColor)
    {
    
    }
    
    private void paintRotor(GraphicsAdapter graphicsAdapter)
    {
        Enemy enemy = getEnemy();
        paintRotor(graphicsAdapter, enemy.getPaintBounds().x, enemy.getPaintBounds().y);
    }
    
    private void paintRotor(GraphicsAdapter graphicsAdapter, int x, int y)
    {
        Barrier barrier = getEnemy();
        HelicopterPainter.paintRotor(graphicsAdapter,
            !barrier.isDestroyed()
                ?(Colorations.setAlpha(Colorations.barrierColor[barrier.getRotorColor()][Events.timeOfDay.ordinal()], barrier.alpha))
                : Colorations.adjustBrightness(Colorations.barrierColor[barrier.getRotorColor()][Events.timeOfDay.ordinal()], Colorations.DESTRUCTION_DIM_FACTOR),
            x, y, barrier.getPaintBounds().width, barrier.getPaintBounds().height, 5, (barrier.getSpeedLevel().equals(Enemy.ZERO_SPEED) ? (barrier.getSnoozeTimer() <= Enemy.SNOOZE_TIME ? 3 : 0) : 8) * (barrier.isClockwiseBarrier() ? -1 : 1) * barrier.getLifetime()%360,
            24, BORDER_SIZE, barrier.getSnoozeTimer() == 0);
        paintCannon(graphicsAdapter, x, y);
    }
    
    private void paintCannon(GraphicsAdapter graphicsAdapter, int x, int y)
    {
        Enemy enemy = getEnemy();
        Color tempColor;
        int distanceX, distanceY;
        for(int i = 0; i < 3; i++)
        {
            tempColor = (enemy.getBarrierShootTimer() != Enemy.DISABLED && enemy.getBarrierShootTimer() <= enemy.getShotsPerCycle() * enemy.getShootingRate() && i != 0 && !enemy.isDestroyed())
                ?  Colorations.variableGreen
                : !enemy.isDestroyed()
                ? Colorations.barrierColor[i][Events.timeOfDay.ordinal()]
                : Colorations.adjustBrightness(Colorations.barrierColor[i][Events.timeOfDay.ordinal()], Colorations.DESTRUCTION_DIM_FACTOR);
            if(enemy.alpha != 255){tempColor = Colorations.setAlpha(tempColor, enemy.alpha);}
            graphicsAdapter.setColor(tempColor);
            
            distanceX = (int) ((0.45f + i * 0.01f) * enemy.getPaintBounds().width);
            distanceY = (int) ((0.45f + i * 0.01f) * enemy.getPaintBounds().height);
            
            graphicsAdapter.fillOval(x + distanceX,
                y + distanceY,
                enemy.getPaintBounds().width  - 2*distanceX,
                enemy.getPaintBounds().height - 2*distanceY);
        }
    }
    
    @Override
    protected void paintEnemy(GraphicsAdapter graphicsAdapter, Barrier enemy, int directionX, Color color, boolean isImagePaint, int offsetX, int offsetY, Color mainColorLight, Color mainColorDark, Color barColor, Color inactiveNozzleColor)
    {
        paintBarrier(graphicsAdapter,
                     offsetX,
                     offsetY,
                     isImagePaint,
                     mainColorLight,
                     mainColorDark,
                     barColor,
                     inactiveNozzleColor);
    }
    
    private void paintBarrier(GraphicsAdapter graphicsAdapter,
                              int offsetX, int offsetY,
                              boolean imagePaint,
                              Color mainColorLight,
                              Color mainColorDark,
                              Color barColor,
                              Color inactiveNozzleColor)
    {
        Enemy enemy = getEnemy();
        
        // Rahmen & Antriebsbalken
        paintBarFrame(graphicsAdapter, offsetX, offsetY, 0.15f, 0f,    0f,    0.5f, barColor, mainColorLight, true);
        paintBarFrame(graphicsAdapter, offsetX, offsetY, 0.07f, 0.35f, 0.04f, 0.7f, inactiveNozzleColor, null, true);
        
        // "Augen"
        this.paintBarrierEyes(graphicsAdapter,
            offsetX,
            offsetY,
            Colorations.barrierColor[Colorations.EYES][Events.timeOfDay.ordinal()],
            imagePaint);
        
        // Turbinen-Innenraum
        this.paintRotorInterior(graphicsAdapter, mainColorDark, offsetX, offsetY );
        
        if(enemy.isDestroyed()){this.paintRotor(graphicsAdapter, offsetX, offsetY);}
    }
    
    private void paintBarFrame(GraphicsAdapter graphicsAdapter, int x, int y,
                               float thicknessFactor,
                               float shift, float centerShift,
                               float dimFactor,
                               Color inputColor, Color backgroundColor,
                               boolean imagePaint)
    {
        Enemy enemy = getEnemy();
        if(backgroundColor != null)
        {
            graphicsAdapter.setPaint(new GradientPaint(	0,
                y,
                backgroundColor,
                0,
                y + 0.3f*thicknessFactor*enemy.getPaintBounds().height,
                Colorations.adjustBrightness(backgroundColor, 0.85f),
                true));
            
            graphicsAdapter.fillRect(x + (int)(thicknessFactor/2 * enemy.getPaintBounds().width),
                y + (int)(thicknessFactor/2 * enemy.getPaintBounds().height),
                (int)((1f-thicknessFactor)  * enemy.getPaintBounds().width),
                (int)((1f-thicknessFactor)  * enemy.getPaintBounds().height));
        }
        
        int xShift = (int) (shift * enemy.getPaintBounds().width),
            yShift = (int) (shift * enemy.getPaintBounds().height),
            xCenterShift = (int) (centerShift * enemy.getPaintBounds().width),
            yCenterShift = (int) (centerShift * enemy.getPaintBounds().height);
        
        
        if(imagePaint || (enemy.getSpeedLevel().getX() != 0 && enemy.isFlyingLeft()))
        {
            paintBar(
                graphicsAdapter,
                x + xCenterShift,
                y + yShift,
                enemy.getPaintBounds().width,
                enemy.getPaintBounds().height - 2 * yShift,
                thicknessFactor,
                0.2f,
                dimFactor,
                false,
                inputColor);
        }
        if(imagePaint || (enemy.getSpeedLevel().getX() != 0 && enemy.isFlyingLeft()))
        {
            paintBar(
                graphicsAdapter,
                (int)(x + 1 + (1f-thicknessFactor)*enemy.getPaintBounds().width)-xCenterShift,
                y + yShift,
                enemy.getPaintBounds().width,
                enemy.getPaintBounds().height - 2 * yShift,
                thicknessFactor,
                0.2f,
                dimFactor,
                false,
                inputColor);
        }
        if(imagePaint || (enemy.getSpeedLevel().getY() != 0 && enemy.isFlyingDown()))
        {
            paintBar(
                graphicsAdapter,
                x + xShift,
                y + yCenterShift,
                enemy.getPaintBounds().width - 2 * xShift,
                enemy.getPaintBounds().height,
                thicknessFactor,
                0.2f,
                dimFactor,
                true,
                inputColor);
        }
        if(imagePaint || (enemy.getSpeedLevel().getY() != 0 && enemy.isFlyingUp()))
        {
            paintBar(
                graphicsAdapter,
                x + xShift,
                (int)(y + 1 + (1f-thicknessFactor)*enemy.getPaintBounds().height)-yCenterShift,
                enemy.getPaintBounds().width - 2 * xShift,
                enemy.getPaintBounds().height,
                thicknessFactor,
                0.2f,
                dimFactor,
                true,
                inputColor);
        }
    }
    
    private static void paintBar(GraphicsAdapter graphicsAdapter,
                                 int x, int y,
                                 int width, int height,
                                 float thicknessFactor,
                                 float rounding,
                                 float dimFactor,
                                 boolean horizontal,
                                 Color inputColor)
    {
        paintBar(
            graphicsAdapter,
            x, y,
            width, height,
            0, 0,
            thicknessFactor,
            rounding,
            dimFactor,
            1,
            horizontal,
            inputColor);
    }
    
    private void paintRotorInterior(GraphicsAdapter graphicsAdapter, Color mainColorDark,
                                    int offsetX, int offsetY)
    {
        Enemy enemy = getEnemy();
        
        int distanceX = (int) (BORDER_SIZE * enemy.getPaintBounds().width),
            distanceY = (int) (BORDER_SIZE * enemy.getPaintBounds().height);
        
        graphicsAdapter.setPaint(new GradientPaint(	0,
            offsetY,
            mainColorDark,
            0,
            offsetY + 0.045f*enemy.getPaintBounds().height,
            Colorations.adjustBrightness(mainColorDark, 0.85f),
            true));
        
        graphicsAdapter.fillOval(offsetX + distanceX,
            offsetY + distanceY,
            enemy.getPaintBounds().width  - 2 * distanceX,
            enemy.getPaintBounds().height - 2 * distanceY);
    }
    
    @Override
    protected void paintBlinkingElements(GraphicsAdapter graphicsAdapter, Barrier enemy)
    {
        this.paintBarrierEyes(graphicsAdapter);
    }
    
    @Override
    protected void paintExhaustPipe(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color color2, Color color4)
    {
        paintBarFrame(
            graphicsAdapter,
            getEnemy().getPaintBounds().x,
            getEnemy().getPaintBounds().y,
            0.07f,
            0.35f,
            0.04f,
            0.7f,
            color4,
            null,
            false);
    }
    
    private void paintBarrierEyes(GraphicsAdapter graphicsAdapter)
    {
        paintBarrierEyes(
            graphicsAdapter,
            getEnemy().getPaintBounds().x,
            getEnemy().getPaintBounds().y,
            getEnemy().alpha != 255
                ? Colorations.setAlpha(Colorations.variableRed, getEnemy().alpha)
                : Colorations.variableRed,
            false);
    }
    
    private void paintBarrierEyes(GraphicsAdapter graphicsAdapter, int x, int y, Color color, boolean imagePaint)
    {
        int borderDistance = (int)(0.85f * BORDER_SIZE * getEnemy().getPaintBounds().width),
            eyeSize = 		 (int)(	       EYE_SIZE * getEnemy().getPaintBounds().width);
        
        graphicsAdapter.setPaint(color);
        
        graphicsAdapter.fillOval(
            x + borderDistance,
            y + borderDistance,
            eyeSize, eyeSize);
        
        graphicsAdapter.fillOval(
            x - borderDistance + getEnemy().getPaintBounds().width  - eyeSize,
            y - borderDistance + getEnemy().getPaintBounds().height - eyeSize,
            eyeSize, eyeSize);
        
        // TODO auslagern in Methode mit verständlichem Namen
        if(!imagePaint && !(getEnemy().getSnoozeTimer() > Enemy.SNOOZE_TIME))
        {
            graphicsAdapter.setPaint(Colorations.reversedRandomRed(color));
        }
        
        graphicsAdapter.fillOval(
            x + borderDistance,
            y - borderDistance + getEnemy().getPaintBounds().height - eyeSize,
            eyeSize, eyeSize);
        
        graphicsAdapter.fillOval(
            x - borderDistance + getEnemy().getPaintBounds().width  - eyeSize,
            y + borderDistance,
            eyeSize, eyeSize);
    }
}
