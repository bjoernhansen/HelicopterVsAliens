package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

public class Healer extends FinalBossServant
{
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.canDodge = true;
    
        super.doTypeSpecificInitialization();
    }

    @Override
    protected void performFlightManeuver(Controller controller, Helicopter helicopter)
    {
        if(dodgeTimer == READY)
        {
            this.healerAction();
        }
        super.performFlightManeuver(controller, helicopter);
    }
    
    private void healerAction()
    {
        if(Events.boss.getHitPoints() < Events.boss.startingHitPoints)
        {
            if(this.getSpeedLevel().getX() != 0)
            {
                int stop = 0;
                if(this.getX() < Events.boss.getX()
                    + 0.55f * Events.boss.getWidth())
                {
                    this.direction.x = 1;
                }
                else if(this.getX() > Events.boss.getX()
                    + 0.65f * Events.boss.getWidth())
                {
                    this.direction.x = -1;
                }
                else{stop++;}
                
                if(		this.getY() < Events.boss.getY()
                    + Events.boss.getHeight()
                    - 1.25f * this.getHeight())
                {
                    this.direction.y = 1;
                }
                else if(this.getY() > Events.boss.getY()
                    + Events.boss.getHeight()
                    - 1.05f * this.getHeight())
                {
                    this.direction.y = -1;
                }
                else{stop++;}
                
                if(stop >= 2)
                {
                    this.getSpeedLevel().setLocation(ZERO_SPEED);
                    this.direction.x = -1;
                    this.canDodge = true;
                }
            }
            else
            {
                Events.boss.healHitPoints();
            }
        }
        else
        {
            this.getSpeedLevel().setLocation(this.targetSpeedLevel);
        }
    }
}
