package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.control.events.MouseEvent;
import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.enemy.basic.CapturingEnemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.helicopter.components.Battery;
import de.helicopter_vs_aliens.model.helicopter.components.PowerUpController;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.score.ScoreScreenTimes;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import javax.sound.sampled.Clip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.ROCH;


public abstract class Helicopter extends RectangularGameEntity
// TODO Klasse zerschlagen
{
    // TODO alles public fields genau prüfen, ob sie public sein müssen, und wenn ja mit Setter-Methoden arbeiten, sonst private
    public static final int
        POWER_UP_DURATION = 900;
    
    public static final int
        POWER_UP_FADE_TIME = POWER_UP_DURATION / 4;
    
    public static final int
        NO_COLLISION_DAMAGE_TIME = 20;
    
    public static final int
        INVULNERABILITY_DAMAGE_REDUCTION = 80;
    
    public static final int
        STANDARD_SPECIAL_COSTS = 125000;
    
    public static final int
        CHEAP_SPECIAL_COSTS = 10000;
    
    public static final double
        FOCAL_POINT_X_LEFT = 39,
        FOCAL_POINT_X_RIGHT = 83,
        FOCAL_POINT_Y_EXP = 44,
        FOCAL_PNT_Y_POS = 56;
    
    static final int
        GOLIATH_PLATING_STRENGTH = 2,
        STANDARD_GOLIATH_COSTS = 75000,
        NO_COLLISION_HEIGHT = 6;
    
    static final float
        ENEMY_MISSILE_DAMAGE_FACTOR = 0.5f,
        STANDARD_MISSILE_DAMAGE_FACTOR = 1.0f;
    
    private static final int
        RECENT_DAMAGE_TIME = 50,        // Zeitrate in der die Lebenspunktleiste nach Kollisionen blinkt
        SLOW_TIME = 100,
        FIRE_RATE_POWERUP_LEVEL = 3,    // so vielen zusätzlichen Upgrades der Feuerrate entspricht die temporäre Steigerung der Feuerrate durch das entsprechende PowerUp
        STATIC_CHARGE_ENERGY_DRAIN = 45,              // Energieabzug für den Helikopter bei Treffer
        STANDARD_PLATING_STRENGTH = 1,
        SLOW_ROTATIONAL_SPEED = 7,
        FAST_ROTATIONAL_SPEED = 12,
        DAY_BONUS_FACTOR = 60,
        NIGHT_BONUS_FACTOR = 90,
        SPOTLIGHT_COSTS = 35000;
    
    private static final float
        NOSEDIVE_SPEED = 12f,            // Geschwindigkeit des Helikopters bei Absturz
        INVULNERABILITY_PROTECTION_FACTOR = 1.0f - INVULNERABILITY_DAMAGE_REDUCTION / 100.0f,
        STANDARD_PROTECTION_FACTOR = 1.0f,
        STANDARD_BASE_PROTECTION_FACTOR = 1.0f,
        PLATING_MULTIPLIER = 1.3f;
    
    public static final Point
        HELICOPTER_MENU_PAINT_POS = new Point(692, 360);
    
    private static final Dimension
        HELICOPTER_SIZE = new Dimension(122, 69);
    
    private static final Rectangle
        INITIAL_BOUNDS = new Rectangle(150,
                                       GROUND_Y
                                           - HELICOPTER_SIZE.height
                                           - NO_COLLISION_HEIGHT,
                                       HELICOPTER_SIZE.width,
                                       HELICOPTER_SIZE.height);
    
    public int
        missileDrive,                        // Geschwindigkeit [Pixel pro Frame] der Raketen
        currentBaseFirepower,                // aktuelle Feuerkraft unter Berücksichtigung des Upgrade-Levels und des eventuell erforschten Jumbo-Raketen-Spezial-Upgrades
        platingDurabilityFactor = STANDARD_PLATING_STRENGTH,    // SpezialUpgrade; = 2, wenn erforscht, sonst = 1; Faktor, der die Standardpanzerung erhöht
        numberOfCannons = 1,                // Anzahl der Kanonen; mögliche Werte: 1, 2 und 3
        recentDamageTimer;                    // aktiv, wenn Helicopter kürzlich Schaden genommen hat; für Animation der HitPoint-Leiste
    
    // für die Spielstatistik
    
    
    private final Map<StandardUpgradeType, Integer>
        levelsOfStandardUpgrades = new EnumMap<>(StandardUpgradeType.class);  // Upgrade-Level aller 6 StandardUpgrades
    
    public ScoreScreenTimes
        scoreScreenTimes = new ScoreScreenTimes();    // Zeit, die bis zum Besiegen jedes einzelnen der 5 Boss-Gegner vergangen ist
    
    public float
        rotorSystem;                        // legt die aktuelle Geschwindigkeit des Helikopters fest
    
    public int
        rotorPosition;                        // Stellung des Helikopter-Hauptrotors für alle Klassen; genutzt für die StartScreen-Animation
    
    float
        spellCosts;                            // Energiekosten für die Nutzung des Energie-Upgrades
    
    public boolean
        hasSpotlights,                        // = true: Helikopter hat Scheinwerfer
        hasPiercingWarheads,                // = true: Helikopter-Raketen werden mit Durchstoß-Sprengköpfen bestückt
        isActive,                            // = false: Helikopter ist nicht in Bewegung und kann auch nicht starten, Raketen abschießen, etc. (vor dem ersten Start oder nach Absturz = false)
        isDamaged,                            // = true: Helikopter hat einen Totalschaden erlitten
    // TODO kann eventuell genutzt werden, um Malen des Helicopters und Drehen des Propellers zu trennen
    isRotorSystemActive,                // = true: Propeller dreht sich / Helikopter fliegt
        isContinuousFireEnabled,            // = true: Dauerfeuer aktiv
        isMovingLeft,
        isPlayedWithCheats = true;            // = true: Spielstand kann in die Highscore übernommen werden, da keine cheats angewendet wurden
    
    public final Point
        destination = new Point();                // dorthin fliegt der Helikopter
    
    // TODO noch Phoenix auslagern
    public final Point
        priorTeleportLocation = new Point();    // nur für Phönix-Klasse: Aufenthaltsort vor Teleportation
    
    public boolean
        isSearchingForTeleportDestination;        // = true: es wird gerade der Zielort der Teleportation ausgewählt
    
    public int
        // nur für Phönix- und Kamaitachi-Klasse
        // TODO auslagern in Phönix- und Kamaitachi-Klasse
        bonusKills,                            // Anzahl der Kills, für den aktuellen MultiKill-Award
        bonusKillsMoney,                    // Gesamtverdienst am Abschuss aller Gegner innerhalb des aktuellen MultiKill-Awards ohne Bonus
        bonusKillsTimer;                    // reguliert die Zeit, innerhalb welcher Kills für den MultiKill-Award berücksichtigt werden
    
    final Battery
        battery = Battery.createFor(getType());
    
    final PowerUpController
        powerUpController = new PowerUpController(this);
    
    public final Point2D
        location = new Point2D.Float();            // exakter Aufenthaltsort
    
    final Point2D
        nextLocation = new Point2D.Float();
    
    public CapturingEnemy
        tractor;            // Referenz auf den Gegner, der den Helikopter mit einem Traktorstrahl festhält
    
    private int
        fireRateTimer,    // reguliert die Zeit [frames], die mind. vergehen muss, bis wieder geschossen werden kann
        timeBetweenTwoShots,// Zeit [frames], die mindestens verstreichen muss, bis wieder geschossen werden kann
        slowedTimer;        // reguliert die Verlangsamung des Helicopters durch gegnerische Geschosse
    
    private float
        speed,                // aktuelle Geschwindigkeit des Helikopters
        currentPlating;        // aktuelle Panzerung (immer <= maximale Panzerung)
    
    private boolean
        isCrashing;            // Helikopter befindet sich im Sturzflug
    
    protected Helicopter()
    {
        paintBounds.setSize(HELICOPTER_SIZE);
        powerUpController.turnOfAllBoosters();
    }
    
    public void update(GameRessourceProvider gameRessourceProvider)
    {
        updateTimer();
        if(canRegenerateEnergy())
        {
            battery.recharge();
        }
        evaluateFire(gameRessourceProvider);
        move(gameRessourceProvider);
    }
    
    public boolean hasSpotlightsTurnedOn()
    {
        return hasSpotlights
            && Events.timeOfDay == TimeOfDay.NIGHT
            && WindowManager.window == WindowType.GAME;
    }
    
    void updateTimer()
    {
        if(recentDamageTimer > 0)
        {
            recentDamageTimer--;
        }
        if(slowedTimer > 0)
        {
            slowedTimer--;
        }
        powerUpController.evaluatePowerUpActivationStates();
    }
    
    private void evaluateFire(GameRessourceProvider gameRessourceProvider)
    {
        if(isReadyForShooting())
        {
            shoot(gameRessourceProvider);
        }
        fireRateTimer++;
    }
    
    private boolean isReadyForShooting()
    {
        return isContinuousFireEnabled
            && !isDamaged
            && !isOnTheGround()
            && fireRateTimer >= timeBetweenTwoShots;
    }
    
    void shoot(GameRessourceProvider gameRessourceProvider)
    {
        // TODO Code Duplizierungen auflösen
        if(hasPiercingWarheads)
        {
            Audio.play(Audio.launch2);
        }
        else
        {
            Audio.play(Audio.launch1);
        }
        fireRateTimer = 0;
        gameRessourceProvider.getGameStatisticsCalculator()
                             .incrementMissileCounterBy(numberOfCannons);
        
        boolean stunningMissile = isShootingStunningMissile();
        Missile sister = null;
        
        Map<CollectionSubgroupType, Queue<Missile>> missiles = gameRessourceProvider.getActiveGameEntityManager()
                                                                                    .getMissiles();
        if(numberOfCannons >= 1)
        {
            Iterator<Missile> iterator = missiles.get(CollectionSubgroupType.INACTIVE)
                                                 .iterator();
            Missile missile;
            if(iterator.hasNext())
            {
                missile = iterator.next();
                iterator.remove();
            }
            else
            {
                missile = new Missile();
            }
            if(getType() == ROCH || getType() == OROCHI)
            {
                missile.sister[0] = null;
                missile.sister[1] = null;
                sister = missile;
            }
            missiles.get(CollectionSubgroupType.ACTIVE)
                    .add(missile);
            missile.launch(this, stunningMissile, 56);
        }
        if(numberOfCannons >= 2)
        {
            Iterator<Missile> iterator = missiles.get(CollectionSubgroupType.INACTIVE)
                                                 .iterator();
            Missile missile;
            if(iterator.hasNext())
            {
                missile = iterator.next();
                iterator.remove();
            }
            else
            {
                missile = new Missile();
            }
            // TODO warum immer true
            if(sister != null && sister.sister != null &&
                (getType() == ROCH || getType() == OROCHI))
            {
                missile.sister[0] = sister;
                missile.sister[1] = null;
                sister.sister[0] = missile;
                sister = missile;
            }
            missiles.get(CollectionSubgroupType.ACTIVE)
                    .add(missile);
            missile.launch(this, stunningMissile, 28);
        }
        if(numberOfCannons >= 3)
        {
            Iterator<Missile> iterator = missiles.get(CollectionSubgroupType.INACTIVE)
                                                 .iterator();
            Missile missile;
            if(iterator.hasNext())
            {
                missile = iterator.next();
                iterator.remove();
            }
            else
            {
                missile = new Missile();
            }
            // TODO warum immer true
            if(sister != null && sister.sister != null &&
                (getType() == ROCH || getType() == OROCHI))
            {
                missile.sister[0] = sister.sister[0];
                missile.sister[1] = sister;
                sister.sister[0].sister[1] = missile;
                sister.sister[1] = missile;
            }
            missiles.get(CollectionSubgroupType.ACTIVE)
                    .add(missile);
            missile.launch(this, stunningMissile, 42);
        }
    }
    
    private void move(GameRessourceProvider gameRessourceProvider)
    {
        if(isOnTheGround())
        {
            isRotorSystemActive = false;
        }
        
        float
            nextX = (float)location.getX(),
            nextY = (float)location.getY();
        
        if(isCrashing)
        {
            nextY += NOSEDIVE_SPEED;
        }
        else if(isActive && tractor == null)
        {
            speed = (slowedTimer > 0) ? 1.5f : rotorSystem;
            float fraction = (float)(speed / location.distance(destination.x, destination.y));
            
            if(fraction < 1)
            {
                if(!(getMaxY() + NO_COLLISION_HEIGHT >= GROUND_Y
                    && destination.y >= GROUND_Y))
                {
                    nextX += (float)(fraction * (destination.x - location.getX()) - 1);
                }
                nextY += (float)(fraction * (destination.y - location.getY()));
            }
            else
            {
                nextX = destination.x;
                nextY = destination.y;
            }
        }
        
        boolean isInTheAir = location.getY() != 407d;
        float lastX = (float)location.getX();
        
        nextLocation.setLocation(nextX, nextY);
        correctAndSetCoordinates();
        
        if(EnemyController.currentNumberOfBarriers > 0 && !isDamaged)
        {
            for(int i = 0; i < EnemyController.currentNumberOfBarriers; i++)
            {
                Enemy enemy = EnemyController.livingBarrier[i];
                enemy.lastTouchedSite = enemy.touchedSite;
                if(isLocationAdaptionApproved(enemy))
                {
                    adaptPosTo(enemy);
                    correctAndSetCoordinates();
                    enemy.performLocationAdaptionAction(gameRessourceProvider);
                }
                else
                {
                    enemy.setUntouched();
                }
                if(enemy.isUntouched())
                {
                    enemy.untouchedCounter++;
                    if(enemy.untouchedCounter > 2)
                    {
                        enemy.untouchedCounter = 0;
                        enemy.isTouchingHelicopter = false;
                    }
                }
                else
                {
                    enemy.untouchedCounter = 0;
                }
            }
            for(int i = 0; i < EnemyController.currentNumberOfBarriers; i++)
            {
                EnemyController.livingBarrier[i].evaluatePosAdaption();
            }
        }
        
        if(isActive && tractor == null)
        {
            if(!isCrashing)
            {
                if(getMaxY() + NO_COLLISION_HEIGHT != GROUND_Y
                    || lastX != (float)location.getX())
                {
                    isRotorSystemActive = true;
                }
                if(isInTheAir && !(location.getY() != 407d))
                {
                    Audio.play(Audio.landing);
                }
            }
            else if(isInTheAir && location.getY() == 407d)
            {
                crashed(gameRessourceProvider.getActiveGameEntityManager()
                                             .getExplosions());
            }
        }
        if(isRotorSystemActive)
        {
            rotatePropellerFast();
        }
        setPaintBounds();
    }
    
    boolean isShootingStunningMissile()
    {
        return false;
    }
    
    public boolean isLocationAdaptionApproved(Enemy enemy)
    {
        return enemy.isPushingHelicopter(this);
    }
    
    void adaptPosTo(Enemy enemy)
    {
        double
            x = getCenterX() - enemy.getCenterX(),
            y = getCenterY() - enemy.getCenterY(),
            pseudoAngle = (x / Calculations.ZERO_POINT.distance(x, y)),
            distance,
            localSpeed = enemy.hasUnresolvedIntersection ? speed : Double.MAX_VALUE;
        
        if(pseudoAngle > Calculations.ROOT05)
        {
            // Right
            // new pos x: enemy.getMaxX() + (moves_left ? 39 : 83)
            distance = enemy.getMaxX() + (isMovingLeft ? 39 : 83) - location.getX();
            nextLocation.setLocation(
                location.getX() + Math.min(distance, localSpeed),
                location.getY());
            enemy.setTouchedSiteToRight();
        }
        else if(pseudoAngle < -Calculations.ROOT05)
        {
            // Left
            // new pos x: enemy.x - getWidth() + (moves_left ? 39 : 83)
            distance = location.getX() - enemy.getX() + getWidth() - (isMovingLeft ? 39 : 83);
            nextLocation.setLocation(
                location.getX() - Math.min(distance, localSpeed),
                location.getY());
            enemy.setTouchedSiteToLeft();
        }
        else
        {
            if(getCenterY() > enemy.getCenterY())
            {
                // Bottom
                // new pos y: enemy.getMaxY() + 56
                distance = enemy.getMaxY() + 56 - location.getY();
                nextLocation.setLocation(
                    location.getX(),
                    location.getY() + Math.min(distance, localSpeed));
                enemy.setTouchedSiteToBottom();
            }
            else
            {
                // Top
                // new pos y: enemy.getY() - getHeight() + 56
                distance = location.getY() - enemy.getY() + getHeight() - 56;
                nextLocation.setLocation(
                    location.getX(),
                    location.getY() - Math.min(distance, localSpeed));
                enemy.setTouchedSiteToTop();
            }
            if(tractor != null)
            {
                stopTractor();
            }
        }
    }
    
    void correctAndSetCoordinates()
    {
        location.setLocation
                    (
                        Math.max(40, Math.min(1024, nextLocation.getX())),
                        Math.max(32, Math.min(407, nextLocation.getY()))
                    );
        setBounds();
    }
    
    // TODO in Methoden auslagern
    void setBounds()
    {
        setBounds(location.getX()
                      - (isMovingLeft
                      ? FOCAL_POINT_X_LEFT
                      : FOCAL_POINT_X_RIGHT),
                  location.getY() - FOCAL_PNT_Y_POS,
                  getWidth(),
                  getHeight());
    }
    
    void initializeForNewGame()
    {
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            setUpgradeLevelOf(standardUpgradeType, getType().getInitialUpgradeLevelFor(standardUpgradeType));
        }
        restorePlating();
        battery.restore();
        generalInitialization();
    }
    
    void initializeFromSavegame(Savegame savegame)
    {
        restoreLastGameState(savegame);
        generalInitialization();
    }
    
    void generalInitialization()
    {
        setSpellCosts();
        fireRateTimer = timeBetweenTwoShots;
        placeAtStartpos();
        prepareForMission();
    }
    
    private void restoreLastGameState(Savegame savegame)
    {
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            Integer upgradeLevel = savegame.levelsOfStandardUpgrades.get(standardUpgradeType);
            setUpgradeLevelOf(standardUpgradeType, upgradeLevel);
        }
        hasSpotlights = savegame.spotlight;
        platingDurabilityFactor = savegame.platingDurabilityFactor;
        hasPiercingWarheads = savegame.hasPiercingWarheads;
        numberOfCannons = savegame.numberOfCannons;
        currentPlating = savegame.currentPlating;
        
        battery.upgradeTo(getUpgradeLevelOf(StandardUpgradeType.ENERGY_ABILITY));
        battery.setCurrentCharge(savegame.currentEnergy);
        
        if(savegame.hasFifthSpecial)
        {
            obtainFifthSpecial();
        }
        
        GameResources.getProvider()
                     .getGameStatisticsCalculator()
                     .restoreFrom(savegame);
        
        isPlayedWithCheats = savegame.wasCreatedThroughCheating;
        
        scoreScreenTimes = savegame.scoreScreenTimes;
    }
    
    public void reset()
    {
        // TODO ggf. muss einiges nicht mehr resettet werden, da immer ein neuer Helicopter erzeugt wird
        partialReset();
        placeAtStartpos();
        isDamaged = false;
        isPlayedWithCheats = false;
        GameResources.getProvider()
                     .getGameStatisticsCalculator()
                     .resetCounterForHighscore();
        resetSpecialUpgrades();
        scoreScreenTimes.clear();
    }
    
    public void resetStateGeneral()
    {
        inactivate();
        isCrashing = false;
        slowedTimer = 0;
        recentDamageTimer = 0;
        powerUpController.reset();
        resetRotorPosition();
        fireRateTimer = timeBetweenTwoShots;
    }
    
    private void resetSpecialUpgrades()
    {
        hasSpotlights = false;
        platingDurabilityFactor = STANDARD_PLATING_STRENGTH;
        hasPiercingWarheads = false;
        numberOfCannons = 1;
        resetFifthSpecial();
    }
    
    abstract void resetFifthSpecial();
    
    public void repair()
    {
        Audio.play(Audio.cash);
        GameResources.getProvider()
                     .getGameStatisticsCalculator()
                     .incrementNumberOfRepairs();
        isDamaged = false;
        isCrashing = false;
        restorePlating();
        setRelativePlatingDisplayColor();
    }
    
    public void obtainAllUpgrades()
    {
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            maximizeUpgrade(standardUpgradeType);
        }
        platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
        hasPiercingWarheads = true;
        getMaximumNumberOfCannons();
        makeAdjustmentsForCheatedUpgrades();
    }
    
    private void maximizeUpgrade(StandardUpgradeType standardUpgradeType)
    {
        setUpgradeLevelOf(standardUpgradeType, getType().getMaximumUpgradeLevelFor(standardUpgradeType));
    }
    
    void getMaximumNumberOfCannons()
    {
        numberOfCannons = 2;
    }
    
    private void makeAdjustmentsForCheatedUpgrades()
    {
        restorePlating();
        battery.restore();
        isDamaged = false;
        Window.updateRepairShopButtons(this);
        isPlayedWithCheats = true;
    }
    
    public void obtainSomeUpgrades()
    {
        hasSpotlights = true;
        obtainFifthSpecial();
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            if(getUpgradeLevelOf(standardUpgradeType) < PriceLevel.EXTORTIONATE.getMaximumUpgradeLevel())
            {
                setUpgradeLevelOf(standardUpgradeType, PriceLevel.EXTORTIONATE.getMaximumUpgradeLevel());
            }
        }
        makeAdjustmentsForCheatedUpgrades();
    }
    
    public boolean hasSomeUpgrades()
    {
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            if(getUpgradeLevelOf(standardUpgradeType) < PriceLevel.EXTORTIONATE.getMaximumUpgradeLevel())
            {
                return false;
            }
        }
        if(!hasSpotlights)
        {
            return false;
        }
        else
        {
            return hasFifthSpecial();
        }
    }
    
    abstract public boolean hasFifthSpecial();
    
    abstract public void obtainFifthSpecial();
    
    private boolean hasAllSpecialUpgrades()
    {
        return hasSpotlights
            && hasGoliathPlating()
            && hasPiercingWarheads
            && hasAllCannons()
            && hasFifthSpecial();
    }
    
    public boolean hasGoliathPlating()
    {
        return platingDurabilityFactor == GOLIATH_PLATING_STRENGTH;
    }
    
    public boolean hasAllCannons()
    {
        return numberOfCannons == 2;
    }
    
    public boolean hasAllUpgrades()
    {
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            if(!hasMaximumUpgradeLevelFor(standardUpgradeType))
            {
                return false;
            }
        }
        return hasAllSpecialUpgrades();
    }
    
    public void rotatePropellerSlow()
    {
        rotatePropeller(SLOW_ROTATIONAL_SPEED);
    }
    
    public void rotatePropellerFast()
    {
        rotatePropeller(FAST_ROTATIONAL_SPEED);
    }
    
    private void rotatePropeller(int rotationalSpeed)
    {
        rotorPosition = (rotorPosition + rotationalSpeed) % 360;
    }
    
    public void placeAtStartpos()
    {
        isMovingLeft = false;
        setBounds(INITIAL_BOUNDS);
        location.setLocation(getX() + FOCAL_POINT_X_RIGHT,
                             INITIAL_BOUNDS.y + FOCAL_PNT_Y_POS);
        setPaintBounds();
    }
    
    public void stopTractor()
    {
        Audio.tractorBeam.stop();
        tractor.stopTractor();
        tractor = null;
    }
    
    public void crash()
    {
        isDamaged = true;
        isRotorSystemActive = false;
        battery.discharge();
        destination.setLocation(getX() + 40, 520);
        if(tractor != null)
        {
            stopTractor();
        }
        GameResources.getProvider()
                     .getGameStatisticsCalculator()
                     .incrementNumberOfCrashes();
        if(location.getY() == 407d)
        {
            crashed(GameResources.getProvider()
                                 .getActiveGameEntityManager()
                                 .getExplosions());
        }
        else
        {
            isCrashing = true;
        }
    }
    
    private void crashed(Map<CollectionSubgroupType, Queue<Explosion>> explosions)
    {
        isActive = false;
        powerUpController.startDecayOfAllActivePowerUps();
        if(Events.level < 51 && explosions != null)
        {
            Audio.play(Audio.explosion3);
            Explosion.start(explosions,
                            this,
                            (int)(getX()
                                + (isMovingLeft
                                ? FOCAL_POINT_X_LEFT
                                : FOCAL_POINT_X_RIGHT)),
                            (int)(getY() + FOCAL_POINT_Y_EXP),
                            ExplosionType.ORDINARY,
                            false);
        }
        Events.isRestartWindowVisible = true;
        isCrashing = false;
    }
    
    public void takeMissileDamage()
    {
        currentPlating = Math.max(currentPlating - getProtectionFactor() * ENEMY_MISSILE_DAMAGE_FACTOR, 0f);
        startRecentDamageTimer();
        if(isDestinedToCrash())
        {
            crash();
        }
    }
    
    public boolean hasDestroyedPlating()
    {
        return currentPlating <= 0;
    }
    
    void startRecentDamageTimer()
    {
        recentDamageTimer = RECENT_DAMAGE_TIME;
    }
    
    private void updateRotorSystem()
    {
        rotorSystem = getSpeed();
    }
    
    private void updateMissileDrive()
    {
        missileDrive = getMissileDrive();
    }
    
    void setSpellCosts()
    {
        // TODO sollte für die Battery gesetzt werden, hier und automatisch, Helicopter braucht spellCost evtl. nicht mehr
        spellCosts = getType().getSpellCosts();
    }
    
    public void setRelativePlatingDisplayColor()
    {
        Colorations.plating = Colorations.percentColor(getRelativePlating());
    }
    
    public void activate()
    {
        isActive = true;
        isRotorSystemActive = true;
    }
    
    public void inactivate()
    {
        isActive = false;
        isRotorSystemActive = false;
    }
    
    public void adjustFireRate()
    {
        adjustFireRate(false);
    }
    
    public void adjustFireRate(boolean poweredUp)
    {
        // TODO überprüfen ob man direkt hier hasBoostedFireRate() nutzen kann und somit Parameter wegfallen kann
        timeBetweenTwoShots = getFireRate(poweredUp);
    }
    
    public int calculateSumOfFireRateBooster(boolean poweredUp)
    {
        return getUpgradeLevelOf(StandardUpgradeType.FIRE_RATE)
            + (poweredUp ? FIRE_RATE_POWERUP_LEVEL : 0);
    }
    
    public abstract void updateUnlockedHelicopters();
    
    public void useReparationPowerUp()
    {
        Audio.play(Audio.cash);
        if(!hasMaximumPlating())
        {
            currentPlating
                = Math.min(
                getMaximumPlating(),
                currentPlating + Math.max(1, missingPlating() / 2));
        }
    }
    
    public boolean hasMaximumPlating()
    {
        return currentPlating >= getMaximumPlating();
    }
    
    public float kaboomDamage()
    {
        return Math.max(4, 2 * currentPlating / 3);
    }
    
    
    private void restorePlating()
    {
        currentPlating = getMaximumPlating();
    }
    
    public float getMaximumPlating()
    {
        return platingDurabilityFactor * getBasePlating();
    }
    
    public float getBasePlating()
    {
        return getBasePlating(getPlatingLevel());
    }
    
    private float getBasePlating(int platingLevel)
    {
        return PLATING_MULTIPLIER * StandardUpgradeType.PLATING.getMagnitude(platingLevel);
    }
    
    private int getPlatingLevel()
    {
        return getUpgradeLevelOf(StandardUpgradeType.PLATING);
    }
    
    private void updatePlating()
    {
        currentPlating += getLastPlatingDurabilityIncrease();
        setRelativePlatingDisplayColor();
    }
    
    public float getLastPlatingDurabilityIncrease()
    {
        return platingDurabilityFactor * (getBasePlating() - getPreviousBasePlating());
    }
    
    private float getPreviousBasePlating()
    {
        int previousPlatingLevel = getPlatingLevel() - 1;
        return getBasePlating(previousPlatingLevel);
    }
    
    public float getCurrentPlating()
    {
        return currentPlating;
    }
    
    public void receiveStaticCharge(float degree)
    {
        if(!isInvincible())
        {
            slowDown();
            if(!hasUnlimitedEnergy())
            {
                float energyConsumption = degree * getStaticChargeEnergyDrain();
                battery.drain(energyConsumption);
            }
        }
    }
    
    void slowDown()
    {
        slowedTimer = SLOW_TIME;
    }
    
    float getStaticChargeEnergyDrain()
    {
        return STATIC_CHARGE_ENERGY_DRAIN;
    }
    
    public boolean isCollidingWith(Enemy enemy)
    {
        return basicCollisionRequirementsSatisfied(enemy) && enemy.canCollide();
    }
    
    public boolean basicCollisionRequirementsSatisfied(Enemy enemy)
    {
        return !isDamaged
            && enemy.isOnScreen()
            && enemy.intersects(getBounds());
    }
    
    public float getProtectionFactor()
    {
        return isInvincible()
            ? INVULNERABILITY_PROTECTION_FACTOR
            : STANDARD_PROTECTION_FACTOR;
    }
    
    public void becomesCenterOf(Explosion exp)
    {
        exp.ellipse.setFrameFromCenter(
            getX() + (isMovingLeft ? FOCAL_POINT_X_LEFT : FOCAL_POINT_X_RIGHT),
            getY() + FOCAL_POINT_Y_EXP,
            getX() + (isMovingLeft ? FOCAL_POINT_X_LEFT : FOCAL_POINT_X_RIGHT),
            getY() + FOCAL_POINT_Y_EXP);
    }
    
    public boolean isOnTheGround()
    {
        return getMaxY() + NO_COLLISION_HEIGHT == GROUND_Y;
    }
    
    public void turnAround()
    {
        isMovingLeft = !isMovingLeft;
        setBounds();
    }
    
    public void beAffectedByCollisionWith(Enemy enemy,
                                          GameRessourceProvider gameRessourceProvider,
                                          boolean playCollisionSound)
    {
        startRecentDamageEffect(enemy);
        if(playCollisionSound)
        {
            Audio.play(enemy.getType() == EnemyType.KABOOM
                           ? Audio.explosion4
                           : getCollisionAudio());
        }
        slowedTimer = 2;
        currentPlating = Math.max(0, currentPlating - enemy.collisionDamage());
    }
    
    void startRecentDamageEffect(Enemy enemy)
    {
        startRecentDamageTimer();
    }
    
    Clip getCollisionAudio()
    {
        return Audio.explosion1;
    }
    
    public boolean hasPerformedTeleportKill()
    {
        return bonusKillsTimer > 0;
    }
    
    public abstract HelicopterType getType();
    
    public void installGoliathPlating()
    {
        platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
        currentPlating += getBasePlating();
        setRelativePlatingDisplayColor();
    }
    
    public int getGoliathCosts()
    {
        return STANDARD_GOLIATH_COSTS;
    }
    
    public int getPiercingWarheadsCosts()
    {
        return STANDARD_SPECIAL_COSTS;
    }
    
    public void installPiercingWarheads()
    {
        hasPiercingWarheads = true;
    }
    
    public boolean canBeStoppedByTractorBeam()
    {
        return tractor == null;
    }
    
    public float getMissileDamageFactor()
    {
        return STANDARD_MISSILE_DAMAGE_FACTOR;
    }
    
    public ExplosionType getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
    {
        return ExplosionType.ORDINARY;
    }
    
    public boolean canImmobilizePowerUp()
    {
        return false;
    }
    
    public boolean canDetectCloakedVessels()
    {
        return false;
    }
    
    public void setCurrentBaseFirepower()
    {
        currentBaseFirepower = (int)(getMissileDamageFactor() * getFirepower());
    }
    
    public boolean isFifthSpecialOnMaximumStrength()
    {
        return true;
    }
    
    public boolean canBeHit()
    {
        return true;
    }
    
    public void initMenuEffect(int i)
    {
        Audio.playSpecialSound(getType());
    }
    
    public void updateMenuEffect()
    {
        rotatePropellerSlow();
        if(Window.effectTimer[getType().ordinal()] == 1)
        {
            stopMenuEffect();
        }
    }
    
    abstract public void stopMenuEffect();
    
    public boolean isTakingKaboomDamageFrom(Enemy enemy)
    {
        return enemy.isKaboomDamageDealer();
    }
    
    public float getBaseDamage()
    {
        return currentBaseFirepower;
    }
    
    public void rightMouseButtonReleaseAction(MouseEvent mouseEvent, double scalingFactor) {}
    
    public boolean canObtainCollisionReward()
    {
        return false;
    }
    
    public int getBonusFactor()
    {
        return hasSpotlights ? NIGHT_BONUS_FACTOR : DAY_BONUS_FACTOR;
    }
    
    public void resetRotorPosition()
    {
        rotorPosition = 0;
    }
    
    public float getBaseProtectionFactor(boolean canExplode)
    {
        return STANDARD_BASE_PROTECTION_FACTOR;
    }
    
    public String getTypeSpecificDebuggingOutput()
    {
        return "";
    }
    
    public abstract void resetStateTypeSpecific();
    
    public void prepareForMission()
    {
        resetRotorPosition();
    }
    
    public boolean deservesMantisReward(long missileLaunchingTime)
    {
        return false;
    }
    
    public void receiveRewardFor(Enemy enemy, Missile missile, boolean beamKill)
    {
        Events.updateFinance(enemy);
        typeSpecificRewards(enemy, missile, beamKill);
        Window.moneyDisplayTimer = Events.START;
    }
    
    public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill) {}
    
    public void levelUpEffect(int previousLevel) {}
    
    public PriceLevel getPriceLevelFor(StandardUpgradeType standardUpgradeType)
    {
        return getType().getPriceLevelFor(standardUpgradeType);
    }
    
    private float getSpeed()
    {
        return StandardUpgradeType.ROTOR_SYSTEM.getMagnitude(getUpgradeLevelOf(StandardUpgradeType.ROTOR_SYSTEM));
    }
    
    private int getMissileDrive()
    {
        return (int)StandardUpgradeType.MISSILE_DRIVE.getMagnitude(getUpgradeLevelOf(StandardUpgradeType.MISSILE_DRIVE));
    }
    
    private int getFirepower()
    {
        return (int)StandardUpgradeType.FIREPOWER.getMagnitude(getUpgradeLevelOf(StandardUpgradeType.FIREPOWER));
    }
    
    public int getEmpDamage()
    {
        return (int)StandardUpgradeType.FIREPOWER.getMagnitude(getUpgradeLevelOf(StandardUpgradeType.ENERGY_ABILITY));
    }
    
    private int getFireRate(boolean poweredUp)
    {
        return (int)StandardUpgradeType.FIRE_RATE.getMagnitude(calculateSumOfFireRateBooster(poweredUp));
    }
    
    boolean canRegenerateEnergy()
    {
        return !isDamaged;
    }
    
    public float getCurrentEnergy()
    {
        return battery.getCurrentCharge();
    }
    
    public float getMaximumEnergy()
    {
        return battery.getCapacity();
    }
    
    public float getRelativeEnergy()
    {
        return battery.getStateOfCharge();
    }
    
    public void restoreEnergy()
    {
        battery.restore();
    }
    
    float getRegenerationRate()
    {
        return battery.getRegenerationRate();
    }
    
    protected void consumeSpellCosts()
    {
        battery.drain(getEffectiveSpellCosts());
    }
    
    protected float getEffectiveSpellCosts()
    {
        return hasUnlimitedEnergy() ? 0.0f : spellCosts;
    }
    
    public boolean isEnergyAbilityActivatable()
    {
        return hasEnoughEnergyForAbility();
    }
    
    public boolean hasEnoughEnergyForAbility()
    {
        return battery.getCurrentCharge() >= spellCosts || hasUnlimitedEnergy();
    }
    
    public void boostEnergy()
    {
        battery.boostCharge();
    }
    
    public void updateEnergyAbility()
    {
        battery.upgradeTo(getUpgradeLevelOf(StandardUpgradeType.ENERGY_ABILITY));
    }
    
    public void tryToUseEnergyAbility(GameRessourceProvider gameRessourceProvider)
    {
        if(isEnergyAbilityActivatable())
        {
            useEnergyAbility(gameRessourceProvider);
        }
    }
    
    public abstract void useEnergyAbility(GameRessourceProvider gameRessourceProvider);
    
    public int getUpgradeLevelOf(StandardUpgradeType standardUpgradeType)
    {
        return levelsOfStandardUpgrades.get(standardUpgradeType);
    }
    
    public void upgrade(StandardUpgradeType standardUpgradeType)
    {
        int currentLevelOfUpgrade = getUpgradeLevelOf(standardUpgradeType);
        setUpgradeLevelOf(standardUpgradeType, currentLevelOfUpgrade + 1);
    }
    
    public void setUpgradeLevelOf(StandardUpgradeType standardUpgradeType, Integer upgradeLevel)
    {
        levelsOfStandardUpgrades.put(standardUpgradeType, upgradeLevel);
        
        switch(standardUpgradeType)
        {
            case ROTOR_SYSTEM -> updateRotorSystem();
            case MISSILE_DRIVE -> updateMissileDrive();
            case PLATING -> updatePlating();
            case FIREPOWER -> setCurrentBaseFirepower();
            case FIRE_RATE -> adjustFireRate();
            case ENERGY_ABILITY -> updateEnergyAbility();
        }
    }
    
    public boolean hasMaximumUpgradeLevelFor(StandardUpgradeType standardUpgradeType)
    {
        return getUpgradeLevelOf(standardUpgradeType) >= getType().getMaximumUpgradeLevelFor(standardUpgradeType);
    }
    
    public int getUpgradeCostFor(StandardUpgradeType standardUpgradeType)
    {
        int upgradeLevel = getUpgradeLevelOf(standardUpgradeType);
        PriceLevel priceLevel = getPriceLevelFor(standardUpgradeType);
        int baseUpgradeCosts = priceLevel.getBaseUpgradeCosts(upgradeLevel);
        int additionalUpgradeCosts = getAdditionalCosts(standardUpgradeType, upgradeLevel);
        
        return baseUpgradeCosts + additionalUpgradeCosts;
    }
    
    private int getAdditionalCosts(StandardUpgradeType standardUpgradeType, int upgradeLevel)
    {
        return getType().getAdditionalCosts(standardUpgradeType, upgradeLevel);
    }
    
    public float missingPlating()
    {
        return getMaximumPlating() - getCurrentPlating();
    }
    
    public void destroyPlating()
    {
        currentPlating = 0f;
    }
    
    public float getRelativePlating()
    {
        return getCurrentPlating() / getMaximumPlating();
    }
    
    public boolean isDestinedToCrash()
    {
        return hasDestroyedPlating() && !isDamaged;
    }
    
    public boolean hasTimeRecordingMissiles()
    {
        return false;
    }
    
    public boolean hasKillCountingMissiles()
    {
        return false;
    }
    
    public void inactivate(Map<CollectionSubgroupType, Queue<Missile>> missiles, Missile missile)
    {
        missiles.get(CollectionSubgroupType.INACTIVE)
                .add(missile);
    }
    
    public int getFifthSpecialCosts()
    {
        return CHEAP_SPECIAL_COSTS;
    }
    
    public int getSpotlightCosts()
    {
        return SPOTLIGHT_COSTS;
    }
    
    public Map<StandardUpgradeType, Integer> getLevelsOfStandardUpgrades()
    {
        return new EnumMap<>(levelsOfStandardUpgrades);
    }
    
    public boolean isCountingAsFairPlayedHelicopter()
    {
        return !isPlayedWithCheats || Events.IS_SAVE_GAME_SAVED_ANYWAY;
    }
    
    public Color getPrimaryHullColor()
    {
        return hasGoliathPlating()
            ? getType().getPlatedPrimaryHullColor()
            : getType().getStandardPrimaryHullColor();
    }
    
    public Color getSecondaryHullColor()
    {
        return hasGoliathPlating()
            ? getType().getPlatedSecondaryHullColor()
            : getType().getStandardSecondaryHullColor();
    }
    
    public int getLastCannonCost()
    {
        return STANDARD_SPECIAL_COSTS;
    }
    
    
    // Method for interacting with PowerUpController class
    public void startDecayOfAllCurrentBooster()
    {
        powerUpController.startDecayOfAllActivePowerUps();
    }
    
    public boolean isUnacceptablyBoostedForBossLevel()
    {
        return powerUpController.isAnyPowerUpForbiddenAtBossLevelActive();
    }
    
    public boolean isBoosted(PowerUpType powerUpType)
    {
        return powerUpController.isPowerUpActive(powerUpType);
    }
    
    public boolean hasTripleDamage()
    {
        return isBoosted(PowerUpType.TRIPLE_DAMAGE);
    }
    
    public boolean isInvincible()
    {
        return isBoosted(PowerUpType.INVINCIBLE);
    }
    
    public boolean hasUnlimitedEnergy()
    {
        return isBoosted(PowerUpType.UNLIMITED_ENERGY);
    }
    
    public boolean hasBoostedFireRate()
    {
        return isBoosted(PowerUpType.BOOSTED_FIRE_RATE);
    }
    
    public void turnOfInvincibility()
    {
        powerUpController.turnOfInvinciblePowerUp();
    }
    
    public void gainTripleDamagePermanently()
    {
        powerUpController.activateTripleDamagePowerUpPermanently();
    }
    
    public void gainInvincibilityPermanently()
    {
        powerUpController.activateInvinciblePowerUpPermanently();
    }
    
    public void restartPowerUpTimer(PowerUpType powerUpType)
    {
        powerUpController.restartPowerUpTimer(powerUpType);
    }
    
    public void switchPowerUpActivationState(Map<CollectionSubgroupType, Queue<PowerUp>> powerUps,
                                             PowerUpType powerUpType)
    {
        powerUpController.switchPowerUpActivationState(powerUps, powerUpType);
    }
    
    public void partialReset()
    {
        resetStateGeneral();
        resetStateTypeSpecific();
    }
    
    public void typeSpecificActionOn(Enemy enemy, GameRessourceProvider gameRessourceProvider)
    {
    }
    
    public int calculateCollisionDamage()
    {
        return 0;
    }
}