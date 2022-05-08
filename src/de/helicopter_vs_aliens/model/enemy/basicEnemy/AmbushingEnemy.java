package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class AmbushingEnemy extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color( 30 + Calculations.random(40),
            60 + Calculations.random(40),
            120 + Calculations.random(40));
        this.setVarWidth(95);
        this.targetSpeedLevel.setLocation( 1 + 1.5*Math.random(), 0);
    
        this.canExplode = true;
        this.speedup = READY;
        
        super.create(helicopter);
    }
}
