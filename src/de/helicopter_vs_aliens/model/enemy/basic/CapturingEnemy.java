package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.model.enemy.AbilityStatusType;

public class CapturingEnemy extends BasicEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.tractor = AbilityStatusType.READY;
    
        super.doTypeSpecificInitialization();
    }
}
