package de.helicopter_vs_aliens.control.factory;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.ressource_transfer.GameProgress;
import de.helicopter_vs_aliens.platform_specific.awt.AwtController;
import de.helicopter_vs_aliens.platform_specific.javafx.JavaFxController;

import static de.helicopter_vs_aliens.graphics.GraphicsApiType.JAVAFX;


public class ControllerFactory
{
    private final GameProgress
        gameProgress;


    public ControllerFactory(GameProgress gameProgress)
    {
        this.gameProgress = gameProgress;
    }

    public Controller makeInstance()
    {
        if(gameProgress.getGraphicsApiType() == JAVAFX)
        {
            return new JavaFxController(gameProgress);
        }
        return new AwtController(gameProgress);
    }
}
