package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.LevelManager;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Pegasus;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Queue;


public class FourthBoss extends BossEnemy
{
    private static final int
        FIRST_SERVANT_CREATION_TIME = 60,
        SECOND_SERVANT_CREATION_TIME = 90;
    
    private static final float
        SPONTANEOUS_SERVANT_CREATION_PROBABILITY = 0.02f;
    
    private static final Point2D
        FAST_SPEED = new Point2D.Float(11, 11),
        MEDIUM_SPEED = new Point2D.Float(3, 3);
    
    private int
        spawningHornetTimer;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        spawningHornetTimer = 30;
        LevelManager.nextBossEnemyType = EnemyType.BOSS_4_SERVANT;
        LevelManager.maxNr = 15;
        canTurn = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        boss4Action(gameRessourceProvider.getActiveGameEntityManager()
                                         .getEnemies());
        super.performFlightManeuver(gameRessourceProvider);
    }
    
    private void boss4Action(Map<CollectionSubgroupType, Queue<Enemy>> enemies)
    {
        if(    getX() < 930
            && getX() > 150)
        {
            spawningHornetTimer++;
        }
        if(spawningHornetTimer == 1)
        {
            getSpeedLevel().setLocation(FAST_SPEED);
            canMoveChaotic = true;
            canKamikaze = true;
        }
        else if(spawningHornetTimer >= 50)
        {
            if(spawningHornetTimer == 50)
            {
                getSpeedLevel().setLocation(MEDIUM_SPEED);
                canMoveChaotic = false;
                canKamikaze = false;
            }
            else if(spawningHornetTimer == SECOND_SERVANT_CREATION_TIME)
            {
                getSpeedLevel().setLocation(ZERO_SPEED);
            }
            if(enemies.get(CollectionSubgroupType.ACTIVE).size() < 15
                && (    spawningHornetTimer == FIRST_SERVANT_CREATION_TIME
                        || spawningHornetTimer == SECOND_SERVANT_CREATION_TIME
                        || Calculations.tossUp(SPONTANEOUS_SERVANT_CREATION_PROBABILITY)))
            {
                boss.setLocation(getCenterX(), getCenterY());
                EnemyController.makeBoss4Servant = true;
            }
        }
    }
    
    @Override
    public boolean areALlRequirementsForPowerUpDropMet()
    {
        return true;
    }
    
    @Override
    protected void bossTypeSpecificDestructionEffect(GameRessourceProvider gameRessourceProvider)
    {
        killOwnServants(gameRessourceProvider);
    }
    
    @Override
    protected double getKamikazeSpeedUpX()
    {
        return INCREASED_KAMIKAZE_SPEED_UP_X;
    }
    
    @Override
    public void reactToHit(Missile missile)
    {
        resetSpawningHornetTimer();
        super.reactToHit(missile);
    }
    
    @Override
    protected void empShock(GameRessourceProvider gameRessourceProvider, Pegasus pegasus)
    {
        resetSpawningHornetTimer();
        super.empShock(gameRessourceProvider, pegasus);
    }
    
    private void resetSpawningHornetTimer()
    {
        spawningHornetTimer = READY;
    }
}
