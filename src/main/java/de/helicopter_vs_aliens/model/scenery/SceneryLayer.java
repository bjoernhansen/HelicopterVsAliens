package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.util.Calculations;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum SceneryLayer
{
    BACKGROUND(-1),
    FOREGROUND_INNER(0),
    FOREGROUND_MIDDLE(1),
    FOREGROUND_OUTER(2);
    
    private static final List<SceneryLayer>
        VALUES = List.of(values());
    
    private static final Set<SceneryLayer>
        FOREGROUND_LAYERS = Collections.unmodifiableSet(EnumSet.range(FOREGROUND_INNER, FOREGROUND_OUTER));
    
    private final int LayerPosition;
    
    
    SceneryLayer(int layerPosition)
    {
        LayerPosition = layerPosition;
    }
    
    public static List<SceneryLayer> getValues()
    {
        return VALUES;
    }
    
    public static Set<SceneryLayer> getForegroundLayers()
    {
        return FOREGROUND_LAYERS;
    }
    
    public static SceneryLayer getRandomLayer()
    {
        return Calculations.tossUp() ? BACKGROUND : FOREGROUND_MIDDLE;
    }
    
    public int getLayerPosition()
    {
        return LayerPosition;
    }
    
    public boolean isBackgroundLayer()
    {
        return this == BACKGROUND;
    }
}