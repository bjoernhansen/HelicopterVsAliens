package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class ShootingBarrier extends ArmedBarrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.setInitialWidth();
        this.hasYPosSet = true;
        this.barrierShootTimer = READY;
        
        this.isLasting = true;
        
        super.create(helicopter);
    }
    
    @Override
    protected int calculateRotationSpeed()
    {
        return isRotatingShotDirectionAloud()
                ? getRandomShootingRate()
                : 0;
    }
    
    private boolean isRotatingShotDirectionAloud()
    {
        return Calculations.tossUp(SPIN_SHOOTER_RATE) && Events.level >= MIN_SPIN_SHOOTER_LEVEL;
    }
    
    private int getRandomShootingRate()
    {
        return Calculations.randomDirection()*(this.shootingRate/3 + Calculations.random(10));
    }
}
