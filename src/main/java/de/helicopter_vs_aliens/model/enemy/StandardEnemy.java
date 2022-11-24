package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.util.Calculations;

public abstract class StandardEnemy extends Enemy
{
    private boolean
        isSpeedBoosted;
    
    @Override
    public void reset()
    {
        super.reset();
        isSpeedBoosted = false;
    }
    
    @Override
    protected void doTypeSpecificInitialization()
    {
    }
    
    @Override
    public boolean areALlRequirementsForPowerUpDropMet()
    {
        return !Events.isCurrentLevelBossLevel() && canDropPowerUp();
    }
    
    protected boolean canDropPowerUp()
    {
        return Calculations.tossUp(POWER_UP_PROB) && Events.level >= MIN_POWER_UP_LEVEL;
    }
    
    @Override
    protected void evaluateSpeedBoost()
    {
        int bottomTurnLine = getBottomTurnLine();
        
        if(isSpeedBoosted)
        {
            if(    getMinY() > TURN_FRAME.getMinY()
                && getMaxY() < bottomTurnLine)
            {
                getSpeedLevel().setLocation(targetSpeedLevel);
                isSpeedBoosted = false;
            }
        }
        else if(stoppingBarrier != null
            &&(     getMinY() < TURN_FRAME.getMinY()
            || (getMaxY() > bottomTurnLine)))
        {
            isSpeedBoosted = true;
            getSpeedLevel().setLocation(Math.max(getSpeedLevel().getX(), targetSpeedLevel.getX() + 7.5),
                Math.max(getSpeedLevel().getY(), 5.5));
            
            // Wenn Gegner droht am Boden durch Barrier zerdrückt zu werden, dann nimmt Gegner den kürzesten Weg.
            if(mustAvoidGroundCollision(bottomTurnLine))
            {
                performXTurnAtBarrier();
            }
        }
    }
    
    protected int getBottomTurnLine()
    {
        return (int) TURN_FRAME.getMaxY();
    }
    
    private boolean mustAvoidGroundCollision(int yTurnLine)
    {
        return getMaxY() > yTurnLine
            &&(   (isFlyingRight()
            && getCenterX() < stoppingBarrier.getCenterX())
            ||(isFlyingLeft()
            && getCenterX() > stoppingBarrier.getCenterX()));
    }
}
