package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public class Bodyguard extends FinalBossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        cloakingTimer = 0;
        canInstantTurn = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        bodyguardAction();
        super.performFlightManeuver(gameRessourceProvider);
    }
    
    private void bodyguardAction()
    {
        if(Events.boss.shield < 1)
        {
            canKamikaze = true;
            getSpeedLevel().setLocation(7.5, getSpeedLevel().getY());
        }
        else
        {
            canKamikaze = false;
            getSpeedLevel().setLocation(targetSpeedLevel);
        }
    }
}
