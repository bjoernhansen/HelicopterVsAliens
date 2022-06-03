package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.entities.GameEntityGroupType;
import de.helicopter_vs_aliens.control.entities.GroupTypeOwner;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.EnemyPainter;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType;
import de.helicopter_vs_aliens.model.enemy.devices.CloakingDevice;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.Pegasus;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.EnemyMissileType;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.applet.AudioClip;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.BOTTOM;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.LEFT;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.NONE;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.RIGHT;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.TOP;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.STUNNING;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.NICE_CATCH_TIME;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.TELEPORT_KILL_TIME;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.REPARATION;
import static de.helicopter_vs_aliens.model.scenery.SceneryObject.BG_SPEED;


public abstract class Enemy extends RectangularGameEntity implements GroupTypeOwner
{
	public static final int KAMIKAZE_RANGE = 620;
	
	public boolean isCloaked()
	{
		return cloakingDevice.isCompletelyCloaking();
	}
	
	public static class FinalEnemyOperator
	{
		private final EnumMap<FinalBossServantType, Enemy>
			servants = new EnumMap<>(FinalBossServantType.class);
		
		private final EnumMap<FinalBossServantType, Integer>
			timeSinceDeath = new EnumMap<>(FinalBossServantType.class);
		
		public boolean hasMinimumTimeBeforeRecreationElapsed(FinalBossServantType servantType)
		{
			return timeSinceDeath.get(servantType) > servantType.getMinimumTimeBeforeRecreation();
		}
		
		public void incrementTimeSinceDeathCounter(FinalBossServantType servantType)
		{
			Integer timeSinceDeathCounter = timeSinceDeath.get(servantType);
			timeSinceDeath.put(servantType, timeSinceDeathCounter + 1);
		}
		
		public boolean containsServant(FinalBossServantType servantType)
		{
			return servants.containsKey(servantType);
		}
		
		public void remove(FinalBossServantType servantType)
		{
			servants.remove(servantType);
		}
		
		public void resetTimeSinceDeath(FinalBossServantType servantType)
		{
			timeSinceDeath.put(servantType, 0);
		}
		
		public Enemy getServant(FinalBossServantType servantType)
		{
			return servants.get(servantType);
		}
		
		public void putServant(Enemy enemy)
		{
			FinalBossServantType.of(enemy.type)
								.ifPresent(servantType -> servants.put(servantType, enemy));
		}
	}
	
	private static final int
		WIDTH_VARIANCE_DIVISOR = 10,
		MAX_STARTING_Y = 220,
		MIN_STARTING_Y = 90,
		DODGE_BORDER_DISTANCE_LEFT = 90,
		DODGE_BORDER_DISTANCE_RIGHT = Main.VIRTUAL_DIMENSION.width - DODGE_BORDER_DISTANCE_LEFT,
		DEFAULT_CALL_BACK_MINIMUM_FOR_TURN_AT_BARRIER = 0;
	
	protected static final int
		DEFAULT_KAMIKAZE_SPEED_UP_X = 8;
	
	private static final float
		PRIMARY_COLOR_BRIGHTNESS_FACTOR = 1.3f,
		SECONDARY_COLOR_BRIGHTNESS_FACTOR = 1.5f,
		DEFAULT_TURN_PROBABILITY = 0.25f;
	
	// Konstanten
	public static final int
		SNOOZE_TIME = 100,    // Zeit, die vergeht, bis sich ein aktives Hindernis in Bewegung setzt
		DISABLED = -1; // TODO unnötig machen
	
	private static final float
		RADAR_DETECTABILITY = 0.2f;        // Alpha-Wert: legt fest, wie stark ein getarnter Gegner bei aktiviertem Radar noch zu sehen ist
	
		protected static final float POWER_UP_PROB				= 0.02f;
	protected static final float SPIN_SHOOTER_RATE 		   	= 0.55f;
	private static final float EXTRA_INACTIVE_TIME_FACTOR 	= 0.65f;
	
	public static final float
		REPARATION_POWER_UP_DROP_RATE = 0.14f;
	
	private static final float// Multiplikatoren, welche den Grundschaden von Raketen unter bestimmten Voraussetzungen erhöhen
		RADIATION_DAMAGE_FACTOR = 1.5f;            // Phönix-Klasse, nach Erwerb von Nahkampf-Bestrahlung: Schaden im Verhältnis zum regulären Raketenschaden, den ein Gegner bei Kollisionen mit dem Helikopter erleidet
		private static final float TELEPORT_DAMAGE_FACTOR = 4f;            // Phönix-Klasse: wie RADIATION_DAMAGE_FACTOR, aber für Kollisionen unmittelbar nach einem Transportvorgang
		private static final float EMP_DAMAGE_FACTOR_BOSS = 1.5f;            // Pegasus-Klasse: Schaden einer EMP-Welle im Verhältnis zum normalen Raketenschaden gegenüber von Boss-Gegnern // 1.5
		private static final float EMP_DAMAGE_FACTOR_ORDINARY = 2.5f;        // Pegasus-Klasse: wie EMP_DAMAGE_FACTOR_BOSS, nur für Nicht-Boss-Gegner // 3
	
	private static final int
		// Raum-Konstanten
		SAVE_ZONE_WIDTH = 116;
	protected static final int APPEARANCE_DISTANCE = 10;
	
	private static final int DISAPPEARANCE_DISTANCE = 100;
	private static final int BARRIER_DISTANCE = 100;

	private static final int KABOOM_Y_TURN_LINE = GROUND_Y - (int) (EnemyModelType.TIT.getHeightFactor() * EnemyType.KABOOM.getWidth());
	// Zeit-Konstanten
	
		protected static final int EMP_SLOW_TIME = 175;    // Zeit, die von EMP getroffener Gegner verlangsamt bleibt // 113

	private static final int INACTIVATION_TIME = 150;
	private static final int STUNNING_TIME_BASIS = 45;    // Basis-Wert zur Berechnung der Stun-Zeit nach Treffern von Stopp-Raketen
		private static final int BORROW_TIME = 65;
	private static final int MIN_TURN_TIME = 31;
	private static final int MIN_TURN_NOISELESS_TIME = 15;
	private static final int STATIC_CHARGE_TIME = 110;
	

	
	public static final int
		MIN_POWER_UP_LEVEL = 3;
	

	private static final int STANDARD_REWARD_FACTOR = 1;
	
	private static final int// TODO die 4 austauschen / anders lösen
		PRE_READY = 1;
	public static final int READY = 0;
	
	public static final Point2D
		ZERO_SPEED = new Point2D.Float(0, 0);
	
	private static final Point2D
		SLOW_VERTICAL_SPEED = new Point2D.Float(0, 1);
		
	private static final Point
		TURN_DISTANCE = new Point(50, 10);
		
	protected static final Rectangle
		TURN_FRAME = new Rectangle(TURN_DISTANCE.x,
								   TURN_DISTANCE.y,
								   Main.VIRTUAL_DIMENSION.width 
								   	- 2*TURN_DISTANCE.x,
								   GROUND_Y 
									- SAVE_ZONE_WIDTH
									- 2*TURN_DISTANCE.y);
	
	// für die Tarnung nötige Variablen
    public static final float[]
    	scales = { 1f, 1f, 1f, RADAR_DETECTABILITY},
    	offsets = new float[4];	
	
    private static final RescaleOp
		ROP_CLOAKED = new RescaleOp(scales, offsets, null);
	
	
	public static final
		Point2D boss = new Point2D.Float();	// Koordinaten vom aktuellen Boss; wichtig für Gegner-produzierende Boss-Gegner	
		
	/*
	 * 	Attribute der Enemy-Objekte	
	 */

	public int
		startingHitPoints,				// Anfangs-HitPoints (bei Erstellung des Gegners)
		invincibleTimer,				// reguliert die Zeit, die ein Gegner unverwundbar ist
		teleportTimer,					// Zeit [frames], bis der Gegner sich erneut teleportieren kann
		shield,							// nur für Boss 5 relevant; kann die Werte 0 (kein Schild), 1 oder 2 annehmen
		alpha,
		burrowTimer,
		untouchedCounter,
		stunningTimer,
		empSlowedTimer,			// reguliert die Länge der Verlangsamung nach EMP-Treffer (Pegasus-Klasse)
		collisionDamageTimer;			// Timer zur überwachung der Zeit zwischen zwei Helikopter-HP-Abzügen;
	
	public boolean
		isTouchingHelicopter,
		hasUnresolvedIntersection;
		
	
	public EnemyType
		type;
	
	public BarrierPositionType
		touchedSite,
		lastTouchedSite;
	
	// Farben
    public Color
		primaryColor,
    	secondaryColor;

	// TODO is wird of -direction.x (minus) übergeben, unlogisch, implementieren hier verständlicher machen. ggf. enemy übergeben und dann isMovingLeft etc. aufrufen
    private final Point
    	direction = new Point();		// Flugrichtung
    	
	Enemy
		stoppingBarrier,		// Hindernis-Gegner, der diesen Gegner aufgehalten hat
		isPreviousStoppingBarrier;
	
