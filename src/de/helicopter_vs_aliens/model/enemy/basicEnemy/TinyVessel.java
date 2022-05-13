package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class TinyVessel extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((180 + Calculations.random(30)),
                                (120 + Calculations.random(30)),
                                (      Calculations.random(15)));
        this.setInitialWidth();
        this.targetSpeedLevel.setLocation(0.5 + Math.random(),
                                          0.5 * Math.random());
        this.canExplode = true;
        this.dimFactor = 1.2f;
        
        super.create(helicopter);
    }
    
    @Override
    protected boolean canBecomeMiniBoss()
    {
        return false;
    }
}
