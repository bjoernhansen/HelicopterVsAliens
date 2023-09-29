package de.helicopter_vs_aliens.control.ressource_transfer;


import de.helicopter_vs_aliens.graphics.GraphicsApiType;

import java.util.Objects;


public final class GameResources
{
    private static GameProgress
        gameProgress;


    private GameResources()
    {
        throw new UnsupportedOperationException();
    }

    public static GameRessourceProvider getProvider()
    {
        return Objects.requireNonNull(gameProgress);
    }

    public static GameProgress getGameProgressInstance(GraphicsApiType graphicsApiType)
    {
        if(gameProgress == null)
        {
            GameResources.gameProgress = new GameProgress(graphicsApiType);
            GameResources.gameProgress.initialize();
        }
        return gameProgress;
    }
}
