package de.helicopter_vs_aliens.graphics.painter.enemy;

import de.helicopter_vs_aliens.model.enemy.barrier.BurrowingBarrier;

public class BurrowingBarrierPainter extends BarrierPainter<BurrowingBarrier>
{
    @Override
    protected String getTestingInfo()
    {
        return "Borrow: " + getEnemy().burrowTimer + " ; " + super.getTestingInfo();
    }
}
