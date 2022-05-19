package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class SmallCruiser extends BasicEnemy
{
    @Override
    protected int hitPointVariance()
    {
        return Calculations.random(type.getHitPoints());
    }
}
