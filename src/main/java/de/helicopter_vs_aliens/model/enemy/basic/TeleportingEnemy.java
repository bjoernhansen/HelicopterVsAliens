package de.helicopter_vs_aliens.model.enemy.basic;

public class TeleportingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.teleportTimer = READY;
        this.canKamikaze = true;
    
        super.doTypeSpecificInitialization();
    }
}
