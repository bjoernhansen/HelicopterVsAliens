package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;

public class Rock extends BasicEnemy
{
    private static final int
        ROCK_WIDTH = 300;
    
    @Override
    protected void create(Helicopter helicopter)
    {
        currentRock = this;
        this.model = CARGO;
    
        this.primaryColor = new Color((180 + Calculations.random(30)),
            (120 + Calculations.random(30)),
            (      Calculations.random(15)));
        this.invincibleTimer = Integer.MAX_VALUE;
    
        this.bounds.setRect(this.bounds.getX(),
            GROUND_Y - ROCK_WIDTH * (HEIGHT_FACTOR_SUPERSIZE - 0.05f),
            ROCK_WIDTH,
            ROCK_WIDTH * HEIGHT_FACTOR_SUPERSIZE);
        this.hasHeightSet = true;
        this.hasYPosSet = true;
        this.isLasting = true;
        
        helicopter.numberOfEnemiesSeen--;
        
        super.create(helicopter);
    }
    
    @Override
    protected boolean canBecomeMiniBoss()
    {
        return false;
    }
}
