package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class LonelySpeeder extends Speeder
{
    @Override
    protected void create(Helicopter helicopter)
    {
        initializeBolt();
        this.targetSpeedLevel.setLocation(  12  + 3.5 * Math.random(),
            0.5 + 3   * Math.random());
        if(Calculations.tossUp()){this.callBack = 1;}
        
        super.create(helicopter);
    }
}
