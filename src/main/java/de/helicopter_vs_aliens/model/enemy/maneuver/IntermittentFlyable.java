package de.helicopter_vs_aliens.model.enemy.maneuver;

import java.awt.geom.Point2D;


public interface IntermittentFlyable
{
    boolean isTargetSpeedReachedOrExceededX();
    
    Point2D getSpeedLevel();
    
    void increaseSpeedLevelX(double increment);
}
