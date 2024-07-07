package de.helicopter_vs_aliens.model.enemy.basic;

public final class ChaoticallyFlyingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canMoveChaotic = true;
    
        super.doTypeSpecificInitialization();
    }
}
