package de.helicopter_vs_aliens.model.enemy.maneuver;

import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;


public class ManeuverManger
{
    private final List<Maneuver> maneuvers = new ArrayList<>();
    
    public void initializeManeuversFor(Enemy enemy)
    {
        // TODO vielleicht könnte auch mit einem Reset gearbeitet werden, wenn die Manöver schon da sind
        maneuvers.clear();
        enemy.getType()
             .getManeuverTypes()
             .stream()
             .map(maneuverType -> maneuverType.makeInstanceFor(enemy))
             .forEach(maneuvers::add);
    }
    
    public void performAll()
    {
        maneuvers.forEach(Maneuver::perform);
    }
}
