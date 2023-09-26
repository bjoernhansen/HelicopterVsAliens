package de.helicopter_vs_aliens;

import de.helicopter_vs_aliens.control.GameProgress;
import de.helicopter_vs_aliens.control.factory.ControllerFactory;

import java.awt.*;


public class Main
{
    public static final Dimension
        VIRTUAL_DIMENSION = new Dimension(1024, 461);


    public static void main(final String[] args)
    {
        var gameProgress = new GameProgress();
        var controllerFactory = new ControllerFactory(gameProgress);

        controllerFactory.makeInstance()
                         .start();
    }
}
