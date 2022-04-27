package de.helicopter_vs_aliens.graphics;

import java.util.Objects;

public abstract class AbstractGraphicsAdapter<E> implements GraphicsAdapter
{
    protected final E graphics;
    
    protected AbstractGraphicsAdapter(E graphics)
    {
        this.graphics = Objects.requireNonNull(graphics);
    }
}