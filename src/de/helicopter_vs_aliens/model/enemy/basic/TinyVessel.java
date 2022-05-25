package de.helicopter_vs_aliens.model.enemy.basic;

public class TinyVessel extends BasicEnemy
{
    public static final float
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 1.2f;
        
    @Override
    protected boolean canBecomeMiniBoss()
    {
        return false;
    }
    
    @Override
    protected float getSecondaryColorBrightnessFactor()
    {
        return SECONDARY_COLOR_BRIGHTNESS_FACTOR;
    }
}
