package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.util.Calculations;

public abstract class ShieldMaker extends FinalBossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.direction.x = Calculations.randomDirection();
        this.shieldMakerTimer = READY;
        this.setShieldingPosition();
    
        super.doTypeSpecificInitialization();
    }
}
