package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class Dodger extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((85 + Calculations.random(20)),
            (35 + Calculations.random(30)),
            (95 + Calculations.random(30)));
        this.setVarWidth(170);
        this.targetSpeedLevel.setLocation(1.5 + 1.5*Math.random(),
            0.5*Math.random());
        this.canDodge = true;
        
        super.create(helicopter);
    }
}
