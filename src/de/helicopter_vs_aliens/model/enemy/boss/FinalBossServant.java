package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public abstract class FinalBossServant extends BossEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        selectAsFinalBossServant(this);
        
        super.create(helicopter);
    }
    
    public static void selectAsFinalBossServant(Enemy enemy)
    {
        Events.boss.operator.servants[enemy.id()] = enemy;
        enemy.hasYPosSet = true;
    }
}
