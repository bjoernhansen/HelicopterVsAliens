package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public abstract class Speeder extends BasicEnemy
{
    protected void initializeBolt()
    {
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
}
