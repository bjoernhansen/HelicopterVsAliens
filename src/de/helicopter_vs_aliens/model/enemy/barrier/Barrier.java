package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;

abstract class Barrier extends Enemy
{
    private static final int
        WIDTH_VARIANCE_DIVISOR = 5;
    
    private static final float
        DIM_FACTOR = 0.75f;
    
    
    @Override
    protected void create(Helicopter helicopter)
    {
        model = BARRIER;
    
        helicopter.numberOfEnemiesSeen--;
        rotorColor = 1;
        isClockwiseBarrier = Calculations.tossUp();
        secondaryColor = Colorations.dimColor(primaryColor, DIM_FACTOR);
        deactivationProb = 1.0f / type.getStrength();
    
        if(Events.timeOfDay == NIGHT)
        {
            primaryColor = Colorations.dimColor(primaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
            secondaryColor = Colorations.dimColor(secondaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
        }
        barrierTimer = (int)((helicopter.getBounds().getWidth() + bounds.getWidth())/2);
 
        super.create(helicopter);
        
        if(isShootingBarrier())
        {
            this.initializeShootDirectionOfBarriers();
        }
    }
    
    private boolean isShootingBarrier()
    {
        return this.barrierShootTimer == READY;
    }
    
    private void initializeShootDirectionOfBarriers()
    {
        double randomAngle
            = Math.PI * (1 + Math.random()/2)
            + (this.bounds.getY() + this.bounds.getHeight()/2 < GROUND_Y/2f
            ? Math.PI/2
            : 0);
        
        this.shootingDirection.setLocation(
            Math.sin(randomAngle),
            Math.cos(randomAngle) );
    }
    
    @Override
    protected boolean isMeetingRequirementsForGlowingEyes()
    {
        return this.snoozeTimer <= SNOOZE_TIME + 75;
    }
    
    @Override
    protected void setRandomY()
    {
        setFixedY(Math.random()*(GROUND_Y - this.bounds.getHeight()));
    }
    
    @Override
    protected int getWidthVarianceDivisor()
    {
        return WIDTH_VARIANCE_DIVISOR;
    }
}
