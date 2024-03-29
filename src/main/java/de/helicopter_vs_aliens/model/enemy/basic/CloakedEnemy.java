package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.devices.CloakingDevice;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public class CloakedEnemy extends BasicEnemy
{
    private static final int
        CLOAKING_SPEED = 2;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        canInstantlyTurnAround = true;
        CloakingDevice cloakingDevice = getCloakingDevice();
        cloakingDevice.setToEndOfCloakedTime();
        cloakingDevice.setCloakingSpeed(CLOAKING_SPEED);
        isAbleToTurnAroundEarly = true;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void performHitTriggeredTurn()
    {
        startKamikazeMode();
        super.performHitTriggeredTurn();
    }
    @Override
    protected boolean isPreventedFromCloaking()
    {
        return false;
    }
    
    @Override
    protected void uncloakAndResetCloakingDevice()
    {
        uncloakAndDisableCloakingDevice();
    }
    
    @Override
    protected boolean hasCrossedRightBoundary()
    {
        return false;
    }
    
    @Override
    protected void adaptSpeedLevelForKamikaze(){}
    
    @Override
    protected boolean isReadyToContinueCloakingProcess()
    {
        return canFrontalSpeedup;
    }
    
    @Override
    protected boolean isUncloakingWhenDisabled()
    {
        return false;
    }
    
    @Override
    protected boolean prolongCloakingTimeWhenHitWhileCloaked()
    {
        return false;
    }
    
    @Override
    protected void calculateFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        if(isLearningKamikazeOn(gameRessourceProvider.getHelicopter()))
        {
            startKamikazeMode();
            getNavigationDevice().turnLeft();
        }
        super.calculateFlightManeuver(gameRessourceProvider);
    }
    
    private boolean isLearningKamikazeOn(Helicopter helicopter)
    {
        return isMovingAwayFromHelicopter()
                && turnTimer == READY
                && getDistanceOfMinX(helicopter) < KAMIKAZE_RANGE;
    }
    
    private double getDistanceOfMinX(RectangularGameEntity gameEntity)
    {
        return Math.abs(getMinX() - gameEntity.getMinX());
    }
}
