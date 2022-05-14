package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class BatchwiseFlyingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = new Color((135 + Calculations.random(30)),
                                      ( 80+ Calculations.random(20)),
                                      ( 85 + Calculations.random(30)));
        this.targetSpeedLevel.setLocation(	7 + 4*Math.random(),
                                            1 + 0.5*Math.random());
        this.batchWiseMove = 1;
    
        super.doTypeSpecificInitialization();
    }
}
