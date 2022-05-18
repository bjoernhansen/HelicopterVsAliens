package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.util.Colorations;

public class Bodyguard extends FinalBossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        cloakingTimer = 0;
        canInstantTurn = true;
    
        super.doTypeSpecificInitialization();
    }
}
