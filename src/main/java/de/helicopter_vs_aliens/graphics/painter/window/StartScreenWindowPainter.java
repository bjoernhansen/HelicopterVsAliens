package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.gui.button.StartScreenButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;


public class StartScreenWindowPainter extends WindowPainter
{
    private static final String
        VERSION =   "Version 1.3.4",			// Spielversion
        GAME_NAME = "Helicopter vs. Aliens",
        DEVELOPERS_NAME = "Bj\u00F6rn Hansen";
    
    private static final int
        CROSS_MAX_DISPLAY_TIME = 60; // Maximale Anzeigezeit des Block-Kreuzes (StartScreen)
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Window window)
    {
        super.paint(graphicsAdapter, window);
        paintStartScreen(graphicsAdapter, helicopter);
    }
    
    private static void paintStartScreen(GraphicsAdapter graphicsAdapter, Helicopter helicopter)
    {
        graphicsAdapter.setPaint(Colorations.gradientVariableWhite);
        graphicsAdapter.setFont(fontProvider.getPlain(80));
        graphicsAdapter.drawString(GAME_NAME, 512 - graphicsAdapter.getFontMetrics().stringWidth(GAME_NAME)/2, 85);
        
        graphicsAdapter.setColor(Color.yellow);
        graphicsAdapter.setFont(fontProvider.getBold(16));
        graphicsAdapter.drawString(VERSION, 1016 - graphicsAdapter.getFontMetrics().stringWidth(VERSION), 20);
        graphicsAdapter.setColor(Colorations.darkGray);
        graphicsAdapter.setFont(fontProvider.getItalicBold(15));
        graphicsAdapter.drawString(String.format("%s %s", Window.dictionary.developedBy(), DEVELOPERS_NAME), 505, 120);
        
        if(Window.messageTimer == 0)
        {
            graphicsAdapter.setColor(Colorations.variableYellow);
            graphicsAdapter.setFont(fontProvider.getPlain(29));
            String tempString = Window.dictionary.helicopterSelectionRequest();
            graphicsAdapter.drawString(tempString, (512 - graphicsAdapter.getFontMetrics().stringWidth(tempString)/2), 185);
        }
        else
        {
            graphicsAdapter.setColor(Colorations.golden);
            graphicsAdapter.setFont(fontProvider.getBold(16));
            
            graphicsAdapter.drawString(Window.message[0], (512 - graphicsAdapter.getFontMetrics().stringWidth(Window.message[0])/2), 160);
            graphicsAdapter.drawString(Window.message[1], (512 - graphicsAdapter.getFontMetrics().stringWidth(Window.message[1])/2), 185);
            graphicsAdapter.drawString(Window.message[2], (512 - graphicsAdapter.getFontMetrics().stringWidth(Window.message[2])/2), 210);
        }
        
        for(int i = 0; i < Window.NUMBER_OF_START_SCREEN_HELICOPTERS; i++)
        {
            if(    Events.nextHelicopterType != null
                && Events.nextHelicopterType.ordinal() == (Window.helicopterSelection +i)% HelicopterType.count())
            {
                graphicsAdapter.setColor(Color.white);
            }
            else{graphicsAdapter.setColor(Colorations.lightGray);}
            graphicsAdapter.setFont(fontProvider.getBold(20));
            
            String className = Window.dictionary.typeName(HelicopterType.getValues().get((Window.helicopterSelection +i)% HelicopterType.count()));
            int sw = graphicsAdapter.getFontMetrics().stringWidth(className);
            graphicsAdapter.drawString(
                className,
                30 + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE + (206-sw)/2,
                225 + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
            
            graphicsAdapter.setFont(new Font("Dialog", Font.BOLD, 15));
            
            HelicopterType type = HelicopterType.getValues().get((Window.helicopterSelection +i)% HelicopterType.count());
            for(int j = 0; j < 3; j++)
            {
                graphicsAdapter.drawString(
                    Window.dictionary.helicopterInfos(type).get(j),
                    29 + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
                    380 + j * 20 + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
            }
            
            if(Window.helicopterFrame[i].contains(helicopter.destination))
            {
                GraphicalEntities.paintFrame(graphicsAdapter, Window.helicopterFrame[i], Colorations.darkBlue);
            }
            
            Helicopter nextStartScreenHelicopter = Window.helicopterDummies.get(HelicopterType.getValues()
                                                                                              .get((Window.helicopterSelection + i) % HelicopterType.count()));
            HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(nextStartScreenHelicopter.getClass());
            helicopterPainter.startScreenPaint(
                graphicsAdapter,
                nextStartScreenHelicopter,
                HELICOPTER_START_SCREEN_OFFSET.x + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
                HELICOPTER_START_SCREEN_OFFSET.y + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
            // TODO destination darf keine Eigenschaft von Helicopter sein
            if(!Window.helicopterFrame[i].contains(helicopter.destination.x, helicopter.destination.y))
            {
                GraphicalEntities.paintFrame(graphicsAdapter, Window.helicopterFrame[i], Colorations.translucentBlack);
            }
            if(Events.allPlayable || HelicopterType.getValues().get((Window.helicopterSelection + i)% HelicopterType.count()).isUnlocked())
            {
                paintTickMark(graphicsAdapter, i);
            }
        }
        
        // die Buttons
        StartScreenButtonType.getValues()
                             .forEach(buttonType -> Window.buttons.get(buttonType).paint(graphicsAdapter));
        
        // die grünen Pfeile
        for(int i = 0; i < 2; i++)
        {
            if(Window.triangles[i].contains(helicopter.destination)){graphicsAdapter.setColor(Color.green);}
            else{graphicsAdapter.setColor(Colorations.arrowGreen);}
            graphicsAdapter.fillPolygon(Window.triangles[i]);
            graphicsAdapter.setColor(Colorations.darkArrowGreen);
            graphicsAdapter.drawPolygon(Window.triangles[i]);
        }
        
        if(Window.crossTimer > 0)
        {
            int alpha = (int)(255*(((double)CROSS_MAX_DISPLAY_TIME- Window.crossTimer)/(CROSS_MAX_DISPLAY_TIME/2)));
            if(Window.crossTimer < CROSS_MAX_DISPLAY_TIME/2){graphicsAdapter.setColor(Color.red);}
            else{graphicsAdapter.setColor(Colorations.setAlpha(Color.red, alpha));}
            // graphicsAdapter.fill(Window.cross);
            graphicsAdapter.fillPolygon(Window.cross);
            if(Window.crossTimer < CROSS_MAX_DISPLAY_TIME/2){graphicsAdapter.setColor(Colorations.red);}
            else{graphicsAdapter.setColor(Colorations.setAlpha(Colorations.red, alpha));}
            graphicsAdapter.setStroke(new BasicStroke(2));
            graphicsAdapter.drawPolygon(Window.cross);
            graphicsAdapter.setStroke(new BasicStroke(1));
        }
    }
    
    private static void paintTickMark(GraphicsAdapter graphicsAdapter, int i)
    {
        // TODO diese boundaries als Konstante festlegen
        int x = 210;
        int y = 323;
        int w = 15;
        int h = 20;
        
        GraphicalEntities.paintGlowingLine( graphicsAdapter,
                                            x + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
                                            y + h/2 + Window.START_SCREEN_HELICOPTER_OFFSET_Y,
                                            x + w/3 + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
                                            y + h + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
    
        GraphicalEntities.paintGlowingLine( graphicsAdapter,
                                            x + w + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
                                            y + Window.START_SCREEN_HELICOPTER_OFFSET_Y,
                                            x + w/3 + Window.START_SCREEN_OFFSET_X + i * Window.HELICOPTER_DISTANCE,
                                            y + h + Window.START_SCREEN_HELICOPTER_OFFSET_Y);
    }
}
