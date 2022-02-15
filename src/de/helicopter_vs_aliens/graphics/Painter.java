package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.model.Paintable;

import java.awt.*;


abstract class Painter<E extends Paintable>
{
     abstract void paint(Graphics2D g2d, E gameEntity);
}