package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class ChaoticallyFlyingEnemy extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((150 + Calculations.random(20)),
            (130 + Calculations.random(25)),
            ( 75 + Calculations.random(30)));
        this.setVarWidth(125);
        this.targetSpeedLevel.setLocation( 3.5 + 1.5*Math.random(),
            6.5 + 2*Math.random());
        this.canMoveChaotic = true;
        this.canExplode = true;
        
        super.create(helicopter);
    }
}
