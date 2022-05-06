package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class SecondBossServant extends BossEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.bounds.setRect(boss.getX(),
            boss.getY(),
            65,
            this.bounds.getHeight());
        this.hasYPosSet = true;
        this.primaryColor = new Color(80 + Calculations.random(25), 80 + Calculations.random(25), 80 + Calculations.random(25));
        this.hitPoints = 15;
        this.targetSpeedLevel.setLocation(3 + 10.5*Math.random(),
            3 + 10.5*Math.random());
    
        this.direction.x = Calculations.randomDirection();
        this.invincibleTimer = 67;
        
        super.create(helicopter);
    }}
