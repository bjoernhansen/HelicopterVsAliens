package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.model.Paintable;

import java.awt.*;


abstract public class Painter<E extends Paintable>
{
     abstract public void paint(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter, E gameEntity);
}