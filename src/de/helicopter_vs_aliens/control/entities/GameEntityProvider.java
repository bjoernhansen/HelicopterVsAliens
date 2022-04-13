package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.model.AbstractGameEntityFactory;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.Paintable;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpFactory;

import java.util.HashMap;
import java.util.Map;


class GameEntityProvider
{
    private final Map<Class<? extends GameEntity>, AbstractGameEntityFactory<? extends GameEntity>>
        gameEntityFactories = new HashMap<>();
    
    
    GameEntityProvider()
    {
        gameEntityFactories.put(PowerUp.class, new PowerUpFactory());
    }
    
    public <T extends GameEntity> T makeEntityOf(Class<T> classOfGameEntity)
    {
        return (T)gameEntityFactories.get(classOfGameEntity).make();
    }
}
