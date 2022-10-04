package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.model.enemy.AbilityStatusType;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.image.RescaleOp;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;


public abstract class EnemyPainter <T extends Enemy> extends Painter<T>
{
    // Konstanten
    public static final boolean
        SHOW_RED_FRAME = false;
    
    private static final float
        DESTROYED_ENEMY_NIGHT_DIM_FACTOR = 1.3f * Colorations.NIGHT_DIM_FACTOR;
    
    private T
        enemy;


    @Override
    public void paint(GraphicsAdapter graphicsAdapter, T enemy)
    {
        setEnemy(enemy);
        Helicopter helicopter = Controller.getInstance().getHelicopter();
        int g2DSel = enemy.isFlyingLeft() ? 0 : 1;
        if(!enemy.isCloaked())
        {
            paintUncloaked(graphicsAdapter, helicopter, g2DSel);
        }
        else if(helicopter.canDetectCloakedVessels())
        {
            graphicsAdapter.drawImage(	enemy.getImage()[g2DSel + 2],
                enemy.getPaintBounds().x - (enemy.isFlyingLeft() ? enemy.getPaintBounds().width/36 : 0),
                enemy.getPaintBounds().y - enemy.getPaintBounds().height/4,
                null);
        }
    }
    
    protected void paintUncloaked(GraphicsAdapter graphicsAdapter, Helicopter helicopter, int g2DSel)
    {
        if(enemy.isInvincible())
        {
            this.paintImage(graphicsAdapter, enemy, enemy.getNegativeDirectionX(), Colorations.variableGreen, false);
        }
        else if(enemy.alpha != 255)
        {
            if(enemy.alpha > 51 || !helicopter.canDetectCloakedVessels())
            {
                Enemy.scales[3] = ((float) enemy.alpha)/255;
                graphicsAdapter.drawImage(	enemy.getImage()[g2DSel],
                    new RescaleOp(Enemy.scales, Enemy.offsets, null),
                    enemy.getPaintBounds().x - (enemy.isFlyingLeft() ? enemy.getPaintBounds().width/36 : 0),
                    enemy.getPaintBounds().y - enemy.getPaintBounds().height/4);
            }
            else
            {
                graphicsAdapter.drawImage(enemy.getImage()[g2DSel + 2],
                    enemy.getPaintBounds().x - (enemy.isFlyingLeft() ? enemy.getPaintBounds().width/36 : 0),
                    enemy.getPaintBounds().y - enemy.getPaintBounds().height/4, null);
            }
        }
        else
        {
            graphicsAdapter.drawImage(enemy.getImage()[g2DSel],
                enemy.getPaintBounds().x - (enemy.isFlyingLeft() ? enemy.getPaintBounds().width/36 : 0),
                enemy.getPaintBounds().y - enemy.getPaintBounds().height/4, null);
        }
        
        // Dach
        if(!enemy.isDestroyed() && (enemy.getTractor() == AbilityStatusType.ACTIVE || enemy.getShootTimer() > 0 || enemy.isShielding()))
        {
            Color inputColorRoof
                = enemy.alpha < 255
                ? Colorations.setAlpha(Colorations.variableGreen, enemy.alpha)
                : Colorations.variableGreen;
            
            this.paintCannon(graphicsAdapter, enemy.getPaintBounds().x, enemy.getPaintBounds().y, enemy.getNegativeDirectionX(), inputColorRoof);
        }
        
        // blinkende Scheibe von Bossen und Mini-Bossen bzw. Eyes bei Hindernissen
        if(enemy.hasGlowingEyes())
        {
            paintBlinkingElements(graphicsAdapter, enemy);
        }
        
        // Auspuff
        if(!(enemy.isDestroyed() || enemy.isStunned()))
        {
            int temp = 63 - (((int)(2 + 0.1f * Math.abs(enemy.getSpeedLevel().getX())) * enemy.getLifetime())%32);
            Color colorTemp = new Color(255, 192+temp, 129+temp, enemy.alpha);
            this.paintExhaustPipe(graphicsAdapter, colorTemp);
        }
    }
    
    protected abstract void paintCannon(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color inputColor);
    
    abstract void paintBlinkingElements(GraphicsAdapter graphicsAdapter, T enemy);
    
