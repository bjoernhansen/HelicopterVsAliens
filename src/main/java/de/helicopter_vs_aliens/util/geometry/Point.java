package de.helicopter_vs_aliens.util.geometry;

import java.util.Objects;


public final class Point
{
    private final int
        x;
    
    private final int
        y;
    
    
    public static Point newInstance(int x, int y)
    {
        return new Point(x, y);
    }
    
    private Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Point other)) return false;
        
        return this.x == other.x && this.y == other.y;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString()
    {
        return "Point{ x=" + x + ", y=" + y + '}';
    }
}