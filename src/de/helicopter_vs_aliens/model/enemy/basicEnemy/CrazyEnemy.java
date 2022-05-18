package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class CrazyEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canExplode = true;
        this.canChaosSpeedup = true;
        this.canDodge = true;
    
        super.doTypeSpecificInitialization();
    }
}
