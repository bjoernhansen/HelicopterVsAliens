package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.missile.Missile;

public class ThirdBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        canMoveChaotic = true;
        canKamikaze = true;
        setCloakingDeviceReadyForUse();
        canDodge = true;
        shootTimer = 0;
        shootingRate = 10;
        shotSpeed = 10;
        canInstantlyTurnAround = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected boolean hasDeadlyShots()
    {
        return true;
    }
    
    @Override
    public boolean areALlRequirementsForPowerUpDropMet()
    {
        return true;
    }
    
    @Override
    protected void doTypeSpecificDodgeActions(Missile missile)
    {
        this.getSpeedLevel()
            .setLocation(this.getSpeedLevel()
                             .getX(), 9);
        this.dodgeTimer = 16;
    }
    
    @Override
    protected double getKamikazeSpeedUpX()
    {
        return INCREASED_KAMIKAZE_SPEED_UP_X;
    }
    
    @Override
    protected boolean isUncloakingWhenDisabled()
    {
        return false;
    }
}
