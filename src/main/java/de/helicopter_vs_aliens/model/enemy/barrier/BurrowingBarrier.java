package de.helicopter_vs_aliens.model.enemy.barrier;

public abstract class BurrowingBarrier extends ArmedBarrier
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        burrowTimer = READY;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected boolean canBePositionedBelowGround()
    {
        return burrowTimer != DISABLED
                || super.canBePositionedBelowGround();
    }
}
