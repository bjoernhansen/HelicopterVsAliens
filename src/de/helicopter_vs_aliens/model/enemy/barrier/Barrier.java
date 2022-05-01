package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;

public abstract class Barrier extends Enemy
{
    @Override
    protected void create(Helicopter helicopter)
    {
        this.createBarrier(helicopter);
        super.create(helicopter);
    }
    
    protected void createBarrier(Helicopter helicopter)
    {
        this.model = BARRIER;
        
        helicopter.numberOfEnemiesSeen--;
        this.hitpoints = Integer.MAX_VALUE;
        this.rotorColor = 1;
        this.isClockwiseBarrier = Calculations.tossUp();
        
        if(this.type == EnemyType.SMALL_BARRIER || this.type == EnemyType.BIG_BARRIER)
        {
            this.farbe1 = Colorations.bleach(Color.green, 0.6f);
            this.isLasting = true;
            
            // Level 2
            if(this.type == EnemyType.SMALL_BARRIER)
            {
                this.setVarWidth(65);
            }
            // Level 6
            else if(this.type == EnemyType.BIG_BARRIER)
            {
                this.setVarWidth(150);
                this.setInitialY(GROUND_Y - this.bounds.getWidth());
            }
        }
        // Level 12
        else if(this.type == EnemyType.STUNNING_BARRIER)
        {
            this.farbe1 = Colorations.bleach(Color.yellow, 0.6f);
            this.targetSpeedLevel.setLocation(0, 1 + 2*Math.random());
            this.setVarWidth(65);
            
            this.rotorColor = 2;
            this.staticChargeTimer = READY;
            this.isLasting = true;
        }
        // Level 15
        else if(this.type == EnemyType.PUSHING_BARRIER)
        {
            this.farbe1 = Colorations.bleach(new Color(255, 192, 0), 0.0f);
            this.targetSpeedLevel.setLocation(0.5 + 2*Math.random(), 0);
            this.setVarWidth(105);
            if(this.targetSpeedLevel.getX() >= 5){this.direction.x = 1;}
            
            this.setLocation(this.targetSpeedLevel.getX() >= 5
                    ? -this.bounds.getWidth()-APPEARANCE_DISTANCE
                    : this.bounds.getX(),
                GROUND_Y - this.bounds.getWidth() - (5 + Calculations.random(11)));
            this.hasYPosSet = true;
        }
        // Level 18
        else if(this.type == EnemyType.SHOOTING_BARRIER)
        {
            this.setVarWidth(85);
            this.hasYPosSet = true;
            this.barrierShootTimer = READY;
            this.setBarrierShootingProperties();
            this.shotRotationSpeed
                = Calculations.tossUp(SPIN_SHOOTER_RATE) && Events.level >= MIN_SPIN_SHOOTER_LEVEL
                ? Calculations.randomDirection()*(this.shootingRate/3 + Calculations.random(10))
                : 0;
            
            this.isLasting = true;
        }
        // Level 32
        else if(this.type == EnemyType.BURROWING_BARRIER)
        {
            this.setVarWidth(80);
            this.setInitialY(GROUND_Y - this.bounds.getWidth()/8);
            
            this.burrowTimer = READY;
            this.setBarrierShootingProperties();
            
            this.isLasting = true;
        }
        // Level 42
        else if(this.type == EnemyType.SHIELDING_BARRIER)
        {
            this.farbe1 =  Colorations.bleach(new Color(33 ,125 ,115),0.4f);
            this.setVarWidth(80);
            
            this.isLasting = true;
        }
        // Level 44
        else if(this.type == EnemyType.CLOAKED_BARRIER)
        {
            this.farbe1 = Colorations.bleach(Colorations.cloaked, 0.6f);
            this.setVarWidth(100);
            
            this.barrierTeleportTimer = READY;
            this.setBarrierShootingProperties();
            this.startBarrierUncloaking(helicopter);
            
            this.hasYPosSet = true;
            this.callBack = 1 + Calculations.random(4);
        }
        
        this.farbe2 = Colorations.dimColor(this.farbe1, 0.75f);
        this.deactivationProb = 1.0f / this.type.getStrength();
        
        if(Events.timeOfDay == NIGHT)
        {
            this.farbe1 = Colorations.dimColor(this.farbe1, Colorations.BARRIER_NIGHT_DIM_FACTOR);
            this.farbe2 = Colorations.dimColor(this.farbe2, Colorations.BARRIER_NIGHT_DIM_FACTOR);
        }
        barrierTimer = (int)((helicopter.getBounds().getWidth() + this.bounds.getWidth())/2);
    }
}
