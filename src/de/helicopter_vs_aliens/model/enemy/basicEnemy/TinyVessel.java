package de.helicopter_vs_aliens.model.enemy.basicEnemy;

public class TinyVessel extends BasicEnemy
{
    public static final float
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 1.2f;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.targetSpeedLevel.setLocation(0.5 + Math.random(),
                                          0.5 * Math.random());
        this.canExplode = true;
    
        super.doTypeSpecificInitialization();
    }
    
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
