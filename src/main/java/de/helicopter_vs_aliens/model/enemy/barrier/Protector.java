package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.enemy.boss.FinalBoss;
import de.helicopter_vs_aliens.model.enemy.boss.FinalBossAcquaintance;
import de.helicopter_vs_aliens.model.enemy.boss.FinalBossServant;

public final class Protector extends BurrowingBarrier implements FinalBossAcquaintance
{
    private static final int
        POSITION_X = 919,
        SHOOT_PAUSE = 0,
        SHOOTING_RATE = 25,
        SHOTS_PER_CYCLE = 5,
        SHOOT_SPEED = 10;
        
    @Override
    protected void doTypeSpecificInitialization()
    {
        getFinalBoss().registerServant(this);
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected int calculateShootPause()
    {
        return SHOOT_PAUSE;
    }
    
    @Override
    protected int calculateShootingRate()
    {
        return SHOOTING_RATE;
    }
    
    @Override
    protected int calculateShotsPerCycle()
    {
        return SHOTS_PER_CYCLE;
    }
    
    @Override
    protected int calculateShootSpeed()
    {
        return SHOOT_SPEED;
    }
    
    @Override
    protected boolean isArmingWithBusterMissilesApproved()
    {
        return true;
    }
    
    @Override
    protected double calculateInitialX()
    {
        return POSITION_X;
    }
    
    @Override
    protected double calculateInitialY()
    {
        return GROUND_Y;
    }
    
    @Override
    protected int getWidthVariance()
    {
        return 0;
    }
    
    @Override
    public boolean isDisappearingAfterEnteringRepairShop()
    {
        return true;
    }
    
    @Override
    public boolean isStunnable()
    {
        return false;
    }
    
    @Override
    public boolean canCountForKillsAfterLevelUp()
    {
        return false;
    }
    
    @Override
    protected void evaluateBossDestructionEffect(GameRessourceProvider gameRessourceProvider)
    {
        finalBossServantRemoval();
    }
    
    @Override
    public FinalBoss getFinalBoss()
    {
        return (FinalBoss)Events.boss;
    }
    
    @Override
    public void finalBossServantRemoval()
    {
        getFinalBoss().removeServant(FinalBossServantType.PROTECTOR);
    }
}
