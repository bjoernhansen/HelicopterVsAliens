package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.score.Savegame;


public final class HelicopterFactory
{
    public static Helicopter createForNewGame(HelicopterType type)
    {
        Helicopter helicopter = create(type);
        helicopter.initializeForNewGame();
        return helicopter;
    }

    public static Helicopter createFromSavegame(Savegame savegame)
    {
        Helicopter helicopter = create(savegame.helicopterType);
        helicopter.initializeFromSavegame(savegame);
        return helicopter;
    }

    public static Helicopter create(HelicopterType type)
    {
        switch(type)
        {
            case PHOENIX:
                return new Phoenix();
            case ROCH:
                return new Roch();
            case OROCHI:
                return new Orochi();
            case KAMAITACHI:
                return new Kamaitachi();
            case PEGASUS:
                return new Pegasus();
            default:
                return new Helios();
        }
    }
}