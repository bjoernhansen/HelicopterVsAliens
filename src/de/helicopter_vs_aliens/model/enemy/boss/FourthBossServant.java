package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class FourthBossServant extends BossServant
{
    private static final int
        WIDTH_VARIANCE = 15;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.primaryColor = new Color(80 + Calculations.random(20), 80 + Calculations.random(20), 80 + Calculations.random(20));
        this.targetSpeedLevel.setLocation(  6 + 2.5*Math.random(),
                                            6 + 2.5*Math.random());
        this.direction.x = Calculations.randomDirection();
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected int hitPointVariance()
    {
        return Calculations.random(type.getHitPoints()/2);
    }
    
    @Override
    protected int getWidthVariance()
    {
        return Calculations.random(WIDTH_VARIANCE);
    }
}
