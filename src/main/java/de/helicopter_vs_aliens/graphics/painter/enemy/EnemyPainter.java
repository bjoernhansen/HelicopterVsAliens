package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.image.RescaleOp;
import java.util.Objects;


public abstract class EnemyPainter <T extends Enemy> extends Painter<T>
{
    private static final float
        DESTROYED_ENEMY_NIGHT_DIM_FACTOR = 1.3f * Colorations.NIGHT_DIM_FACTOR;
    
    private T
        enemy;


    @Override
    public void paint(GraphicsAdapter graphicsAdapter, T enemy)
    {
        setEnemy(enemy);
        
        if(!enemy.isCompletelyCloaked())
        {
            paintUnOrIncompletelyCloaked(graphicsAdapter);
        }
        else if(enemy.canBeDetectedByHelicopter())
        {
            paintCompletelyCloaked(graphicsAdapter);
        }
    }
    
    private void paintUnOrIncompletelyCloaked(GraphicsAdapter graphicsAdapter)
    {
        if(enemy.isInvincible())
        {
            paintCorpus(graphicsAdapter, enemy, enemy.getNegativeDirectionX(), Colorations.variableGreen, false, false);
        }
        else if(enemy.isCloakingDeviceActive())
        {
            if(enemy.isAlmostCloaked() && enemy.canBeDetectedByHelicopter())
            {
                paintCompletelyCloaked(graphicsAdapter);
            }
            else
            {
                paintPartiallyCloaked(graphicsAdapter);
            }
        }
        else
        {
            paintUncloaked(graphicsAdapter);
        }
        paintAnimatedElements(graphicsAdapter);
    }
    
    public final void paintCorpus(GraphicsAdapter graphicsAdapter, T enemy, int directionX, Color color, boolean isCompletelyCloakedImagePaint, boolean isImagePaint)
    {
        Color mainColorLight = Objects.requireNonNull(color);
        Color mainColorDark = Colorations.adjustBrightness(mainColorLight, 1.5f);
        paintCorpus(graphicsAdapter, enemy, directionX, mainColorLight, mainColorDark, isCompletelyCloakedImagePaint, isImagePaint);
    }
    
    public void standardImagePaintForCorpus(GraphicsAdapter graphicsAdapter, T enemy, int directionX)
    {
        Color mainColorLight, mainColorDark;
        
        if(enemy.isDestroyed() && Events.timeOfDay == TimeOfDay.NIGHT)
        {
            mainColorLight = Colorations.adjustBrightness(enemy.primaryColor, DESTROYED_ENEMY_NIGHT_DIM_FACTOR);
            mainColorDark  = Colorations.adjustBrightness(enemy.secondaryColor, DESTROYED_ENEMY_NIGHT_DIM_FACTOR);
        }
        else
        {
            mainColorLight = enemy.primaryColor;
            mainColorDark  = enemy.secondaryColor;
        }
        paintCorpus(graphicsAdapter, enemy, directionX, mainColorLight, mainColorDark, false, true);
    }
    
    private void paintCorpus(GraphicsAdapter graphicsAdapter,
                            T enemy,
                            int directionX,
                            Color mainColorLight, Color mainColorDark,
                            boolean isCompletelyCloakedImagePaint, boolean isImagePaint)
    {
        setEnemy(enemy);
        
        Color barColor = enemy.getBarColor(isImagePaint);
        Color inactiveNozzleColor = enemy.getInactiveNozzleColor();
        
        if(enemy.isDestroyed())
        {
            barColor = Colorations.adjustBrightness(barColor, Events.timeOfDay == TimeOfDay.NIGHT ? DESTROYED_ENEMY_NIGHT_DIM_FACTOR : 1);
            inactiveNozzleColor = Colorations.adjustBrightness(inactiveNozzleColor, Events.timeOfDay == TimeOfDay.NIGHT ? DESTROYED_ENEMY_NIGHT_DIM_FACTOR : 1);
        }
        
        //Malen des Gegners
        int offsetX = (int)(isImagePaint
            ? (directionX == 1 ? 0.028f * enemy.getPaintBounds().width : 0)
            : enemy.getPaintBounds().x),
            
            offsetY = (int)(isImagePaint
                ? 0.25f * enemy.getPaintBounds().height
                : enemy.getPaintBounds().y);
        
        paintEnemy(graphicsAdapter, directionX, isCompletelyCloakedImagePaint, isImagePaint, offsetX, offsetY, mainColorLight, mainColorDark, barColor, inactiveNozzleColor);
    }
    
