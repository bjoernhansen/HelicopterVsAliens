package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.scenery.BackgroundObject;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;
import java.awt.image.BufferedImage;

import static de.helicopter_vs_aliens.model.scenery.BackgroundType.*;

//TODO hier auch mit Subklassen arbeiten, die von BackgroundObject erben
public class BackgroundObjectPainter extends Painter<BackgroundObject>
{
    private static final BufferedImage[]
        CACTUS_IMG = paintCactusImage(),
        PALM_CROWN_IMG = paintPalmCrownImage();
    
    @Override
    void paint(Graphics2D g2d, BackgroundObject backgroundObject)
    {
        // Kaktus
        if(backgroundObject.type == CACTUS)
        {
            g2d.drawImage(CACTUS_IMG[Events.timeOfDay.ordinal()], (int) backgroundObject.getX(), 235, null);
        }
    
        // Steine
        if(backgroundObject.type == STONE)
        {
            g2d.setPaint(Colorations.gradientStones[Events.timeOfDay.ordinal()]);
            g2d.fillOval( (int)(backgroundObject.getX() + backgroundObject.getCoordinateOfComponent(0,0)),
                    400 + backgroundObject.getCoordinateOfComponent(0,1),
                    backgroundObject.getCoordinateOfComponent(0,2),
                    backgroundObject.getCoordinateOfComponent(0,3));
        }
    
        // Berge im Hintergrund in Bodenfarbe
        if(backgroundObject.type == HILL)
        {
            g2d.setPaint(Colorations.gradientHills[Events.timeOfDay.ordinal()]);
            g2d.fillArc( (int)(backgroundObject.getX() + backgroundObject.getCoordinateOfComponent(0,0)),
                    400 + backgroundObject.getCoordinateOfComponent(0,1),
                    backgroundObject.getCoordinateOfComponent(0,2),
                    backgroundObject.getCoordinateOfComponent(0,3), 0, 180);
            g2d.fillArc( (int)(backgroundObject.getX() + backgroundObject.getCoordinateOfComponent(1,0)),
                    400 + backgroundObject.getCoordinateOfComponent(1,1),
                    backgroundObject.getCoordinateOfComponent(1,2),
                    backgroundObject.getCoordinateOfComponent(1,3), 0, 180);
        }
    
        // Sand
        if(backgroundObject.type == DESERT)
        {
            g2d.drawImage(backgroundObject.getImage(Events.timeOfDay.ordinal()), (int)backgroundObject.getX(), 426, null);
        }
    
        // Palme
        if(backgroundObject.type == PALM)
        {
            g2d.drawImage( backgroundObject.getImage(Events.timeOfDay.ordinal()), (int)(backgroundObject.getX() + 110),
                    350 - backgroundObject.getCoordinateOfComponent(0,0), null);
            g2d.drawImage( PALM_CROWN_IMG[Events.timeOfDay.ordinal()], (int)backgroundObject.getX(),
                    340 - backgroundObject.getCoordinateOfComponent(0,0), null);
        }
    }
    
    public void paintPalmStemImage(BackgroundObject bgo)
    {
        // TODO image auf EnumMap TimeOfDay umstellen
        for(int i = 0; i < 2; i++)
        {
            bgo.setImage(i, new BufferedImage(20, 80 + bgo.getCoordinateOfComponent(0,0) + 6, BufferedImage.TYPE_INT_ARGB));
            Graphics2D g2dPalmStem = (Graphics2D)bgo.getImage(i).getGraphics();
            g2dPalmStem.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2dPalmStem.setPaint( new GradientPaint(	12, 0,  bgo.getColor(i),
                    23, 0, 	Colorations.dimColor(bgo.getColor(i), 0.75f), true));
            g2dPalmStem.fillRect(0, 0, 20, 80 + bgo.getCoordinateOfComponent(0,0));
            g2dPalmStem.fillArc( 0, 73 + bgo.getCoordinateOfComponent(0,0), 20, 12, 180, 180);
        }
    }
    
