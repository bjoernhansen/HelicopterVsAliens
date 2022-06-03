package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;

public class Bodyguard extends FinalBossServant
{
    private static final int
        RIGHT_BOUNDARY = 660;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        setCloakingDeviceReadyForUse();
        canInstantlyTurnAround = true;
    
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
    
    @Override
    protected double getRightBoundary()
    {
        return RIGHT_BOUNDARY;
    }
}
