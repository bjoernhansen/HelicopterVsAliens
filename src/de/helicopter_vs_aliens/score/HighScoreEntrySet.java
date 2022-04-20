package de.helicopter_vs_aliens.score;

public class HighScoreEntrySet extends SizeLimitedTreeSet<HighScoreEntry>
{
    private static final int
        MAX_SIZE = 10;
    
    public HighScoreEntrySet()
    {
        super(MAX_SIZE);
    }
}
