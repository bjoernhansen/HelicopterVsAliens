package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.EnemyPainter;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
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
import java.util.function.Predicate;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.DESTROYED;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.BOTTOM;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.LEFT;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.NONE;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.RIGHT;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.TOP;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.EMP;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.JUMBO;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PHASE_SHIFT;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PLASMA;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.STUNNING;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.NICE_CATCH_TIME;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.TELEPORT_KILL_TIME;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.BUSTER;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.DISCHARGER;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.REPARATION;
import static de.helicopter_vs_aliens.model.scenery.SceneryObject.BG_SPEED;


public abstract class Enemy extends RectangularGameEntity
{
	public static class FinalEnemyOperator
	{
		private final EnumMap<FinalBossServantType, Enemy>
			servants = new EnumMap<>(FinalBossServantType.class);
		
		private final EnumMap<FinalBossServantType, Integer>
			timeSinceDeath = new EnumMap<>(FinalBossServantType.class);
		
		public boolean hasMinimumTimeBeforeRecreationElapsed(FinalBossServantType servantType)
		{
			return this.timeSinceDeath.get(servantType) > servantType.getMinimumTimeBeforeRecreation();
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
		MIN_STARTING_Y = 90;
	
	private static final float
		PRIMARY_COLOR_BRIGHTNESS_FACTOR = 1.3f,
		SECONDARY_COLOR_BRIGHTNESS_FACTOR = 1.5f;
	
	// Konstanten
	public static final int
		CLOAKING_TIME = 135, // Zeit, die beim Vorgang der Tarnung und Enttarnung vergeht
		CLOAKED_TIME = 135,     // Zeit, die ein Gegner getarnt bleibt
		SNOOZE_TIME = 100,    // Zeit, die vergeht, bis sich ein aktives Hindernis in Bewegung setzt
		DISABLED = -1; // TODO unnötig machen
	
	public static final Point2D
		ZERO_SPEED = new Point2D.Float(0, 0);
	
	private static final float
		RADAR_DETECTABILITY = 0.2f;        // Alpha-Wert: legt fest, wie stark ein getarnter Gegner bei aktiviertem Radar noch zu sehen ist
	
		private static final float POWER_UP_PROB				= 0.02f;
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
	
		private static final int EMP_SLOW_TIME = 175;    // Zeit, die von EMP getroffener Gegner verlangsamt bleibt // 113
		private static final int EMP_SLOW_TIME_BOSS = 110;
	private static final int INACTIVATION_TIME = 150;
	private static final int STUNNING_TIME_BASIS = 45;    // Basis-Wert zur Berechnung der Stun-Zeit nach Treffern von Stopp-Raketen
		private static final int BORROW_TIME = 65;
	private static final int MIN_TURN_TIME = 31;
	private static final int MIN_TURN_NOISELESS_TIME = 15;
	private static final int STATIC_CHARGE_TIME = 110;
	

	
	public static final int
		MIN_POWER_UP_LEVEL = 3;
	

	private static final int STANDARD_REWARD_FACTOR = 1;
	private static final int MINI_BOSS_REWARD_FACTOR = 4;
	
	private static final int// TODO die 4 austauschen / anders lösen
		PRE_READY = 1;
	protected static final int READY = 0;
	private static final int ACTIVE_TIMER = 1;
	
	private static final Point
		TURN_DISTANCE = new Point(50, 10),
		SHIELD_MAKER_STAMPEDE_SPEED = new Point(10, 10);
		
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
		isMiniBoss,					// = true: Gegner ist ein Mini-Boss
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

	// TODO Zugriff ändern (moveToTheLeft() moveToTheRight() isMovingLeft() isMovingRight()
    public final Point
    	direction = new Point();		// Flugrichtung
    	
	Enemy
		stoppingBarrier,		// Hindernis-Gegner, der diesen Gegner aufgehalten hat
		isPreviousStoppingBarrier;
	
	private int
		hitPoints;						// aktuelle HitPoints
	
	private int
		lifetime;                // Anzahl der Frames seit Erstellung des Gegners; und vergangene Zeit seit Erstellung, Zeit
	private int
		yCrashPos;                // Bestimmt wie tief ein Gegner nach Absturz im Boden versinken kann
	private int
		collisionAudioTimer;
	private int
		turnAudioTimer;
	private int
		explodingTimer;            // Timer zur überwachung der Zeit zwischen Abschuss und Absturz
	protected int
		cloakingTimer;            // reguliert die Tarnung eines Gegners; = DISABLED: Gegner kann sich grundsätzlich nicht tarnen
	protected int
		uncloakingSpeed;
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
	private int
		turnTimer;
	protected int
		dodgeTimer;            // Zeit [frames], bis ein Gegner erneut ausweichen kann
	protected int
		snoozeTimer;
	protected int
		staticChargeTimer;
	
	protected int// nur für Hindernis-Gegner relevant
		rotorColor;
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
		deactivationProb;
	       
        protected boolean canDodge;                // = true: Gegner kann Schüssen ausweichen
        protected boolean canKamikaze;            // = true: Gegner geht auf Kollisionskurs, wenn die Distanz zum Helicopter klein ist
        protected boolean canLearnKamikaze;        // = true: Gegner kann den Kamikaze-Modus einschalten, wenn der Helikopter zu nahe kommt
        protected boolean canEarlyTurn;
	protected boolean canMoveChaotic;        // reguliert den zufälligen Richtungswechsel bei Chaos-Flug-Modus
        protected boolean canSinusMove;            // Gegner fliegt in Kurven ähnlicher einer Sinus-Kurve
        protected boolean canTurn;                // Gegner ändert bei Beschuss eventuell seine Flugrichtung in Richtung Helikopter
        protected boolean canInstantTurn;            // Gegner ändert bei Beschuss immer(!) seine Flugrichtung in Richtung Helikopter
        private boolean canFrontalSpeedup;        // Gegner wird schneller, wenn Helikopter ihm zu Nahe kommt
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
        protected boolean isCarrier;                // = true
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
	
	private final BufferedImage []
		image = new BufferedImage[4];
		
	protected final Point2D
		targetSpeedLevel = new Point2D.Float();        // Anfangsgeschwindigkeit
		private final Point2D speedLevel = new Point2D.Float();            // auf Basis dieses Objektes wird die tatsächliche Geschwindigkeit berechnet
		private final Point2D speed = new Point2D.Float();                // tatsächliche Geschwindigkeit
		protected final Point2D shootingDirection = new Point2D.Float();   	// Schussrichtung von schießenden Barrier-Gegnern
	
	public Enemy()
	{
		this.lifetime = 0;
		this.targetSpeedLevel.setLocation(ZERO_SPEED);
		this.direction.setLocation(-1, Calculations.randomDirection());
		this.callBack = 0;
		this.shield = 0;
		this.operator = null;
		this.alpha = 255;
		
		this.isDestroyed = false;
		this.isMarkedForRemoval = false;
		this.hasUnresolvedIntersection = false;
		this.canMoveChaotic = false;
		this.canDodge = false;
		this.canChaosSpeedup = false;
		this.canKamikaze = false;
		this.canEarlyTurn = false;
		this.isTouchingHelicopter = false;
		this.isSpeedBoosted = false;
		this.canLearnKamikaze = false;
		this.canFrontalSpeedup = false;
		this.canSinusMove = false;
		this.isShielding = false;
		this.canTurn = false;
		this.canLoop = false;
		this.isClockwiseBarrier = true;
		this.stoppingBarrier = null;
		this.isPreviousStoppingBarrier = null;
		this.isMiniBoss = false;
		this.hasCrashed = false;
		this.canInstantTurn = false;
		this.isCarrier = false;
		this.isRecoveringSpeed = false;
		this.hasHeightSet = false;
		this.isEmpShocked = false;
		
		this.collisionDamageTimer = READY;
		this.collisionAudioTimer = READY;
		this.turnAudioTimer = READY;
		this.dodgeTimer = READY;
		this.turnTimer = READY;
		this.explodingTimer = READY;
		this.empSlowedTimer = READY;
		this.invincibleTimer = READY;
		this.chaosTimer = READY;
		this.snoozeTimer = READY;
		this.nonStunableTimer = READY;
		
		this.spawningHornetTimer = DISABLED;
		this.cloakingTimer = DISABLED;
		this.teleportTimer = DISABLED;
		this.shieldMakerTimer = DISABLED;
		this.shootTimer = DISABLED;
		this.barrierShootTimer = DISABLED;
		this.barrierTeleportTimer = DISABLED;
		this.burrowTimer = DISABLED;
		this.staticChargeTimer = DISABLED;
		this.speedup = DISABLED;
		
		this.uncloakingSpeed = 1;
		this.tractor = AbilityStatusType.DISABLED;
		this.batchWiseMove = 0;
		
		this.shootingDirection.setLocation(0, 0);
		this.shootPause = 0;
		this.shootingRate = 0;
		this.shotsPerCycle = 0;
		this.shootingCycleLength = 0;
		this.shotSpeed = 0;
		this.shotRotationSpeed = 0;
		this.shotType = DISCHARGER;
		
		this.touchedSite = NONE;
		this.lastTouchedSite = NONE;
		
		this.untouchedCounter = 0;
		this.rotorColor = 0;
		this.deactivationProb = 0f;
		this.stunningTimer = READY;
		
		this.totalStunningTime = 0;
		this.knockBackDirection = 0;
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
    	for(int i = 0; i < this.image.length; i++)
    	{
    		this.image[i] = null; 
    		if(i < 2){this.graphicsAdapters[i] = null;}
    	}
    }
    
    // TODO gehört diese Methode nicht eher in die Painter-Klasse?
	public void repaint()
	{
		for(int j = 0; j < 2; j++)
		{		
			if(getModel() != BARRIER)
			{
				// TODO hier taucht null pointer exception auf. Warum?
				this.graphicsAdapters[j].setComposite(AlphaComposite.Src);
				this.graphicsAdapters[j].setColor(Colorations.translucentDarkestBlack);
				this.graphicsAdapters[j].fillRect(0, 0, this.image[j].getWidth(), this.image[j].getHeight());
			}
			EnemyPainter enemyPainter = GraphicsManager.getInstance().getPainter(this.getClass());
			enemyPainter.paintImage(this.graphicsAdapters[j], this, 1-2*j, null, true);
		}
	}
	
	public boolean countsForTotalAmountOfEnemiesSeen()
	{
		// TODO countsForTotalAmountOfEnemiesSeen implementieren, an anderen Stellen die -- aufrufe streichen; finale instanzvariable anlegen
		return true;
	}
	
	public void initialize(Helicopter helicopter)
	{
		setBasicProperties();
		doTypeSpecificInitialization();
		finalizeInitialization(helicopter);
	}
	
	private void setTargetSpeedLevel()
	{
		Point2D targetSpeedLevel = type.calculateTargetSpeed();
		this.targetSpeedLevel.setLocation(targetSpeedLevel);
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
	
	protected void finalizeInitialization(Helicopter helicopter)
	{
		setInitialLocation(helicopter);
		
		this.speedLevel.setLocation(this.targetSpeedLevel);
		this.setPaintBounds((int)this.getWidth(),
							(int)this.getHeight());
		this.assignImage(helicopter);
		
		if(isShootingStandardEnemy())
		{
			this.initializeShootDirectionOfDefaultEnemies();
		}
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
		return MIN_STARTING_Y + Math.random()*(MAX_STARTING_Y - this.getHeight());
	}
	
	
	// TODO Methode überarbeiten und Teile in kleinere Methoden auslagern
	private void assignImage(Helicopter helicopter)
	{
		for(int i = 0; i < 2; i++)
		{
			this.image[i] = new BufferedImage((int)(1.028f * this.paintBounds.width),
											  (int)(1.250f * this.paintBounds.height),
											  BufferedImage.TYPE_INT_ARGB);
			this.graphicsAdapters[i] = Graphics2DAdapter.withAntialiasing(this.image[i]);
			//this.graphics[i].setComposite(AlphaComposite.Src);
			
			EnemyPainter enemyPainter = GraphicsManager.getInstance().getPainter(this.getClass());
			enemyPainter.paintImage(this.graphicsAdapters[i], this,1-2*i, null, true);
			if(this.cloakingTimer != DISABLED && helicopter.getType() == HelicopterType.OROCHI)
			{
				BufferedImage 
					 tempImage = new BufferedImage((int)(1.028f * this.paintBounds.width),
							 						(int)(1.250f * this.paintBounds.height),
							 						BufferedImage.TYPE_INT_ARGB);
				
				this.image[2+i] = new BufferedImage((int)(1.028f * this.paintBounds.width),
													(int)(1.250f * this.paintBounds.height),
													BufferedImage.TYPE_INT_ARGB);
				
				enemyPainter.paintImage(Graphics2DAdapter.withAntialiasing(tempImage), this,1-2*i, Color.red, true);
				Graphics2DAdapter.withAntialiasing(this.image[2+i]).drawImage(tempImage, ROP_CLOAKED, 0, 0);
			}
		}		
	}

    private void placeCloakingBarrierAtPausePosition()
	{
		this.callBack--;
		this.uncloak(DISABLED);
		this.barrierTeleportTimer = READY;
		this.setY(GROUND_Y + 2 * this.getWidth());
	}

	private void placeNearHelicopter(Helicopter helicopter)
	{		
		boolean isLeftOfHelicopter = !(helicopter.getMaxX() + (0.5f * this.getWidth() + BARRIER_DISTANCE) < 1024);
					
		int x, 
			y = (int)(helicopter.getY()
				+ helicopter.getHeight()/2
				- this.getWidth()
				+ Math.random()*this.getWidth());
		
		if(isLeftOfHelicopter)
		{
			x = (int)(helicopter.getX()
				-3*this.getWidth()/2
				- 10
				+ Math.random()*(this.getWidth()/3));
		}
		else
		{
			x = (int)(helicopter.getMaxX()
				+ BARRIER_DISTANCE);		
		}
		// TODO in Methode auslagern
		this.setLocation(x, Math.max(0, Math.min(y, GROUND_Y-this.getWidth())));
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
		float shootingDirectionX = (float) this.direction.x;
		this.shootingDirection.setLocation( shootingDirectionX, 0f);
	}
	
	private boolean isShootingStandardEnemy()
	{
		return this.shootTimer == READY;
	}
	
	private static boolean turnaroundIsTurnAway(double dir, double enemyCenter,
												double barrierCenter)
	{
		return 	   dir ==  1 && enemyCenter < barrierCenter
				|| dir == -1 && enemyCenter > barrierCenter;
	}
	
	public boolean isVisibleNonBarricadeVessel(boolean hasRadarDevice)
	{
		return this.type != EnemyType.ROCK
			&& getModel() != BARRIER
			&& !(this.cloakingTimer > CLOAKING_TIME
				&& this.cloakingTimer <= CLOAKING_TIME + CLOAKED_TIME
				&& !hasRadarDevice);
	}
	
	public final void update(Controller controller, Helicopter helicopter)
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
			performFlightManeuver(controller, helicopter);
			validateTurns();
		}		
		calculateSpeed(helicopter);
		move();
		
		if(helicopter.canCollideWith(this))
		{
			collision(controller, helicopter);
		}
		if(helicopter.getType() == HelicopterType.PEGASUS)
		{
			checkForEmpStrike(controller, (Pegasus)helicopter);
		}
		if(hasDeadlyGroundContact())
		{
			destroy(helicopter, controller.powerUps, false);
		}
		if(isToBeRemoved())
		{
			prepareRemoval();
		}
		setPaintBounds();
	}
	
