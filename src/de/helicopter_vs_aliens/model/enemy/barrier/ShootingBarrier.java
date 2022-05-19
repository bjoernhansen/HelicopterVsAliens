package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.util.Calculations;

public class ShootingBarrier extends ArmedBarrier
{
    public static final int
        MIN_SPIN_SHOOTER_LEVEL = 23;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.barrierShootTimer = READY;
    
        super.doTypeSpecificInitialization();
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
