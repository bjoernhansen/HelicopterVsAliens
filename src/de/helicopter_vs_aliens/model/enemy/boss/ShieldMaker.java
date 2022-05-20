package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;

public abstract class ShieldMaker extends FinalBossServant
{
    private static final int
        SHIELD_TARGET_DISTANCE = 20;
    
    private static final Point
        TARGET_DISTANCE_VARIANCE = new Point(10, 3),
        SHIELD_MAKER_CALM_DOWN_SPEED = new Point(3, 3);
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        this.setRandomDirectionX();
        this.shieldMakerTimer = READY;
        this.setShieldingPosition();
    
        super.doTypeSpecificInitialization();
    }
    
    @Override
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        if(this.shieldMakerTimer != DISABLED)
        {
            this.shieldMakerAction();
        }
        super.performFlightManeuver(gameRessourceProvider);
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
            this.turnRight();
        }
        else if( this.getX()
            > Events.boss.getCenterX()
            + TARGET_DISTANCE_VARIANCE.x)
        {
            this.turnLeft();
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
            this.flyDown();
        }
        else if( this.isUpperShieldMaker
            && this.getMaxY() > Events.boss.getMinY() - SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y
            ||
            !this.isUpperShieldMaker
                && this.getMinY() > Events.boss.getMaxY() + SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y)
        {
            this.flyUp();
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
        this.turnLeft();
        this.isShielding = true;
        Events.boss.shield++;
        this.canDodge = true;
        this.shieldMakerTimer = DISABLED;
    }
}
