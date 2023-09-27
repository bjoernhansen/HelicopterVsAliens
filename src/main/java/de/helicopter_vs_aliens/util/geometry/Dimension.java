package de.helicopter_vs_aliens.util.geometry;

import java.util.Objects;


public class Dimension
{
    private final int
        width;

    private final int
        height;


    public static Dimension newInstance(int x, int y)
    {
        return new Dimension(x, y);
    }

    public static Dimension of(java.awt.Dimension dimension)
    {
        return new Dimension(dimension.width, dimension.height);
    }

    private Dimension(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public java.awt.Dimension asAwtDimension()
    {
        return new java.awt.Dimension(width, height);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Dimension other)) return false;

        return this.width == other.width && this.height == other.height;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(width, height);
    }

    @Override
    public String toString()
    {
        return "Dimension{ width=" + width + ", height=" + height + '}';
    }
}
