package de.helicopter_vs_aliens.model.enemy.basic;

public class LoopingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        getNavigationDevice().flyUp();
        setCloakingDeviceReadyForUse();
        canSinusMove = true;
        canLoop = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected double calculateInitialY()
    {
        return TURN_FRAME.getCenterY();
    }
    
    @Override
    protected void sinusLoop()
    {
        super.sinusLoop();
        if(isFlyingLeft() && getY()-155>0)
        {
            getNavigationDevice().turnRight();
            getSpeedLevel().setLocation(11, getSpeedLevel().getY());
        }
        else if(isFlyingRight() && getY()-155<0)
        {
            getNavigationDevice().turnLeft();
            getSpeedLevel().setLocation(7.5, getSpeedLevel().getY());
        }
    }
}
