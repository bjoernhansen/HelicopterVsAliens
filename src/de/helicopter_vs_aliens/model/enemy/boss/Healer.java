package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
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
            if(this.getSpeedLevel().getX() != 0)
            {
                int stop = 0;
                if(this.getX() < Events.boss.getX()
                    + 0.55f * Events.boss.getWidth())
                {
                    this.turnRight();
                }
                else if(this.getX() > Events.boss.getX()
                    + 0.65f * Events.boss.getWidth())
                {
                    this.turnLeft();
                }
                else{stop++;}
                
                if(		this.getY() < Events.boss.getY()
                    + Events.boss.getHeight()
                    - 1.25f * this.getHeight())
                {
                    this.flyDown();
                }
                else if(this.getY() > Events.boss.getY()
                    + Events.boss.getHeight()
                    - 1.05f * this.getHeight())
                {
                    this.flyUp();
                }
                else{stop++;}
                
                if(stop >= 2)
                {
                    this.getSpeedLevel().setLocation(ZERO_SPEED);
                    this.turnLeft();
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
