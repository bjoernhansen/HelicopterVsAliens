package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

public abstract class BasicEnemy extends StandardEnemy
{
    private static final float
        STANDARD_MINI_BOSS_PROB = 0.05f,
        CHEAT_MINI_BOSS_PROB = 1.0f;
    private static final double
        MINI_BOSS_SIZE_FACTOR = 1.44;
    
    public static Enemy
        currentMiniBoss;    // Referenz auf den aktuellen Boss-Gegner
    public static float
        miniBossProb = 0.05f;// bestimmt die Häufigkeit, mit der Mini-Bosse erscheinen
    
    
    public static void changeMiniBossProb()
    {
        BasicEnemy.miniBossProb = BasicEnemy.miniBossProb == STANDARD_MINI_BOSS_PROB ? CHEAT_MINI_BOSS_PROB: STANDARD_MINI_BOSS_PROB;
    }
    
    @Override
    protected void finalizeInitialization(Helicopter helicopter)
    {
        if(canBecomeMiniBoss())
        {
            turnIntoMiniBoss(helicopter);
        }
        super.finalizeInitialization(helicopter);
    }
    
    protected boolean canBecomeMiniBoss()
    {
        return 	currentMiniBoss == null
            && Events.level > 4
            && Calculations.tossUp(miniBossProb);
    }
    
    private void turnIntoMiniBoss(Helicopter helicopter)
    {
        helicopter.numberOfMiniBossSeen++;
        currentMiniBoss = this;
        resizeToMiniBossDimension();
        isMiniBoss = true;
        canExplode = false;
        callBack += 2;
        canTurn = true;
        if((type.isCloakableAsMiniBoss() && !canLearnKamikaze && Calculations.tossUp(0.2f)) || shootTimer == 0 )
        {
            cloakingTimer = 0;
        }
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
        return this.isMiniBoss;
    }
}
