package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.missile.Missile;

public class Healer extends FinalBossServant
{
    private static final int
        LEFT_BOUNDARY = 563;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canDodge = true;
    
        super.doTypeSpecificInitialization();
    }

    @Override
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        if(dodgeTimer == READY)
        {
            this.healerAction();
        }
        super.performFlightManeuver(gameRessourceProvider);
    }
    
    private void healerAction()
    {
        if(Events.boss.getHitPoints() < Events.boss.startingHitPoints)
        {
            if(getSpeedLevel().getX() != 0)
            {
                int stop = 0;
                if(getX() < Events.boss.getX()
                    + 0.55f * Events.boss.getWidth())
                {
                    getNavigationDevice().turnRight();
                }
                else if(getX() > Events.boss.getX()
                    + 0.65f * Events.boss.getWidth())
                {
                    getNavigationDevice().turnLeft();
                }
                else{stop++;}
                
                if(		getY() < Events.boss.getY()
                    + Events.boss.getHeight()
                    - 1.25f * getHeight())
                {
                    getNavigationDevice().flyDown();
                }
                else if(getY() > Events.boss.getY()
                    + Events.boss.getHeight()
                    - 1.05f * getHeight())
                {
                    getNavigationDevice().flyUp();
                }
                else{stop++;}
                
                if(stop >= 2)
                {
                    getSpeedLevel().setLocation(ZERO_SPEED);
                    getNavigationDevice().turnLeft();
                    canDodge = true;
                }
            }
            else
            {
                Events.boss.healHitPoints();
            }
        }
        else
        {
            getSpeedLevel().setLocation(targetSpeedLevel);
        }
    }
    
    @Override
    public void dodge(Missile missile)
    {
        super.dodge(missile);
        this.canDodge = false;
    }
    
    @Override
    protected double getLeftBoundary()
    {
        return LEFT_BOUNDARY;
    }
}
