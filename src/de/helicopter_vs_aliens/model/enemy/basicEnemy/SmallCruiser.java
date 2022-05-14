package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class SmallCruiser extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = new Color((140 + Calculations.random(25)),
                                      ( 65 + Calculations.random(35)),
                                      (      Calculations.random(25)));
                          
        this.targetSpeedLevel.setLocation(1 + 1.5*Math.random(),
                                          0.5*Math.random());
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected int hitPointVariance()
    {
        return Calculations.random(type.getHitPoints());
    }
}
