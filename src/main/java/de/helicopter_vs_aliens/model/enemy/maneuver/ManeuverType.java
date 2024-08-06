package de.helicopter_vs_aliens.model.enemy.maneuver;

import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.util.function.Function;


public enum ManeuverType implements ManeuverFactory
{
    BATCH_WISE_MOVE(IntermittentFlightManeuver::new);
    
    private final Function<Enemy, Maneuver>
        maneuverProvider;
    
    
    ManeuverType(Function<Enemy, Maneuver> maneuverProvider) {
        this.maneuverProvider = maneuverProvider;
    }
    
    @Override
    public Maneuver makeInstanceFor(Enemy target)
    {
        return maneuverProvider.apply(target);
    }
}
