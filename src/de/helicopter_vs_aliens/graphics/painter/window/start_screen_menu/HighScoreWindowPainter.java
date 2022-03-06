package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.gui.button.StartScreenSubButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.score.HighScoreEntry;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Graphics2D;

import static de.helicopter_vs_aliens.control.Events.MAXIMUM_LEVEL;

public class HighScoreWindowPainter extends StartScreenMenuWindowPainter
{
    @Override
    void paintStartScreenMenu(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter)
    {
        super.paintStartScreenMenu(g2d, graphics2DAdapter);
        
        if(Window.page == StartScreenSubButtonType.BUTTON_1)
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
            if(Window.page.ordinal() > 1 && Window.page.ordinal() < 2 + HelicopterType.size())
            {
                paintHelicopterInStartScreenMenu(g2d, graphics2DAdapter);
            }
        
            int columnDistance = 114,
                topLine = 125,
                lineDistance = 21,
                leftColumn = 55,
                realLeftColumn = leftColumn,
                xShift = 10;
        
            g2d.setColor(Color.lightGray);
            for(int i = 0; i < Window.NUMBER_OF_HIGH_SCORE_COLUMN_NAMES; i++)
            {
                if(i == 1){realLeftColumn = leftColumn - 46;}
                else if(i == 2){realLeftColumn = leftColumn + 42;}
                g2d.drawString(
                    Window.dictionary.highScoreColumnNames().get(i),
                    realLeftColumn + i * columnDistance,
                    topLine - lineDistance);
            }
        
            for(int j = 0; j < HighScoreEntry.NUMBER_OF_ENTRIES; j++)
            {
                HighScoreEntry tempEntry = Events.highScore[Window.page.ordinal()==1?6: Window.page.ordinal()-2][j];
            
                if(tempEntry != null)
                {
                    g2d.setColor(Color.white);
                    g2d.drawString(toStringWithSpace(j+1, false), leftColumn + xShift , topLine + j * lineDistance);
                    g2d.drawString(tempEntry.playerName, leftColumn - 46 + xShift + columnDistance, topLine + j * lineDistance);
                    g2d.setColor(Colorations.brightenUp(tempEntry.helicopterType.getStandardPrimaryHullColor()));
                    g2d.drawString(Window.dictionary.helicopterName(tempEntry.helicopterType),   realLeftColumn + xShift + 2 * columnDistance, topLine + j * lineDistance);
                    g2d.setColor(tempEntry.maxLevel > MAXIMUM_LEVEL ? Colorations.HS_GREEN : Colorations.HS_RED);
                    int printLevel = Math.min(tempEntry.maxLevel, MAXIMUM_LEVEL);
                    g2d.drawString(toStringWithSpace(printLevel), realLeftColumn + xShift + 3 * columnDistance, topLine + j * lineDistance);
                    g2d.setColor(Color.white);
                    g2d.drawString(toStringWithSpace((int)tempEntry.playingTime) + " min", realLeftColumn + xShift + 4 * columnDistance, topLine + j * lineDistance);
                    g2d.drawString(toStringWithSpace(tempEntry.crashes), 		  				  realLeftColumn + xShift + 5 * columnDistance, topLine + j * lineDistance);
                    g2d.drawString(toStringWithSpace(tempEntry.repairs),		  				  realLeftColumn + xShift + 6 * columnDistance, topLine + j * lineDistance);
                    g2d.setColor(Colorations.percentColor(2*tempEntry.bonusIncome));
                    g2d.drawString(toStringWithSpace(tempEntry.bonusIncome) + "%",		  realLeftColumn + xShift + 7 * columnDistance, topLine + j * lineDistance);
                }
                else break;
            }
        }
    }
    
    private static String toStringWithSpace(int value)
    {
        return toStringWithSpace(value, true);
    }
    
    private static String toStringWithSpace(int value, boolean big)
    {
        // TODO String.format oder ähnliches verwenden
        return (big	? (value >= 100 ? "" : "  ") : "")
            + (value >= 10 ? "" : "  ")
            + value;
    }
}
