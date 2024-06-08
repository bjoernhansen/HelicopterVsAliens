package de.helicopter_vs_aliens.model.enemy.maneuver;

import de.helicopter_vs_aliens.control.ressource_transfer.AbstractGameRessourceAcceptor;


public abstract class AbstractManeuver extends AbstractGameRessourceAcceptor implements Maneuver
{
    private final MovingObject target;
    
    AbstractManeuver(MovingObject movingObject)
    {
        target = movingObject;
    }
    
    protected final MovingObject getTarget()
    {
        return target;
    }
}
