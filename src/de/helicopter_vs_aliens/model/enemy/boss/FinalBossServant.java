package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;

public abstract class FinalBossServant extends BossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        selectAsFinalBossServant(this);
    
        super.doTypeSpecificInitialization();
    }
    
    public static void selectAsFinalBossServant(Enemy enemy)
    {
        Events.boss.operator.servants[enemy.id()] = enemy;
        enemy.hasYPosSet = true;
    }
}
