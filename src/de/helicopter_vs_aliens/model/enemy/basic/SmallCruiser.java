package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.util.Calculations;

public class SmallCruiser extends BasicEnemy
{
    @Override
    protected int hitPointVariance()
    {
        return Calculations.random(type.getHitPoints());
    }
}
