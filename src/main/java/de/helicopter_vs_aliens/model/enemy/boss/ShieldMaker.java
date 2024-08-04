package de.helicopter_vs_aliens.model.enemy.boss;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.FinalBossServantType;
import de.helicopter_vs_aliens.model.missile.Missile;

import java.awt.Point;


public abstract class ShieldMaker extends FinalBossServant
{
    private static final int
        SHIELD_TARGET_DISTANCE = 20;
    
    private static final Point
        TARGET_DISTANCE_VARIANCE = new Point(10, 3);
    
    private static final Point
        CALM_DOWN_SPEED = new Point(3, 3);
    
    private static final Point
        STAMPEDE_SPEED = new Point(10, 10);
    
    
    private int
        stampedeTimer;
    
    private boolean
        isUpperShieldMaker;        // bestimmt die Position der Schild-Aufspannenden Servants von Boss 5
    
    private boolean
        isShielding;            // = true: Gegner spannt gerade ein Schutzschild für Boss 5 auf (nur für Schild-Generatoren von Boss 5)
    
    
    @Override
    public void reset()
    {
        super.reset();
        isShielding = false;
        stampedeTimer = DISABLED;
    }
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        getNavigationDevice().setRandomDirectionX();
        stampedeTimer = READY;
        setShieldingPosition();
        super.doTypeSpecificInitialization();
    }
    
    private void setShieldingPosition()
    {
        isUpperShieldMaker = getFinalBoss().isUpperShieldPositionAvailableFor(this);
    }
    
    abstract FinalBossServantType getShieldingBrotherServantType();
    
    @Override
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        if(this.stampedeTimer != DISABLED)
        {
            this.performStampede();
        }
        super.performFlightManeuver(gameRessourceProvider);
    }
    
    private void performStampede()
    {
        this.stampedeTimer++;
        if(this.stampedeTimer > 100)
        {
            if(this.stampedeTimer == 101)
            {
                this.calmDown();
            }
            this.correctDirection();
            if(this.canStartShielding())
            {
                this.startShielding();
            }
        }
    }
    
    private void calmDown()
    {
        this.getSpeedLevel()
            .setLocation(CALM_DOWN_SPEED);
        this.targetSpeedLevel.setLocation(CALM_DOWN_SPEED);
        this.canMoveChaotic = false;
    }
    
    // TODO Bedingungen in Methoden auslagern
    private void correctDirection()
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
        return this.stampedeTimer > 200
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
        stampedeTimer = DISABLED;
    }
    
    @Override
    public void dodge(Missile missile)
    {
        super.dodge(missile);
        initiateStampede();
    }
    
    private void initiateStampede()
    {
        stampedeTimer = READY;
        getSpeedLevel().setLocation(STAMPEDE_SPEED);
        targetSpeedLevel.setLocation(STAMPEDE_SPEED);
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
    
    boolean isUpperShieldMaker()
    {
        return isUpperShieldMaker;
    }
    
    public boolean isShielding()
    {
        return isShielding;
    }
    
    @Override
    protected boolean generatesEnergieBeam()
    {
        return isShielding();
    }
}