    static void paintBar(GraphicsAdapter graphicsAdapter,
                         int x, int y,
                         int width, int height,
                         float xShiftLeft, float xShiftRight,
                         float thicknessFactor, float rounding,
                         float dimFactor, int directionX,
                         boolean horizontal, Color inputColor)
    {
        graphicsAdapter.setPaint(
            new GradientPaint(
                (int) (horizontal ? 0 : x + 0.5f * thicknessFactor * width),
                (int) (horizontal ?     y + 0.5f * thicknessFactor * height : 0),
                inputColor,
                (int) (horizontal ? 0 : x + 1.0f * thicknessFactor * width),
                (int) (horizontal ?     y + 1.0f * thicknessFactor * height : 0),
                Colorations.adjustBrightness(inputColor, dimFactor),
                true));
        
        graphicsAdapter.fillRoundRect(
            (int) (x - (directionX == 1 ? xShiftLeft : xShiftRight) * width),
            y,
            (int) (	horizontal ? (1 + xShiftLeft + xShiftRight) * width : thicknessFactor * width),
            (int) (	horizontal ? thicknessFactor * height : (1 + xShiftLeft + xShiftRight) * height ),
            (int) (	horizontal ? rounding * width : thicknessFactor * width),
            (int) (	horizontal ? thicknessFactor * height : rounding * height) );
    }
    
    // malen der Seitenflügel mit Antriebsdüse
    private void paintExhaustPipe(GraphicsAdapter graphicsAdapter, Color color4)
    {
        paintExhaustPipe(
            graphicsAdapter,
            enemy.getPaintBounds().x,
            enemy.getPaintBounds().y,
            enemy.getNegativeDirectionX(),
            null,
            color4);
    }
    
    // TODO bessere Namen für Bezeichner color2 , color 4
    protected abstract void paintExhaustPipe(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color color2, Color color4);
    
    // TODO Methodensignatur verbessern: Name paintImage, boolean Parameter isImagePaint ... unverständlich
    public void paintImage(GraphicsAdapter graphicsAdapter, T enemy, int directionX, Color color, boolean isImagePaint)
    {
        setEnemy(enemy);
        int offsetX = (int)(isImagePaint
            ? (directionX == 1 ? 0.028f * enemy.getPaintBounds().width : 0)
            : enemy.getPaintBounds().x),
            
            offsetY = (int)(isImagePaint
                ? 0.25f * enemy.getPaintBounds().height
                : enemy.getPaintBounds().y);
        
        /*
         * Festlegen der Farben
         */
        Color mainColorLight, mainColorDark;
        if(color == null)
        {
            if(enemy.isDestroyed() && Events.timeOfDay == NIGHT)
            {
                mainColorLight = Colorations.adjustBrightness(enemy.primaryColor, DESTROYED_ENEMY_NIGHT_DIM_FACTOR);
                mainColorDark  = Colorations.adjustBrightness(enemy.secondaryColor, DESTROYED_ENEMY_NIGHT_DIM_FACTOR);
            }
            else
            {
                mainColorLight = enemy.primaryColor;
                mainColorDark  = enemy.secondaryColor;
            }
        }
        else
        {
            mainColorLight = color;
            mainColorDark = Colorations.adjustBrightness(color, 1.5f);
        }
        
        Color barColor = enemy.getBarColor(isImagePaint);
        Color inactiveNozzleColor = enemy.getInactiveNozzleColor();
        
        if(enemy.isDestroyed())
        {
            barColor = Colorations.adjustBrightness(barColor, Events.timeOfDay == NIGHT ? DESTROYED_ENEMY_NIGHT_DIM_FACTOR : 1);
            inactiveNozzleColor = Colorations.adjustBrightness(inactiveNozzleColor, Events.timeOfDay == NIGHT ? DESTROYED_ENEMY_NIGHT_DIM_FACTOR : 1);
        }
        
        //Malen des Gegners
        paintEnemy(graphicsAdapter, enemy, directionX, color, isImagePaint, offsetX, offsetY, mainColorLight, mainColorDark, barColor, inactiveNozzleColor);
    }
    
    protected abstract void paintEnemy(GraphicsAdapter graphicsAdapter, T enemy, int directionX, Color color, boolean isImagePaint, int offsetX, int offsetY, Color mainColorLight, Color mainColorDark, Color barColor, Color inactiveNozzleColor);
    
    protected final T getEnemy()
    {
        return enemy;
    }
    
    private void setEnemy(T enemy)
    {
        this.enemy = enemy;
    }
}