package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.graphics.GraphicsManager;

import java.awt.*;

abstract public class GameEntity implements Paintable
{
    @Override
    public void paint(Graphics2D g2d)
    {
        GraphicsManager.getInstance().paint(this);
    }
}
