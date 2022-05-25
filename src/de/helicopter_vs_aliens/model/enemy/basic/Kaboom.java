package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;

import java.applet.AudioClip;

public class Kaboom extends BasicEnemy
{
    @Override
    protected boolean canBecomeMiniBoss()
    {
        return false;
    }
    
    @Override
    protected boolean isMeetingRequirementsForGlowingEyes()
    {
        return true;
    }
    
    @Override
    protected double calculateInitialY()
    {
        return getOnTheGroundY() - getHeight();
    }
    
    @Override
    protected int hitPointVariance()
    {
        return 0;
    }
    
    @Override
    public boolean countsForTotalAmountOfEnemiesSeen()
    {
        return false;
    }
    
    @Override
    protected AudioClip getCrashToTheGroundSound()
    {
        return Audio.explosion4;
    }
    
    @Override
    protected ExplosionType getExplosionType()
    {
        return ExplosionType.JUMBO;
    }
    
    @Override
    protected boolean isDetonatingExtraStrong()
    {
        return true;
    }
}
