package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;


public class SceneryPainter extends Painter<Scenery>
{
    private Scenery scenery;
    
    @Override
    public void paint(Graphics2D g2d, Scenery scenery)
    {
        setScenery(scenery);
        paintSunOrMoon(g2d);
        if (Events.timeOfDay == NIGHT)
        {
            paintStars(g2d);
        }
        paintCloud(g2d);
        paintAllBackgroundSceneryObjects(g2d);
    }
    
    private void setScenery(Scenery scenery)
    {
        this.scenery = scenery;
    }
    
    private void paintStars(Graphics2D g2d)
    {
        g2d.setColor(Color.white);
        
        // TODO in eine drawPoint-Methode auslagern, sobald es den Graphics2DAdapter gibt
        scenery.getStars()
               .forEach(star -> g2d.drawLine(star.x, star.y, star.x, star.y));
    }
    
    private void paintCloud(Graphics2D g2d)
    {
        g2d.setPaint(Colorations.gradientCloud[Events.timeOfDay.ordinal()]);
        g2d.fillOval((int) scenery.getCloudX(), 51,  82, 45);
        g2d.fillOval((int)(scenery.getCloudX() + 41), 63, 150, 60);
        g2d.fillOval((int)(scenery.getCloudX() + 68), 40,  60, 53);
    }
 
    private void paintSunOrMoon(Graphics2D g2d)
    {
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
    }
    
    private void paintAllBackgroundSceneryObjects(Graphics2D g2d)
    {
        scenery.getSceneryObjects()
               .get(ACTIVE)
               .stream()
               .filter(SceneryObject::isInBackground)
               .forEach(sceneryObject -> sceneryObject.paint(g2d));
    }
}