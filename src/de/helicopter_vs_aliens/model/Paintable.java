package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;

import java.awt.*;

public interface Paintable
{
    void paint(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter);
}