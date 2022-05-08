package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class Freighter extends BasicEnemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.model = CARGO;
        this.primaryColor = new Color((100 + Calculations.random(30)),
                                (50 + Calculations.random(30)),
                                (45 + Calculations.random(20)));
        this.setVarWidth(145);
        this.targetSpeedLevel.setLocation(0.5 + Math.random(),
            0.5*Math.random());
        this.canEarlyTurn = true;
        this.canTurn = true;
        
        super.create(helicopter);
    }
}
