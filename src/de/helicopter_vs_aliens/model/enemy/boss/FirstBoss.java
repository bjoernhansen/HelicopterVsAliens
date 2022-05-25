package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.util.Calculations;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;

public class FirstBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canKamikaze = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    public boolean areALlRequirementsForPowerUpDropMet()
    {
        return true;
    }
}
