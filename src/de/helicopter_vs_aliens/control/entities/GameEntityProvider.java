package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.AbstractGameEntityFactory;
import de.helicopter_vs_aliens.model.Paintable;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpFactory;

import java.util.HashMap;
import java.util.Map;


class GameEntityProvider
{
    private final Map<Class, AbstractGameEntityFactory> gameEntityFactories = new HashMap<>();
    
    
    GameEntityProvider()
    {
        gameEntityFactories.put(PowerUp.class, new PowerUpFactory());
    }
    
    public <T extends Paintable> T makeEntityOf(Class<T> classOfGameEntity)
    {
        return (T)gameEntityFactories.get(classOfGameEntity).make();
    }
}
