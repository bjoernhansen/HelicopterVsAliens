package de.helicopter_vs_aliens.control.ressource_transfer;


public final class GameResources
{
    private static GameRessourceProvider
        gameRessourceProvider;


    private GameResources()
    {
        throw new UnsupportedOperationException();
    }

    public static void setGameResources(GameRessourceProvider gameRessourceProvider)
    {
        GameResources.gameRessourceProvider = gameRessourceProvider;
    }

    public static GameRessourceProvider getProvider()
    {
        return gameRessourceProvider;
    }
}
