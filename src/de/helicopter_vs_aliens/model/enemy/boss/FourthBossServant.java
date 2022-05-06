package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class FourthBossServant extends BossEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.bounds.setRect(boss.getX(),
            boss.getY(),
            85 + Calculations.random(15),
            this.bounds.getHeight());
        this.hasYPosSet = true;
        this.primaryColor = new Color(80 + Calculations.random(20), 80 + Calculations.random(20), 80 + Calculations.random(20));
        this.hitPoints = 100 + Calculations.random(50);
        this.targetSpeedLevel.setLocation(6 + 2.5*Math.random(),
            6 + 2.5*Math.random());
        this.direction.x = Calculations.randomDirection();
        this.canExplode = true;
        
        super.create(helicopter);
    }
}
