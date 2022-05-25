package de.helicopter_vs_aliens.model.enemy.basic;

public class AmbushingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.speedup = READY;
        
        super.doTypeSpecificInitialization();
    }
}
