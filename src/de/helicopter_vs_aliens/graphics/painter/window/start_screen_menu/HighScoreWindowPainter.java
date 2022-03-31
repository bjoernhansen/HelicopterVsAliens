package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.score.HighScoreColumnType;
import de.helicopter_vs_aliens.score.HighScoreType;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicInteger;

import static de.helicopter_vs_aliens.control.Events.MAXIMUM_LEVEL;

public class HighScoreWindowPainter extends StartScreenMenuWindowPainter
{
    // TODO Konstanten deklarieren
    private static final int
        TOP_ROW_SHIFT_X = -10,
        TOP_ROW_Y = 104,
    
        LEFT_COLUMN_X = 107,
        
        
        COLUMN_DISTANCE = 114,
        ROW_DISTANCE = 21;
    
    
    @Override
    void paintStartScreenMenu(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter)
    {
        super.paintStartScreenMenu(g2d, graphics2DAdapter);
        
        if(Window.page == StartScreenMenuButtonType.BUTTON_1)
        {
            String tempString = "";
            // TODO Magic number entfernen
            for(int i = 0; i < 6; i++)
            {
                // TODO über HelicopterTypes iterieren
                for(int j = 0; j < HelicopterType.size() + 1; j++)
                {
                    if(j == 0 && i!=0)
                    {
                        g2d.setColor(Colorations.golden);
                        tempString = "Boss " + i;
                    }
                    else if(j != 0 && i==0)
                    {
                        g2d.setColor(Colorations.brightenUp(HelicopterType.getValues().get(j-1).getStandardPrimaryHullColor()));
                        tempString = Window.dictionary.helicopterName(HelicopterType.getValues().get(j-1));
                    }
                    else if(i != 0)
                    {
                        g2d.setColor(Color.white);
                        if(i==1)
                        {
                            tempString = Events.recordTime[j-1][i-1] == 0
                                ? ""
                                : Events.recordTime[j - 1][i - 1] + " min";
                        }
                        else
                        {
                            tempString = Events.recordTime[j-1][i-1] == 0
                                ? ""
                                : Events.recordTime[j - 1][i - 1] - Events.recordTime[j - 1][i - 2] + " min";
                        }
                    
                    }
                    g2d.drawString(tempString, 200 + (j-1) * 135, 150 + (i-1) * 35);
                }
            }
        }
        else
        {
            if(!Window.page.isFarRightButton())
            {
                paintHelicopterInStartScreenMenu(g2d, graphics2DAdapter);
            }
        
            
            
            g2d.setColor(Color.lightGray);
            for(int i = 0; i < Window.NUMBER_OF_HIGH_SCORE_COLUMN_NAMES; i++)
            {
                g2d.drawString(
                    Window.dictionary.highScoreColumnNames().get(i),
                    getColumnX(i) + TOP_ROW_SHIFT_X,
                    TOP_ROW_Y);
            }
            
            AtomicInteger entryCounter = new AtomicInteger();
            Events.highScoreMap.get(HighScoreType.of(Window.page))
                               .asQueue()
                               .forEach(highScoreEntry -> {
                                   int j = entryCounter.incrementAndGet();
                                   HighScoreColumnType.getValues().forEach(highScoreColumnType -> {
                                       boolean isFirstEntryRow = j == 1;
                                       String text = isFirstEntryRow ? String.format("% 2d", j)
                                                                     : highScoreColumnType.getText(highScoreEntry);
                                       g2d.setColor(highScoreColumnType.getFontColor(highScoreEntry));
                                       g2d.drawString(text, highScoreColumnType.getColumnX(), getRowY(j));
                                   });
                                   
                                   
                                   g2d.setColor(Color.white);
                                   g2d.drawString(smallNumberToStringWithSpace(j),  getColumnX(0), getRowY(j));
                                   g2d.drawString(highScoreEntry.getPlayerName(),   getColumnX(1), getRowY(j));
                                   g2d.setColor(Colorations.brightenUp(highScoreEntry.getHelicopterType().getStandardPrimaryHullColor()));
                                   g2d.drawString(Window.dictionary.helicopterName(highScoreEntry.getHelicopterType()),   getColumnX(2), getRowY(j));
                                   g2d.setColor(highScoreEntry.getMaxLevel() > MAXIMUM_LEVEL ? Colorations.HS_GREEN : Colorations.HS_RED);
                                   int printLevel = Math.min(highScoreEntry.getMaxLevel(), MAXIMUM_LEVEL);
                                   g2d.drawString(bigNumberToStringWithSpace(printLevel), getColumnX(3), getRowY(j));
                                   g2d.setColor(Color.white);
                                   g2d.drawString(bigNumberToStringWithSpace(highScoreEntry.getPlayingTime()) + " min", getColumnX(4), getRowY(j));
                                   g2d.drawString(bigNumberToStringWithSpace(highScoreEntry.getCrashes()), 		  				  getColumnX(5), getRowY(j));
                                   g2d.drawString(bigNumberToStringWithSpace(highScoreEntry.getRepairs()),		  				  getColumnX(6), getRowY(j));
                                   g2d.setColor(Colorations.percentColor(2*highScoreEntry.getBonusIncome()));
                                   g2d.drawString(bigNumberToStringWithSpace(highScoreEntry.getBonusIncome()) + "%",		  getColumnX(7), getRowY(j));
                               });
        }
    }
    
    private int getColumnX(int columnIndex)
    {
        int columnDeviation = columnIndex == 0 ? -42 : columnIndex == 1 ? -88 : 0;
        return LEFT_COLUMN_X + columnDeviation + columnIndex * COLUMN_DISTANCE;
    }
    
    private int getRowY(int rowIndex)
    {
        return TOP_ROW_Y + rowIndex * ROW_DISTANCE;
    }
    
    private static String bigNumberToStringWithSpace(int value)
    {
        return String.format("% 3d", value);
    }
    
    private static String smallNumberToStringWithSpace(int value)
    {
        return String.format("% 2d", value);
    }
}
