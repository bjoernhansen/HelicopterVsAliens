package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.graphics.painter.ButtonPainter;
import de.helicopter_vs_aliens.graphics.painter.EnemyMissilePainter;
import de.helicopter_vs_aliens.graphics.painter.EnemyPainter;
import de.helicopter_vs_aliens.graphics.painter.ExplosionPainter;
import de.helicopter_vs_aliens.graphics.painter.MissilePainter;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.PowerUpPainter;
import de.helicopter_vs_aliens.graphics.painter.SceneryObjectPainter;
import de.helicopter_vs_aliens.graphics.painter.SceneryPainter;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.model.Paintable;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;

import java.util.HashMap;
import java.util.Map;

public class GraphicsManager
{
    private final Map<Class<? extends Paintable>, Painter<? extends Paintable>>
        painters = new HashMap<>();
        
    private GraphicsAdapter
        graphicsAdapter;
    
    private static final GraphicsManager
        instance = new GraphicsManager();
    
    private GraphicsManager()
    {
        // HelicopterPainter
        HelicopterType.getValues().forEach(helicopterType ->
            painters.put(helicopterType.getHelicopterClass(), helicopterType.makePainterInstance()));
        
        //GUI Painter
        painters.put(Button.class, new ButtonPainter());
    
        WindowType.getValues().forEach(windowType ->
            painters.put(windowType.getMenuClass(), windowType.makePainterInstance()));
        
        // sonstige Painter
        painters.put(PowerUp.class, new PowerUpPainter());
        painters.put(Missile.class, new MissilePainter());
        painters.put(EnemyMissile.class, new EnemyMissilePainter());
        painters.put(SceneryObject.class, new SceneryObjectPainter());
        painters.put(Scenery.class, new SceneryPainter());
        painters.put(Explosion.class, new ExplosionPainter());
        painters.put(Enemy.class, new EnemyPainter());
    }
    
    public static GraphicsManager getInstance()
    {
        return instance;
    }
    
    public <E extends Paintable> void paint(E gameEntity)
    {
        Painter<E> painter = getPainter(gameEntity.getClass());
        painter.paint(graphicsAdapter, gameEntity);
    }
    
    public <E extends Painter<? extends Paintable>> E getPainter(Class<? extends Paintable> classOfGameEntity)
    {
        return (E) painters.get(classOfGameEntity);
    }

    public void setGraphics(GraphicsAdapter graphicsAdapter)
    {
        this.graphicsAdapter = graphicsAdapter;
    }
}