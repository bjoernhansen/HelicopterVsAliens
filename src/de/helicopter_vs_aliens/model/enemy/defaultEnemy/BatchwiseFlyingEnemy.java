package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class BatchwiseFlyingEnemy extends StandardEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((135 + Calculations.random(30)),
            (80+ Calculations.random(20)),
            (85 + Calculations.random(30)));
        this.setHitPoints(16);
        this.setVarWidth(130);
        this.targetSpeedLevel.setLocation(	7 + 4*Math.random(),
            1 + 0.5*Math.random());
        this.batchWiseMove = 1;
        
        super.create(helicopter);
    }
}
