package de.helicopter_vs_aliens.model.enemy.basic;

public class CloakedEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canLearnKamikaze = true;
        this.canInstantTurn = true;
        this.cloakingTimer = CLOAKING_TIME + CLOAKED_TIME;
        this.uncloakingSpeed = 2;
        this.canEarlyTurn = true;
    
        super.doTypeSpecificInitialization();
    }
}
