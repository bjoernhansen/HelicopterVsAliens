package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;


public abstract class FinalBossServant extends BossServant implements FinalBossAcquaintance
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        getFinalBoss().registerServant(this);
        
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void bossTypeSpecificDestructionEffect(GameRessourceProvider gameRessourceProvider)
    {
        finalBossServantRemoval();
    }
    
    @Override
    public FinalBoss getFinalBoss()
    {
        return (FinalBoss)Events.boss;
    }
    
    @Override
    public void finalBossServantRemoval()
    {
        getFinalBoss().removeServant(getType());
    }
}
