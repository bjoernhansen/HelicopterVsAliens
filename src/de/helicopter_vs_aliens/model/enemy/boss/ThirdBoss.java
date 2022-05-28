package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.missile.Missile;

public class ThirdBoss extends BossEnemy
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canMoveChaotic = true;
        this.canKamikaze = true;
        this.cloakingTimer = READY;
        this.canDodge = true;
        this.shootTimer = 0;
        this.shootingRate = 10;
        this.shotSpeed = 10;
        this.canInstantTurn = true;
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
}
