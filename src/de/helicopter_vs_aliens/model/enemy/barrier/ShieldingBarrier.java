package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

public class ShieldingBarrier extends Barrier
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = Colorations.shieldingBarrierTurquoise;
    
        super.doTypeSpecificInitialization();
    }
}
