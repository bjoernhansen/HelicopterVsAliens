package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class CallbackEnemy extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((70 + Calculations.random(40)),
            (130 + Calculations.random(50)),
            (30 + Calculations.random(45)));
        this.setVarWidth(95);
        this.targetSpeedLevel.setLocation( 5.5 + 2.5*Math.random(),
            5 + 2*Math.random());
        this.canExplode = true;
        this.callBack = 1;
        
        super.create(helicopter);
    }
}
