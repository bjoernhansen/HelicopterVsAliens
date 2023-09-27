package de.helicopter_vs_aliens.control.factory;

import de.helicopter_vs_aliens.graphics.GraphicsApiType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;


public final class ParameterProvider
{
    private static final String
        PARAMETER_PROPERTIES_FILE_NAME = "/config.properties";

    private final GraphicsApiType
        graphicsApiType;


    public ParameterProvider()
    {
        Properties parameter = getParameter();
        boolean isJavaFxApplication = Boolean.parseBoolean(parameter.getProperty("isJavaFxApplication", "false"));
        graphicsApiType = isJavaFxApplication ? GraphicsApiType.JAVAFX : GraphicsApiType.GRAPHICS_2D;
    }

    private Properties getParameter()
    {
        Properties parameter = new Properties();
        try(InputStream inputStream = Objects.requireNonNull(getClass().getResource(PARAMETER_PROPERTIES_FILE_NAME))
                                             .openStream())
        {
            parameter.load(inputStream);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return parameter;
    }

    public GraphicsApiType getGraphicsApiType()
    {
        return graphicsApiType;
    }
}
