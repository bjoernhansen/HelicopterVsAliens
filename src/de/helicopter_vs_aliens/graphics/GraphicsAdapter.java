package de.helicopter_vs_aliens.graphics;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
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

public interface GraphicsAdapter
{
    void setColor(Color c);
    void draw(Shape s);
    void fillOval(int x, int y, int width, int height);
    void setStroke(Stroke s);
    void drawLine(int x1, int y1, int x2, int y2);
    void drawPoint(int x, int y);
    void drawPoint(Point point);
    void setPaint( Paint paint );
    void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);
    void fillRect(int x, int y, int width, int height);
    void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);
    void drawImage(Image img, int x, int y);
    void fill(Shape s);
    void setFont(Font font);
    void drawString(String str, int x, int y);
    FontMetrics getFontMetrics();
    void drawRect(int x, int y, int width, int height);
    void setRenderingHint(RenderingHints.Key hintKey, Object hintValue);
    void setClip(Shape clip);
    void drawImage(BufferedImage img, BufferedImageOp op, int x, int y);
    void setComposite(Composite comp);
    void drawOval(int x, int y, int width, int height);
    void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);
    void fillPolygon(Polygon p);
    void drawPolygon(Polygon p);
}
