package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public abstract class Speeder extends StandardEnemy
{
    protected void initializeBolt()
    {
        this.primaryColor = new Color(75 + Calculations.random(30),
            75 + Calculations.random(30),
            75 + Calculations.random(30) );
        this.setHitPoints(26);
        this.setVarWidth(70);
        
        this.canExplode = true;
    }
}
