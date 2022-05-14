package de.helicopter_vs_aliens.model.enemy.boss;

import java.awt.Color;

public class SmallShieldMaker extends ShieldMaker
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.targetSpeedLevel.setLocation(7, 6.5);
        this.primaryColor = new Color(25, 125, 105);
    
        super.doTypeSpecificInitialization();
    }
}
