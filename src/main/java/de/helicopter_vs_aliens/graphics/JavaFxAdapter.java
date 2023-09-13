package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.util.geometry.Polygon;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;


public class JavaFxAdapter extends AbstractGraphicsAdapter<GraphicsContext>
{
    public JavaFxAdapter(GraphicsContext graphics)
    {
        super(graphics);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        graphics.strokeLine(x1, y1, x2, y2);
    }
    
    @Override
    public void drawOval(int x, int y, int width, int height)
    {
        graphics.strokeOval(x, y, width, height);
    }
    
    @Override
    public void fillOval(int x, int y, int width, int height)
    {
        graphics.fillOval(x, y, width, height);
    }
    
    @Override
    public void drawRect(int x, int y, int width, int height)
    {
        graphics.strokeRect(x, y, width, height);
    }
    
    @Override
    public void fillRect(int x, int y, int width, int height)
    {
        graphics.fillRect(x, y, width, height);
    }
    
    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        graphics.strokeRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }
    
    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        graphics.fillArc(x, y, width, height, startAngle, arcAngle, ArcType.OPEN);
    }
    
    @Override
    public void fillPolygon(Polygon polygon)
    {
        graphics.fillPolygon(polygon.getCoordinatesX(), polygon.getCoordinatesY(), polygon.getPointCount());
    }
    
    @Override
    public void drawPolygon(Polygon polygon)
    {
        graphics.strokePolygon(polygon.getCoordinatesX(), polygon.getCoordinatesY(), polygon.getPointCount());
    }

    // TODO evtl. eigene Ractangle Klasse schreiben
    @Override
    public void drawRectangle(Rectangle2D rectangle)
    {
        drawRect(
            (int)rectangle.getX(),
            (int)rectangle.getY(),
            (int)rectangle.getWidth(),
            (int)rectangle.getHeight());
    }
    
    @Override
    public void fillRectangle(Rectangle2D rectangle)
    {
        fillRect(
            (int)rectangle.getX(),
            (int)rectangle.getY(),
            (int)rectangle.getWidth(),
            (int)rectangle.getHeight());
    }

    @Override
    public void drawEllipse(Ellipse2D ellipse)
    {
        drawOval(
            (int)ellipse.getX(),
            (int)ellipse.getY(),
            (int)ellipse.getWidth(),
            (int)ellipse.getHeight());
    }

    @Override
    public void drawString(String text, int x, int y)
    {
        graphics.strokeText(text, x, y);
    }
    
    // TODO evtl. eigene Image-Klasse verwenden
    @Override
    public void drawImage(java.awt.Image image, int x, int y)
    {
        var bufferedImage = (BufferedImage) image;
        // TODO für einen Ansatz entscheiden
        Image imageFx = SwingFXUtils.toFXImage(bufferedImage, null);
        Image imageFx2 = getImage(bufferedImage);
        graphics.drawImage(imageFx, x, y);
    }
    
    private javafx.scene.image.Image getImage(BufferedImage image)
    {
        //converting to a good type, read about types here: https://openjfx.io/javadoc/13/javafx.graphics/javafx/scene/image/PixelBuffer.html
        BufferedImage newImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        newImg.createGraphics()
              .drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        
        //converting the BufferedImage to an IntBuffer
        int[] type_int_agrb = ((DataBufferInt) newImg.getRaster()
                                                     .getDataBuffer()).getData();
        IntBuffer buffer = IntBuffer.wrap(type_int_agrb);
        
        //converting the IntBuffer to an Image, read more about it here: https://openjfx.io/javadoc/13/javafx.graphics/javafx/scene/image/PixelBuffer.html
        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
        PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer<>(newImg.getWidth(), newImg.getHeight(), buffer, pixelFormat);
        return new WritableImage(pixelBuffer);
    }
    
    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
    {
        // TODO nur Zwischenlösung
        drawImage(img, x, y);
    }

    @Override
    public int getStringWidth(String s)
    {
        Font font = graphics.getFont();
        Text text = new Text(s);
        text.setFont(font);
        return (int)text.getBoundsInLocal().getWidth();
    }
    
    @Override
    public void setColor(java.awt.Color color)
    {
        graphics.setFill(convertAwtToFxColor(color));
    }
    
    private javafx.scene.paint.Paint convertAwtToFxColor(Color color)
    {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        double opacity = a / 255.0;
        return javafx.scene.paint.Color.rgb(r, g, b, opacity);
    }
    
    @Override
    public void setPaint(Paint paint)
    {
    
    }
    
    @Override
    public void setStroke(Stroke s)
    {
    
    }
    
    @Override
    public void setFont(java.awt.Font font)
    {
        // graphics.setFont();
    }
    
    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue)
    {
        // TODO vermutlich nicht nötig, da Antialiasing immer an in JavaFX
    }
    
    @Override
    public void setClip(Shape clip)
    {
        // TODO implementieren
    }
    
    @Override
    public void setComposite(Composite comp)
    {
        // probably not necessary
    }
}
