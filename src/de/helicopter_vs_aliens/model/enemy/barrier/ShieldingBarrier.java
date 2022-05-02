package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

public class ShieldingBarrier extends Barrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = Colorations.shieldingBarrierTurquoise;
        this.setVarWidth(80);
    
        this.isLasting = true;
        
        super.create(helicopter);
    }
}
