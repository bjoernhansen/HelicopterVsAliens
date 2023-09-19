package de.helicopter_vs_aliens;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.ControllerFx;
import de.helicopter_vs_aliens.control.GraphicsApiType;
import javafx.application.Application;


public class Main
{
	public static final GraphicsApiType
		graphicsApiType = GraphicsApiType.GRAPHICS_2D;


    public static void main(final String[] args)
    {
		if (graphicsApiType == GraphicsApiType.GRAPHICS_2D)
		{
			final Controller controller = Controller.getInstance();
			controller.init();
			controller.start();
		}
		else
		{
			Application.launch(ControllerFx.class);
		}
    }
}
