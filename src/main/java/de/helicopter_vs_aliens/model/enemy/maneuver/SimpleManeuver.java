package de.helicopter_vs_aliens.model.enemy.maneuver;


public class SimpleManeuver extends AbstractManeuver
{
    SimpleManeuver(Maneuverable maneuverable)
    {
        super(maneuverable);
    }
    
    @Override
    public void perform()
    {
        getTarget().doSomeMove();
    }
}