	protected void performFlightManeuver(Controller controller, Helicopter helicopter)
	{
		this.calculateFlightManeuver(controller, helicopter);
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
		return getMaxY() > GROUND_Y
			   && getModel() != BARRIER
			   && !this.isDestroyed;
	}

	protected void checkForBarrierCollision()
	{
		this.isPreviousStoppingBarrier = this.stoppingBarrier;
		if(this.stoppingBarrier == null || !this.stoppingBarrier.intersects(this.getBounds()))
		{
			this.stoppingBarrier = null;
			for(int i = 0; i < EnemyController.currentNumberOfBarriers; i++)
			{
				if(	   EnemyController.livingBarrier[i] != this
					&& EnemyController.livingBarrier[i].intersects(this.getBounds()))
					
				{
					this.stoppingBarrier = EnemyController.livingBarrier[i];
					break;
				}
			}
		}
	}

	private void tryToTurnAtBarrier()
	{		
		this.turnTimer = MIN_TURN_TIME;
		if(this.isOnScreen()
		   && this.stoppingBarrier.isOnScreen()
		   && this.stoppingBarrier != this.isPreviousStoppingBarrier
		   && this.turnAudioTimer == READY)
		{
			Audio.play(Audio.landing);
			this.turnAudioTimer = MIN_TURN_NOISELESS_TIME;
		}
				
		if(this.hasLateralFaceTouchWith(this.stoppingBarrier))
		{
			if(	turnaroundIsTurnAway(this.direction.x,
			   							 this.getCenterX(),
			   							 this.stoppingBarrier.getCenterX())
			   	// Gegner sollen nicht an Barriers abdrehen, bevor sie im Bild waren.					
				&& this.isOnScreen())
			{							
				this.performXTurnAtBarrier();
			}
		}
		else
		{
			if(turnaroundIsTurnAway(this.direction.y,
										this.getCenterY(),
										this.stoppingBarrier.getCenterY()))
			{	
				this.direction.y = -this.direction.y;				
			}
		}		
	}
		
	boolean hasLateralFaceTouchWith(Enemy barrier)
	{
		return  
			Calculations.getIntersectionLength(	this.getMinX(),
											this.getMaxX(),
											barrier.getMinX(),
											barrier.getMaxX())
			<										 									 
			Calculations.getIntersectionLength(	this.getMinY(),
											this.getMaxY(),
											barrier.getMinY(),
											barrier.getMaxY());
	}

	private void performXTurnAtBarrier()
	{
		this.direction.x = -this.direction.x;
		if(this.callBack <= (this.isMiniBoss ? 2 : 0))
		{
			this.callBack++;
		}		
	}

	private void updateTimer()
	{		
		if(	this.collisionDamageTimer > 0) {this.collisionDamageTimer--;}
		if(	this.collisionAudioTimer > 0) {this.collisionAudioTimer--;}
		if(	this.turnAudioTimer > 0) {this.turnAudioTimer--;}
		if(	this.empSlowedTimer > 0) {this.empSlowedTimer--;}
		if(	this.staticChargeTimer > 0) {this.staticChargeTimer--;}
		if(	this.invincibleTimer > 0) {this.invincibleTimer--;}
		if(	this.chaosTimer > 0) {this.chaosTimer--;}
		if(	this.nonStunableTimer > 0) {this.nonStunableTimer--;}
		if( this.turnTimer > 0) {this.turnTimer--;}
		if( this.isStunned()) {this.stunningTimer--;}
	}

