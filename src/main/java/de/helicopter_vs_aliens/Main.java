package de.helicopter_vs_aliens;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.ControllerFx;
import de.helicopter_vs_aliens.control.GraphicsApiType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import javafx.application.Application;

import java.awt.*;


public class Main
{
	public static final GraphicsApiType
		graphicsApiType = GraphicsApiType.GRAPHICS_2D;
    public static final Dimension
        VIRTUAL_DIMENSION = new Dimension(1024, 461);


    public static void main(final String[] args)
    {
		if (graphicsApiType == GraphicsApiType.GRAPHICS_2D)
		{
			Controller controller = new Controller();
			GameResources.setGameResources(controller);
			controller.init();
			controller.start();
		}
		else
		{
			Application.launch(ControllerFx.class);
		}
    }
}
