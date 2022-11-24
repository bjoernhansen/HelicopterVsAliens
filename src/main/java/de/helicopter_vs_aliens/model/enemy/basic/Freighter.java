package de.helicopter_vs_aliens.model.enemy.basic;

public class Freighter extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.isAbleToTurnAroundEarly = true;
        this.canTurn = true;
    
        super.doTypeSpecificInitialization();
    }
}
