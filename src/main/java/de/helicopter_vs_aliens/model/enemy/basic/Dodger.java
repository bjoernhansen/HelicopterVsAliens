package de.helicopter_vs_aliens.model.enemy.basic;

public class Dodger extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canDodge = true;
    
        super.doTypeSpecificInitialization();
    }
}
