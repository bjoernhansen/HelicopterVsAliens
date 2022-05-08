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
    protected void create(Helicopter helicopter)
    {
        this.setVarWidth(100);
    
        this.barrierTeleportTimer = READY;
        this.setBarrierShootingProperties();
        this.startBarrierUncloaking(helicopter);
    
        this.hasYPosSet = true;
        this.callBack = 1 + Calculations.random(4);
        
        super.create(helicopter);
    }
    
    @Override
    protected EnemyMissileType getMissileType()
    {
        return EnemyMissileType.BUSTER;
    }
    
    @Override
    protected Color getArmedBarrierColor()
    {
        return Colorations.bleachedCloaked;
    }
}
