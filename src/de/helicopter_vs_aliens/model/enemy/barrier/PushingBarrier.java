package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

public class PushingBarrier extends Barrier
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = Colorations.bleachedOrange;
        this.targetSpeedLevel.setLocation(0.5 + 2*Math.random(), 0);
        this.setVarWidth(105);
        if(this.targetSpeedLevel.getX() >= 5){this.direction.x = 1;}
    
        this.setLocation(this.targetSpeedLevel.getX() >= 5
                ? -this.bounds.getWidth()-APPEARANCE_DISTANCE
                : this.bounds.getX(),
            GROUND_Y - this.bounds.getWidth() - (5 + Calculations.random(11)));
        this.hasYPosSet = true;
        
        super.create(helicopter);
    }
}
