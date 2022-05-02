package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class Runabout extends StandardEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((100 + Calculations.random(30)),
            (100 + Calculations.random(30)),
            (40 + Calculations.random(25)));
        this.hitpoints = 2 + Calculations.random(2);
        this.setVarWidth(100);
        this.targetSpeedLevel.setLocation(2 + 2*Math.random(),
            2.5 + 1.5*Math.random());
        this.canExplode = true;
        
        super.create(helicopter);
    }
}
