package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class DiggerBarrier extends BurrowingBarrier
{
    @Override
    protected int calculateShootingRate()
    {
        return 35 + Calculations.random(15);
    }
    
    @Override
    protected double calculateInitialY()
    {
        return GROUND_Y - getWidth()/8;
    }
}
