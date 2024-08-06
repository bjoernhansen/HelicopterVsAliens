package de.helicopter_vs_aliens.model.enemy.maneuver;

public abstract class AbstractManeuver<T> implements Maneuver
{
    private final T
        target;
    
    private boolean
        isEnabled = true;
    
    
    AbstractManeuver(T target)
    {
        this.target = target;
    }
    
    protected final T getTarget()
    {
        return target;
    }
    
    
    @Override
    public void disable()
    {
        isEnabled = false;
    }
    
    @Override
    public void enable()
    {
        isEnabled = true;
    }
    
    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }
    
    @Override
    public void reset()
    {
        isEnabled = true;
    }
}
