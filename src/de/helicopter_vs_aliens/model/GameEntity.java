package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;

abstract public class GameEntity implements Paintable
{
    @Override
    public void paint(GraphicsAdapter graphicsAdapter)
    {
        GraphicsManager.getInstance().paint(this);
    }
}
