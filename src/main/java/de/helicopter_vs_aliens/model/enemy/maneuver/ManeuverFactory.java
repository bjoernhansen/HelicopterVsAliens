package de.helicopter_vs_aliens.model.enemy.maneuver;


import de.helicopter_vs_aliens.model.enemy.Enemy;


interface ManeuverFactory
{
    Maneuver makeInstanceFor(Enemy target);
}

