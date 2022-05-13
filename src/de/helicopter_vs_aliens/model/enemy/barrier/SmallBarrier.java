package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

public class SmallBarrier extends Barrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = Colorations.bleachedGreen;
        this.isLasting = true;
        this.setInitialWidth();
        
        super.create(helicopter);
    }
}
