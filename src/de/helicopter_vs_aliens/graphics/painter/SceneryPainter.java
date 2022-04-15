package de.helicopter_vs_aliens.graphics.painter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;


public class SceneryPainter extends Painter<Scenery>
{
    private Scenery scenery;
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Scenery scenery)
    {
        setScenery(scenery);
        paintSunOrMoon(graphicsAdapter);
        if (Events.timeOfDay == NIGHT)
        {
            paintStars(graphicsAdapter);
        }
        paintCloud(graphicsAdapter);
        paintAllBackgroundSceneryObjects(graphicsAdapter);
    }
    
    private void setScenery(Scenery scenery)
    {
        this.scenery = scenery;
    }
    
    private void paintStars(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Color.white);
        scenery.getStars()
               .forEach(graphicsAdapter::drawPoint);
    }
    
    private void paintCloud(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setPaint(Colorations.gradientCloud[Events.timeOfDay.ordinal()]);
        graphicsAdapter.fillOval((int) scenery.getCloudX(), 51,  82, 45);
        graphicsAdapter.fillOval((int)(scenery.getCloudX() + 41), 63, 150, 60);
        graphicsAdapter.fillOval((int)(scenery.getCloudX() + 68), 40,  60, 53);
    }
 
    private void paintSunOrMoon(GraphicsAdapter graphicsAdapter)
    {
        int coronaRadiusIncrease = 0;
        if(Events.timeOfDay == NIGHT) {graphicsAdapter.setColor(Colorations.lighterYellow); }
        else
        {
            graphicsAdapter.setColor(Colorations.randomLight);
            coronaRadiusIncrease = (Colorations.randomSunlightBlue - 175)/20;
        }
        graphicsAdapter.fillOval(865, 30, 60, 60);
        graphicsAdapter.setColor(Colorations.translucentSun);
        graphicsAdapter.setStroke(new BasicStroke(35));
        graphicsAdapter.drawOval(855-coronaRadiusIncrease, 20-coronaRadiusIncrease,
            80+2*coronaRadiusIncrease, 80+2*coronaRadiusIncrease);
        graphicsAdapter.setStroke(new BasicStroke(1));
    }
    
    private void paintAllBackgroundSceneryObjects(GraphicsAdapter graphicsAdapter)
    {
        scenery.getSceneryObjects()
               .get(ACTIVE)
               .stream()
               .filter(SceneryObject::isInBackground)
               .forEach(sceneryObject -> sceneryObject.paint(graphicsAdapter));
    }
}