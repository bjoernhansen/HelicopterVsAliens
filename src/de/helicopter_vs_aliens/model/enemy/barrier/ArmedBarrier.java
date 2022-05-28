package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
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
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        setBarrierShootingProperties();
    
        super.doTypeSpecificInitialization();
    }
    
    private void setBarrierShootingProperties()
    {
        shootPause = calculateShootPause();
        shootingRate = calculateShootingRate();
        shotsPerCycle = calculateShotsPerCycle();
        shootingCycleLength = this.shootPause + this.shootingRate * this.shotsPerCycle;
        this.shotSpeed = calculateShootSpeed();
        this.shotRotationSpeed = calculateRotationSpeed();
    }
    
    protected int calculateShootPause()
    {
        return 2 * this.shootingRate + 20 + Calculations.random(40);
    }
    
    protected int calculateShootingRate()
    {
        return 25 + Calculations.random(25);
    }
    
    protected int calculateShotsPerCycle()
    {
        return 2 + Calculations.random(9);
    }
    
    protected int calculateShootSpeed()
    {
        return 5 + Calculations.random(6);
    }
    
    private void setShotType()
    {
        this.shotType = calculateMissileType();
    }
 
    private EnemyMissileType calculateMissileType()
    {
        return isArmingWithBusterMissilesApproved()
                ? EnemyMissileType.BUSTER
                : EnemyMissileType.DISCHARGER;
    }
    
    protected boolean isArmingWithBusterMissilesApproved()
    {
        return Calculations.tossUp(BUSTER_PROBABILITY) && Events.level >= MIN_BUSTER_LEVEL;
    }
    
    protected Color getArmedBarrierColor()
    {
        return this.shotType.getCorrespondingBarrierColor();
    }
    
    protected int calculateRotationSpeed()
    {
        return 0;
    }
    
    @Override
    protected void setTypeSpecificPrerequisites()
    {
        setShotType();
    }
    
    @Override
    protected Color getBaseColor()
    {
        return getArmedBarrierColor();
    }
}
