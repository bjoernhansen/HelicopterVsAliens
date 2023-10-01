package de.helicopter_vs_aliens.graphics;

import java.util.Objects;

public abstract class AbstractGraphicsAdapter<E> implements GraphicsAdapter
{
    protected final E graphics;
    
    protected AbstractGraphicsAdapter(E graphics)
    {
        this.graphics = Objects.requireNonNull(graphics);
    }

    public abstract void drawImage(java.awt.Image image, int x, int y, int scaleWidth, int scaleHeight);
}