package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.LevelManager;
import de.helicopter_vs_aliens.model.enemy.Enemy;

public class FinalBoss extends BossEnemy
{
    private static final int
        FINAL_BOSS_POSITION_Y = 98;
    
    public static final float
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 1.3f;
        
    @Override
    protected void doTypeSpecificInitialization()
    {
        LevelManager.maxNr = 5; // TODO diese Zuweisung gehört hier nicht her oder?
        this.operator = new Enemy.FinalEnemyOperator();
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }

    @Override
    protected double calculateInitialY()
    {
        return FINAL_BOSS_POSITION_Y;
    }
    
    @Override
    protected float getSecondaryColorBrightnessFactor()
    {
        return SECONDARY_COLOR_BRIGHTNESS_FACTOR;
    }
    
    @Override
    public boolean isStunable()
    {
        return false;
    }
}