    public void paintDesertImage(BackgroundObject bgo)
    {
        for(int i = 0; i < 2; i++)
        {
            bgo.setImage(i, new BufferedImage(bgo.getWidth(), 35, BufferedImage.TYPE_INT_ARGB));
            Graphics2D g2dDesert = (Graphics2D) bgo.getImage(i).getGraphics();
            g2dDesert.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2dDesert.setPaint(new GradientPaint(	 0 + bgo.getX(),  0, Colorations.sand[i],
                    10 + bgo.getX(), 20, Colorations.dimColor(Colorations.sand[i], 0.9f), true));
            g2dDesert.fillArc(0, -35, 300, 70, 180, 90);
            g2dDesert.fillRect(149, 0, bgo.getWidth() - 298, 35);
            g2dDesert.fillArc(bgo.getWidth() - 300, -35, 300, 70, 270, 90);
        }
    }
    
    private static BufferedImage [] paintCactusImage()
    {
        Graphics2D g2d;
        GradientPaint[] myGradientColor = new GradientPaint[3];
        // TODO umstellen auf EnumMap für TimeOfDay
        BufferedImage[] cactusImage = new BufferedImage[2];
        for(int i = 0; i < 2; i++)
        {
            cactusImage[i] = new BufferedImage(52, 197, BufferedImage.TYPE_INT_ARGB);
            g2d =  (Graphics2D) cactusImage[i].getGraphics();
            g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            myGradientColor[0] = new GradientPaint(	 2,  0, Colorations.myGreen[0][i],
                    15,  0, Colorations.dimColor(Colorations.myGreen[0][i], 0.80f), true);
            myGradientColor[1] = new GradientPaint(	 8,  0, Colorations.myGreen[1][i],
                    17,  0, Colorations.dimColor(Colorations.myGreen[1][i], 0.80f), true);
            myGradientColor[2] = new GradientPaint(	 9,  0, Colorations.myGreen[2][i],
                    17,  0, Colorations.dimColor(Colorations.myGreen[2][i], 0.85f), true);
            g2d.setPaint(myGradientColor[0]);
            g2d.fillOval( 11, 62, 30, 135);
            g2d.setPaint(myGradientColor[1]);
            g2d.fillOval( 33,  0, 19, 124);
            g2d.setPaint(myGradientColor[2]);
            g2d.fillOval(  0, 34, 17, 94);
        }
        return cactusImage;
    }
    
    private static BufferedImage [] paintPalmCrownImage()
    {
        Graphics2D g2DPalmCrown;
        // TODO umstellen auf EnumMap für TimeOfDay
        BufferedImage[] palmCrownImage = new BufferedImage[2];
        for(int i = 0; i < 2; i++)
        {
            palmCrownImage[i] = 	new BufferedImage(209, 27, BufferedImage.TYPE_INT_ARGB);
            g2DPalmCrown = (Graphics2D) palmCrownImage[i].getGraphics();
            g2DPalmCrown.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2DPalmCrown.setPaint(new GradientPaint(	0,  4, Colorations.myGreen[0][i],
                    0, 17, Colorations.dimColor(Colorations.myGreen[0][i],
                    0.65f), true));
            g2DPalmCrown.fillArc(115, 1, 94, 26, 0, 225);
            g2DPalmCrown.setPaint(new GradientPaint(	0,  3, Colorations.myGreen[1][i],
                    0, 13, Colorations.dimColor(Colorations.myGreen[1][i],
                    0.60f), true));
            g2DPalmCrown.fillArc(0, 0 , 125, 21, -45, 225);
            g2DPalmCrown.setPaint(new GradientPaint(	0, 17, Colorations.myGreen[2][i],
                    0, 23, Colorations.dimColor(Colorations.myGreen[2][i],
                    0.65f), true));
            g2DPalmCrown.fillArc(55, 14, 68, 11, -45, 240);
        }
        return palmCrownImage;
    }
}