package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.enemy.EnemyModelType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

public class Kaboom extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.targetSpeedLevel.setLocation(0.5 + 0.5*Math.random(), 0);
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void finalizeInitialization(Helicopter helicopter)
    {
        helicopter.numberOfEnemiesSeen--;
        super.finalizeInitialization(helicopter);
    }
    
    @Override
    protected boolean canBecomeMiniBoss()
    {
        return false;
    }
    
    @Override
    protected boolean isMeetingRequirementsForGlowingEyes()
    {
        return true;
    }
    
    @Override
    protected double calculateInitialY()
    {
        return getOnTheGroundY() - getHeight();
    }
    
    @Override
    protected int hitPointVariance()
    {
        return 0;
    }
}
