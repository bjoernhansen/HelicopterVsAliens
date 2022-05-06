package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class SmallCruiser extends StandardEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((140 + Calculations.random(25)),
            ( 65 + Calculations.random(35)),
            (  0 + Calculations.random(25)));
        this.hitPoints = 3 + Calculations.random(3);
        this.setVarWidth(125);
        this.targetSpeedLevel.setLocation(1 + 1.5*Math.random(),
            0.5*Math.random());
        this.canExplode = true;
        
        super.create(helicopter);
    }
}
