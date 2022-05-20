package de.helicopter_vs_aliens.model.enemy.boss;

public class SecondBossServant extends BossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.setRandomDirectionX();
        this.invincibleTimer = 67;
    
        super.doTypeSpecificInitialization();
    }
}
