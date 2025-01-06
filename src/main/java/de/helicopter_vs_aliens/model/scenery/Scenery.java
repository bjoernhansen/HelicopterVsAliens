package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableActivation;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.PaintableEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import static de.helicopter_vs_aliens.model.RectangularPaintableEntity.GROUND_Y;

// TODO es sollte nicht alles static sein, sondern über eine Scenery Instanz laufen
public class Scenery extends PaintableEntity
{
    private static final int
        NR_OF_STARS = 60,
        MAXIMUM_NUMBER_OF_SCENERY_OBJECTS = 20,
        ACTIVATION_PAUSE_DURATION = 20,
        X_LIMIT_FOR_REMOVAL = -50;
    
    private static final SceneryObjectFactory
        sceneryObjectFactory = new SceneryObjectFactory();
    
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
        getGameRessourceProvider().getActiveManageablePaintableController().clearActiveEntities(ManageablePaintableGroupType.SCENERY_OBJECT);
        createInitialSceneryObjects();
        cloudX = 135;
    }
    
    public void createInitialSceneryObjects()
    {
        SceneryObject firstCactus = getSceneryObject();
        firstCactus.makeFirstCactus();
        SceneryObject firstHill = getSceneryObject();
        firstHill.makeFirstHill();
        SceneryObject firstDesert = getSceneryObject();
        firstDesert.makeFirstDesert();
        Collection<SceneryObject> firstSceneryObjects = List.of(firstCactus, firstHill, firstDesert);
        getSceneryObjects().addAll(firstSceneryObjects);
    }
    
    private SceneryObject getSceneryObject()
    {
        return getGameRessourceProvider().getNewManageablePaintableInstance(sceneryObjectFactory);
    }
    
    public void update(GameRessourceProvider gameRessourceProvider)
    {
        backgroundMoves = isBackgroundMoving(gameRessourceProvider);
        Queue<SceneryObject> activeSceneryObjects = getSceneryObjects();
        for(Iterator<SceneryObject> iterator = activeSceneryObjects.iterator(); iterator.hasNext();)
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
                gameRessourceProvider.storeManageablePaintable(sceneryObject);
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
            && !isMajorBossActive(gameRessourceProvider.getActiveManageablePaintableController()
                                                       .getIntactEnemies())
            && helicopter.tractor == null;
    }
    
    private boolean isMajorBossActive(Queue<Enemy> enemies)
    {
        return !enemies.isEmpty()
             && enemies
                       .element().getType().isMajorBoss();
    }
    
    private boolean arePrerequisitesForSceneryObjectsCreationMet()
    {
        return areSceneryObjectsMissing()
            && ManageablePaintableActivation.isApproved(numberOfMissingSceneryObjects(),
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
        return MAXIMUM_NUMBER_OF_SCENERY_OBJECTS - getSceneryObjects().size();
    }
    
    private void generateNewSceneryObject()
    {
        SceneryObject.generalObjectTimer = ACTIVATION_PAUSE_DURATION;
        SceneryObject sceneryObject = getSceneryObject();
        sceneryObject.preset();
        getSceneryObjects().add(sceneryObject);
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
    
    public Queue<SceneryObject> getSceneryObjects()
    {
        return getGameRessourceProvider().getActiveManageablePaintableController()
                                         .getSceneryObjects();
    }
}
