package de.helicopter_vs_aliens.control.ressource_transfer;

import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.entities.ActiveManageablePaintableController;
import de.helicopter_vs_aliens.control.entities.ManageablePaintable;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableFactory;
import de.helicopter_vs_aliens.graphics.GraphicsApiType;
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
    ActiveManageablePaintableController getActiveManageablePaintableController();
    
    // TODO denkbar wäre eine Methode activatePaintableEntityInstance, die automatisch die Instance auch dem aktiven Entities hinzufügt
    
    <T extends ManageablePaintable> T getNewManageablePaintableInstance(ManageablePaintableFactory<T> factory);
    
    void storeManageablePaintable(ManageablePaintable manageablePaintable);
    
    void storeAllManageablePaintableInstances(Queue<? extends ManageablePaintable> manageablePaintableInstances);
    
    <T extends ManageablePaintable> int numberOfInactiveManageablePaintableInstances(Class<T> classOfManageablePaintable);

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
