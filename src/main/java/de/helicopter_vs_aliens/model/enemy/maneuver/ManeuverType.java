package de.helicopter_vs_aliens.model.enemy.maneuver;

import java.util.function.Function;


public enum ManeuverType implements ManeuverFactory
{
    SIMPLE(SimpleManeuver::new);
    
    
    Function<Maneuverable, Maneuver> maneuverProvider;
    
    
    ManeuverType(Function<Maneuverable, Maneuver> maneuverProvider) {
        this.maneuverProvider = maneuverProvider;
    }
    
    @Override
    public Maneuver makeInstanceFor(Maneuverable target)
    {
        return maneuverProvider.apply(target);
    }
}
