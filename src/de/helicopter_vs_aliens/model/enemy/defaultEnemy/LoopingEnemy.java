package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

public class LoopingEnemy extends StandardEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = Colorations.cloaked;
        this.setHitPoints(330);
        this.setVarWidth(105);
        this.targetSpeedLevel.setLocation(9, 11);
    
        this.direction.y = -1;
        this.setInitialY(TURN_FRAME.getCenterY());
        this.cloakingTimer = 0;
        this.canLoop = true;
        
        super.create(helicopter);
    }}
