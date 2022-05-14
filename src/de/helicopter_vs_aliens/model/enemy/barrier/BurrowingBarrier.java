package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public abstract class BurrowingBarrier extends ArmedBarrier
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.burrowTimer = READY;
    
        super.doTypeSpecificInitialization();
    }
}
