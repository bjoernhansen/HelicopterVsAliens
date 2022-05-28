package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;

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
