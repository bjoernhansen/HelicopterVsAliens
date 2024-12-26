package de.helicopter_vs_aliens.control.ressource_transfer;

import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.entities.ActivePaintableEntityManager;
import de.helicopter_vs_aliens.control.entities.PaintableEntityFactory;
import de.helicopter_vs_aliens.graphics.GraphicsApiType;
import de.helicopter_vs_aliens.model.PaintableEntity;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import java.util.Queue;


public interface GameRessourceProvider
{
    GameStatisticsCalculator getGameStatisticsCalculator();
    
    Scenery getScenery();
    
    Savegame getSaveGame();
    
    ActivePaintableEntityManager getActivePaintableEntityManager();
    
    <T extends PaintableEntity> T getNewPaintableEntityInstance(PaintableEntityFactory<T> factory);
    
    void storePaintableEntity(PaintableEntity paintableEntity);
    
    <T extends PaintableEntity> void storeAllPaintableEntities(Queue<T> gameEntities);
    
    <T extends PaintableEntity> int numberOfInactivePaintableEntities(Class<T> classOfPaintableEntity);

    boolean isFpsDisplayVisible();

    void switchFpsVisibleState();

    int getGameLoopCount();

    Dimension getDisplayShift();

    void notifyMousePointerLeftWindow();

    void notifyMousePointerEnteredWindow();

    boolean isMouseCursorInWindow();

    GraphicsApiType getGraphicsApiType();

    void resetBackgroundRepaintTimer();

    double getScalingFactor();
    
    Helicopter getHelicopter();
    
    void restoreHelicopter();
    
    void setNewHelicopter(HelicopterType nextHelicopterType);
}
