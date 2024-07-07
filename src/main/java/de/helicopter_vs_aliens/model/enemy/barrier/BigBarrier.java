package de.helicopter_vs_aliens.model.enemy.barrier;

public final class BigBarrier extends Barrier
{
    @Override
    protected double calculateInitialY(){
        return getOnTheGroundY();
    }
}
