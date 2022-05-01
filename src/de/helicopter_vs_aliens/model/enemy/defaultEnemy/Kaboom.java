package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

public class Kaboom extends DefaultEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.createKaboom();
        helicopter.numberOfEnemiesSeen--;
        super.create(helicopter);
    }
    
    private void createKaboom()
    {
        this.farbe1 = Color.white;
        this.hitpoints = Integer.MAX_VALUE;
        this.setVarWidth(KABOOM_WIDTH);
        this.targetSpeedLevel.setLocation(0.5 + 0.5*Math.random(), 0);
        this.canExplode = true;
        this.setInitialY(GROUND_Y - 2*this.bounds.getWidth()*HEIGHT_FACTOR);
    }
}
