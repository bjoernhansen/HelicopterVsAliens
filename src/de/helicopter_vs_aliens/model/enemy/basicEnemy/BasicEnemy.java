package de.helicopter_vs_aliens.model.enemy.basicEnemy;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;

public abstract class BasicEnemy extends StandardEnemy
{
    private static final float
        STANDARD_MINI_BOSS_PROB = 0.05f,
        CHEAT_MINI_BOSS_PROB = 1.0f;
    
    public static Enemy
        currentMiniBoss;    // Referenz auf den aktuellen Boss-Gegner
    public static float
        miniBossProb = 0.05f;// bestimmt die Häufigkeit, mit der Mini-Bosse erscheinen
    
    
    public static void changeMiniBossProb()
    {
        BasicEnemy.miniBossProb = BasicEnemy.miniBossProb == STANDARD_MINI_BOSS_PROB ? CHEAT_MINI_BOSS_PROB: STANDARD_MINI_BOSS_PROB;
    }
    
    @Override
    protected void create(Helicopter helicopter)
    {
        if(canBecomeMiniBoss())
        {
            turnIntoMiniBoss(helicopter);
        }
        super.create(helicopter);
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
        bounds.setRect( this.bounds.getX(),
                        bounds.getY(),
                        1.44 * this.bounds.getWidth(),
                        1.44 * this.bounds.getHeight());
        isMiniBoss = true;
        canExplode = false;
        callBack += 2;
        canTurn = true;
        if((type.isCloakableAsMiniBoss() && !canLearnKamikaze && Calculations.tossUp(0.2f)) || shootTimer == 0 )
        {
            cloakingTimer = 0;
        }
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
