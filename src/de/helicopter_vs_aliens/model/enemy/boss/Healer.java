package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class Healer extends FinalBossServant
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.model = CARGO;
    
        this.bounds.setRect(boss.getX(),
            boss.getY(),
            115,
            this.bounds.getHeight());
        this.primaryColor = Color.white;
        this.hitpoints = 3500;
        this.targetSpeedLevel.setLocation(2.5, 3);
        this.canDodge = true;
        
        super.create(helicopter);
    }
}
