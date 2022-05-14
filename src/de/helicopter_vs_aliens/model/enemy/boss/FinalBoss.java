package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.util.Colorations;

public class FinalBoss extends BossEnemy
{
    private static final int
        FINAL_BOSS_POSITION_Y = 98;
        
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = Colorations.brown;
        this.targetSpeedLevel.setLocation(23.5, 0);
        maxNr = 5; // TODO diese Zuweisung gehört hier nicht her oder?
        this.operator = new Enemy.FinalEnemyOperator();
        this.isStunable = false;
        this.dimFactor = 1.3f;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }

    @Override
    protected double calculateInitialY()
    {
        return FINAL_BOSS_POSITION_Y;
    }
}
