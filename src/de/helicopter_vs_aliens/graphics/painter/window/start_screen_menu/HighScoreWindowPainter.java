package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.control.BossLevel;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.score.HighScoreColumnType;
import de.helicopter_vs_aliens.score.HighScoreType;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.util.Optional;
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
            paintRecordTimeTable(graphicsAdapter);
        }
        else
        {
            if(!Window.page.isFarRightButton())
            {
                paintHelicopterInStartScreenMenu(graphicsAdapter);
            }
            paintHighScoreColumnHeadings(graphicsAdapter);
            paintHighScoreEntryRows(graphicsAdapter);
        }
    }
    
    private void paintRecordTimeTable(GraphicsAdapter graphicsAdapter)
    {
        paintRecordTimeTableColumnLabels(graphicsAdapter);
        paintRecordTimeTableRowLabels(graphicsAdapter);
        paintRecordTimeTableInnerEntries(graphicsAdapter);
    }
    
    private void paintRecordTimeTableRowLabels(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Colorations.golden);
        BossLevel.getValues().forEach(bossLevel -> {
            String text =  "Boss " + bossLevel.getBossNr();
            drawRecordTimeTableEntry(graphicsAdapter, text, 0, bossLevel.getBossNr());
        });
    }
    
    private void paintRecordTimeTableColumnLabels(GraphicsAdapter graphicsAdapter)
    {
        HelicopterType.getValues().forEach(helicopterType -> {
            Color typeSpecificColor = Colorations.brightenUp(helicopterType.getStandardPrimaryHullColor());
            graphicsAdapter.setColor(typeSpecificColor);
            String text = Window.dictionary.helicopterName(helicopterType);
            drawRecordTimeTableEntry(graphicsAdapter, text, helicopterType.getNumber(), 0);
        });
    }
    
    private void paintRecordTimeTableInnerEntries(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Color.white);
        HelicopterType.getValues().forEach(helicopterType ->
            BossLevel.getValues().forEach(bossLevel -> {
                String text = getRecordTimeText(helicopterType, bossLevel);
                drawRecordTimeTableEntry(graphicsAdapter, text, helicopterType.getNumber(), bossLevel.getBossNr());
            }));
    }
    
    private String getRecordTimeText(HelicopterType helicopterType, BossLevel bossLevel)
    {
        return Optional.ofNullable(bossLevel)
                       .filter(helicopterType::hasPassed)
                       .map(helicopterType::getRecordTime)
                       .map(recordTime -> recordTime + " min")
                       .orElse("");
    }
    
    private void drawRecordTimeTableEntry(GraphicsAdapter graphicsAdapter, String text, int indexX, int indexY)
    {
        // TODO Magic numbers
        graphicsAdapter.drawString(text, 200 + (indexX-1) * 135, 150 + (indexY-1) * 35);
    }
    
    private void paintHighScoreColumnHeadings(GraphicsAdapter graphicsAdapter)
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
    
    private String formatNumber(int number)
    {
        return String.format(NUMBER_FORMAT, number)
                     .replace(" ", "  ");
    }
}