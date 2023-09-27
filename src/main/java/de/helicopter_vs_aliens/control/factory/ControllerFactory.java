package de.helicopter_vs_aliens.control.factory;

import de.helicopter_vs_aliens.control.awt.AwtController;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.GameProgress;
import de.helicopter_vs_aliens.control.javafx.JavaFxController;
import de.helicopter_vs_aliens.graphics.GraphicsApiType;

import static de.helicopter_vs_aliens.graphics.GraphicsApiType.GRAPHICS_2D;
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
