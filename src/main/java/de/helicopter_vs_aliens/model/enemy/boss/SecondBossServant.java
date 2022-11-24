package de.helicopter_vs_aliens.model.enemy.boss;

public class SecondBossServant extends BossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        getNavigationDevice().setRandomDirectionX();
        invincibleTimer = 67;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    public boolean canCountForKillsAfterLevelUp()
    {
        return true;
    }
}
