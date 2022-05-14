package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class SecondBossServant extends BossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = new Color(80 + Calculations.random(25), 80 + Calculations.random(25), 80 + Calculations.random(25));
        this.targetSpeedLevel.setLocation(  3 + 10.5*Math.random(),
                                            3 + 10.5*Math.random());
        this.direction.x = Calculations.randomDirection();
        this.invincibleTimer = 67;
    
        super.doTypeSpecificInitialization();
    }
}
