package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;

public final class Bodyguard extends FinalBossServant
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
    protected void performFlightManeuver()
    {
        bodyguardAction();
        super.performFlightManeuver();
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
            reachTargetSpeedLevel();
        }
    }
    
    @Override
    protected double getRightBoundary()
    {
        return RIGHT_BOUNDARY;
    }
}
