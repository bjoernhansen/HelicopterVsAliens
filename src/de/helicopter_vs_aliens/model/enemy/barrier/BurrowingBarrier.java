package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public abstract class BurrowingBarrier extends ArmedBarrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.burrowTimer = READY;
        
        super.create(helicopter);
    }
}
