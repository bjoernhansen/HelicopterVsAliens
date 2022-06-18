package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;

public abstract class FinalBossServant extends BossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        selectAsFinalBossServant(this);
    
        super.doTypeSpecificInitialization();
    }
    
    public static void selectAsFinalBossServant(Enemy enemy)
    {
        Events.boss.operator.putServant(enemy);
    }
    
    @Override
    protected void bossTypeSpecificDestructionEffect(GameRessourceProvider gameRessourceProvider)
    {
        finalBossServantRemoval();
    }
}
