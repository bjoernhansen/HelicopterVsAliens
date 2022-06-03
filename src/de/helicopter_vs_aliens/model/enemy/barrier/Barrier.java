package de.helicopter_vs_aliens.model.enemy.barrier;

import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyModelType;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;


abstract class Barrier extends Enemy
{
    private static final int
        WIDTH_VARIANCE_DIVISOR = 5,
        TOP_BOUNDARY = 0;
    
    private static final float
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 0.75f;
    
    @Override
    protected void doTypeSpecificInitialization()
    {
        rotorColor = 1;
        isClockwiseBarrier = Calculations.tossUp();
        deactivationProbability = 1.0f / type.getStrength();
    }
    
    @Override
    protected void finalizeInitialization(GameRessourceProvider gameRessourceProvider)
    {
        EnemyController.barrierTimer = (int)((gameRessourceProvider.getHelicopter().getWidth() + getWidth())/2);
 
        super.finalizeInitialization(gameRessourceProvider);
        
        if(isShootingBarrier())
        {
            this.initializeShootDirectionOfBarriers();
        }
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
        return alpha == 255 && burrowTimer != 0 && hasUnresolvedIntersection;
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
}

