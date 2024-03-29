package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;
import de.helicopter_vs_aliens.util.Calculations;

public abstract class BossEnemy extends StandardEnemy
{
    protected static final int
        INCREASED_KAMIKAZE_SPEED_UP_X = 12;
    
    private static final int
        HEALED_HIT_POINTS = 11,
        EMP_SLOW_TIME_BOSS = 110;
    
    private static final float
        SPONTANEOUS_TURN_PROBABILITY = 0.008f;
    
    
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
    
    protected void bossTypeSpecificDestructionEffect(GameRessourceProvider gameRessourceProvider){}
    
    protected void bossInactivationEvent()
    {
        Events.boss = null;
    }
    
    protected void killOwnServants(GameRessourceProvider gameRessourceProvider)
    {
        gameRessourceProvider.getActiveGameEntityManager()
                             .getEnemies()
                             .get(CollectionSubgroupType.ACTIVE)
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
    
    @Override
    protected void makeKamikazeIfAppropriate()
    {
        if(isTurningAroundSpontaneouslyTowards())
        {
            getNavigationDevice().turnAround();
            setSpeedLevelToZeroX();
        }
        super.makeKamikazeIfAppropriate();
    }
    
    private boolean isTurningAroundSpontaneouslyTowards()
    {
        // Boss-Gegner mit der Fähigkeit "Kamikaze" drehen mit einer bestimmten
        // Wahrscheinlichkeit um, wenn sie dem Helikopter das Heck zugekehrt haben.
        return Calculations.tossUp(SPONTANEOUS_TURN_PROBABILITY) && isMovingAwayFromHelicopter();
    }
    
    @Override
    protected boolean isRemainingOnScreen()
    {
        return true;
    }
}
