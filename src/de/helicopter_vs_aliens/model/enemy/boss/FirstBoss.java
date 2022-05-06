package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

public class FirstBoss extends BossEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color(115, 70, 100);
        this.hitPoints = 225;
        this.setWidth(275);
        this.targetSpeedLevel.setLocation(2, 0.5);
    
        this.canKamikaze = true;
    
        Events.boss = this;
        
        super.create(helicopter);
    }
}
