package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class Shooter extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.model = CARGO;
    
        this.primaryColor = new Color(80 + Calculations.random(25),
            80 + Calculations.random(25),
            80 + Calculations.random(25));
        this.setVarWidth(80);
        this.targetSpeedLevel.setLocation( 0.5 + Math.random(),
            0.5 * Math.random());
        this.canDodge = true;
        this.shootTimer = 0;
        this.shootingRate = 35;
        
        super.create(helicopter);
    }}
