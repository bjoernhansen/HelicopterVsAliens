package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;

import javax.sound.sampled.Clip;

public class Kaboom extends BasicEnemy
{
    private static final int
        Y_TURN_LINE = GROUND_Y - (int) (EnemyType.KABOOM.getModel().getHeightFactor() * EnemyType.KABOOM.getWidth());
    
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
    protected Clip getCrashToTheGroundSound()
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
    
    @Override
    protected int getBottomTurnLine()
    {
        return Y_TURN_LINE;
    }
}
