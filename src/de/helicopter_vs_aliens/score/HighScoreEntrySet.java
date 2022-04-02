package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.util.SizeLimitedTreeSet;

public class HighScoreEntrySet extends SizeLimitedTreeSet<HighScoreEntry>
{
    private static final int
        MAX_SIZE = 10;
    
    public HighScoreEntrySet()
    {
        super(MAX_SIZE);
    }
}
