package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public class SecondBossServant extends BossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.direction.x = Calculations.randomDirection();
        this.invincibleTimer = 67;
    
        super.doTypeSpecificInitialization();
    }
}
