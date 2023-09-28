package de.helicopter_vs_aliens.control.ressource_transfer;

import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.awt.BackgroundRepaintTimer;
import de.helicopter_vs_aliens.control.entities.ActiveGameEntityManager;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.control.entities.GameEntitySupplier;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.geometry.Dimension;


public interface GameRessourceProvider extends HelicopterAccessor
{
    GameStatisticsCalculator getGameStatisticsCalculator();
    
    Scenery getScenery();
    
    Savegame getSaveGame();
    
    GameEntitySupplier getGameEntitySupplier();
    
    ActiveGameEntityManager getActiveGameEntityManager();

    GuiStateProvider getGuiStateProvider();
    
    <T extends GameEntity> T getNewGameEntityInstance(GameEntityFactory<T> factory);

    boolean isFpsDisplayVisible();

    void switchFpsVisibleState();

    int getGameLoopCount();

    Dimension getDisplayShift();

    void notifyMousePointerLeftWindow();

    void notifyMousePointerEnteredWindow();

    boolean isMouseCursorInWindow();

    void resetBackgroundRepaintTimer();
}
