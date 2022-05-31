package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.util.Calculations;

public abstract class StandardEnemy extends Enemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
    }
    
    @Override
    public boolean areALlRequirementsForPowerUpDropMet()
    {
        return !Events.isBossLevel() && canDropPowerUp();
    }
    
    protected boolean canDropPowerUp()
    {
        return Calculations.tossUp(POWER_UP_PROB) && Events.level >= MIN_POWER_UP_LEVEL;
    }
}
