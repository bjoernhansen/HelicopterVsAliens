package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceAcceptor;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;


public abstract class GameEntity implements Paintable, GameRessourceAcceptor
{
    private GameRessourceProvider gameRessourceProvider;
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter)
    {
        GraphicsManager.getInstance().paint(this);
    }
    
    @Override
    public void setGameRessourceProvider(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    protected GameRessourceProvider getGameRessourceProvider()
    {
        return gameRessourceProvider;
    }
    
    protected Helicopter getHelicopter()
    {
        return getGameRessourceProvider().getHelicopter();
    }
}
