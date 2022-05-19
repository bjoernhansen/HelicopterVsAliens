package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;

public class FirstBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canKamikaze = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
}
