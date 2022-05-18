package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

public class StunningBarrier extends Barrier
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.rotorColor = 2;
        this.staticChargeTimer = READY;
    
        super.doTypeSpecificInitialization();
    }
}