	private int
		hitPoints;						// aktuelle HitPoints
	
	private int
		lifetime;                // Anzahl der Frames seit Erstellung des Gegners; und vergangene Zeit seit Erstellung, Zeit
	private int
		crashPositionY;                // Bestimmt wie tief ein Gegner nach Absturz im Boden versinken kann
	private int
		collisionAudioTimer;
	private int
		turnAudioTimer;
	private int
		explodingTimer;            // Timer zur überwachung der Zeit zwischen Abschuss und Absturz
	
	protected int
		shieldMakerTimer;
	protected int
		callBack;
	private int
		chaosTimer;
	protected int
		speedup;
	protected int
		batchWiseMove;
	protected int
		shootTimer;
	protected int
		spawningHornetTimer;
	protected int
		turnTimer;
	protected int
		dodgeTimer;            // Zeit [frames], bis ein Gegner erneut ausweichen kann
	protected int
		snoozeTimer;
	protected int
		staticChargeTimer;
	
	protected int// nur für Hindernis-Gegner relevant
		rotorColor;	// TODO Color als int?? umbenennen!
	protected int barrierShootTimer;
	protected int barrierTeleportTimer;
	protected int shootPause;
	protected int shootingRate;
	protected int shotsPerCycle;
	protected int shootingCycleLength;
	protected int shotSpeed;
	protected int shotRotationSpeed;
	
	private int// Regulation des Stun-Effektes nach Treffer durch Stopp-Rakete der Orochi-Klasse
		nonStunableTimer;
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
        protected boolean canLoop;                // = true: Gegner fliegt Loopings
        protected boolean canChaosSpeedup;        // erhöht die Geschwindigkeit, wenn in Helicopter-Nähe
        private boolean isSpeedBoosted;
	private boolean isDestroyed;            // = true: Gegner wurde vernichtet
        protected boolean hasHeightSet;            // = false --> height = height_factor * width; = true --> height wurde manuell festgelegt
        private boolean hasCrashed;            // = true: Gegner ist abgestürzt
        private boolean isEmpShocked;            // = true: Gegner steht unter EMP-Schock --> ist verlangsamt
        public boolean isMarkedForRemoval;        // = true --> Gegner nicht mehr zu sehen; kann entsorgt werden
        protected boolean isUpperShieldMaker;        // bestimmt die Position der Schild-Aufspannenden Servants von Boss 5
        protected boolean isShielding;            // = true: Gegner spannt gerade ein Schutzschild für Boss 5 auf (nur für Schild-Generatoren von Boss 5)
        protected boolean isClockwiseBarrier;        // = true: der Rotor des Hindernisses dreht im Uhrzeigersinn
        protected boolean isRecoveringSpeed;
  
	protected AbilityStatusType
		tractor;				// = DISABLED (Gegner ohne Traktor); = READY (Traktor nicht aktiv); = 1 (Traktor aktiv)
	
	protected EnemyMissileType
		shotType;
	
	
	private final GraphicsAdapter []
		graphicsAdapters = new GraphicsAdapter[2];
	
	public FinalEnemyOperator
		operator;
	
	private final CloakingDevice
		cloakingDevice = new CloakingDevice();
	
	private final BufferedImage []
		image = new BufferedImage[4];
		
	protected final Point2D
		targetSpeedLevel = new Point2D.Float();        // Anfangsgeschwindigkeit
		private final Point2D speedLevel = new Point2D.Float();            // auf Basis dieses Objektes wird die tatsächliche Geschwindigkeit berechnet
		private final Point2D speed = new Point2D.Float();                // tatsächliche Geschwindigkeit
		protected final Point2D shootingDirection = new Point2D.Float();   	// Schussrichtung von schießenden Barrier-Gegnern
	
	public void reset()
	{
		lifetime = 0;
		targetSpeedLevel.setLocation(ZERO_SPEED);
		initializeMovingDirection();
		callBack = 0;
		shield = 0;
		operator = null;
		alpha = 255;
		
		isDestroyed = false;
		isMarkedForRemoval = false;
		hasUnresolvedIntersection = false;
		canMoveChaotic = false;
		canDodge = false;
		canChaosSpeedup = false;
		canKamikaze = false;
		isAbleToTurnAroundEarly = false;
		isTouchingHelicopter = false;
		isSpeedBoosted = false;
		canFrontalSpeedup = false;
		canSinusMove = false;
		isShielding = false;
		canTurn = false;
		canLoop = false;
		isClockwiseBarrier = true;
		stoppingBarrier = null;
		isPreviousStoppingBarrier = null;
		hasCrashed = false;
		canInstantlyTurnAround = false;
		isRecoveringSpeed = false;
		hasHeightSet = false;
		isEmpShocked = false;
		
		collisionDamageTimer = READY;
		collisionAudioTimer = READY;
		turnAudioTimer = READY;
		dodgeTimer = READY;
		turnTimer = READY;
		explodingTimer = READY;
		empSlowedTimer = READY;
		invincibleTimer = READY;
		chaosTimer = READY;
		snoozeTimer = READY;
		nonStunableTimer = READY;
		
		spawningHornetTimer = DISABLED;
		cloakingDevice.reset();
		teleportTimer = DISABLED;
		shieldMakerTimer = DISABLED;
		shootTimer = DISABLED;
		barrierShootTimer = DISABLED;
		barrierTeleportTimer = DISABLED;
		burrowTimer = DISABLED;
		staticChargeTimer = DISABLED;
		speedup = DISABLED;
		
		tractor = AbilityStatusType.DISABLED;
		batchWiseMove = 0;
		
		shootingDirection.setLocation(0, 0);
		shootPause = 0;
		shootingRate = 0;
		shotsPerCycle = 0;
		shootingCycleLength = 0;
		shotSpeed = 0;
		shotRotationSpeed = 0;
		shotType = EnemyMissileType.DISCHARGER;
		
		touchedSite = NONE;
		lastTouchedSite = NONE;
		
		untouchedCounter = 0;
		rotorColor = 0;
		deactivationProbability = 0f;
		stunningTimer = READY;
		
		totalStunningTime = 0;
		knockBackDirection = 0;
	}
	
	private void initializeMovingDirection()
	{
		turnLeft();
		setRandomDirectionY();
	}
	
