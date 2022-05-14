package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class CrazyEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = new Color((180 + Calculations.random(50)),
                                      (230 + Calculations.random(20)),
                                      ( 20 + Calculations.random(60)));
        this.targetSpeedLevel.setLocation( 4 + 2.5 * Math.random(),
            0.5 + Math.random());
        this.canExplode = true;
        this.canChaosSpeedup = true;
        this.canDodge = true;
    
        super.doTypeSpecificInitialization();
    }
}
