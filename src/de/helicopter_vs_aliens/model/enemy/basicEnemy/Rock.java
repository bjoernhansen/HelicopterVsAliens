package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

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
    protected void finalizeInitialization(Helicopter helicopter)
    {
        helicopter.numberOfEnemiesSeen--;
        currentRock = this;
        super.finalizeInitialization(helicopter);
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
    public boolean isRemainingAfterEnteringRepairShop()
    {
        return true;
    }
}
