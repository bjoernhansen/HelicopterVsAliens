package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.graphics.painter.ButtonPainter;
import de.helicopter_vs_aliens.graphics.painter.EnemyMissilePainter;
import de.helicopter_vs_aliens.graphics.painter.ExplosionPainter;
import de.helicopter_vs_aliens.graphics.painter.MissilePainter;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.PowerUpPainter;
import de.helicopter_vs_aliens.graphics.painter.SceneryObjectPainter;
import de.helicopter_vs_aliens.graphics.painter.SceneryPainter;
import de.helicopter_vs_aliens.graphics.painter.enemy.BurrowingBarrierPainter;
import de.helicopter_vs_aliens.graphics.painter.enemy.FinalBossPainter;
import de.helicopter_vs_aliens.graphics.painter.enemy.HealerPainter;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.model.Paintable;
import de.helicopter_vs_aliens.model.enemy.EnemyModelType;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.barrier.BurrowingBarrier;
import de.helicopter_vs_aliens.model.enemy.boss.FinalBoss;
import de.helicopter_vs_aliens.model.enemy.boss.Healer;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.graphics.painter.enemy.CapturingEnemyPainter;
import de.helicopter_vs_aliens.model.enemy.basic.CapturingEnemy;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.powerup.PowerUp;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class GraphicsManager
{
    private final Map<Class<? extends Paintable>, Painter<? extends Paintable>>
        PAINTERS;
        
    private GraphicsAdapter
        graphicsAdapter;
    
    private static final GraphicsManager
        instance = new GraphicsManager();
    
    private GraphicsManager()
    {
        PAINTERS = getPainterMap();
    }
    
    private Map<Class<? extends Paintable>, Painter<? extends Paintable>> getPainterMap()
    {
        Map<Class<? extends Paintable>, Painter<? extends Paintable>>
            painterMap = new HashMap<>();
        
        // HelicopterPainter
        HelicopterType.getValues().forEach(helicopterType ->
            painterMap.put(helicopterType.getCorrespondingClass(), helicopterType.makePainterInstance()));
    
        //GUI Painter
        painterMap.put(Button.class, new ButtonPainter());
    
        WindowType.getValues().forEach(windowType ->
            painterMap.put(windowType.getMenuClass(), windowType.makePainterInstance()));
    
        // Painter für Gegner
        // TODO Verbessern: im Enum direkt die Painter angeben, aber nicht jeden einzeln erstellen!
        final Map<EnemyModelType, Painter<?>> enemyPainter = getEnemyPainterMap();
        EnemyType.getValues()
                 .forEach(enemyType -> painterMap.put(enemyType.getCorrespondingClass(), enemyPainter.get(enemyType.getModel())));
        painterMap.put(Healer.class, new HealerPainter());
        painterMap.put(BurrowingBarrier.class, new BurrowingBarrierPainter());
        painterMap.put(FinalBoss.class, new FinalBossPainter());
        painterMap.put(CapturingEnemy.class, new CapturingEnemyPainter());
    
        // sonstige Painter
        painterMap.put(PowerUp.class, new PowerUpPainter());
        painterMap.put(Missile.class, new MissilePainter());
        painterMap.put(EnemyMissile.class, new EnemyMissilePainter());
        painterMap.put(SceneryObject.class, new SceneryObjectPainter());
        painterMap.put(Scenery.class, new SceneryPainter());
        painterMap.put(Explosion.class, new ExplosionPainter());
        
        return Collections.unmodifiableMap(painterMap);
    }
    
    private Map<EnemyModelType, Painter<?>> getEnemyPainterMap()
    {
        BinaryOperator<Painter<?>> lastWriteWinsStrategy = (v1, v2) -> v2;
    
        return EnemyModelType.getValues()
                             .stream()
                             .collect(toMap(
                                 identity(),
                                 EnemyModelType::makePainterInstance,
                                 lastWriteWinsStrategy,
                                 () -> new EnumMap<>(EnemyModelType.class)));
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
    
    // TODO eingeschränkter Wildcard-Typ als Rückgabewert sollte immer vermieden werden (siehe Effective Java)
    // TODO unchecked Cast beseitigen
    public <E extends Painter<? extends Paintable>> E getPainter(Class<? extends Paintable> classOfGameEntity)
    {
        return (E) PAINTERS.get(classOfGameEntity);
    }

    public void setGraphics(GraphicsAdapter graphicsAdapter)
    {
        this.graphicsAdapter = graphicsAdapter;
    }
}