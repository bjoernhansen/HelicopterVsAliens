package de.helicopter_vs_aliens.model.enemy;

import java.awt.geom.Point2D;

final class TargetSpeedLevelProvider
{
    private static final Point2D
        NO_VARIANCE_IN_SPEED = new Point2D.Double(0,0);
    
    private static final TargetSpeedLevelProvider
        ZERO_SPEED_PROVIDER = TargetSpeedLevelProvider.ofFixedSpeed(0,0);
    
    private final Point2D
        minimumSpeed,
        varianceInSpeed;
    
    
    public static TargetSpeedLevelProvider ofVariableSpeed(double minimumSpeedX, double minimumSpeedY, double varianceInSpeedX, double varianceInSpeedY)
    {
        Point2D minimumSpeed = new Point2D.Double(minimumSpeedX, minimumSpeedY);
        Point2D varianceInSpeed = new Point2D.Double(varianceInSpeedX, varianceInSpeedY);
        return new TargetSpeedLevelProvider(minimumSpeed, varianceInSpeed);
    }
    
    public static TargetSpeedLevelProvider ofFixedSpeed(double minimumSpeedX, double minimumSpeedY)
    {
        Point2D minimumSpeed = new Point2D.Double(minimumSpeedX, minimumSpeedY);
        return new TargetSpeedLevelProvider(minimumSpeed, NO_VARIANCE_IN_SPEED);
    }
    
    public static TargetSpeedLevelProvider ofZeroSpeed()
    {
        return ZERO_SPEED_PROVIDER;
    }
    
    private TargetSpeedLevelProvider(Point2D minimumSpeed, Point2D varianceInSpeed)
    {
        this.minimumSpeed = minimumSpeed;
        this.varianceInSpeed = varianceInSpeed;
    }
    
    Point2D selectTargetSpeedLevel()
    {
        double x = minimumSpeed.getX() + Math.random() * varianceInSpeed.getX();
        double y = minimumSpeed.getY() + Math.random() * varianceInSpeed.getY();
        return new Point2D.Double(x,y);
    }
}
