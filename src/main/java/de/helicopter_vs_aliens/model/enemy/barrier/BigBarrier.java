package de.helicopter_vs_aliens.model.enemy.barrier;

public class BigBarrier extends Barrier
{
    @Override
    protected double calculateInitialY(){
        return getOnTheGroundY();
    }
}
