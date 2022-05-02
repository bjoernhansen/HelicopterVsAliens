package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public class BurrowingBarrier extends ArmedBarrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.setVarWidth(80);
        this.setInitialY(GROUND_Y - this.bounds.getWidth()/8);
        this.burrowTimer = READY;
        this.setBarrierShootingProperties();
        this.isLasting = true;
        
        super.create(helicopter);
    }
}
