package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.model.enemy.AbilityStatusType;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;
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
    public static boolean
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
    public void paint(Graphics2D g2d, Enemy enemy)
    {
        setEnemy(enemy);
        Helicopter helicopter = Controller.getInstance().getHelicopter();
        boolean cloaked = enemy.getCloakingTimer() > Enemy.CLOAKING_TIME && enemy.getCloakingTimer() <= Enemy.CLOAKING_TIME + Enemy.CLOAKED_TIME;
        int g2DSel = enemy.direction.x == -1 ? 0 : 1;
        
        if(!cloaked)
        {
            if(enemy.isInvincible())
            {
                this.paintImage(g2d, enemy, -enemy.direction.x, Colorations.variableGreen, false);
            }
            else if(enemy.alpha != 255)
            {
                if(enemy.alpha > 51 || !helicopter.canDetectCloakedVessels())
                {
                    Enemy.scales[3] = ((float)enemy.alpha)/255;
                    g2d.drawImage(	enemy.getImage()[g2DSel],
                        new RescaleOp(Enemy.scales, Enemy.offsets, null),
                        enemy.getPaintBounds().x - (enemy.direction.x == -1 ? enemy.getPaintBounds().width/36 : 0),
                        enemy.getPaintBounds().y - enemy.getPaintBounds().height/4);
                }
                else
                {
                    g2d.drawImage(enemy.getImage()[g2DSel + 2],
                        enemy.getPaintBounds().x - (enemy.direction.x == -1 ? enemy.getPaintBounds().width/36 : 0),
                        enemy.getPaintBounds().y - enemy.getPaintBounds().height/4, null);
                }
            }
            else
            {
                g2d.drawImage(enemy.getImage()[g2DSel],
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
                
                this.paintCannon(g2d, enemy.getPaintBounds().x, enemy.getPaintBounds().y, -enemy.direction.x, inputColorRoof);
            }
            
            // blinkende Scheibe von Bossen und Mini-Bossen bzw. Eyes bei Hindernissen
            if(enemy.hasGlowingEyes())
            {
                if(enemy.model != BARRIER){this.paintCockpitWindow(g2d);}
                else{this.paintBarrierEyes(g2d);}
            }
            
            // Auspuff
            if(!(enemy.isDestroyed() || enemy.stunningTimer > 0))
            {
                int temp = 63 - (((int)(2 + 0.1f * Math.abs(enemy.getSpeedLevel().getX())) * enemy.getLifetime())%32); //d
                Color colorTemp = new Color(255, 192+temp, 129+temp, enemy.alpha);
                this.paintExhaustPipe(g2d, colorTemp);
            }
            
            // die Schild- und Traktorstrahlen
            if(enemy.getTractor() == AbilityStatusType.ACTIVE){this.paintTractorBeam(g2d, helicopter);}
            else if(enemy.type == FINAL_BOSS)
            {
                for(int servantType = Enemy.id(SMALL_SHIELD_MAKER); servantType <= Enemy.id(BIG_SHIELD_MAKER); servantType++)
                {
                    Optional.ofNullable(enemy.getOperatorServant(servantType))
                            .filter(Enemy::isShielding)
                            .ifPresent(servant -> this.paintShieldBeam(g2d, servant));
                }
            }
            
            if(enemy.model == BARRIER && !enemy.isDestroyed())
            {
                this.paintRotor(g2d);
            }
        }
        else if(helicopter.canDetectCloakedVessels())
        {
            g2d.drawImage(	enemy.getImage()[g2DSel + 2],
                enemy.getPaintBounds().x - (enemy.direction.x == -1 ? enemy.getPaintBounds().width/36 : 0),
                enemy.getPaintBounds().y - enemy.getPaintBounds().height/4,
                null);
        }
        
        //zu Testzwecken:
        if(SHOW_BARRIER_TESTING_INFO)
        {
            g2d.setColor(Color.red);
            if(enemy.model == BARRIER)
            {
                g2d.drawString(   "Borrow: " + enemy.borrowTimer + " ; "
                        
                        + "Stun: "   + enemy.stunningTimer + " ; "
                        + "Snooze: " + enemy.getSnoozeTimer() + " ; ",
                    (int)enemy.getBounds().getX(),
                    (int)enemy.getBounds().getY());
            }
        }
    }
    
    private void paintRotor(Graphics2D g2d)
    {
        paintRotor(g2d, enemy.getPaintBounds().x, enemy.getPaintBounds().y);
    }
    
    private void paintRotor(Graphics2D g2d, int x, int y)
    {
        HelicopterPainter.paintRotor(	g2d,
            !enemy.isDestroyed()
                ?(Colorations.setAlpha(Colorations.barrierColor[enemy.getRotorColor()][Events.timeOfDay.ordinal()], enemy.alpha))
                : Colorations.dimColor(Colorations.barrierColor[enemy.getRotorColor()][Events.timeOfDay.ordinal()], Colorations.DESTRUCTION_DIM_FACTOR),
            x, y, enemy.getPaintBounds().width, enemy.getPaintBounds().height, 5, (enemy.getSpeedLevel().equals(Enemy.ZERO_SPEED) ? (enemy.getSnoozeTimer() <= Enemy.SNOOZE_TIME ? 3 : 0) : 8) * (enemy.isClockwiseBarrier() ? -1 : 1) * enemy.getLifetime()%360,
            24, BARRIER_BORDER_SIZE, enemy.getSnoozeTimer() == 0);
        this.paintBarrierCannon(g2d, x, y);
    }
    
    private void paintBarrierCannon(Graphics2D g2d, int x, int y)
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
            g2d.setColor(tempColor);
            
            distanceX = (int) ((0.45f + i * 0.01f) * enemy.getPaintBounds().width);
            distanceY = (int) ((0.45f + i * 0.01f) * enemy.getPaintBounds().height);
            
            g2d.fillOval(x + distanceX,
                y + distanceY,
                enemy.getPaintBounds().width  - 2*distanceX,
                enemy.getPaintBounds().height - 2*distanceY);
        }
    }
    
    private void paintCannon(Graphics2D g2d, int x, int y, int directionX, Color inputColor)
    {
        if(enemy.model == TIT)
        {
            paintBar(	g2d,
                x,	y,
                enemy.getPaintBounds().width, enemy.getPaintBounds().height,
                0.02f, 0.007f, 0.167f, 0.04f, 0.6f,
                directionX, true, inputColor);
        }
        else if(enemy.model == CARGO)
        {
            paintBar(	g2d,
                x, (int) (y + 0.48f * enemy.getPaintBounds().height),
                enemy.getPaintBounds().width, enemy.getPaintBounds().height,
                0, 0, 0.1f, 0.04f, 0.6f,
                directionX, true, inputColor);
        }
    }
    
    private void paintBarFrame(Graphics2D g2d, int x, int y,
                               float thicknessFactor,
                               float shift, float centerShift,
                               float dimFactor,
                               Color inputColor, Color backgroundColor,
                               boolean imagePaint)
    {
        if(backgroundColor != null)
        {
            g2d.setPaint(new GradientPaint(	0,
                y,
                backgroundColor,
                0,
                y + 0.3f*thicknessFactor*enemy.getPaintBounds().height,
                Colorations.dimColor(backgroundColor, 0.85f),
                true));
            
            g2d.fillRect(x + (int)(thicknessFactor/2 * enemy.getPaintBounds().width),
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
            paintBar(	g2d,
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
            paintBar(	g2d,
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
            paintBar(	g2d,
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
            paintBar(	g2d,
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
    
    private static void paintBar(Graphics2D g2d,
                                 int x, int y,
                                 int width, int height,
                                 float thicknessFactor,
                                 float rounding,
                                 float dimFactor,
                                 boolean horizontal,
                                 Color inputColor)
    {
        paintBar(	g2d,
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
    
    private static void paintBar(Graphics2D g2d,
                                 int x, int y,
                                 int width, int height,
                                 float xShiftLeft, float xShiftRight,
                                 float thicknessFactor, float rounding,
                                 float dimFactor, int directionX,
                                 boolean horizontal, Color inputColor)
    {
        g2d.setPaint( new GradientPaint(	(int) (horizontal ? 0 : x + 0.5f * thicknessFactor * width),
            (int) (horizontal ?     y + 0.5f * thicknessFactor * height : 0),
            inputColor,
            (int) (horizontal ? 0 : x + 1.0f * thicknessFactor * width),
            (int) (horizontal ?     y + 1.0f * thicknessFactor * height : 0),
            Colorations.dimColor(inputColor, dimFactor),
            true));
        
        g2d.fillRoundRect(	(int) (x - (directionX == 1 ? xShiftLeft : xShiftRight) * width),
            y,
            (int) (	horizontal ? (1 + xShiftLeft + xShiftRight) * width : thicknessFactor * width),
            (int) (	horizontal ? thicknessFactor * height : (1 + xShiftLeft + xShiftRight) * height ),
            (int) (	horizontal ? rounding * width : thicknessFactor * width),
            (int) (	horizontal ? thicknessFactor * height : rounding * height) );
    }
    
    // malen der Seitenflügel mit Antriebsdüse
    private void paintExhaustPipe(Graphics2D g2d, Color color4)
    {
        paintExhaustPipe(g2d,
            enemy.getPaintBounds().x,
            enemy.getPaintBounds().y,
            -enemy.direction.x,
            null,
            color4);
    }
    
    // TODO bessere Namen für Bezeichner color2 , color 4
    private void paintExhaustPipe(Graphics2D g2d, int x, int y, int directionX, Color color2, Color color4)
    {
        if(enemy.model == TIT)
        {
            paintEngine(g2d, x, y, 0.45f, 0.27f, 0.5f, 0.4f, directionX, color2, color4);
        }
        else if(enemy.model == CARGO)
        {
            paintEngine(g2d, x, y, 0.45f, 0.17f, 0.45f, 0.22f, directionX, color2, color4);
            paintEngine(g2d, x, y, 0.45f, 0.17f, 0.25f, 0.70f, directionX, color2, color4);
        }
        else if(enemy.model == BARRIER)
        {
            paintBarFrame(g2d, enemy.getPaintBounds().x, enemy.getPaintBounds().y,
                0.07f, 0.35f, 0.04f, 0.7f, color4, null, false);
        }
    }
    
    private void paintEngine(Graphics2D g2d,
                             int x, int y,
                             float width, float height,
                             float xShift, float yShift,
                             int directionX,
                             Color color2, Color color4)
    {
        if(color2 != null)
        {
            paintPipe(g2d, x, y, width, height, xShift, 				  yShift, directionX, color2, false);
        }
        paintPipe(g2d, x, y, 0.05f, height, xShift + width - 0.05f, yShift, directionX, color4, true);
    }
    
    private void paintPipe(Graphics2D g2d,
                           int x, int y,
                           float width, float height,
                           float xShift, float yShift,
                           int directionX, Color color, boolean isExhaust)
    {
        g2d.setPaint(new GradientPaint(	0,
            y + (yShift + 0.05f)  * enemy.getPaintBounds().height,
            color,
            0,
            y + (yShift + height) * enemy.getPaintBounds().height,
            Colorations.dimColor(color, 0.5f),
            true));
        
        g2d.fillRoundRect(	(int) (x + (directionX == 1
                ? xShift
                : 1f - xShift - width)	* enemy.getPaintBounds().width),
            (int) (y + 	yShift 			   	* enemy.getPaintBounds().height),
            (int) (		width  				   	* enemy.getPaintBounds().width),
            (int) (		height  			   	* enemy.getPaintBounds().height),
            (int) ((isExhaust ? 0f : height/2) * enemy.getPaintBounds().width),
            (int) ((isExhaust ? 0f : height  ) * enemy.getPaintBounds().height)  );
    }
    
    public void paintImage(Graphics2D g2d, Enemy enemy, int directionX, Color color, boolean imagePaint)
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
                mainColorLight = Colorations.dimColor(enemy.farbe1, 1.3f * Colorations.NIGHT_DIM_FACTOR);
                mainColorDark  = Colorations.dimColor(enemy.farbe2, 1.3f * Colorations.NIGHT_DIM_FACTOR);
            }
            else
            {
                mainColorLight = enemy.farbe1;
                mainColorDark  = enemy.farbe2;
            }
        }
        else
        {
            mainColorLight = color;
            mainColorDark = Colorations.dimColor(color, 1.5f);
        }
        
        if(enemy.model == BARRIER){barColor = Colorations.barrierColor[Colorations.FRAME][Events.timeOfDay.ordinal()];}
        else if(!enemy.isDestroyed() && (enemy.getTractor() == AbilityStatusType.ACTIVE || enemy.getShootTimer() > 0 || enemy.isShielding())){barColor = Colorations.variableGreen;}
        else if(!enemy.isDestroyed() && !imagePaint && enemy.isInvincible()){barColor = Color.green;}
        else if(enemy.isMiniBoss){barColor = enemy.farbe2;}
        else{barColor = Colorations.enemyGray;}
        inactiveNozzleColor = Colorations.INACTIVE_NOZZLE;
        
        if(enemy.model == BARRIER && Events.timeOfDay == NIGHT)
        {
            inactiveNozzleColor = Colorations.barrierColor[Colorations.NOZZLE][Events.timeOfDay.ordinal()];
        }
        
        if(enemy.isDestroyed())
        {
            barColor = Colorations.dimColor(barColor, Events.timeOfDay == NIGHT ? 1.3f * Colorations.NIGHT_DIM_FACTOR : 1);
            inactiveNozzleColor = Colorations.dimColor(inactiveNozzleColor, Events.timeOfDay == NIGHT ? 1.3f * Colorations.NIGHT_DIM_FACTOR : 1);
        }
        
        //Malen des Gegners
        if(enemy.model != BARRIER)
        {
            paintVessel(	g2d,
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
            paintBarrier(	g2d,
                            offsetX, 
                            offsetY,
                            imagePaint,
                            mainColorLight, 
                            mainColorDark,
                            barColor,
                            inactiveNozzleColor);
        }
    }
    
    private void paintVessel(Graphics2D g2d, int offsetX, int offsetY,
                             int directionX, Color color, boolean getarnt,
                             boolean imagePaint,
                             Color mainColorLight,
                             Color mainColorDark,
                             Color cannonColor,
                             Color inactiveNozzleColor)
    {
        if(enemy.model == CARGO)
        {
            this.paintRoof(g2d, cannonColor, offsetX, offsetY, directionX);
        }
        this.paintAirframe(g2d, mainColorLight, offsetX, offsetY, directionX);
        this.paintCannon(g2d, offsetX, offsetY, directionX, cannonColor);
        if(enemy.model == TIT)
        {
            this.paintVerticalStabilizer(g2d, offsetX, offsetY, directionX);
        }
        this.paintExhaustPipe(	g2d, offsetX, offsetY, directionX,
            mainColorDark, inactiveNozzleColor);
        
        if(Color.red.equals(color) || !enemy.isLivingBoss())
        {
            this.paintCockpitWindow(
                g2d,
                offsetX,
                (int)(offsetY
                    + enemy.getPaintBounds().height
                    *(enemy.model == TIT ? 0.067f : 0.125f)),
                Color.red.equals(color) ? Colorations.cloakedBossEye : null,
                directionX,
                getarnt && !imagePaint);
        }
        
        // das rote Kreuz
        if(enemy.type == HEALER)
        {
            paintRedCross(
                g2d,
                (int)( offsetX + (directionX == 1
                    ? 0.7f * enemy.getPaintBounds().width
                    : (1 - 0.7f - 0.18f) * enemy.getPaintBounds().width)),
                (int) (offsetY + 0.6f * enemy.getPaintBounds().height),
                (int) (			  0.18f * enemy.getPaintBounds().width));
        }
        
        if(SHOW_RED_FRAME){
            g2d.setColor(Color.red);
            g2d.drawRect(offsetX, offsetY, (int)(enemy.getBounds().getWidth() - 1), (int)(enemy.getBounds().getHeight() - 1));
        }
    }
    
    private void paintBarrier(Graphics2D g2d,
                              int offsetX, int offsetY,
                              boolean imagePaint,
                              Color mainColorLight,
                              Color mainColorDark,
                              Color barColor,
                              Color inactiveNozzleColor)
    {
        // Rahmen & Antriebsbalken
        paintBarFrame(g2d, offsetX, offsetY, 0.15f, 0f,    0f,    0.5f, barColor, mainColorLight, true);
        paintBarFrame(g2d, offsetX, offsetY, 0.07f, 0.35f, 0.04f, 0.7f, inactiveNozzleColor, null, true);
        
        // "Augen"
        this.paintBarrierEyes(g2d,
            offsetX,
            offsetY,
            Colorations.barrierColor[Colorations.EYES][Events.timeOfDay.ordinal()],
            imagePaint);
        
        // Turbinen-Innenraum
        this.paintRotorInterior(g2d, mainColorDark, offsetX, offsetY );
        
        if(enemy.isDestroyed()){this.paintRotor(g2d, offsetX, offsetY);}
    }
    
    private void paintRotorInterior(Graphics2D g2d, Color mainColorDark,
                                    int offsetX, int offsetY)
    {
        int distanceX = (int) (BARRIER_BORDER_SIZE * enemy.getPaintBounds().width),
            distanceY = (int) (BARRIER_BORDER_SIZE * enemy.getPaintBounds().height);
        
        g2d.setPaint(new GradientPaint(	0,
            offsetY,
            mainColorDark,
            0,
            offsetY + 0.045f*enemy.getPaintBounds().height,
            Colorations.dimColor(mainColorDark, 0.85f),
            true));
        
        g2d.fillOval(offsetX + distanceX,
            offsetY + distanceY,
            enemy.getPaintBounds().width  - 2 * distanceX,
            enemy.getPaintBounds().height - 2 * distanceY);
    }
    
    private void paintRoof(Graphics2D g2d, Color roofColor, int offsetX,
                           int offsetY, int directionX)
    {
        g2d.setPaint(roofColor);
        g2d.fillRoundRect(	(int) (offsetX + (directionX == 1 ? 0.05f :  0.35f) * enemy.getPaintBounds().width),
            offsetY,
            (int) (0.6f   * enemy.getPaintBounds().width),
            (int) (0.125f * enemy.getPaintBounds().height),
            (int) (0.6f   * enemy.getPaintBounds().width),
            (int) (0.125f * enemy.getPaintBounds().height));
    }
    
    // malen des Schiffrumpfes
    private void paintAirframe(Graphics2D g2d, Color mainColorLight,
                               int offsetX, int offsetY, int directionX)
    {
        this.setAirframeColor(g2d, offsetY, mainColorLight);
        
        if(enemy.model == TIT)
        {
            g2d.fillArc(offsetX,
                (int) (offsetY - 0.333f * enemy.getPaintBounds().height - 2),
                enemy.getPaintBounds().width,
                enemy.getPaintBounds().height, 180, 180);
            
            g2d.fillArc((int)(offsetX + (directionX == 1 ? 0.2f * enemy.getPaintBounds().width : 0)),
                (int)(offsetY - 0.667f * enemy.getPaintBounds().height),
                (int)(			 0.8f   * enemy.getPaintBounds().width),
                (int)(			 1.667f * enemy.getPaintBounds().height), 180, 180);
        }
        else if(enemy.model == CARGO)
        {
            g2d.fillOval(	(int)(offsetX + 0.02f * enemy.getPaintBounds().width),
                (int)(offsetY + 0.1f * enemy.getPaintBounds().height),
                (int)(0.96f * enemy.getPaintBounds().width),
                (int)(0.9f  * enemy.getPaintBounds().height));
            
            g2d.fillRect(	(int)(offsetX + (directionX == 1 ? 0.05f : 0.35f) * enemy.getPaintBounds().width),
                (int)(offsetY + 0.094f * enemy.getPaintBounds().height),
                (int)(0.6f * enemy.getPaintBounds().width),
                (int)(0.333f * enemy.getPaintBounds().height));
            
            g2d.fillRoundRect(	(int) (offsetX + (directionX == 1 ? 0.05f : 0.35f) * enemy.getPaintBounds().width),
                (int) (offsetY + 0.031 * enemy.getPaintBounds().height),
                (int) (0.6f * enemy.getPaintBounds().width),
                (int) (0.125f * enemy.getPaintBounds().height),
                (int) (0.6f * enemy.getPaintBounds().width),
                (int) (0.125f * enemy.getPaintBounds().height));
            
            // Rückflügel
            g2d.fillArc(	(int)(offsetX + (directionX == 1 ? 0.5f * enemy.getPaintBounds().width : 0)),
                (int)(offsetY - 0.3f * enemy.getPaintBounds().height),
                (int)(0.5f * enemy.getPaintBounds().width),
                enemy.getPaintBounds().height,
                directionX == 1 ? -32 : 155,
                57);
        }
    }
    
    private void paintVerticalStabilizer(Graphics2D g2d,
                                         int offsetX, int offsetY,
                                         int directionX)
    {
        g2d.setPaint(this.gradientColor);
        g2d.fillArc((int)(offsetX + (directionX == 1 ? 0.4f : 0.1f) * enemy.getPaintBounds().width),
            (int)(offsetY - 						   0.917f * enemy.getPaintBounds().height),
            (int)(0.5f * enemy.getPaintBounds().width),
            2 * enemy.getPaintBounds().height, directionX == 1 ? 0 : 160, 20);
    }
    
    private void setAirframeColor(Graphics2D g2d, int offsetY,
                                  Color mainColorLight)
    {
        this.gradientColor = new GradientPaint(
            0,
            offsetY + (enemy.model == TIT ? 0.25f : 0.375f) * enemy.getPaintBounds().height,
            mainColorLight,
            0,
            offsetY + enemy.getPaintBounds().height,
            Colorations.dimColor(mainColorLight, 0.5f),
            true);
        
        g2d.setPaint(this.gradientColor);
    }
    
    private void paintBarrierEyes(Graphics2D g2d)
    {
        paintBarrierEyes(	g2d,
            enemy.getPaintBounds().x,
            enemy.getPaintBounds().y,
            enemy.alpha != 255
                ? Colorations.setAlpha(Colorations.variableRed, enemy.alpha)
                : Colorations.variableRed,
            false);
    }
    
    private void paintBarrierEyes(Graphics2D g2d, int x, int y, Color color, boolean imagePaint)
    {
        int borderDistance = (int)(0.85f * BARRIER_BORDER_SIZE * enemy.getPaintBounds().width),
            eyeSize = 		  (int)(	    BARRIER_EYE_SIZE    * enemy.getPaintBounds().width);
        
        g2d.setPaint(color);
        
        g2d.fillOval(x + borderDistance,
            y + borderDistance,
            eyeSize, eyeSize);
        
        g2d.fillOval(x - borderDistance + enemy.getPaintBounds().width  - eyeSize,
            y - borderDistance + enemy.getPaintBounds().height - eyeSize,
            eyeSize, eyeSize);
        
        if(!imagePaint && !(enemy.getSnoozeTimer() > Enemy.SNOOZE_TIME)){g2d.setPaint(Colorations.reversedRandomRed(color));}
        g2d.fillOval(x + borderDistance,
            y - borderDistance + enemy.getPaintBounds().height - eyeSize,
            eyeSize, eyeSize);
        
        g2d.fillOval(x - borderDistance + enemy.getPaintBounds().width  - eyeSize,
            y + borderDistance,
            eyeSize, eyeSize);
    }
    
    private static void paintRedCross(Graphics2D g2d, int x, int y, int height)
    {
        g2d.setColor(Color.red);
        g2d.setStroke(new BasicStroke(height/5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2d.drawLine(x + height/2, y + height/5, x + height/2, y + (4 * height)/5);
        g2d.drawLine(x + height/5, y + height/2, x + (4 * height)/5, y + height/2);
        g2d.setStroke(new BasicStroke(1));
        //g2d.drawRect(x, y, height, height);
    }
    
    private void paintTractorBeam(Graphics2D g2d, Helicopter helicopter)
    {
        GraphicalEntities.paintGlowingLine(	g2d,
                                            enemy.getPaintBounds().x,
                                            enemy.getPaintBounds().y + 1,
                                            (int)(helicopter.getBounds().getX()
                                                + (helicopter.isMovingLeft
                                                ? Helicopter.FOCAL_PNT_X_LEFT
                                                : Helicopter.FOCAL_PNT_X_RIGHT)),  // 114
                                            (int)(helicopter.getBounds().getY()
                                                + Helicopter.FOCAL_PNT_Y_EXP));
    }
    
    private void paintShieldBeam(Graphics2D g2d, Enemy enemy)
    {
        GraphicalEntities.paintGlowingLine(g2d,
                                           enemy.getPaintBounds().x + (enemy.direction.x + 1)/2 * enemy.getPaintBounds().width,
                                           enemy.getPaintBounds().y,
                                           Events.boss.getPaintBounds().x + Events.boss.getPaintBounds().width/48,
                                           Events.boss.getPaintBounds().y + Events.boss.getPaintBounds().width/48);
    }
        
    private void paintCockpitWindow(Graphics2D g2d)
    {
        paintCockpitWindow(g2d,
            enemy.getPaintBounds().x,
            (int) (enemy.getPaintBounds().y
                + enemy.getPaintBounds().height
                *(enemy.model == TIT ? 0.067f : 0.125f)),
            enemy.alpha != 255
                ? Colorations.setAlpha(Colorations.variableRed, enemy.alpha)
                : Colorations.variableRed,
            -enemy.direction.x,
            false);
    }
    
    private void paintCockpitWindow(Graphics2D g2d, int x, int y, Color color, int directionX, boolean getarnt)
    {
        this.setWindowColor(g2d, color, getarnt);
        
        if(enemy.model == TIT)
        {
            g2d.fillArc(	(int) (x + (directionX == 1 ? 0.25f : 0.55f)
                    * enemy.getPaintBounds().width),
                y,
                (int) (0.2f   * enemy.getPaintBounds().width),
                (int) (0.267f * enemy.getPaintBounds().height),
                180,
                180);
        }
        else if(enemy.model == CARGO)
        {
            g2d.fillArc(	(int) (x + (directionX == 1 ? 0.1 : 0.6)
                    * enemy.getPaintBounds().width),
                y,
                (int) (0.3f   * enemy.getPaintBounds().width),
                (int) (0.333f * enemy.getPaintBounds().height),
                directionX == 1 ? 90 : 0,
                90);
        }
    }
    
    private void setWindowColor(Graphics2D g2d, Color color, boolean getarnt)
    {
        if(color == null && !getarnt)
        {
            g2d.setColor(enemy.isLivingBoss()
                         ? (enemy.alpha == 255
                             ? Colorations.variableRed
                             : Colorations.setAlpha(Colorations.variableRed, enemy.alpha))
                         : (enemy.alpha == 255
                             ? Colorations.windowBlue
                             : Colorations.setAlpha(Colorations.windowBlue, enemy.alpha)));
        }
        else{g2d.setColor(color);}
    }
    
    private void setEnemy(Enemy enemy)
    {
        this.enemy = enemy;
    }
}