package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.util.Colorations;

public class LoopingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.direction.y = -1;
        this.cloakingTimer = 0;
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
        if(this.direction.x == -1 && this.getY()-155>0)
        {
            this.direction.x = 1;
            this.getSpeedLevel()
                .setLocation(11, this.getSpeedLevel().getY());
        }
        else if(this.direction.x == 1 && this.getY()-155<0)
        {
            this.direction.x = -1;
            this.getSpeedLevel()
                .setLocation(7.5, this.getSpeedLevel().getY());
        }
    }
}
