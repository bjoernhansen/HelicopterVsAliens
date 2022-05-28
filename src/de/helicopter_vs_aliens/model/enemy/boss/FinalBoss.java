package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.control.LevelManager;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.util.function.Predicate;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;

public class FinalBoss extends BossEnemy
{
    private static final int
        FINAL_BOSS_POSITION_Y = 98;
    
    public static final float
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 1.3f;
        
    @Override
    protected void doTypeSpecificInitialization()
    {
        LevelManager.maxNr = 5; // TODO diese Zuweisung gehört hier nicht her oder?
        operator = new Enemy.FinalEnemyOperator();
        Events.boss = this;
    
        super.doTypeSpecificInitialization();
    }

    @Override
    protected double calculateInitialY()
    {
        return FINAL_BOSS_POSITION_Y;
    }
    
    @Override
    protected float getSecondaryColorBrightnessFactor()
    {
        return SECONDARY_COLOR_BRIGHTNESS_FACTOR;
    }
    
    @Override
    public boolean isStunable()
    {
        return false;
    }
    
    @Override
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        finalBossAction();
        super.performFlightManeuver(gameRessourceProvider);
    }
    private void finalBossAction()
    {
        if(getSpeedLevel()
               .getX() > 0)
        {
            if(getSpeedLevel()
                   .getX() - 0.5 <= 0)
            {
                getSpeedLevel()
                    .setLocation(ZERO_SPEED);
                boss.setLocation(getCenterX(),
                    getCenterY());
                EnemyController.makeAllBoss5Servants = true;
            }
            else
            {
                getSpeedLevel()
                    .setLocation(getSpeedLevel()
                                     .getX()-0.5,	0);
            }
        }
        else
        {
            FinalBossServantType.getValues()
                                .stream()
                                .filter(Predicate.not(operator::containsServant))
                                .forEach(servantType -> {
                                    if (isFinalBossServantCreationAllowedFor(servantType))
                                    {
                                        EnemyController.makeFinalBossServant.add(servantType);
                                    }
                                    else
                                    {
                                        operator.incrementTimeSinceDeathCounter(servantType);
                                    }
                                });
        }
    }
    
    private boolean isFinalBossServantCreationAllowedFor(FinalBossServantType servantType)
    {
        return Calculations.tossUp(servantType.getReturnProbability())
            && operator.hasMinimumTimeBeforeRecreationElapsed(servantType);
    }
    
    @Override
    protected void bossInactivationEvent(){}
    
    @Override
    protected void bossTypeSpecificDestructionEffect(GameRessourceProvider gameRessourceProvider)
    {
        killOwnServants(gameRessourceProvider);
        
        Events.isRestartWindowVisible = true;
        Events.level = Events.maxLevel = 51;
    
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        helicopter.isDamaged = true;
        // TODO Konstanten definieren
        helicopter.destination.setLocation(helicopter.getX()+40,520.0);
        Events.determineHighscoreTimes(helicopter);
    }
}
