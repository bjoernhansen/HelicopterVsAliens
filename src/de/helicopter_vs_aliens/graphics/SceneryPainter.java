package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.scenery.BackgroundObject;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.RectangularGameEntity.GROUND_Y;


public class SceneryPainter extends Painter<Scenery>
{
    public static void paintBackground(Graphics2D g2d, Scenery scenery, EnumMap<CollectionSubgroupType, LinkedList<BackgroundObject>> backgroundObjects)
    {
        scenery.paint(g2d);
        paintBgObjects(g2d, backgroundObjects);
    }
    
    @Override
    void paint(Graphics2D g2d, Scenery scenery)
    {
        // Sonne bzw. Mond
        int coronaRadiusIncrease = 0;
        if(Events.timeOfDay == NIGHT) {g2d.setColor(Colorations.lighterYellow); }
        else
        {
            g2d.setColor(Colorations.randomLight);
            coronaRadiusIncrease = (Colorations.randomSunlightBlue - 175)/20;
        }
        g2d.fillOval(865, 30, 60, 60);
        g2d.setColor(Colorations.translucentSun);
        g2d.setStroke(new BasicStroke(35));
        g2d.drawOval(855-coronaRadiusIncrease, 20-coronaRadiusIncrease,
            80+2*coronaRadiusIncrease, 80+2*coronaRadiusIncrease);
        g2d.setStroke(new BasicStroke(1));
        
        // Sterne
        if(Events.timeOfDay == NIGHT)
        {
            g2d.setColor(Color.white);
            
            // TODO in eine drawPoint-Methode auslagern, sobald es den Graphics2DAdapter gibt
            scenery.getStars()
                   .forEach(star -> g2d.drawLine(star.x, star.y, star.x, star.y));
        }
        
        // Wolke
        g2d.setPaint(Colorations.gradientCloud[Events.timeOfDay.ordinal()]);
        g2d.fillOval((int) scenery.getCloudX(), 51,  82, 45);
        g2d.fillOval((int)(scenery.getCloudX() + 41), 63, 150, 60);
        g2d.fillOval((int)(scenery.getCloudX() + 68), 40,  60, 53);
    }
    
    private static void paintBgObjects(Graphics2D g2d, EnumMap<CollectionSubgroupType, LinkedList<BackgroundObject>> backgroundObjects)
    {
        for (BackgroundObject bgo : backgroundObjects.get(ACTIVE))
        {
            if (bgo.getPlane() == -1)
            {
                bgo.paint(g2d);
            }
        }
    }
    
    public static void paintForeground(Graphics2D g2d, EnumMap<CollectionSubgroupType, LinkedList<BackgroundObject>> backgroundObjects)
    {
        // der Boden
        g2d.setPaint(Colorations.gradientGround[Events.timeOfDay.ordinal()]);
        g2d.fillRect(0, GROUND_Y, Main.VIRTUAL_DIMENSION.width, 35);
        
        // Objekte vor dem Helikopter
        for(int j = 0; j < 3; j++)
        {
            for (BackgroundObject bgo : backgroundObjects.get(ACTIVE))
            {
                if (bgo.getPlane() == j)
                {
                    bgo.paint(g2d);
                }
            }
        }
    }
}