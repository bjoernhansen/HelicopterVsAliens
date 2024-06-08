package de.helicopter_vs_aliens.model.enemy.maneuver;

import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ManeuverManger
{
    private final List<Maneuver> maneuvers = new ArrayList<>();
    
    
    public void initializeFor(Enemy enemy)
    {
        maneuvers.clear();
        
        enemy.getType()
             .getManeuverTypes()
             .forEach(maneuverType -> maneuverType.makeInstanceFor(enemy));
    }
}
