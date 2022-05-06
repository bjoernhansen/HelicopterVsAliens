package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;

abstract class Barrier extends Enemy
{
    private static final float
        DIM_FACTOR = 0.75f;
    
    @Override
    protected void create(Helicopter helicopter)
    {
        this.model = BARRIER;
    
        helicopter.numberOfEnemiesSeen--;
        this.hitPoints = Integer.MAX_VALUE;
        this.rotorColor = 1;
        this.isClockwiseBarrier = Calculations.tossUp();
        this.secondaryColor = Colorations.dimColor(this.primaryColor, DIM_FACTOR);
        this.deactivationProb = 1.0f / this.type.getStrength();
    
        if(Events.timeOfDay == NIGHT)
        {
            this.primaryColor = Colorations.dimColor(this.primaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
            this.secondaryColor = Colorations.dimColor(this.secondaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
        }
        barrierTimer = (int)((helicopter.getBounds().getWidth() + this.bounds.getWidth())/2);
 
        super.create(helicopter);
    }
}
