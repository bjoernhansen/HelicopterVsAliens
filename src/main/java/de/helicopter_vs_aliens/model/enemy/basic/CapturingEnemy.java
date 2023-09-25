package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.AbilityStatusType;
import de.helicopter_vs_aliens.model.enemy.Enemy;


public class CapturingEnemy extends BasicEnemy
{
    private AbilityStatusType
        tractorDeviceActivityStatus; // = DISABLED (Gegner ohne Traktor); = READY (Traktor nicht aktiv); = ACTIVE (Traktor aktiv)
    
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        installTractorDevice();
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void calculateFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        super.calculateFlightManeuver(gameRessourceProvider);
 				
        if(isAbleToStopHelicopterByTractorBeam())
        {
            startTractor();
        }
    }
    
    private boolean isAbleToStopHelicopterByTractorBeam()
    {
        return isTractorReady()
                && isInRangeOfHelicopter()
                && getHelicopter().canBeStoppedByTractorBeam();
    }
    
    private boolean isInRangeOfHelicopter()
    {
        double
            helicopterX = getHelicopter().getX(),
            helicopterY = getHelicopter().getY();
        
        return helicopterX - getX() > -750
                && helicopterX - getX() < -50
                && helicopterY + 56 > getY() + 0.2 * getHeight()
                && helicopterY + 60 < getY() + 0.8 * getHeight();
    }
    
    private boolean isTractorReady() {
        return tractorDeviceActivityStatus == AbilityStatusType.READY
                && !isEmpSlowed()
                && !getCloakingDevice().isActive()
                && getMaxX() < Main.VIRTUAL_DIMENSION.width;
    }
    
    private void startTractor()
    {
        Audio.loop(Audio.tractorBeam);
        tractorDeviceActivityStatus = AbilityStatusType.ACTIVE;
        getSpeedLevel().setLocation(Enemy.ZERO_SPEED);
        getHelicopter().tractor = this;
        getNavigationDevice().turnLeft();
    }
    
    public void stopTractor()
    {
        removeTractorDeviceIfPresent();
        getSpeedLevel().setLocation(targetSpeedLevel);
    }
    
    public boolean isTractorBeamActive()
    {
        return tractorDeviceActivityStatus == AbilityStatusType.ACTIVE;
    }
    
    public AbilityStatusType getTractorDeviceActivityStatus()
    {
        return tractorDeviceActivityStatus;
    }
    
    public void installTractorDevice()
    {
        tractorDeviceActivityStatus = AbilityStatusType.READY;
    }
    
    public void removeTractorDeviceIfPresent()
    {
        tractorDeviceActivityStatus = AbilityStatusType.DISABLED;
    }
    
    @Override
    protected boolean canCloak()
    {
        return super.canCloak() && !isTractorBeamActive();
    }
    
    @Override
    protected boolean generatesEnergieBeam()
    {
        return getTractorDeviceActivityStatus() == AbilityStatusType.ACTIVE || super.generatesEnergieBeam();
    }
}
