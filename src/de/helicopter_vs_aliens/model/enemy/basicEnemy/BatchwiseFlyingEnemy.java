package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class BatchwiseFlyingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.targetSpeedLevel.setLocation(	7 + 4*Math.random(),
                                            1 + 0.5*Math.random());
        this.batchWiseMove = 1;
    
        super.doTypeSpecificInitialization();
    }
}
