package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.util.Calculations;

public class EscapedSpeeder extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.callBack = 1 + Calculations.random(3);
        this.setRandomDirectionX();
        this.invincibleTimer = 67;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected boolean canBecomeMiniBoss()
    {
        return false;
    }
    
    @Override
    protected double calculateInitialX()
    {
        return EnemyController.carrierDestroyedJustNow.getCenterX();
    }
    
    @Override
    protected double calculateInitialY()
    {
        return EnemyController.carrierDestroyedJustNow.getCenterY();
    }
}
