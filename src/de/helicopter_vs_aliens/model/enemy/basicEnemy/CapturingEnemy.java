package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.enemy.AbilityStatusType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class CapturingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = new Color(    5 + Calculations.random(55),
                                        105 + Calculations.random(40),
                                         90 + Calculations.random(30));
        this.targetSpeedLevel.setLocation( 2.5 + 2*Math.random(),
            4.5 + 1.5*Math.random());
        this.tractor = AbilityStatusType.READY;
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
}
