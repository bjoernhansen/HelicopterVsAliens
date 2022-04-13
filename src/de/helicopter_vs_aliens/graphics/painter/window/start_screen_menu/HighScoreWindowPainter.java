package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.score.HighScoreColumnType;
import de.helicopter_vs_aliens.score.HighScoreType;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;


public class HighScoreWindowPainter extends StartScreenMenuWindowPainter
{
    // TODO Konstanten deklarieren
    private static final int
        TOP_ROW_SHIFT_X = -10,
        TOP_ROW_Y = 104,
        ROW_DISTANCE = 21;
    
    private static final String
        NUMBER_FORMAT = "%2d";
        
    @Override
    void paintStartScreenMenu(GraphicsAdapter graphicsAdapter)
    {
        super.paintStartScreenMenu(graphicsAdapter);
        
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
                        graphicsAdapter.setColor(Colorations.golden);
                        tempString = "Boss " + i;
                    }
                    else if(j != 0 && i==0)
                    {
                        graphicsAdapter.setColor(Colorations.brightenUp(HelicopterType.getValues().get(j-1).getStandardPrimaryHullColor()));
                        tempString = Window.dictionary.helicopterName(HelicopterType.getValues().get(j-1));
                    }
                    else if(i != 0)
                    {
                        graphicsAdapter.setColor(Color.white);
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
                    graphicsAdapter.drawString(tempString, 200 + (j-1) * 135, 150 + (i-1) * 35);
                }
            }
        }
        else
        {
            if(!Window.page.isFarRightButton())
            {
                paintHelicopterInStartScreenMenu(graphicsAdapter);
            }
            paintColumnHeadings(graphicsAdapter);
            paintHighScoreEntryRows(graphicsAdapter);
        }
    }
    
    private void paintColumnHeadings(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Color.lightGray);
        HighScoreColumnType.getValues().forEach(highScoreColumnType ->
            graphicsAdapter.drawString(
                Window.dictionary.highScoreColumnName(highScoreColumnType),
                highScoreColumnType.getColumnX() + TOP_ROW_SHIFT_X,
                TOP_ROW_Y));
    }
    
    private void paintHighScoreEntryRows(GraphicsAdapter graphicsAdapter)
    {
        AtomicInteger entryCounter = new AtomicInteger();
        Events.highScoreMap.get(HighScoreType.of(Window.page))
                           .forEach(highScoreEntry -> {
                               int j = entryCounter.incrementAndGet();
                               HighScoreColumnType.getValues().forEach(highScoreColumnType -> {
                                   String text = highScoreColumnType == HighScoreColumnType.RANK
                                                    ? formatNumber(j)
                                                    : highScoreColumnType.getText(highScoreEntry);
                                   graphicsAdapter.setColor(highScoreColumnType.getFontColor(highScoreEntry));
                                   graphicsAdapter.drawString(text, highScoreColumnType.getColumnX(), getRowY(j));
                               });
                           });
    }

    private int getRowY(int rowIndex)
    {
        return TOP_ROW_Y + rowIndex * ROW_DISTANCE;
    }
    
    private static String formatNumber(int number)
    {
        return String.format(NUMBER_FORMAT, number)
                     .replace(" ", "  ");
    }
}