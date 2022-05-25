package de.helicopter_vs_aliens.model.enemy.basic;

public class Shooter extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canDodge = true;
        this.shootTimer = 0;
        this.shootingRate = 35;
    
        super.doTypeSpecificInitialization();
    }
}
