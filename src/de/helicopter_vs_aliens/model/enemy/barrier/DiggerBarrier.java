package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class DiggerBarrier extends BurrowingBarrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.setInitialWidth();
        this.setFixedY(GROUND_Y - this.bounds.getWidth()/8);
       
        this.isLasting = true;
        
        super.create(helicopter);
    }
    
    @Override
    protected int calculateShootingRate()
    {
        return 35 + Calculations.random(15);
    }
}
