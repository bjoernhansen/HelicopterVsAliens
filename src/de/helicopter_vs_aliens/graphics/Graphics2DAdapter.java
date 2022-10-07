package de.helicopter_vs_aliens.graphics;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;


public class Graphics2DAdapter extends AbstractGraphicsAdapter<Graphics2D>
{
    public static GraphicsAdapter of(Graphics graphics){
        return new Graphics2DAdapter((Graphics2D)graphics);
    }
    
    public static GraphicsAdapter of(Image image){
        return Graphics2DAdapter.of(image.getGraphics());
    }
    
    public static GraphicsAdapter withAntialiasing(Image image){
        GraphicsAdapter graphicsAdapter = Graphics2DAdapter.of(image);
        graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);
        return graphicsAdapter;
    }
    
    private Graphics2DAdapter(Graphics2D graphics2D)
    {
        super(graphics2D);
    }
    
    @Override
    public void setColor(Color c)
    {
        graphics.setColor(c);
    }
    
    @Override
    public void draw(Shape s)
    {
        graphics.draw(s);
    }
    
    @Override
    public void fillOval(int x, int y, int width, int height)
    {
        graphics.fillOval(x, y, width, height);
    }
    
    @Override
    public void setStroke(Stroke s)
    {
        graphics.setStroke(s);
    }
    
    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        graphics.drawLine(x1, y1, x2, y2);
    }
    
    @Override
    public void drawPoint(int x, int y)
    {
        graphics.drawLine(x, y, x, y);
    }
    
    @Override
    public void drawPoint(Point point)
    {
        drawPoint(point.x, point.y);
    }
    
    @Override
    public void setPaint(Paint paint)
    {
        graphics.setPaint(paint);
    }
    
    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    @Override
    public void fillRect(int x, int y, int width, int height)
    {
        graphics.fillRect(x, y, width, height);
    }
    
    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        graphics.fillArc(x, y, width, height, startAngle, arcAngle);
    }
    
    @Override
    public void drawImage(Image img, int x, int y)
    {
        graphics.drawImage(img, x, y, null);
    }
    
    @Override
    public void fill(Shape s)
    {
        graphics.fill(s);
    }
    
    @Override
    public void setFont(Font font)
    {
        graphics.setFont(font);
    }
    
    @Override
    public void drawString(String str, int x, int y)
    {
        graphics.drawString(str, x, y);
    }
    
    @Override
    public FontMetrics getFontMetrics()
    {
        return graphics.getFontMetrics();
    }
    
    @Override
    public void drawRect(int x, int y, int width, int height)
    {
        graphics.drawRect(x, y, width, height);
    }
    
    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue)
    {
        graphics.setRenderingHint(hintKey, hintValue);
    }
    
    @Override
    public void setClip(Shape clip)
    {
        graphics.setClip(clip);
    }
    
    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
    {
        graphics.drawImage(img, op, x, y);
    }
    
    @Override
    public void setComposite(Composite comp)
    {
        graphics.setComposite(comp);
    }
    
    @Override
    public void drawOval(int x, int y, int width, int height)
    {
        graphics.drawOval(x, y, width, height);
    }
    
    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    @Override
    public void fillPolygon(Polygon p)
    {
        graphics.fillPolygon(p);
    }
    
    @Override
    public void drawPolygon(Polygon p)
    {
        graphics.drawPolygon(p);
    }
}
