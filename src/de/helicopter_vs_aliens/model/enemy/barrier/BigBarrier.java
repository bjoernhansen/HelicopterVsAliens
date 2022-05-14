package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.util.Colorations;

public class BigBarrier extends Barrier
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = Colorations.bleachedGreen;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected double calculateInitialY()
    {
        return getOnTheGroundY();
    }
}