	private void calculateFlightManeuver(Controller controller,
										 Helicopter helicopter)
	{				
		// Beschleunigung
		if(    this.speedup != DISABLED 
			|| this.canFrontalSpeedup)
		{
			evaluateSpeedup(helicopter);
		}	
				
		// Schubweises Fliegen
		if(this.batchWiseMove != 0){
			evaluateBatchWiseMove();}
					
		// Chaos-Flug
		if(    this.canMoveChaotic
			&& this.chaosTimer == READY
			&& this.dodgeTimer == READY)
		{
			if( Calculations.tossUp(0.2f)
			    && this.type.isShieldMaker())
			{
				this.direction.x = -this.direction.x;
			}
			if( Calculations.tossUp(0.2f))
			{
				this.direction.y = -this.direction.y;
			}			
			this.chaosTimer = 5;
		}
		
		// Early x-Turn
		if( this.canEarlyTurn
			&& this.getMinX() < 0.85 * Main.VIRTUAL_DIMENSION.width)
		{
			this.canEarlyTurn = false;
			this.direction.x = 1;
		}
							
		// Frontal-Angriff
		if( this.canLearnKamikaze
				
			&& ((this.direction.x == 1 
					&& helicopter.getMaxX() < this.getMinX()
					&& this.getX() - helicopter.getX() < 620)
				||
				(this.direction.x == -1 
					&& this.getMaxX() < helicopter.getMinX()
					&& helicopter.getX() - this.getX() < 620)))
		{
			this.startKamikazeMode();
			this.direction.x = -1;
		}			
		if(	this.canKamikaze && !(this.teleportTimer > 0)){this.kamikaze(helicopter);}
			
		// Vergraben			
		if(this.burrowTimer != DISABLED && !(this.snoozeTimer > 0))
		{				
			evaluateBorrowProcedure(helicopter);
		}
								
		// Shooting
		if(this.shootTimer != DISABLED){evaluateShooting(controller, helicopter);}
		if(this.barrierShootTimer != DISABLED)
		{
			evaluateBarrierShooting(controller, helicopter);
		}									
		
		// Snooze bei Hindernissen									
		if(this.snoozeTimer == PRE_READY)
		{
			this.endSnooze();
		}
		
		// Barrier-Teleport			
		if(this.barrierTeleportTimer != DISABLED
			&& !(this.snoozeTimer > 0))
		{				
			evaluateBarrierTeleport(helicopter);
		}				
				
		// Sinus- und Loop-Flug
		if(this.canSinusMove || this.canLoop){this.sinusLoop();}
		
		// tarnen
		if(this.cloakingTimer > 0
			&& (!this.isEmpSlowed() || this.canLearnKamikaze))
		{
			this.cloaking();
		}
		
		// Tractor					
		if(canStopByTractorBeam(helicopter)){
			startTractor(helicopter);}
				
		//Chaos-SpeedUp
		if(	this.canChaosSpeedup
			&& this.speedLevel.getX() == this.targetSpeedLevel.getX()
			&& helicopter.getX() - this.getX() > -350	)
		{				
			this.speedLevel.setLocation(6 + this.targetSpeedLevel.getX(),
										 this.speedLevel.getY());
		}
		if(this.canChaosSpeedup
		   && (helicopter.getX() - this.getX()) > -160)
		{			
			this.canMoveChaotic = true;
			this.speedLevel.setLocation(this.speedLevel.getX(),
										 9 + 4.5*Math.random());
		}
				
		// Ausweichen
		if(this.dodgeTimer > 0){
			evaluateDodge();}
		
		// Teleportieren
		if(this.teleportTimer > 0)
		{	
			this.teleportTimer--;
			if(	this.teleportTimer == READY)
			{
				this.speedLevel.setLocation(this.targetSpeedLevel);
			}				
		}		
	}
	
	private void endSnooze()
	{
		if(this.burrowTimer == DISABLED)
		{
			this.speedLevel.setLocation(this.targetSpeedLevel);
		}
		else
		{
			this.endInterruptedBorrowProcedure();
		}
		
		if(this.barrierTeleportTimer != DISABLED)
		{
			this.cloakingTimer = 1;
			this.barrierTeleportTimer = CLOAKING_TIME;
		}		
	}

	private void endInterruptedBorrowProcedure()
	{
		if(this.burrowTimer > BORROW_TIME + this.shootingRate * this.shotsPerCycle)
		{
			this.burrowTimer = 2 * BORROW_TIME + this.shootingRate * this.shotsPerCycle - this.burrowTimer;
		}
		else if(this.burrowTimer > BORROW_TIME)
		{
			this.burrowTimer = BORROW_TIME;
		}
		this.direction.y = 1;
		this.speedLevel.setLocation(0, 1);
	}

	private void validateTurns()
	{
		if(	this.stoppingBarrier != null && this.burrowTimer == DISABLED)
		{
			this.tryToTurnAtBarrier();
		}
		else if(this.hasToTurnAtXBorder()){this.changeXDirection();}
		else if(this.hasToTurnAtYBorder()){this.changeYDirection();}
	}
	
	private void checkForEmpStrike(Controller controller,
								   Pegasus pegasus)
	{
		if(pegasus.empWave != null)
		{
			if(this.isEmpShockable(pegasus))
			{
				this.empShock(controller, pegasus);
			}
		}
		else{this.isEmpShocked = false;}
	}

	private void empShock(Controller controller, Pegasus pegasus)
    {
    	takeDamage((int)getEmpVulnerabilityFactor() * pegasus.getEmpDamage());
		isEmpShocked = true;
		if(type == EnemyType.BOSS_4){spawningHornetTimer = READY;}
		disableSiteEffects(pegasus);
				
		if(hasHPsLeft())
		{			
			Audio.play(Audio.stun);
			if(getModel() == BARRIER){snooze(true);}
			else if(teleportTimer == READY ){teleport();}
			else if(isStunable() && !isShielding)
			{
				empSlowedTimer = type.isMainBoss()
											? EMP_SLOW_TIME_BOSS 
											: EMP_SLOW_TIME;
			}
			reactToHit(pegasus, null);
			
			Explosion.start(controller.explosions, pegasus,
							getCenterX(),
							getCenterY(), STUNNING, false);
		}
		else
		{
			Audio.play(Audio.explosion2);
			pegasus.empWave.kills++;
			pegasus.empWave.earnedMoney += calculateReward(pegasus);
			die(controller, pegasus, null);
		}
    }
    
    private float getEmpVulnerabilityFactor()
    {
        return this.type.isMajorBoss()
                ? EMP_DAMAGE_FACTOR_BOSS
                : EMP_DAMAGE_FACTOR_ORDINARY;
    }
		
	private boolean hasToTurnAtXBorder()
	{
		return this.barrierTeleportTimer == DISABLED
			   //&& this.stoppingBarrier == null
			   && this.turnTimer == READY
			   &&(	(this.direction.x == -1 
			   			&&(((this.callBack > 0 || this.type.isMajorBoss()) && this.getMinX() < TURN_FRAME.getMinX())
			   					|| (this.type == EnemyType.HEALER && this.getX() < 563)))
			   		||
			   		(this.direction.x == 1 
			   			&&(((this.callBack > 0 || this.type.isMajorBoss()) && this.getMaxX() > TURN_FRAME.getMaxX() && !this.canLearnKamikaze)
			   					|| (this.type == EnemyType.BODYGUARD && (this.getX() + this.getWidth() > 660)))));
	}

	private void changeXDirection()
	{
		this.direction.x = -this.direction.x;
		//this.turn_timer = MIN_TURN_TIME;
		if(this.callBack > 0){this.callBack--;}
	}
	
	private boolean hasToTurnAtYBorder()
	{		
		return 	this.burrowTimer == DISABLED
				&&( (this.getMinY() <= (getModel() == BARRIER ? 0 : TURN_FRAME.getMinY())
			   	  	 && this.direction.y < 0) 
			        ||
			        (this.getMaxY() >= (getModel() == BARRIER ? GROUND_Y : TURN_FRAME.getMaxY())
			   	     &&  this.direction.y > 0 
			   	     && !this.isDestroyed) );
	}

	private void changeYDirection()
	{
		this.direction.y = -this.direction.y;
		if(this.canSinusMove){this.speedLevel.setLocation(this.speedLevel.getX(), 1);}
		if(getModel() == BARRIER)
		{
			if(this.direction.y == -1){Audio.play(Audio.landing);}
			this.snooze(false);
		}		
	}

	private boolean isToBeRemoved()
	{		
		return this.type != EnemyType.BOSS_2_SERVANT
			   && this.barrierTeleportTimer == DISABLED
			   && !this.isDodging()
			   && (this.callBack == 0 || !this.speed.equals(ZERO_SPEED))
			   && (    (this.getMinX() > Main.VIRTUAL_DIMENSION.width + DISAPPEARANCE_DISTANCE
					   	 && this.direction.x ==  1)
				    || (this.getMaxX() < -DISAPPEARANCE_DISTANCE));
	}
	
	private boolean isDodging()
	{		
		return this.dodgeTimer > 0;
	}

	public boolean isOnScreen()
	{		
		return this.getMaxX() > 0
			   && this.getMinX() < Main.VIRTUAL_DIMENSION.width;
	}
	