	public void dimmedRepaint()
	{
		primaryColor = Colorations.adjustBrightness(primaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
		secondaryColor = Colorations.adjustBrightness(secondaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
		repaint();
	}
	
	public boolean hasGlowingEyes()
	{
		return !isDestroyed && isMeetingRequirementsForGlowingEyes();
	}
	
	protected abstract boolean isMeetingRequirementsForGlowingEyes();
	
	public void clearImage()
    {
    	for(int i = 0; i < image.length; i++)
    	{
    		image[i] = null; 
    		if(i < 2){graphicsAdapters[i] = null;}
    	}
    }
    
    // TODO gehört diese Methode nicht eher in die Painter-Klasse?
	public void repaint()
	{
		for(int j = 0; j < 2; j++)
		{		
			if(getModel() != EnemyModelType.BARRIER)
			{
				// TODO hier taucht null pointer exception auf. Warum?
				graphicsAdapters[j].setComposite(AlphaComposite.Src);
				graphicsAdapters[j].setColor(Colorations.translucentDarkestBlack);
				graphicsAdapters[j].fillRect(0, 0, image[j].getWidth(), image[j].getHeight());
			}
			EnemyPainter enemyPainter = GraphicsManager.getInstance().getPainter(getClass());
			enemyPainter.paintImage(graphicsAdapters[j], this, 1-2*j, null, true);
		}
	}
	public boolean countsForTotalAmountOfEnemiesSeen()
	{
		return true;
	}
	public void initialize(GameRessourceProvider gameRessourceProvider)
	{
		setBasicProperties();
		doTypeSpecificInitialization();
		finalizeInitialization(gameRessourceProvider);
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
	
	protected void finalizeInitialization(GameRessourceProvider gameRessourceProvider)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		setInitialLocation(helicopter);
		
		speedLevel.setLocation(targetSpeedLevel);
		setPaintBounds((int)getWidth(),
							(int)getHeight());
		assignImage(helicopter);
		
		if(isShootingStandardEnemy())
		{
			initializeShootDirectionOfDefaultEnemies();
		}
	}
	
	private boolean isShootingStandardEnemy()
	{
		return isReadyToShoot();
	}
	
	protected boolean isReadyToShoot()
	{
		return shootTimer == READY;
	}
	
	private boolean isShooter()
	{
		return shootTimer != DISABLED;
	}
	
	private boolean isCurrentlyShooting()
	{
		return isShooter() && !isReadyToShoot();
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
		return type.getWidth()/getWidthVarianceDivisor();
	}
	
	protected int getWidthVarianceDivisor()
	{
		return WIDTH_VARIANCE_DIVISOR;
	}
	
	protected void setInitialLocation(Helicopter helicopter)
	{
		double x = calculateInitialX();
		double y = calculateInitialY();
		setLocation(x, y);
	}
	
	protected double calculateInitialX()
	{
		return Main.VIRTUAL_DIMENSION.width + APPEARANCE_DISTANCE;
	}
	
	protected double calculateInitialY()
	{
		return MIN_STARTING_Y + Math.random()*(MAX_STARTING_Y - getHeight());
	}
	
	
	// TODO Methode überarbeiten und Teile in kleinere Methoden auslagern
	private void assignImage(Helicopter helicopter)
	{
		for(int i = 0; i < 2; i++)
		{
			image[i] = new BufferedImage((int)(1.028f * paintBounds.width),
											  (int)(1.250f * paintBounds.height),
											  BufferedImage.TYPE_INT_ARGB);
			graphicsAdapters[i] = Graphics2DAdapter.withAntialiasing(image[i]);
			//graphics[i].setComposite(AlphaComposite.Src);
			
			EnemyPainter enemyPainter = GraphicsManager.getInstance().getPainter(getClass());
			enemyPainter.paintImage(graphicsAdapters[i], this,1-2*i, null, true);
			if(cloakingDevice.isEnabled() && helicopter.getType() == HelicopterType.OROCHI)
			{
				BufferedImage 
					 tempImage = new BufferedImage((int)(1.028f * paintBounds.width),
							 						(int)(1.250f * paintBounds.height),
							 						BufferedImage.TYPE_INT_ARGB);
				
				image[2+i] = new BufferedImage((int)(1.028f * paintBounds.width),
													(int)(1.250f * paintBounds.height),
													BufferedImage.TYPE_INT_ARGB);
				
				enemyPainter.paintImage(Graphics2DAdapter.withAntialiasing(tempImage), this,1-2*i, Color.red, true);
				Graphics2DAdapter.withAntialiasing(image[2+i]).drawImage(tempImage, ROP_CLOAKED, 0, 0);
			}
		}		
	}

    private void placeCloakingBarrierAtPausePosition()
	{
		callBack--;
		uncloakAndDisableCloakingDevice();
		barrierTeleportTimer = READY;
		setY(GROUND_Y + 2 * getWidth());
	}

	private void placeNearHelicopter(Helicopter helicopter)
	{		
		boolean isLeftOfHelicopter = !(helicopter.getMaxX() + (0.5f * getWidth() + BARRIER_DISTANCE) < 1024);
					
		int x, 
			y = (int)(helicopter.getY()
				+ helicopter.getHeight()/2
				- getWidth()
				+ Math.random()*getWidth());
		
		if(isLeftOfHelicopter)
		{
			x = (int)(helicopter.getX()
				-3*getWidth()/2
				- 10
				+ Math.random()*(getWidth()/3));
		}
		else
		{
			x = (int)(helicopter.getMaxX() + BARRIER_DISTANCE);
		}
		// TODO in Methode auslagern
		setLocation(x, Math.max(0, Math.min(y, GROUND_Y-getWidth())));
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
		float shootingDirectionX = (float) getDirectionX();
		shootingDirection.setLocation( shootingDirectionX, 0f);
	}
	
	private static boolean turnaroundIsTurnAway(double dir, double enemyCenter,
												double barrierCenter)
	{
		return 	   dir ==  1 && enemyCenter < barrierCenter
				|| dir == -1 && enemyCenter > barrierCenter;
	}
	
	public boolean isVisibleNonBarricadeVessel()
	{
		return isVisible();
	}
	
	private boolean isVisible()
	{
		Helicopter helicopter = Controller.getInstance().getHelicopter();
		return !isCloaked() || helicopter.canDetectCloakedVessels();
	}
	
	public final void update(GameRessourceProvider gameRessourceProvider)
	{												
		lifetime++;
		updateTimer();
		if(Events.isBossLevel())
		{
			callBack = 0;
		}
		checkForBarrierCollision();
		if(!isStunned())
		{
			updateStoppableTimer();
			performFlightManeuver(gameRessourceProvider);
			validateTurns();
		}
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		calculateSpeed(helicopter);
		move();
		if(helicopter.canCollideWith(this))
		{
			collision(gameRessourceProvider);
		}
		helicopter.typeSpecificActionOn(gameRessourceProvider,this);
		if(hasDeadlyGroundContact())
		{
			destroyByCrash(gameRessourceProvider);
		}
		if(isToBeRemoved())
		{
			prepareRemoval();
		}
		setPaintBounds();
	}
	
	protected void performFlightManeuver(GameRessourceProvider gameRessourceProvider)
	{
		calculateFlightManeuver(gameRessourceProvider);
	}
	
	public boolean isStunned()
	{
		return stunningTimer > READY;
	}
	
	private void updateStoppableTimer()
	{
		if(snoozeTimer > 0){snoozeTimer--;}
	}

	protected boolean hasDeadlyGroundContact()
	{	
		return isIntersectingGroundLine()
			   && getModel() != EnemyModelType.BARRIER
			   && !isDestroyed;
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
				if(	   EnemyController.livingBarrier[i] != this
					&& EnemyController.livingBarrier[i].intersects(this))
					
				{
					stoppingBarrier = EnemyController.livingBarrier[i];
					break;
				}
			}
		}
	}
	
	
	private void updateTimer()
	{		
		if(	collisionDamageTimer > 0) {collisionDamageTimer--;}
		if(	collisionAudioTimer > 0) {collisionAudioTimer--;}
		if(	turnAudioTimer > 0) {turnAudioTimer--;}
		if(	empSlowedTimer > 0) {empSlowedTimer--;}
		if(	staticChargeTimer > 0) {staticChargeTimer--;}
		if(	invincibleTimer > 0) {invincibleTimer--;}
		if(	chaosTimer > 0) {chaosTimer--;}
		if(	nonStunableTimer > 0) {nonStunableTimer--;}
		if( turnTimer > 0) {turnTimer--;}
		if( isStunned()) {stunningTimer--;}
	}

	protected void calculateFlightManeuver(GameRessourceProvider gameRessourceProvider)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		
		// Beschleunigung
		if(speedup != DISABLED || canFrontalSpeedup)
		{
			evaluateSpeedup(helicopter);
		}	
				
		// Schubweises Fliegen
		if(batchWiseMove != 0){
			evaluateBatchWiseMove();}
					
		// Chaos-Flug
		if(    canMoveChaotic
			&& chaosTimer == READY
			&& dodgeTimer == READY)
		{
			if( Calculations.tossUp(0.2f) && type.isShieldMaker())
			{
				turnAround();
			}
			if( Calculations.tossUp(0.2f))
			{
				switchDirectionY();
			}			
			chaosTimer = 5;
		}
		
		// Early x-Turn
		if(isTurningAroundEarly())
		{
			isAbleToTurnAroundEarly = false;
			turnRight();
		}
							
		// Frontal-Angriff
		if(isAbeleToMakeKamikaze())
		{
			makeKamikazeIfAppropriateWith(helicopter);
		}
		
		// Shooting
		if(isShooter()){evaluateShooting(gameRessourceProvider);}
		
		// TODO das gehört zu den Barriers
		// Vergraben
		if(burrowTimer != DISABLED && !(snoozeTimer > 0))
		{				
			evaluateBorrowProcedure(helicopter);
		}
		
		// Shooting Barrier
		if(barrierShootTimer != DISABLED)
		{
			evaluateBarrierShooting(gameRessourceProvider);
		}									
		
		// Snooze bei Hindernissen									
		if(snoozeTimer == PRE_READY)
		{
			endSnooze();
		}
		
		// Barrier-Teleport			
		if(barrierTeleportTimer != DISABLED
			&& !(snoozeTimer > 0))
		{				
			evaluateBarrierTeleport(helicopter);
		}				
				
		// Sinus- und Loop-Flug
		if(canSinusMove){sinusLoop();}
		
		// tarnen
		if(cloakingDevice.isActive() && !isRestrictedByCloakingObstacles())
		{
			cloaking();
		}
		
		// Tractor					
		if(canStopByTractorBeam(helicopter))
		{
			startTractor(helicopter);
		}
				
		//Chaos-SpeedUp
		if(	canChaosSpeedup
			&& speedLevel.getX() <= targetSpeedLevel.getX()
			&& helicopter.getX() - getX() > -350	)
		{
			setSpeedLevelX(targetSpeedLevel.getX() + 6);
		}
		if(canChaosSpeedup && (helicopter.getX() - getX()) > -160)
		{			
			canMoveChaotic = true;
			setSpeedLevelY(9 + 4.5*Math.random());
		}
				
		// Ausweichen
		if(dodgeTimer > 0){
			evaluateDodge();}
		
