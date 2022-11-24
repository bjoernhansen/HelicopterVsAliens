package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.score.Savegame;


public final class HelicopterFactory
{
    public static Helicopter createForNewGame(HelicopterType type)
    {
        Helicopter helicopter = type.makeInstance();
        helicopter.initializeForNewGame();
        return helicopter;
    }

    public static Helicopter createFromSavegame(Savegame savegame)
    {
        Helicopter helicopter = savegame.helicopterType.makeInstance();
        helicopter.initializeFromSavegame(savegame);
        return helicopter;
    }
}