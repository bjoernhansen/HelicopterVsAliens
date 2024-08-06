package de.helicopter_vs_aliens.model.enemy.maneuver;

public interface Maneuver
{
    void perform();
    
    void disable();
    
    void enable();
    
    boolean isEnabled();
    
    void reset();
}