    private void paintCompletelyCloaked(GraphicsAdapter graphicsAdapter)
    {
        Enemy enemy = getEnemy();
        graphicsAdapter.drawImage(
            enemy.getCloakedImage(),
            enemy.getPaintBounds().x - (enemy.isFlyingLeft() ? enemy.getPaintBounds().width/36 : 0),
            enemy.getPaintBounds().y - enemy.getPaintBounds().height/4);
    }
    
    private void paintPartiallyCloaked(GraphicsAdapter graphicsAdapter)
    {
        Enemy.scales[3] = ((float) enemy.getAlpha())/Colorations.MAX_OPACITY;
        graphicsAdapter.drawImage(
            enemy.getStandardImage(),
            new RescaleOp(Enemy.scales, Enemy.offsets, null),
            enemy.getPaintBounds().x - (enemy.isFlyingLeft() ? enemy.getPaintBounds().width/36 : 0),
            enemy.getPaintBounds().y - enemy.getPaintBounds().height/4);
    }
    
    private void paintUncloaked(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.drawImage(
            enemy.getStandardImage(),
            enemy.getPaintBounds().x - (enemy.isFlyingLeft() ? enemy.getPaintBounds().width/36 : 0),
            enemy.getPaintBounds().y - enemy.getPaintBounds().height/4);
    }
    
    void paintAnimatedElements(GraphicsAdapter graphicsAdapter)
    {
        // blinkende Scheibe von Bossen und Mini-Bossen bzw. die 4 Eyes bei Hindernissen
        if(enemy.hasGlowingEyes())
        {
            paintBlinkingElements(graphicsAdapter, enemy);
        }
        
        // Auspuff
        if(hasActivePropellingNozzle())
        {
            this.paintActivePropellingNozzle(graphicsAdapter);
        }
    }
    
    private void paintActivePropellingNozzle(GraphicsAdapter graphicsAdapter)
    {
        // malen der Seitenflügel mit Antriebsdüse
        paintExhaustPipe(
            graphicsAdapter,
            enemy.getPaintBounds().x,
            enemy.getPaintBounds().y,
            enemy.getNegativeDirectionX(),
            null,
            getBlinkingNozzleColor());
    }
    
    private Color getBlinkingNozzleColor()
    {
        int temp = 63 - (((int)(2 + 0.1f * Math.abs(enemy.getSpeedLevel().getX())) * enemy.getLifetime())%32);
        return new Color(  Colorations.INACTIVE_NOZZLE.getRed(),
            Colorations.INACTIVE_NOZZLE.getGreen()+temp,
            Colorations.INACTIVE_NOZZLE.getBlue()+temp,
            enemy.getAlpha());
    }
    
    private boolean hasActivePropellingNozzle()
    {
        return enemy.isIntact() && !enemy.isStunned();
    }
    
    abstract void paintCannon(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color inputColor);
    
    abstract void paintBlinkingElements(GraphicsAdapter graphicsAdapter, T enemy);
    
    abstract void paintExhaustPipe(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color mainColor, Color nozzleColor);
    
    abstract void paintEnemy(GraphicsAdapter graphicsAdapter, int directionX, boolean isCompletelyCloakedImagePaint, boolean isImagePaint, int offsetX, int offsetY, Color mainColorLight, Color mainColorDark, Color barColor, Color inactiveNozzleColor);
    
    final T getEnemy()
    {
        return enemy;
    }
    
    private void setEnemy(T enemy)
    {
        this.enemy = enemy;
    }
}