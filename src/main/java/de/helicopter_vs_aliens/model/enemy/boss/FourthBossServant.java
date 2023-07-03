package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.util.Calculations;

public class FourthBossServant extends BossServant
{
    private static final int
        WIDTH_VARIANCE = 15;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        getNavigationDevice().setRandomDirectionX();
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected int hitPointVariance()
    {
        return Calculations.random(getType().getHitPoints()/2);
    }
    
    @Override
    protected int getWidthVariance()
    {
        return Calculations.random(WIDTH_VARIANCE);
    }
}
