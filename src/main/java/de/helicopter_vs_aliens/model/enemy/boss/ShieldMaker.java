package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;


public abstract class ShieldMaker extends FinalBossServant
{
    private static final int
        SHIELD_TARGET_DISTANCE = 20;
    
    private static final Point
        TARGET_DISTANCE_VARIANCE = new Point(10, 3);
    
    private static final Point
        SHIELD_MAKER_CALM_DOWN_SPEED = new Point(3, 3);
    
    private static final Point
        SHIELD_MAKER_STAMPEDE_SPEED = new Point(10, 10);
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        getNavigationDevice().setRandomDirectionX();
        shieldMakerTimer = READY;
        setShieldingPosition();
        super.doTypeSpecificInitialization();
    }
    
    private void setShieldingPosition()
    {
        if(!getFinalBoss().hasServant(shieldingBrother()))
        {
            isUpperShieldMaker = Calculations.tossUp();
        }
        else
        {
            isUpperShieldMaker
                = !getFinalBoss().getServant(shieldingBrother()).isUpperShieldMaker;
        }
    }
    
    abstract FinalBossServantType shieldingBrother();
    
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
            if(this.shieldMakerTimer == 101)
            {
                this.calmDown();
            }
            this.correctShieldMakerDirection();
            if(this.canStartShielding())
            {
                this.startShielding();
            }
        }
    }
    
    private void calmDown()
    {
        this.getSpeedLevel()
            .setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);
        this.targetSpeedLevel.setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);
        this.canMoveChaotic = false;
    }
    
    // TODO Bedingungen in Methoden auslagern
    private void correctShieldMakerDirection()
    {
        FinalBoss finalBoss = getFinalBoss();
        if(getX()
            < finalBoss.getCenterX()
            - TARGET_DISTANCE_VARIANCE.x)
        {
            getNavigationDevice().turnRight();
        }
        else if(getX()
            > finalBoss.getCenterX()
            + TARGET_DISTANCE_VARIANCE.x)
        {
            getNavigationDevice().turnLeft();
        }
        
        if(isUpperShieldMaker
            && getMaxY()
            < finalBoss.getMinY()
            - SHIELD_TARGET_DISTANCE
            - TARGET_DISTANCE_VARIANCE.y
            ||
            !isUpperShieldMaker
                && getMinY()
                < finalBoss.getMaxY()
                + SHIELD_TARGET_DISTANCE
                - TARGET_DISTANCE_VARIANCE.y)
        {
            getNavigationDevice().flyDown();
        }
        else if(isUpperShieldMaker
            && getMaxY() > finalBoss.getMinY() - SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y
            ||
            !isUpperShieldMaker
                && getMinY() > finalBoss.getMaxY() + SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y)
        {
            getNavigationDevice().flyUp();
        }
    }
    
    private boolean canStartShielding()
    {
        FinalBoss finalBoss = getFinalBoss();
        return this.shieldMakerTimer > 200
            && !isRecoveringSpeed
            && TARGET_DISTANCE_VARIANCE.x
            > Math.abs(finalBoss.getCenterX()
                           - getX())
            && TARGET_DISTANCE_VARIANCE.y
            > (isUpperShieldMaker
            ? Math.abs(getMaxY()
                           - finalBoss.getMinY()
                           + SHIELD_TARGET_DISTANCE)
            : Math.abs(getMinY()
                           - finalBoss.getMaxY()
                           - SHIELD_TARGET_DISTANCE));
    }
    
    private void startShielding()
    {
        Audio.play(Audio.shieldUp);
        stopMoving();
        getNavigationDevice().turnLeft();
        isShielding = true;
        getFinalBoss().shield++;
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
        if(isShielding)
        {
            stopShielding();
        }
    }
    
    @Override
    public void die(GameRessourceProvider gameRessourceProvider, Missile missile, boolean beamKill)
    {
        if(isShielding)
        {
            stopShielding();
        }
        super.die(gameRessourceProvider, missile, beamKill);
    }
    
    private void stopShielding()
    {
        if(getFinalBoss().shield == 1)
        {
            Audio.shieldUp.stop();
        }
        getFinalBoss().shield--;
        isShielding = false;
    }
    
    @Override
    protected boolean isAbleToBeSlowedDownByEmp()
    {
        return !isShielding && super.isAbleToBeSlowedDownByEmp();
    }
}
