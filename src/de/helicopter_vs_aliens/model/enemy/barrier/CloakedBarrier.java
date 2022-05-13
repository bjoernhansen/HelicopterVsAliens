package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class CloakedBarrier extends ArmedBarrier
{
    // TODO CLOAKED_BARRIER  überarbeiten, wie oft soll er wiederkommen? manchmal schießt er nicht,
    @Override
    protected void create(Helicopter helicopter)
    {
        this.setInitialWidth();
    
        this.barrierTeleportTimer = READY;
        
        this.startBarrierUncloaking(helicopter);
    
        this.hasYPosSet = true;
        this.callBack = 1 + Calculations.random(4);
        
        super.create(helicopter);
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
}
