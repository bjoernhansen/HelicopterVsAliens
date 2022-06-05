package de.helicopter_vs_aliens.model.enemy.devices;

import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;

public class NavigationDevice
{
    private final Point
        direction = new Point();		// Flugrichtung
    
    public final boolean isFlyingLeft()
    {
        return getDirectionX() == -1;
    }
    
    public final void turnLeft()
    {
        direction.x = -1;
    }
    
    public final boolean isFlyingRight()
    {
        return getDirectionX() == 1;
    }
    
    public final void turnRight()
    {
        direction.x = 1;
    }
    
    public int getDirectionX()
    {
        return direction.x;
    }
    
    public final void turnAround()
    {
        direction.x = -direction.x;
    }
    
    public final void setRandomDirectionX()
    {
        direction.x = Calculations.randomDirection();
    }
    
    public final boolean isFlyingDown()
    {
        return direction.y == 1;
    }
    
    public final void flyDown()
    {
        direction.y = 1;
    }
    
    public final boolean isFlyingUp()
    {
        return getDirectionY() == -1;
    }
    
    public final void flyUp()
    {
        direction.y = -1;
    }
    
    public final int getDirectionY()
    {
        return direction.y;
    }
    
    public final void switchDirectionY()
    {
        direction.y = -getDirectionY();
    }
    
    public final void setRandomDirectionY()
    {
        direction.y = Calculations.randomDirection();
    }
}
