package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.control.EnemyController;

public class Rock extends BasicEnemy
{
    private static final int
        BURIAL_DEPTH = 15;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        becomeInvincibleForever();
    
        super.doTypeSpecificInitialization();
    }
    
    private void becomeInvincibleForever()
    {
        invincibleTimer = Integer.MAX_VALUE;
    }
    
    @Override
    protected void finalizeInitialization()
    {
        EnemyController.currentRock = this;
        super.finalizeInitialization();
    }
    
    @Override
    protected boolean canBecomeMiniBoss()
    {
        return false;
    }

    @Override
    protected double calculateInitialY()
    {
        return getOnTheGroundY() + BURIAL_DEPTH;
    }
    
    @Override
    protected int getWidthVariance()
    {
        return 0;
    }
    
    @Override
    public boolean isDisappearingAfterEnteringRepairShop()
    {
        return false;
    }
    
    @Override
    protected void prepareRemoval()
    {
        super.prepareRemoval();
        EnemyController.removeCurrentRock();
    }
    
    @Override
    protected boolean hasDeadlyGroundContact()
    {
        return false;
    }
    
    @Override
    protected void checkForBarrierCollision(){}
    
    @Override
    public boolean countsForTotalAmountOfEnemiesSeen()
    {
        return false;
    }
    
    @Override
    public boolean isVisibleNonBarricadeVessel()
    {
        return false;
    }
}
