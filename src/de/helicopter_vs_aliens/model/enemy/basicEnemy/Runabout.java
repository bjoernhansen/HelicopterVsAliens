package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class Runabout extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = new Color((100 + Calculations.random(30)),
                                      (100 + Calculations.random(30)),
                                      ( 40 + Calculations.random(25)));
        this.targetSpeedLevel.setLocation(2.0 + 2.0 * Math.random(),
                                          2.5 + 1.5 * Math.random());
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected int hitPointVariance()
    {
        return Calculations.random(type.getHitPoints());
    }
}
