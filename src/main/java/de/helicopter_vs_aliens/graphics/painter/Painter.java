package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.model.Paintable;


public abstract class Painter<E extends Paintable>
{
    public abstract void paint(GraphicsAdapter graphicsAdapter, E paintableEntity);
    
    private GraphicsAdapter graphicsAdapter;
    
    
    public void setGraphicsAdapter(GraphicsAdapter graphicsAdapter)
    {
        this.graphicsAdapter = graphicsAdapter;
    }
    
    protected GraphicsAdapter getGraphicsAdapter()
    {
        // TODO durch die Verwendung dieser Methode sollten viele der Methodenaufrufe mit Übergabe des GraphicsAdapters unnötig werden.
        // TODO eventuell könnte auch im Konstruktor einmalig ein Feld belegt werden. Dann müsste nicht jedes mal wieder statisch auf die Instanz zugegriffen werden.
        return graphicsAdapter;
    }
}
