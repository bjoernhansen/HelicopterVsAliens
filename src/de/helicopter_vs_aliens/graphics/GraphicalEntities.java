package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.util.ArrayList;

public final class GraphicalEntities
{
    private GraphicalEntities()
    {
        throw new UnsupportedOperationException();
    }
    
    public static void paintGlowingLine(GraphicsAdapter graphicsAdapter, int x1, int y1, int x2, int y2)
    {
        graphicsAdapter.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphicsAdapter.setColor(Colorations.green);
        graphicsAdapter.drawLine(x1, y1, x2, y2);
        graphicsAdapter.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphicsAdapter.setColor(Color.green);
        graphicsAdapter.drawLine(x1, y1+1, x2, y2);
        graphicsAdapter.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }
    
    public static void paintFrame(GraphicsAdapter graphicsAdapter, Rectangle frame, Color filledColor)
    {
        paintFrame(graphicsAdapter, frame.x, frame.y, frame.width, frame.height, filledColor);
    }
    
    public static void paintFrame(GraphicsAdapter graphicsAdapter, int left, int top, int width, int height)
    {
        paintFrame(graphicsAdapter, left, top, width, height, null);
    }
    
    public static void paintFrame(GraphicsAdapter graphicsAdapter, int left, int top, int width, int height, Color filledColor)
    {
        ArrayList<GradientPaint> gradientPaintList = new ArrayList<>(4);
        
        gradientPaintList.add(new GradientPaint(0, top-1, Color.white, 0, top+4, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(0, top+height-1, Color.white, 0, top+height+4, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(left, 0, Color.white, left+5, 0, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(left+width, 0, Color.white, left+width+5, 0, Colorations.darkestGray, true));
        if(filledColor != null)
        {
            graphicsAdapter.setPaint(filledColor);
            graphicsAdapter.fillRect(left, top, width, height);
        }
        graphicsAdapter.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphicsAdapter.setPaint(gradientPaintList.get(0));
        graphicsAdapter.drawLine(left+1, top, left+width-2, top);
        graphicsAdapter.setPaint(gradientPaintList.get(1));
        graphicsAdapter.drawLine(left+1, top+height, left+width-2, top+height);
        graphicsAdapter.setPaint(gradientPaintList.get(2));
        graphicsAdapter.drawLine(left, top+1, left, top+height-2);
        graphicsAdapter.setPaint(gradientPaintList.get(3));
        graphicsAdapter.drawLine(left+width, top+1, left+width, top+height-2);
        graphicsAdapter.setStroke(new BasicStroke(1));
    }
    
    public static void paintFrameLine(GraphicsAdapter graphicsAdapter, int left, int top, int width)
    {
        GradientPaint frameLineGradientPaint = new GradientPaint(0, top-1, Color.white, 0, top+4,
            Colorations.darkestGray,
            true);
        graphicsAdapter.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphicsAdapter.setPaint(frameLineGradientPaint);
        graphicsAdapter.drawLine(left+1, top, left+width-2, top);
        graphicsAdapter.setStroke(new BasicStroke(1));
    }
}
