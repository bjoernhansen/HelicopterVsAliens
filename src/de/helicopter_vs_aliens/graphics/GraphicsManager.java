package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.gui.menu.GameMenu;
import de.helicopter_vs_aliens.gui.menu.Menu;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.gui.menu.RepairShopMenu;
import de.helicopter_vs_aliens.gui.menu.ScoreScreenMenu;
import de.helicopter_vs_aliens.gui.menu.StartScreenMenu;
import de.helicopter_vs_aliens.gui.menu.StartScreenSubMenu;
import de.helicopter_vs_aliens.model.Paintable;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.helicopter.*;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.BackgroundObject;
import de.helicopter_vs_aliens.model.scenery.Scenery;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

public class GraphicsManager
{
    private final Map<Class<? extends Paintable>, Painter<? extends Paintable>>
        painters = new HashMap<>();
    
    private Graphics2D
        graphics2D;
    
    private static final GraphicsManager
        instance = new GraphicsManager();
    
    private GraphicsManager()
    {
        // HelicopterPainter
        painters.put(Phoenix.class, new PhoenixPainter());
        painters.put(Roch.class, new RochPainter());
        painters.put(Orochi.class, new OrochiPainter());
        painters.put(Kamaitachi.class, new KamaitachiPainter());
        painters.put(Pegasus.class, new PegasusPainter());
        painters.put(Helios.class, new HeliosPainter());
        
        //GUI Painter
        painters.put(Button.class, new ButtonPainter());
        MenuPainter menuPainter = new MenuPainter();
    
        painters.put(StartScreenMenu.class, menuPainter);
        painters.put(StartScreenSubMenu.class, menuPainter);
        painters.put(GameMenu.class, menuPainter);
        painters.put(RepairShopMenu.class, menuPainter);
        painters.put(ScoreScreenMenu.class, menuPainter);
        painters.put(Menu.class, menuPainter);
        
        // sonstige Painter
        painters.put(PowerUp.class, new PowerUpPainter());
        painters.put(Missile.class, new MissilePainter());
        painters.put(EnemyMissile.class, new EnemyMissilePainter());
        painters.put(BackgroundObject.class, new BackgroundObjectPainter());
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