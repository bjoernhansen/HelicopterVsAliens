package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class ShootingBarrier extends ArmedBarrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.setVarWidth(85);
        this.hasYPosSet = true;
        this.barrierShootTimer = READY;
        this.setBarrierShootingProperties();
        this.shotRotationSpeed
            = Calculations.tossUp(SPIN_SHOOTER_RATE) && Events.level >= MIN_SPIN_SHOOTER_LEVEL
            ? Calculations.randomDirection()*(this.shootingRate/3 + Calculations.random(10))
            : 0;
        this.isLasting = true;
        
        super.create(helicopter);
    }
}
