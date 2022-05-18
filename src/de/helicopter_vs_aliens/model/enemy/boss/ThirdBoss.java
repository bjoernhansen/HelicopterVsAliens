package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.util.Colorations;

public class ThirdBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canMoveChaotic = true;
        this.canKamikaze = true;
        this.cloakingTimer = READY;
        this.canDodge = true;
        this.shootTimer = 0;
        this.shootingRate = 10;
        this.shotSpeed = 10;
        this.canInstantTurn = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
}
