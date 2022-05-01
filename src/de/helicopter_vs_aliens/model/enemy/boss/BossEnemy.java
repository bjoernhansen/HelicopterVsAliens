package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.DefaultEnemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Dimension;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.BUSTER;

public class BossEnemy extends DefaultEnemy
{
    private static final int
        FINAL_BOSS_STARTING_POSITION_Y = 98,
        FINAL_BOSS_WIDTH = 450;
    
    private static final Dimension
        FINAL_BOSS_DIMENSION = new Dimension(FINAL_BOSS_WIDTH, (int)HEIGHT_FACTOR * FINAL_BOSS_WIDTH );
    
    @Override
    protected void create(Helicopter helicopter)
    {
        this.createBoss(helicopter);
        super.create(helicopter);
    }
    
    private void createBoss(Helicopter helicopter)
    {
        // Level 10
        if( this.type == EnemyType.BOSS_1)
        {
            this.farbe1 = new Color(115, 70, 100);
            this.hitpoints = 225;
            this.setWidth(275);
            this.targetSpeedLevel.setLocation(2, 0.5);
            
            this.canKamikaze = true;
            
            Events.boss = this;
        }
        // Level 20
        else if( this.type == EnemyType.BOSS_2)
        {
            this.model = CARGO;
            this.farbe1 = new Color(85, 85, 85);
            this.hitpoints = 500;
            this.setWidth(250);
            this.targetSpeedLevel.setLocation(7, 8);
            
            this.canMoveChaotic = true;
            this.shootTimer = 0;
            this.shootingRate = 5;
            this.shotSpeed = 3;
            this.canInstantTurn = true;
            
            Events.boss = this;
        }
        else if( this.type == EnemyType.BOSS_2_SERVANT)
        {
            this.bounds.setRect(boss.getX(),
                boss.getY(),
                65,
                this.bounds.getHeight());
            this.hasYPosSet = true;
            this.farbe1 = new Color(80 + Calculations.random(25), 80 + Calculations.random(25), 80 + Calculations.random(25));
            this.hitpoints = 15;
            this.targetSpeedLevel.setLocation(3 + 10.5*Math.random(),
                3 + 10.5*Math.random());
            
            this.direction.x = Calculations.randomDirection();
            this.invincibleTimer = 67;
        }
        // Level 30
        else if( this.type == EnemyType.BOSS_3)
        {
            this.setWidth(250);
            this.farbe1 = Colorations.cloaked;
            this.hitpoints = 1750;
            this.targetSpeedLevel.setLocation(5, 4);
            
            this.canMoveChaotic = true;
            this.canKamikaze = true;
            this.cloakingTimer = READY;
            this.canDodge = true;
            this.shootTimer = 0;
            this.shootingRate = 10;
            this.shotSpeed = 10;
            this.canInstantTurn = true;
            
            Events.boss = this;
        }
        // Level 40
        else if(this.type == EnemyType.BOSS_4)
        {
            this.setWidth(250);
            this.farbe1 = Color.red;
            this.hitpoints = 10000;
            this.targetSpeedLevel.setLocation(10, 10);
            
            this.spawningHornetTimer = 30;
            nextBossEnemyType = EnemyType.BOSS_4_SERVANT;
            maxNr = 15;
            this.canTurn = true;
            
            Events.boss = this;
        }
        else if(this.type == EnemyType.BOSS_4_SERVANT)
        {
            this.bounds.setRect(boss.getX(),
                boss.getY(),
                85 + Calculations.random(15),
                this.bounds.getHeight());
            this.hasYPosSet = true;
            this.farbe1 = new Color(80 + Calculations.random(20), 80 + Calculations.random(20), 80 + Calculations.random(20));
            this.hitpoints = 100 + Calculations.random(50);
            this.targetSpeedLevel.setLocation(6 + 2.5*Math.random(),
                6 + 2.5*Math.random());
            this.direction.x = Calculations.randomDirection();
            this.canExplode = true;
        }
        // Level 50
        else if(this.type == EnemyType.FINAL_BOSS)
        {
            this.bounds.setRect(this.bounds.getX(),
                FINAL_BOSS_STARTING_POSITION_Y,
                FINAL_BOSS_DIMENSION.width,
                FINAL_BOSS_DIMENSION.height);
            this.hasYPosSet = true;
            this.hasHeightSet = true;
            
            this.farbe1 = Colorations.brown;
            this.hitpoints = 25000;
            this.targetSpeedLevel.setLocation(23.5, 0);
            
            maxNr = 5;
            this.operator = new Enemy.FinalEnemyOperator();
            this.isStunnable = false;
            this.dimFactor = 1.3f;
            
            Events.boss = this;
        }
        else if(this.type.isFinalBossServant())
        {
            Events.boss.operator.servants[this.id()] = this;
            this.hasYPosSet = true;
            
            if(this.type.isShieldMaker())
            {
                this.bounds.setRect(boss.getX(),
                    boss.getY(),
                    this.type == EnemyType.SMALL_SHIELD_MAKER ? 125 : 145,
                    this.bounds.getHeight());
                this.direction.x = Calculations.randomDirection();
                
                this.shieldMakerTimer = READY;
                this.setShieldingPosition();
                
                if(this.type == EnemyType.SMALL_SHIELD_MAKER)
                {
                    this.targetSpeedLevel.setLocation(7, 6.5);
                    this.farbe1 = new Color(25, 125, 105);
                    this.hitpoints = 3000;
                }
                else
                {
                    this.targetSpeedLevel.setLocation(6.5, 7);
                    this.farbe1 = new Color(105, 135, 65);
                    this.hitpoints = 4250;
                    
                    this.shootTimer = 0;
                    this.shootingRate = 25;
                    this.shotSpeed = 1;
                }
            }
            else if( this.type == EnemyType.BODYGUARD)
            {
                this.bounds.setRect(boss.getX(),
                    boss.getY(),
                    225,
                    this.bounds.getHeight());
                this.farbe1 = Colorations.cloaked;
                this.hitpoints = 7500;
                this.targetSpeedLevel.setLocation(1, 2);
                
                this.cloakingTimer = 0;
                this.canInstantTurn = true;
                
                Events.boss.operator.servants[this.id()] = this;
            }
            else if(this.type == EnemyType.HEALER)
            {
                this.model = CARGO;
                
                this.bounds.setRect(boss.getX(),
                    boss.getY(),
                    115,
                    this.bounds.getHeight());
                this.farbe1 = Color.white;
                this.hitpoints = 3500;
                this.targetSpeedLevel.setLocation(2.5, 3);
                
                this.canDodge = true;
                
                Events.boss.operator.servants[this.id()] = this;
            }
            else if(this.type == EnemyType.PROTECTOR)
            {
                this.model = BARRIER;
                
                this.bounds.setRect(boss.getX() + 200,
                    GROUND_Y,
                    PROTECTOR_WIDTH,
                    this.bounds.getHeight());
                
                helicopter.numberOfEnemiesSeen--;
                this.hitpoints = Integer.MAX_VALUE;
                this.isClockwiseBarrier = Calculations.tossUp();
                this.farbe1 = Colorations.bleach(new Color(170, 0, 255), 0.6f);
                this.targetSpeedLevel.setLocation(ZERO_SPEED);
                
                this.deactivationProb = 0.04f;
                this.burrowTimer = READY;
                this.shootingRate = 25;
                this.shotsPerCycle = 5;
                this.shootingCycleLength = this.shootPause
                    + this.shootingRate
                    * this.shotsPerCycle;
                this.shotSpeed = 10;
                this.shotType = BUSTER;
                this.isStunnable = false;
                this.farbe2 = Colorations.dimColor(this.farbe1, 0.75f);
                if(Events.timeOfDay == NIGHT)
                {
                    this.farbe1 = Colorations.dimColor(this.farbe1, Colorations.BARRIER_NIGHT_DIM_FACTOR);
                    this.farbe2 = Colorations.dimColor(this.farbe2, Colorations.BARRIER_NIGHT_DIM_FACTOR);
                }
                Events.boss.operator.servants[this.id()] = this;
            }
        }
    }
}