	protected void prepareRemoval()
	{
		this.isMarkedForRemoval = true;
	}	
	
	private boolean isEmpShockable(Pegasus pegasus)
	{
		return     !this.isEmpShocked
				&& !this.isDestroyed
				&& !this.isInvincible()
				&& !(this.barrierTeleportTimer != DISABLED && this.barrierShootTimer == DISABLED)
				&& pegasus.empWave.ellipse.intersects(this.getBounds());
	}	

	private void evaluateSpeedup(Helicopter helicopter)
	{
		if(  this.speedLevel.getX() < (this.speedup == DISABLED ? 12 : 19) 
			 && (this.speedup  > 0 || this.canFrontalSpeedup) )
		{
			this.speedLevel.setLocation(this.speedLevel.getX()+0.5,
					 					 this.speedLevel.getY());
		}			
		if(	this.speedup == 0 && this.atEyeLevel(helicopter))
		{
			this.speedup = 1;
			this.canSinusMove = true;
		}
		else if(this.speedup == 1 && !this.atEyeLevel(helicopter))
		{
			this.speedup = 2;
		}
		else if(this.speedup == 2 && this.atEyeLevel(helicopter))
		{
			this.speedup = 3;
			this.canSinusMove = false;
			this.speedLevel.setLocation(this.speedLevel.getX(), 1.5);
			if(this.getY() < helicopter.getY()){this.direction.y = 1;}
			else{this.direction.y = -1;}	
		}		
	}
	
	private boolean atEyeLevel(Helicopter helicopter)
	{
		return this.intersects(Integer.MIN_VALUE/2f,
								  helicopter.getY(),
								  Integer.MAX_VALUE,
								  helicopter.getHeight());
	}
	
	private void evaluateBatchWiseMove()
	{
		if(this.batchWiseMove == 1)
		{
			this.speedLevel.setLocation(this.speedLevel.getX()+0.5,
					 					 this.speedLevel.getY());
		}
		else if(this.batchWiseMove == -1)
		{
			this.speedLevel.setLocation(this.speedLevel.getX()-0.5,
					 					 this.speedLevel.getY());
		}
		if(this.speedLevel.getX() <= 0){this.batchWiseMove = 1;}
		if(this.speedLevel.getX() >= this.targetSpeedLevel.getX()){this.batchWiseMove = -1;}
	}
	
	private void startKamikazeMode()
	{
		this.canKamikaze = true;
		this.canFrontalSpeedup = true;
	}
	
	private void kamikaze(Helicopter helicopter)
    {
    	// Boss-Gegner mit der Fähigkeit "Kamikaze" drehen mit einer bestimmten
    	// Wahrscheinlichkeit um, wenn sie dem Helikopter das Heck zugekehrt haben.
    	if(	this.isBoss()
    		&& Calculations.tossUp(0.008f)
    		&& ( (this.getMinX() > helicopter.getMaxX()
    			   && this.direction.x == 1) 
    			 ||
    			 (helicopter.getMinX() > this.getMaxX()
    			   && this.direction.x == -1)))		
    	{
			this.direction.x = -this.direction.x;	
			this.speedLevel.setLocation(0, this.speedLevel.getY());
		}		
		
    	if(((this.getMaxX() > helicopter.getMinX() && this.direction.x == -1)&&
			(this.getMaxX() - helicopter.getMinX() ) < 620) ||
		   ((helicopter.getMaxX() > this.getMinX() && this.direction.x == 1)&&
			(helicopter.getMaxX() - this.getMinX() < 620)))
		{			
			if(!this.canLearnKamikaze)
			{
				this.speedLevel.setLocation((this.type == EnemyType.BOSS_4 || this.type == EnemyType.BOSS_3) ? 12 : 8,
											 this.speedLevel.getY());
			}						
			if(this.direction.y == 1 
				&& helicopter.getY()  < this.getY())
			{							
				this.direction.y = -1;				
				this.speedLevel.setLocation(this.speedLevel.getX(), 0);
			}
			else if(this.direction.y == -1 
					&& helicopter.getMaxY() > this.getMaxY())
			{
				this.direction.y = 1;
				this.speedLevel.setLocation(this.speedLevel.getX(), 0);
			}
			
			if(this.speedLevel.getY() < 8)
			{
				this.speedLevel.setLocation(this.speedLevel.getX(),
											 this.speedLevel.getY()+0.5);
			}
		}
		else if(!this.canFrontalSpeedup && this.dodgeTimer == READY)
		{			
			if(this.type == EnemyType.BODYGUARD && Events.boss.shield < 1)
			{
				this.speedLevel.setLocation(7.5, this.targetSpeedLevel.getY());
			}
			else
			{
				this.speedLevel.setLocation(this.targetSpeedLevel);
			}
		}
    }
	
	private void evaluateBorrowProcedure(Helicopter helicopter)
	{		
		if(this.burrowTimer > 0){this.burrowTimer--;}
		if(this.burrowTimer == BORROW_TIME + this.shootingRate * this.shotsPerCycle)
		{					
			this.barrierShootTimer = this.shootingRate * this.shotsPerCycle;
			this.speedLevel.setLocation(ZERO_SPEED);
		}
		else if(this.burrowTimer == BORROW_TIME)
		{
			this.barrierShootTimer = DISABLED;
			this.speedLevel.setLocation(0, 1);
			this.direction.y = 1;
		}
		else if(this.burrowTimer == 1)
		{
			this.speedLevel.setLocation(ZERO_SPEED);
		}
		else if(this.burrowTimer == READY
				&&( (this.type != EnemyType.PROTECTOR
				     && Calculations.tossUp(0.004f))
				    || 
				    (this.type == EnemyType.PROTECTOR
				     && (helicopter.getX() > boss.getX() - 225) )))
		{			
			this.burrowTimer = 2 * BORROW_TIME
								+ this.shootingRate * this.shotsPerCycle
								+ (this.getY() == GROUND_Y
									? EnemyType.PROTECTOR.getWidth()/8
									: 0)
								- 1;
			this.speedLevel.setLocation(0, 1); 
			this.direction.y = -1;
		}	
	}
	
	private void evaluateShooting(Controller controller, Helicopter helicopter)
	{
		if(	this.shootTimer == 0
			&& !this.isEmpSlowed()
			&& Calculations.tossUp(0.1f)
			&& this.getX() + this.getWidth() > 0
			&& !(this.cloakingTimer > CLOAKING_TIME && this.cloakingTimer <= CLOAKING_TIME + CLOAKED_TIME)
			&& ((this.direction.x == -1 
				 && helicopter.getBounds().intersects(	
						 				this.getX() + Integer.MIN_VALUE/2f,
						 				this.getY() + (getModel() == TIT ? 0 : this.getWidth()/2) - 15,
						 				Integer.MAX_VALUE/2f,
						 				EnemyMissile.DIAMETER+30))
				||
				((this.direction.x == 1 
				  && helicopter.getBounds().intersects(
						  				this.getX(),
						  				this.getY() + (getModel() == TIT ? 0 : this.getWidth()/2) - 15,
								 		Integer.MAX_VALUE/2f,
								 		EnemyMissile.DIAMETER+30))))) 
		{
			this.shoot(	controller.enemyMissiles,
						this.hasDeadlyShots() ? BUSTER : DISCHARGER,
						this.shotSpeed + 3*Math.random()+5);
			
			this.shootTimer = this.shootingRate;
		}
		if(this.shootTimer > 0){this.shootTimer--;}
	}
	
	private boolean hasDeadlyShots()
	{		
		return this.type == EnemyType.BOSS_3
				|| this.isMiniBoss
				|| (this.type == EnemyType.BIG_SHIELD_MAKER && Calculations.tossUp());
	}

	private void evaluateBarrierShooting(Controller controller,
										 Helicopter helicopter)
	{
		if(this.barrierShootTimer == 0)
		{
			this.barrierShootTimer = this.shootingCycleLength;
			if(	this.shotRotationSpeed == 0
				&&	  (helicopter.getX()    < this.getX()         && this.shootingDirection.getX() > 0)
					||(helicopter.getMaxX() > this.getMaxX() && this.shootingDirection.getX() < 0) )
			{
				this.shootingDirection.setLocation(-this.shootingDirection.getX(), this.shootingDirection.getY());
			}
		}		
		if( this.barrierShootTimer <= this.shotsPerCycle * this.shootingRate
			&& this.getX() + this.getWidth() > 0
			&& this.barrierShootTimer %this.shootingRate == 0)
		{					
			if(this.shotRotationSpeed != 0)
			{
				float tempValue = 0.0005f * this.shotRotationSpeed * this.lifetime;
				this.shootingDirection.setLocation(
						Math.sin(tempValue),
						Math.cos(tempValue) );
			}
			if(this.burrowTimer != DISABLED || this.barrierTeleportTimer != DISABLED)
			{
				// Schussrichtung wird auf Helicopter ausgerichtet
				this.shootingDirection.setLocation(
						( (helicopter.getX() + (helicopter.isMovingLeft ? Helicopter.FOCAL_PNT_X_LEFT : Helicopter.FOCAL_PNT_X_RIGHT))
							  - (this.getX() +       this.getWidth()/2)),
						  (helicopter.getY() + Helicopter.FOCAL_PNT_Y_EXP)
						  	  - (this.getY() +       this.getHeight()/2)) ;
				float distance = (float) Calculations.ZERO_POINT.distance(this.shootingDirection);
				this.shootingDirection.setLocation(this.shootingDirection.getX()/distance,
													this.shootingDirection.getY()/distance);
			}
			this.shoot(controller.enemyMissiles, this.shotType, this.shotSpeed);
		}				
		this.barrierShootTimer--;
	}
	
