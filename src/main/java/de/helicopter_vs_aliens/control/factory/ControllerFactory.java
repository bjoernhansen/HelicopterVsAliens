package de.helicopter_vs_aliens.control.factory;

import de.helicopter_vs_aliens.control.AwtController;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.GameProgress;
import de.helicopter_vs_aliens.control.javafx.JavaFxController;

import static de.helicopter_vs_aliens.control.factory.GraphicsApiType.GRAPHICS_2D;
import static de.helicopter_vs_aliens.control.factory.GraphicsApiType.JAVAFX;


public class ControllerFactory
{
    private static final GraphicsApiType
        GRAPHICS_API_TYPE = GRAPHICS_2D;

    private final GameProgress
        gameProgress;


    public ControllerFactory(GameProgress gameProgress)
    {
        this.gameProgress = gameProgress;
    }

    public Controller makeInstance()
    {
        if(GRAPHICS_API_TYPE == JAVAFX)
        {
            return new JavaFxController(gameProgress);
        }
        return new AwtController(gameProgress);
    }
}
