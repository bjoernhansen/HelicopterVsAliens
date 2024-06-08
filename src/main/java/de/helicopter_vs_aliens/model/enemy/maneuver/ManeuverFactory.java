package de.helicopter_vs_aliens.model.enemy.maneuver;

import de.helicopter_vs_aliens.model.enemy.MovingObject;


public interface ManeuverFactory
{
    Maneuver makeInstanceFor(MovingObject target);
}