	public void shoot(EnumMap<CollectionSubgroupType, LinkedList<EnemyMissile>> enemyMissiles, EnemyMissileType missileType, double missileSpeed)
    {
    	Iterator<EnemyMissile> i = enemyMissiles.get(INACTIVE).iterator();
		EnemyMissile em;
		if(i.hasNext()){em = i.next(); i.remove();}
		else{em = new EnemyMissile();}
		enemyMissiles.get(ACTIVE).add(em);
		em.launch(this, missileType, missileSpeed, this.shootingDirection);
		Audio.play(Audio.launch3);
    }
	
	private void evaluateBarrierTeleport(Helicopter helicopter)
	{
		if(this.barrierTeleportTimer == CLOAKING_TIME + this.shootingRate * this.shotsPerCycle)
		{					
			this.barrierShootTimer = this.shootingRate * this.shotsPerCycle;
			this.uncloak(DISABLED);
		}
		else if(this.barrierTeleportTimer == CLOAKING_TIME)
		{
			this.barrierShootTimer = DISABLED;
			this.cloakingTimer = ACTIVE_TIMER;
			if(this.getMaxX() > 0){Audio.play(Audio.cloak);}
		}	
		else if(this.barrierTeleportTimer == READY && Calculations.tossUp(0.004f))
		{
			this.startBarrierUncloaking(helicopter);
		}
		
		if(this.barrierTeleportTimer != READY)
		{
			this.barrierTeleportTimer--;
			if(this.barrierTeleportTimer == READY)
			{				
				if(this.callBack > 0)
				{					
					this.placeCloakingBarrierAtPausePosition();
				}
				else{this.isMarkedForRemoval = true;}
			}
		}		
	}
	
	protected void startBarrierUncloaking(Helicopter helicopter)
	{
		this.barrierTeleportTimer = 2 * CLOAKING_TIME + this.shootingRate * this.shotsPerCycle;
		this.cloakingTimer = CLOAKED_TIME + CLOAKING_TIME;
		this.placeNearHelicopter(helicopter);
	}

	
	
	private void sinusLoop()
    {
		this.speedLevel.setLocation(
				this.speedLevel.getX(),
				Math.max(4.0, 0.15f*(145-Math.abs(this.getY()-155))));
		
    	if(this.canLoop)
    	{
    		if(this.direction.x == -1 && this.getY()-155>0)
    		{
    			this.direction.x = 1;
    			this.speedLevel.setLocation(11, this.speedLevel.getY());
    		}
    		else if(this.direction.x == 1 && this.getY()-155<0)
    		{
    			this.direction.x = -1;
    			this.speedLevel.setLocation(7.5, this.speedLevel.getY());
    		}
    	}
    }	
	
	private void cloaking()
    {
    	if(!this.canLearnKamikaze || this.canFrontalSpeedup){this.cloakingTimer += this.uncloakingSpeed;}
    	if(this.cloakingTimer <= CLOAKING_TIME)
		{						 
			this.alpha = 255 - 255*this.cloakingTimer /CLOAKING_TIME;
    	}
		else if(this.cloakingTimer > CLOAKING_TIME + CLOAKED_TIME && this.cloakingTimer <= CLOAKED_TIME + 2 * CLOAKING_TIME)
		{
			if(this.cloakingTimer == CLOAKING_TIME + CLOAKED_TIME + this.uncloakingSpeed){Audio.play(Audio.cloak);}
			this.alpha = 255*(this.cloakingTimer - CLOAKED_TIME - CLOAKING_TIME)/CLOAKING_TIME;
			if(this.cloakingTimer >= CLOAKED_TIME + 2 * CLOAKING_TIME)
			{
				if(this.canLearnKamikaze){this.uncloak(DISABLED);}
				else{this.uncloak(READY);}
			}
		}
		else {this.alpha = 255;}
    }
	
	private boolean canStopByTractorBeam(Helicopter helicopter)
	{		
		return isTractorReady()
				&& isInRangeOf(helicopter)
				&& helicopter.canBeStoppedByTractorBeam();
	}
	
	private boolean isInRangeOf(Helicopter helicopter)
	{
		return    helicopter.getX() - this.getX() > -750
			   && helicopter.getX() - this.getX() < -50
			   && helicopter.getY() + 56 > this.getY() + 0.2 * this.getHeight()
			   && helicopter.getY() + 60 < this.getY() + 0.8 * this.getHeight();
	}
	
	private boolean isTractorReady() {
		return this.tractor == AbilityStatusType.READY
				&& !this.isEmpSlowed()
				&& this.cloakingTimer < 1
				&& this.getMaxX() < Main.VIRTUAL_DIMENSION.width;
	}

	private void startTractor(Helicopter helicopter)
	{
		Audio.loop(Audio.tractorBeam);
		this.tractor = AbilityStatusType.ACTIVE;
		this.speedLevel.setLocation(ZERO_SPEED);
		helicopter.tractor = this;
		this.direction.x = -1;		
	}
	
	public void stopTractor()
	{
		this.tractor = AbilityStatusType.DISABLED;
		this.speedLevel.setLocation(this.targetSpeedLevel);
	}
	
	private void evaluateDodge()
	{
		this.dodgeTimer--;
		if(this.dodgeTimer == READY)
		{				
			this.speedLevel.setLocation(this.targetSpeedLevel);
			this.direction.x = -1;												   
		}		
	}

	private void snooze(boolean inactivation)
	{
		this.snoozeTimer
			= Math.max(	this.snoozeTimer,
						SNOOZE_TIME 
						+ (inactivation
							? INACTIVATION_TIME
							  + Calculations.random((int)(EXTRA_INACTIVE_TIME_FACTOR * INACTIVATION_TIME))
							:0));
		this.speedLevel.setLocation(ZERO_SPEED);
		if(this.targetSpeedLevel.getY() != 0
		   && this.getMaxY() + 1.5 * this.speed.getY() > GROUND_Y)
		{
			this.setY(GROUND_Y - this.getHeight());
		}		
		if(this.burrowTimer != DISABLED)
		{						
			this.barrierShootTimer = DISABLED;
		}		
		else if(this.cloakingTimer != DISABLED)
		{
			this.barrierTeleportTimer = DISABLED;
			this.barrierShootTimer = DISABLED;
		}
		else if(this.barrierShootTimer != DISABLED)
		{
			this.barrierShootTimer = this.snoozeTimer - SNOOZE_TIME + this.shootingCycleLength;
		}
	}
	
	private void move()
	{			
		if(!this.speed.equals(ZERO_SPEED)|| Scenery.backgroundMoves)
		{
			this.setLocation(
					this.getX()
						+ this.direction.x * this.speed.getX() 
						- (Scenery.backgroundMoves ? BG_SPEED : 0),
					Math.max( getModel() == BARRIER ? 0 : Integer.MIN_VALUE,
							this.type == EnemyType.ROCK ? this.getY() :
								Math.min( this.canBePositionedBelowGround()
											? Integer.MAX_VALUE
											: GROUND_Y - this.getHeight(),
										this.getY()
											+ this.direction.y 
											* this.speed.getY())));
		}
	}

	private boolean canBePositionedBelowGround()
	{		
		return !(getModel() == BARRIER
			     && this.burrowTimer == DISABLED)
			   || this.isDestroyed
			   || this.type == EnemyType.ROCK;
	}

	private void calculateSpeedDead()
	{		
		if(this.explodingTimer <= 0)
		{
			this.speed.setLocation(this.speedLevel);
		}
		else
		{
			this.explodingTimer--;
			this.speed.setLocation(this.speedLevel.getX(), 0);
			if(this.explodingTimer == 0){this.explodingTimer = DISABLED;}
		}
	}
	
	private void calculateSpeed(Helicopter helicopter)
	{		
		if(this.isStunned())
		{
			this.adjustSpeedTo(helicopter.missileDrive);
			if(this.stunningTimer == 1)
			{
				if(getModel() == BARRIER){this.snooze(true);}
				else{this.isRecoveringSpeed = true;}
			}
		}
		if(getModel() != BARRIER){this.evaluateSpeedBoost();}
		if(this.isRecoveringSpeed){this.recoverSpeed();}
		
		this.speed.setLocation(this.speedLevel);			//d
		
		if(this.isEmpSlowed())
		{
			// relevant, wenn mit der PEGASUS-Klasse gespielt wird
			this.speed.setLocation(	
				this.speed.getX()
					*((double)(EMP_SLOW_TIME-this.empSlowedTimer)/EMP_SLOW_TIME),
				this.speed.getY()
					*((double)(EMP_SLOW_TIME-this.empSlowedTimer)/EMP_SLOW_TIME));
		}
				
		if(	this.stoppingBarrier != null
			&& this.burrowTimer == DISABLED
			&& !(getModel() == BARRIER && this.type == EnemyType.BIG_BARRIER))
		{
			this.adjustSpeedToBarrier(helicopter);
		}
	}
	
	private boolean isEmpSlowed()
	{		
		return this.empSlowedTimer > 0;
	}

