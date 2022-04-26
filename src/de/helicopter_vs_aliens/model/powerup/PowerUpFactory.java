package de.helicopter_vs_aliens.model.powerup;

import de.helicopter_vs_aliens.control.entities.GameEntityFactory;

public class PowerUpFactory implements GameEntityFactory<PowerUp>
{
    @Override
    public PowerUp makeInstance()
    {
        return new PowerUp();
    }
    
    @Override
    public Class<PowerUp> getCorrespondingClass()
    {
        // TODO implementation
        return null;
    }
}
