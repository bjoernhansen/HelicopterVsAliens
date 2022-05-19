package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.LevelManager;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.util.function.Predicate;

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
        this.operator = new Enemy.FinalEnemyOperator();
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
    protected void performFlightManeuver(Controller controller, Helicopter helicopter)
    {
        this.finalBossAction();
        super.performFlightManeuver(controller, helicopter);
    }
    private void finalBossAction()
    {
        if(this.getSpeedLevel()
               .getX() > 0)
        {
            if(this.getSpeedLevel()
                   .getX() - 0.5 <= 0)
            {
                this.getSpeedLevel()
                    .setLocation(ZERO_SPEED);
                boss.setLocation(this.getCenterX(),
                    this.getCenterY());
                EnemyController.makeAllBoss5Servants = true;
            }
            else
            {
                this.getSpeedLevel()
                    .setLocation(this.getSpeedLevel()
                                     .getX()-0.5,	0);
            }
        }
        else
        {
            FinalBossServantType.getValues()
                                .stream()
                                .filter(Predicate.not(this.operator::containsServant))
                                .forEach(servantType -> {
                                    if (isFinalBossServantCreationAllowedFor(servantType))
                                    {
                                        EnemyController.makeFinalBossServant.add(servantType);
                                    }
                                    else
                                    {
                                        this.operator.incrementTimeSinceDeathCounter(servantType);
                                    }
                                });
        }
    }
    
    private boolean isFinalBossServantCreationAllowedFor(FinalBossServantType servantType)
    {
        return Calculations.tossUp(servantType.getReturnProbability())
            && this.operator.hasMinimumTimeBeforeRecreationElapsed(servantType);
    }
}