		// Teleportieren
		if(teleportTimer > 0)
		{	
			teleportTimer--;
			if(	teleportTimer == READY)
			{
				speedLevel.setLocation(targetSpeedLevel);
			}				
		}		
	}
	
	protected boolean isRestrictedByCloakingObstacles()
	{
		return isEmpSlowed();
	}
	
	private boolean isAbeleToMakeKamikaze()
	{
		return canKamikaze && !(teleportTimer > 0);
	}
	
	private boolean isTurningAroundEarly()
	{
		return isAbleToTurnAroundEarly && getMinX() < 0.85 * Main.VIRTUAL_DIMENSION.width;
	}
	
	
	private void endSnooze()
	{
		if(burrowTimer == DISABLED)
		{
			speedLevel.setLocation(targetSpeedLevel);
		}
		else
		{
			endInterruptedBorrowProcedure();
		}
		
		if(barrierTeleportTimer != DISABLED)
		{
			cloakingDevice.activate();
			barrierTeleportTimer = CloakingDevice.BOOT_AND_FADE_TIME;
		}		
	}

	private void endInterruptedBorrowProcedure()
	{
		if(burrowTimer > BORROW_TIME + shootingRate * shotsPerCycle)
		{
			burrowTimer = 2 * BORROW_TIME + shootingRate * shotsPerCycle - burrowTimer;
		}
		else if(burrowTimer > BORROW_TIME)
		{
			burrowTimer = BORROW_TIME;
		}
		flyDown();
		speedLevel.setLocation(SLOW_VERTICAL_SPEED);
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
			if(	turnaroundIsTurnAway(getDirectionX(),
				getCenterX(),
				stoppingBarrier.getCenterX())
				// Gegner sollen nicht an Barriers abdrehen, bevor sie im Bild waren.
				&& isOnScreen())
			{
				performXTurnAtBarrier();
			}
		}
		else
		{
			if(turnaroundIsTurnAway(getDirectionY(),
				getCenterY(),
				stoppingBarrier.getCenterY()))
			{
				switchDirectionY();
			}
		}
	}
	
	boolean hasLateralFaceTouchWith(Enemy barrier)
	{
		return
			Calculations.getIntersectionLength(	getMinX(),
				getMaxX(),
				barrier.getMinX(),
				barrier.getMaxX())
				<
				Calculations.getIntersectionLength(	getMinY(),
					getMaxY(),
					barrier.getMinY(),
					barrier.getMaxY());
	}
	
	private void performXTurnAtBarrier()
	{
		turnAround();
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
		turnAround();
		turnTimer = MIN_TURN_TIME;
		if(callBack > 0){callBack--;}
	}
	
	private boolean isTurningAtVerticalBoundaries()
	{
		return 	burrowTimer == DISABLED
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
			&& !isDestroyed;
	}
	
	private boolean hasCrossedBottomBoundaries()
	{
		return getMaxY() >= getBottomBoundary();
	}
	
	protected double getBottomBoundary()
	{
		return TURN_FRAME.getMaxY();
	}
	
	private void performVerticalTurn()
	{
		switchDirectionY();
		if(canSinusMove)
		{
			setSpeedLevelY(1.0);
		}
		if(getModel() == EnemyModelType.BARRIER)
		{
			if(isFlyingUp()){Audio.play(Audio.landing);}
			snooze(false);
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

	private void empShock(GameRessourceProvider gameRessourceProvider, Pegasus pegasus)
    {
    	takeDamage((int)getEmpVulnerabilityFactor() * pegasus.getEmpDamage());
		isEmpShocked = true;
		if(type == EnemyType.BOSS_4){spawningHornetTimer = READY;}
		disableSiteEffects(pegasus);
				
		if(hasHPsLeft())
		{			
			Audio.play(Audio.stun);
			if(getModel() == EnemyModelType.BARRIER){snooze(true);}
			else if(teleportTimer == READY ){teleport();}
			else if(isAbleToBeSlowedDownByEmp())
			{
				empSlowedTimer = getEmpSlowTime();
			}
			reactToHit(pegasus, null);
			
			Explosion.start(gameRessourceProvider.getExplosions(), pegasus,
							getCenterX(),
							getCenterY(), STUNNING, false);
		}
		else
		{
			Audio.play(Audio.explosion2);
			pegasus.empWave.kills++;
			pegasus.empWave.earnedMoney += calculateReward(pegasus);
			dieFromEmpWave(gameRessourceProvider);
		}
    }
	
	protected boolean isAbleToBeSlowedDownByEmp()
	{
		return isStunable();
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
			   && (    (getMinX() > Main.VIRTUAL_DIMENSION.width + DISAPPEARANCE_DISTANCE
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
			   && getMinX() < Main.VIRTUAL_DIMENSION.width;
	}
	
	protected void prepareRemoval()
	{
		isMarkedForRemoval = true;
	}	
	
	private boolean isEmpShockable(Pegasus pegasus)
	{
		return     !isEmpShocked
				&& !isDestroyed
				&& !isInvincible()
				&& !(barrierTeleportTimer != DISABLED && barrierShootTimer == DISABLED)
				&& pegasus.empWave.ellipse.intersects(getBounds());
	}	

	private void evaluateSpeedup(Helicopter helicopter)
	{
		if(  speedLevel.getX() < (speedup == DISABLED ? 12 : 19)
			 && (speedup  > 0 || canFrontalSpeedup) )
		{
			increaseSpeedLevelX(0.5);
		}			
		if(	speedup == 0 && atEyeLevel(helicopter))
		{
			speedup = 1;
			canSinusMove = true;
		}
		else if(speedup == 1 && !atEyeLevel(helicopter))
		{
			speedup = 2;
		}
		else if(speedup == 2 && atEyeLevel(helicopter))
		{
			speedup = 3;
			canSinusMove = false;
			setSpeedLevelY(1.5);
			if(getY() < helicopter.getY()){flyDown();}
			else{flyUp();}	
		}		
	}
	
	private boolean atEyeLevel(Helicopter helicopter)
	{
		return intersects(Integer.MIN_VALUE/2f,
								  helicopter.getY(),
								  Integer.MAX_VALUE,
								  helicopter.getHeight());
	}
	
	private void evaluateBatchWiseMove()
	{
		if(batchWiseMove == 1)
		{
			increaseSpeedLevelX(0.5);
		}
		else if(batchWiseMove == -1)
		{
			increaseSpeedLevelX(-0.5);
		}
		if(speedLevel.getX() <= 0){batchWiseMove = 1;}
		if(speedLevel.getX() >= targetSpeedLevel.getX()){batchWiseMove = -1;}
	}
	
	protected void startKamikazeMode()
	{
		canKamikaze = true;
		canFrontalSpeedup = true;
	}
	
	protected void makeKamikazeIfAppropriateWith(Helicopter helicopter)
    {
    	if(isWithinKamikazeRangeOf(helicopter))
		{
			makeKamikazeWith(helicopter);
		}
		else if(!canFrontalSpeedup && dodgeTimer == READY)
		{			
			if(type == EnemyType.BODYGUARD && Events.boss.shield < 1)
			{
				setSpeedLevelX(7.5);
			}
			else
			{
				speedLevel.setLocation(targetSpeedLevel);
			}
		}
    }
	
	protected void makeKamikazeWith(Helicopter helicopter)
	{
		adaptSpeedLevelForKamikaze();
		if(isFlyingDown() && isFurtherDownThan(helicopter))
		{
			flyUp();
			setSpeedLevelToZeroY();
		}
		else if(isFlyingUp() && isFurtherUp(helicopter))
		{
			flyDown();
			setSpeedLevelToZeroY();
		}
		if(speedLevel.getY() < 8)
		{
			increaseSpeedLevelY(0.5);
		}
	}
	
	private boolean isFurtherUp(Helicopter helicopter)
	{
		return getMaxY() < helicopter.getMaxY();
	}
	
	private boolean isFurtherDownThan(Helicopter helicopter)
	{
		return getMinY() > helicopter.getMinY();
	}
	
	protected void adaptSpeedLevelForKamikaze()
	{
		setSpeedLevelX(getKamikazeSpeedUpX());
	}
	
	protected double getKamikazeSpeedUpX()
	{
		return DEFAULT_KAMIKAZE_SPEED_UP_X;
	}
	
	private void evaluateBorrowProcedure(Helicopter helicopter)
	{		
		if(burrowTimer > 0){burrowTimer--;}
		if(burrowTimer == BORROW_TIME + shootingRate * shotsPerCycle)
		{					
			barrierShootTimer = shootingRate * shotsPerCycle;
			speedLevel.setLocation(ZERO_SPEED);
		}
		else if(burrowTimer == BORROW_TIME)
		{
			barrierShootTimer = DISABLED;
			speedLevel.setLocation(SLOW_VERTICAL_SPEED);
			flyDown();
		}
		else if(burrowTimer == 1)
		{
			speedLevel.setLocation(ZERO_SPEED);
		}
		else if(burrowTimer == READY
				&&( (type != EnemyType.PROTECTOR
				     && Calculations.tossUp(0.004f))
				    || 
				    (type == EnemyType.PROTECTOR
				     && (helicopter.getX() > boss.getX() - 225) )))
		{			
			burrowTimer = 2 * BORROW_TIME
								+ shootingRate * shotsPerCycle
								+ (getY() == GROUND_Y
									? EnemyType.PROTECTOR.getWidth()/8
									: 0)
								- 1;
			speedLevel.setLocation(SLOW_VERTICAL_SPEED);
			flyUp();
		}	
	}
	
	private void evaluateShooting(GameRessourceProvider gameRessourceProvider)
	{
		if(	isReadyToShoot()
			&& !isEmpSlowed()
			&& Calculations.tossUp(0.1f)
			&& getX() + getWidth() > 0
			&& !isCloaked()
			&& ((isFlyingLeft() 
				 && gameRessourceProvider.getHelicopter().intersects(
					getX() + Integer.MIN_VALUE/2f,
					getY() + (getModel() == EnemyModelType.TIT ? 0 : getWidth()/2) - 15,
					Integer.MAX_VALUE/2f,
					EnemyMissile.DIAMETER+30))
				||
				((isFlyingRight() 
				  && gameRessourceProvider.getHelicopter().intersects(
					getX(),
					getY() + (getModel() == EnemyModelType.TIT ? 0 : getWidth()/2) - 15,
					Integer.MAX_VALUE/2f,
					EnemyMissile.DIAMETER+30)))))
		{
			shoot(gameRessourceProvider.getEnemyMissiles(),
						hasDeadlyShots() ? EnemyMissileType.BUSTER : EnemyMissileType.DISCHARGER,
						shotSpeed + 3*Math.random()+5);
			
			shootTimer = shootingRate;
		}
		if(isCurrentlyShooting()){shootTimer--;}
	}
	
	protected boolean hasDeadlyShots()
	{		
		return false;
	}

	private void evaluateBarrierShooting(GameRessourceProvider gameRessourceProvider)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		if(barrierShootTimer == 0)
		{
			barrierShootTimer = shootingCycleLength;
			if(	shotRotationSpeed == 0
				&&	  (helicopter.getX()    < getX()         && shootingDirection.getX() > 0)
					||(helicopter.getMaxX() > getMaxX() && shootingDirection.getX() < 0) )
			{
				shootingDirection.setLocation(-shootingDirection.getX(), shootingDirection.getY());
			}
		}		
		if( barrierShootTimer <= shotsPerCycle * shootingRate
			&& getX() + getWidth() > 0
			&& barrierShootTimer %shootingRate == 0)
		{					
			if(shotRotationSpeed != 0)
			{
				float tempValue = 0.0005f * shotRotationSpeed * lifetime;
				shootingDirection.setLocation(
						Math.sin(tempValue),
						Math.cos(tempValue) );
			}
			if(burrowTimer != DISABLED || barrierTeleportTimer != DISABLED)
			{
				// Schussrichtung wird auf Helicopter ausgerichtet
				shootingDirection.setLocation(
						( (helicopter.getX() + (helicopter.isMovingLeft ? Helicopter.FOCAL_PNT_X_LEFT : Helicopter.FOCAL_PNT_X_RIGHT))
							  - (getX() +       getWidth()/2)),
						  (helicopter.getY() + Helicopter.FOCAL_PNT_Y_EXP)
						  	  - (getY() +       getHeight()/2)) ;
				float distance = (float) Calculations.ZERO_POINT.distance(shootingDirection);
				shootingDirection.setLocation(shootingDirection.getX()/distance,
													shootingDirection.getY()/distance);
			}
			shoot(gameRessourceProvider.getEnemyMissiles(), shotType, shotSpeed);
		}				
		barrierShootTimer--;
	}
	
	public void shoot(Map<CollectionSubgroupType, LinkedList<EnemyMissile>> enemyMissiles, EnemyMissileType missileType, double missileSpeed)
    {
    	Iterator<EnemyMissile> iterator = enemyMissiles.get(CollectionSubgroupType.INACTIVE).iterator();
		EnemyMissile enemyMissile;
		if(iterator.hasNext()){enemyMissile = iterator.next(); iterator.remove();}
		else{enemyMissile = new EnemyMissile();}
		enemyMissiles.get(CollectionSubgroupType.ACTIVE).add(enemyMissile);
		enemyMissile.launch(this, missileType, missileSpeed, shootingDirection);
		Audio.play(Audio.launch3);
    }
	
	private void evaluateBarrierTeleport(Helicopter helicopter)
	{
		if(barrierTeleportTimer == CloakingDevice.BOOT_AND_FADE_TIME + shootingRate * shotsPerCycle)
		{					
			barrierShootTimer = shootingRate * shotsPerCycle;
			uncloakAndDisableCloakingDevice();
		}
		else if(barrierTeleportTimer == CloakingDevice.BOOT_AND_FADE_TIME)
		{
			barrierShootTimer = DISABLED;
			cloakingDevice.activate();
			if(getMaxX() > 0){Audio.play(Audio.cloak);}
		}	
		else if(barrierTeleportTimer == READY && Calculations.tossUp(0.004f))
		{
			startBarrierUncloaking(helicopter);
		}
		
		if(barrierTeleportTimer != READY)
		{
			barrierTeleportTimer--;
			if(barrierTeleportTimer == READY)
			{				
				if(callBack > 0)
				{					
					placeCloakingBarrierAtPausePosition();
				}
				else{isMarkedForRemoval = true;}
			}
		}		
	}
	
	protected void startBarrierUncloaking(Helicopter helicopter)
	{
		barrierTeleportTimer = 2 * CloakingDevice.BOOT_AND_FADE_TIME + shootingRate * shotsPerCycle;
		cloakingDevice.setToEndOfCloakedTime();
		placeNearHelicopter(helicopter);
	}
	
	
	protected void sinusLoop()
    {
		speedLevel.setLocation(
				speedLevel.getX(),
				Math.max(4.0, 0.15f*(145-Math.abs(getY()-155))));
    }	
	
	private void cloaking()
    {
    	if(isReadyToContinueCloakingProcess())
		{
			cloakingDevice.run();
		}
    	if(cloakingDevice.isUncloakingInProgress())
		{
			if(cloakingDevice.hasJustStartedFadingAway())
			{
				Audio.play(Audio.cloak);
			}
			if(cloakingDevice.isShutDownCompleted())
			{
				uncloakAndResetCloakingDevice();
			}
		}
		setTransparency();
    }
	
	private void setTransparency()
	{
		alpha = cloakingDevice.getAlpha();
	}
	
	protected boolean isReadyToContinueCloakingProcess()
	{
		return true;
	}
	
	protected void uncloakAndResetCloakingDevice()
	{
		uncloakAndSetCloakingDeviceReadyForUse();
	}
	
	private boolean canStopByTractorBeam(Helicopter helicopter)
	{		
		return isTractorReady()
				&& isInRangeOf(helicopter)
				&& helicopter.canBeStoppedByTractorBeam();
	}
	
	private boolean isInRangeOf(Helicopter helicopter)
	{
		return    helicopter.getX() - getX() > -750
			   && helicopter.getX() - getX() < -50
			   && helicopter.getY() + 56 > getY() + 0.2 * getHeight()
			   && helicopter.getY() + 60 < getY() + 0.8 * getHeight();
	}
	
	private boolean isTractorReady() {
		return tractor == AbilityStatusType.READY
				&& !isEmpSlowed()
				&& !cloakingDevice.isActive()
				&& getMaxX() < Main.VIRTUAL_DIMENSION.width;
	}

	private void startTractor(Helicopter helicopter)
	{
		Audio.loop(Audio.tractorBeam);
		tractor = AbilityStatusType.ACTIVE;
		speedLevel.setLocation(ZERO_SPEED);
		helicopter.tractor = this;
		turnLeft();		
	}
	
	public void stopTractor()
	{
		tractor = AbilityStatusType.DISABLED;
		speedLevel.setLocation(targetSpeedLevel);
	}
	
	private void evaluateDodge()
	{
		dodgeTimer--;
		if(dodgeTimer == READY)
		{				
			speedLevel.setLocation(targetSpeedLevel);
			turnLeft();												   
		}		
	}

	private void snooze(boolean inactivation)
	{
		snoozeTimer
			= Math.max(	snoozeTimer,
						SNOOZE_TIME 
						+ (inactivation
							? INACTIVATION_TIME
							  + Calculations.random((int)(EXTRA_INACTIVE_TIME_FACTOR * INACTIVATION_TIME))
							:0));
		speedLevel.setLocation(ZERO_SPEED);
		if(targetSpeedLevel.getY() != 0
		   && getMaxY() + 1.5 * speed.getY() > GROUND_Y)
		{
			setY(GROUND_Y - getHeight());
		}		
		if(burrowTimer != DISABLED)
		{						
			barrierShootTimer = DISABLED;
		}		
		else if(cloakingDevice.isEnabled())
		{
			barrierTeleportTimer = DISABLED;
			barrierShootTimer = DISABLED;
		}
		else if(barrierShootTimer != DISABLED)
		{
			barrierShootTimer = snoozeTimer - SNOOZE_TIME + shootingCycleLength;
		}
	}
	
	private void move()
	{			
		if(!speed.equals(ZERO_SPEED)|| Scenery.backgroundMoves)
		{
			setLocation(
					getX()
						+ getDirectionX() * speed.getX()
						- (Scenery.backgroundMoves ? BG_SPEED : 0),
					Math.max( getModel() == EnemyModelType.BARRIER ? 0 : Integer.MIN_VALUE,
							type == EnemyType.ROCK ? getY() :
								Math.min( canBePositionedBelowGround()
											? Integer.MAX_VALUE
											: GROUND_Y - getHeight(),
										getY()
											+ getDirectionY()
											* speed.getY())));
		}
	}

	private boolean canBePositionedBelowGround()
	{		
		return !(getModel() == EnemyModelType.BARRIER
			     && burrowTimer == DISABLED)
			   || isDestroyed
			   || type == EnemyType.ROCK;
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
			if(explodingTimer == 0){explodingTimer = DISABLED;}
		}
	}
	
	private void calculateSpeed(Helicopter helicopter)
	{		
		if(isStunned())
		{
			adjustSpeedTo(helicopter.missileDrive);
			if(stunningTimer == 1)
			{
				if(getModel() == EnemyModelType.BARRIER){snooze(true);}
				else{isRecoveringSpeed = true;}
			}
		}
		if(getModel() != EnemyModelType.BARRIER){evaluateSpeedBoost();}
		if(isRecoveringSpeed){recoverSpeed();}
		
		speed.setLocation(speedLevel);			//d
		
		if(isEmpSlowed())
		{
			// relevant, wenn mit der PEGASUS-Klasse gespielt wird
			speed.setLocation(	
				speed.getX()
					*((double)(EMP_SLOW_TIME-empSlowedTimer)/EMP_SLOW_TIME),
				speed.getY()
					*((double)(EMP_SLOW_TIME-empSlowedTimer)/EMP_SLOW_TIME));
		}
				
		if(	stoppingBarrier != null
			&& burrowTimer == DISABLED
			&& !(getModel() == EnemyModelType.BARRIER && type == EnemyType.BIG_BARRIER))
		{
			adjustSpeedToBarrier(helicopter);
		}
	}
	
	private boolean isEmpSlowed()
	{		
		return empSlowedTimer > 0;
	}

	private void adjustSpeedTo(int missileDrive)
	{
		if( !speedLevel.equals(ZERO_SPEED)
			&& 
			( totalStunningTime - 13 == stunningTimer
			  || getMaxX()
			  	 + 18 
			  	 + missileDrive/2f > Main.VIRTUAL_DIMENSION.width
			  	 								+ 2 * getWidth()/3
			  || getMinX()
			  	 - 18 
			  	 - missileDrive/2f < - 2 * getWidth()/3))
		{
			speedLevel.setLocation(ZERO_SPEED);
		}
	}	
	
	private void evaluateSpeedBoost()
	{		
		int bottomTurnLine = type == EnemyType.KABOOM
								  ? KABOOM_Y_TURN_LINE 
								  : (int)TURN_FRAME.getMaxY();
									
		if(isSpeedBoosted)
		{
			if(    getMinY() > TURN_FRAME.getMinY()
			    && getMaxY() < bottomTurnLine)
			{
				speedLevel.setLocation(targetSpeedLevel);
				isSpeedBoosted = false;
			}						
		}
		else if(stoppingBarrier != null
				&&(     getMinY() < TURN_FRAME.getMinY()
				    || (getMaxY() > bottomTurnLine)))
		{
			isSpeedBoosted = true;
			speedLevel.setLocation(Math.max(speedLevel.getX(), targetSpeedLevel.getX() + 7.5),
								   Math.max(speedLevel.getY(), 5.5));
			
			// Wenn Gegner droht am Boden durch Barrier zerdrückt zu werden, dann nimmt Gegner den kürzesten Weg.
			if(mustAvoidGroundCollision(bottomTurnLine))
			{
				performXTurnAtBarrier();
			}
		}
	}	

	private boolean mustAvoidGroundCollision(int yTurnLine)
	{		
		return getMaxY() > yTurnLine
				   &&(   (isFlyingRight()
				   			&& getCenterX() < stoppingBarrier.getCenterX())
					   ||(isFlyingLeft()
					   		&& getCenterX() > stoppingBarrier.getCenterX()));
	}
	
	private void recoverSpeed()
	{
		if(	burrowTimer != DISABLED || hasReachedTargetSpeed())
		{
			isRecoveringSpeed = false;
			if(burrowTimer != DISABLED)
			{
				speedLevel.setLocation(SLOW_VERTICAL_SPEED);
			}
			else{speedLevel.setLocation(targetSpeedLevel);}
		}		
		else if(speedLevel.getX() < targetSpeedLevel.getX())
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
		return    speedLevel.getX() >= targetSpeedLevel.getX()
			   && speedLevel.getY() >= targetSpeedLevel.getY();
	}

	private void adjustSpeedToBarrier(Helicopter helicopter)
	{
		if(  hasSameDirectionX(stoppingBarrier)
		   && stoppingBarrier.getCenterX()*getDirectionX()
		   		             < getCenterX()*getDirectionX()
		   && stoppingBarrier.speed.getX() > speed.getX())
		{
			speed.setLocation(	stoppingBarrier.isOnScreen()
									&& !isOnScreen()
			   							? 0 
			   							: stoppingBarrier.speed.getX(),
			   						speed.getY());
		}
		else if( hasSameDirectionY(stoppingBarrier)
				 && stoppingBarrier.getCenterY()*getDirectionY()
				 		           < getCenterY()*getDirectionY()
				 && stoppingBarrier.speed.getY() > speed.getY()
				 && burrowTimer == DISABLED)
		{
			speed.setLocation(speed.getX(), stoppingBarrier.speed.getY());
			if(helicopter.tractor == this){helicopter.stopTractor();}
		}		
	}
	
	private boolean hasSameDirectionX(Enemy otherEnemy)
	{
		return otherEnemy.getDirectionX() == getDirectionX();
	}
	
	private boolean hasSameDirectionY(Enemy otherEnemy)
	{
		return otherEnemy.getDirectionY() == getDirectionY();
	}
	
	public static void updateAllDestroyed(GameRessourceProvider gameRessourceProvider)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		for(Iterator<Enemy> iterator = gameRessourceProvider.getEnemies().get(CollectionSubgroupType.DESTROYED).iterator(); iterator.hasNext();)
		{
			Enemy enemy = iterator.next();
			enemy.updateDead(gameRessourceProvider.getExplosions(), helicopter);
			
			if(	helicopter.basicCollisionRequirementsSatisfied(enemy)
				&& !enemy.hasCrashed)
			{
				enemy.collision(gameRessourceProvider);
			}				
			if(enemy.isMarkedForRemoval)
			{
				enemy.clearImage();
				iterator.remove();
				gameRessourceProvider.getGameEntitySupplier().store(enemy);
			}				
		}
	}

	private void updateDead(Map<CollectionSubgroupType, LinkedList<Explosion>> explosion, Helicopter helicopter)
	{				
		if(collisionDamageTimer > 0){collisionDamageTimer--;}
		if(collisionAudioTimer > 0){collisionAudioTimer--;}
		if( !hasCrashed
		    && getMaxY() + speed.getY() >= crashPositionY)
		{
			handleCrashToTheGround(explosion, helicopter);
		}		
		calculateSpeedDead();
		move();
		if(getMaxX() < 0){isMarkedForRemoval = true;}
		setPaintBounds();
	}
	
	private void handleCrashToTheGround(Map<CollectionSubgroupType, LinkedList<Explosion>> explosion,
										Helicopter helicopter)
	{
		hasCrashed = true;
		speedLevel.setLocation(ZERO_SPEED);
		setY(crashPositionY - getHeight());
		if(type.isServant()){isMarkedForRemoval = true;}
		Audio.play(getCrashToTheGroundSound());
		Explosion.start(explosion, 
						helicopter, 
						getCenterX(),
						getCenterY(),
						getExplosionType(),
						isDetonatingExtraStrong());
	}
	
	protected boolean isDetonatingExtraStrong()
	{
		return false;
	}
	
	protected AudioClip getCrashToTheGroundSound()
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
	
	public boolean isLivingBoss()
	{		
		return isBoss() && !isDestroyed;
	}

	private void collision(GameRessourceProvider gameRessourceProvider)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		boolean playCollisionSound = collisionAudioTimer == READY;
		helicopter.beAffectedByCollisionWith(this, gameRessourceProvider, playCollisionSound);
				
		if(playCollisionSound)
		{
			collisionAudioTimer = Helicopter.NO_COLLISION_DAMAGE_TIME;
		}		
		collisionDamageTimer = Helicopter.NO_COLLISION_DAMAGE_TIME;
			
		if(	isExplodingOnCollisions()
			&& !isInvincible()
			&& !isDestroyed)
		{
			explode( gameRessourceProvider,
						  0, 
						  getExplosionType(),
						  type == EnemyType.KABOOM);
			
			if(	helicopter.canObtainCollisionReward()
				&& !(type == EnemyType.KABOOM))
			{
				grantRewards(gameRessourceProvider, null, helicopter.hasPerformedTeleportKill());
			}
			destroyByHelicopter(gameRessourceProvider);
		}				
		if(	helicopter.isDestinedToCrash())
		{
			helicopter.crash();
		}		
	}

	private void grantRewards(GameRessourceProvider gameRessourceProvider, Missile missile, boolean beamKill)
	{
		gameRessourceProvider.getHelicopter().receiveRewardFor(this, missile, beamKill);
		grantGeneralRewards(gameRessourceProvider);
	}

	public void reactToRadiation(GameRessourceProvider gameRessourceProvider)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		if(	teleportTimer == READY){teleport();}
		else if(canTakeCollisionDamage())
		{
			takeDamage((int)(
				helicopter.currentBaseFirepower
				* (helicopter.bonusKillsTimer > NICE_CATCH_TIME - TELEPORT_KILL_TIME
					? TELEPORT_DAMAGE_FACTOR 
					: RADIATION_DAMAGE_FACTOR)));				
							
			if(getModel() == EnemyModelType.BARRIER)
			{
				if(	helicopter.hasTripleDamage()
					&&  Calculations.tossUp(
							deactivationProbability
							*(helicopter.bonusKillsTimer
								> NICE_CATCH_TIME
								  - TELEPORT_KILL_TIME ? 2 : 1)))
				{
					hitPoints = 0;
				}
				else if(Calculations.tossUp(deactivationProbability *(helicopter.bonusKillsTimer > NICE_CATCH_TIME - TELEPORT_KILL_TIME ? 4 : 2)))
				{
					snooze(true);
				}
			}
			if(hasHPsLeft())
			{
				reactToHit(helicopter, null);
			}
			else
			{
				boolean beamKill = helicopter.bonusKillsTimer > 0;
				dieFromRadiation(gameRessourceProvider, beamKill);
			}
		}		
	}

	private boolean canTakeCollisionDamage()
	{		
		return 	   !isDestroyed
				&& !isExplodingOnCollisions()
				&& !isInvincible()
				&& !(barrierTeleportTimer != DISABLED && barrierShootTimer == DISABLED)
				&& collisionAudioTimer == READY;
	}
	
	public float collisionDamage(Helicopter helicopter)
	{		
		return helicopter.getProtectionFactor()
				// TODO 0.65 und 1.0 in Konstanten auslagern
			   *helicopter.getBaseProtectionFactor(isExplodingOnCollisions())
			   *(helicopter.isTakingKaboomDamageFrom(this)
			     ? helicopter.kaboomDamage()
			     : (isExplodingOnCollisions() && !isInvincible() && !isDestroyed)
					? 1.0f 
					: collisionDamageTimer > 0
						? 0.0325f 
						: 0.65f);
	}
	
	void takeDamage(int dmg)
	{
		hitPoints -= dmg;
		if(!hasHPsLeft()){hitPoints = 0;}
	}
	
	public void hitByMissile(GameRessourceProvider gameRessourceProvider, Missile missile)
	{
		gameRessourceProvider.getGameStatisticsCalculator()
							 .incrementHitCounter();
				
		if(missile.hasGreatExplosivePower())
		{
			Audio.play(Audio.explosion4);
		}
		else{Audio.play(Audio.explosion2);}
		missile.hits.put(hashCode(), this);
		takeDamage(missile.dmg);
		if(getModel() == EnemyModelType.BARRIER)
		{
			if(missile.hasGreatExplosivePower()
				&& Calculations.tossUp(	0.5f
									* deactivationProbability
									* (missile.hasGreatExplosivePower() ? 2 : 1)))
			{
				hitPoints = 0;
			}
			else if(isToBeInactivatedBy(missile))
			{
				snooze(true);
			}
		}		
		if(areStunningRequirementsMet(missile))
		{
			stun(gameRessourceProvider, missile);
		}
	}
	
	private boolean isToBeInactivatedBy(Missile missile)
	{
		return Calculations.tossUp(deactivationProbability
									  * missile.typeOfExplosion.getBarrierDeactivationProbabilityFactor());
	}
	
	private boolean areStunningRequirementsMet(Missile missile)
	{
		return missile.typeOfExplosion == STUNNING
			&& isStunable()
			&& nonStunableTimer == READY;
	}
	
	private void stun(GameRessourceProvider gameRessourceProvider, Missile missile)
	{
		if(hasHPsLeft()){Audio.play(Audio.stun);}
		explode(gameRessourceProvider, missile);
		nonStunableTimer = (int)(type.isMainBoss() || type.isFinalBossServant()
										  ? 2.25f*Events.level 
										  : 0);
		knockBackDirection = missile.speed > 0 ? 1 : -1;
		
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		// TODO in Methoden auslagern, Code verständlicher machen
		speedLevel.setLocation(
				(knockBackDirection == getDirectionX() ? 1 : -1)
				  *(type.isMainBoss() || type.isFinalBossServant()
				    ? (10f + helicopter.missileDrive)/(Events.level/10f)
					:  10f + helicopter.missileDrive),
				0);
						
		stunningTimer = totalStunningTime
			= (int)(17 + STUNNING_TIME_BASIS 
					     * (type.isMajorBoss() ? (10f/Events.level) : 2.5f));
				
		disableSiteEffects(helicopter);
	}

	private void disableSiteEffects(Helicopter helicopter)
	{
		if(helicopter.tractor == this){helicopter.stopTractor();}
		if(isUncloakingWhenDisabled())
		{
			uncloakAndSetCloakingDeviceReadyForUse();
		}		
	}
	
	protected boolean isUncloakingWhenDisabled()
	{
		return cloakingDevice.isActive();
	}
	
	public void reactToHit(Helicopter helicopter, Missile missile)
	{
		if(isReadyToDodge(helicopter))
		{
			dodge(missile);
		}
		if(canDoHitTriggeredTurn())
		{
			performHitTriggeredTurn(helicopter);
		}

		if(cloakingDevice.isReadyToBeUsed() && !(tractor == AbilityStatusType.ACTIVE))
		{
			Audio.play(Audio.cloak);
			cloakingDevice.activate();
		}
		if( missile != null 
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
	
	protected boolean prolongCloakingTimeWhenHitWhileCloaked()
	{
		return isCloaked();
	}
	
	protected void uncloakTriggeredByStunningMissile()
	{
		uncloakAndSetCloakingDeviceReadyForUse();
	}
	
	protected void performHitTriggeredTurn(Helicopter helicopter)
	{
		if(getMinX() > helicopter.getMinX())
		{
			turnLeft();
		}
		else if(getMaxX() < helicopter.getMaxX())
		{
			turnRight();
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
		explode(gameRessourceProvider, missile.speed, missile.typeOfExplosion, missile.extraDamage);
	}
	
	private void explode(GameRessourceProvider gameRessourceProvider, double missileSpeed, ExplosionType explosionType, boolean extraDamage)
	{
		// TODO refactoring
		if(explodingTimer == 0){explodingTimer = 7;}
		Explosion.start(gameRessourceProvider.getExplosions(),
						gameRessourceProvider.getHelicopter(),
						getX() + ((explosionType != ExplosionType.EMP && getModel() != EnemyModelType.BARRIER)
							? (missileSpeed < 0 ? 2 : 1) * getWidth()/3
							: getWidth()/2),
						getY() + getHeight()/2,
						explosionType,
						extraDamage);
	}
	
	public void destroyByHelicopter(GameRessourceProvider gameRessourceProvider)
	{
		writeDestructionStatistics(gameRessourceProvider.getGameStatisticsCalculator());
		beDestroyed(gameRessourceProvider.getHelicopter());
	}
	
	protected void writeDestructionStatistics(GameStatisticsCalculator gameStatisticsCalculator)
	{
		gameStatisticsCalculator.incrementNumberOfEnemiesKilled();
	}
	
	void destroyByCrash(GameRessourceProvider gameRessourceProvider)
	{
		evaluatePowerUpDrop(gameRessourceProvider);
		beDestroyed(gameRessourceProvider.getHelicopter());
	}
	
	private void evaluatePowerUpDrop(GameRessourceProvider gameRessourceProvider)
	{
		if(areALlRequirementsForPowerUpDropMet())
		{
			dropRandomPowerUp(gameRessourceProvider);
		}
	}
	
	private void beDestroyed(Helicopter helicopter)
	{
		isDestroyed = true;
		if(cloakingDevice.isActive()){uncloakAndDisableCloakingDevice();}
		teleportTimer = DISABLED;
		primaryColor = Colorations.adjustBrightness(primaryColor, Colorations.DESTRUCTION_DIM_FACTOR);
		secondaryColor = Colorations.adjustBrightness(secondaryColor, Colorations.DESTRUCTION_DIM_FACTOR);
		
		repaint();
		
		if(helicopter.tractor == this)
		{
			helicopter.stopTractor();
		}
		speedLevel.setLocation(0, 12);
		flyDown();
		
		empSlowedTimer = READY;
		crashPositionY = (int)(isIntersectingGroundLine()
								? getMaxY()
								: GROUND_Y + 1 + Math.random() * 0.25 * getHeight());
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
		alpha = 255;
		primaryColor = Colorations.setAlpha(primaryColor, 255);
		secondaryColor = Colorations.setAlpha(secondaryColor, 255);
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
		else if(missile.typeOfExplosion != STUNNING)
		{
			explode(gameRessourceProvider, missile);
		}		
		
		evaluateBossDestructionEffect(gameRessourceProvider);
		if(missile != null){missile.hits.remove(hashCode());}
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
				? REPARATION
				: PowerUpType.getValues().get(getRandomIndexOfDroppablePowerUp());
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
	
	public int calculateReward(Helicopter helicopter)
	{		
		return helicopter.getBonusFactor() * getEffectiveStrength() + getRewardModifier();
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
		if(getY() > 143){flyUp();}
		else{flyDown();}
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
				turnAround();
			}
			speedLevel.setLocation(6, 6);
			dodgeTimer = 16;
		}
	}
	
	private boolean hasEnoughDistanceFromScreenBordersToDodgeAway()
	{
		return 	   (isFlyingLeft() && getMaxX() < DODGE_BORDER_DISTANCE_RIGHT)
				|| (isFlyingRight()  && getMaxX() > DODGE_BORDER_DISTANCE_LEFT);
	}
	
	private boolean isFlyingTowardsMissile(Missile missile)
	{
		return 	   (missile.isFlyingRight() && isFlyingLeft())
				|| (missile.isFlyingLeft()  && isFlyingRight());
	}
	


	protected void setShieldingPosition()
	{
		if(!Events.boss.operator.containsServant(shieldingBrother()))
		{
			isUpperShieldMaker = Calculations.tossUp();
		}
		else
		{
			isUpperShieldMaker
				= !Events.boss.getOperatorServant(shieldingBrother()).isUpperShieldMaker;
		}		
	}

	private FinalBossServantType shieldingBrother()
	{		
		return type == EnemyType.SMALL_SHIELD_MAKER
							 ? FinalBossServantType.BIG_SHIELD_MAKER
							 : FinalBossServantType.SMALL_SHIELD_MAKER;
	}

	public void teleport()
	{
		Audio.play(Audio.teleport2);		
		setLocation(260.0 + Math.random()*(660.0 - getWidth()),
						   20.0 + Math.random()*(270.0 - getHeight()));
		speedLevel.setLocation(ZERO_SPEED);
		teleportTimer = 60;
		invincibleTimer = 40;
	}
	
	public boolean isStaticallyCharged()
	{		
		return staticChargeTimer == READY
   	 		   && snoozeTimer <= SNOOZE_TIME;
	}
	
	public void startStaticDischarge(Map<CollectionSubgroupType, LinkedList<Explosion>> explosion,
									 Helicopter helicopter)
	{
		staticChargeTimer = STATIC_CHARGE_TIME;
		helicopter.receiveStaticCharge(2.5f);
		Audio.play(Audio.emp);
		Explosion.start(explosion, helicopter, (int)getCenterX(), (int)getCenterY(), STUNNING, false, this);
	}

	public boolean isHittable(Missile missile)
	{		
		return !isDestroyed
			   && !(barrierTeleportTimer != DISABLED && alpha != 255)
			   && missile.intersects(this)
			   && !missile.hits.containsKey(hashCode());
	}

	public boolean isReadyToDodge(Helicopter helicopter)
	{
		// TODO in weitere kleinere verständliche Methoden auslagern
		return 	    canDodge
				&&  dodgeTimer == READY
				&& !isEmpSlowed()
				&& !isDestroyed
				&& !(type == EnemyType.HEALER
					 && Events.boss.shield > 0 
					 && getMinX() > Events.boss.getMinX()
				     && getMaxX() < Events.boss.getMaxX())
				&& !( (     (helicopter.getX() - getMaxX() > -500)
						 && (helicopter.getX() - getX() 	  <  150))
					  && canKamikaze
					  && isFlyingLeft());
	}

	public boolean isInvincible()
	{		
		return    invincibleTimer > 0
			   || shield > 0;
	}

	public void evaluatePosAdaption(Helicopter helicopter)
	{
		if(helicopter.isLocationAdaptionApproved(this))
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

	public static void getRidOfSomeEnemies(GameRessourceProvider gameRessourceProvider)
	{
		for(Enemy e : gameRessourceProvider.getEnemies().get(CollectionSubgroupType.ACTIVE))
		{
			if (e.getModel() == EnemyModelType.BARRIER && e.isOnScreen())
			{
				e.explode(gameRessourceProvider);
				e.destroyByHelicopter(gameRessourceProvider);
			} else if (!e.isOnScreen())
			{
				e.isMarkedForRemoval = true;
			}
		}		
	}
	
	public boolean hasHPsLeft()
	{
		return hitPoints >= 1;
	}

	public void setTouchedSiteToRight() {
		touchedSite = RIGHT;
	}

	public void setTouchedSiteToLeft() {
		touchedSite = LEFT;
	}

	public void setTouchedSiteToBottom()
	{
		touchedSite = BOTTOM;
	}

	public void setTouchedSiteToTop()
	{
		touchedSite = TOP;
	}

	public void setUntouched()
	{
		touchedSite = NONE;
	}

	public boolean isUntouched()
	{
		return touchedSite == NONE;
	}
	
	public boolean isKaboomDamageDealer()
	{
		return type == EnemyType.KABOOM && !isDestroyed;
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
	
	public Enemy getOperatorServant(FinalBossServantType servantType)
	{
		return operator.getServant(servantType);
	}
	
	public Point2D getSpeedLevel()
	{
		return speedLevel;
	}
	
	public int getLifetime()
	{
		return lifetime;
	}
	
	public AbilityStatusType getTractor()
	{
		return tractor;
	}
	
	public BufferedImage[] getImage()
	{
		return image;
	}
	
	public int getRotorColor()
	{
		return rotorColor;
	}
	
	public int getSnoozeTimer()
	{
		return snoozeTimer;
	}
	
	public boolean isShielding()
	{
		return isShielding;
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
	public boolean isStunable()
	{
		return true;
	}
	
	protected boolean isExplodingOnCollisions()
	{
		return type.isExplodingOnCollisions();
	}
	
	public boolean isPushingHelicopter(Helicopter helicopter)
	{
		return intersects(helicopter) && alpha == 255 && burrowTimer != 0;
	}
	
	public boolean canCollide()
	{
		return true;
	}
	
	protected void finalBossServantRemoval()
	{
		FinalBossServantType.of(type).ifPresent(servantType -> {
			Events.boss.operator.remove(servantType);
			Events.boss.operator.resetTimeSinceDeath(servantType);
		});
	}
	
	public Color getBarColor(boolean isImagePaint)
	{
		if(isDestroyed())
		{
			return getDefaultBarColor();
		}
		if(getTractor() == AbilityStatusType.ACTIVE || getShootTimer() > 0 || isShielding())
		{
			return Colorations.variableGreen;
		}
		if(!isImagePaint && isInvincible())
		{
			return Color.green;
		}
		return getDefaultBarColor();
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
	public final boolean isFlyingLeft()
	{
		return getDirectionX() == -1;
	}
	
	public final void turnLeft()
	{
		direction.x = -1;
	}
	
	public final boolean isFlyingRight()
	{
		return getDirectionX() == 1;
	}
	
	public final void turnRight()
	{
		direction.x = 1;
	}
	
	public int getDirectionX()
	{
		return direction.x;
	}
	
	public final void turnAround()
	{
		direction.x = -direction.x;
	}
	
	public final void setRandomDirectionX()
	{
		direction.x = Calculations.randomDirection();
	}
	
	public final int getNegativeDirectionX()
	{
		return -direction.x;
	}
	
	public final boolean isFlyingDown()
	{
		return direction.y == 1;
	}
	
	public final void flyDown()
	{
		direction.y = 1;
	}
	
	public final boolean isFlyingUp()
	{
		return getDirectionY() == -1;
	}
		
	public final void flyUp()
	{
		direction.y = -1;
	}
	
	public final int getDirectionY()
	{
		return direction.y;
	}
	
	public final void switchDirectionY()
	{
		direction.y = -getDirectionY();
	}
	
	public final void setRandomDirectionY()
	{
		direction.y = Calculations.randomDirection();
	}
	
	private void increaseSpeedLevelX(double increment)
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
	public GameEntityGroupType getGroupType()
	{
		return GameEntityGroupType.ENEMY;
	}
	
	protected boolean isMovingAwayFrom(RectangularGameEntity gameEntity)
	{
		return    (isLeftOf(gameEntity)  && isFlyingLeft())
			   || (isRightOf(gameEntity) && isFlyingRight());
	}
	
	private boolean isWithinKamikazeRangeOf(RectangularGameEntity gameEntity)
	{
		return getDistanceOfMaxX(gameEntity) < KAMIKAZE_RANGE
		       && (   (!isLeftOf(gameEntity) && isFlyingLeft())
			       || (!isRightOf(gameEntity) && isFlyingRight()));
	}
	

	
	private double getDistanceOfMaxX(RectangularGameEntity gameEntity)
	{
		return Math.abs(getMaxX() - gameEntity.getMaxX());
	}
	
	protected void setCloakingDeviceReadyForUse()
	{
		cloakingDevice.setReadyForUse();
	}
	
	protected CloakingDevice getCloakingDevice()
	{
		return cloakingDevice;
	}
}