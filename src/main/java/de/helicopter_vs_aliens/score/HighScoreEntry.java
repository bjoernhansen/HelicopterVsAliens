package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

import java.io.Serializable;
import java.util.Comparator;

public final class HighScoreEntry implements Serializable, Comparable<HighScoreEntry>
{
    private static final long
        serialVersionUID = 1L;
    
    private static final Comparator<HighScoreEntry>
        COMPARATOR = Comparator.comparingInt(HighScoreEntry::getMaxLevel)
                               .reversed()
                               .thenComparing(HighScoreEntry::getPlayingTime)
                               .thenComparing(HighScoreEntry::getCrashes)
                               .thenComparing(HighScoreEntry::getRepairs)
                               .thenComparing(HighScoreEntry::getBonusIncome, Comparator.reverseOrder())
                               .thenComparing(Object::hashCode);
    
    // Variablen eines Highscore-Eintrages
    private final String
        playerName;
    
    private final HelicopterType
        helicopterType;
    
    private final int
        maxLevel;
    
    private final int
        playingTime;
    
    private final int
        crashes;
    
    private final int
        repairs;
    
    private final int
        bonusIncome;
    
    
    public static HighScoreEntry of(Savegame savegame)
    {
        return new HighScoreEntry(savegame);
    }
    
    private HighScoreEntry(Savegame savegame)
    {
        this.playerName = Events.currentPlayerName;
        this.helicopterType = savegame.helicopterType;
        this.maxLevel = savegame.maxLevel;
        this.playingTime = (int) savegame.playingTime / 60000;
        this.crashes = savegame.numberOfCrashes;
        this.repairs = savegame.numberOfRepairs;
        this.bonusIncome = Events.bonusIncomePercentage();
    }
    
    @Override
    public int compareTo(HighScoreEntry highScoreEntry)
    {
        return COMPARATOR.compare(this, highScoreEntry);
    }
    
    public String getPlayerName()
    {
        return playerName;
    }
    
    public HelicopterType getHelicopterType()
    {
        return helicopterType;
    }
    
    public int getMaxLevel()
    {
        return maxLevel;
    }
    
    public int getCrashes()
    {
        return crashes;
    }
    
    public int getRepairs()
    {
        return repairs;
    }
    
    public int getBonusIncome()
    {
        return bonusIncome;
    }
    
    public int getPlayingTime()
    {
        return playingTime;
    }
}
