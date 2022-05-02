package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class CrazyEnemy extends StandardEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((180 + Calculations.random(50)),
            (230 + Calculations.random(20)),
            (20 + Calculations.random(60)));
        this.setHitPoints(140);
        this.setVarWidth(115);
        this.targetSpeedLevel.setLocation( 4 + 2.5 * Math.random(),
            0.5 + Math.random());
        this.canExplode = true;
        this.canChaosSpeedup = true;
        this.canDodge = true;
        
        super.create(helicopter);
    }
}
