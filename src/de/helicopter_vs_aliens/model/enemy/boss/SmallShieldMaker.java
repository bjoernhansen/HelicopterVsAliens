package de.helicopter_vs_aliens.model.enemy.boss;

import java.awt.Color;

public class SmallShieldMaker extends ShieldMaker
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.targetSpeedLevel.setLocation(7, 6.5);
    
        super.doTypeSpecificInitialization();
    }
}
