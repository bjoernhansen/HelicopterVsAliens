package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.EnemyType;

public abstract class BossServant extends BossEnemy
{
    @Override
    protected double calculateInitialX()
    {
        return boss.getX();
    }
    
    @Override
    protected double calculateInitialY()
    {
        return boss.getY();
    }
    
    @Override
    public boolean canCountForKillsAfterLevelUp()
    {
        return false;
    }
    
    @Override
    protected void bossInactivationEvent(){}
}
