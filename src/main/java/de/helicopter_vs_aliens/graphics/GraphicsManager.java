package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.model.Paintable;

public final class GraphicsManager
{
    private GraphicsAdapter
        graphicsAdapter;
    
    private static final GraphicsManager
        instance = new GraphicsManager();
    
    private GraphicsManager(){}
    
    public static GraphicsManager getInstance()
    {
        return instance;
    }
    
    // TODO nachvollziehen: an zwei Stellen verwendet, aber dort wird der GraphicsAdapter übergeben. Unnötig?
    public <E extends Paintable> void paint(E paintableEntity)
    {
        Painter<E> painter = PainterProvider.getPainterFor(paintableEntity.getClass()).with(graphicsAdapter);
        painter.paint(paintableEntity);
    }
    
    public void setGraphics(GraphicsAdapter graphicsAdapter)
    {
        this.graphicsAdapter = graphicsAdapter;
    }
}