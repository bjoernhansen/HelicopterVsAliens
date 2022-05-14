package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.model.enemy.AbilityStatusType;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.image.RescaleOp;
import java.util.Optional;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BIG_SHIELD_MAKER;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.FINAL_BOSS;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.HEALER;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.SMALL_SHIELD_MAKER;

public class EnemyPainter extends Painter<Enemy>
{
    // Konstanten
    public static final boolean
        SHOW_BARRIER_TESTING_INFO = false,
        SHOW_RED_FRAME = false;
    
    private static final float
        BARRIER_BORDER_SIZE 		= 0.23f,
        BARRIER_EYE_SIZE 			= 0.08f;
            
    private Enemy 
        enemy;

    private GradientPaint
        gradientColor;
    
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Enemy enemy)
    {
        setEnemy(enemy);
        Helicopter helicopter = Controller.getInstance().getHelicopter();
        boolean cloaked = enemy.getCloakingTimer() > Enemy.CLOAKING_TIME && enemy.getCloakingTimer() <= Enemy.CLOAKING_TIME + Enemy.CLOAKED_TIME;
        int g2DSel = enemy.direction.x == -1 ? 0 : 1;
        
        if(!cloaked)
        {
            if(enemy.isInvincible())
            {
                this.paintImage(graphicsAdapter, enemy, -enemy.direction.x, Colorations.variableGreen, false);
            }
            else if(enemy.alpha != 255)
            {
                if(enemy.alpha > 51 || !helicopter.canDetectCloakedVessels())
                {
                    Enemy.scales[3] = ((float)enemy.alpha)/255;
                    graphicsAdapter.drawImage(	enemy.getImage()[g2DSel],
                        new RescaleOp(Enemy.scales, Enemy.offsets, null),
                        enemy.getPaintBounds().x - (enemy.direction.x == -1 ? enemy.getPaintBounds().width/36 : 0),
                        enemy.getPaintBounds().y - enemy.getPaintBounds().height/4);
                }
                else
                {
                    graphicsAdapter.drawImage(enemy.getImage()[g2DSel + 2],
                        enemy.getPaintBounds().x - (enemy.direction.x == -1 ? enemy.getPaintBounds().width/36 : 0),
                        enemy.getPaintBounds().y - enemy.getPaintBounds().height/4, null);
                }
            }
            else
            {
                graphicsAdapter.drawImage(enemy.getImage()[g2DSel],
                    enemy.getPaintBounds().x - (enemy.direction.x == -1 ? enemy.getPaintBounds().width/36 : 0),
                    enemy.getPaintBounds().y - enemy.getPaintBounds().height/4, null);
            }
            
            // Dach
            if(!enemy.isDestroyed() && (enemy.getTractor() == AbilityStatusType.ACTIVE || enemy.getShootTimer() > 0 || enemy.isShielding()))
            {
                Color inputColorRoof
                    = enemy.alpha < 255
                    ? Colorations.setAlpha(Colorations.variableGreen, enemy.alpha)
                    : Colorations.variableGreen;
                
                this.paintCannon(graphicsAdapter, enemy.getPaintBounds().x, enemy.getPaintBounds().y, -enemy.direction.x, inputColorRoof);
            }
            
            // blinkende Scheibe von Bossen und Mini-Bossen bzw. Eyes bei Hindernissen
            if(enemy.hasGlowingEyes())
            {
                if(enemy.getModel() != BARRIER){this.paintCockpitWindow(graphicsAdapter);}
                else{this.paintBarrierEyes(graphicsAdapter);}
            }
            
            // Auspuff
            if(!(enemy.isDestroyed() || enemy.stunningTimer > 0))
            {
                int temp = 63 - (((int)(2 + 0.1f * Math.abs(enemy.getSpeedLevel().getX())) * enemy.getLifetime())%32);
                Color colorTemp = new Color(255, 192+temp, 129+temp, enemy.alpha);
                this.paintExhaustPipe(graphicsAdapter, colorTemp);
            }
            
            // die Schild- und Traktorstrahlen
            if(enemy.getTractor() == AbilityStatusType.ACTIVE){this.paintTractorBeam(graphicsAdapter, helicopter);}
            else if(enemy.type == FINAL_BOSS)
            {
                for(int servantType = Enemy.id(SMALL_SHIELD_MAKER); servantType <= Enemy.id(BIG_SHIELD_MAKER); servantType++)
                {
                    Optional.ofNullable(enemy.getOperatorServant(servantType))
                            .filter(Enemy::isShielding)
                            .ifPresent(servant -> this.paintShieldBeam(graphicsAdapter, servant));
                }
            }
            
            if(enemy.getModel() == BARRIER && !enemy.isDestroyed())
            {
                this.paintRotor(graphicsAdapter);
            }
        }
        else if(helicopter.canDetectCloakedVessels())
        {
            graphicsAdapter.drawImage(	enemy.getImage()[g2DSel + 2],
                enemy.getPaintBounds().x - (enemy.direction.x == -1 ? enemy.getPaintBounds().width/36 : 0),
                enemy.getPaintBounds().y - enemy.getPaintBounds().height/4,
                null);
        }
        
        //zu Testzwecken:
        if(SHOW_BARRIER_TESTING_INFO)
        {
            graphicsAdapter.setColor(Color.red);
            if(enemy.getModel() == BARRIER)
            {
                graphicsAdapter.drawString(   "Borrow: " + enemy.burrowTimer + " ; "
                        
                        + "Stun: "   + enemy.stunningTimer + " ; "
                        + "Snooze: " + enemy.getSnoozeTimer() + " ; ",
                    (int)enemy.getX(),
                    (int)enemy.getY());
            }
        }
    }
    
    private void paintRotor(GraphicsAdapter graphicsAdapter)
    {
        paintRotor(graphicsAdapter, enemy.getPaintBounds().x, enemy.getPaintBounds().y);
    }
    
    private void paintRotor(GraphicsAdapter graphicsAdapter, int x, int y)
    {
        HelicopterPainter.paintRotor(graphicsAdapter,
            !enemy.isDestroyed()
                ?(Colorations.setAlpha(Colorations.barrierColor[enemy.getRotorColor()][Events.timeOfDay.ordinal()], enemy.alpha))
                : Colorations.dimColor(Colorations.barrierColor[enemy.getRotorColor()][Events.timeOfDay.ordinal()], Colorations.DESTRUCTION_DIM_FACTOR),
            x, y, enemy.getPaintBounds().width, enemy.getPaintBounds().height, 5, (enemy.getSpeedLevel().equals(Enemy.ZERO_SPEED) ? (enemy.getSnoozeTimer() <= Enemy.SNOOZE_TIME ? 3 : 0) : 8) * (enemy.isClockwiseBarrier() ? -1 : 1) * enemy.getLifetime()%360,
            24, BARRIER_BORDER_SIZE, enemy.getSnoozeTimer() == 0);
        this.paintBarrierCannon(graphicsAdapter, x, y);
    }
    
    private void paintBarrierCannon(GraphicsAdapter graphicsAdapter, int x, int y)
    {
        Color tempColor;
        int distanceX, distanceY;
        for(int i = 0; i < 3; i++)
        {
            tempColor = (enemy.getBarrierShootTimer() != Enemy.DISABLED && enemy.getBarrierShootTimer() <= enemy.getShotsPerCycle() * enemy.getShootingRate() && i != 0 && !enemy.isDestroyed())
                ?  Colorations.variableGreen
                : !enemy.isDestroyed()
                ? Colorations.barrierColor[i][Events.timeOfDay.ordinal()]
                : Colorations.dimColor(Colorations.barrierColor[i][Events.timeOfDay.ordinal()], Colorations.DESTRUCTION_DIM_FACTOR);
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
    
    private void paintCannon(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color inputColor)
    {
        if(enemy.getModel() == TIT)
        {
            paintBar(	graphicsAdapter,
                x,	y,
                enemy.getPaintBounds().width, enemy.getPaintBounds().height,
                0.02f, 0.007f, 0.167f, 0.04f, 0.6f,
                directionX, true, inputColor);
        }
        else if(enemy.getModel() == CARGO)
        {
            paintBar(	graphicsAdapter,
                x, (int) (y + 0.48f * enemy.getPaintBounds().height),
                enemy.getPaintBounds().width, enemy.getPaintBounds().height,
                0, 0, 0.1f, 0.04f, 0.6f,
                directionX, true, inputColor);
        }
    }
    
    private void paintBarFrame(GraphicsAdapter graphicsAdapter, int x, int y,
                               float thicknessFactor,
                               float shift, float centerShift,
                               float dimFactor,
                               Color inputColor, Color backgroundColor,
                               boolean imagePaint)
    {
        if(backgroundColor != null)
        {
            graphicsAdapter.setPaint(new GradientPaint(	0,
                y,
                backgroundColor,
                0,
                y + 0.3f*thicknessFactor*enemy.getPaintBounds().height,
                Colorations.dimColor(backgroundColor, 0.85f),
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
        
        
        if(imagePaint || (enemy.getSpeedLevel().getX() != 0 && enemy.direction.x == 1))
        {
            paintBar(	graphicsAdapter,
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
        if(imagePaint || (enemy.getSpeedLevel().getX() != 0 && enemy.direction.x ==  -1))
        {
            paintBar(	graphicsAdapter,
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
        if(imagePaint || (enemy.getSpeedLevel().getY() != 0 && enemy.direction.y ==  1))
        {
            paintBar(	graphicsAdapter,
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
        if(imagePaint || (enemy.getSpeedLevel().getY() != 0 && enemy.direction.y == -1))
        {
            paintBar(	graphicsAdapter,
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
        paintBar(	graphicsAdapter,
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
    
    private static void paintBar(GraphicsAdapter graphicsAdapter,
                                 int x, int y,
                                 int width, int height,
                                 float xShiftLeft, float xShiftRight,
                                 float thicknessFactor, float rounding,
                                 float dimFactor, int directionX,
                                 boolean horizontal, Color inputColor)
    {
        graphicsAdapter.setPaint( new GradientPaint(	(int) (horizontal ? 0 : x + 0.5f * thicknessFactor * width),
            (int) (horizontal ?     y + 0.5f * thicknessFactor * height : 0),
            inputColor,
            (int) (horizontal ? 0 : x + 1.0f * thicknessFactor * width),
            (int) (horizontal ?     y + 1.0f * thicknessFactor * height : 0),
            Colorations.dimColor(inputColor, dimFactor),
            true));
        
        graphicsAdapter.fillRoundRect(	(int) (x - (directionX == 1 ? xShiftLeft : xShiftRight) * width),
            y,
            (int) (	horizontal ? (1 + xShiftLeft + xShiftRight) * width : thicknessFactor * width),
            (int) (	horizontal ? thicknessFactor * height : (1 + xShiftLeft + xShiftRight) * height ),
            (int) (	horizontal ? rounding * width : thicknessFactor * width),
            (int) (	horizontal ? thicknessFactor * height : rounding * height) );
    }
    
    // malen der Seitenflügel mit Antriebsdüse
    private void paintExhaustPipe(GraphicsAdapter graphicsAdapter, Color color4)
    {
        paintExhaustPipe(graphicsAdapter,
            enemy.getPaintBounds().x,
            enemy.getPaintBounds().y,
            -enemy.direction.x,
            null,
            color4);
    }
    
    // TODO bessere Namen für Bezeichner color2 , color 4
    private void paintExhaustPipe(GraphicsAdapter graphicsAdapter, int x, int y, int directionX, Color color2, Color color4)
    {
        if(enemy.getModel() == TIT)
        {
            paintEngine(graphicsAdapter, x, y, 0.45f, 0.27f, 0.5f, 0.4f, directionX, color2, color4);
        }
        else if(enemy.getModel() == CARGO)
        {
            paintEngine(graphicsAdapter, x, y, 0.45f, 0.17f, 0.45f, 0.22f, directionX, color2, color4);
            paintEngine(graphicsAdapter, x, y, 0.45f, 0.17f, 0.25f, 0.70f, directionX, color2, color4);
        }
        else if(enemy.getModel() == BARRIER)
        {
            paintBarFrame(graphicsAdapter, enemy.getPaintBounds().x, enemy.getPaintBounds().y,
                0.07f, 0.35f, 0.04f, 0.7f, color4, null, false);
        }
    }
    
    private void paintEngine(GraphicsAdapter graphicsAdapter,
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
        graphicsAdapter.setPaint(new GradientPaint(	0,
            y + (yShift + 0.05f)  * enemy.getPaintBounds().height,
            color,
            0,
            y + (yShift + height) * enemy.getPaintBounds().height,
            Colorations.dimColor(color, 0.5f),
            true));
        
        graphicsAdapter.fillRoundRect(	(int) (x + (directionX == 1
                ? xShift
                : 1f - xShift - width)	* enemy.getPaintBounds().width),
            (int) (y + 	yShift 			   	* enemy.getPaintBounds().height),
            (int) (		width  				   	* enemy.getPaintBounds().width),
            (int) (		height  			   	* enemy.getPaintBounds().height),
            (int) ((isExhaust ? 0f : height/2) * enemy.getPaintBounds().width),
            (int) ((isExhaust ? 0f : height  ) * enemy.getPaintBounds().height)  );
    }
    
    public void paintImage(GraphicsAdapter graphicsAdapter, Enemy enemy, int directionX, Color color, boolean imagePaint)
    {
        setEnemy(enemy);
        int offsetX = (int)(imagePaint
            ? (directionX == 1 ? 0.028f * enemy.getPaintBounds().width : 0)
            : enemy.getPaintBounds().x),
            
            offsetY = (int)(imagePaint
                ? 0.25f * enemy.getPaintBounds().height
                : enemy.getPaintBounds().y);
        
        /*
         * Festlegen der Farben
         */
        Color mainColorLight, mainColorDark, barColor, inactiveNozzleColor;
        
        if(color == null)
        {
            if(enemy.isDestroyed() && Events.timeOfDay == NIGHT)
            {
                mainColorLight = Colorations.dimColor(enemy.primaryColor, 1.3f * Colorations.NIGHT_DIM_FACTOR);
                mainColorDark  = Colorations.dimColor(enemy.secondaryColor, 1.3f * Colorations.NIGHT_DIM_FACTOR);
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
            mainColorDark = Colorations.dimColor(color, 1.5f);
        }
        
        if(enemy.getModel() == BARRIER){barColor = Colorations.barrierColor[Colorations.FRAME][Events.timeOfDay.ordinal()];}
        else if(!enemy.isDestroyed() && (enemy.getTractor() == AbilityStatusType.ACTIVE || enemy.getShootTimer() > 0 || enemy.isShielding())){barColor = Colorations.variableGreen;}
        else if(!enemy.isDestroyed() && !imagePaint && enemy.isInvincible()){barColor = Color.green;}
        else if(enemy.isMiniBoss){barColor = enemy.secondaryColor;}
        else{barColor = Colorations.enemyGray;}
        inactiveNozzleColor = Colorations.INACTIVE_NOZZLE;
        
        if(enemy.getModel() == BARRIER && Events.timeOfDay == NIGHT)
        {
            inactiveNozzleColor = Colorations.barrierColor[Colorations.NOZZLE][Events.timeOfDay.ordinal()];
        }
        
        if(enemy.isDestroyed())
        {
            barColor = Colorations.dimColor(barColor, Events.timeOfDay == NIGHT ? 1.3f * Colorations.NIGHT_DIM_FACTOR : 1);
            inactiveNozzleColor = Colorations.dimColor(inactiveNozzleColor, Events.timeOfDay == NIGHT ? 1.3f * Colorations.NIGHT_DIM_FACTOR : 1);
        }
        
        //Malen des Gegners
        if(enemy.getModel() != BARRIER)
        {
            paintVessel(	graphicsAdapter,
                            offsetX, 
                            offsetY,
                            directionX,
                            color,
                            enemy.isCloaked(), 
                            imagePaint,
                            mainColorLight, 
                            mainColorDark,
                            barColor, 
                            inactiveNozzleColor);
        }
        else
        {
            paintBarrier(	graphicsAdapter,
                            offsetX, 
                            offsetY,
                            imagePaint,
                            mainColorLight, 
                            mainColorDark,
                            barColor,
                            inactiveNozzleColor);
        }
    }
    
    private void paintVessel(GraphicsAdapter graphicsAdapter, int offsetX, int offsetY,
                             int directionX, Color color, boolean getarnt,
                             boolean imagePaint,
                             Color mainColorLight,
                             Color mainColorDark,
                             Color cannonColor,
                             Color inactiveNozzleColor)
    {
        if(enemy.getModel() == CARGO)
        {
            this.paintRoof(graphicsAdapter, cannonColor, offsetX, offsetY, directionX);
        }
        this.paintAirframe(graphicsAdapter, mainColorLight, offsetX, offsetY, directionX);
        this.paintCannon(graphicsAdapter, offsetX, offsetY, directionX, cannonColor);
        if(enemy.getModel() == TIT)
        {
            this.paintVerticalStabilizer(graphicsAdapter, offsetX, offsetY, directionX);
        }
        this.paintExhaustPipe(	graphicsAdapter, offsetX, offsetY, directionX,
            mainColorDark, inactiveNozzleColor);
        
        if(Color.red.equals(color) || !enemy.isLivingBoss())
        {
            this.paintCockpitWindow(
                graphicsAdapter,
                offsetX,
                (int)(offsetY
                    + enemy.getPaintBounds().height
                    *(enemy.getModel() == TIT ? 0.067f : 0.125f)),
                Color.red.equals(color) ? Colorations.cloakedBossEye : null,
                directionX,
                getarnt && !imagePaint);
        }
        
        // das rote Kreuz
        if(enemy.type == HEALER)
        {
            paintRedCross(
                graphicsAdapter,
                (int)( offsetX + (directionX == 1
                    ? 0.7f * enemy.getPaintBounds().width
                    : (1 - 0.7f - 0.18f) * enemy.getPaintBounds().width)),
                (int) (offsetY + 0.6f * enemy.getPaintBounds().height),
                (int) (			  0.18f * enemy.getPaintBounds().width));
        }
        
        if(SHOW_RED_FRAME){
            graphicsAdapter.setColor(Color.red);
            graphicsAdapter.drawRect(offsetX, offsetY, (int)(enemy.getWidth() - 1), (int)(enemy.getHeight() - 1));
        }
    }
    
    private void paintBarrier(GraphicsAdapter graphicsAdapter,
                              int offsetX, int offsetY,
                              boolean imagePaint,
                              Color mainColorLight,
                              Color mainColorDark,
                              Color barColor,
                              Color inactiveNozzleColor)
    {
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
    
    private void paintRotorInterior(GraphicsAdapter graphicsAdapter, Color mainColorDark,
                                    int offsetX, int offsetY)
    {
        int distanceX = (int) (BARRIER_BORDER_SIZE * enemy.getPaintBounds().width),
            distanceY = (int) (BARRIER_BORDER_SIZE * enemy.getPaintBounds().height);
        
        graphicsAdapter.setPaint(new GradientPaint(	0,
            offsetY,
            mainColorDark,
            0,
            offsetY + 0.045f*enemy.getPaintBounds().height,
            Colorations.dimColor(mainColorDark, 0.85f),
            true));
        
        graphicsAdapter.fillOval(offsetX + distanceX,
            offsetY + distanceY,
            enemy.getPaintBounds().width  - 2 * distanceX,
            enemy.getPaintBounds().height - 2 * distanceY);
    }
    
    private void paintRoof(GraphicsAdapter graphicsAdapter, Color roofColor, int offsetX,
                           int offsetY, int directionX)
    {
        graphicsAdapter.setPaint(roofColor);
        graphicsAdapter.fillRoundRect(	(int) (offsetX + (directionX == 1 ? 0.05f :  0.35f) * enemy.getPaintBounds().width),
            offsetY,
            (int) (0.6f   * enemy.getPaintBounds().width),
            (int) (0.125f * enemy.getPaintBounds().height),
            (int) (0.6f   * enemy.getPaintBounds().width),
            (int) (0.125f * enemy.getPaintBounds().height));
    }
    
    // malen des Schiffrumpfes
    private void paintAirframe(GraphicsAdapter graphicsAdapter, Color mainColorLight,
                               int offsetX, int offsetY, int directionX)
    {
        this.setAirframeColor(graphicsAdapter, offsetY, mainColorLight);
        
        if(enemy.getModel() == TIT)
        {
            graphicsAdapter.fillArc(offsetX,
                (int) (offsetY - 0.333f * enemy.getPaintBounds().height - 2),
                enemy.getPaintBounds().width,
                enemy.getPaintBounds().height, 180, 180);
            
            graphicsAdapter.fillArc((int)(offsetX + (directionX == 1 ? 0.2f * enemy.getPaintBounds().width : 0)),
                (int)(offsetY - 0.667f * enemy.getPaintBounds().height),
                (int)(			 0.8f   * enemy.getPaintBounds().width),
                (int)(			 1.667f * enemy.getPaintBounds().height), 180, 180);
        }
        else if(enemy.getModel() == CARGO)
        {
            graphicsAdapter.fillOval(	(int)(offsetX + 0.02f * enemy.getPaintBounds().width),
                (int)(offsetY + 0.1f * enemy.getPaintBounds().height),
                (int)(0.96f * enemy.getPaintBounds().width),
                (int)(0.9f  * enemy.getPaintBounds().height));
            
            graphicsAdapter.fillRect(	(int)(offsetX + (directionX == 1 ? 0.05f : 0.35f) * enemy.getPaintBounds().width),
                (int)(offsetY + 0.094f * enemy.getPaintBounds().height),
                (int)(0.6f * enemy.getPaintBounds().width),
                (int)(0.333f * enemy.getPaintBounds().height));
            
            graphicsAdapter.fillRoundRect(	(int) (offsetX + (directionX == 1 ? 0.05f : 0.35f) * enemy.getPaintBounds().width),
                (int) (offsetY + 0.031 * enemy.getPaintBounds().height),
                (int) (0.6f * enemy.getPaintBounds().width),
                (int) (0.125f * enemy.getPaintBounds().height),
                (int) (0.6f * enemy.getPaintBounds().width),
                (int) (0.125f * enemy.getPaintBounds().height));
            
            // Rückflügel
            graphicsAdapter.fillArc(	(int)(offsetX + (directionX == 1 ? 0.5f * enemy.getPaintBounds().width : 0)),
                (int)(offsetY - 0.3f * enemy.getPaintBounds().height),
                (int)(0.5f * enemy.getPaintBounds().width),
                enemy.getPaintBounds().height,
                directionX == 1 ? -32 : 155,
                57);
        }
    }
    
    private void paintVerticalStabilizer(GraphicsAdapter graphicsAdapter,
                                         int offsetX, int offsetY,
                                         int directionX)
    {
        graphicsAdapter.setPaint(this.gradientColor);
        graphicsAdapter.fillArc((int)(offsetX + (directionX == 1 ? 0.4f : 0.1f) * enemy.getPaintBounds().width),
            (int)(offsetY - 						   0.917f * enemy.getPaintBounds().height),
            (int)(0.5f * enemy.getPaintBounds().width),
            2 * enemy.getPaintBounds().height, directionX == 1 ? 0 : 160, 20);
    }
    
    private void setAirframeColor(GraphicsAdapter graphicsAdapter, int offsetY,
                                  Color mainColorLight)
    {
        this.gradientColor = new GradientPaint(
            0,
            offsetY + (enemy.getModel() == TIT ? 0.25f : 0.375f) * enemy.getPaintBounds().height,
            mainColorLight,
            0,
            offsetY + enemy.getPaintBounds().height,
            Colorations.dimColor(mainColorLight, 0.5f),
            true);
        
        graphicsAdapter.setPaint(this.gradientColor);
    }
    
    private void paintBarrierEyes(GraphicsAdapter graphicsAdapter)
    {
        paintBarrierEyes(	graphicsAdapter,
            enemy.getPaintBounds().x,
            enemy.getPaintBounds().y,
            enemy.alpha != 255
                ? Colorations.setAlpha(Colorations.variableRed, enemy.alpha)
                : Colorations.variableRed,
            false);
    }
    
    private void paintBarrierEyes(GraphicsAdapter graphicsAdapter, int x, int y, Color color, boolean imagePaint)
    {
        int borderDistance = (int)(0.85f * BARRIER_BORDER_SIZE * enemy.getPaintBounds().width),
            eyeSize = 		  (int)(	    BARRIER_EYE_SIZE    * enemy.getPaintBounds().width);
        
        graphicsAdapter.setPaint(color);
        
        graphicsAdapter.fillOval(x + borderDistance,
            y + borderDistance,
            eyeSize, eyeSize);
        
        graphicsAdapter.fillOval(x - borderDistance + enemy.getPaintBounds().width  - eyeSize,
            y - borderDistance + enemy.getPaintBounds().height - eyeSize,
            eyeSize, eyeSize);
        
        if(!imagePaint && !(enemy.getSnoozeTimer() > Enemy.SNOOZE_TIME)){graphicsAdapter.setPaint(Colorations.reversedRandomRed(color));}
        graphicsAdapter.fillOval(x + borderDistance,
            y - borderDistance + enemy.getPaintBounds().height - eyeSize,
            eyeSize, eyeSize);
        
        graphicsAdapter.fillOval(x - borderDistance + enemy.getPaintBounds().width  - eyeSize,
            y + borderDistance,
            eyeSize, eyeSize);
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
    
    private void paintTractorBeam(GraphicsAdapter graphicsAdapter, Helicopter helicopter)
    {
        GraphicalEntities.paintGlowingLine(	graphicsAdapter,
                                            enemy.getPaintBounds().x,
                                            enemy.getPaintBounds().y + 1,
                                            (int)(helicopter.getX()
                                                + (helicopter.isMovingLeft
                                                ? Helicopter.FOCAL_PNT_X_LEFT
                                                : Helicopter.FOCAL_PNT_X_RIGHT)),  // 114
                                            (int)(helicopter.getY()
                                                + Helicopter.FOCAL_PNT_Y_EXP));
    }
    
    private void paintShieldBeam(GraphicsAdapter graphicsAdapter, Enemy enemy)
    {
        GraphicalEntities.paintGlowingLine(graphicsAdapter,
                                           enemy.getPaintBounds().x + (enemy.direction.x + 1)/2 * enemy.getPaintBounds().width,
                                           enemy.getPaintBounds().y,
                                           Events.boss.getPaintBounds().x + Events.boss.getPaintBounds().width/48,
                                           Events.boss.getPaintBounds().y + Events.boss.getPaintBounds().width/48);
    }
        
    private void paintCockpitWindow(GraphicsAdapter graphicsAdapter)
    {
        paintCockpitWindow(graphicsAdapter,
            enemy.getPaintBounds().x,
            (int) (enemy.getPaintBounds().y
                + enemy.getPaintBounds().height
                *(enemy.getModel() == TIT ? 0.067f : 0.125f)),
            enemy.alpha != 255
                ? Colorations.setAlpha(Colorations.variableRed, enemy.alpha)
                : Colorations.variableRed,
            -enemy.direction.x,
            false);
    }
    
    private void paintCockpitWindow(GraphicsAdapter graphicsAdapter, int x, int y, Color color, int directionX, boolean getarnt)
    {
        this.setWindowColor(graphicsAdapter, color, getarnt);
        
        if(enemy.getModel() == TIT)
        {
            graphicsAdapter.fillArc(	(int) (x + (directionX == 1 ? 0.25f : 0.55f)
                    * enemy.getPaintBounds().width),
                y,
                (int) (0.2f   * enemy.getPaintBounds().width),
                (int) (0.267f * enemy.getPaintBounds().height),
                180,
                180);
        }
        else if(enemy.getModel() == CARGO)
        {
            graphicsAdapter.fillArc(	(int) (x + (directionX == 1 ? 0.1 : 0.6)
                    * enemy.getPaintBounds().width),
                y,
                (int) (0.3f   * enemy.getPaintBounds().width),
                (int) (0.333f * enemy.getPaintBounds().height),
                directionX == 1 ? 90 : 0,
                90);
        }
    }
    
    private void setWindowColor(GraphicsAdapter graphicsAdapter, Color color, boolean getarnt)
    {
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
        else{graphicsAdapter.setColor(color);}
    }
    
    private void setEnemy(Enemy enemy)
    {
        this.enemy = enemy;
    }
}