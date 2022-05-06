package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

public class ThirdBoss extends BossEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.setWidth(250);
        this.primaryColor = Colorations.cloaked;
        this.hitPoints = 1750;
        this.targetSpeedLevel.setLocation(5, 4);
    
        this.canMoveChaotic = true;
        this.canKamikaze = true;
        this.cloakingTimer = READY;
        this.canDodge = true;
        this.shootTimer = 0;
        this.shootingRate = 10;
        this.shotSpeed = 10;
        this.canInstantTurn = true;
    
        Events.boss = this;
        
        super.create(helicopter);
    }
}
