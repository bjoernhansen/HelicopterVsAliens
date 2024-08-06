package de.helicopter_vs_aliens.model.enemy.maneuver;

import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;


public class ManeuverManger
{
    private final Map<ManeuverType, Maneuver> maneuvers = new EnumMap<>(ManeuverType.class);
    
    public void initializeManeuversFor(Enemy enemy)
    {
        // TODO vielleicht könnte auch mit einem Reset gearbeitet werden, wenn die Manöver schon da sind
        maneuvers.clear();
        enemy.getType()
             .getManeuverTypes()
             .forEach(maneuverType -> maneuvers.put(maneuverType, maneuverType.makeInstanceFor(enemy)));
    }
    
    public void performAll()
    {
        maneuvers.values()
                 .stream()
                 .filter(Maneuver::isEnabled)
                 .forEach(Maneuver::perform);
    }
    
    public void putManeuver(ManeuverType maneuverType, Enemy enemy)
    {
        maneuvers.put(maneuverType, maneuverType.makeInstanceFor(enemy));
    }
    
    public void enableManeuver(ManeuverType maneuverType)
    {
        Optional.ofNullable(maneuvers.get(maneuverType))
                .ifPresent(Maneuver::enable);
    }
    
    public void disableManeuver(ManeuverType maneuverType)
    {
        Optional.ofNullable(maneuvers.get(maneuverType))
                .ifPresent(Maneuver::disable);
    }
    
    public void resetManeuvers()
    {
        maneuvers.values()
                 .forEach(Maneuver::reset);
    }
}
