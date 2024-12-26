package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.control.ressource_transfer.AbstractGameRessourceAcceptor;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;


public abstract class PaintableEntity extends AbstractGameRessourceAcceptor implements Paintable
{
    @Override
    public void paint(GraphicsAdapter graphicsAdapter)
    {
        GraphicsManager.getInstance().paint(this);
    }
}
