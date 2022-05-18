package de.helicopter_vs_aliens.model.enemy.boss;

import java.awt.Color;

public class BigShieldMaker extends ShieldMaker
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        shootTimer = 0;
        shootingRate = 25;
        shotSpeed = 1;
        
        super.doTypeSpecificInitialization();
    }
}
