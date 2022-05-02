package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;

public class BigShieldMaker extends ShieldMaker
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.targetSpeedLevel.setLocation(6.5, 7);
        this.primaryColor = new Color(105, 135, 65);
        this.hitpoints = 4250;
    
        this.shootTimer = 0;
        this.shootingRate = 25;
        this.shotSpeed = 1;
        
        super.create(helicopter);
    }
}
