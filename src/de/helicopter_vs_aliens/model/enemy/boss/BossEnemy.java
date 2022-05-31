package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;

public abstract class BossEnemy extends StandardEnemy
{
    protected static final int
        INCREASED_KAMIKAZE_SPEED_UP_X = 12;
    
    private static final int
        HEALED_HIT_POINTS = 11,
        EMP_SLOW_TIME_BOSS = 110;
    
    @Override
    protected int getRewardModifier()
    {
        return 0;
    }
    
    @Override
    protected boolean isMeetingRequirementsForGlowingEyes()
    {
        return true;
    }
    
    @Override
    protected int getWidthVariance()
    {
        return 0;
    }
   
    protected void healHitPoints()
    {
        int newHitPoints = Math.min(Events.boss.getHitPoints() + HEALED_HIT_POINTS,
            Events.boss.startingHitPoints);
        setHitPoints(newHitPoints);
    }
    
    @Override
    protected void evaluateBossDestructionEffect(GameRessourceProvider gameRessourceProvider)
    {
        bossTypeSpecificDestructionEffect(gameRessourceProvider);
        bossInactivationEvent();
    }
    
    protected void bossTypeSpecificDestructionEffect(GameRessourceProvider gameRessourceProvider){};
    
    protected void bossInactivationEvent()
    {
        Events.boss = null;
    }
    
    protected void killOwnServants(GameRessourceProvider gameRessourceProvider)
    {
        gameRessourceProvider.getEnemies()
                             .get(ACTIVE)
                             .forEach(enemy -> {
                                 enemy.explode(gameRessourceProvider);
                                 if(enemy != this)
                                 {
                                     enemy.destroyByHelicopter(gameRessourceProvider);
                                 }
                             });
    }
    
    @Override
    protected int getEmpSlowTime()
    {
        return EMP_SLOW_TIME_BOSS;
    }
}
