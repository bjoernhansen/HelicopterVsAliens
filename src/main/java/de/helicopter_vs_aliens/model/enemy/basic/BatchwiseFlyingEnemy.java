package de.helicopter_vs_aliens.model.enemy.basic;

public class BatchwiseFlyingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.batchWiseMove = 1;
    
        super.doTypeSpecificInitialization();
    }
}
