package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.entities.GameEntityActivation;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
    
    public Scenery(GameRessourceProvider gameRessourceProvider)
    {
        setGameRessourceProvider(gameRessourceProvider);
        initializeStars();
    }
    
    private void initializeStars()
    {
        List<Point> calculatedStars = new ArrayList<>(NR_OF_STARS);
        for(int i = 0; i < NR_OF_STARS; i++)
        {
            Point star = new Point( Calculations.random(GraphicsAdapter.VIRTUAL_DIMENSION.getWidth()),
                Calculations.random(GROUND_Y));
            calculatedStars.add(star);
        }
        stars = List.copyOf(calculatedStars);
    }
    
    public void reset()
    {
        getSceneryObjects().get(CollectionSubgroupType.INACTIVE).addAll(getSceneryObjects().get(CollectionSubgroupType.ACTIVE));
        getSceneryObjects().get(CollectionSubgroupType.ACTIVE).clear();
        createInitialSceneryObjects();
        cloudX = 135;
    }
    
    public void createInitialSceneryObjects()
    {
        Iterator<SceneryObject> iterator = getSceneryObjects().get(CollectionSubgroupType.INACTIVE).iterator();
        
        SceneryObject firstCactus;
        if(iterator.hasNext()){firstCactus = iterator.next(); iterator.remove();}
        else{firstCactus = new SceneryObject();}
        firstCactus.makeFirstCactus();
        getSceneryObjects().get(CollectionSubgroupType.ACTIVE).add(firstCactus);
    
        SceneryObject firstHill;
        if(iterator.hasNext()){firstHill = iterator.next(); iterator.remove();}
        else{firstHill = new SceneryObject();}
        firstHill.makeFirstHill();
        getSceneryObjects().get(CollectionSubgroupType.ACTIVE).add(firstHill);
    
        SceneryObject firstDesert;
        if(iterator.hasNext()){firstDesert = iterator.next(); iterator.remove();}
        else{firstDesert = new SceneryObject();}
        firstDesert.makeFirstDesert();
        getSceneryObjects().get(CollectionSubgroupType.ACTIVE).add(firstDesert);
    }
    
    public void update(GameRessourceProvider gameRessourceProvider)
    {
        backgroundMoves = isBackgroundMoving(gameRessourceProvider);
        for(Iterator<SceneryObject> iterator = getSceneryObjects().get(CollectionSubgroupType.ACTIVE).iterator(); iterator.hasNext();)
        {
            SceneryObject sceneryObject = iterator.next();
            if (backgroundMoves)
            {
                sceneryObject.move();
            }
            if(sceneryObject.getSceneryObjectMaxX() < X_LIMIT_FOR_REMOVAL)
            {
                sceneryObject.clearImage();
                iterator.remove();
                getSceneryObjects().get(CollectionSubgroupType.INACTIVE).add(sceneryObject);
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
            && !isMajorBossActive(gameRessourceProvider.getActiveGameEntityManager()
                                                       .getEnemies())
            && helicopter.tractor == null;
    }
    
    private boolean isMajorBossActive(Map<CollectionSubgroupType, Queue<Enemy>> enemies)
    {
        return !enemies.get(CollectionSubgroupType.ACTIVE).isEmpty()
             && enemies.get(CollectionSubgroupType.ACTIVE)
                       .element().getType().isMajorBoss();
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
        return MAXIMUM_NUMBER_OF_SCENERY_OBJECTS - getSceneryObjects().get(CollectionSubgroupType.ACTIVE).size();
    }
    
    private void generateNewSceneryObject()
    {
        SceneryObject.generalObjectTimer = ACTIVATION_PAUSE_DURATION;
        Iterator<SceneryObject> iterator = getSceneryObjects().get(CollectionSubgroupType.INACTIVE).iterator();
        SceneryObject sceneryObject;
        if (iterator.hasNext())
        {
            sceneryObject = iterator.next();
            iterator.remove();
        }
        else{sceneryObject = new SceneryObject();}
        sceneryObject.preset();
        getSceneryObjects().get(CollectionSubgroupType.ACTIVE).add(sceneryObject);
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
    
    public Map<CollectionSubgroupType, Queue<SceneryObject>> getSceneryObjects()
    {
        return getGameRessourceProvider().getActiveGameEntityManager()
                                         .getSceneryObjects();
    }
}
