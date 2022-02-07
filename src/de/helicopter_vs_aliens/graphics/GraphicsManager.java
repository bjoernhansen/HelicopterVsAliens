package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.model.Paintable;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.BackgroundObject;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GraphicsManager
{
    private Map<Class<? extends Paintable>, Painter<? extends Paintable>>
        painters = new HashMap<>();
    
    private Graphics2D
        graphics2D;
    
    private static final GraphicsManager
        instance = new GraphicsManager();
    
    private GraphicsManager()
    {
        painters.put(Helicopter.class, new HelicopterPainter());
        painters.put(PowerUp.class, new PowerUpPainter());
        painters.put(Missile.class, new MissilePainter());
        painters.put(EnemyMissile.class, new EnemyMissilePainter());
        painters.put(BackgroundObject.class, new BackgroundObjectPainter());
        painters.put(Scenery.class, new SceneryPainter());
        painters.put(Explosion.class, new ExplosionPainter());
    }
    
    public static GraphicsManager getInstance()
    {
        return instance;
    }
    
    public <E extends Paintable> void paint(E gameEntity)
    {
        Painter<E> painter = getPainter(gameEntity.getClass());
        painter.paint(graphics2D, gameEntity);
    }
    
    public <E extends Painter<? extends Paintable>> E getPainter(Class<? extends Paintable> classOfGameEntity)
    {
        return (E) painters.get(classOfGameEntity);
    }

    public void setGraphics2D(Graphics2D graphics2D)
    {
        this.graphics2D = graphics2D;
    }
}