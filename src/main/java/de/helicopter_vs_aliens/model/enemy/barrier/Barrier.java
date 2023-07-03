package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.devices.CloakingDevice;
import de.helicopter_vs_aliens.model.helicopter.Phoenix;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;


public abstract class Barrier extends Enemy
{
    private static final int
        WIDTH_VARIANCE_DIVISOR = 5,
        TOP_BOUNDARY = 0,
        BORROW_TIME = 65,
        INACTIVATION_TIME = 150;
    
    private static final float
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 0.75f,
        EXTRA_INACTIVE_TIME_FACTOR 	= 0.65f;
    
    protected int
        snoozeTimer;
    
    
    @Override
    public void reset()
    {
        super.reset();
        snoozeTimer = READY;
    }
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        rotorColor = 1;
        isClockwiseBarrier = Calculations.tossUp();
        deactivationProbability = 1.0f / getType().getStrength();
    }
    
    @Override
    protected void finalizeInitialization()
    {
        EnemyController.barrierTimer = (int)((getHelicopter().getWidth() + getWidth())/2);
 
        super.finalizeInitialization();
        
        if(isShootingBarrier())
        {
            this.initializeShootDirectionOfBarriers();
        }
    }
    
    @Override
    protected void calculateFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        super.calculateFlightManeuver(gameRessourceProvider);
        
        // Vergraben
        if(burrowTimer != DISABLED && !(snoozeTimer > 0))
        {
            evaluateBorrowProcedure();
        }
        
        // Shooting Barrier
        if(barrierShootTimer != DISABLED)
        {
            evaluateBarrierShooting(gameRessourceProvider);
        }
        
        // Snooze bei Hindernissen
        if(snoozeTimer == TIMER_ALMOST_OVER)
        {
            endSnooze();
        }
        
        // Barrier-Teleport
        if(barrierTeleportTimer != DISABLED
            && !(snoozeTimer > 0))
        {
            evaluateBarrierTeleport();
        }
    }
    
    private void evaluateBorrowProcedure()
    {
        if(burrowTimer > 0){burrowTimer--;}
        if(burrowTimer == BORROW_TIME + shootingRate * shotsPerCycle)
        {
            barrierShootTimer = shootingRate * shotsPerCycle;
            getSpeedLevel().setLocation(ZERO_SPEED);
        }
        else if(burrowTimer == BORROW_TIME)
        {
            barrierShootTimer = DISABLED;
            getSpeedLevel().setLocation(SLOW_VERTICAL_SPEED);
            getNavigationDevice().flyDown();
        }
        else if(burrowTimer == 1)
        {
            getSpeedLevel().setLocation(ZERO_SPEED);
        }
        else if(burrowTimer == READY
            &&( (getType() != EnemyType.PROTECTOR
            && Calculations.tossUp(0.004f))
            ||
            (getType() == EnemyType.PROTECTOR
                && (getHelicopter().getX() > boss.getX() - 225) )))
        {
            burrowTimer = 2 * BORROW_TIME
                + shootingRate * shotsPerCycle
                + (getY() == GROUND_Y
                ? EnemyType.PROTECTOR.getWidth()/8
                : 0)
                - 1;
            getSpeedLevel().setLocation(SLOW_VERTICAL_SPEED);
            getNavigationDevice().flyUp();
        }
    }
    
    private void evaluateBarrierShooting(GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        if(barrierShootTimer == 0)
        {
            barrierShootTimer = shootingCycleLength;
            if(	shotRotationSpeed == 0
                &&	  (helicopter.getX()    < getX()         && shootingDirection.getX() > 0)
                ||(helicopter.getMaxX() > getMaxX() && shootingDirection.getX() < 0) )
            {
                shootingDirection.setLocation(-shootingDirection.getX(), shootingDirection.getY());
            }
        }
        if( barrierShootTimer <= shotsPerCycle * shootingRate
            && getX() + getWidth() > 0
            && barrierShootTimer %shootingRate == 0)
        {
            if(shotRotationSpeed != 0)
            {
                float tempValue = 0.0005f * shotRotationSpeed * getLifetime();
                shootingDirection.setLocation(
                    Math.sin(tempValue),
                    Math.cos(tempValue) );
            }
            if(burrowTimer != DISABLED || barrierTeleportTimer != DISABLED)
            {
                // Schussrichtung wird auf Helicopter ausgerichtet
                shootingDirection.setLocation(
                    ( (helicopter.getX() + (helicopter.isMovingLeft ? Helicopter.FOCAL_POINT_X_LEFT : Helicopter.FOCAL_POINT_X_RIGHT))
                        - (getX() +       getWidth()/2)),
                    (helicopter.getY() + Helicopter.FOCAL_POINT_Y_EXP)
                        - (getY() +       getHeight()/2)) ;
                float distance = (float) Calculations.ZERO_POINT.distance(shootingDirection);
                shootingDirection.setLocation(shootingDirection.getX()/distance,
                    shootingDirection.getY()/distance);
            }
            shoot(gameRessourceProvider.getEnemyMissiles(), shotType, shotSpeed);
        }
        barrierShootTimer--;
    }
    
    private void endSnooze()
    {
        if(burrowTimer == DISABLED)
        {
            getSpeedLevel().setLocation(targetSpeedLevel);
        }
        else
        {
            endInterruptedBorrowProcedure();
        }
        
        if(barrierTeleportTimer != DISABLED)
        {
            getCloakingDevice().activate();
            barrierTeleportTimer = CloakingDevice.BOOT_AND_FADE_TIME;
        }
    }
    
    private void endInterruptedBorrowProcedure()
    {
        if(burrowTimer > BORROW_TIME + shootingRate * shotsPerCycle)
        {
            burrowTimer = 2 * BORROW_TIME + shootingRate * shotsPerCycle - burrowTimer;
        }
        else if(burrowTimer > BORROW_TIME)
        {
            burrowTimer = BORROW_TIME;
        }
        getNavigationDevice().flyDown();
        getSpeedLevel().setLocation(SLOW_VERTICAL_SPEED);
    }
    
    private void evaluateBarrierTeleport()
    {
        if(barrierTeleportTimer == CloakingDevice.BOOT_AND_FADE_TIME + shootingRate * shotsPerCycle)
        {
            barrierShootTimer = shootingRate * shotsPerCycle;
            uncloakAndDisableCloakingDevice();
        }
        else if(barrierTeleportTimer == CloakingDevice.BOOT_AND_FADE_TIME)
        {
            barrierShootTimer = DISABLED;
            getCloakingDevice().activate();
            if(getMaxX() > 0){
                Audio.play(Audio.cloak);}
        }
        else if(barrierTeleportTimer == READY && Calculations.tossUp(0.004f))
        {
            startBarrierUncloaking();
        }
        
        if(barrierTeleportTimer != READY)
        {
            barrierTeleportTimer--;
            if(barrierTeleportTimer == READY)
            {
                if(callBack > 0)
                {
                    placeCloakingBarrierAtPausePosition();
                }
                else{isMarkedForRemoval = true;}
            }
        }
    }
    
    private void placeCloakingBarrierAtPausePosition()
    {
        callBack--;
        uncloakAndDisableCloakingDevice();
        barrierTeleportTimer = READY;
        setY(GROUND_Y + 2 * getWidth());
    }
    
    @Override
    protected void writeDestructionStatistics(GameStatisticsCalculator gameStatisticsCalculator)
    {
    }
    
    private boolean isShootingBarrier()
    {
        return this.barrierShootTimer == READY;
    }
    
    private void initializeShootDirectionOfBarriers()
    {
        double randomAngle
            = Math.PI * (1 + Math.random()/2)
                + (this.getCenterY() < GROUND_Y/2f
                    ? Math.PI/2
                    : 0);
        
        this.shootingDirection.setLocation(
            Math.sin(randomAngle),
            Math.cos(randomAngle) );
    }
    
    @Override
    protected boolean isMeetingRequirementsForGlowingEyes()
    {
        return this.snoozeTimer <= SNOOZE_TIME + 75;
    }
    
    @Override
    protected double calculateInitialY()
    {
        return Math.random() * getOnTheGroundY();
    }
    
    @Override
    protected int getWidthVarianceDivisor()
    {
        return WIDTH_VARIANCE_DIVISOR;
    }
    
    @Override
    public boolean isDisappearingAfterEnteringRepairShop()
    {
        return false;
    }
    
    @Override
    protected float getPrimaryColorBrightnessFactor()
    {
        return Events.timeOfDay.getBrightnessFactor();
    }
    
    @Override
    protected float getSecondaryColorBrightnessFactor()
    {
        return SECONDARY_COLOR_BRIGHTNESS_FACTOR;
    }
    
    @Override
    public boolean countsForTotalAmountOfEnemiesSeen()
    {
        return false;
    }
    
    @Override
    public boolean canCollide()
    {
        return getAlpha() == 255 && burrowTimer != 0 && hasUnresolvedIntersection;
    }
    
    @Override
    protected void evaluateBossDestructionEffect(GameRessourceProvider gameRessourceProvider){}
    
    @Override
    public Color getBarColor(boolean isImagePaint)
    {
        return Colorations.barrierColor[Colorations.FRAME][Events.timeOfDay.ordinal()];
    }
    
    @Override
    public Color getInactiveNozzleColor()
    {
        if(Events.timeOfDay == TimeOfDay.NIGHT)
        {
            return Colorations.barrierColor[Colorations.NOZZLE][Events.timeOfDay.ordinal()];
        }
        return super.getInactiveNozzleColor();
    }
    
    @Override
    protected double getTopBoundary()
    {
        return TOP_BOUNDARY;
    }
    
    @Override
    protected double getBottomBoundary()
    {
        return GROUND_Y;
    }
    
    @Override
    protected void uncloakTriggeredByStunningMissile()
    {
        uncloakAndDisableCloakingDevice();
    }
    
    @Override
    public boolean isVisibleNonBarricadeVessel()
    {
        return false;
    }
  
    @Override
    protected void performStoppableActions(GameRessourceProvider gameRessourceProvider)
    {
        updateStoppableTimer();
        super.performStoppableActions(gameRessourceProvider);
    }
    
    private void updateStoppableTimer()
    {
        if(snoozeTimer > 0){snoozeTimer--;}
    }
    
    @Override
    protected void performVerticalTurn()
    {
        super.performVerticalTurn();
        snooze(false);
    }
    
    @Override
    protected void performEmpWaveSurvivorActions()
    {
        snooze(true);
        super.performEmpWaveSurvivorActions();
    }
    
    @Override
    protected void recoverFromBeingStunned()
    {
        snooze(true);
    }
    
    @Override
    public void hitByMissile(GameRessourceProvider gameRessourceProvider, Missile missile)
    {
        super.hitByMissile(gameRessourceProvider, missile);
        if(missile.hasGreatExplosivePower()
            && Calculations.tossUp(	0.5f
            * deactivationProbability
            * (missile.hasGreatExplosivePower() ? 2 : 1)))
        {
            setHitPoints(0);
        }
        else if(isToBeInactivatedBy(missile))
        {
            snooze(true);
        }
    }
    
    private boolean isToBeInactivatedBy(Missile missile)
    {
        return Calculations.tossUp(deactivationProbability
            * missile.typeOfExplosion.getBarrierDeactivationProbabilityFactor());
    }
    
    @Override
    protected void takeCollisionDamage(Helicopter helicopter)
    {
        if(	helicopter.hasTripleDamage()
            &&  Calculations.tossUp(
            deactivationProbability
                *(helicopter.bonusKillsTimer
                > Phoenix.NICE_CATCH_TIME
                - Phoenix.TELEPORT_KILL_TIME ? 2 : 1)))
        {
            setHitPoints(0);
        }
        else if(Calculations.tossUp(deactivationProbability *(helicopter.bonusKillsTimer > Phoenix.NICE_CATCH_TIME - Phoenix.TELEPORT_KILL_TIME ? 4 : 2)))
        {
            snooze(true);
        }
    }
    
    private void snooze(boolean inactivation)
    {
        // TODO Code Leserlichkeit erhöhen, ggf. in Methoden Teile auslagern
        snoozeTimer
            = Math.max(	snoozeTimer,
            SNOOZE_TIME
                + (inactivation
                ? INACTIVATION_TIME
                + Calculations.random((int)(EXTRA_INACTIVE_TIME_FACTOR * INACTIVATION_TIME))
                :0));
        getSpeedLevel().setLocation(ZERO_SPEED);
        if(targetSpeedLevel.getY() != 0 && getMaxY() + 1.5 * getSpeedY() > GROUND_Y)
        {
            setY(GROUND_Y - getHeight());
        }
        if(burrowTimer != DISABLED)
        {
            barrierShootTimer = DISABLED;
        }
        else if(getCloakingDevice().isEnabled())
        {
            barrierTeleportTimer = DISABLED;
            barrierShootTimer = DISABLED;
        }
        else if(barrierShootTimer != DISABLED)
        {
            barrierShootTimer = snoozeTimer - SNOOZE_TIME + shootingCycleLength;
        }
    }
    
    public int getSnoozeTimer()
    {
        return snoozeTimer;
    }
    
    @Override
    protected boolean hasDeadlyGroundContact()
    {
        return false;
    }
}

