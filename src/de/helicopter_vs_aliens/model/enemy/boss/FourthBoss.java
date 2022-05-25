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
        this.spawningHornetTimer = 30;
        LevelManager.nextBossEnemyType = EnemyType.BOSS_4_SERVANT;
        LevelManager.maxNr = 15;
        this.canTurn = true;
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        this.boss4Action(gameRessourceProvider.getEnemies());
        super.performFlightManeuver(gameRessourceProvider);
    }
    
    private void boss4Action(Map<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        if(    this.getX() < 930
            && this.getX() > 150)
        {
            this.spawningHornetTimer++;
        }
        if(this.spawningHornetTimer == 1)
        {
            this.getSpeedLevel()
                .setLocation(11, 11);
            this.canMoveChaotic = true;
            this.canKamikaze = true;
        }
        else if(this.spawningHornetTimer >= 50)
        {
            if(this.spawningHornetTimer == 50)
            {
                this.getSpeedLevel()
                    .setLocation(3, 3);
                this.canMoveChaotic = false;
                this.canKamikaze = false;
            }
            else if(this.spawningHornetTimer == 90)
            {
                this.getSpeedLevel()
                    .setLocation(ZERO_SPEED);
            }
            if(enemy.get(ACTIVE).size() < 15
                && (    this.spawningHornetTimer == 60
                || this.spawningHornetTimer == 90
                || Calculations.tossUp(0.02f)))
            {
                boss.setLocation(	this.getX() + this.getWidth() /2,
                    this.getY() + this.getHeight()/2);
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
}
