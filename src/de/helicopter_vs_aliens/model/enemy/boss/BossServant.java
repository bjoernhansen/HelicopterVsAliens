package de.helicopter_vs_aliens.model.enemy.boss;

public abstract class BossServant extends BossEnemy
{
    @Override
    protected double calculateInitialX()
    {
        return boss.getX();
    }
    
    @Override
    protected double calculateInitialY()
    {
        return boss.getY();
    }
}
