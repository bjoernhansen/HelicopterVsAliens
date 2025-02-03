package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.control.entities.ManageablePaintableActivation;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableController;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.PaintableEntity;
import de.helicopter_vs_aliens.util.Calculations;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static de.helicopter_vs_aliens.model.RectangularPaintableEntity.GROUND_Y;

// TODO es sollte nicht alles static sein, sondern über eine Scenery Instanz laufen
public class Scenery extends PaintableEntity
{
    private static final int
        NR_OF_STARS = 60;
    
    private static final int
        MAXIMUM_NUMBER_OF_SCENERY_OBJECTS = 20;
    
    private static final int
        ACTIVATION_PAUSE_DURATION = 20;
    
    private static final SceneryObjectFactory
        SCENERY_OBJECT_FACTORY = new SceneryObjectFactory();
    
    // TODO sollte eigentlich eine Instanz-Variable sein
    public static boolean
        isBackgroundMoving; // = true: bewegter Hintergrund
    
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
        getGameRessourceProvider().getManageablePaintableController().clearActiveEntities(ManageablePaintableGroupType.SCENERY_OBJECT);
        createInitialSceneryObjects();
        cloudX = 135;
    }
    
    public void createInitialSceneryObjects()
    {
        SceneryObject firstCactus = getGameRessourceProvider().getManageablePaintableController().activateEntity(
            SCENERY_OBJECT_FACTORY);
        firstCactus.initializeAsFirstCactus();
        SceneryObject firstHill = getGameRessourceProvider().getManageablePaintableController().activateEntity(
            SCENERY_OBJECT_FACTORY);
        firstHill.initializeAsFirstHill();
        SceneryObject firstDesert = getGameRessourceProvider().getManageablePaintableController().activateEntity(
            SCENERY_OBJECT_FACTORY);
        firstDesert.initializeAsFirstDesert();
    }
    
    public void update()
    {
        isBackgroundMoving = isBackgroundMoving();
        ManageablePaintableController manageablePaintableController = getGameRessourceProvider().getManageablePaintableController();
        if(isBackgroundMoving)
        {
            manageablePaintableController.forEachActiveEntity(ManageablePaintableGroupType.SCENERY_OBJECT,
                                                              SceneryObject::move);
        }
        manageablePaintableController.forEachActiveEntityIf(ManageablePaintableGroupType.SCENERY_OBJECT,
                                                            SceneryObject::isOutOfSight,
                                                            SceneryObject::clearImage);
        // TODO eventuell könnte noch eine postSet Methode ins Interface aufgenommen werden für das clear image,
        manageablePaintableController.removeIf(ManageablePaintableGroupType.SCENERY_OBJECT,
                                               SceneryObject::isOutOfSight);
        
        if(arePrerequisitesForSceneryObjectsCreationMet())
        {
            generateNewSceneryObject();
        }
        if (isBackgroundMoving)
        {
            SceneryObject.updateBackgroundTimer();
        }
        moveCloud();
    }
    
    private boolean isBackgroundMoving()
    {
        return getHelicopter().isMoving() && !isMajorBossActive();
    }
    
    private boolean isMajorBossActive()
    {
        return getGameRessourceProvider().getManageablePaintableController()
                                         .isMajorBossActive();
    }
    
    private boolean arePrerequisitesForSceneryObjectsCreationMet()
    {
        return areSceneryObjectsMissing()
            && ManageablePaintableActivation.isApproved(numberOfMissingSceneryObjects(),
                                                        SceneryObject.probabilityReductionFactor)
            && SceneryObject.generalObjectTimer == 0
            && isBackgroundMoving;
    }
    
    private boolean areSceneryObjectsMissing()
    {
        return numberOfMissingSceneryObjects() > 0;
    }
    
    private int numberOfMissingSceneryObjects()
    {
        return MAXIMUM_NUMBER_OF_SCENERY_OBJECTS - getGameRessourceProvider().getManageablePaintableController().numberOfActiveEntities(ManageablePaintableGroupType.SCENERY_OBJECT);
    }
    
    private void generateNewSceneryObject()
    {
        SceneryObject.generalObjectTimer = ACTIVATION_PAUSE_DURATION;
        SceneryObject sceneryObject = getGameRessourceProvider().getManageablePaintableController().activateEntity(
            SCENERY_OBJECT_FACTORY);
        sceneryObject.preset();
    }
    
    private void moveCloud()
    {
        cloudX -= isBackgroundMoving ? 0.5f : 0.125f;
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
    
    public void paintAllBackgroundSceneryObjects(GraphicsAdapter graphicsAdapter)
    {
        getGameRessourceProvider().getManageablePaintableController()
                                  .forEachActiveEntityIf(ManageablePaintableGroupType.SCENERY_OBJECT,
                                                         SceneryObject::isInBackground,
                                                         sceneryObject -> sceneryObject.paint(graphicsAdapter));
    }
}
