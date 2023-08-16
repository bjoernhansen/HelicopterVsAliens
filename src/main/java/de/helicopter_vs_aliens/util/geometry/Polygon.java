package de.helicopter_vs_aliens.util.geometry;


public class Polygon
{
    private final int
        pointCount;
    
    private final java.awt.Polygon
        awtPolygon;
    
    private final double[]
        pointsX;
    
    private final double[]
        pointsY;
    
    
    public Polygon(Point... points)
    {
        pointCount = points.length;
        
        int[] coordinatesX = new int[points.length];
        int[] coordinatesY = new int[points.length];
        
        pointsX = new double[points.length];
        pointsY = new double[points.length];
        
        for(int i = 0; i < points.length; i++)
        {
            pointsX[i] = coordinatesX[i] = points[i].getX();
            pointsY[i] = coordinatesY[i] = points[i].getY();
        }
        
        awtPolygon = new java.awt.Polygon(coordinatesX, coordinatesY, points.length);
    }
    
    public java.awt.Polygon asAwtPolygon()
    {
        return awtPolygon;
    }
    
    public double[] getCoordinatesX()
    {
        return pointsX;
    }
    
    public double[] getCoordinatesY()
    {
        return pointsY;
    }
    
    public int getPointCount()
    {
        return pointCount;
    }
    
    public boolean contains(java.awt.Point point)
    {
        return awtPolygon.contains(point);
    }
}
