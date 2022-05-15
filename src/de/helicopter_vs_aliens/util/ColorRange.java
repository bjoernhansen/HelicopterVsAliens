package de.helicopter_vs_aliens.util;

import java.awt.Color;
import java.util.Objects;

public final class ColorRange
{
    private static final Color
        DEFAULT = new Color(0,0,0,0);
    
    private static final int
        NO_SCATTERING = 1;
    
    private final Color
        baseColor;
    
    private final boolean
        isScattering;
    
    private final int
        varianceRed,
        varianceGreen,
        varianceBlue;
    
    
    public static ColorRange of(Color baseColor, int varianceRed, int varianceGreen, int varianceBlue)
    {
        return new ColorRange(baseColor, varianceRed, varianceGreen, varianceBlue);
    }
    
    public static ColorRange withoutScatteringOf(Color baseColor)
    {
        return new ColorRange(baseColor, NO_SCATTERING, NO_SCATTERING, NO_SCATTERING);
    }
    
    public static ColorRange getDefault()
    {
        return ColorRange.withoutScatteringOf(DEFAULT);
    }
    
    private ColorRange(Color baseColor, int varianceRed, int varianceGreen, int varianceBlue)
    {
        this.baseColor = Objects.requireNonNull(baseColor);
        this.varianceRed = adjust(varianceRed);
        this.varianceGreen = adjust(varianceGreen);
        this.varianceBlue = adjust(varianceBlue);
        this.isScattering = isScattering();
    }
    
    private int adjust(int varianceValue)
    {
        if(varianceValue < 1){return 1;}
        return Math.min(varianceValue, 256 - baseColor.getRed());
    }
    
    private boolean isScattering()
    {
        return     varianceRed   != NO_SCATTERING
                || varianceGreen != NO_SCATTERING
                || varianceBlue  != NO_SCATTERING;
    }
    
    public Color selectColor()
    {
        if(isScattering)
        {
            int red   = baseColor.getRed()   + Calculations.random(varianceRed);
            int green = baseColor.getGreen() + Calculations.random(varianceGreen);
            int blue  = baseColor.getBlue()  + Calculations.random(varianceBlue);
            return new Color(red, green, blue);
        }
        return baseColor;
    }
}
