package de.helicopter_vs_aliens.score;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class HighScore implements Serializable
{
    private final Map<HighScoreType, SizeLimitedTreeSet<HighScoreEntry>>
        highScoreMap;
    
    public HighScore()
    {
        Map<HighScoreType, SizeLimitedTreeSet<HighScoreEntry>> map = new EnumMap<>(HighScoreType.class);
        HighScoreType.getValues()
                     .forEach(highScoreType -> map.put(highScoreType, new HighScoreEntrySet()));
        highScoreMap = Collections.unmodifiableMap(map);
    }
    
    public SizeLimitedTreeSet<HighScoreEntry> getEntrySetFor(HighScoreType highScoreType)
    {
        return highScoreMap.get(highScoreType);
    }
    
    public void saveEntryFor(Savegame savegame)
    {
        if(savegame.isWorthyForHighscore())
        {
            HighScoreEntry newHighScoreEntry = HighScoreEntry.of(savegame);
            HighScoreType highScoreType = HighScoreType.of(newHighScoreEntry.getHelicopterType());
            highScoreMap.get(highScoreType)
                        .add(newHighScoreEntry);
            highScoreMap.get(HighScoreType.OVERALL)
                        .add(newHighScoreEntry);
        }
    }
}