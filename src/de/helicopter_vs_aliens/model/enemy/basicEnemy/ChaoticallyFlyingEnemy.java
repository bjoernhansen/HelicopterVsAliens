package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class ChaoticallyFlyingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.targetSpeedLevel.setLocation( 3.5 + 1.5*Math.random(),
            6.5 + 2*Math.random());
        this.canMoveChaotic = true;
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
}