	private void adjustSpeedTo(int missileDrive)
	{
		if( !this.speedLevel.equals(ZERO_SPEED)
			&& 
			( this.totalStunningTime - 13 == this.stunningTimer
			  || this.getMaxX()
			  	 + 18 
			  	 + missileDrive/2f > Main.VIRTUAL_DIMENSION.width
			  	 								+ 2 * this.getWidth()/3
			  || this.getMinX()
			  	 - 18 
			  	 - missileDrive/2f < - 2 * this.getWidth()/3))
		{
			this.speedLevel.setLocation(ZERO_SPEED);
		}
	}	
	
	private void evaluateSpeedBoost()
	{		
		int bottomTurnLine = this.type == EnemyType.KABOOM
								  ? KABOOM_Y_TURN_LINE 
								  : (int)TURN_FRAME.getMaxY();
									
		if(this.isSpeedBoosted)
		{
			if(    this.getMinY() > TURN_FRAME.getMinY()
			    && this.getMaxY() < bottomTurnLine)
			{
				this.speedLevel.setLocation(this.targetSpeedLevel);
				this.isSpeedBoosted = false;
			}						
		}
		else if(this.stoppingBarrier != null
				&&(     this.getMinY() < TURN_FRAME.getMinY()
				    || (this.getMaxY() > bottomTurnLine)))
		{
			this.isSpeedBoosted = true;
			this.speedLevel.setLocation(Math.max(
											this.speedLevel.getX(),
											this.targetSpeedLevel.getX()
												+ 7.5), 
										 Math.max(this.speedLevel.getY(), 5.5));
			
			// Wenn Gegner droht am Boden durch Barrier zerdrückt zu werden, dann nimmt Gegner den kürzesten Weg.
			if(this.mustAvoidGroundCollision(bottomTurnLine))
			{
				this.performXTurnAtBarrier();
			}
		}
	}	

	private boolean mustAvoidGroundCollision(int yTurnLine)
	{		
		return this.getMaxY() > yTurnLine
				   &&(   (this.direction.getX() ==  1 
				   			&& this.getCenterX() < this.stoppingBarrier.getCenterX())
					   ||(this.direction.getX() == -1 
					   		&& this.getCenterX() > this.stoppingBarrier.getCenterX()));
	}
	
	private void recoverSpeed()
	{
		if(	this.burrowTimer != DISABLED || this.hasReachedTargetSpeed())
		{
			this.isRecoveringSpeed = false;
			if(this.burrowTimer != DISABLED)
			{
				this.speedLevel.setLocation(0, 1);
			}
			else{this.speedLevel.setLocation(this.targetSpeedLevel);}
		}		
		else if(this.speedLevel.getX() < this.targetSpeedLevel.getX())
		{					
			this.speedLevel.setLocation(this.speedLevel.getX()+0.025,
					 					 this.speedLevel.getY());
		}
		if(this.speedLevel.getY() < this.targetSpeedLevel.getY())
		{
			this.speedLevel.setLocation(this.speedLevel.getX(),
					 					 this.speedLevel.getY()+0.025);
		}		
	}

	private boolean hasReachedTargetSpeed()
	{		
		return    this.speedLevel.getX() >= this.targetSpeedLevel.getX()
			   && this.speedLevel.getY() >= this.targetSpeedLevel.getY();
	}

	private void adjustSpeedToBarrier(Helicopter helicopter)
	{
		if(   this.stoppingBarrier.direction.x == this.direction.x
		   && this.stoppingBarrier.getCenterX()*this.direction.x
		   		             < this.getCenterX()*this.direction.x
		   && this.stoppingBarrier.speed.getX() > this.speed.getX())
		{
			this.speed.setLocation(	this.stoppingBarrier.isOnScreen()
									&& !this.isOnScreen()
			   							? 0 
			   							: this.stoppingBarrier.speed.getX(),
			   						this.speed.getY());
		}
		else if( this.stoppingBarrier.direction.y == this.direction.y
				 && this.stoppingBarrier.getCenterY()*this.direction.y
				 		           < this.getCenterY()*this.direction.y
				 && this.stoppingBarrier.speed.getY() > this.speed.getY()
				 && this.burrowTimer == DISABLED)
		{
			this.speed.setLocation(this.speed.getX(), this.stoppingBarrier.speed.getY());
			if(helicopter.tractor == this){helicopter.stopTractor();}
		}		
	}
	
	public static void updateAllDestroyed(Controller controller,
										  Helicopter helicopter)
	{
		for(Iterator<Enemy> i = controller.enemies.get(DESTROYED).iterator(); i.hasNext();)
		{
			Enemy e = i.next();
			e.updateDead(controller.explosions, helicopter);
			
			if(	helicopter.basicCollisionRequirementsSatisfied(e)
				&& !e.hasCrashed)
			{
				e.collision(controller, helicopter);
			}				
			if(e.isMarkedForRemoval)
			{
				e.clearImage();
				i.remove(); 
				// controller.enemies.get(INACTIVE).add(e);
			}				
		}		// this.slowed_timer
	}

	private void updateDead(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion, Helicopter helicopter)
	{				
		if(this.collisionDamageTimer > 0){this.collisionDamageTimer--;}
		if(this.collisionAudioTimer > 0){this.collisionAudioTimer--;}
		if( !this.hasCrashed
		    && this.getMaxY() + this.speed.getY() >= this.yCrashPos)
		{
			this.handleCrashToTheGround(explosion, helicopter);
		}		
		this.calculateSpeedDead();
		this.move();
		if(this.getMaxX() < 0){this.isMarkedForRemoval = true;}
		this.setPaintBounds();
	}
	
	private void handleCrashToTheGround(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion,
										Helicopter helicopter)
	{
		this.hasCrashed = true;
		this.speedLevel.setLocation(ZERO_SPEED);
		this.setY(this.yCrashPos - this.getHeight());
		if(this.type.isServant()){this.isMarkedForRemoval = true;}
		Audio.play(this.type == EnemyType.KABOOM ? Audio.explosion4 : Audio.explosion3);
		Explosion.start(explosion, 
						helicopter, 
						this.getCenterX(),
						this.getCenterY(),
						this.type == EnemyType.KABOOM ? JUMBO : ORDINARY,
						this.type == EnemyType.KABOOM);
	}
	
	private boolean isBoss()
	{
		return this.isMiniBoss || this.type.isMajorBoss();
	}
	
	public boolean isLivingBoss()
	{		
		return this.isBoss() && !this.isDestroyed;
	}

	private void collision(Controller controller, Helicopter helicopter)
	{
		boolean playCollisionSound = this.collisionAudioTimer == READY;
		helicopter.beAffectedByCollisionWith(this, controller, playCollisionSound);
				
		if(playCollisionSound)
		{
			this.collisionAudioTimer = Helicopter.NO_COLLISION_DAMAGE_TIME;
		}		
		this.collisionDamageTimer = Helicopter.NO_COLLISION_DAMAGE_TIME;
			
		if(	this.isExplodingOnCollisions()
			&& !this.isInvincible()
			&& !this.isDestroyed)
		{
			this.explode( controller.explosions,
						  helicopter, 
						  0, 
						  this.type == EnemyType.KABOOM
						  	? JUMBO 
						  	: ORDINARY,
						  this.type == EnemyType.KABOOM);
			
			if(	helicopter.canObtainCollisionReward()
				&& !(this.type == EnemyType.KABOOM))
			{
				this.grantRewards(helicopter, null, helicopter.hasPerformedTeleportKill(), controller.powerUps);
			}
			this.destroy(helicopter);
		}				
		if(	helicopter.isDestinedToCrash())
		{
			helicopter.crash();
		}		
	}

	private void grantRewards(Helicopter helicopter, Missile missile, boolean beamKill, EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUps)
	{
		helicopter.receiveRewardFor(this, missile, beamKill);
		this.grantGeneralRewards(helicopter, powerUps);
	}

	public void reactToRadiation(Controller controller, Helicopter helicopter)
	{
		if(	this.teleportTimer == READY){this.teleport();}
		else if(this.canTakeCollisionDamage())
		{
			this.takeDamage((int)(
				helicopter.currentBaseFirepower
				* (helicopter.bonusKillsTimer > NICE_CATCH_TIME - TELEPORT_KILL_TIME
					? TELEPORT_DAMAGE_FACTOR 
					: RADIATION_DAMAGE_FACTOR)));				
							
			if(getModel() == BARRIER)
			{
				if(	helicopter.hasTripleDamage()
					&&  Calculations.tossUp(
							this.deactivationProb
							*(helicopter.bonusKillsTimer
								> NICE_CATCH_TIME
								  - TELEPORT_KILL_TIME ? 2 : 1)))
				{
					this.hitPoints = 0;
				}
				else if(Calculations.tossUp(this.deactivationProb *(helicopter.bonusKillsTimer > NICE_CATCH_TIME - TELEPORT_KILL_TIME ? 4 : 2)))
				{
					this.snooze(true);
				}
			}
			if(this.hasHPsLeft())
			{
				this.reactToHit(helicopter, null);
			}
			else
			{
				boolean beamKill = helicopter.bonusKillsTimer > 0;
				this.die(controller, helicopter, null, beamKill);
			}
		}		
	}

	private boolean canTakeCollisionDamage()
	{		
		return 	   !this.isDestroyed
				&& !this.isExplodingOnCollisions()
				&& !this.isInvincible()
				&& !(this.barrierTeleportTimer != DISABLED && this.barrierShootTimer == DISABLED)
				&& this.collisionAudioTimer == READY;
	}
	
