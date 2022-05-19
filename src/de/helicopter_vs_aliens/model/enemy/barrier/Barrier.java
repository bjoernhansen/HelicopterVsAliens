package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

abstract class Barrier extends Enemy
{
    private static final int
        WIDTH_VARIANCE_DIVISOR = 5;
    
    private static final float
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 0.75f;
    
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        rotorColor = 1;
        isClockwiseBarrier = Calculations.tossUp();
        deactivationProb = 1.0f / type.getStrength();
    }
    
    @Override
    protected void finalizeInitialization(Helicopter helicopter)
    {
        helicopter.numberOfEnemiesSeen--;
        EnemyController.barrierTimer = (int)((helicopter.getWidth() + getWidth())/2);
 
        super.finalizeInitialization(helicopter);
        
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
            + (this.getY() + this.getHeight()/2 < GROUND_Y/2f
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
    protected double calculateInitialY()
    {
        return Math.random() * getOnTheGroundY();
    }
    
    @Override
    protected int getWidthVarianceDivisor()
    {
        return WIDTH_VARIANCE_DIVISOR;
    }
    
    @Override
    public boolean isRemainingAfterEnteringRepairShop()
    {
        return true;
    }
    
    @Override
    protected float getPrimaryColorBrightnessFactor()
    {
        return Events.timeOfDay.getBrightnessFactor();
    }
    
    @Override
    protected float getSecondaryColorBrightnessFactor()
    {
        return SECONDARY_COLOR_BRIGHTNESS_FACTOR;
    }
}
