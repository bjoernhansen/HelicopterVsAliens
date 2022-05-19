package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.enemy.StandardEnemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.function.Predicate;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;

public abstract class BossEnemy extends StandardEnemy
{
    private static final int HEALED_HIT_POINTS = 11;
    private static final int SHIELD_TARGET_DISTANCE = 20;
    
    private static final Point
        TARGET_DISTANCE_VARIANCE = new Point(10, 3),
        SHIELD_MAKER_CALM_DOWN_SPEED = new Point(3, 3);
    
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
    
    @Override
    protected void performFlightManeuver(Controller controller, Helicopter helicopter)
    {
        if(this.type.isMajorBoss())
        {
            this.calculateBossManeuver(controller.enemies);
        }
        super.performFlightManeuver(controller, helicopter);
    }
    
    protected void calculateBossManeuver(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        if(this.type == EnemyType.BOSS_4)    {this.boss4Action(enemy);}
        else if(this.type == EnemyType.FINAL_BOSS){this.finalBossAction();}
        else if(this.shieldMakerTimer != DISABLED){this.shieldMakerAction();}
        else if(this.type == EnemyType.BODYGUARD) {this.bodyguardAction();}
        else if(this.type == EnemyType.HEALER
            && this.dodgeTimer == READY){this.healerAction();}
    }
    
    private void boss4Action(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
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
    
    private void shieldMakerAction()
    {
        this.shieldMakerTimer++;
        if(this.shieldMakerTimer > 100)
        {
            if(this.shieldMakerTimer == 101){this.calmDown();}
            this.correctShieldMakerDirection();
            if(this.canStartShielding()){this.startShielding();}
        }
    }
    
    private void calmDown()
    {
        this.getSpeedLevel().setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);
        this.targetSpeedLevel.setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);
        this.canMoveChaotic = false;
    }
    
    // TODO Bedingungen in Methoden auslagern
    private void correctShieldMakerDirection()
    {
        if(      this.getX()
            < Events.boss.getCenterX()
            - TARGET_DISTANCE_VARIANCE.x)
        {
            this.direction.x =  1;
        }
        else if( this.getX()
            > Events.boss.getCenterX()
            + TARGET_DISTANCE_VARIANCE.x)
        {
            this.direction.x = -1;
        }
        
        if(	 this.isUpperShieldMaker
            && this.getMaxY()
            < Events.boss.getMinY()
            - SHIELD_TARGET_DISTANCE
            - TARGET_DISTANCE_VARIANCE.y
            ||
            !this.isUpperShieldMaker
                && this.getMinY()
                < Events.boss.getMaxY()
                + SHIELD_TARGET_DISTANCE
                - TARGET_DISTANCE_VARIANCE.y)
        {
            this.direction.y = 1;
        }
        else if( this.isUpperShieldMaker
            && this.getMaxY() > Events.boss.getMinY() - SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y
            ||
            !this.isUpperShieldMaker
                && this.getMinY() > Events.boss.getMaxY() + SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y)
        {
            this.direction.y = -1;
        }
    }
    
    private boolean canStartShielding()
    {
        return 	   this.shieldMakerTimer > 200
            && !this.isRecoveringSpeed
            && TARGET_DISTANCE_VARIANCE.x
            > Math.abs(Events.boss.getCenterX()
            -this.getX())
            &&  TARGET_DISTANCE_VARIANCE.y
            > (this.isUpperShieldMaker
            ? Math.abs(this.getMaxY()
            - Events.boss.getMinY()
            + SHIELD_TARGET_DISTANCE)
            : Math.abs(this.getMinY()
            - Events.boss.getMaxY()
            - SHIELD_TARGET_DISTANCE));
    }
    
    private void startShielding()
    {
        Audio.play(Audio.shieldUp);
        this.getSpeedLevel().setLocation(ZERO_SPEED);
        this.direction.x = -1;
        this.isShielding = true;
        Events.boss.shield++;
        this.canDodge = true;
        this.shieldMakerTimer = DISABLED;
    }
    
    private void bodyguardAction()
    {
        if(Events.boss.shield < 1)
        {
            this.canKamikaze = true;
            this.getSpeedLevel().setLocation(7.5, this.getSpeedLevel().getY());
        }
        else
        {
            this.canKamikaze = false;
            this.getSpeedLevel().setLocation(this.targetSpeedLevel);
        }
    }
    
    private void healerAction()
    {
        if(Events.boss.getHitPoints() < Events.boss.startingHitPoints)
        {
            if(this.getSpeedLevel().getX() != 0)
            {
                int stop = 0;
                if(this.getX() < Events.boss.getX()
                    + 0.55f * Events.boss.getWidth())
                {
                    this.direction.x = 1;
                }
                else if(this.getX() > Events.boss.getX()
                    + 0.65f * Events.boss.getWidth())
                {
                    this.direction.x = -1;
                }
                else{stop++;}
                
                if(		this.getY() < Events.boss.getY()
                    + Events.boss.getHeight()
                    - 1.25f * this.getHeight())
                {
                    this.direction.y = 1;
                }
                else if(this.getY() > Events.boss.getY()
                    + Events.boss.getHeight()
                    - 1.05f * this.getHeight())
                {
                    this.direction.y = -1;
                }
                else{stop++;}
                
                if(stop >= 2)
                {
                    this.getSpeedLevel().setLocation(ZERO_SPEED);
                    this.direction.x = -1;
                    this.canDodge = true;
                }
            }
            else
            {
                Events.boss.healHitPoints();
            }
        }
        else
        {
            this.getSpeedLevel().setLocation(this.targetSpeedLevel);
        }
    }
    
    private void healHitPoints()
    {
        int newHitPoints = Math.min(Events.boss.getHitPoints() + HEALED_HIT_POINTS,
            Events.boss.startingHitPoints);
        this.setHitPoints(newHitPoints);
    }
}
