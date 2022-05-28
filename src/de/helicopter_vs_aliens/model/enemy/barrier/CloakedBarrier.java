package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.missile.EnemyMissileType;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

public class CloakedBarrier extends ArmedBarrier
{
    // TODO CLOAKED_BARRIER  überarbeiten, wie oft soll er wiederkommen? manchmal schießt er nicht,
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        barrierTeleportTimer = READY;
        callBack = 1 + Calculations.random(4);
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected int calculateShootingRate()
    {
        return 35 + Calculations.random(15);
    }
    
    @Override
    protected int calculateShootPause()
    {
        return 0;
    }
    
    @Override
    protected boolean isArmingWithBusterMissilesApproved()
    {
        return true;
    }
    
    @Override
    protected void setInitialLocation(Helicopter helicopter)
    {
        startBarrierUncloaking(helicopter);
    }
    
    @Override
    public boolean isDisappearingAfterEnteringRepairShop()
    {
        return true;
    }
    
    @Override
    protected Color getArmedBarrierColor()
    {
        return Colorations.bleachedCloaked;
    }
}
