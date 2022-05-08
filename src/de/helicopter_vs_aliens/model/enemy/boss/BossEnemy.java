package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.enemy.StandardEnemy;

public abstract class BossEnemy extends StandardEnemy
{
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
}
