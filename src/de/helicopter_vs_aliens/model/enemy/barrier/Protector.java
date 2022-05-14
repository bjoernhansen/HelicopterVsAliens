package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.model.enemy.boss.FinalBossServant;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public class Protector extends BurrowingBarrier
{
    private static final int
        POSITION_X = 919,
        SHOOT_PAUSE = 0,
        SHOOTING_RATE = 25,
        SHOTS_PER_CYCLE = 5,
        SHOOT_SPEED = 10;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.isStunable = false;
        FinalBossServant.selectAsFinalBossServant(this);
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected int calculateShootPause()
    {
        return SHOOT_PAUSE;
    }
    
    @Override
    protected int calculateShootingRate()
    {
        return SHOOTING_RATE;
    }
    
    @Override
    protected int calculateShotsPerCycle()
    {
        return SHOTS_PER_CYCLE;
    }
    
    @Override
    protected int calculateShootSpeed()
    {
        return SHOOT_SPEED;
    }
    
    @Override
    protected boolean isArmingWithBusterMissilesApproved()
    {
        return true;
    }
    
    @Override
    protected double calculateInitialX()
    {
        return POSITION_X;
    }
    
    @Override
    protected double calculateInitialY()
    {
        return GROUND_Y;
    }
    
    @Override
    protected int getWidthVariance()
    {
        return 0;
    }
    
    @Override
    public boolean isRemainingAfterEnteringRepairShop()
    {
        return false;
    }
}
