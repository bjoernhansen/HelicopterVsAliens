package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.util.geometry.Dimension;
import de.helicopter_vs_aliens.util.geometry.Polygon;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;


public interface GraphicsAdapter
{
    Dimension
        VIRTUAL_DIMENSION = Dimension.newInstance(1024, 461);
    
    
    default void drawPoint(int x, int y)
    {
        drawLine(x, y, x, y);
    }
    
    default void drawPoint(Point point)
    {
        drawPoint(point.x, point.y);
    }
    
    void drawLine(int x1, int y1, int x2, int y2);
    
    void drawOval(int x, int y, int width, int height);
    
    void fillOval(int x, int y, int width, int height);
    
    void drawRect(int x, int y, int width, int height);
    
    void fillRect(int x, int y, int width, int height);
    
    void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);
    
    void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);
    
    void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);
    
    void fillPolygon(Polygon p);
    
    void drawPolygon(Polygon p);
    
    // TODO einen eigenen Shape-Typ einführen oder separate Methoden für die verschiedenen Shapes
    void drawRectangle(Rectangle2D rectangle);
    
    void fillRectangle(Rectangle2D rectangle);

    void drawEllipse(Ellipse2D ellipse);
    
    default void drawWholeScreenHorizontallyCenteredString(String str, int y)
    {
        drawHorizontallyCenteredString(str, 0, VIRTUAL_DIMENSION.getWidth(), y);
    }
    
    default void drawHorizontallyCenteredString(String str, int left, int width, int y)
    {
        int x = left + (width - getStringWidth(str)) / 2;
        drawString(str, x, y);
    }
    
    void drawString(String str, int x, int y);
    
    void drawImage(Image img, int x, int y);
    
    void drawImage(BufferedImage img, BufferedImageOp op, int x, int y);

    void drawImage(java.awt.Image image, Dimension displayShift, Dimension scaledDimension);
    
    int getStringWidth(String text);
    
    // TODO einen eigenen Color-Typ einführen, vgl. Water Morris Maze
    void setColor(Color c);
    
    void setPaint(Paint paint);
    
    void setStroke(Stroke s);
    
    void setFont(Font font);
    
    void setRenderingHint(RenderingHints.Key hintKey, Object hintValue);
    
    void setComposite(Composite comp);
}
