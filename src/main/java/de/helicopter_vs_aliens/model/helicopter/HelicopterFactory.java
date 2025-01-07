package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.score.Savegame;


public final class HelicopterFactory
{
    private final GameRessourceProvider
        gameRessourceProvider;
    
    public HelicopterFactory(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    public Helicopter createForNewGame(HelicopterType type)
    {
        Helicopter helicopter = type.makeInstance(gameRessourceProvider);
        helicopter.initializeForNewGame();
        return helicopter;
    }

    public Helicopter createFromSavegame(Savegame savegame)
    {
        Helicopter helicopter = savegame.helicopterType.makeInstance(gameRessourceProvider);
        helicopter.initializeFromSavegame(savegame);
        return helicopter;
    }
}