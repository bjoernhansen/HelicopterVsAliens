package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.CACTUS;
import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.DESERT;
import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.HILL;
import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.PALM;
import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.STONE;

//TODO hier auch mit Subklassen arbeiten, die von SceneryObject erben
public class SceneryObjectPainter extends Painter<SceneryObject>
{
    private static final BufferedImage[]
        CACTUS_IMG = paintCactusImage(),
        PALM_CROWN_IMG = paintPalmCrownImage();
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, SceneryObject sceneryObject)
    {
        // Kaktus
        if(sceneryObject.type == CACTUS)
        {
            graphicsAdapter.drawImage(CACTUS_IMG[Events.timeOfDay.ordinal()], (int) sceneryObject.getSceneryObjectX(), 235, null);
        }
    
        // Steine
        if(sceneryObject.type == STONE)
        {
            graphicsAdapter.setPaint(Colorations.gradientStones[Events.timeOfDay.ordinal()]);
            graphicsAdapter.fillOval( (int)(sceneryObject.getSceneryObjectX() + sceneryObject.getCoordinateOfComponent(0,0)),
                    400 + sceneryObject.getCoordinateOfComponent(0,1),
                    sceneryObject.getCoordinateOfComponent(0,2),
                    sceneryObject.getCoordinateOfComponent(0,3));
        }
    
        // Berge im Hintergrund in Bodenfarbe
        if(sceneryObject.type == HILL)
        {
            graphicsAdapter.setPaint(Colorations.gradientHills[Events.timeOfDay.ordinal()]);
            graphicsAdapter.fillArc( (int)(sceneryObject.getSceneryObjectX() + sceneryObject.getCoordinateOfComponent(0,0)),
                    400 + sceneryObject.getCoordinateOfComponent(0,1),
                    sceneryObject.getCoordinateOfComponent(0,2),
                    sceneryObject.getCoordinateOfComponent(0,3), 0, 180);
            graphicsAdapter.fillArc( (int)(sceneryObject.getSceneryObjectX() + sceneryObject.getCoordinateOfComponent(1,0)),
                    400 + sceneryObject.getCoordinateOfComponent(1,1),
                    sceneryObject.getCoordinateOfComponent(1,2),
                    sceneryObject.getCoordinateOfComponent(1,3), 0, 180);
        }
    
        // Sand
        if(sceneryObject.type == DESERT)
        {
            graphicsAdapter.drawImage(sceneryObject.getImage(Events.timeOfDay.ordinal()), (int) sceneryObject.getSceneryObjectX(), 426, null);
        }
    
        // Palme
        if(sceneryObject.type == PALM)
        {
            graphicsAdapter.drawImage( sceneryObject.getImage(Events.timeOfDay.ordinal()), (int)(sceneryObject.getSceneryObjectX() + 110),
                    350 - sceneryObject.getCoordinateOfComponent(0,0), null);
            graphicsAdapter.drawImage( PALM_CROWN_IMG[Events.timeOfDay.ordinal()], (int) sceneryObject.getSceneryObjectX(),
                    340 - sceneryObject.getCoordinateOfComponent(0,0), null);
        }
    }
    
    public void paintPalmStemImage(SceneryObject sceneryObject)
    {
        // TODO image auf EnumMap TimeOfDay umstellen
        for(int i = 0; i < 2; i++)
        {
            sceneryObject.setImage(i, new BufferedImage(20, 80 + sceneryObject.getCoordinateOfComponent(0,0) + 6, BufferedImage.TYPE_INT_ARGB));
            GraphicsAdapter graphicsAdapter = Graphics2DAdapter.of(sceneryObject.getImage(i));
            graphicsAdapter.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON);
            
            graphicsAdapter.setPaint( new GradientPaint(	12, 0,  sceneryObject.getColor(i),
                    23, 0, 	Colorations.dimColor(sceneryObject.getColor(i), 0.75f), true));
            graphicsAdapter.fillRect(0, 0, 20, 80 + sceneryObject.getCoordinateOfComponent(0,0));
            graphicsAdapter.fillArc( 0, 73 + sceneryObject.getCoordinateOfComponent(0,0), 20, 12, 180, 180);
        }
    }
    
    public void paintDesertImage(SceneryObject sceneryObject)
    {
        // TODO ist vermutlich loop für Tageszeiten hier entsprechend Enum-Datentypen verwenden
        for(int i = 0; i < 2; i++)
        {
            sceneryObject.setImage(i, new BufferedImage(sceneryObject.getSceneryObjectWidth(), 35, BufferedImage.TYPE_INT_ARGB));
            GraphicsAdapter graphicsAdapter = Graphics2DAdapter.of(sceneryObject.getImage(i));
            graphicsAdapter.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphicsAdapter.setPaint(new GradientPaint(	 0 + sceneryObject.getSceneryObjectX(),  0, Colorations.sand[i],
                    10 + sceneryObject.getSceneryObjectX(), 20, Colorations.dimColor(Colorations.sand[i], 0.9f), true));
            graphicsAdapter.fillArc(0, -35, 300, 70, 180, 90);
            graphicsAdapter.fillRect(149, 0, sceneryObject.getSceneryObjectWidth() - 298, 35);
            graphicsAdapter.fillArc(sceneryObject.getSceneryObjectWidth() - 300, -35, 300, 70, 270, 90);
        }
    }
    
    private static BufferedImage [] paintCactusImage()
    {
        GradientPaint[] myGradientColor = new GradientPaint[3];
        // TODO umstellen auf EnumMap für TimeOfDay
        BufferedImage[] cactusImage = new BufferedImage[2];
        for(int i = 0; i < 2; i++)
        {
            cactusImage[i] = new BufferedImage(52, 197, BufferedImage.TYPE_INT_ARGB);
            GraphicsAdapter graphicsAdapter =  Graphics2DAdapter.of(cactusImage[i]);
            graphicsAdapter.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            myGradientColor[0] = new GradientPaint(	 2,  0, Colorations.myGreen[0][i],
                    15,  0, Colorations.dimColor(Colorations.myGreen[0][i], 0.80f), true);
            myGradientColor[1] = new GradientPaint(	 8,  0, Colorations.myGreen[1][i],
                    17,  0, Colorations.dimColor(Colorations.myGreen[1][i], 0.80f), true);
            myGradientColor[2] = new GradientPaint(	 9,  0, Colorations.myGreen[2][i],
                    17,  0, Colorations.dimColor(Colorations.myGreen[2][i], 0.85f), true);
            graphicsAdapter.setPaint(myGradientColor[0]);
            graphicsAdapter.fillOval( 11, 62, 30, 135);
            graphicsAdapter.setPaint(myGradientColor[1]);
            graphicsAdapter.fillOval( 33,  0, 19, 124);
            graphicsAdapter.setPaint(myGradientColor[2]);
            graphicsAdapter.fillOval(  0, 34, 17, 94);
        }
        return cactusImage;
    }
    
    private static BufferedImage [] paintPalmCrownImage()
    {
        GraphicsAdapter graphicsAdapter;
        // TODO umstellen auf EnumMap für TimeOfDay
        BufferedImage[] palmCrownImage = new BufferedImage[2];
        for(int i = 0; i < 2; i++)
        {
            palmCrownImage[i] = 	new BufferedImage(209, 27, BufferedImage.TYPE_INT_ARGB);
            graphicsAdapter = Graphics2DAdapter.of(palmCrownImage[i]);
            graphicsAdapter.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON);
            graphicsAdapter.setPaint(new GradientPaint(	0,  4, Colorations.myGreen[0][i],
                                    0, 17, Colorations.dimColor(Colorations.myGreen[0][i],
                                    0.65f), true));
            graphicsAdapter.fillArc(115, 1, 94, 26, 0, 225);
            graphicsAdapter.setPaint(new GradientPaint(	0,  3, Colorations.myGreen[1][i],
                    0, 13, Colorations.dimColor(Colorations.myGreen[1][i],
                    0.60f), true));
            graphicsAdapter.fillArc(0, 0 , 125, 21, -45, 225);
            graphicsAdapter.setPaint(new GradientPaint(	0, 17, Colorations.myGreen[2][i],
                    0, 23, Colorations.dimColor(Colorations.myGreen[2][i],
                    0.65f), true));
            graphicsAdapter.fillArc(55, 14, 68, 11, -45, 240);
        }
        return palmCrownImage;
    }
}
