package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceAcceptor;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;


final class DependencyInjector
{
    private final GameRessourceProvider
        gameRessourceProvider;
    
    static DependencyInjector instanceFor(GameRessourceProvider gameRessourceProvider)
    {
        return new DependencyInjector(gameRessourceProvider);
    }
    
    private DependencyInjector(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    void injectDependenciesFor(GameRessourceAcceptor gameRessourceAcceptor)
    {
        gameRessourceAcceptor.setGameRessourceProvider(gameRessourceProvider);
    }
}
