package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.LevelManager;
import de.helicopter_vs_aliens.model.enemy.EnemyType;

public class FourthBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.spawningHornetTimer = 30;
        LevelManager.nextBossEnemyType = EnemyType.BOSS_4_SERVANT;
        LevelManager.maxNr = 15;
        this.canTurn = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
}
