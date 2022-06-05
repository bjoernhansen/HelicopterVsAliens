package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;

import static de.helicopter_vs_aliens.model.explosion.ExplosionType.STUNNING;

public abstract class ShieldMaker extends FinalBossServant
{
    private static final int
        SHIELD_TARGET_DISTANCE = 20;
    
    private static final Point
        TARGET_DISTANCE_VARIANCE = new Point(10, 3),
        SHIELD_MAKER_CALM_DOWN_SPEED = new Point(3, 3),
        SHIELD_MAKER_STAMPEDE_SPEED = new Point(10, 10);
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        getNavigationDevice().setRandomDirectionX();
        shieldMakerTimer = READY;
        setShieldingPosition();
    
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
        if(      getX()
            < Events.boss.getCenterX()
            - TARGET_DISTANCE_VARIANCE.x)
        {
            getNavigationDevice().turnRight();
        }
        else if( getX()
            > Events.boss.getCenterX()
            + TARGET_DISTANCE_VARIANCE.x)
        {
            getNavigationDevice().turnLeft();
        }
        
        if(	 isUpperShieldMaker
            && getMaxY()
            < Events.boss.getMinY()
            - SHIELD_TARGET_DISTANCE
            - TARGET_DISTANCE_VARIANCE.y
            ||
            !isUpperShieldMaker
                && getMinY()
                < Events.boss.getMaxY()
                + SHIELD_TARGET_DISTANCE
                - TARGET_DISTANCE_VARIANCE.y)
        {
            getNavigationDevice().flyDown();
        }
        else if( isUpperShieldMaker
            && getMaxY() > Events.boss.getMinY() - SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y
            ||
            !isUpperShieldMaker
                && getMinY() > Events.boss.getMaxY() + SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y)
        {
            getNavigationDevice().flyUp();
        }
    }
    
    private boolean canStartShielding()
    {
        return 	   this.shieldMakerTimer > 200
                    && !isRecoveringSpeed
                    && TARGET_DISTANCE_VARIANCE.x
                    > Math.abs(Events.boss.getCenterX()
                    -getX())
                    &&  TARGET_DISTANCE_VARIANCE.y
                    > (isUpperShieldMaker
                    ? Math.abs(getMaxY()
                    - Events.boss.getMinY()
                    + SHIELD_TARGET_DISTANCE)
                    : Math.abs(getMinY()
                    - Events.boss.getMaxY()
                    - SHIELD_TARGET_DISTANCE));
    }
    
    private void startShielding()
    {
        Audio.play(Audio.shieldUp);
        getSpeedLevel().setLocation(ZERO_SPEED);
        getNavigationDevice().turnLeft();
        isShielding = true;
        Events.boss.shield++;
        canDodge = true;
        shieldMakerTimer = DISABLED;
    }
    
    @Override
    public void dodge(Missile missile)
    {
        super.dodge(missile);
        stampedeShieldMaker();
    }
    
    private void stampedeShieldMaker()
    {
        shieldMakerTimer = READY;
        getSpeedLevel().setLocation(SHIELD_MAKER_STAMPEDE_SPEED);
        targetSpeedLevel.setLocation(SHIELD_MAKER_STAMPEDE_SPEED);
        canMoveChaotic = true;
        canDodge = false;
        setShieldingPosition();
        if(isShielding){stopShielding();}
    }
    
    @Override
    public void die(GameRessourceProvider gameRessourceProvider, Missile missile, boolean beamKill)
    {
        if(isShielding){stopShielding();}
        super.die(gameRessourceProvider, missile, beamKill);
    }
    
    private void stopShielding()
    {
        if(Events.boss.shield == 1){Audio.shieldUp.stop();}
        Events.boss.shield--;
        isShielding = false;
    }
    
    @Override
    protected boolean isAbleToBeSlowedDownByEmp()
    {
        return !isShielding && super.isAbleToBeSlowedDownByEmp();
    }
}
