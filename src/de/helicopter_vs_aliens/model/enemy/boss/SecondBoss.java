package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;

public class SecondBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canMoveChaotic = true;
        this.shootTimer = 0;
        this.shootingRate = 5;
        this.shotSpeed = 3;
        this.canInstantTurn = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void bossTypeSpecificDestructionEffect(GameRessourceProvider gameRessourceProvider)
    {
        boss.setLocation(this.getCenterX(),
            this.getCenterY());
        EnemyController.makeBossTwoServants = true;
    }
}
