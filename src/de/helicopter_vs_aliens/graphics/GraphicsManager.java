package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.model.Paintable;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GraphicsManager
{
    private Map<Class, Painter>
        graphicalRepresentations = new HashMap<>();
    
    private Graphics2D
        graphics2D;
    
    private static final GraphicsManager
        instance = new GraphicsManager();
        
    
    private GraphicsManager()
    {
        graphicalRepresentations.put(Helicopter.class, new HelicopterPainter());
        graphicalRepresentations.put(PowerUp.class, new PowerUpPainter());
    }
    
    public static GraphicsManager getInstance()
    {
        return instance;
    }
    
    public <E extends Paintable> void paint(E gameEntity)
    {
        Painter graphicalRepresentation = graphicalRepresentations.get(gameEntity.getClass());
        graphicalRepresentation.paint(graphics2D, gameEntity);
    }
    
    public <E> E getGraphicalRepresentation(Class classOfGameEntity)
    {
        return (E)graphicalRepresentations.get(classOfGameEntity);
    }
    
    public void setGraphics2D(Graphics2D graphics2D)
    {
        this.graphics2D = graphics2D;
    }
}
