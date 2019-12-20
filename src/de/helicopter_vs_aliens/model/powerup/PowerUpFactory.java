package de.helicopter_vs_aliens.model.powerup;

import de.helicopter_vs_aliens.model.AbstractGameEntityFactory;

public class PowerUpFactory extends AbstractGameEntityFactory <PowerUp>
{
    @Override
    public PowerUp make()
    {
        return new PowerUp();
    }
}
