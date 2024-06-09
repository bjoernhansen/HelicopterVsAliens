package de.helicopter_vs_aliens.model.enemy.maneuver;

import de.helicopter_vs_aliens.control.ressource_transfer.AbstractGameRessourceAcceptor;


public abstract class AbstractManeuver extends AbstractGameRessourceAcceptor implements Maneuver
{
    private final Maneuverable target;
    
    AbstractManeuver(Maneuverable maneuverable)
    {
        target = maneuverable;
    }
    
    protected final Maneuverable getTarget()
    {
        return target;
    }
}
