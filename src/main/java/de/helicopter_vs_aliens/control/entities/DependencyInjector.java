package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceAcceptor;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;

import java.util.Objects;
import java.util.Optional;


public final class DependencyInjector
{
    private static DependencyInjector
        instance;

    private final GameRessourceProvider
        gameRessourceProvider;


    private DependencyInjector()
    {
        this.gameRessourceProvider = Objects.requireNonNull(GameResources.getProvider());
    }

    public void injectDependenciesFor(GameRessourceAcceptor gameRessourceAcceptor)
    {
        gameRessourceAcceptor.setGameRessourceProvider(gameRessourceProvider);
    }

    public static DependencyInjector getInstance()
    {
        instance = Optional.ofNullable(instance)
                           .orElseGet(DependencyInjector::new);
        return instance;
    }
}
