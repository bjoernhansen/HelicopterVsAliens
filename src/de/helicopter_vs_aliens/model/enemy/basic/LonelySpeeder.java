package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.util.Calculations;

public class LonelySpeeder extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        if(Calculations.tossUp()){this.callBack = 1;}
    
        super.doTypeSpecificInitialization();
    }
}
