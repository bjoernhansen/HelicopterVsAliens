package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class SinusoidallyFlyingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canSinusMove = true;
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected double calculateInitialY()
    {
        return TURN_FRAME.getCenterY();
    }
}
