package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Dimension;

public class FinalBoss extends BossEnemy
{
    private static final int
        FINAL_BOSS_STARTING_POSITION_Y = 98,
        FINAL_BOSS_WIDTH = 450;
    
    private static final Dimension
        FINAL_BOSS_DIMENSION = new Dimension(FINAL_BOSS_WIDTH, (int)HEIGHT_FACTOR * FINAL_BOSS_WIDTH );
    
    
    @Override
    protected void create(Helicopter helicopter)
    {
        this.bounds.setRect(this.bounds.getX(),
            FINAL_BOSS_STARTING_POSITION_Y,
            FINAL_BOSS_DIMENSION.width,
            FINAL_BOSS_DIMENSION.height);
        this.hasYPosSet = true;
        this.hasHeightSet = true;
    
        this.primaryColor = Colorations.brown;
        this.hitPoints = 25000;
        this.targetSpeedLevel.setLocation(23.5, 0);
    
        maxNr = 5;
        this.operator = new Enemy.FinalEnemyOperator();
        this.isStunable = false;
        this.dimFactor = 1.3f;
    
        Events.boss = this;
        
        super.create(helicopter);
    }
}
