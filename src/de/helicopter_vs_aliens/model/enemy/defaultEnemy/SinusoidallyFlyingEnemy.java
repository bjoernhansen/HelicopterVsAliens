package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class SinusoidallyFlyingEnemy extends StandardEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.primaryColor = new Color((185 + Calculations.random(40)),
            ( 70 + Calculations.random(30)),
            (135 + Calculations.random(40)));
        this.setHitPoints(6);
        this.setVarWidth(110);
        this.targetSpeedLevel.setLocation(2.5 + 2.5*Math.random(), 11);
    
        this.setInitialY(TURN_FRAME.getCenterY());
        this.canSinusMove = true;
        this.canExplode = true;
        
        super.create(helicopter);
    }}
