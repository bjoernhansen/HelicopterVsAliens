package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public abstract class FinalBossServant extends BossEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        Events.boss.operator.servants[this.id()] = this;
        this.hasYPosSet = true;
        
        super.create(helicopter);
    }
}
