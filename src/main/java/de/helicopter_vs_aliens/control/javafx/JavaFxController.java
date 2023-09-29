package de.helicopter_vs_aliens.control.javafx;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.ressource_transfer.GameProgress;
import javafx.application.Application;


public class JavaFxController implements Controller
{
    static GameProgress
        gameProgress;


    public JavaFxController(GameProgress gameProgress)
    {
        JavaFxController.gameProgress = gameProgress;
    }

    @Override
    public void start()
    {
        Application.launch(GameApplication.class);
    }
}
