package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.missile.EnemyMissileType;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;


abstract class ArmedBarrier extends Barrier
{
    private static final int
        MIN_BUSTER_LEVEL = 29;
    private static final float
        BUSTER_PROBABILITY = 0.35f;
    
    protected void setBarrierShootingProperties()
    {
        if(this.barrierTeleportTimer != DISABLED || this.burrowTimer != DISABLED)
        {
            this.shootingRate = 35 + Calculations.random(15);
        }
        else
        {
            this.shootingRate = 25 + Calculations.random(25);
        }
        
        if(this.barrierTeleportTimer == DISABLED){this.shootPause = 2 * this.shootingRate + 20 + Calculations.random(40);}
        this.shotsPerCycle = 2 + Calculations.random(9);
        this.shootingCycleLength = this.shootPause + this.shootingRate * this.shotsPerCycle;
        this.shotSpeed = 5 + Calculations.random(6);
        this.shotType = getMissileType();
        this.primaryColor = getArmedBarrierColor();
    }
    
    protected EnemyMissileType getMissileType()
    {
        return isArmingWithBusterMissilesApproved()
                ? EnemyMissileType.BUSTER
                : EnemyMissileType.DISCHARGER;
    }
    
    private boolean isArmingWithBusterMissilesApproved()
    {
        return Calculations.tossUp(BUSTER_PROBABILITY) && Events.level >= MIN_BUSTER_LEVEL;
    }
    
    protected Color getArmedBarrierColor()
    {
        return this.shotType == EnemyMissileType.BUSTER
                ? Colorations.bleachedViolet
                : Colorations.bleachedRed;
    }
}
