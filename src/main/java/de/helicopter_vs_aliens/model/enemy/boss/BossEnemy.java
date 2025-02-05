package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;
import de.helicopter_vs_aliens.util.Calculations;


public abstract class BossEnemy extends StandardEnemy
{
    static final int
        INCREASED_KAMIKAZE_SPEED_UP_X = 12;
    
    private static final int
        HEALED_HIT_POINTS = 11;
    
    private static final int
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
    protected void evaluateBossDestructionEffect()
    {
        bossTypeSpecificDestructionEffect();
        bossInactivationEvent();
    }
    
    protected void bossTypeSpecificDestructionEffect() {}
    
    protected void bossInactivationEvent()
    {
        Events.boss = null;
    }
    
    void killOwnServants()
    {
        getGameRessourceProvider().getManageablePaintableService()
                                  .forEachActiveEntity(ManageablePaintableGroupType.INTACT_ENEMY, this::killServant);
    }
    
    private void killServant(Enemy enemy)
    {
        enemy.explode();
        if(enemy != this)
        {
            enemy.destroyByHelicopter();
        }
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
