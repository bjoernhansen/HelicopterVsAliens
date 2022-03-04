package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.gui.button.StartScreenButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;

public class StartScreenWindowPainter extends WindowPainter
{
    private static final String
        VERSION =   "Version 1.3.4",			// Spielversion
        GAME_NAME = "Helicopter vs. Aliens",
        DEVELOPERS_NAME = "Björn Hansen";
    
    private static final int
        CROSS_MAX_DISPLAY_TIME = 60; // Maximale Anzeigezeit des Block-Kreuzes (StartScreen)
    
    @Override
    public void paint(Graphics2D g2d, Window window)
    {
        super.paint(g2d, window);
        paintStartScreen(g2d, helicopter);
    }
    
    private static void paintStartScreen(Graphics2D g2d, Helicopter helicopter)
    {
        g2d.setPaint(Colorations.gradientVariableWhite);
        g2d.setFont(fontProvider.getPlain(80));
        g2d.drawString(GAME_NAME, 512 - g2d.getFontMetrics().stringWidth(GAME_NAME)/2, 85);
        
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(16));
        g2d.drawString(VERSION, 1016 - g2d.getFontMetrics().stringWidth(VERSION), 20);
        g2d.setColor(Colorations.darkGray);
        g2d.setFont(fontProvider.getItalicBold(15));
        g2d.drawString(String.format("%s %s", Window.dictionary.developedBy(), DEVELOPERS_NAME), 505, 120);
        
        if(Window.messageTimer == 0)
        {
            g2d.setColor(Colorations.variableYellow);
            g2d.setFont(fontProvider.getPlain(29));
            String tempString = Window.dictionary.helicopterSelectionRequest();
            g2d.drawString(tempString, (512 - g2d.getFontMetrics().stringWidth(tempString)/2), 185);
        }
        else
        {
            g2d.setColor(Colorations.golden);
            g2d.setFont(fontProvider.getBold(16));
            
            g2d.drawString(Window.message[0], (512 - g2d.getFontMetrics().stringWidth(Window.message[0])/2), 160);
            g2d.drawString(Window.message[1], (512 - g2d.getFontMetrics().stringWidth(Window.message[1])/2), 185);
            g2d.drawString(Window.message[2], (512 - g2d.getFontMetrics().stringWidth(Window.message[2])/2), 210);
        }
        
        for(int i = 0; i < Window.NUMBER_OF_START_SCREEN_HELICOPTERS; i++)
        {
            if(    Events.nextHelicopterType != null
                && Events.nextHelicopterType.ordinal() == (Window.helicopterSelection +i)% HelicopterType.size())
            {
                g2d.setColor(Color.white);
            }
            else{g2d.setColor(Colorations.lightGray);}
            g2d.setFont(fontProvider.getBold(20));
            
            String className = Window.dictionary.typeName(HelicopterType.getValues().get((Window.helicopterSelection +i)% HelicopterType.size()));
            int sw = g2d.getFontMetrics().stringWidth(className);
            g2d.drawString(
                className,
                30 + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE + (206-sw)/2,
                225 + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
            
            g2d.setFont(new Font("Dialog", Font.BOLD, 15));
            
            HelicopterType type = HelicopterType.getValues().get((Window.helicopterSelection +i)% HelicopterType.size());
            for(int j = 0; j < 3; j++)
            {
                g2d.drawString(
                    Window.dictionary.helicopterInfos(type).get(j),
                    29 + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
                    380 + j * 20 + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
            }
            
            if(Window.helicopterFrame[i].contains(helicopter.destination))
            {
                paintFrame(g2d, Window.helicopterFrame[i], Colorations.darkBlue);
            }
            
            Helicopter nextStartScreenHelicopter = Window.helicopterDummies.get(HelicopterType.getValues()
                                                                                              .get((Window.helicopterSelection + i) % HelicopterType.size()));
            HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(nextStartScreenHelicopter.getClass());
            helicopterPainter.startScreenPaint(
                g2d,
                nextStartScreenHelicopter,
                HELICOPTER_START_SCREEN_OFFSET.x + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
                HELICOPTER_START_SCREEN_OFFSET.y + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
            // TODO destination darf keine Eigenschaft von Helicopter sein
            if(!Window.helicopterFrame[i].contains(helicopter.destination.x, helicopter.destination.y))
            {
                paintFrame(g2d, Window.helicopterFrame[i], Colorations.translucentBlack);
            }
            if(Events.allPlayable || HelicopterType.getValues().get((Window.helicopterSelection + i)% HelicopterType.size()).isUnlocked())
            {
                // TODO diese boundaries als Konstante festlegen
                paintTickMark(g2d, i, 210, 323, 15, 20);
            }
        }
        
        // die Buttons
        StartScreenButtonType.getValues()
                             .forEach(buttonType -> Window.buttons.get(buttonType).paint(g2d));
        
        // die grünen Pfeile
        for(int i = 0; i < 2; i++)
        {
            if(Window.triangle[i].contains(helicopter.destination.x, helicopter.destination.y)){g2d.setColor(Color.green);}
            else{g2d.setColor(Colorations.arrowGreen);}
            g2d.fillPolygon(Window.triangle[i]);
            g2d.setColor(Colorations.darkArrowGreen);
            g2d.drawPolygon(Window.triangle[i]);
        }
        
        if(Window.crossTimer > 0)
        {
            int alpha = (int)(255*(((double)CROSS_MAX_DISPLAY_TIME- Window.crossTimer)/(CROSS_MAX_DISPLAY_TIME/2)));
            if(Window.crossTimer < CROSS_MAX_DISPLAY_TIME/2){g2d.setColor(Color.red);}
            else{g2d.setColor(Colorations.setAlpha(Color.red, alpha));}
            g2d.fill(Window.cross);
            if(Window.crossTimer < CROSS_MAX_DISPLAY_TIME/2){g2d.setColor(Colorations.red);}
            else{g2d.setColor(Colorations.setAlpha(Colorations.red, alpha));}
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(Window.cross);
            g2d.setStroke(new BasicStroke(1));
        }
    }
    
    private static void paintTickMark(Graphics2D g2d, int i, int x, int y, int w, int h)
    {
        Enemy.paintEnergyBeam(g2d,
            x + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
            y + h/2 + Window.START_SCREEN_HELICOPTER_OFFSET_Y,
            x + w/3 + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
            y + h + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
        
        Enemy.paintEnergyBeam(g2d,
            x + w + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
            y + Window.START_SCREEN_HELICOPTER_OFFSET_Y,
            x + w/3 + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
            y + h + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
    }
    
    private static void paintFrame(Graphics2D g2d, Rectangle frame, Color filledColor)
    {
        paintFrame(g2d, frame.x, frame.y, frame.width, frame.height, filledColor);
    }
}
