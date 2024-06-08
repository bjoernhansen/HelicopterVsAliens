package de.helicopter_vs_aliens.model.enemy.maneuver;


public class SimpleManeuver extends AbstractManeuver
{
    SimpleManeuver(MovingObject movingObject)
    {
        super(movingObject);
    }
    
    @Override
    public void perform()
    {
        getTarget().doSomeMove();
    }
}
