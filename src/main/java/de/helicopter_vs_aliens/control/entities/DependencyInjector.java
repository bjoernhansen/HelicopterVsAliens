package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceAcceptor;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;

public final class DependencyInjector
{
    private static final DependencyInjector
        instance = new DependencyInjector();
    
    private final GameRessourceProvider
        gameRessourceProvider;
    
    
    private DependencyInjector()
    {
        this.gameRessourceProvider = GameResources.getProvider();
    }
    
    public void injectDependenciesFor(GameRessourceAcceptor gameRessourceAcceptor)
    {
        gameRessourceAcceptor.setGameRessourceProvider(gameRessourceProvider);
    }
    
    public static DependencyInjector getInstance()
    {
        return instance;
    }
}