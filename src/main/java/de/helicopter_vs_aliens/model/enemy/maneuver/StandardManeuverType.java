package de.helicopter_vs_aliens.model.enemy.maneuver;

import java.util.function.Function;


public enum StandardManeuverType implements ManeuverFactory
{
    BATCH_WISE_MOVE(BatchWiseMoveManeuver::new);
    
    private final Function<Maneuverable, Maneuver>
        maneuverProvider;
    
    
    StandardManeuverType(Function<Maneuverable, Maneuver> maneuverProvider) {
        this.maneuverProvider = maneuverProvider;
    }
    
    @Override
    public Maneuver makeInstanceFor(Maneuverable target)
    {
        return maneuverProvider.apply(target);
    }
}
