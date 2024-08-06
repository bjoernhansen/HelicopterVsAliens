package de.helicopter_vs_aliens.model.enemy.maneuver;

public class IntermittentFlightManeuver extends AbstractManeuver<IntermittentFlyable>
{
    private boolean isInSpeedUpPhase = true;
    
    IntermittentFlightManeuver(IntermittentFlyable intermittentFlyable)
    {
        super(intermittentFlyable);
    }
    
    @Override
    public void perform()
    {
        if(isInSpeedUpPhase)
        {
            getTarget().increaseSpeedLevelX(0.5);
        }
        else
        {
            getTarget().increaseSpeedLevelX(-0.5);
        }
        if(getTarget().getSpeedLevel().getX() <= 0){isInSpeedUpPhase = true;}
        if(getTarget().isTargetSpeedReachedOrExceededX()){isInSpeedUpPhase = false;}
    }
    
    @Override
    public void reset()
    {
        super.reset();
        isInSpeedUpPhase = true;
    }
}
