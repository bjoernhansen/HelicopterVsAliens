package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class AmbushingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.targetSpeedLevel.setLocation( 1 + 1.5*Math.random(), 0);
        this.canExplode = true;
        this.speedup = READY;
        
        super.doTypeSpecificInitialization();
    }
}
