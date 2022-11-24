package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;


public enum HighScoreColumnType
{
    RANK(1, null, -42),
    PLAYER(2, HighScoreColumnType::getPlayerName, -88),
    TYPE(3, HighScoreColumnType::getHelicopterName)
    {
        @Override
        public Color getFontColor(HighScoreEntry highScoreEntry)
        {
            return Colorations.brightenUp(highScoreEntry.getHelicopterType().getStandardPrimaryHullColor());
        }
    },
    MAX_LEVEL(4, HighScoreColumnType::getMaxLevel)
    {
        @Override
        public Color getFontColor(HighScoreEntry highScoreEntry)
        {
            return highScoreEntry.getMaxLevel() > Events.MAXIMUM_LEVEL ? Colorations.HS_GREEN : Colorations.HS_RED;
        }
    },
    PLAYING_TIME(5, HighScoreColumnType::getPlayingTime),
    CRASHES(6, HighScoreColumnType::getCrashes),
    REPAIRS(7, HighScoreColumnType::getRepairs),
    BONUSES(8, HighScoreColumnType::getBonusIncome)
    {
        @Override
        public Color getFontColor(HighScoreEntry highScoreEntry)
        {
            return Colorations.percentColor(2*highScoreEntry.getBonusIncome());
        }
    };
    
    private static final int
        LEFT_X = 107,
        DISTANCE = 114;
    
    private static final String
        NUMBER_FORMAT = "%3d",
        KEY_PREFIX = "highScore.columnNames.";
    
    private static final List<HighScoreColumnType>
        VALUES = List.of(values());
     
    private final Function<HighScoreEntry, String>
        textFunction;
    
    private final int
        columnShiftX;
    
    private final String
        key;
      
    
    HighScoreColumnType(int index, Function<HighScoreEntry, String> textFunction)
    {
        this.key = KEY_PREFIX + index;
        this.textFunction = textFunction;
        this.columnShiftX = 0;
    }
    
    HighScoreColumnType(int index, Function<HighScoreEntry, String> textFunction, int columnShiftX)
    {
        this.key = KEY_PREFIX + index;
        this.textFunction = textFunction;
        this.columnShiftX = columnShiftX;
    }
    
    public static List<HighScoreColumnType> getValues()
    {
        return VALUES;
    }
    
    private static String getPlayerName(HighScoreEntry highScoreEntry)
    {
        return highScoreEntry.getPlayerName();
    }
    
    private static String getHelicopterName(HighScoreEntry highScoreEntry)
    {
        return Window.dictionary.helicopterName(highScoreEntry.getHelicopterType());
    }
    
    private static String getMaxLevel(HighScoreEntry highScoreEntry)
    {
        int maxLevel = Math.min(highScoreEntry.getMaxLevel(), Events.MAXIMUM_LEVEL);
        return formatNumber(maxLevel);
    }
    
    private static String getPlayingTime(HighScoreEntry highScoreEntry)
    {
        return formatNumber(highScoreEntry.getPlayingTime()) + " min";
    }
    
    private static String getCrashes(HighScoreEntry highScoreEntry)
    {
        return formatNumber(highScoreEntry.getCrashes());
    }
    
    private static String getRepairs(HighScoreEntry highScoreEntry)
    {
        return formatNumber(highScoreEntry.getRepairs());
    }
    
    private static String getBonusIncome(HighScoreEntry highScoreEntry)
    {
        return formatNumber(highScoreEntry.getBonusIncome()) + "%";
    }
    
    private static String formatNumber(int number)
    {
        return String.format(NUMBER_FORMAT, number)
                     .replace(" ", "  ");
    }
    
    public Color getFontColor(HighScoreEntry highScoreEntry)
    {
        return Color.white;
    }
    
    public String getText(HighScoreEntry highScoreEntry)
    {
        return textFunction.apply(highScoreEntry);
    }
    
    public int getColumnX()
    {
        return LEFT_X + columnShiftX + ordinal() * DISTANCE;
    }
    
    public String getKey()
    {
        return key;
    }
}