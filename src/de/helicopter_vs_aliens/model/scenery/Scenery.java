package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static de.helicopter_vs_aliens.model.RectangularGameEntity.GROUND_Y;

// TODO es sollte nicht alles static sein, sondern über eine Scenery Instanz laufen
public class Scenery extends GameEntity
{
    private static final int
        NR_OF_STARS = 60;
    
    // Sternkoordinaten
    private static List<Point>
        stars = new ArrayList<>(NR_OF_STARS);
    
    static{
        initializeStars();
    }
    
    private static void initializeStars()
    {
        List<Point> calculatedStars = new ArrayList<>(NR_OF_STARS);
        for(int i = 0; i < NR_OF_STARS; i++)
        {
            Point star = new Point( Calculations.random(Main.VIRTUAL_DIMENSION.width),
                                    Calculations.random(GROUND_Y));
            calculatedStars.add(star);
        }
        stars = List.copyOf(calculatedStars);
    }
    
    private static float
        cloudX = 135;                // x-Koordinate der Wolke
        
    public static void reset()
    {
        cloudX = 135;
    }
    
    public static void update()
    {
        moveCloud();
    }
    
    private static void moveCloud()
    {
        cloudX -= BackgroundObject.backgroundMoves ? 0.5f : 0.125f;
        if (cloudX < -250)
        {
            cloudX = 1000;
        }
    }
    
    public static List<Point> getStars()
    {
        return stars;
    }
    
    public static float getCloudX()
    {
        return cloudX;
    }
}
