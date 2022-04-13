package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.Paintable;


abstract public class Painter<E extends Paintable>
{
     abstract public void paint(GraphicsAdapter graphicsAdapter, E gameEntity);
}