package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.util.Colorations;

public class Bodyguard extends FinalBossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        primaryColor = Colorations.cloaked;
        targetSpeedLevel.setLocation(1, 2);
        cloakingTimer = 0;
        canInstantTurn = true;
    
        super.doTypeSpecificInitialization();
    }
}
