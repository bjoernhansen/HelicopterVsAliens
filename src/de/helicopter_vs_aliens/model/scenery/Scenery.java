package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.control.entities.GameEntityActivation;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.model.RectangularGameEntity.GROUND_Y;

// TODO es sollte nicht alles static sein, sondern über eine Scenery Instanz laufen
public class Scenery extends GameEntity
{
    private static final int
        NR_OF_STARS = 60,
        MAXIMUM_NUMBER_OF_SCENERY_OBJECTS = 20,
        ACTIVATION_PAUSE_DURATION = 20,
        X_LIMIT_FOR_REMOVAL = -50;
    
    // TODO sollte eigentlich eine Instanz-Variable sein
    public static boolean
        backgroundMoves; // = true: bewegter Hintergrund
    
    private float
        cloudX = 135; // x-Koordinate der Wolke
    
    private List<Point>
        stars = new ArrayList<>(NR_OF_STARS);
        
    private final Map<CollectionSubgroupType, LinkedList<SceneryObject>>
        sceneryObjects = new EnumMap<>(CollectionSubgroupType.class);
    
    
    public Scenery()
    {
        initializeStars();
    }
    
    private void initializeStars()
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
    
    public void reset()
    {
        sceneryObjects.get(INACTIVE).addAll(sceneryObjects.get(ACTIVE));
        sceneryObjects.get(ACTIVE).clear();
        createInitialSceneryObjects();
        cloudX = 135;
    }
    
    public void createInitialSceneryObjects()
    {
        Iterator<SceneryObject> i = sceneryObjects.get(INACTIVE).iterator();
        
        SceneryObject firstCactus;
        if(i.hasNext()){firstCactus = i.next(); i.remove();}
        else{firstCactus = new SceneryObject();}
        firstCactus.makeFirstCactus();
        sceneryObjects.get(ACTIVE).add(firstCactus);
    
        SceneryObject firstHill;
        if(i.hasNext()){firstHill = i.next(); i.remove();}
        else{firstHill = new SceneryObject();}
        firstHill.makeFirstHill();
        sceneryObjects.get(ACTIVE).add(firstHill);
    
        SceneryObject firstDesert;
        if(i.hasNext()){firstDesert = i.next(); i.remove();}
        else{firstDesert = new SceneryObject();}
        firstDesert.makeFirstDesert();
        sceneryObjects.get(ACTIVE).add(firstDesert);
    }
    
    public void update(GameRessourceProvider gameRessourceProvider)
    {
        backgroundMoves = isBackgroundMoving(gameRessourceProvider);
        for(Iterator<SceneryObject> i = sceneryObjects.get(ACTIVE).iterator(); i.hasNext();)
        {
            SceneryObject sceneryObject = i.next();
            if (backgroundMoves)
            {
                sceneryObject.move();
            }
            if(sceneryObject.getSceneryObjectMaxX() < X_LIMIT_FOR_REMOVAL)
            {
                sceneryObject.clearImage();
                i.remove();
                sceneryObjects.get(INACTIVE).add(sceneryObject);
            }
        }
        if(arePrerequisitesForSceneryObjectsCreationMet())
        {
            generateNewSceneryObject();
        }
        if (backgroundMoves)
        {
            SceneryObject.updateBackgroundTimer();
        }
        moveCloud();
    }
    
    private boolean isBackgroundMoving(GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        return helicopter.isRotorSystemActive
            && !isMajorBossActive(gameRessourceProvider.getEnemies())
            && helicopter.tractor == null;
    }
    
    private boolean isMajorBossActive(Map<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
        return !enemy.get(ACTIVE).isEmpty()
                && enemy.get(ACTIVE).getFirst().type.isMajorBoss();
    }
    
    private boolean arePrerequisitesForSceneryObjectsCreationMet()
    {
        return areSceneryObjectsMissing()
            && GameEntityActivation.isApproved( numberOfMissingSceneryObjects(),
            SceneryObject.probabilityReductionFactor)
            && SceneryObject.generalObjectTimer == 0
            && backgroundMoves;
    }
    
    private boolean areSceneryObjectsMissing()
    {
        return numberOfMissingSceneryObjects() > 0;
    }
    
    private int numberOfMissingSceneryObjects()
    {
        return MAXIMUM_NUMBER_OF_SCENERY_OBJECTS - sceneryObjects.get(ACTIVE).size();
    }
    
    private void generateNewSceneryObject()
    {
        SceneryObject.generalObjectTimer = ACTIVATION_PAUSE_DURATION;
        Iterator<SceneryObject> i = sceneryObjects.get(INACTIVE).iterator();
        SceneryObject sceneryObject;
        if (i.hasNext())
        {
            sceneryObject = i.next();
            i.remove();
        }
        else{sceneryObject = new SceneryObject();}
        sceneryObject.preset();
        sceneryObjects.get(ACTIVE).add(sceneryObject);
    }
    
    private void moveCloud()
    {
        cloudX -= backgroundMoves ? 0.5f : 0.125f;
        if (cloudX < -250)
        {
            cloudX = 1000;
        }
    }
    
    // Getter-Methoden
    public List<Point> getStars()
    {
        return stars;
    }
    
    public float getCloudX()
    {
        return cloudX;
    }
    
    public Map<CollectionSubgroupType, LinkedList<SceneryObject>> getSceneryObjects()
    {
        return sceneryObjects;
    }
}
