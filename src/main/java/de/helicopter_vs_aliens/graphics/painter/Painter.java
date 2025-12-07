package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.Paintable;


public abstract class Painter<E extends Paintable>
{
     public abstract void paint(GraphicsAdapter graphicsAdapter, E paintableEntity);
     
     /*
     // TODO Abwägen, ob dies eine sinnvolle Änderung wäre
          private GraphicsAdapter graphicsAdapter;
     
     public void paint(GraphicsAdapter graphicsAdapter, E paintableEntity)
     {
          this.graphicsAdapter = graphicsAdapter;
          paint(paintableEntity);
     }
     
     protected abstract void paint(E paintableEntity);
     
     protected GraphicsAdapter getGraphicsAdapter()
     {
          return graphicsAdapter;
     }
     
      */
}