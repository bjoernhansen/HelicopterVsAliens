package de.helicopter_vs_aliens.model.enemy.maneuver;

public abstract class AbstractManeuver<T> implements Maneuver
{
    private final T target;
    
    AbstractManeuver(T target)
    {
        this.target = target;
    }
    
    protected final T getTarget()
    {
        return target;
    }
}
