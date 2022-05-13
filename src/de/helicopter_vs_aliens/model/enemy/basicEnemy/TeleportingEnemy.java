package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class TeleportingEnemy extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.model = CARGO;
    
        this.primaryColor = new Color(190 + Calculations.random(40),
            10 + Calculations.random(60),
            15 + Calculations.random(60));
        this.setInitialWidth();
        this.targetSpeedLevel.setLocation( 1 + Math.random(),
            0.5*Math.random());
        this.teleportTimer = READY;
        this.canKamikaze = true;
    
        super.create(helicopter);
    }
}
