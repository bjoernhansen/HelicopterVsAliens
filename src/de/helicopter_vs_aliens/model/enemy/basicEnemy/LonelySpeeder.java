package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public class LonelySpeeder extends Speeder
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        initializeBolt();
        if(Calculations.tossUp()){this.callBack = 1;}
    
        super.doTypeSpecificInitialization();
    }
}
