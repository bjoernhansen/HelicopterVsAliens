package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

public class StunningBarrier extends Barrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = Colorations.bleachedYellow;
        this.targetSpeedLevel.setLocation(0, 1 + 2*Math.random());
        this.setVarWidth(65);
    
        this.rotorColor = 2;
        this.staticChargeTimer = READY;
        this.isLasting = true;
        
        super.create(helicopter);
    }
}