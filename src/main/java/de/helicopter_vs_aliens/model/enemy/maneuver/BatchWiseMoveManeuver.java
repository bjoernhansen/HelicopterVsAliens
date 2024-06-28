package de.helicopter_vs_aliens.model.enemy.maneuver;

public class BatchWiseMoveManeuver extends AbstractManeuver
{
    private boolean isInSpeedUpPhase = true;
    
    BatchWiseMoveManeuver(Maneuverable maneuverable)
    {
        super(maneuverable);
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
}
