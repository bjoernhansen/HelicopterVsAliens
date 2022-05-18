package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class Freighter extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canEarlyTurn = true;
        this.canTurn = true;
    
        super.doTypeSpecificInitialization();
    }
}
