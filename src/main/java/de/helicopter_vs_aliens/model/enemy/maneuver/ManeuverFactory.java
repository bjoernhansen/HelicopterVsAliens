package de.helicopter_vs_aliens.model.enemy.maneuver;


public interface ManeuverFactory
{
    Maneuver makeInstanceFor(MovingObject target);
}

