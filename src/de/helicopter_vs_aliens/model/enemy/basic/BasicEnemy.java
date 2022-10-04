package de.helicopter_vs_aliens.model.enemy.basic;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Color;

public abstract class BasicEnemy extends StandardEnemy
{
    private static final int
        MIN_MINI_BOSS_LEVEL = 5,
        MINI_BOSS_REWARD_FACTOR = 4,
        MINI_BOSS_CALL_BACK_MINIMUM_FOR_TURN_AT_BARRIER = 2;
    
    private static final float
        STANDARD_MINI_BOSS_PROB = 0.05f,
        CHEAT_MINI_BOSS_PROB = 1.0f;
    
    private static final double
        MINI_BOSS_SIZE_FACTOR = 1.44;
    
    private static float
        miniBossProb = STANDARD_MINI_BOSS_PROB; // bestimmt die Häufigkeit, mit der Mini-Bosse erscheinen
    
    
    private boolean
        isMiniBoss;					// = true: Gegner ist ein Mini-Boss
    
    
    public static void changeMiniBossProb()
    {
        BasicEnemy.miniBossProb = BasicEnemy.miniBossProb == STANDARD_MINI_BOSS_PROB ? CHEAT_MINI_BOSS_PROB: STANDARD_MINI_BOSS_PROB;
    }
    
    @Override
    protected void finalizeInitialization()
    {
        if(canBecomeMiniBoss())
        {
            turnIntoMiniBoss();
        }
        super.finalizeInitialization();
    }
    
    @Override
    public void reset()
    {
        super.reset();
        isMiniBoss = false;
    }
    
    protected boolean canBecomeMiniBoss()
    {
        return 	EnemyController.currentMiniBoss == null
            && Events.level >= MIN_MINI_BOSS_LEVEL
            && Calculations.tossUp(miniBossProb);
    }
    
    private void turnIntoMiniBoss()
    {
        getGameStatisticsCalculator().incrementNumberOfMiniBossSeen();
        EnemyController.currentMiniBoss = this;
        resizeToMiniBossDimension();
        isMiniBoss = true;
        callBack += 2;
        canTurn = true;
        if(isToBecomeCloakableMiniBoss())
        {
            setCloakingDeviceReadyForUse();
        }
    }
    
    private GameStatisticsCalculator getGameStatisticsCalculator()
    {
        return getGameRessourceProvider().getGameStatisticsCalculator();
    }
    
    private boolean isToBecomeCloakableMiniBoss()
    {
        return hasCanonReadyToFire() || (type.isCloakableAsMiniBoss() && Calculations.tossUp(0.2f));
    }
    
    @Override
    protected void writeDestructionStatistics(GameStatisticsCalculator gameStatisticsCalculator)
    {
        super.writeDestructionStatistics(gameStatisticsCalculator);
        if(isMiniBoss){gameStatisticsCalculator.incrementNumberOfMiniBossKilled();}
    }
    
    private void resizeToMiniBossDimension()
    {
        setBounds( getX(),
                   getY(),
                   MINI_BOSS_SIZE_FACTOR * getWidth(),
                   MINI_BOSS_SIZE_FACTOR * getHeight());
    }
    
    @Override
    protected int hitPointVariance()
    {
        return Calculations.random(type.getHitPoints()/2);
    }
    
    @Override
    protected int getRewardModifier()
    {
        return isMiniBoss ? 0 : super.getRewardModifier();
    }
    
    @Override
    protected int calculateHitPoints()
    {
        int standardHitPoints = super.calculateHitPoints();
        if(isMiniBoss)
        {
            return 1 + 5 * standardHitPoints;
        }
        return standardHitPoints;
    }
    
    @Override
    protected boolean isMeetingRequirementsForGlowingEyes()
    {
        return isMiniBoss;
    }
    
    @Override
    protected boolean isExplodingOnCollisions()
    {
        return !isMiniBoss && super.isExplodingOnCollisions();
    }
    
    @Override
    protected void prepareRemoval()
    {
        super.prepareRemoval();
        if(isMiniBoss)
        {
            EnemyController.currentMiniBoss = null;
        }
    }
    
    @Override
    protected boolean hasDeadlyShots()
    {
        return isMiniBoss;
    }
    
    @Override
    public boolean areALlRequirementsForPowerUpDropMet()
    {
        return super.areALlRequirementsForPowerUpDropMet() || isMiniBoss;
    }
    
    @Override
    public void grantGeneralRewards(GameRessourceProvider gameRessourceProvider)
    {
        super.grantGeneralRewards(gameRessourceProvider);
        if(isMiniBoss)
        {
            Audio.play(Audio.applause2);
        }
    }
    
    @Override
    protected boolean canDropPowerUp()
    {
        return super.canDropPowerUp() || isMiniBoss;
    }
    
    @Override
    protected float getTurnProbability()
    {
        return getTurnProbabilityFactor() * super.getTurnProbability();
    }
    
    protected float getTurnProbabilityFactor()
    {
        return isMiniBoss ? 2f : 1f;
    }
    
    @Override
    protected boolean isBoss()
    {
        return isMiniBoss || super.isBoss();
    }
    
    @Override
    protected int getRewardFactor()
    {
        return isMiniBoss ? MINI_BOSS_REWARD_FACTOR : super.getRewardFactor();
    }
    
    @Override
    protected void evaluateBossDestructionEffect(GameRessourceProvider gameRessourceProvider)
    {
        if(isMiniBoss)
        {
            EnemyController.currentMiniBoss = null;
        }
    }
    
    @Override
    protected Color getDefaultBarColor()
    {
        if(isMiniBoss)
        {
            return secondaryColor;
        }
        return super.getDefaultBarColor();
    }
    
    @Override
    protected int getCallBackMinimumForTurnAtBarrier()
    {
        return isMiniBoss
                ? MINI_BOSS_CALL_BACK_MINIMUM_FOR_TURN_AT_BARRIER
                : super.getCallBackMinimumForTurnAtBarrier();
    }
    
    protected final boolean isMiniBoss()
    {
        return isMiniBoss;
    }
}
