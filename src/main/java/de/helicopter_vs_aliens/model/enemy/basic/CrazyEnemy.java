package de.helicopter_vs_aliens.model.enemy.basic;

public final class CrazyEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canChaosSpeedup = true;
        this.canDodge = true;
    
        super.doTypeSpecificInitialization();
    }
}
