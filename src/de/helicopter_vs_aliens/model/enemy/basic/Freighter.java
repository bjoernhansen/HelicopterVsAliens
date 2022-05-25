package de.helicopter_vs_aliens.model.enemy.basic;

public class Freighter extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canEarlyTurn = true;
        this.canTurn = true;
    
        super.doTypeSpecificInitialization();
    }
}
