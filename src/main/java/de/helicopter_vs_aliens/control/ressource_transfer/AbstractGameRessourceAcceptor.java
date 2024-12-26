package de.helicopter_vs_aliens.control.ressource_transfer;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;


public abstract class AbstractGameRessourceAcceptor implements GameRessourceAcceptor
{
    private GameRessourceProvider gameRessourceProvider;
    
    @Override
    public void setGameRessourceProvider(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    protected GameRessourceProvider getGameRessourceProvider()
    // TODO alle Übergaben von gameRessourceProvider entfernen und über die Methode direkt zugreifen
    {
        return gameRessourceProvider;
    }
    
    protected Helicopter getHelicopter()
    {
        return getGameRessourceProvider().getHelicopter();
    }
}
