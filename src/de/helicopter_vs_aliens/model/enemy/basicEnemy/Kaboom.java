package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;

public class Kaboom extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = Color.white;
        this.setVarWidth(KABOOM_WIDTH);
        this.targetSpeedLevel.setLocation(0.5 + 0.5*Math.random(), 0);
        this.canExplode = true;
        this.setInitialY(GROUND_Y - 2*this.bounds.getWidth()*HEIGHT_FACTOR);
        
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
