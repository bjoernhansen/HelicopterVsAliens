package de.helicopter_vs_aliens.control;

public enum TimeOfDay
{
    NIGHT(0.7f),
    DAY(1.0f);
    
    private final float
        brightnessFactor;
    
    
    TimeOfDay(float brightnessFactor)
    {
        this.brightnessFactor = brightnessFactor;
    }
    
    public float getBrightnessFactor()
    {
        return brightnessFactor;
    }
}
