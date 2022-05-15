package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.EnemyType;

import java.awt.Color;

public class FourthBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.targetSpeedLevel.setLocation(10, 10);
        this.spawningHornetTimer = 30;
        nextBossEnemyType = EnemyType.BOSS_4_SERVANT;
        maxNr = 15;
        this.canTurn = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
}
