package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

public class SmallShieldMaker extends ShieldMaker
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.targetSpeedLevel.setLocation(7, 6.5);
        this.primaryColor = new Color(25, 125, 105);
        
        super.create(helicopter);
    }
}
