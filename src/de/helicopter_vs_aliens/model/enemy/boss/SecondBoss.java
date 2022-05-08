package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class SecondBoss extends BossEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.model = CARGO;
        this.primaryColor = new Color(85, 85, 85);
        this.setWidth(250);
        this.targetSpeedLevel.setLocation(7, 8);
    
        this.canMoveChaotic = true;
        this.shootTimer = 0;
        this.shootingRate = 5;
        this.shotSpeed = 3;
        this.canInstantTurn = true;
    
        Events.boss = this;
        
        super.create(helicopter);
    }
}
