package de.helicopter_vs_aliens.model.enemy.basic;

public class LoopingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.flyUp();
        setCloakingDeviceReadyForUse();
        this.canSinusMove = true;
        this.canLoop = true;
    
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
        if(isFlyingLeft() && this.getY()-155>0)
        {
            this.turnRight();
            this.getSpeedLevel()
                .setLocation(11, this.getSpeedLevel().getY());
        }
        else if(this.isFlyingRight() && this.getY()-155<0)
        {
            this.turnLeft();
            this.getSpeedLevel()
                .setLocation(7.5, this.getSpeedLevel().getY());
        }
    }
}