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
    
    // TODO eigentlich sollte hier mit dem Interface ActivePaintableEntityProvider
    ActivePaintableEntityManager getActivePaintableEntityManager();
    
    // TODO denkbar wäre eine Methode activatePaintableEntityInstance, die automatisch die Instance auch dem aktiven Entitites hinzufügt
    
    // TODO an all diesen Stellen sollte eigentlich das Interface Paintable und nicht PaintableEntity verwendet werden
    <T extends PaintableEntity> T getNewPaintableEntityInstance(PaintableEntityFactory<T> factory);
    
    void storePaintableEntity(PaintableEntity paintableEntity);
    
    void storeAllPaintableEntities(Queue<? extends PaintableEntity> gameEntities);
    
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
