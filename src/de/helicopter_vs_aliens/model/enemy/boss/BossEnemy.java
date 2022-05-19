package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;

public abstract class BossEnemy extends StandardEnemy
{
    private static final int HEALED_HIT_POINTS = 11;
    
    @Override
    protected int getRewardModifier()
    {
        return 0;
    }
    
    @Override
    protected boolean isMeetingRequirementsForGlowingEyes()
    {
        return true;
    }
    
    @Override
    protected int getWidthVariance()
    {
        return 0;
    }
   
    protected void healHitPoints()
    {
        int newHitPoints = Math.min(Events.boss.getHitPoints() + HEALED_HIT_POINTS,
            Events.boss.startingHitPoints);
        this.setHitPoints(newHitPoints);
    }
}
