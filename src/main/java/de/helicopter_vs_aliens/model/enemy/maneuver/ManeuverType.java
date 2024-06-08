package de.helicopter_vs_aliens.model.enemy.maneuver;

import de.helicopter_vs_aliens.model.enemy.MovingObject;

import java.util.function.Function;


public enum ManeuverType implements ManeuverFactory
{
    SIMPLE(SimpleManeuver::new);
    
    
    Function<MovingObject, Maneuver> maneuverProvider;
    
    
    ManeuverType(Function<MovingObject, Maneuver> maneuverProvider) {
        this.maneuverProvider = maneuverProvider;
    }
    
    @Override
    public Maneuver makeInstanceFor(MovingObject target)
    {
        return maneuverProvider.apply(target);
    }
}
