package de.helicopter_vs_aliens.model;

import java.awt.*;

public abstract class GraphicalRepresentation <E extends Paintable>
{
    protected abstract void paint(Graphics2D g2d, E gameEntity);
}