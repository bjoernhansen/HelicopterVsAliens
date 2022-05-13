package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class Carrier extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.model = CARGO;
    
        this.primaryColor = new Color(70 + Calculations.random(15),
            60 + Calculations.random(10),
            45 + Calculations.random(10)); // new Color(25 + MyMath.random(35), 70 + MyMath.random(45), 25 + MyMath.random(35));
        this.setInitialWidth();
        this.targetSpeedLevel.setLocation( 0.5 + Math.random(),
            0.5 * Math.random());
        this.canEarlyTurn = true;
        this.isCarrier = true;
        this.canTurn = true;
        
        super.create(helicopter);
    }
}
