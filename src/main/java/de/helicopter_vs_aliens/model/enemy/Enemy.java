package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.entities.GroupTypeOwner;
import de.helicopter_vs_aliens.control.entities.PaintableEntityGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.enemy.EnemyPainter;
import de.helicopter_vs_aliens.model.RectangularPaintableEntity;
import de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType;
import de.helicopter_vs_aliens.model.enemy.devices.CloakingDevice;
import de.helicopter_vs_aliens.model.enemy.devices.NavigationDevice;
import de.helicopter_vs_aliens.model.enemy.maneuver.ManeuverManger;
import de.helicopter_vs_aliens.model.enemy.maneuver.Maneuverable;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.Pegasus;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.EnemyMissileFactory;
import de.helicopter_vs_aliens.model.missile.EnemyMissileType;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import javax.sound.sampled.Clip;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;


public abstract class Enemy extends RectangularPaintableEntity implements GroupTypeOwner, Maneuverable
    // TODO Klasse zerschlagen
{
    public static final int
        KAMIKAZE_RANGE = 620;
    
    public static final int
        TIMER_ALMOST_OVER = 1;
    
    public static final int
        DISABLED = -1; // TODO unnötig machen
    
    public static final int
        READY = 0;
    
    protected static final int
        EMP_SLOW_TIME = 175;    // Zeit, die von EMP getroffener Gegner verlangsamt bleibt // 113
        
    protected static final int
        APPEARANCE_DISTANCE = 10;
        
    private static final int
        DEFAULT_KAMIKAZE_SPEED_UP_X = 8,
        FIELD_OF_FIRE_TOLERANCE_Y = 15,
        WIDTH_VARIANCE_DIVISOR = 10,
        MAX_STARTING_Y = 220,
        MIN_STARTING_Y = 90,
        DODGE_BORDER_DISTANCE_LEFT = 90,
        DODGE_BORDER_DISTANCE_RIGHT = GraphicsAdapter.VIRTUAL_DIMENSION.getWidth() - DODGE_BORDER_DISTANCE_LEFT,
        DEFAULT_CALL_BACK_MINIMUM_FOR_TURN_AT_BARRIER = 0;
    

    private static final float
        PRIMARY_COLOR_BRIGHTNESS_FACTOR = 1.3f,
        SECONDARY_COLOR_BRIGHTNESS_FACTOR = 1.5f,
        DEFAULT_TURN_PROBABILITY = 0.25f;
    
    private static final float
        RADAR_DETECTABILITY = 0.2f;        // Alpha-Wert: legt fest, wie stark ein getarnter Gegner bei aktiviertem Radar noch zu sehen ist
    
    private static final float
        REPARATION_POWER_UP_DROP_RATE = 0.14f;
    
    // Multiplikatoren, welche den Grundschaden von Raketen unter bestimmten Voraussetzungen erhöhen
    private static final float
        EMP_DAMAGE_FACTOR_BOSS = 1.5f;            // Pegasus-Klasse: Schaden einer EMP-Welle im Verhältnis zum normalen Raketenschaden gegenüber von Boss-Gegnern // 1.5
    
    private static final float
        EMP_DAMAGE_FACTOR_ORDINARY = 2.5f;        // Pegasus-Klasse: wie EMP_DAMAGE_FACTOR_BOSS, nur für Nicht-Boss-Gegner // 3
    
    private static final int
        // Raum-Konstanten
        SAVE_ZONE_WIDTH = 116;
    
    private static final int
        DISAPPEARANCE_DISTANCE = 100;
    private static final int
        BARRIER_DISTANCE = 100;
    
    private static final int
        STUNNING_TIME_BASIS = 45;    // Basis-Wert zur Berechnung der Stun-Zeit nach Treffern von Stopp-Raketen
    
    private static final int
        MIN_TURN_TIME = 31;
    private static final int
        MIN_TURN_NOISELESS_TIME = 15;
    
    private static final int
        STANDARD_REWARD_FACTOR = 1;
    
    private static final int
        MAX_IMAGE_COUNT = 4;
    
    private static final Point2D
        ZERO_SPEED = new Point2D.Float(0, 0);
    
    protected static final Point2D
        SLOW_VERTICAL_SPEED = new Point2D.Float(0, 1);
    
    private static final Point
        TURN_DISTANCE = new Point(50, 10);
    
    protected static final Rectangle
        TURN_FRAME = new Rectangle(TURN_DISTANCE.x,
                                   TURN_DISTANCE.y,
                                   GraphicsAdapter.VIRTUAL_DIMENSION.getWidth()
                                       - 2 * TURN_DISTANCE.x,
                                   GROUND_Y
                                       - SAVE_ZONE_WIDTH
                                       - 2 * TURN_DISTANCE.y);
    
    // für die Tarnung nötige Variablen
    public static final float[]
        scales = {1f, 1f, 1f, RADAR_DETECTABILITY};
    public static final float[]
        offsets = new float[4];
    
    private static final RescaleOp
        CLOAKED_ENEMY_RESCALE_OPERATOR = new RescaleOp(scales, offsets, null);
    
    public static final Point2D
        boss = new Point2D.Float();    // Koordinaten vom aktuellen Boss; wichtig für Gegner-produzierende Boss-Gegner
    
    private static final EnemyMissileFactory
        enemyMissileFactory = new EnemyMissileFactory();
    
    /*
     * 	Attribute der Enemy-Objekte
     */
    
    public int
        startingHitPoints,                // Anfangs-HitPoints (bei Erstellung des Gegners)
        invincibleTimer,                // reguliert die Zeit, die ein Gegner unverwundbar ist
        teleportTimer,                    // Zeit [frames], bis der Gegner sich erneut teleportieren kann
        shield,                            // nur für Boss 5 relevant; kann die Werte 0 (kein Schild), 1 oder 2 annehmen
        burrowTimer,
        untouchedCounter,
        stunningTimer,
        empSlowedTimer,            // reguliert die Länge der Verlangsamung nach EMP-Treffer (Pegasus-Klasse)
        collisionDamageTimer;    // Timer zur überwachung der Zeit zwischen zwei Helikopter-HP-Abzügen;
    
    public boolean
        isTouchingHelicopter,
        hasUnresolvedIntersection;
    
    
    private final EnemyType
        type;
    
    public BarrierPositionType
        touchedSite,
        lastTouchedSite;
    
    // Farben
    private Color
        primaryColor;
    
    private Color
        secondaryColor;
    

    
    // TODO is wird of -direction.x (minus) übergeben, unlogisch, implementieren hier verständlicher machen. ggf. enemy übergeben und dann isMovingLeft etc. aufrufen
    
    
    Enemy
        stoppingBarrier;
    
    Enemy
        isPreviousStoppingBarrier;
    
    private int
        hitPoints;                        // aktuelle HitPoints
    
    private int
        lifetime;                // Anzahl der Frames seit Erstellung des Gegners; und vergangene Zeit seit Erstellung, Zeit
    private int
        crashPositionY;                // Bestimmt wie tief ein Gegner nach Absturz im Boden versinken kann
    private int
        collisionTimer;
    private int
        turnAudioTimer;
    private int
        explodingTimer;            // Timer zur überwachung der Zeit zwischen Abschuss und Absturz
    

    protected int
        callBack;
    private int
        chaosTimer;
    protected int
        speedup;
    
    protected int
        shootTimer;
    
    protected int
        turnTimer;
    protected int
        dodgeTimer;            // Zeit [frames], bis ein Gegner erneut ausweichen kann
    
    
    protected int// nur für Hindernis-Gegner relevant
        rotorColor;    // TODO Color als int?? umbenennen!
    protected int barrierShootTimer;
    protected int barrierTeleportTimer;
    protected int shootPause;
    protected int shootingRate;
    protected int shotsPerCycle;
    protected int shootingCycleLength;
    protected int shotSpeed;
    protected int shotRotationSpeed;
    
    private int// Regulation des Stun-Effektes nach Treffer durch Stopp-Rakete der Orochi-Klasse
        nonStunnableTimer;
    private int totalStunningTime;
    private int knockBackDirection;
    
    protected float
        deactivationProbability;
    
    protected boolean canDodge;                // = true: Gegner kann Schüssen ausweichen
    protected boolean canKamikaze;            // = true: Gegner geht auf Kollisionskurs, wenn die Distanz zum Helicopter klein ist
    protected boolean isAbleToTurnAroundEarly;
    protected boolean canMoveChaotic;        // reguliert den zufälligen Richtungswechsel bei Chaos-Flug-Modus
    protected boolean canSinusMove;            // Gegner fliegt in Kurven ähnlicher einer Sinus-Kurve
    protected boolean canTurn;                // Gegner ändert bei Beschuss eventuell seine Flugrichtung in Richtung Helikopter
    protected boolean canInstantlyTurnAround;            // Gegner ändert bei Beschuss immer(!) seine Flugrichtung in Richtung Helikopter
    protected boolean canFrontalSpeedup;        // Gegner wird schneller, wenn Helikopter ihm zu Nahe kommt
    protected boolean canChaosSpeedup;        // erhöht die Geschwindigkeit, wenn in Helicopter-Nähe
    private boolean isDestroyed;            // = true: Gegner wurde vernichtet
    private boolean hasCrashed;            // = true: Gegner ist abgestürzt
    private boolean isEmpShocked;            // = true: Gegner steht unter EMP-Schock --> ist verlangsamt
    private boolean isMarkedForRemoval;        // = true --> Gegner nicht mehr zu sehen; kann entsorgt werden
    protected boolean isClockwiseBarrier;        // = true: der Rotor des Hindernisses dreht im Uhrzeigersinn
    protected boolean isRecoveringSpeed;
    
    protected EnemyMissileType
        shotType;
    
    private final GraphicsAdapter[]
        graphicsAdapters = new GraphicsAdapter[2];
    
    private final CloakingDevice
        cloakingDevice = new CloakingDevice();
    
    private final NavigationDevice
        navigationDevice = new NavigationDevice();
    
    private final BufferedImage[]
        image = new BufferedImage[MAX_IMAGE_COUNT];
    
    protected final Point2D
        targetSpeedLevel = new Point2D.Float();        // Anfangsgeschwindigkeit
    
    private final Point2D
        speedLevel = new Point2D.Float();            // auf Basis dieses Objektes wird die tatsächliche Geschwindigkeit berechnet
    
    private final Point2D
        speed = new Point2D.Float();                // tatsächliche Geschwindigkeit
    
    protected final Point2D
        shootingDirection = new Point2D.Float();    // Schussrichtung von schießenden Barrier-Gegnern
    
    private final ManeuverManger
        maneuverManger = new ManeuverManger();
    
    
    protected Enemy()
    {
        type = EnemyType.getTypeFromClass(getClass());
        maneuverManger.initializeManeuversFor(this);
    }
    
    public void reset()
    {
        lifetime = 0;
        stopMoving();
        initializeMovingDirection();
        maneuverManger.resetManeuvers();
        
        callBack = 0;
        shield = 0;
        isDestroyed = false;
        isMarkedForRemoval = false;
        hasUnresolvedIntersection = false;
        canMoveChaotic = false;
        canDodge = false;
        canChaosSpeedup = false;
        canKamikaze = false;
        isAbleToTurnAroundEarly = false;
        isTouchingHelicopter = false;
        canFrontalSpeedup = false;
        canSinusMove = false;
        canTurn = false;
        isClockwiseBarrier = true;
        stoppingBarrier = null;
        isPreviousStoppingBarrier = null;
        hasCrashed = false;
        canInstantlyTurnAround = false;
        isRecoveringSpeed = false;
        isEmpShocked = false;
        collisionDamageTimer = READY;
        collisionTimer = READY;
        turnAudioTimer = READY;
        dodgeTimer = READY;
        turnTimer = READY;
        explodingTimer = READY;
        empSlowedTimer = READY;
        invincibleTimer = READY;
        chaosTimer = READY;
        nonStunnableTimer = READY;
        cloakingDevice.reset();
        teleportTimer = DISABLED;
        shootTimer = DISABLED;
        barrierShootTimer = DISABLED;
        barrierTeleportTimer = DISABLED;
        burrowTimer = DISABLED;
        speedup = DISABLED;
        shootingDirection.setLocation(0, 0);
        shootPause = 0;
        shootingRate = 0;
        shotsPerCycle = 0;
        shootingCycleLength = 0;
        shotSpeed = 0;
        shotRotationSpeed = 0;
        shotType = EnemyMissileType.DISCHARGER;
        
        touchedSite = BarrierPositionType.NONE;
        lastTouchedSite = BarrierPositionType.NONE;
        
        untouchedCounter = 0;
        rotorColor = 0;
        deactivationProbability = 0f;
        stunningTimer = READY;
        
        totalStunningTime = 0;
        knockBackDirection = 0;
    }
    
    public boolean isCompletelyCloaked()
    {
        return cloakingDevice.isWorkingAtMaximumEfficiency();
    }
    
    public boolean isCloakingDeviceActive()
    {
        return cloakingDevice.isActive();
    }
    
    public boolean isAlmostCloaked()
    {
        return cloakingDevice.isAlmostWorkingAtMaximumEfficiency();
    }
    
    protected NavigationDevice getNavigationDevice()
    {
        return navigationDevice;
    }
    
    public boolean isFlyingLeft()
    {
        return navigationDevice.isFlyingLeft();
    }
    
    public boolean isFlyingRight()
    {
        return navigationDevice.isFlyingRight();
    }
    
    public int getNegativeDirectionX()
    {
        return -navigationDevice.getDirectionX();
    }
    
    public boolean isFlyingDown()
    {
        return navigationDevice.isFlyingDown();
    }
    
    public boolean isFlyingUp()
    {
        return navigationDevice.isFlyingUp();
    }
    
    public int getDirectionX()
    {
        return navigationDevice.getDirectionX();
    }
    
    private void initializeMovingDirection()
    {
        navigationDevice.turnLeft();
        navigationDevice.setRandomDirectionY();
    }
    
    public void performLocationAdaptionAction(GameRessourceProvider gameRessourceProvider)
    {
    }
    
    public boolean isMovingX()
    {
        return getSpeedLevel().getX() != 0;
    }
    
    public boolean isMovingY()
    {
        return getSpeedLevel().getY() != 0;
    }
    
    public void dimmedRepaint()
    {
        adjustBrightnessOfColors(Colorations.BARRIER_NIGHT_DIM_FACTOR);
        repaint();
    }
    
    public boolean hasGlowingEyes()
    {
        return isIntact() && isMeetingRequirementsForGlowingEyes();
    }
    
    protected abstract boolean isMeetingRequirementsForGlowingEyes();
    
    public void clearImage()
    {
        for(int i = 0; i < image.length; i++)
        {
            image[i] = null;
            if(i < 2)
            {
                graphicsAdapters[i] = null;
            }
        }
    }
    
    // TODO gehört diese Methode nicht eher in die Painter-Klasse?
    // TODO Raw use beseitigen
    public void repaint()
    {
        for(int j = 0; j < 2; j++)
        {
            if(getModel() != EnemyModelType.BARRIER)
            {
                // TODO hier taucht NullPointerException auf. Warum?
                graphicsAdapters[j].setComposite(AlphaComposite.Src);
                graphicsAdapters[j].setColor(Colorations.translucentDarkestBlack);
                graphicsAdapters[j].fillRect(0, 0, image[j].getWidth(), image[j].getHeight());
            }
            EnemyPainter enemyPainter = GraphicsManager.getInstance()
                                                       .getPainter(getClass());
            enemyPainter.standardImagePaintForCorpus(graphicsAdapters[j], this, 1 - 2 * j);
        }
    }
    
    public boolean countsForTotalAmountOfEnemiesSeen()
    {
        return true;
    }
    
    public void initialize()
    {
        setBasicProperties();
        doTypeSpecificInitialization();
        finalizeInitialization();
    }
    
    private void setTargetSpeedLevel()
    {
        Point2D nextTargetSpeedLevel = type.calculateTargetSpeed();
        targetSpeedLevel.setLocation(nextTargetSpeedLevel);
    }
    
    private void setBasicProperties()
    {
        setTypeSpecificPrerequisites();
        setHitPoints();
        setSize();
        setColors();
        setTargetSpeedLevel();
    }
    
    protected void setTypeSpecificPrerequisites()
    {
    }
    
    private void setColors()
    {
        primaryColor = calculatePrimaryColor();
        secondaryColor = calculateSecondaryColor();
    }
    
    private Color calculatePrimaryColor()
    {
        return Colorations.adjustBrightness(getBaseColor(), getPrimaryColorBrightnessFactor());
    }
    
    protected Color getBaseColor()
    {
        return type.calculateColor();
    }
    
    protected float getPrimaryColorBrightnessFactor()
    {
        return PRIMARY_COLOR_BRIGHTNESS_FACTOR;
    }
    
    private Color calculateSecondaryColor()
    {
        return Colorations.adjustBrightness(primaryColor, getSecondaryColorBrightnessFactor());
    }
    
    protected float getSecondaryColorBrightnessFactor()
    {
        return SECONDARY_COLOR_BRIGHTNESS_FACTOR;
    }
    
    protected abstract void doTypeSpecificInitialization();
    
    protected void finalizeInitialization()
    {
        setInitialLocation();
        
        reachTargetSpeedLevel();
        setPaintBounds((int)getWidth(),
                       (int)getHeight());
        assignImage();
        
        if(hasCanonReadyToFire())
        {
            initializeShootDirectionOfDefaultEnemies();
        }
    }
    
    protected boolean hasCanonReadyToFire()
    {
        return shootTimer == READY;
    }
    
    private boolean isShooter()
    {
        return shootTimer != DISABLED;
    }
    
    private boolean isCurrentlyShooting()
    {
        return isShooter() && !hasCanonReadyToFire();
    }
    
    public int getShootTimer()
    {
        return shootTimer;
    }
    
    private void setHitPoints()
    {
        hitPoints = calculateHitPoints();
        startingHitPoints = hitPoints;
    }
    
    protected int calculateHitPoints()
    {
        return type.getHitPoints() + hitPointVariance();
    }
    
    protected int hitPointVariance()
    {
        return 0;
    }
    
    private void setSize()
    {
        int width = calculateInitialWidth();
        double height = calculateInitialHeight(width);
        setDimension(width, height);
    }
    
    private int calculateInitialWidth()
    {
        return type.getWidth() + getWidthVariance();
    }
    
    protected int getWidthVariance()
    {
        return Calculations.random(getWidthVarianceRange());
    }
    
    private int getWidthVarianceRange()
    {
        return type.getWidth() / getWidthVarianceDivisor();
    }
    
    protected int getWidthVarianceDivisor()
    {
        return WIDTH_VARIANCE_DIVISOR;
    }
    
    protected void setInitialLocation()
    {
        double x = calculateInitialX();
        double y = calculateInitialY();
        setLocation(x, y);
    }
    
    protected double calculateInitialX()
    {
        return GraphicsAdapter.VIRTUAL_DIMENSION.getWidth() + APPEARANCE_DISTANCE;
    }
    
    protected double calculateInitialY()
    {
        return MIN_STARTING_Y + Math.random() * (MAX_STARTING_Y - getHeight());
    }
    
    // TODO Methode überarbeiten und Teile in kleinere Methoden auslagern
    // TODO Raw use beseitigen
    // TODO gehört das nicht in eine andere Klasse? EnemyPainter oder ähnliches?
    private void assignImage()
    {
        for(int i = 0; i < 2; i++)
        {
            image[i] = getBufferedImage();
            graphicsAdapters[i] = Graphics2DAdapter.withAntialiasing(image[i]);
            //graphics[i].setComposite(AlphaComposite.Src);
            
            EnemyPainter enemyPainter = GraphicsManager.getInstance()
                                                       .getPainter(getClass());
            enemyPainter.standardImagePaintForCorpus(graphicsAdapters[i], this, 1 - 2 * i);
            if(cloakingDevice.isEnabled() && getHelicopter().getType() == HelicopterType.OROCHI)
            {
                BufferedImage tempImage = getBufferedImage();
                image[2 + i] = getBufferedImage();
                enemyPainter.paintCorpus(Graphics2DAdapter.withAntialiasing(tempImage),
                                         this,
                                         1 - 2 * i,
                                         Color.red,
                                         true,
                                         true);
                Graphics2DAdapter.withAntialiasing(image[2 + i])
                                 .drawImage(tempImage, CLOAKED_ENEMY_RESCALE_OPERATOR, 0, 0);
            }
        }
    }
    
    private BufferedImage getBufferedImage()
    {
        return new BufferedImage((int)(1.028f * paintBounds.width),
                                 (int)(1.250f * paintBounds.height),
                                 BufferedImage.TYPE_INT_ARGB);
    }
    
    
    private void placeNearHelicopter()
    {
        Helicopter helicopter = getHelicopter();
        boolean isLeftOfHelicopter = helicopter.getMaxX() + 0.5f * getWidth() + BARRIER_DISTANCE >= 1024;
        
        int x;
        int y = (int)(helicopter.getY()
            + helicopter.getHeight() / 2
            - getWidth()
            + Calculations.random() * getWidth());
        
        if(isLeftOfHelicopter)
        {
            x = (int)(helicopter.getX()
                - 3 * getWidth() / 2
                - 10
                + Calculations.random() * (getWidth() / 3.0));
        }
        else
        {
            x = (int)(helicopter.getMaxX() + BARRIER_DISTANCE);
        }
        // TODO in Methode auslagern
        setLocation(x, Math.max(0, Math.min(y, GROUND_Y - getWidth())));
    }
    
    public int getHitPoints()
    {
        return hitPoints;
    }
    
    protected void setHitPoints(int hitPoints)
    {
        this.hitPoints = hitPoints;
    }
    
    private double calculateInitialHeight(int width)
    {
        return getModel().getHeightFactor() * width;
    }
    
    private void initializeShootDirectionOfDefaultEnemies()
    {
        float shootingDirectionX = navigationDevice.getDirectionX();
        shootingDirection.setLocation(shootingDirectionX, 0f);
    }
    
    private boolean turnaroundIsTurnAwayX()
    {
        return turnaroundIsTurnAway(navigationDevice.getDirectionX(),
                                    getCenterX(),
                                    stoppingBarrier.getCenterX());
    }
    
    private boolean turnaroundIsTurnAwayY()
    {
        return turnaroundIsTurnAway(navigationDevice.getDirectionY(),
                                    getCenterY(),
                                    stoppingBarrier.getCenterY());
    }
    
    private static boolean turnaroundIsTurnAway(double direction, double enemyCenter, double barrierCenter)
    {
        return direction == 1 && enemyCenter < barrierCenter
            || direction == -1 && enemyCenter > barrierCenter;
    }
    
    public boolean isVisibleNonBarricadeVessel()
    {
        return isVisible();
    }
    
    private boolean isVisible()
    {
        return !isCompletelyCloaked() || canBeDetectedByHelicopter();
    }
    
    public boolean canBeDetectedByHelicopter()
    {
        return getHelicopter().canDetectCloakedVessels();
    }
    
    /**
     * Regulation der Gegner-Bewegung:
     * Unter Berücksichtigung jeglicher Eventualitäten (Spezial-Manöver, Ausweichbewegungen, ...)
     * werden die neuen Koordinaten berechnet.
     */
    public final void update(GameRessourceProvider gameRessourceProvider)
    {
        lifetime++;
        updateTimer();
        if(Events.isCurrentLevelBossLevel())
        {
            callBack = 0;
        }
        checkForBarrierCollision();
        if(!isStunned())
        {
            performStoppableActions(gameRessourceProvider);
        }
        calculateSpeed();
        move();
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        if(helicopter.isCollidingWith(this))
        {
            collision(gameRessourceProvider);
        }
        helicopter.typeSpecificActionOn(this, gameRessourceProvider);
        if(hasDeadlyGroundContact())
        {
            destroyByCrash(gameRessourceProvider);
        }
        if(isToBeRemoved())
        {
            markForRemoval();
        }
        setPaintBounds();
    }
    
    public boolean isStunned()
    {
        return stunningTimer > READY;
    }
    
    protected boolean hasDeadlyGroundContact()
    {
        return isIntersectingGroundLine()
            && isIntact();
    }
    
    private boolean isIntersectingGroundLine()
    {
        return getMaxY() > GROUND_Y;
    }
    
    protected void checkForBarrierCollision()
    {
        isPreviousStoppingBarrier = stoppingBarrier;
        if(stoppingBarrier == null || !stoppingBarrier.intersects(this))
        {
            stoppingBarrier = null;
            for(int i = 0; i < EnemyController.currentNumberOfBarriers; i++)
            {
                if(EnemyController.livingBarrier[i] != this
                    && EnemyController.livingBarrier[i].intersects(this))
                
                {
                    stoppingBarrier = EnemyController.livingBarrier[i];
                    break;
                }
            }
        }
    }
    
    protected void updateTimer()
    {
        if(collisionDamageTimer > 0)
        {
            collisionDamageTimer--;
        }
        if(collisionTimer > 0)
        {
            collisionTimer--;
        }
        if(turnAudioTimer > 0)
        {
            turnAudioTimer--;
        }
        if(empSlowedTimer > 0)
        {
            empSlowedTimer--;
        }
        if(invincibleTimer > 0)
        {
            invincibleTimer--;
        }
        if(chaosTimer > 0)
        {
            chaosTimer--;
        }
        if(nonStunnableTimer > 0)
        {
            nonStunnableTimer--;
        }
        if(turnTimer > 0)
        {
            turnTimer--;
        }
        if(isStunned())
        {
            stunningTimer--;
        }
    }
    
    protected void performStoppableActions(GameRessourceProvider gameRessourceProvider)
    {
        performFlightManeuver(gameRessourceProvider);
        validateTurns();
    }
    
    protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
    {
        maneuverManger.performAll();
        
        // Beschleunigung
        if(speedup != DISABLED || canFrontalSpeedup)
        {
            evaluateSpeedup();
        }
        
        // Chaos-Flug
        if(canMoveChaotic
            && chaosTimer == READY
            && dodgeTimer == READY)
        {
            if(Calculations.tossUp(0.2f) && type.isShieldMaker())
            {
                navigationDevice.turnAround();
            }
            if(Calculations.tossUp(0.2f))
            {
                navigationDevice.switchDirectionY();
            }
            chaosTimer = 5;
        }
        
        // Early x-Turn
        if(isTurningAroundEarly())
        {
            isAbleToTurnAroundEarly = false;
            navigationDevice.turnRight();
        }
        
        // Frontal-Angriff
        if(isAbleToMakeKamikaze())
        {
            makeKamikazeIfAppropriate();
        }
        
        // Shooting
        if(isShooter())
        {
            evaluateShooting(gameRessourceProvider);
        }
        
        // Sinus- und Loop-Flug
        if(canSinusMove)
        {
            sinusLoop();
        }
        
        // tarnen
        if(cloakingDevice.isActive() && !isPreventedFromCloaking())
        {
            cloaking();
        }
        
        //Chaos-SpeedUp
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        if(canChaosSpeedup
            && !isTargetSpeedExceededX()
            && helicopter.getX() - getX() > -350)
        {
            setSpeedLevelX(targetSpeedLevel.getX() + 6);
        }
        if(canChaosSpeedup && (helicopter.getX() - getX()) > -160)
        {
            canMoveChaotic = true;
            setSpeedLevelY(9 + 4.5 * Math.random());
        }
        
        // Ausweichen
        if(dodgeTimer > 0)
        {
            evaluateDodge();
        }
        
        // Teleportieren
        if(teleportTimer > 0)
        {
            teleportTimer--;
            if(teleportTimer == READY)
            {
                reachTargetSpeedLevel();
            }
        }
    }
    
    protected boolean isPreventedFromCloaking()
    {
        return isEmpSlowed();
    }
    
    private boolean isAbleToMakeKamikaze()
    {
        return canKamikaze && teleportTimer <= 0;
    }
    
    private boolean isTurningAroundEarly()
    {
        return isAbleToTurnAroundEarly && getMinX() < 0.85 * GraphicsAdapter.VIRTUAL_DIMENSION.getWidth();
    }
    
    private void validateTurns()
    {
        if(isTouchingBarrier())
        {
            tryToTurnAtBarrier();
        }
        else if(isTurningAtLateralBoundaries())
        {
            performLateralTurn();
        }
        else if(isTurningAtVerticalBoundaries())
        {
            performVerticalTurn();
        }
    }
    
    private boolean isTouchingBarrier()
    {
        return stoppingBarrier != null && burrowTimer == DISABLED;
    }
    
    private void tryToTurnAtBarrier()
    {
        turnTimer = MIN_TURN_TIME;
        if(isOnScreen()
            && stoppingBarrier.isOnScreen()
            && stoppingBarrier != isPreviousStoppingBarrier
            && turnAudioTimer == READY)
        {
            Audio.play(Audio.landing);
            turnAudioTimer = MIN_TURN_NOISELESS_TIME;
        }
        
        if(hasLateralFaceTouchWith(stoppingBarrier))
        {
            if(turnaroundIsTurnAwayX()
                // Gegner sollen nicht an Barriers abdrehen, bevor sie im Bild waren.
                && isOnScreen())
            {
                performXTurnAtBarrier();
            }
        }
        else if(turnaroundIsTurnAwayY())
        {
            navigationDevice.switchDirectionY();
        }
    }
    
    boolean hasLateralFaceTouchWith(Enemy barrier)
    {
        return
            Calculations.getIntersectionLength(getMinX(),
                                               getMaxX(),
                                               barrier.getMinX(),
                                               barrier.getMaxX())
                <
                Calculations.getIntersectionLength(getMinY(),
                                                   getMaxY(),
                                                   barrier.getMinY(),
                                                   barrier.getMaxY());
    }
    
    void performXTurnAtBarrier()
    {
        navigationDevice.turnAround();
        if(callBack <= getCallBackMinimumForTurnAtBarrier())
        {
            callBack++;
        }
    }
    
    protected int getCallBackMinimumForTurnAtBarrier()
    {
        return DEFAULT_CALL_BACK_MINIMUM_FOR_TURN_AT_BARRIER;
    }
    
    private boolean isTurningAtLateralBoundaries()
    {
        return barrierTeleportTimer == DISABLED
            && turnTimer == READY
            && isRemainingOnScreen()
            && isMovingOutOfLateralBoundaries();
    }
    
    protected boolean isRemainingOnScreen()
    {
        return callBack > 0;
    }
    
    private boolean isMovingOutOfLateralBoundaries()
    {
        return isMovingOutOfLeftBoundaries() || isMovingOutOfRightBoundaries();
    }
    
    private boolean isMovingOutOfLeftBoundaries()
    {
        return isFlyingLeft() && hasCrossedLeftBoundary();
    }
    
    private boolean hasCrossedLeftBoundary()
    {
        return getMinX() < getLeftBoundary();
    }
    
    protected double getLeftBoundary()
    {
        return TURN_FRAME.getMinX();
    }
    
    private boolean isMovingOutOfRightBoundaries()
    {
        return isFlyingRight() && hasCrossedRightBoundary();
    }
    
    protected boolean hasCrossedRightBoundary()
    {
        return getMaxX() > getRightBoundary();
    }
    
    protected double getRightBoundary()
    {
        return TURN_FRAME.getMaxX();
    }
    
    private void performLateralTurn()
    {
        navigationDevice.turnAround();
        turnTimer = MIN_TURN_TIME;
        if(callBack > 0)
        {
            callBack--;
        }
    }
    
    private boolean isTurningAtVerticalBoundaries()
    {
        return burrowTimer == DISABLED
            && isMovingOutOfVerticalBoundaries();
    }
    
    private boolean isMovingOutOfVerticalBoundaries()
    {
        return isMovingOutOfTopBoundaries() || isMovingOutOfBottomBoundaries();
    }
    
    private boolean isMovingOutOfTopBoundaries()
    {
        return isFlyingUp() && hasCrossedTopBoundary();
    }
    
    private boolean hasCrossedTopBoundary()
    {
        return getMinY() <= getTopBoundary();
    }
    
    protected double getTopBoundary()
    {
        return TURN_FRAME.getMinY();
    }
    
    private boolean isMovingOutOfBottomBoundaries()
    {
        return isFlyingDown()
            && hasCrossedBottomBoundaries()
            && isIntact();
    }
    
    private boolean hasCrossedBottomBoundaries()
    {
        return getMaxY() >= getBottomBoundary();
    }
    
    protected double getBottomBoundary()
    {
        return TURN_FRAME.getMaxY();
    }
    
    protected void performVerticalTurn()
    {
        navigationDevice.switchDirectionY();
        if(canSinusMove)
        {
            setSpeedLevelY(1.0);
        }
    }
    
    public void checkForEmpStrike(GameRessourceProvider gameRessourceProvider, Pegasus pegasus)
    {
        if(pegasus.empWave != null)
        {
            if(isEmpShockable(pegasus))
            {
                empShock(gameRessourceProvider, pegasus);
            }
        }
        else
        {
            isEmpShocked = false;
        }
    }
    
    protected void empShock(GameRessourceProvider gameRessourceProvider, Pegasus pegasus)
    {
        takeDamage((int)getEmpVulnerabilityFactor() * pegasus.getEmpDamage());
        isEmpShocked = true;
        disableSiteEffects();
        
        if(hasHPsLeft())
        {
            performEmpWaveSurvivorActions();
            Explosion.start(gameRessourceProvider.getActivePaintableEntityManager()
                                                 .getExplosions(), pegasus,
                            getCenterX(),
                            getCenterY(), ExplosionType.STUNNING, false);
        }
        else
        {
            Audio.play(Audio.explosion2);
            int reward = calculateReward();
            pegasus.rewardAndCountEmpKill(reward);
            dieFromEmpWave(gameRessourceProvider);
        }
    }
    
    protected void performEmpWaveSurvivorActions()
    {
        Audio.play(Audio.stun);
        if(teleportTimer == READY)
        {
            teleport();
        }
        else if(isAbleToBeSlowedDownByEmp())
        {
            empSlowedTimer = getEmpSlowTime();
        }
        // TODO übergeben von null ... Implementierung verbessern
        reactToHit(null);
    }
    
    protected boolean isAbleToBeSlowedDownByEmp()
    {
        return isStunnable();
    }
    
    protected int getEmpSlowTime()
    {
        return EMP_SLOW_TIME;
    }
    
    private float getEmpVulnerabilityFactor()
    {
        return type.isMajorBoss()
            ? EMP_DAMAGE_FACTOR_BOSS
            : EMP_DAMAGE_FACTOR_ORDINARY;
    }
    
    private boolean isToBeRemoved()
    {
        return type != EnemyType.BOSS_2_SERVANT
            && barrierTeleportTimer == DISABLED
            && !isDodging()
            && (callBack == 0 || !speed.equals(ZERO_SPEED))
            && ((getMinX() > GraphicsAdapter.VIRTUAL_DIMENSION.getWidth() + DISAPPEARANCE_DISTANCE
            && isFlyingRight())
            || (getMaxX() < -DISAPPEARANCE_DISTANCE));
    }
    
    private boolean isDodging()
    {
        return dodgeTimer > 0;
    }
    
    public boolean isOnScreen()
    {
        return getMaxX() > 0
            && getMinX() < GraphicsAdapter.VIRTUAL_DIMENSION.getWidth();
    }
    
    public void markForRemoval()
    {
        isMarkedForRemoval = true;
    }
    
    public boolean isMarkedForRemoval()
    {
        return isMarkedForRemoval;
    }
    
    private boolean isEmpShockable(Pegasus pegasus)
    {
        return !isEmpShocked
            && isIntact()
            && !isInvincible()
            && !(barrierTeleportTimer != DISABLED && barrierShootTimer == DISABLED)
            && pegasus.empWave.getEllipse()
                              .intersects(getBounds());
    }
    
    private void evaluateSpeedup()
    {
        if(speedLevel.getX() < (speedup == DISABLED ? 12 : 19)
            && (speedup > 0 || canFrontalSpeedup))
        {
            increaseSpeedLevelX(0.5);
        }
        if(speedup == 0 && isAtEyeLevelWithHelicopter())
        {
            speedup = 1;
            canSinusMove = true;
        }
        else if(speedup == 1 && !isAtEyeLevelWithHelicopter())
        {
            speedup = 2;
        }
        else if(speedup == 2 && isAtEyeLevelWithHelicopter())
        {
            speedup = 3;
            canSinusMove = false;
            setSpeedLevelY(1.5);
            if(getY() < getHelicopter().getY())
            {
                navigationDevice.flyDown();
            }
            else
            {
                navigationDevice.flyUp();
            }
        }
    }
    
    private boolean isAtEyeLevelWithHelicopter()
    {
        return intersects(Integer.MIN_VALUE / 2f,
                          getHelicopter().getY(),
                          Integer.MAX_VALUE,
                          getHelicopter().getHeight());
    }
    
    @Override
    public boolean isTargetSpeedReachedOrExceededX()
    {
        return getSpeedLevel().getX() >= targetSpeedLevel.getX();
    }
    
    private boolean isTargetSpeedExceededX()
    {
        return getSpeedLevel().getX() > targetSpeedLevel.getX();
    }
    
    
    protected void startKamikazeMode()
    {
        canKamikaze = true;
        canFrontalSpeedup = true;
    }
    
    protected void makeKamikazeIfAppropriate()
    {
        if(isWithinKamikazeRangeOfHelicopter())
        {
            makeKamikazeWithHelicopter();
        }
        else if(!canFrontalSpeedup && dodgeTimer == READY)
        {
            if(type == EnemyType.BODYGUARD && Events.boss.shield < 1)
            {
                setSpeedLevelX(7.5);
            }
            else
            {
                reachTargetSpeedLevel();
            }
        }
    }
    
    protected void makeKamikazeWithHelicopter()
    {
        adaptSpeedLevelForKamikaze();
        if(isFlyingDown() && isFurtherDownThanHelicopter())
        {
            navigationDevice.flyUp();
            setSpeedLevelToZeroY();
        }
        else if(isFlyingUp() && isFurtherUpThanHelicopter())
        {
            navigationDevice.flyDown();
            setSpeedLevelToZeroY();
        }
        if(speedLevel.getY() < 8)
        {
            increaseSpeedLevelY(0.5);
        }
    }
    
    private boolean isFurtherUpThanHelicopter()
    {
        return getMaxY() < getHelicopter().getMaxY();
    }
    
    private boolean isFurtherDownThanHelicopter()
    {
        return getMinY() > getHelicopter().getMinY();
    }
    
    protected void adaptSpeedLevelForKamikaze()
    {
        setSpeedLevelX(getKamikazeSpeedUpX());
    }
    
    protected double getKamikazeSpeedUpX()
    {
        return DEFAULT_KAMIKAZE_SPEED_UP_X;
    }
    
    
    private void evaluateShooting(GameRessourceProvider gameRessourceProvider)
    {
        // TODO if-Bedingung in mehrere(!) Methoden auslagern
        if(hasCanonReadyToFire()
            && !isEmpSlowed()
            && Calculations.tossUp(0.1f)
            && getX() + getWidth() > 0
            && !isCompletelyCloaked()
            && ((isFlyingLeft()
            && gameRessourceProvider.getHelicopter()
                                    .intersects(
                                        getX() + Integer.MIN_VALUE / 2f,
                                        getY() + (getModel() == EnemyModelType.TIT ? 0 : getWidth() / 2) - FIELD_OF_FIRE_TOLERANCE_Y,
                                        Integer.MAX_VALUE / 2f,
                                        EnemyMissile.DIAMETER + 2 * FIELD_OF_FIRE_TOLERANCE_Y))
            ||
            (isFlyingRight()
                && gameRessourceProvider.getHelicopter()
                                        .intersects(
                                            getX() + 0,
                                            getY() + (getModel() == EnemyModelType.TIT ? 0 : getWidth() / 2) - FIELD_OF_FIRE_TOLERANCE_Y,
                                            Integer.MAX_VALUE / 2f,
                                            EnemyMissile.DIAMETER + 2 * FIELD_OF_FIRE_TOLERANCE_Y))))
        {
            shoot(hasDeadlyShots() ? EnemyMissileType.BUSTER : EnemyMissileType.DISCHARGER,
                  shotSpeed + 3 * Math.random() + 5);
            
            shootTimer = shootingRate;
        }
        if(isCurrentlyShooting())
        {
            shootTimer--;
        }
    }
    
    protected boolean hasDeadlyShots()
    {
        return false;
    }
    
    
    public void shoot(EnemyMissileType missileType,
                      double missileSpeed)
    {
        EnemyMissile enemyMissile = getGameRessourceProvider().getNewPaintableEntityInstance(enemyMissileFactory);
        getGameRessourceProvider().getActivePaintableEntityManager()
                                  .getEnemyMissiles()
                                  .get(CollectionSubgroupType.ACTIVE)
                                  .add(enemyMissile);
        enemyMissile.launch(this, missileType, missileSpeed, shootingDirection);
        Audio.play(Audio.launch3);
    }
    
    
    protected void startBarrierUncloaking()
    {
        barrierTeleportTimer = 2 * CloakingDevice.BOOT_AND_FADE_TIME + shootingRate * shotsPerCycle;
        cloakingDevice.setToEndOfCloakedTime();
        placeNearHelicopter();
    }
    
    
    protected void sinusLoop()
    {
        speedLevel.setLocation(
            speedLevel.getX(),
            Math.max(4.0, 0.15f * (145 - Math.abs(getY() - 155))));
    }
    
    private void cloaking()
    {
        if(isReadyToContinueCloakingProcess())
        {
            cloakingDevice.run();
        }
        if(cloakingDevice.isUncloakingInProgress())
        {
            if(cloakingDevice.hasCloakingJustStartedFadingAway())
            {
                Audio.play(Audio.cloak);
            }
            else if(cloakingDevice.isShutDownCompleted())
            {
                uncloakAndResetCloakingDevice();
            }
        }
    }
    
    protected boolean isReadyToContinueCloakingProcess()
    {
        return true;
    }
    
    protected void uncloakAndResetCloakingDevice()
    {
        uncloakAndSetCloakingDeviceReadyForUse();
    }
    
    private void evaluateDodge()
    {
        dodgeTimer--;
        if(dodgeTimer == READY)
        {
            reachTargetSpeedLevel();
            navigationDevice.turnLeft();
        }
    }
    
    private void move()
    {
        if(!speed.equals(ZERO_SPEED) || Scenery.backgroundMoves)
        {
            setLocation(
                getX()
                    + navigationDevice.getDirectionX() * speed.getX()
                    - (Scenery.backgroundMoves ? SceneryObject.BG_SPEED : 0),
                Math.max(getModel() == EnemyModelType.BARRIER ? 0 : Integer.MIN_VALUE,
                         type == EnemyType.ROCK
                             ? getY()
                             : Math.min(canBePositionedBelowGround()
                                            ? Integer.MAX_VALUE
                                            : GROUND_Y - getHeight(),
                                        getY() + navigationDevice.getDirectionY() * speed.getY())));
        }
    }
    
    protected boolean canBePositionedBelowGround()
    {
        return isDestroyed();
    }
    
    private void calculateSpeedDead()
    {
        if(explodingTimer <= 0)
        {
            speed.setLocation(speedLevel);
        }
        else
        {
            explodingTimer--;
            speed.setLocation(speedLevel.getX(), 0);
            if(explodingTimer == 0)
            {
                explodingTimer = DISABLED;
            }
        }
    }
    
    private void calculateSpeed()
    {
        if(isStunned())
        {
            adjustSpeedTo(getHelicopter().missileDrive);
            if(stunningTimer == TIMER_ALMOST_OVER)
            {
                recoverFromBeingStunned();
            }
        }
        evaluateSpeedBoost();
        if(isRecoveringSpeed)
        {
            recoverSpeed();
        }
        
        speed.setLocation(speedLevel);
        
        if(isEmpSlowed())
        {
            // relevant, wenn mit der PEGASUS-Klasse gespielt wird
            speed.setLocation(
                speed.getX()
                    * ((double)(EMP_SLOW_TIME - empSlowedTimer) / EMP_SLOW_TIME),
                speed.getY()
                    * ((double)(EMP_SLOW_TIME - empSlowedTimer) / EMP_SLOW_TIME));
        }
        
        if(stoppingBarrier != null
            && burrowTimer == DISABLED
            && !(getModel() == EnemyModelType.BARRIER && type == EnemyType.BIG_BARRIER))
        {
            adjustSpeedToBarrier();
        }
    }
    
    protected void recoverFromBeingStunned()
    {
        isRecoveringSpeed = true;
    }
    
    protected boolean isEmpSlowed()
    {
        return empSlowedTimer > 0;
    }
    
    private void adjustSpeedTo(int missileDrive)
    {
        if(isMoving()
            &&
            (totalStunningTime - 13 == stunningTimer
                || getMaxX()
                + 18
                + missileDrive / 2f > GraphicsAdapter.VIRTUAL_DIMENSION.getWidth()
                + 2 * getWidth() / 3
                || getMinX()
                - 18
                - missileDrive / 2f < -2 * getWidth() / 3))
        {
            stopMoving();
        }
    }
    
    public boolean isMoving()
    {
        return !speedLevel.equals(ZERO_SPEED);
    }
    
    protected void evaluateSpeedBoost() {}
    
    
    private void recoverSpeed()
    {
        if(burrowTimer != DISABLED || hasReachedTargetSpeed())
        {
            isRecoveringSpeed = false;
            if(burrowTimer != DISABLED)
            {
                speedLevel.setLocation(SLOW_VERTICAL_SPEED);
            }
            else
            {
                reachTargetSpeedLevel();
            }
        }
        else if(!isTargetSpeedReachedOrExceededX())
        {
            increaseSpeedLevelX(0.025);
        }
        if(speedLevel.getY() < targetSpeedLevel.getY())
        {
            increaseSpeedLevelY(0.025);
        }
    }
    
    private boolean hasReachedTargetSpeed()
    {
        return isTargetSpeedReachedOrExceededX()
            && speedLevel.getY() >= targetSpeedLevel.getY();
    }
    
    private void adjustSpeedToBarrier()
    {
        if(hasSameDirectionX(stoppingBarrier)
            && stoppingBarrier.getCenterX() * navigationDevice.getDirectionX()
            < getCenterX() * navigationDevice.getDirectionX()
            && stoppingBarrier.speed.getX() > speed.getX())
        {
            speed.setLocation(stoppingBarrier.isOnScreen()
                                  && !isOnScreen()
                                  ? 0
                                  : stoppingBarrier.speed.getX(),
                              speed.getY());
        }
        else if(hasSameDirectionY(stoppingBarrier)
            && stoppingBarrier.getCenterY() * navigationDevice.getDirectionY()
            < getCenterY() * navigationDevice.getDirectionY()
            && stoppingBarrier.speed.getY() > speed.getY()
            && burrowTimer == DISABLED)
        {
            speed.setLocation(speed.getX(), stoppingBarrier.speed.getY());
            if(getHelicopter().tractor == this)
            {
                getHelicopter().stopTractor();
            }
        }
    }
    
    private boolean hasSameDirectionX(Enemy otherEnemy)
    {
        return otherEnemy.navigationDevice.getDirectionX() == navigationDevice.getDirectionX();
    }
    
    private boolean hasSameDirectionY(Enemy otherEnemy)
    {
        return otherEnemy.navigationDevice.getDirectionY() == navigationDevice.getDirectionY();
    }
    
    public void updateDead(Map<CollectionSubgroupType, Queue<Explosion>> explosions)
    {
        if(collisionDamageTimer > 0)
        {
            collisionDamageTimer--;
        }
        if(collisionTimer > 0)
        {
            collisionTimer--;
        }
        if(!hasCrashed
            && getMaxY() + speed.getY() >= crashPositionY)
        {
            handleCrashToTheGround(explosions);
        }
        calculateSpeedDead();
        move();
        if(getMaxX() < 0)
        {
            isMarkedForRemoval = true;
        }
        setPaintBounds();
    }
    
    private void handleCrashToTheGround(Map<CollectionSubgroupType, Queue<Explosion>> explosions)
    {
        hasCrashed = true;
        stopMoving();
        setY(crashPositionY - getHeight());
        if(type.isServant())
        {
            isMarkedForRemoval = true;
        }
        Audio.play(getCrashToTheGroundSound());
        Explosion.start(explosions,
                        getHelicopter(),
                        getCenterX(),
                        getCenterY(),
                        getExplosionType(),
                        isDetonatingExtraStrong());
    }
    
    protected boolean isDetonatingExtraStrong()
    {
        return false;
    }
    
    protected Clip getCrashToTheGroundSound()
    {
        return Audio.explosion3;
    }
    
    protected ExplosionType getExplosionType()
    {
        return ExplosionType.ORDINARY;
    }
    
    protected boolean isBoss()
    {
        return type.isMajorBoss();
    }
    
    public boolean isIntactBoss()
    {
        return isBoss() && isIntact();
    }
    
    public void collision(GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        boolean playCollisionSound = collisionTimer == READY;
        helicopter.beAffectedByCollisionWith(this, gameRessourceProvider, playCollisionSound);
        
        if(playCollisionSound)
        {
            collisionTimer = Helicopter.NO_COLLISION_DAMAGE_TIME;
        }
        collisionDamageTimer = Helicopter.NO_COLLISION_DAMAGE_TIME;
        
        if(isExplodingOnCollisions()
            && !isInvincible()
            && isIntact())
        {
            explode(gameRessourceProvider,
                    0,
                    getExplosionType(),
                    dealsExtraCollisionDamage());
            
            if(helicopter.canObtainCollisionReward()
                && grantsCollisionReward())
            {
                grantRewards(gameRessourceProvider, null, helicopter.hasPerformedTeleportKill());
            }
            destroyByHelicopter(gameRessourceProvider);
        }
        if(helicopter.isDestinedToCrash())
        {
            helicopter.crash();
        }
    }
    
    protected boolean dealsExtraCollisionDamage()
    {
        return false;
    }
    
    protected boolean grantsCollisionReward()
    {
        return true;
    }
    
    private void grantRewards(GameRessourceProvider gameRessourceProvider, Missile missile, boolean beamKill)
    {
        gameRessourceProvider.getHelicopter()
                             .receiveRewardFor(this, missile, beamKill);
        grantGeneralRewards(gameRessourceProvider);
    }
    
    public void reactToRadiation(GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        if(teleportTimer == READY)
        {
            teleport();
        }
        else if(canTakeCollisionDamage())
        {
            takeCollisionDamage(helicopter);
            if(hasHPsLeft())
            {
                // TODO übergeben von null ... Implementierung verbessern
                reactToHit(null);
            }
            else
            {
                boolean beamKill = helicopter.bonusKillsTimer > 0;
                dieFromRadiation(gameRessourceProvider, beamKill);
            }
        }
    }
    
    protected void takeCollisionDamage(Helicopter helicopter)
    {
        int damage = helicopter.calculateCollisionDamage();
        takeDamage(damage);
    }
    
    private boolean canTakeCollisionDamage()
    {
        return isIntact()
            && !isExplodingOnCollisions()
            && !isInvincible()
            && !(barrierTeleportTimer != DISABLED && barrierShootTimer == DISABLED)
            && collisionTimer == READY;
    }
    
    public float collisionDamage()
    {
        Helicopter helicopter = getHelicopter();
        return helicopter.getProtectionFactor()
            // TODO 0.65 und 1.0 in Konstanten auslagern
            * helicopter.getBaseProtectionFactor(isExplodingOnCollisions())
            * (helicopter.isTakingKaboomDamageFrom(this)
            ? helicopter.kaboomDamage()
            : (isExplodingOnCollisions() && !isInvincible() && isIntact())
            ? 1.0f
            : collisionDamageTimer > 0
            ? 0.0325f
            : 0.65f);
    }
    
    void takeDamage(int dmg)
    {
        hitPoints -= dmg;
        if(!hasHPsLeft())
        {
            hitPoints = 0;
        }
    }
    
    public void hitByMissile(GameRessourceProvider gameRessourceProvider, Missile missile)
    {
        gameRessourceProvider.getGameStatisticsCalculator()
                             .incrementHitCounter();
        // TODO Audio.playExplosionSound(boolean isGreatExplosion)
        if(missile.hasGreatExplosivePower())
        {
            Audio.play(Audio.explosion4);
        }
        else
        {
            Audio.play(Audio.explosion2);
        }
        missile.rememberHitting(this);
        takeDamage(missile.getDamageEffect());
        if(areStunningRequirementsMet(missile))
        {
            stun(gameRessourceProvider, missile);
        }
    }
    
    private boolean areStunningRequirementsMet(Missile missile)
    {
        return missile.isStunning()
            && isStunnable()
            && nonStunnableTimer == READY;
    }
    
    private void stun(GameRessourceProvider gameRessourceProvider, Missile missile)
    {
        if(hasHPsLeft())
        {
            Audio.play(Audio.stun);
        }
        explode(gameRessourceProvider, missile);
        nonStunnableTimer = (int)(type.isMainBoss() || type.isFinalBossServant()
            ? 2.25f * Events.level
            : 0);
        knockBackDirection = missile.getSpeed() > 0 ? 1 : -1;
        
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        // TODO in Methoden auslagern, Code verständlicher machen
        speedLevel.setLocation(
            (knockBackDirection == navigationDevice.getDirectionX() ? 1 : -1)
                * (type.isMainBoss() || type.isFinalBossServant()
                ? (10f + helicopter.missileDrive) / (Events.level / 10f)
                : 10f + helicopter.missileDrive),
            0);
        
        stunningTimer = totalStunningTime
            = (int)(17 + STUNNING_TIME_BASIS
            * (type.isMajorBoss() ? (10f / Events.level) : 2.5f));
        
        disableSiteEffects();
    }
    
    private void disableSiteEffects()
    {
        if(getHelicopter().tractor == this)
        {
            getHelicopter().stopTractor();
        }
        if(isUncloakingWhenDisabled())
        {
            uncloakAndSetCloakingDeviceReadyForUse();
        }
    }
    
    protected boolean isUncloakingWhenDisabled()
    {
        return cloakingDevice.isActive();
    }
    
    public void reactToHit(Missile missile)
    {
        if(isReadyToDodge())
        {
            dodge(missile);
        }
        if(canDoHitTriggeredTurn())
        {
            performHitTriggeredTurn();
        }
        
        if(canCloak())
        {
            Audio.play(Audio.cloak);
            cloakingDevice.activate();
        }
        if(missile != null
            && missile.isStunning()
            && cloakingDevice.isEnabled())
        {
            uncloakTriggeredByStunningMissile();
        }
        else if(prolongCloakingTimeWhenHitWhileCloaked())
        {
            cloakingDevice.setToStartOfCloakedTime();
        }
    }
    
    protected boolean canCloak()
    {
        return cloakingDevice.isReadyToBeUsed();
    }
    
    
    protected boolean prolongCloakingTimeWhenHitWhileCloaked()
    {
        return isCompletelyCloaked();
    }
    
    protected void uncloakTriggeredByStunningMissile()
    {
        uncloakAndSetCloakingDeviceReadyForUse();
    }
    
    protected void performHitTriggeredTurn()
    {
        if(getMinX() > getHelicopter().getMinX())
        {
            navigationDevice.turnLeft();
        }
        else if(getMaxX() < getHelicopter().getMaxX())
        {
            navigationDevice.turnRight();
        }
    }
    
    private boolean canDoHitTriggeredTurn()
    {
        return canInstantlyTurnAround
            || canTurn
            && !isAbleToTurnAroundEarly
            && Calculations.tossUp(getTurnProbability());
    }
    
    protected float getTurnProbability()
    {
        return DEFAULT_TURN_PROBABILITY;
    }
    
    public void explode(GameRessourceProvider gameRessourceProvider)
    {
        explode(gameRessourceProvider, 0, ExplosionType.ORDINARY, false);
    }
    
    private void explode(GameRessourceProvider gameRessourceProvider, Missile missile)
    {
        explode(gameRessourceProvider, missile.getSpeed(), missile.getTypeOfExplosion(), missile.inflictsExtraDamage());
    }
    
    private void explode(GameRessourceProvider gameRessourceProvider,
                         double missileSpeed,
                         ExplosionType explosionType,
                         boolean extraDamage)
    {
        // TODO refactoring
        if(explodingTimer == 0)
        {
            explodingTimer = 7;
        }
        Explosion.start(gameRessourceProvider.getActivePaintableEntityManager()
                                             .getExplosions(),
                        gameRessourceProvider.getHelicopter(),
                        getX() + ((explosionType != ExplosionType.EMP && getModel() != EnemyModelType.BARRIER)
                            ? (missileSpeed < 0 ? 2 : 1) * getWidth() / 3
                            : getWidth() / 2),
                        getY() + getHeight() / 2,
                        explosionType,
                        extraDamage);
    }
    
    public void destroyByHelicopter(GameRessourceProvider gameRessourceProvider)
    {
        writeDestructionStatistics(gameRessourceProvider.getGameStatisticsCalculator());
        beDestroyed();
    }
    
    protected void writeDestructionStatistics(GameStatisticsCalculator gameStatisticsCalculator)
    {
        gameStatisticsCalculator.incrementNumberOfEnemiesKilled();
    }
    
    void destroyByCrash(GameRessourceProvider gameRessourceProvider)
    {
        evaluatePowerUpDrop(gameRessourceProvider);
        beDestroyed();
    }
    
    private void evaluatePowerUpDrop(GameRessourceProvider gameRessourceProvider)
    {
        if(areALlRequirementsForPowerUpDropMet())
        {
            dropRandomPowerUp(gameRessourceProvider);
        }
    }
    
    private void beDestroyed()
    {
        isDestroyed = true;
        if(cloakingDevice.isActive())
        {
            uncloakAndDisableCloakingDevice();
        }
        teleportTimer = DISABLED;
        
        adjustBrightnessOfColors(Colorations.DESTRUCTION_DIM_FACTOR);
        repaint();
        
        if(getHelicopter().tractor == this)
        {
            getHelicopter().stopTractor();
        }
        speedLevel.setLocation(0, 12);
        navigationDevice.flyDown();
        
        empSlowedTimer = READY;
        crashPositionY = (int)(isIntersectingGroundLine()
            ? getMaxY()
            : GROUND_Y + 1 + Calculations.random() * 0.25 * getHeight());
    }
    
    private void adjustBrightnessOfColors(float dimFactor)
    {
        primaryColor = Colorations.adjustBrightness(primaryColor, dimFactor);
        secondaryColor = Colorations.adjustBrightness(secondaryColor, dimFactor);
    }
    
    private void uncloakAndSetCloakingDeviceReadyForUse()
    {
        uncloak();
        cloakingDevice.setReadyForUse();
    }
    
    protected void uncloakAndDisableCloakingDevice()
    {
        uncloak();
        cloakingDevice.disable();
    }
    
    private void uncloak()
    {
        primaryColor = Colorations.setAlpha(primaryColor, Colorations.MAX_OPACITY);
        secondaryColor = Colorations.setAlpha(secondaryColor, Colorations.MAX_OPACITY);
    }
    
    // TODO null und false sollten keine Eingabeargumente sein, hier die Implementierung anpassen
    public void dieFromEmpWave(GameRessourceProvider gameRessourceProvider)
    {
        die(gameRessourceProvider, null, false);
    }
    
    public void dieByMissile(GameRessourceProvider gameRessourceProvider, Missile missile)
    {
        die(gameRessourceProvider, missile, false);
    }
    
    public void dieFromRadiation(GameRessourceProvider gameRessourceProvider, boolean beamKill)
    {
        die(gameRessourceProvider, null, beamKill);
    }
    
    public void die(GameRessourceProvider gameRessourceProvider, Missile missile, boolean beamKill)
    {
        grantRewards(gameRessourceProvider, missile, beamKill);
        destroyByHelicopter(gameRessourceProvider);
        
        if(cloakingDevice.isEnabled())
        {
            Audio.play(Audio.cloak);
        }
        
        if(missile == null)
        {
            explode(gameRessourceProvider);
        }
        else if(!missile.isStunning())
        {
            explode(gameRessourceProvider, missile);
        }
        
        evaluateBossDestructionEffect(gameRessourceProvider);
        if(missile != null)
        {
            missile.forgetHitting(this);
        }
    }
    
    public boolean canCountForKillsAfterLevelUp()
    {
        return true;
    }
    
    public boolean areALlRequirementsForPowerUpDropMet()
    {
        return false;
    }
    
    public void dropRandomPowerUp(GameRessourceProvider gameRessourceProvider)
    {
        PowerUp.activateInstance(gameRessourceProvider, this);
    }
    
    public PowerUpType getTypeOfRandomlyDroppedPowerUp()
    {
        return Calculations.tossUp(REPARATION_POWER_UP_DROP_RATE)
            ? PowerUpType.REPARATION
            : PowerUpType.getValues()
                         .get(getRandomIndexOfDroppablePowerUp());
    }
    
    private int getRandomIndexOfDroppablePowerUp()
    {
        return Calculations.random(getMaximumNumberOfDifferentDroppablePowerUps());
    }
    
    private int getMaximumNumberOfDifferentDroppablePowerUps()
    {
        // major bosses are not supposed to drop bonus income powerUps
        int indexReductionValue = type.isMajorBoss() ? 1 : 0;
        return PowerUpType.valueCount() - indexReductionValue;
    }
    
    public int calculateReward()
    {
        return getHelicopter().getBonusFactor() * getEffectiveStrength() + getRewardModifier();
    }
    
    protected int getRewardModifier()
    {
        return 5 - Calculations.random(11);
    }
    
    public int getEffectiveStrength()
    {
        return type.getStrength() * getRewardFactor();
    }
    
    protected int getRewardFactor()
    {
        return STANDARD_REWARD_FACTOR;
    }
    
    // TODO sobald die Voraussetzungen dafür hergestellt sind, sollte diese Methode als abstrakte Methode in die Klasse Standard-Enemy wandern
    protected abstract void evaluateBossDestructionEffect(GameRessourceProvider gameRessourceProvider);
    
    public void dodge(Missile missile)
    {
        doTypeSpecificDodgeActions(missile);
        if(getY() > 143)
        {
            navigationDevice.flyUp();
        }
        else
        {
            navigationDevice.flyDown();
        }
    }
    
    protected void doTypeSpecificDodgeActions(Missile missile)
    {
        if(isShooter() || canChaosSpeedup)
        {
            setSpeedLevelY(8.5);
            dodgeTimer = 13;
        }
        else
        {
            if(isFlyingTowardsMissile(missile) && hasEnoughDistanceFromScreenBordersToDodgeAway())
            {
                navigationDevice.turnAround();
            }
            speedLevel.setLocation(6, 6);
            dodgeTimer = 16;
        }
    }
    
    private boolean hasEnoughDistanceFromScreenBordersToDodgeAway()
    {
        return (isFlyingLeft() && getMaxX() < DODGE_BORDER_DISTANCE_RIGHT)
            || (isFlyingRight() && getMaxX() > DODGE_BORDER_DISTANCE_LEFT);
    }
    
    private boolean isFlyingTowardsMissile(Missile missile)
    {
        return (missile.isFlyingRight() && isFlyingLeft())
            || (missile.isFlyingLeft() && isFlyingRight());
    }
    
    
    public void teleport()
    {
        Audio.play(Audio.teleport2);
        setLocation(260.0 + Math.random() * (660.0 - getWidth()),
                    20.0 + Math.random() * (270.0 - getHeight()));
        stopMoving();
        teleportTimer = 60;
        invincibleTimer = 40;
    }
    
    public boolean isHittable(Missile missile)
    {
        return isIntact()
            && !(barrierTeleportTimer != DISABLED && getAlpha() != 255) // in die Enemy-Unterklassen auslagern
            && missile.intersects(this)
            && !missile.hasHit(this);
    }
    
    public boolean isReadyToDodge()
    {
        return canDodge
            && dodgeTimer == READY
            && !isEmpSlowed()
            && isIntact()
            && !(isWithinKamikazeRange()
            && canKamikaze
            && isFlyingLeft());
    }
    
    private boolean isWithinKamikazeRange()
    {
        return (getHelicopter().getX() - getMaxX() > -500)
            && (getHelicopter().getX() - getMinX() < 150);
    }
    
    public boolean isInvincible()
    {
        return invincibleTimer > 0 || shield > 0;
    }
    
    public void evaluatePosAdaption()
    {
        if(getHelicopter().isLocationAdaptionApproved(this))
        {
            hasUnresolvedIntersection = true;
        }
        else
        {
            hasUnresolvedIntersection = false;
            if(!isTouchingHelicopter && touchedSite != lastTouchedSite)
            {
                Audio.play(Audio.landing);
                isTouchingHelicopter = true;
            }
        }
    }
    
    public boolean hasHPsLeft()
    {
        return hitPoints >= 1;
    }
    
    public void setTouchedSiteToRight()
    {
        touchedSite = BarrierPositionType.RIGHT;
    }
    
    public void setTouchedSiteToLeft()
    {
        touchedSite = BarrierPositionType.LEFT;
    }
    
    public void setTouchedSiteToBottom()
    {
        touchedSite = BarrierPositionType.BOTTOM;
    }
    
    public void setTouchedSiteToTop()
    {
        touchedSite = BarrierPositionType.TOP;
    }
    
    public void setUntouched()
    {
        touchedSite = BarrierPositionType.NONE;
    }
    
    public boolean isUntouched()
    {
        return touchedSite == BarrierPositionType.NONE;
    }
    
    public boolean isKaboomDamageDealer()
    {
        return false;
    }
    
    public void grantGeneralRewards(GameRessourceProvider gameRessourceProvider)
    {
        if(canCountForKillsAfterLevelUp())
        {
            Events.killsAfterLevelUp++;
        }
        evaluatePowerUpDrop(gameRessourceProvider);
    }
    
    public boolean isRock()
    {
        return type == EnemyType.ROCK;
    }
    
    public int getBounty()
    {
        return type.getBounty();
    }
    
    public boolean isDestroyed()
    {
        return isDestroyed;
    }
    
    public boolean isIntact()
    {
        return !isDestroyed();
    }
    
    @Override
    public Point2D getSpeedLevel()
    {
        return speedLevel;
    }
    
    public int getLifetime()
    {
        return lifetime;
    }
    
    public BufferedImage getStandardImage()
    {
        return image[getImageBaseIndex()];
    }
    
    public BufferedImage getCloakedImage()
    {
        return image[getCloakedImageIndex()];
    }
    
    private int getImageBaseIndex()
    {
        return isFlyingLeft() ? 0 : 1;
    }
    
    private int getCloakedImageIndex()
    {
        return getImageBaseIndex() + 2;
    }
    
    public int getRotorColor()
    {
        return rotorColor;
    }
    
    public int getBarrierShootTimer()
    {
        return barrierShootTimer;
    }
    
    public int getShotsPerCycle()
    {
        return shotsPerCycle;
    }
    
    public int getShootingRate()
    {
        return shootingRate;
    }
    
    public boolean isClockwiseBarrier()
    {
        return isClockwiseBarrier;
    }
    
    protected final double getOnTheGroundY()
    {
        return GROUND_Y - getHeight();
    }
    
    public final EnemyModelType getModel()
    {
        return type.getModel();
    }
    
    public boolean isDisappearingAfterEnteringRepairShop()
    {
        return true;
    }
    
    // bestimmt, ob ein Gegner von Stopp-Raketen (Orochi-Klasse) oder EMP-Schockwellen (Pegasus) "betäubt" werden kann
    public boolean isStunnable()
    {
        return true;
    }
    
    protected boolean isExplodingOnCollisions()
    {
        return type.isExplodingOnCollisions();
    }
    
    public boolean isPushingHelicopter(Helicopter helicopter)
    {
        return intersects(helicopter) && getAlpha() == 255 && burrowTimer != 0;
    }
    
    public boolean canCollide()
    {
        return true;
    }
    
    public Color getBarColor(boolean isImagePaint)
    {
        if(isDestroyed())
        {
            return getDefaultBarColor();
        }
        if(qualifiesForBlinkingCannon())
        {
            return Colorations.variableGreen;
        }
        if(!isImagePaint && isInvincible())
        {
            return Color.green;
        }
        return getDefaultBarColor();
    }
    
    public boolean hasBlinkingCannon()
    {
        return isIntact() && qualifiesForBlinkingCannon();
    }
    
    private boolean qualifiesForBlinkingCannon()
    {
        return getShootTimer() > 0 || generatesEnergieBeam();
    }
    
    protected boolean generatesEnergieBeam()
    {
        return false;
    }
    
    protected Color getDefaultBarColor()
    {
        return Colorations.enemyGray;
    }
    
    public Color getInactiveNozzleColor()
    {
        return Colorations.INACTIVE_NOZZLE;
    }
    
    // Methoden für Richtungsänderungen und -abfragen
    // TODO dies sollte ggf. in eigene Klasse ausgelagert werden, nur die Methoden, die außerhalb von Enemy genutzt werden müssen weitergeleitet werden z.B NavigationDevice
    
    @Override
    public void increaseSpeedLevelX(double increment)
    {
        setSpeedLevelX(speedLevel.getX() + increment);
    }
    
    protected void setSpeedLevelToZeroX()
    {
        setSpeedLevelX(0);
    }
    
    public void setSpeedLevelX(double x)
    {
        double y = speedLevel.getY();
        speedLevel.setLocation(x, y);
    }
    
    protected void increaseSpeedLevelY(double increment)
    {
        setSpeedLevelY(speedLevel.getY() + increment);
    }
    
    private void setSpeedLevelToZeroY()
    {
        setSpeedLevelY(0);
    }
    
    public void setSpeedLevelY(double y)
    {
        double x = speedLevel.getX();
        speedLevel.setLocation(x, y);
    }
    
    @Override
    public PaintableEntityGroupType getGroupType()
    {
        return PaintableEntityGroupType.ENEMY;
    }
    
    protected boolean isMovingAwayFromHelicopter()
    {
        return (isLeftOfHelicopter() && isFlyingLeft())
            || (isRightOfHelicopter() && isFlyingRight());
    }
    
    private boolean isLeftOfHelicopter()
    {
        return isLeftOf(getHelicopter());
    }
    
    private boolean isRightOfHelicopter()
    {
        return isRightOf(getHelicopter());
    }
    
    private boolean isWithinKamikazeRangeOfHelicopter()
    {
        return isWithinKamikazeRangeOf(getHelicopter());
    }
    
    private boolean isWithinKamikazeRangeOf(RectangularPaintableEntity paintableEntity)
    {
        return getDistanceOfMaxX(paintableEntity) < KAMIKAZE_RANGE
            && ((!isLeftOf(paintableEntity) && isFlyingLeft())
            || (!isRightOf(paintableEntity) && isFlyingRight()));
    }
    
    private double getDistanceOfMaxX(RectangularPaintableEntity paintableEntity)
    {
        return Math.abs(getMaxX() - paintableEntity.getMaxX());
    }
    
    protected void setCloakingDeviceReadyForUse()
    {
        cloakingDevice.setReadyForUse();
    }
    
    protected CloakingDevice getCloakingDevice()
    {
        return cloakingDevice;
    }
    
    protected double getSpeedY()
    {
        return speed.getY();
    }
    
    public int getAlpha()
    {
        return cloakingDevice.getAlpha();
    }
    
    public EnemyType getType()
    {
        return type;
    }
    
    protected void stopMoving()
    {
        getSpeedLevel().setLocation(ZERO_SPEED);
    }
    
    protected void reachTargetSpeedLevel()
    {
        getSpeedLevel().setLocation(targetSpeedLevel);
    }
    
    protected final ManeuverManger getManeuverManger()
    {
        return maneuverManger;
    }
    
    public Color getPrimaryColor()
    {
        return primaryColor;
    }
    
    public Color getSecondaryColor()
    {
        return secondaryColor;
    }
    
    public boolean hasCrashed()
    {
        return hasCrashed;
    }
}