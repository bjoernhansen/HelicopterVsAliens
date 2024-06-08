package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.Paintable;


public abstract class Painter<E extends Paintable>
{
     public abstract void paint(GraphicsAdapter graphicsAdapter, E paintableEntity);
}