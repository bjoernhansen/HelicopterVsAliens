package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;


public enum HighScoreColumnType
{
    RANK(null, -42),
    PLAYER(HighScoreColumnType::getPlayerName, -88),
    TYPE(HighScoreColumnType::getHelicopterName)
    {
        @Override
        public Color getFontColor(HighScoreEntry highScoreEntry)
        {
            return Colorations.brightenUp(highScoreEntry.getHelicopterType().getStandardPrimaryHullColor());
        }
    },
    MAX_LEVEL(HighScoreColumnType::getMaxLevel)
    {
        @Override
        public Color getFontColor(HighScoreEntry highScoreEntry)
        {
            return highScoreEntry.getMaxLevel() > Events.MAXIMUM_LEVEL ? Colorations.HS_GREEN : Colorations.HS_RED;
        }
    },
    PLAYING_TIME(HighScoreColumnType::getPlayingTime),
    CRASHES(HighScoreColumnType::getCrashes),
    REPAIRS(HighScoreColumnType::getRepairs),
    BONUSES(HighScoreColumnType::getBonusIncome)
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
        NUMBER_FORMAT = "% 3d";
    
    private static final List<HighScoreColumnType>
        VALUES = List.of(values());
    
    
    private final Function<HighScoreEntry, String>
        textFunction;
    
    private final int
        columnShiftX;
        
      
    HighScoreColumnType(Function<HighScoreEntry, String> textFunction)
    {
        this.columnShiftX = 0;
        this.textFunction = textFunction;
    }
    
    HighScoreColumnType(Function<HighScoreEntry, String> textFunction, int columnShiftX)
    {
        this.columnShiftX = columnShiftX;
        this.textFunction = textFunction;
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
        return String.format(NUMBER_FORMAT, maxLevel);
    }
    
    private static String getPlayingTime(HighScoreEntry highScoreEntry)
    {
        return String.format(NUMBER_FORMAT, highScoreEntry.getPlayingTime()) + " min";
    }
    
    private static String getCrashes(HighScoreEntry highScoreEntry)
    {
        return String.format(NUMBER_FORMAT, highScoreEntry.getCrashes());
    }
    
    private static String getRepairs(HighScoreEntry highScoreEntry)
    {
        return String.format(NUMBER_FORMAT, highScoreEntry.getRepairs());
    }
    
    private static String getBonusIncome(HighScoreEntry highScoreEntry)
    {
        return String.format(NUMBER_FORMAT, highScoreEntry.getBonusIncome()) + "%";
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
}