package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.util.Calculations;

public class FourthBossServant extends BossServant
{
    private static final int
        WIDTH_VARIANCE = 15;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.setRandomDirectionX();
    
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
