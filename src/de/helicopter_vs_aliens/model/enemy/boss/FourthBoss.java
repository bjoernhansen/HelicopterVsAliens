package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.control.LevelManager;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.util.Calculations;

import java.util.LinkedList;
import java.util.Map;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;

public class FourthBoss extends BossEnemy
{
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
        boss4Action(gameRessourceProvider.getEnemies());
        super.performFlightManeuver(gameRessourceProvider);
    }
    
    private void boss4Action(Map<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        if(    getX() < 930
            && getX() > 150)
        {
            spawningHornetTimer++;
        }
        if(spawningHornetTimer == 1)
        {
            getSpeedLevel()
                .setLocation(11, 11);
            canMoveChaotic = true;
            canKamikaze = true;
        }
        else if(spawningHornetTimer >= 50)
        {
            if(spawningHornetTimer == 50)
            {
                getSpeedLevel()
                    .setLocation(3, 3);
                canMoveChaotic = false;
                canKamikaze = false;
            }
            else if(spawningHornetTimer == 90)
            {
                getSpeedLevel()
                    .setLocation(ZERO_SPEED);
            }
            if(enemy.get(ACTIVE).size() < 15
                && (    spawningHornetTimer == 60
                || spawningHornetTimer == 90
                || Calculations.tossUp(0.02f)))
            {
                boss.setLocation(	getX() + getWidth() /2,
                    getY() + getHeight()/2);
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
}
