package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;

public final class SecondBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        canMoveChaotic = true;
        shootTimer = 0;
        shootingRate = 5;
        shotSpeed = 3;
        canInstantlyTurnAround = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void bossTypeSpecificDestructionEffect()
    {
        boss.setLocation(getCenterX(),
                         getCenterY());
        EnemyController.makeBossTwoServants = true;
    }
}
