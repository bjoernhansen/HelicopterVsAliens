package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.util.Calculations;

public class PushingBarrier extends Barrier
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        if(isIntendedToAppearOnTheLeft()){getNavigationDevice().turnRight();}
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected double calculateInitialX()
    {
        return isIntendedToAppearOnTheLeft()
                ? -getWidth()-APPEARANCE_DISTANCE
                : super.calculateInitialX() ;
    }
    
    private boolean isIntendedToAppearOnTheLeft()
    {
        return targetSpeedLevel.getX() >= 5;
    }
    
    @Override
    protected double calculateInitialY()
    {
        return getOnTheGroundY() - calculateRandomDistanceFromTheGround();
    }
    
    private int calculateRandomDistanceFromTheGround()
    {
        return 5 + Calculations.random(11);
    }
    
    @Override
    public boolean isDisappearingAfterEnteringRepairShop()
    {
        return true;
    }
}