	public float collisionDamage(Helicopter helicopter)
	{		
		return helicopter.getProtectionFactor()
				// TODO 0.65 und 1.0 in Konstanten auslagern
			   *helicopter.getBaseProtectionFactor(this.isExplodingOnCollisions())
			   *(helicopter.isTakingKaboomDamageFrom(this)
			     ? helicopter.kaboomDamage()
			     : (this.isExplodingOnCollisions() && !this.isInvincible() && !this.isDestroyed)
					? 1.0f 
					: this.collisionDamageTimer > 0
						? 0.0325f 
						: 0.65f);
	}
	
	void takeDamage(int dmg)
	{
		hitPoints -= dmg;
		if(!hasHPsLeft()){hitPoints = 0;}
	}
	
	public void hitByMissile(Helicopter helicopter, Missile missile, EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{		
		helicopter.hitCounter++;
		if( missile.typeOfExplosion == JUMBO
			|| missile.typeOfExplosion == PHASE_SHIFT
			|| missile.extraDamage)
		{
			Audio.play(Audio.explosion4);
		}
		else{Audio.play(Audio.explosion2);}
		missile.hits.put(this.hashCode(), this);
		this.takeDamage(missile.dmg);
		if(getModel() == BARRIER)
		{
			if((missile.typeOfExplosion == JUMBO || missile.typeOfExplosion == PHASE_SHIFT || missile.extraDamage)
				&& Calculations.tossUp(	0.5f
									* this.deactivationProb
									* (( (missile.typeOfExplosion == JUMBO
											|| missile.typeOfExplosion == PHASE_SHIFT)
										  && missile.extraDamage) ? 2 : 1)))
			{
				this.hitPoints = 0;
			}
			else if(Calculations.tossUp(this.deactivationProb *(missile.typeOfExplosion == PLASMA ? 2 : 1)))
			{
				this.snooze(true);
			}
		}		
		if(areStunningRequirementsMet(missile))
		{			
			this.stun(helicopter, missile, explosion);
		}
	}
	
	private boolean areStunningRequirementsMet(Missile missile)
	{
		return missile.typeOfExplosion == STUNNING
			&& this.isStunable()
			&& this.nonStunableTimer == READY;
	}
	
	private void stun(Helicopter helicopter, Missile missile, EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{
		if(this.hasHPsLeft()){Audio.play(Audio.stun);}
		this.explode(explosion, helicopter, missile);	
		this.nonStunableTimer = (int)(this.type.isMainBoss() || this.type.isFinalBossServant()
										  ? 2.25f*Events.level 
										  : 0);
		this.knockBackDirection = missile.speed > 0 ? 1 : -1;
				
		this.speedLevel.setLocation(
				(this.knockBackDirection == this.direction.x ? 1 : -1)
				  *(this.type.isMainBoss() || this.type.isFinalBossServant()
				    ? (10f + helicopter.missileDrive)/(Events.level/10f)
					:  10f + helicopter.missileDrive),
				0);
						
		this.stunningTimer = this.totalStunningTime
			= (int)(17 + STUNNING_TIME_BASIS 
					     * (this.type.isMajorBoss() ? (10f/Events.level) : 2.5f));
				
		this.disableSiteEffects(helicopter);
	}

	private void disableSiteEffects(Helicopter helicopter)
	{
		if(helicopter.tractor == this){helicopter.stopTractor();}
		if(!this.canLearnKamikaze
		   && this.cloakingTimer > 0
		   && this.type != EnemyType.BOSS_3)
		{
			this.uncloak(READY);
		}		
	}

	public void reactToHit(Helicopter helicopter, Missile missile)
	{
		if(this.isReadyToDodge(helicopter)){this.dodge();}
		
		if(this.canDoHitTriggeredTurn())
		{
			if(this.canLearnKamikaze){this.startKamikazeMode();}
			     if(this.getMinX() > helicopter.getMinX()){this.direction.x = -1;}
			else if(this.getMaxX() < helicopter.getMaxX()){this.direction.x = 1;}
		}
		if(this.type == EnemyType.BOSS_4){this.spawningHornetTimer = READY;}
		
		if(this.cloakingTimer == READY && !(this.tractor == AbilityStatusType.ACTIVE))
		{
			Audio.play(Audio.cloak); 
			this.cloakingTimer = ACTIVE_TIMER;
		}
		if( missile != null 
		    && missile.typeOfExplosion == STUNNING
		    && this.cloakingTimer != DISABLED)
		{
			this.uncloak(getModel() == BARRIER ? DISABLED : READY);
		}
		else if( !this.canLearnKamikaze
				 && this.cloakingTimer > CLOAKING_TIME
				 && this.cloakingTimer <= CLOAKING_TIME+CLOAKED_TIME)
		{
			this.cloakingTimer = CLOAKED_TIME+1;
		}
	}	
		
	private boolean canDoHitTriggeredTurn()
	{		
		return this.canInstantTurn
				|| this.canTurn
					&& !this.canEarlyTurn
					&& Calculations.tossUp(this.isMiniBoss
										? this.isCarrier ? 0.2f : 0.5f
										: this.isCarrier ? 0.1f : 0.25f);
	}

	private void explode(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion, Helicopter helicopter, Missile missile)
	{
		explode(explosion, helicopter, missile.speed, missile.typeOfExplosion, missile.extraDamage);
	}	
	void explode(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion, Helicopter helicopter)
	{
		explode(explosion, helicopter, 0, ORDINARY, false);
	}	
	private void explode(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion, Helicopter helicopter, double missileSpeed, ExplosionTypes explosionType, boolean extraDamage)
	{		
		if(this.explodingTimer == 0){this.explodingTimer = 7;}
		Explosion.start(explosion, 
						helicopter, 
						this.getX() + ((explosionType != EMP && getModel() != BARRIER)
							? (missileSpeed < 0 ? 2 : 1) * this.getWidth()/3
							: this.getWidth()/2),
						this.getY() + this.getHeight()/2,
						explosionType, extraDamage);
	}
	
	void destroy(Helicopter helicopter){destroy(helicopter, null, true);}
	void destroy(Helicopter helicopter,
				 EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp,
				 boolean wasDestroyedByPlayer)
	{
		if(wasDestroyedByPlayer)
		{
			if(getModel() != BARRIER){helicopter.numberOfEnemiesKilled++;}
			if(this.isMiniBoss){helicopter.numberOfMiniBossKilled++;}
		}	
		else
		{
			if(this.canDropPowerUp()){this.dropRandomPowerUp(helicopter, powerUp);}
		}
		this.isDestroyed = true;
		if(this.cloakingTimer > 0){this.uncloak(DISABLED);}
		this.teleportTimer = DISABLED;
		this.primaryColor = Colorations.adjustBrightness(this.primaryColor, Colorations.DESTRUCTION_DIM_FACTOR);
		this.secondaryColor = Colorations.adjustBrightness(this.secondaryColor, Colorations.DESTRUCTION_DIM_FACTOR);
		
		this.repaint();
	
		if(helicopter.tractor == this)
		{
			helicopter.stopTractor();
		}		
		this.speedLevel.setLocation(0, 12);
		this.direction.y = 1;
		
		this.empSlowedTimer = READY;
		this.yCrashPos = (int)(this.getMaxY() >= GROUND_Y
									? this.getMaxY()
									: GROUND_Y 
									  + 1
									  + Math.random()
									    *(this.getHeight()/4));
	}
	
	private void uncloak(int nextCloakingState)
	{
		this.alpha = 255;
		this.primaryColor = Colorations.setAlpha(this.primaryColor, 255);
		this.secondaryColor = Colorations.setAlpha(this.secondaryColor, 255);
		this.cloakingTimer = nextCloakingState;
	}

	public void die(Controller controller, Helicopter helicopter,
					Missile missile)
	{
		this.die(controller, helicopter, missile, false);
	}

	public void die(Controller controller, Helicopter helicopter,
					Missile missile, boolean beamKill)
	{
		this.grantRewards(helicopter, missile, beamKill, controller.powerUps);
		this.destroy(helicopter);
		if(this.isShielding){this.stopShielding();}
		if(this.cloakingTimer != DISABLED){Audio.play(Audio.cloak);}
		
		if(missile == null)
		{
			this.explode(controller.explosions, helicopter);
		}		
		else if(missile.typeOfExplosion != STUNNING)
		{
			this.explode(controller.explosions, helicopter, missile);
		}		
		
		this.evaluateBossDestructionEffect(helicopter,
											  controller.enemies,
											  controller.explosions);
		if(this.isCarrier){
			EnemyController.carrierDestroyedJustNow = this;}
		if(missile != null){missile.hits.remove(this.hashCode());}
	}	

	private void stopShielding()
	{
		if(Events.boss.shield == 1){Audio.shieldUp.stop();}
		Events.boss.shield--;
		this.isShielding = false;
	}

    public boolean canCountForKillsAfterLevelUp()
    {
        return this.type != EnemyType.BOSS_4_SERVANT && !this.type.isFinalBossServant();
    }
    
    public boolean canDropPowerUp()
	{		
		return getModel() != BARRIER
			   &&( (!Events.isBossLevel()
				    &&( ( Calculations.tossUp(POWER_UP_PROB)
						  && Events.level >= MIN_POWER_UP_LEVEL)
						|| this.isMiniBoss))
				|| this.type == EnemyType.BOSS_1
				|| this.type == EnemyType.BOSS_3
				|| this.type == EnemyType.BOSS_4 );
	}
	
	public void dropRandomPowerUp(Helicopter helicopter,
								  EnumMap<CollectionSubgroupType,
								  LinkedList<PowerUp>> powerUps)
	{
		PowerUp.activateInstance(helicopter, powerUps, this);
	}
	
	public PowerUpType getTypeOfRandomlyDroppedPowerUp()
	{
		return Calculations.tossUp(REPARATION_POWER_UP_DROP_RATE)
				? REPARATION
				: PowerUpType.getValues().get(this.getRandomIndexOfDroppablePowerUp());
	}
	
	private int getRandomIndexOfDroppablePowerUp()
	{
		return Calculations.random(this.getMaximumNumberOfDifferentDroppablePowerUps());
	}
	
	private int getMaximumNumberOfDifferentDroppablePowerUps()
	{
		// major bosses are not supposed to drop bonus income powerUps
		int indexReductionValue = this.type.isMajorBoss() ? 1 : 0;
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
        return this.type.getStrength() * this.getRewardFactor();
    }
    
	private int getRewardFactor()
	{
		return this.isMiniBoss ? MINI_BOSS_REWARD_FACTOR : STANDARD_REWARD_FACTOR;
	}
 
	private void evaluateBossDestructionEffect(Helicopter helicopter,
											   EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy,
											   EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{
		if(this.isMiniBoss)
		{
			EnemyController.currentMiniBoss = null;
		}			
		else if(this.type == EnemyType.BOSS_2)
		{								
			boss.setLocation(this.getCenterX(),
							 this.getCenterY());
			EnemyController.makeBossTwoServants = true;
		}					
		else if(this.type == EnemyType.BOSS_4)
		{
			for(Enemy e : enemy.get(ACTIVE))
			{
				e.explode(explosion, helicopter);
				if (e.type != EnemyType.BOSS_4)
				{
					e.destroy(helicopter);
				}
			}
		}
		else if(this.type == EnemyType.FINAL_BOSS)
		{
			for(Enemy e : enemy.get(ACTIVE))
			{
				e.explode(explosion, helicopter);
				if (e.type != EnemyType.FINAL_BOSS)
				{
					e.destroy(helicopter);
				}
			}
			Events.isRestartWindowVisible = true;
			Events.level = Events.maxLevel = 51;
			helicopter.isDamaged = true;
		
			helicopter.destination.setLocation(helicopter.getX()+40,
											   520.0);	
			Events.determineHighscoreTimes(helicopter);
		}
		else if(this.type.isFinalBossServant())
		{
			FinalBossServantType.of(this.type).ifPresent(servantType -> {
				Events.boss.operator.remove(servantType);
				Events.boss.operator.resetTimeSinceDeath(servantType);
			});
		}
		if(this.type.isMainBoss() && this.type != EnemyType.FINAL_BOSS){Events.boss = null;}
	}

	public void dodge()
	{
		if(this.type == EnemyType.BOSS_3)
		{			
			this.speedLevel.setLocation(this.speedLevel.getX(), 9);
			this.dodgeTimer = 16;
		}
		else if(this.shootTimer != DISABLED || this.canChaosSpeedup)
		{			
			this.speedLevel.setLocation(this.speedLevel.getX(), 8.5);
			this.dodgeTimer = 13;
		}
		else
		{
			this.speedLevel.setLocation(6, 6);
			if(this.getMaxX() < 934){this.direction.x = 1;}
			this.dodgeTimer = 16;
		}															   
		
		if(this.getY() > 143){this.direction.y = -1;}
		else{this.direction.y = 1;}
		
		if(this.type.isShieldMaker()){this.stampedeShieldMaker();}
		else if(this.type == EnemyType.HEALER){this.canDodge = false;}
	}
	
	private void stampedeShieldMaker()
	{
		this.shieldMakerTimer = READY;
		this.speedLevel.setLocation(SHIELD_MAKER_STAMPEDE_SPEED);
		this.targetSpeedLevel.setLocation(SHIELD_MAKER_STAMPEDE_SPEED);
		this.canMoveChaotic = true;
		this.canDodge = false;
		this.setShieldingPosition();
		if(this.isShielding){this.stopShielding();}
	}

	protected void setShieldingPosition()
	{
		if(!Events.boss.operator.containsServant(this.shieldingBrother()))
		{
			this.isUpperShieldMaker = Calculations.tossUp();
		}
		else
		{
			this.isUpperShieldMaker
				= !Events.boss.getOperatorServant(this.shieldingBrother()).isUpperShieldMaker;
		}		
	}

	private FinalBossServantType shieldingBrother()
	{		
		return this.type == EnemyType.SMALL_SHIELD_MAKER
							 ? FinalBossServantType.BIG_SHIELD_MAKER
							 : FinalBossServantType.SMALL_SHIELD_MAKER;
	}

	public void teleport()
	{
		Audio.play(Audio.teleport2);		
		this.setLocation(260.0 + Math.random()*(660.0 - this.getWidth()),
						   20.0 + Math.random()*(270.0 - this.getHeight()));
		this.speedLevel.setLocation(ZERO_SPEED);
		this.teleportTimer = 60;
		this.invincibleTimer = 40;
	}
	
	public boolean isStaticallyCharged()
	{		
		return this.staticChargeTimer == READY
   	 		   && this.snoozeTimer <= SNOOZE_TIME;
	}
	
	public void startStaticDischarge(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion,
									 Helicopter helicopter)
	{
		this.staticChargeTimer = STATIC_CHARGE_TIME;
		helicopter.receiveStaticCharge(2.5f);
		Audio.play(Audio.emp);
		Explosion.start(explosion, helicopter, (int)this.getCenterX(), (int)this.getCenterY(), STUNNING, false, this);
	}

	public boolean isHittable(Missile missile)
	{		
		return !this.isDestroyed
			   && !(this.barrierTeleportTimer != DISABLED && this.alpha != 255)
			   && missile.intersects(this)
			   && !missile.hits.containsKey(this.hashCode());
	}

	public boolean isReadyToDodge(Helicopter helicopter)
	{		
		return 	    this.canDodge
				&&  this.dodgeTimer == READY
				&& !this.isEmpSlowed()
				&& !this.isDestroyed
				&& !(this.type == EnemyType.HEALER
					 && Events.boss.shield > 0 
					 && this.getMinX() > Events.boss.getMinX()
				     && this.getMaxX() < Events.boss.getMaxX())
				&& !( (     (helicopter.getX() - this.getMaxX() > -500)
						 && (helicopter.getX() - this.getX() 	  <  150))
					  && this.canKamikaze
					  && this.direction.x == -1);
	}

	public boolean isInvincible()
	{		
		return    this.invincibleTimer > 0
			   || this.shield > 0;
	}

	public void evaluatePosAdaption(Helicopter helicopter)
	{
		if(helicopter.isLocationAdaptionApproved(this))
		{			
			this.hasUnresolvedIntersection = true;
		}
		else
		{
			this.hasUnresolvedIntersection = false;
			if(!this.isTouchingHelicopter
			   && this.touchedSite != this.lastTouchedSite)
			{
				Audio.play(Audio.landing);
				this.isTouchingHelicopter = true;
			}
		}		
	}

	public static void getRidOfSomeEnemies(Helicopter helicopter,
										   EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy,
										   EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{
		for(Enemy e : enemy.get(ACTIVE))
		{
			if (e.getModel() == BARRIER && e.isOnScreen())
			{
				e.explode(explosion, helicopter);
				e.destroy(helicopter);
			} else if (!e.isOnScreen())
			{
				e.isMarkedForRemoval = true;
			}
		}		
	}
	
	public boolean hasHPsLeft()
	{
		return this.hitPoints >= 1;
	}

	public void setTouchedSiteToRight() {
		this.touchedSite = RIGHT;
	}

	public void setTouchedSiteToLeft() {
		this.touchedSite = LEFT;
	}

	public void setTouchedSiteToBottom()
	{
		this.touchedSite = BOTTOM;
	}

	public void setTouchedSiteToTop()
	{
		this.touchedSite = TOP;
	}

	public void setUntouched()
	{
		this.touchedSite = NONE;
	}

	public boolean isUntouched()
	{
		return this.touchedSite == NONE;
	}
	
	public boolean isKaboomDamageDealer()
	{
		return this.type == EnemyType.KABOOM && !this.isDestroyed;
	}

	public void grantGeneralRewards(Helicopter helicopter, EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUps)
	{
		if(this.canCountForKillsAfterLevelUp())
		{
			Events.killsAfterLevelUp++;
		}
		if(this.canDropPowerUp()){this.dropRandomPowerUp(helicopter, powerUps);}
		if(this.isMiniBoss){Audio.play(Audio.applause2);}
	}
	
	public boolean isRock()
	{
		return type == EnemyType.ROCK;
	}
	
	public int getCloakingTimer()
	{
		return cloakingTimer;
	}
	
	public boolean isCloaked()
	{
		return 	this.cloakingTimer > Enemy.CLOAKING_TIME
			&& this.cloakingTimer <= Enemy.CLOAKING_TIME + Enemy.CLOAKED_TIME;
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
	
	public int getShootTimer()
	{
		return shootTimer;
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
		return GROUND_Y - this.getHeight();
	}
	
	public final EnemyModelType getModel()
	{
		return type.getModel();
	}
	
	public boolean isRemainingAfterEnteringRepairShop()
	{
		return false;
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
}