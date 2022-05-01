package de.helicopter_vs_aliens.model.enemy.defaultEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class EscapedSpeeder extends Speeder
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.initializeEscapedSpeeder();
        super.create(helicopter);
    }
    
    private void initializeEscapedSpeeder()
    {
        initializeBolt();
        this.setLocation(carrierDestroyedJustNow.getBounds().getCenterX(),
                         carrierDestroyedJustNow.getBounds().getCenterY());
        this.hasYPosSet = true;
        this.targetSpeedLevel.setLocation(	10  + 7.5 * Math.random(),
                                            0.5 + 3   * Math.random());
        this.callBack = 1 + Calculations.random(3);
        this.direction.x = Calculations.randomDirection();
        this.invincibleTimer = 67;
    }
}
