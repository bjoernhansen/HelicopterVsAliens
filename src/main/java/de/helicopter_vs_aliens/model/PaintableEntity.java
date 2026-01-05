package de.helicopter_vs_aliens.model;

import de.helicopter_vs_aliens.control.ressource_transfer.AbstractGameRessourceAcceptor;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.PainterProvider;
import de.helicopter_vs_aliens.graphics.painter.Painter;


public abstract class PaintableEntity extends AbstractGameRessourceAcceptor implements Paintable
{
    @Override
    public void paint(GraphicsAdapter graphicsAdapter)
    {
        paint(graphicsAdapter, this);
    }
    
    private <E extends Paintable> void paint(GraphicsAdapter graphicsAdapter, E paintableEntity)
    {
        Painter<E> painter = PainterProvider.getPainterFor(paintableEntity.getClass()).with(graphicsAdapter);
        painter.paint(paintableEntity);
    }
}
