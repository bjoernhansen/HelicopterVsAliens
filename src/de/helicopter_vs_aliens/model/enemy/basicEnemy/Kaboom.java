package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

public class Kaboom extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = Color.white;
        this.setInitialWidth();
        this.targetSpeedLevel.setLocation(0.5 + 0.5*Math.random(), 0);
        this.canExplode = true;
        this.setFixedY(GROUND_Y - 2*this.bounds.getWidth()*HEIGHT_FACTOR);
        
        helicopter.numberOfEnemiesSeen--;
        
        super.create(helicopter);
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
}
