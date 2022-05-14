package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class EscapedSpeeder extends Speeder
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        initializeBolt();
        this.targetSpeedLevel.setLocation(	10.0 + 7.5 * Math.random(),
                                             0.5 + 3   * Math.random());
        this.callBack = 1 + Calculations.random(3);
        this.direction.x = Calculations.randomDirection();
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
        return carrierDestroyedJustNow.getCenterX();
    }
    
    @Override
    protected double calculateInitialY()
    {
        return carrierDestroyedJustNow.getCenterY();
    }
}
