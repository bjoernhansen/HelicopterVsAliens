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
}
