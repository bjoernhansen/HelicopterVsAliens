package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.EnemyPainter;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.BasicEnemy;
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
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.DESTROYED;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.BOTTOM;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.LEFT;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.NONE;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.RIGHT;
import static de.helicopter_vs_aliens.model.enemy.barrier.BarrierPositionType.TOP;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
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
		// TODO EnumMap verwenden
		public final Enemy[] servants;
    	// TODO EnumMap verwenden
		final int [] timeSinceDeath;
    	
    	public FinalEnemyOperator()
    	{
    		this.servants = new Enemy [NR_OF_BOSS_5_SERVANTS];
    		this.timeSinceDeath = new int [NR_OF_BOSS_5_SERVANTS];
    	}
    }
	
	// Konstanten
	public static final int
		SPEED_KILL_BONUS_TIME 	= 15, // Zeit [frames], innerhalb welcher für einen Kamaitachi-Extra-Bonus Gegner besiegt werden müssen, erhöht sich um diesen Wert
		CLOAKING_TIME = 135, // Zeit, die beim Vorgang der Tarnung und Enttarnung vergeht
		CLOAKED_TIME = 135,     // Zeit, die ein Gegner getarnt bleibt
		SNOOZE_TIME = 100,    // Zeit, die vergeht, bis sich ein aktives Hindernis in Bewegung setzt
		DISABLED = -1; // TODO unnötig machen
	
	public static final Point2D
		ZERO_SPEED = new Point2D.Float(0, 0);
	
	private static final float
		RADAR_DETECTABILITY = 0.2f;        // Alpha-Wert: legt fest, wie stark ein getarnter Gegner bei aktiviertem Radar noch zu sehen ist
		protected static final float HEIGHT_FACTOR 				= 0.28f;    // legt das Verhältnis von Höhe und Länge für die meisten Gegner fest
		protected static final float HEIGHT_FACTOR_SUPERSIZE 	= 0.65f;    // legt das Verhältnis von Höhe und Länge für besonders hohe Gegner fest
		private static final float ROCK_PROB					= 0.05f;
	private static final float KABOOM_PROB				    = 0.02f;    // Rate mit der Kaboom-Gegner erscheinen
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

	
	private static final float[]
		RETURN_PROB	= { 0.013f,	 	// SMALL_SHIELD_MAKER
						0.013f,  	// BIG_SHIELD_MAKER
						0.007f,  	// BODYGUARD
						0.01f,  	// HEALER
						0.04f}; 	// PROTECTOR
	
	private static final int
		// Raum-Konstanten
		SAVE_ZONE_WIDTH = 116;
	protected static final int APPEARANCE_DISTANCE = 10;
	private static final int SHIELD_TARGET_DISTANCE = 20;
	private static final int DISAPPEARANCE_DISTANCE = 100;
	private static final int BARRIER_DISTANCE = 100;

	protected static final int KABOOM_WIDTH = 120;
	
	protected static final int PROTECTOR_WIDTH = 90;
	private static final int KABOOM_Y_TURN_LINE = GROUND_Y - (int) (HEIGHT_FACTOR * KABOOM_WIDTH);
	
	private static final int// Zeit-Konstanten
		ROCK_FREE_TIME = 250;    // Zeit die mind. vergeht, bis ein neuer Hindernis-Gegner erscheint
		private static final int EMP_SLOW_TIME = 175;    // Zeit, die von EMP getroffener Gegner verlangsamt bleibt // 113
		private static final int EMP_SLOW_TIME_BOSS = 110;
	private static final int INACTIVATION_TIME = 150;
	private static final int STUNNING_TIME_BASIS = 45;    // Basis-Wert zur Berechnung der Stun-Zeit nach Treffern von Stopp-Raketen
		private static final int BORROW_TIME = 65;
	private static final int MIN_TURN_TIME = 31;
	private static final int MIN_TURN_NOISELESS_TIME = 15;
	private static final int STATIC_CHARGE_TIME = 110;
	private static final int MAX_BARRIER_NUMBER = 3;
	
	private static final int// Level-Voraussetzungen
		MIN_BARRIER_LEVEL = 2;
	private static final int MIN_POWER_UP_LEVEL = 3;
	private static final int MIN_FUTURE_LEVEL = 8;
	private static final int MIN_KABOOM_LEVEL = 12;
	protected static final int MIN_SPIN_SHOOTER_LEVEL = 23;
	private static final int MIN_ROCK_LEVEL = 27;
	
	private static final int// für Boss-Gegner
		NR_OF_BOSS_5_SERVANTS = 5;
	private static final int HEALED_HIT_POINTS = 11;
	private static final int STANDARD_REWARD_FACTOR = 1;
	private static final int MINI_BOSS_REWARD_FACTOR = 4;
	
	private static final int// TODO die 4 austauschen / anders lösen
		PRE_READY = 1;
	protected static final int READY = 0;
	private static final int ACTIVE_TIMER = 1;
	
	private static final int[]
		MIN_ABSENT_TIME = { 175,  // SMALL_SHIELD_MAKER
							175,  // BIG_SHIELD_MAKER
							900,  // BODYGUARD
							250,  // HEALER
							90}; // PROTECTOR
	
	private static final Point
		TURN_DISTANCE = new Point(50, 10),
		TARGET_DISTANCE_VARIANCE = new Point(10, 3),
		SHIELD_MAKER_STAMPEDE_SPEED = new Point(10, 10),
		SHIELD_MAKER_CALM_DOWN_SPEED = new Point(3, 3);
		
	protected static final Rectangle
		TURN_FRAME = new Rectangle(TURN_DISTANCE.x,
								   TURN_DISTANCE.y,
								   Main.VIRTUAL_DIMENSION.width 
								   	- 2*TURN_DISTANCE.x,
								   GROUND_Y 
									- SAVE_ZONE_WIDTH
									- 2*TURN_DISTANCE.y);

	private static final EnemySelector
		ENEMY_SELECTOR = new EnemySelector();

	// statische Variablen (keine Konstanten)
	public static int
		maxNr,				 	// bestimmt wie viele Standard-Gegner gleichzeitig erscheinen können
		maxBarrierNr,			// bestimmt wie viele Hindernis-Gegner gleichzeitig erscheinen können
		currentNumberOfBarriers; // aktuelle Anzahl von "lebenden" Hindernis-Gegnern
	
	public static Enemy currentRock;
	public static Enemy carrierDestroyedJustNow;  		// Referenz auf den zuletzt zerstörten Carrier-Gegner
	
	public static final Enemy[]
		livingBarrier = new Enemy [MAX_BARRIER_NUMBER];
	
	public static EnemyType
		nextBossEnemyType;		 	// bestimmt, welche Boss-Typ erstellt wird
	
	private static int 
		// TODO selection ist kein guter Bezeichner
		selection;            // bestimmt welche Typen von Gegnern zufällig erscheinen können
		private static int selectionBarrier;    // bestimmt den Typ der Hindernis-Gegner
		private static int rockTimer;            // reguliert das Erscheinen von "Rock"-Gegnern
		protected static int barrierTimer;		// reguliert das Erscheinen von Hindernis-Gegnern
		
	// für die Tarnung nötige Variablen
    public static final float[]
    	scales = { 1f, 1f, 1f, RADAR_DETECTABILITY},
    	offsets = new float[4];	
	
    private static final RescaleOp
		ROP_CLOAKED = new RescaleOp(scales, offsets, null);
    
	private static boolean
		wasEnemyCreationPaused =  false,	// = false: es werden keine neuen Gegner erzeugt, bis die Anzahl aktiver Gegner auf 0 fällt
		makeBossTwoServants =  false,	// make-Variablen: bestimmen, ob ein bestimmter Boss-Gegner zu erzeugen ist
		makeBoss4Servant =  false,
	    makeAllBoss5Servants =  false;
	
	private static final boolean[]
	    makeBoss5Servant	= 	  {false,	// SMALL_SHIELD_MAKER
	                        	   false,	// BIG_SHIELD_MAKER
	                        	   false,	// BODYGUARD
	                        	   false,	// HEALER
	                        	   false};	// PROTECTOR
			
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
		isLasting,					// = true: Gegner bleibt nach Betreten der Werkstatt noch aktiv
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

    public EnemyModelType
		model;				// legt das Aussehen (Model) des Gegners fest

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
	private int
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
	protected float dimFactor;
    
    protected boolean
		canExplode;            // = true: explodiert bei Kollisionen mit dem Helikopter
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
        protected boolean hasYPosSet;                // = false --> y-Position wurde nicht vorab festgelegt und muss automatisch ermittelt werden
        private boolean hasCrashed;            // = true: Gegner ist abgestürzt
        private boolean isEmpShocked;            // = true: Gegner steht unter EMP-Schock --> ist verlangsamt
        private boolean isMarkedForRemoval;        // = true --> Gegner nicht mehr zu sehen; kann entsorgt werden
        private boolean isUpperShieldMaker;        // bestimmt die Position der Schild-Aufspannenden Servants von Boss 5
        private boolean isShielding;            // = true: Gegner spannt gerade ein Schutzschild für Boss 5 auf (nur für Schild-Generatoren von Boss 5)
        protected boolean isStunable;            // = false für Boss 5; bestimmt, ob ein Gegner von Stopp-Raketen (Orochi-Klasse) "betäubt" werden kann
        protected boolean isCarrier;                // = true
        protected boolean isClockwiseBarrier;        // = true: der Rotor des Hindernisses dreht im Uhrzeigersinn
        private boolean isRecoveringSpeed;
  
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
		this.model = TIT;
		this.targetSpeedLevel.setLocation(ZERO_SPEED);
		this.setX(Main.VIRTUAL_DIMENSION.width + APPEARANCE_DISTANCE);
		this.direction.setLocation(-1, Calculations.randomDirection());
		this.callBack = 0;
		this.shield = 0;
		this.dimFactor = 1.5f;
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
		this.isLasting = false;
		this.isTouchingHelicopter = false;
		this.isSpeedBoosted = false;
		this.canExplode = false;
		this.canLearnKamikaze = false;
		this.canFrontalSpeedup = false;
		this.canSinusMove = false;
		this.isShielding = false;
		this.canTurn = false;
		this.canLoop = false;
		this.isClockwiseBarrier = true;
		this.stoppingBarrier = null;
		this.isPreviousStoppingBarrier = null;
		this.isStunable = true;
		this.isMiniBoss = false;
		this.hasCrashed = false;
		this.canInstantTurn = false;
		this.isCarrier = false;
		this.isRecoveringSpeed = false;
		this.hasHeightSet = false;
		this.hasYPosSet = false;
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
		this.stunningTimer = 0;
		
		this.totalStunningTime = 0;
		this.knockBackDirection = 0;
	}
	
	public void dimmedRepaint()
	{
		primaryColor = Colorations.dimColor(primaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
		secondaryColor = Colorations.dimColor(secondaryColor, Colorations.BARRIER_NIGHT_DIM_FACTOR);
		repaint();
	}
	
	public boolean hasGlowingEyes()
	{
		return !isDestroyed && isMeetingRequirementsForGlowingEyes();
	}
	
	protected abstract boolean isMeetingRequirementsForGlowingEyes();
	
	private void clearImage()
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
			if(this.model != BARRIER)
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
	

	/** 
	 ** 	Level-Anpassung
	 **/

	// TODO dies sollten wohl keine statischen Methoden innerhalb von Enemy sein
	public static void adaptToFirstLevel()
	{
		maxNr = 2;
		nextBossEnemyType = null;
		selection = 3;
		maxBarrierNr = 0;
		selectionBarrier = 1;
	}

	public static void adaptToLevel(Helicopter helicopter, int level, boolean isRealLevelUp)
	{		
		if(level == 1)
		{
			adaptToFirstLevel();
		}
		else if(level == 2){maxNr = 3;}
		else if(level == 3){selection = 6;}
		else if(level == 4){selection = 10; maxBarrierNr = 1;}
		else if(level == 5){selection = 15;}
		else if(level == 6)
		{			
			wasEnemyCreationPaused = false;
			maxNr = 3;
			nextBossEnemyType = null;
			selection = 25;
			maxBarrierNr = 1;
			selectionBarrier = 2;
			
		}
		else if(level == 7){selection = 30; maxBarrierNr = 2;}
		else if(level == 8){
			maxNr = 4;}
		else if(level == 9)
		{
			selection = 35; 
			maxNr = 3;
			maxBarrierNr = 3;
		}
		else if(level == 10)
		{
			wasEnemyCreationPaused = true;
			nextBossEnemyType = EnemyType.BOSS_1;
			selection = 0;
			helicopter.startDecayOfAllCurrentBooster();
		}	  
		else if(level == 11)
		{
			maxNr = 3;
			nextBossEnemyType = null;
			selection = 75;
			maxBarrierNr = 1;
			selectionBarrier = 2;
			
			if(	 helicopter.isCountingAsFairPlayedHelicopter()
				 && !Events.recordTimeManager.hasAnyBossBeenKilledBefore())
			{
				Window.unlock(HelicopterType.HELIOS);
			}
			if(isRealLevelUp){Events.determineHighscoreTimes(helicopter);}
		}				
		else if(level == 12){
			selectionBarrier = 3;}
		else if(level == 13){
			maxBarrierNr = 2; selection = 105;}
		else if(level == 14){selection = 135;} 
		else if(level == 15){
			selectionBarrier = 4;}
		else if(level == 16)
		{			
			wasEnemyCreationPaused = false;
			maxNr = 4;
			nextBossEnemyType = null;
			selection = 155;	
			maxBarrierNr = 2;
			selectionBarrier = 4;
		}
		else if(level == 17){selection = 175;}
		else if(level == 18){
			selectionBarrier = 5;}
		else if(level == 19){
			maxBarrierNr = 3;}
		else if(level == 20)
		{
			wasEnemyCreationPaused = true;
			nextBossEnemyType = EnemyType.BOSS_2;
			selection = 0;
			helicopter.startDecayOfAllCurrentBooster();
			if(helicopter.isCountingAsFairPlayedHelicopter() && !helicopter.getType().hasReachedLevel20())
			{
				Events.helicoptersThatReachedLevel20.add(helicopter.getType());
				helicopter.updateUnlockedHelicopters();
			}
		}
		else if(level == 21)
		{
			maxNr = 3;
			nextBossEnemyType = null;
			selection = 400;
			maxBarrierNr = 2;
			selectionBarrier = 5;
			
			if(isRealLevelUp){Events.determineHighscoreTimes(helicopter);}
		}
		else if(level == 22){selection = 485;}
		else if(level == 23){selection = 570;}
		else if(level == 24){
			maxNr = 4;}
		else if(level == 25){selection = 660;}		
		else if(level == 26)
		{
			wasEnemyCreationPaused = false;
			maxNr = 4;
			nextBossEnemyType = null;
			selection = 735;	
			maxBarrierNr = 2;
			selectionBarrier = 5;
		}		 
		else if(level == 27){selection = 835;}
		else if(level == 28){
			maxNr = 5;}
		else if(level == 29){
			maxNr = 4; maxBarrierNr = 3;}
		else if(level == 30)
		{
			wasEnemyCreationPaused = true;
			nextBossEnemyType = EnemyType.BOSS_3;
			selection = 0;	
			helicopter.startDecayOfAllCurrentBooster();
		}
		else if(level == 31)
		{
			maxNr = 3;
			nextBossEnemyType = null;
			selection = 1670;
			maxBarrierNr = 2;
			selectionBarrier = 5;
			
			if(isRealLevelUp){Events.determineHighscoreTimes(helicopter);}
		}
		else if(level == 32){
			selectionBarrier = 6;}
		else if(level == 33){selection = 2175;}
		else if(level == 34){
			maxNr = 4;}
		else if(level == 35){selection = 3180;} 
		else if(level == 36)
		{
			wasEnemyCreationPaused = false;
			maxNr = 4;
			nextBossEnemyType = null;
			selection = 4185;
			maxBarrierNr = 2;
			selectionBarrier = 6;
		} 
		else if(level == 37){selection = 5525;} 
		else if(level == 38){
			maxNr = 5;}
		else if(level == 39){
			maxNr = 4; maxBarrierNr = 3;}
		else if(level == 40)
		{
			wasEnemyCreationPaused = true;
			nextBossEnemyType = EnemyType.BOSS_4;
			selection = 0;
			helicopter.startDecayOfAllCurrentBooster();
		}			  
		else if(level == 41)
		{
			maxNr = 3;
			nextBossEnemyType = null;
			selection = 15235;	
			maxBarrierNr = 2;
			selectionBarrier = 6;
			
			if(isRealLevelUp){Events.determineHighscoreTimes(helicopter);}
		}
		else if(level == 42){
			selectionBarrier = 7; maxNr = 4;}
		else if(level == 43){selection = 20760;}
		else if(level == 44){
			selectionBarrier = 8; maxNr = 5;}
		else if(level == 45){selection = 26285;}
		else if(level == 46)
		{
			wasEnemyCreationPaused = false;
			maxNr = 5;
			nextBossEnemyType = null;
			selection = 31810;
			maxBarrierNr = 2 ;
			selectionBarrier = 8;
		}
		else if(level == 47){
			maxNr = 6;}
		else if(level == 48){
			maxBarrierNr = 3;}
		else if(level == 49){
			maxNr = 7;}
		else if(level == Events.MAXIMUM_LEVEL)
		{
			wasEnemyCreationPaused = true;
			nextBossEnemyType = EnemyType.FINAL_BOSS;
			selection = 0;
			helicopter.startDecayOfAllCurrentBooster();
		}
	}
	
	/** Methoden zur Erstellung von Gegnern
	 */	
	
	// TODO die Begrenzung nach Anzahl funktioniert nicht mehr
	public static void generateNewEnemies(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy, Helicopter helicopter)
	{
		Events.lastCreationTimer++;
		if(wasCarrierDestroyedJustNow()){
			createCarrierServants(helicopter, enemy);}
		else if(wasEnemyCreationPaused){
			verifyCreationStop(enemy, helicopter);}
		if(isBossServantCreationApproved()){
			createBossServant(helicopter, enemy);}
		else if(isEnemyCreationApproved(enemy))
		{
			creation(helicopter, enemy);
		}
	}
	
	private static boolean wasCarrierDestroyedJustNow()
	{
		return carrierDestroyedJustNow != null;
	}
	
	// TODO könnte diese Methode nicht direkt aufgerufen werden, wenn der Carrier zerstört wurde. Die Variable "carrierKilledJustNow" könnte dann entfallen.
	private static void createCarrierServants(Helicopter helicopter,
											  EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
	{
		for(int m = 0; 
				m < (carrierDestroyedJustNow.isMiniBoss
						? 5 + Calculations.random(3)
						: 2 + Calculations.random(2));
				m++)
			{
				creation(helicopter, enemy);
			}			
			carrierDestroyedJustNow = null;
	}

	private static void verifyCreationStop(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy,
										   Helicopter helicopter)
	{
		if(	enemy.get(ACTIVE).isEmpty()
			&& carrierDestroyedJustNow == null
			&& !(helicopter.isUnacceptablyBoostedForBossLevel()
				 && Events.isBossLevel()) )
		{
			wasEnemyCreationPaused = false;
			if(Events.isBossLevel())
			{
				maxNr = 1;
				maxBarrierNr = 0;
				Events.setBossLevelUpConditions();
			}
		}
	}	
		
	private static boolean isBossServantCreationApproved()
	{
		return     makeBossTwoServants
				|| makeBoss4Servant
				|| makeAllBoss5Servants
				|| hasToMakeBoss5Servants();
	}
	
	private static boolean hasToMakeBoss5Servants()
	{			
		for(int type = 0; type < NR_OF_BOSS_5_SERVANTS; type++)
		{
			if(makeBoss5Servant[type]){return true;}
		}
		return false;
	}
	
	private static void createBossServant(Helicopter helicopter,
										  EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
	{
		// TODO Wir haben hier 3 boolesche Variablen, nur um Festzulegen, welche Servants zu erzeugen sind. Ein Enum wäre hier besser.
		if(makeBossTwoServants)
		{
			createBoss2Servants(helicopter, enemy);
		}
		else if(makeBoss4Servant)
		{								 
            makeBoss4Servant = false;
			creation(helicopter, enemy);                
		}
		else if(makeAllBoss5Servants)
		{
			createAllBoss5Servants(helicopter, enemy);
		}
		else
		{
			EnemyType.getFinalBossServantTypes().forEach(type -> {
				if(makeBoss5Servant[id(type)])
				{
					makeBoss5Servant[id(type)] = false;
					nextBossEnemyType = type;
					creation(helicopter, enemy);
				}
			});
		}
	}
	
	private static void createBoss2Servants(Helicopter helicopter,
											EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
    	makeBossTwoServants = false;
		wasEnemyCreationPaused = true;
		nextBossEnemyType = EnemyType.BOSS_2_SERVANT;
		maxNr = 12;
		for (int m = 0; m < maxNr; m++)
		{
			creation(helicopter, enemy);
		}
    }
    
    private static void createAllBoss5Servants(Helicopter helicopter,
											   EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
    	makeAllBoss5Servants = false;
    	EnemyType.getFinalBossServantTypes().forEach(type -> {
			nextBossEnemyType = type;
			creation(helicopter, enemy);
		});
    }	

    private static boolean isEnemyCreationApproved(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemies)
	{		
		int numberOfEnemies = enemies.get(ACTIVE).size();
		return     !hasNumberOfEnemiesReachedLimit(numberOfEnemies)
				&& !isMajorBossActive(enemies)
				&& !wasEnemyCreationPaused
				&& !Events.wasMaximumLevelExceeded()
				&& Events.hasEnoughTimePassedSinceLastCreation()
				&& Events.wereRandomRequirementsMet(maxNr + maxBarrierNr - numberOfEnemies);
	}
	
	private static boolean isMajorBossActive(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
	{
		return !enemy.get(ACTIVE).isEmpty() && enemy.get(ACTIVE).getFirst().type.isMajorBoss();
	}
	
	private static boolean hasNumberOfEnemiesReachedLimit(int numberOfEnemies)
	{
		return numberOfEnemies >= maxNr + maxBarrierNr;
	}
	
	private static void creation(Helicopter helicopter, EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemies)
	{
		/*Iterator<Enemy> i = enemy.get(INACTIVE).iterator();
		Enemy e;
		if (i.hasNext())
		{
			e = i.next();
			i.remove();
		}
		else{e = EnemyFactory.createEnemy();}*/
		
		LinkedList<Enemy> activeEnemies = enemies.get(ACTIVE);
		int activeEnemyCount = activeEnemies.size();
		GameEntityFactory<Enemy> enemyFactory = getEnemyFactory(activeEnemyCount);
		Enemy enemy = enemyFactory.makeInstance();
		activeEnemies.add(enemy);
		if(enemy.countsForTotalAmountOfEnemiesSeen()){helicopter.numberOfEnemiesSeen++;}
		Events.lastCreationTimer = 0;
		enemy.create(helicopter);
	}
	
	private boolean countsForTotalAmountOfEnemiesSeen()
	{
		// TODO countsForTotalAmountOfEnemiesSeen implementieren, an anderen Stellen die -- aufrufe streichen; finale instanzvariable anlegen
		return true;
	}
	
	private static GameEntityFactory<Enemy> getEnemyFactory(int activeEnemyCount)
	{
		if(wasCarrierDestroyedJustNow()){return EnemyType.ESCAPED_SPEEDER;}
		if(barrierCreationApproved(activeEnemyCount)){return getNextBarrierType();}
		if(rockCreationApproved()){return EnemyType.ROCK;}
		if(kaboomCreationApproved()){return EnemyType.KABOOM;}
		if(isBossEnemyToBeCreated()){return nextBossEnemyType;}
		return getNextDefaultEnemyType();
	}
	
	private static boolean isBossEnemyToBeCreated()
	{
		return nextBossEnemyType != null;
	}
	
	private static EnemyType getNextDefaultEnemyType()
	{
		return ENEMY_SELECTOR.getType(Calculations.random(selection));
	}
	
	protected void create(Helicopter helicopter)
	{
		hitPoints = calculateHitPoints();
		startingHitPoints = hitPoints;
		
		// Festlegen der Höhe und der y-Position des Gegners
		if(!hasHeightSet){setHeight();}
		if(!hasYPosSet){setInitialY();}
		
		
		this.speedLevel.setLocation(this.targetSpeedLevel);
		this.setPaintBounds((int)this.bounds.getWidth(),
			(int)this.bounds.getHeight());
		this.assignImage(helicopter);
		
		if(isShootingStandardEnemy())
		{
			this.initializeShootDirectionOfDefaultEnemies();
		}
	}

	private void assignImage(Helicopter helicopter)
	{
		for(int i = 0; i < 2; i++)
		{
			this.image[i] = new BufferedImage((int)(1.028f * this.paintBounds.width),
											  (int)(1.250f * this.paintBounds.height),
											  BufferedImage.TYPE_INT_ARGB);
			this.graphicsAdapters[i] = getGraphicAdapter(this.image[i]);
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
				
				enemyPainter.paintImage(getGraphicAdapter(tempImage), this,1-2*i, Color.red, true);
				getGraphicAdapter(this.image[2+i]).drawImage(tempImage, ROP_CLOAKED, 0, 0);
			}
		}		
	}

	private static boolean barrierCreationApproved(int numberOfEnemies)
	{		
		return Events.level >= MIN_BARRIER_LEVEL 
				&& !Events.isBossLevel()
				&& barrierTimer == 0
				&& (Calculations.tossUp(0.35f)
					|| (numberOfEnemies - currentNumberOfBarriers >= maxNr))
				&& currentNumberOfBarriers < maxBarrierNr;
	}
	
	
 
	private static EnemyType getNextBarrierType()
	{
		int randomBarrierSelectionModifier = isBarrierFromFutureCreationApproved()
			? Calculations.random(3)
			: 0;
		int selectedBarrierIndex = Calculations.random(Math.min(selectionBarrier + randomBarrierSelectionModifier, EnemyType.getBarrierTypes().size()));
		return (EnemyType) EnemyType.getBarrierTypes().toArray()[selectedBarrierIndex];
	}
    
    private static boolean isBarrierFromFutureCreationApproved()
    {
        return Calculations.tossUp(0.05f) && Events.level >= MIN_FUTURE_LEVEL;
    }
    
    private void placeAtPausePosition()
	{
		this.callBack--;
		this.uncloak(DISABLED);
		this.barrierTeleportTimer = READY;
		this.setY(GROUND_Y + 2 * this.bounds.getWidth());
	}

	private static boolean rockCreationApproved()
	{
		return currentRock == null
				&& Events.level >= MIN_ROCK_LEVEL 
				&& !Events.isBossLevel()
				&& rockTimer == 0
				&& Calculations.tossUp(ROCK_PROB);
	}
	
	private static boolean kaboomCreationApproved()
	{		
		return Events.level >= MIN_KABOOM_LEVEL 
				&& !Events.isBossLevel()
				&& Calculations.tossUp(KABOOM_PROB);
	}

	private void placeNearHelicopter(Helicopter helicopter)
	{		
		boolean isLeftOfHelicopter = !(helicopter.getBounds().getMaxX() + (0.5f * this.bounds.getWidth() + BARRIER_DISTANCE) < 1024);
					
		int x, 
			y = (int)(helicopter.getBounds().getY() 
				+ helicopter.getBounds().getHeight()/2
				- this.bounds.getWidth()
				+ Math.random()*this.bounds.getWidth());
		
		if(isLeftOfHelicopter)
		{
			x = (int)(helicopter.getBounds().getX() 
				-3*this.bounds.getWidth()/2  
				- 10
				+ Math.random()*(this.bounds.getWidth()/3));	
		}
		else
		{
			x = (int)(helicopter.getBounds().getMaxX() 
				+ BARRIER_DISTANCE);		
		}
		this.setLocation(x,
						  Math.max(0, Math.min(y, GROUND_Y-this.bounds.getWidth())));
	}
	
	public int getHitPoints()
	{
		return hitPoints;
	}
	
	protected void setHitPoints(int hitPoints)
	{
		this.hitPoints = hitPoints;
	}
	
	private void setX(double x)
	{
		this.bounds.setRect(x, 
							this.bounds.getY(), 
							this.bounds.getWidth(), 
							this.bounds.getHeight());
	}
	
	private void setY(double y)
	{
		this.bounds.setRect(this.bounds.getX(), 
							y, 
							this.bounds.getWidth(), 
							this.bounds.getHeight());
	}
	
	protected void setLocation(double x, double y)
	{
		this.bounds.setRect(x, 
							y, 
							this.bounds.getWidth(), 
							this.bounds.getHeight());
	}
			
	protected void setWidth(double width)
	{
		this.bounds.setRect(this.bounds.getX(), 
							this.bounds.getY(),
							width,
							this.bounds.getHeight());
	}
	
	protected void setVarWidth(int width)
	{
		setWidth(width + Calculations.random(width/(this.model == BARRIER ? 5 : 10)));
	}
	
	private void setHeight()
	{
		double height;
		if(this.model == TIT)
		{
			height = (int)(HEIGHT_FACTOR * this.bounds.getWidth());
		}		
		else if(this.model == CARGO)
		{
			height = (int)(HEIGHT_FACTOR_SUPERSIZE * this.bounds.getWidth());
		}
		else height = this.bounds.getWidth();	
		
		this.bounds.setRect(this.bounds.getX(), 
							this.bounds.getY(), 
							this.bounds.getWidth(), 
							height);
	}
	
	private void setInitialY()
	{
		if(this.model == BARRIER)
		{
			setInitialY(Math.random()*(GROUND_Y - this.bounds.getHeight()));
		}
		else
		{
			setInitialY(90 + Math.random()*(220 - this.bounds.getHeight()));
		}
	}	
	
	protected void setInitialY(double y)
	{
		this.setY(y);
		this.hasYPosSet = true;
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
	
	
	
	private static GraphicsAdapter getGraphicAdapter(BufferedImage bufferedImage)
	{
		GraphicsAdapter graphicsAdapter = Graphics2DAdapter.of(bufferedImage);
		graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
										 RenderingHints.VALUE_ANTIALIAS_ON);
		return graphicsAdapter;
	}

	/* Die folgende Funktion reguliert die Gegner-Bewegung:
	 * 1. Unter Berücksichtigung jeglicher Eventualitäten (specialManöver, ausweichen, etc.)
	 *	  werden die neuen Koordinaten berechnet.
	 * 2. Der Gegner wird an Stelle seiner neuen Koordinaten gemalt.
	 */
	public static void updateAllActive(Controller controller,
									   Helicopter helicopter)
	{
		if(rockTimer > 0){
			rockTimer--;}
		if(Scenery.backgroundMoves && barrierTimer > 0){
			barrierTimer--;}
		countBarriers(controller.enemies);
		
		for(Iterator<Enemy> i = controller.enemies.get(ACTIVE).iterator(); i.hasNext();)
		{
			Enemy enemy = i.next();			
			if(!enemy.isDestroyed && !enemy.isMarkedForRemoval)
			{								
				enemy.update(controller, helicopter);
			}
			else if(enemy.isDestroyed)
			{
				i.remove(); 
				controller.enemies.get(DESTROYED).add(enemy);
			}			
			else
			{
				enemy.clearImage();
				i.remove(); 
				// controller.enemies.get(INACTIVE).add(enemy);
			}			
		}
	}	

	private static void countBarriers(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
	{
		Arrays.fill(livingBarrier, null);
		currentNumberOfBarriers = 0;
		for(Enemy e : enemy.get(ACTIVE))
		{
			if (e.model == BARRIER
				&& !e.isDestroyed
				&& !e.isMarkedForRemoval)
			{
				livingBarrier[currentNumberOfBarriers] = e;
				currentNumberOfBarriers++;
			}
		}		
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
			&& this.model != BARRIER
			&& !(this.cloakingTimer > CLOAKING_TIME
				&& this.cloakingTimer <= CLOAKING_TIME + CLOAKED_TIME
				&& !hasRadarDevice);
	}
	
	private void update(Controller controller, Helicopter helicopter)
	{												
		this.lifetime++;		
		this.updateTimer();
		if(this.callBack > 0 && Events.isBossLevel()){this.callBack = 0;}
		if(this.type != EnemyType.ROCK){
			checkForBarrierCollision();}
		if(this.stunningTimer == READY)
		{
			this.updateStoppableTimer();
			if(this.type.isMajorBoss()){this.calculateBossManeuver(controller.enemies);}
			this.calculateFlightManeuver(controller, helicopter);
			this.validateTurns();
		}		
		this.calculateSpeed(helicopter);
		this.move();
		
		if(helicopter.canCollideWith(this)){this.collision(controller, helicopter);}
		if(helicopter.getType() == HelicopterType.PEGASUS){this.checkForEmpStrike(controller, (Pegasus)helicopter);}
		if(this.hasDeadlyGroundContact()){this.destroy(helicopter, controller.powerUps, false);}
		if(this.isToBeRemoved()){this.prepareRemoval();}
		this.setPaintBounds();
	}	
	
	private void updateStoppableTimer()
	{
		if(this.snoozeTimer > 0){this.snoozeTimer--;}
	}

	private boolean hasDeadlyGroundContact()
	{	
		return this.bounds.getMaxY() > GROUND_Y
			   && this.model != BARRIER 
			   && !this.isDestroyed
			   && this.type != EnemyType.ROCK;
	}

	private void calculateBossManeuver(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
	{
		     if(this.type == EnemyType.BOSS_4)    {this.boss4Action(enemy);}
		else if(this.type == EnemyType.FINAL_BOSS){this.finalBossAction();}
		else if(this.shieldMakerTimer != DISABLED){this.shieldMakerAction();}
		else if(this.type == EnemyType.BODYGUARD) {this.bodyguardAction();}
		else if(this.type == EnemyType.HEALER
				&& this.dodgeTimer == READY){this.healerAction();}
	}

	private void checkForBarrierCollision()
	{
		this.isPreviousStoppingBarrier = this.stoppingBarrier;
		if(this.stoppingBarrier == null || !this.stoppingBarrier.bounds.intersects(this.bounds))
		{
			this.stoppingBarrier = null;
			for(int i = 0; i < currentNumberOfBarriers; i++)
			{
				if(	   livingBarrier[i] != this
					&& livingBarrier[i].bounds.intersects(this.bounds))
					
				{
					this.stoppingBarrier = livingBarrier[i];
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
			   							 this.bounds.getCenterX(), 
			   							 this.stoppingBarrier.bounds.getCenterX())
			   	// Gegner sollen nicht an Barriers abdrehen, bevor sie im Bild waren.					
				&& this.isOnScreen())
			{							
				this.performXTurnAtBarrier();
			}
		}
		else
		{
			if(turnaroundIsTurnAway(this.direction.y,
										this.bounds.getCenterY(), 
										this.stoppingBarrier.bounds.getCenterY()))
			{	
				this.direction.y = -this.direction.y;				
			}
		}		
	}
		
	boolean hasLateralFaceTouchWith(Enemy barrier)
	{
		return  
			Calculations.getIntersectionLength(	this.bounds.getMinX(),
											this.bounds.getMaxX(),
											barrier.bounds.getMinX(),
											barrier.bounds.getMaxX())										 
			<										 									 
			Calculations.getIntersectionLength(	this.bounds.getMinY(),
											this.bounds.getMaxY(),
											barrier.bounds.getMinY(),
											barrier.bounds.getMaxY());	
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
		if( this.stunningTimer > 0) {this.stunningTimer--;}
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
			&& this.bounds.getMinX() < 0.85 * Main.VIRTUAL_DIMENSION.width)
		{
			this.canEarlyTurn = false;
			this.direction.x = 1;
		}
							
		// Frontal-Angriff
		if( this.canLearnKamikaze
				
			&& ((this.direction.x == 1 
					&& helicopter.getBounds().getMaxX() < this.bounds.getMinX() 
					&& this.bounds.getX() - helicopter.getBounds().getX() < 620)
				||
				(this.direction.x == -1 
					&& this.bounds.getMaxX() < helicopter.getBounds().getMinX() 
					&& helicopter.getBounds().getX() - this.bounds.getX() < 620)))
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
		if(this.snoozeTimer == PRE_READY){this.endSnooze();}
		
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
			&& helicopter.getBounds().getX() - this.bounds.getX() > -350	)
		{				
			this.speedLevel.setLocation(6 + this.targetSpeedLevel.getX(),
										 this.speedLevel.getY());
		}
		if(this.canChaosSpeedup
		   && (helicopter.getBounds().getX() - this.bounds.getX()) > -160)
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
		if(	this.burrowTimer == DISABLED)
		{
			this.speedLevel.setLocation(this.targetSpeedLevel);
		}
		else{this.endInterruptedBorrowProcedure();}
		
		if(	this.barrierTeleportTimer != DISABLED)
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
		if(	this.stoppingBarrier != null
			&& this.burrowTimer == DISABLED)
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
    	this.takeDamage((int)this.getEmpVulnerabilityFactor() * pegasus.getEmpDamage());
		this.isEmpShocked = true;
		if(this.type == EnemyType.BOSS_4){this.spawningHornetTimer = READY;}
		this.disableSiteEffects(pegasus);
				
		if(this.hasHPsLeft())
		{			
			Audio.play(Audio.stun);
			if(this.model == BARRIER){this.snooze(true);}
			else if(this.teleportTimer == READY ){this.teleport();}
			else if(this.isStunable && !this.isShielding)
			{
				this.empSlowedTimer = this.type.isMainBoss()
											? EMP_SLOW_TIME_BOSS 
											: EMP_SLOW_TIME;
			}
			this.reactToHit(pegasus, null);
			
			Explosion.start(controller.explosions, pegasus,
							this.bounds.getCenterX(), 
							this.bounds.getCenterY(), STUNNING, false);
		}
		else
		{
			Audio.play(Audio.explosion2);
			pegasus.empWave.kills++;
			pegasus.empWave.earnedMoney += this.calculateReward(pegasus);
			this.die(controller, pegasus, null);
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
			   			&&(((this.callBack > 0 || this.type.isMajorBoss()) && this.bounds.getMinX() < TURN_FRAME.getMinX())
			   					|| (this.type == EnemyType.HEALER && this.bounds.getX() < 563)))
			   		||
			   		(this.direction.x == 1 
			   			&&(((this.callBack > 0 || this.type.isMajorBoss()) && this.bounds.getMaxX() > TURN_FRAME.getMaxX() && !this.canLearnKamikaze)
			   					|| (this.type == EnemyType.BODYGUARD && (this.bounds.getX() + this.bounds.getWidth() > 660)))));
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
				&&( (this.bounds.getMinY() <= (this.model == BARRIER ? 0 : TURN_FRAME.getMinY()) 
			   	  	 && this.direction.y < 0) 
			        ||
			        (this.bounds.getMaxY() >= (this.model == BARRIER ? GROUND_Y : TURN_FRAME.getMaxY()) 
			   	     &&  this.direction.y > 0 
			   	     && !this.isDestroyed) );
	}

	private void changeYDirection()
	{
		this.direction.y = -this.direction.y;
		if(this.canSinusMove){this.speedLevel.setLocation(this.speedLevel.getX(), 1);}
		if(this.model == BARRIER)
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
			   && (    (this.bounds.getMinX() > Main.VIRTUAL_DIMENSION.width + DISAPPEARANCE_DISTANCE
					   	 && this.direction.x ==  1)
				    || (this.bounds.getMaxX() < -DISAPPEARANCE_DISTANCE));
	}
	
	private boolean isDodging()
	{		
		return this.dodgeTimer > 0;
	}

	public boolean isOnScreen()
	{		
		return this.bounds.getMaxX() > 0 
			   && this.bounds.getMinX() < Main.VIRTUAL_DIMENSION.width;
	}
	
	private void prepareRemoval()
	{
		this.isMarkedForRemoval = true;
		if(this.type == EnemyType.ROCK)
		{
			currentRock = null;
			rockTimer = ROCK_FREE_TIME;
		}
		else if(this.isMiniBoss)
		{
			BasicEnemy.currentMiniBoss = null;
		}		
	}	
	
	private boolean isEmpShockable(Pegasus pegasus)
	{
		return     !this.isEmpShocked
				&& !this.isDestroyed
				&& !this.isInvincible()
				&& !(this.barrierTeleportTimer != DISABLED && this.barrierShootTimer == DISABLED)
				&& pegasus.empWave.ellipse.intersects(this.bounds);
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
			if(this.bounds.getY() < helicopter.getBounds().getY()){this.direction.y = 1;}
			else{this.direction.y = -1;}	
		}		
	}
	
	private boolean atEyeLevel(Helicopter helicopter)
	{
		return this.bounds.intersects(Integer.MIN_VALUE/2f,
									  helicopter.getBounds().getY(),
									  Integer.MAX_VALUE,
									  helicopter.getBounds().getHeight());
	}
	
	private void finalBossAction()
    {
		if(this.speedLevel.getX() > 0)
		{
			if(this.speedLevel.getX() - 0.5 <= 0)
			{
				this.speedLevel.setLocation(ZERO_SPEED);
				boss.setLocation(this.bounds.getCenterX(), 
						 		 this.bounds.getCenterY());
				makeAllBoss5Servants = true;
			}
			else
			{
				this.speedLevel.setLocation(this.speedLevel.getX()-0.5,	0);
			}
		}
		else for(int servantType = 0; servantType < NR_OF_BOSS_5_SERVANTS; servantType++)
		{
			if(this.operator.servants[servantType] == null)
			{	
				if(Calculations.tossUp(RETURN_PROB[servantType])
					&& this.operator.timeSinceDeath[servantType] > MIN_ABSENT_TIME[servantType])
				{
					makeBoss5Servant[servantType] = true;
				}
				else{this.operator.timeSinceDeath[servantType]++;}
			}
		}		
	}
	
	protected int id()
	{
		return id(this.type);
	}
	
	public static int id(EnemyType type)
	{
		switch(type)
		{
			case SMALL_SHIELD_MAKER:
				return 0;
			case BIG_SHIELD_MAKER:
				return 1;
			case BODYGUARD:
				return 2;
			case HEALER:
				return 3;
			case PROTECTOR:
				return 4;
			default:
				return -1;
		}
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
	
	private void bodyguardAction()
	{
		if(Events.boss.shield < 1)
		{
			this.canKamikaze = true;
			this.speedLevel.setLocation(7.5, this.speedLevel.getY());
		}
		else
		{
			this.canKamikaze = false;
			this.speedLevel.setLocation(this.targetSpeedLevel);
		}		
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
    		&& ( (this.bounds.getMinX() > helicopter.getBounds().getMaxX() 
    			   && this.direction.x == 1) 
    			 ||
    			 (helicopter.getBounds().getMinX() > this.bounds.getMaxX()
    			   && this.direction.x == -1)))		
    	{
			this.direction.x = -this.direction.x;	
			this.speedLevel.setLocation(0, this.speedLevel.getY());
		}		
		
    	if(((this.bounds.getMaxX() > helicopter.getBounds().getMinX() && this.direction.x == -1)&&
			(this.bounds.getMaxX() - helicopter.getBounds().getMinX() ) < 620) ||
		   ((helicopter.getBounds().getMaxX() > this.bounds.getMinX() && this.direction.x == 1)&&
			(helicopter.getBounds().getMaxX() - this.bounds.getMinX() < 620)))		    
		{			
			if(!this.canLearnKamikaze)
			{
				this.speedLevel.setLocation((this.type == EnemyType.BOSS_4 || this.type == EnemyType.BOSS_3) ? 12 : 8,
											 this.speedLevel.getY());
			}						
			if(this.direction.y == 1 
				&& helicopter.getBounds().getY()  < this.bounds.getY())				
			{							
				this.direction.y = -1;				
				this.speedLevel.setLocation(this.speedLevel.getX(), 0);
			}
			else if(this.direction.y == -1 
					&& helicopter.getBounds().getMaxY() > this.bounds.getMaxY())
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
				     && (helicopter.getBounds().getX() > boss.getX() - 225) ))) 
		{			
			this.burrowTimer = 2 * BORROW_TIME
								+ this.shootingRate * this.shotsPerCycle
								+ (this.bounds.getY() == GROUND_Y 
									? PROTECTOR_WIDTH/8
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
			&& this.bounds.getX() + this.bounds.getWidth() > 0
			&& !(this.cloakingTimer > CLOAKING_TIME && this.cloakingTimer <= CLOAKING_TIME + CLOAKED_TIME)
			&& ((this.direction.x == -1 
				 && helicopter.getBounds().intersects(	
						 				this.bounds.getX() + Integer.MIN_VALUE/2f,
						 				this.bounds.getY() + (this.model == TIT ? 0 : this.bounds.getWidth()/2) - 15,
						 				Integer.MAX_VALUE/2f,
						 				EnemyMissile.DIAMETER+30))
				||
				((this.direction.x == 1 
				  && helicopter.getBounds().intersects(
						  				this.bounds.getX(), 
						  				this.bounds.getY() + (this.model == TIT ? 0 : this.bounds.getWidth()/2) - 15,
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
				&&	  (helicopter.getBounds().getX()    < this.bounds.getX()         && this.shootingDirection.getX() > 0)
					||(helicopter.getBounds().getMaxX() > this.bounds.getMaxX() && this.shootingDirection.getX() < 0) )
			{
				this.shootingDirection.setLocation(-this.shootingDirection.getX(), this.shootingDirection.getY());
			}
		}		
		if( this.barrierShootTimer <= this.shotsPerCycle * this.shootingRate
			&& this.bounds.getX() + this.bounds.getWidth() > 0
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
						( (helicopter.getBounds().getX() + (helicopter.isMovingLeft ? Helicopter.FOCAL_PNT_X_LEFT : Helicopter.FOCAL_PNT_X_RIGHT))
							  - (this.bounds.getX() +       this.bounds.getWidth()/2)), 
						  (helicopter.getBounds().getY() + Helicopter.FOCAL_PNT_Y_EXP) 
						  	  - (this.bounds.getY() +       this.bounds.getHeight()/2)) ;
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
			if(this.bounds.getMaxX() > 0){Audio.play(Audio.cloak);}
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
					this.placeAtPausePosition();
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

	private void boss4Action(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
    {
    	if(    this.bounds.getX() < 930 
    		&& this.bounds.getX() > 150)
    	{
    		this.spawningHornetTimer++;
    	}
		if(this.spawningHornetTimer == 1)
		{
			this.speedLevel.setLocation(11, 11);
			this.canMoveChaotic = true;
			this.canKamikaze = true;
		}
		else if(this.spawningHornetTimer >= 50)
		{
			if(this.spawningHornetTimer == 50)
			{
				this.speedLevel.setLocation(3, 3);
				this.canMoveChaotic = false;
				this.canKamikaze = false;
			}
			else if(this.spawningHornetTimer == 90)
			{
				this.speedLevel.setLocation(ZERO_SPEED);
			}						
			if(enemy.get(ACTIVE).size() < 15
			   && (    this.spawningHornetTimer == 60
			   	    || this.spawningHornetTimer == 90
			   	    || Calculations.tossUp(0.02f)))
			{
				boss.setLocation(	this.bounds.getX() + this.bounds.getWidth() /2, 
									this.bounds.getY() + this.bounds.getHeight()/2);
				makeBoss4Servant = true;
			}
		}
    }
	
	private void sinusLoop()
    {
		this.speedLevel.setLocation(
				this.speedLevel.getX(),
				Math.max(4.0, 0.15f*(145-Math.abs(this.bounds.getY()-155))));  
		
    	if(this.canLoop)
    	{
    		if(this.direction.x == -1 && this.bounds.getY()-155>0)
    		{
    			this.direction.x = 1;
    			this.speedLevel.setLocation(11, this.speedLevel.getY());
    		}
    		else if(this.direction.x == 1 && this.bounds.getY()-155<0)
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
		return    helicopter.getBounds().getX() - this.bounds.getX() > -750
			   && helicopter.getBounds().getX() - this.bounds.getX() < -50
			   && helicopter.getBounds().getY() + 56 > this.bounds.getY() + 0.2 * this.bounds.getHeight()
			   && helicopter.getBounds().getY() + 60 < this.bounds.getY() + 0.8 * this.bounds.getHeight();
	}
	
	private boolean isTractorReady() {
		return this.tractor == AbilityStatusType.READY
				&& !this.isEmpSlowed()
				&& this.cloakingTimer < 1
				&& this.bounds.getMaxX() < Main.VIRTUAL_DIMENSION.width;
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
	
	private void shieldMakerAction()
	{			   
		this.shieldMakerTimer++;
		if(this.shieldMakerTimer > 100)
		{
			if(this.shieldMakerTimer == 101){this.calmDown();}
			this.correctShieldMakerDirection();
			if(this.canStartShielding()){this.startShielding();}
		}		
	}
	
	private void calmDown()
	{
		this.speedLevel.setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);	
		this.targetSpeedLevel.setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);
		this.canMoveChaotic = false;
	}

	private void startShielding()
	{
		Audio.play(Audio.shieldUp);
		this.speedLevel.setLocation(ZERO_SPEED);
		this.direction.x = -1;
		this.isShielding = true;
		Events.boss.shield++;
		this.canDodge = true;
		this.shieldMakerTimer = DISABLED;
	}

	// TODO Bedingungen in Methoden auslagern
	private void correctShieldMakerDirection()
	{
		if(      this.bounds.getX() 
					< Events.boss.bounds.getCenterX() 
					  - TARGET_DISTANCE_VARIANCE.x)
		{
			this.direction.x =  1;
		}
		else if( this.bounds.getX() 
					> Events.boss.bounds.getCenterX()
					  + TARGET_DISTANCE_VARIANCE.x)
		{										
			this.direction.x = -1;
		}
		
		if(		 this.isUpperShieldMaker
				 && this.bounds.getMaxY()
				 	  < Events.boss.bounds.getMinY()
				 	    - SHIELD_TARGET_DISTANCE
				 	    - TARGET_DISTANCE_VARIANCE.y
			     ||
			     !this.isUpperShieldMaker
			     && this.bounds.getMinY()
			     	  < Events.boss.bounds.getMaxY()
			     	  	+ SHIELD_TARGET_DISTANCE
			     	  	- TARGET_DISTANCE_VARIANCE.y)
		{
			this.direction.y = 1;
		}   
		else if( this.isUpperShieldMaker
				 && this.bounds.getMaxY() > Events.boss.bounds.getMinY() - SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y
				 ||
				 !this.isUpperShieldMaker
				 && this.bounds.getMinY() > Events.boss.bounds.getMaxY() + SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y)
		{
			this.direction.y = -1;
		}		
	}

	private boolean canStartShielding()
	{		
		return 	   this.shieldMakerTimer > 200
				&& !this.isRecoveringSpeed
				&& TARGET_DISTANCE_VARIANCE.x	
				     > Math.abs(Events.boss.bounds.getCenterX() 
						        -this.bounds.getX()) 				
			    &&  TARGET_DISTANCE_VARIANCE.y
					  > (this.isUpperShieldMaker
						 ? Math.abs(this.bounds.getMaxY() 
								    - Events.boss.bounds.getMinY() 
								    + SHIELD_TARGET_DISTANCE) 
						 : Math.abs(this.bounds.getMinY()
								    - Events.boss.bounds.getMaxY()
								    - SHIELD_TARGET_DISTANCE));
	}

	private void healerAction()
    {    	
		if(Events.boss.getHitPoints() < Events.boss.startingHitPoints)
		{						
			if(this.speedLevel.getX() != 0)
			{
				int stop = 0;
				if(this.bounds.getX() < Events.boss.bounds.getX() 
										+ 0.55f * Events.boss.bounds.getWidth())
				{
					this.direction.x = 1;					
				}
				else if(this.bounds.getX() > Events.boss.bounds.getX() 
											 + 0.65f * Events.boss.bounds.getWidth())
				{
					this.direction.x = -1;					
				}
				else{stop++;}
				
				if(		this.bounds.getY() < Events.boss.bounds.getY() 
											 + Events.boss.bounds.getHeight() 
											 - 1.25f * this.bounds.getHeight())
				{
					this.direction.y = 1;					
				}
				else if(this.bounds.getY() > Events.boss.bounds.getY() 
											 + Events.boss.bounds.getHeight() 
											 - 1.05f * this.bounds.getHeight())
				{
					this.direction.y = -1;					
				}
				else{stop++;}
			
				if(stop >= 2)
				{
					this.speedLevel.setLocation(ZERO_SPEED);
					this.direction.x = -1;
					this.canDodge = true;
				}				
			}
			else
			{
				Events.boss.healHitPoints();
			}						 
		}
		else
		{
			this.speedLevel.setLocation(this.targetSpeedLevel);
		}
    }
	
	private void healHitPoints()
	{
		int newHitPoints = Math.min(Events.boss.hitPoints + HEALED_HIT_POINTS,
									Events.boss.startingHitPoints);
		this.setHitPoints(newHitPoints);
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
		   && this.bounds.getMaxY() + 1.5 * this.speed.getY() > GROUND_Y)
		{
			this.setY(GROUND_Y - this.bounds.getHeight());
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
					this.bounds.getX() 
						+ this.direction.x * this.speed.getX() 
						- (Scenery.backgroundMoves ? BG_SPEED : 0),
					Math.max( this.model == BARRIER ? 0 : Integer.MIN_VALUE,
							this.type == EnemyType.ROCK ? this.bounds.getY() :
								Math.min( this.canBePositionedBelowGround()
											? Integer.MAX_VALUE
											: GROUND_Y - this.bounds.getHeight(),
										this.bounds.getY() 
											+ this.direction.y 
											* this.speed.getY())));
		}
	}

	private boolean canBePositionedBelowGround()
	{		
		return !(this.model == BARRIER
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
		if(this.stunningTimer != READY)
		{
			this.adjustSpeedTo(helicopter.missileDrive);
			if(this.stunningTimer == 1)
			{
				if(this.model == BARRIER){this.snooze(true);}
				else{this.isRecoveringSpeed = true;}
			}
		}
		if(this.model != BARRIER){this.evaluateSpeedBoost();}
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
			&& !(this.model == BARRIER && this.type == EnemyType.BIG_BARRIER))
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
			  || this.bounds.getMaxX() 
			  	 + 18 
			  	 + missileDrive/2f > Main.VIRTUAL_DIMENSION.width
			  	 								+ 2 * this.bounds.getWidth()/3  
			  || this.bounds.getMinX() 
			  	 - 18 
			  	 - missileDrive/2f < - 2 * this.bounds.getWidth()/3))
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
			if(    this.bounds.getMinY() > TURN_FRAME.getMinY()
			    && this.bounds.getMaxY() < bottomTurnLine)
			{
				this.speedLevel.setLocation(this.targetSpeedLevel);
				this.isSpeedBoosted = false;
			}						
		}
		else if(this.stoppingBarrier != null
				&&(     this.bounds.getMinY() < TURN_FRAME.getMinY() 
				    || (this.bounds.getMaxY() > bottomTurnLine)))
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
		return this.bounds.getMaxY() > yTurnLine
				   &&(   (this.direction.getX() ==  1 
				   			&& this.bounds.getCenterX() < this.stoppingBarrier.bounds.getCenterX())
					   ||(this.direction.getX() == -1 
					   		&& this.bounds.getCenterX() > this.stoppingBarrier.bounds.getCenterX()));
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
		   && this.stoppingBarrier.bounds.getCenterX()*this.direction.x
		   		             < this.bounds.getCenterX()*this.direction.x
		   && this.stoppingBarrier.speed.getX() > this.speed.getX())
		{
			this.speed.setLocation(	this.stoppingBarrier.isOnScreen()
									&& !this.isOnScreen()
			   							? 0 
			   							: this.stoppingBarrier.speed.getX(),
			   						this.speed.getY());
		}
		else if( this.stoppingBarrier.direction.y == this.direction.y
				 && this.stoppingBarrier.bounds.getCenterY()*this.direction.y
				 		           < this.bounds.getCenterY()*this.direction.y
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
		    && this.bounds.getMaxY() + this.speed.getY() >= this.yCrashPos)
		{
			this.handleCrashToTheGround(explosion, helicopter);
		}		
		this.calculateSpeedDead();
		this.move();
		if(this.bounds.getMaxX() < 0){this.isMarkedForRemoval = true;}
		this.setPaintBounds();
	}
	
	private void handleCrashToTheGround(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion,
										Helicopter helicopter)
	{
		this.hasCrashed = true;
		this.speedLevel.setLocation(ZERO_SPEED);
		this.setY(this.yCrashPos - this.bounds.getHeight());
		if(this.type.isServant()){this.isMarkedForRemoval = true;}
		Audio.play(this.type == EnemyType.KABOOM ? Audio.explosion4 : Audio.explosion3);
		Explosion.start(explosion, 
						helicopter, 
						this.bounds.getCenterX(),
						this.bounds.getCenterY(),
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
			
		if(	this.canExplode
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
							
			if(this.model == BARRIER)
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
				&& !this.canExplode
				&& !this.isInvincible()
				&& !(this.barrierTeleportTimer != DISABLED && this.barrierShootTimer == DISABLED)
				&& this.collisionAudioTimer == READY;
	}
	
	public float collisionDamage(Helicopter helicopter)
	{		
		return helicopter.getProtectionFactor()
				// TODO 0.65 und 1.0 in Konstanten auslagern
			   *helicopter.getBaseProtectionFactor(this.canExplode)
			   *(helicopter.isTakingKaboomDamageFrom(this)
			     ? helicopter.kaboomDamage()
			     : (this.canExplode && !this.isInvincible() && !this.isDestroyed)
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
		if(this.model == BARRIER)
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
		if(missile.typeOfExplosion == STUNNING
		   && this.isStunable
		   && this.nonStunableTimer == READY)
		{			
			this.stun(helicopter, missile, explosion);
		}
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
			     if(this.bounds.getMinX() > helicopter.getBounds().getMinX()){this.direction.x = -1;}
			else if(this.bounds.getMaxX() < helicopter.getBounds().getMaxX()){this.direction.x = 1;}				
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
			this.uncloak(this.model == BARRIER ? DISABLED : READY);
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
						this.bounds.getX() + ((explosionType != EMP && this.model != BARRIER)
							? (missileSpeed < 0 ? 2 : 1) * this.bounds.getWidth()/3
							: this.bounds.getWidth()/2), 
						this.bounds.getY() + this.bounds.getHeight()/2, 
						explosionType, extraDamage);
	}
	
	void destroy(Helicopter helicopter){destroy(helicopter, null, true);}
	void destroy(Helicopter helicopter,
				 EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp,
				 boolean wasDestroyedByPlayer)
	{
		if(wasDestroyedByPlayer)
		{
			if(this.model != BARRIER){helicopter.numberOfEnemiesKilled++;}
			if(this.isMiniBoss){helicopter.numberOfMiniBossKilled++;}
		}	
		else
		{
			if(this.canDropPowerUp()){this.dropRandomPowerUp(helicopter, powerUp);}
		}
		this.isDestroyed = true;
		if(this.cloakingTimer > 0){this.uncloak(DISABLED);}
		this.teleportTimer = DISABLED;
		this.primaryColor = Colorations.dimColor(this.primaryColor, Colorations.DESTRUCTION_DIM_FACTOR);
		this.secondaryColor = Colorations.dimColor(this.secondaryColor, Colorations.DESTRUCTION_DIM_FACTOR);
		
		this.repaint();
	
		if(helicopter.tractor == this)
		{
			helicopter.stopTractor();
		}		
		this.speedLevel.setLocation(0, 12);
		this.direction.y = 1;
		
		this.empSlowedTimer = READY;
		this.yCrashPos = (int)(this.bounds.getMaxY() >= GROUND_Y
									? this.bounds.getMaxY()
									: GROUND_Y 
									  + 1
									  + Math.random()
									    *(this.bounds.getHeight()/4));
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
			carrierDestroyedJustNow = this;}
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
		return this.model != BARRIER
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
			BasicEnemy.currentMiniBoss = null;
		}			
		else if(this.type == EnemyType.BOSS_2)
		{								
			boss.setLocation(this.bounds.getCenterX(), 
							 this.bounds.getCenterY());
			makeBossTwoServants = true;
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
		
			helicopter.destination.setLocation(helicopter.getBounds().getX()+40, 
											   520.0);	
			Events.determineHighscoreTimes(helicopter);
		}
		else if(this.type.isFinalBossServant())
		{
			Events.boss.operator.servants[this.id()] = null;
			Events.boss.operator.timeSinceDeath[this.id()] = 0;
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
			if(this.bounds.getMaxX() < 934){this.direction.x = 1;}
			this.dodgeTimer = 16;
		}															   
		
		if(this.bounds.getY() > 143){this.direction.y = -1;}
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
		if(Events.boss.operator.servants[this.shieldingBrotherId()] == null)
		{
			this.isUpperShieldMaker = Calculations.tossUp();
		}
		else
		{
			this.isUpperShieldMaker
				= !Events.boss.operator.servants[this.shieldingBrotherId()].isUpperShieldMaker;
		}		
	}

	private int shieldingBrotherId()
	{		
		return this.type == EnemyType.SMALL_SHIELD_MAKER
				 ? id(EnemyType.BIG_SHIELD_MAKER)
				 : id(EnemyType.SMALL_SHIELD_MAKER);
	}

	public void teleport()
	{
		Audio.play(Audio.teleport2);		
		this.setLocation(260.0 + Math.random()*(660.0 - this.bounds.getWidth()),
						   20.0 + Math.random()*(270.0 - this.bounds.getHeight()));
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
		Explosion.start(explosion, helicopter, (int)this.bounds.getCenterX(), (int)this.bounds.getCenterY(), STUNNING, false, this);
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
					 && this.bounds.getMinX() > Events.boss.bounds.getMinX() 
				     && this.bounds.getMaxX() < Events.boss.bounds.getMaxX())
				&& !( (     (helicopter.getBounds().getX() - this.bounds.getMaxX() > -500)
						 && (helicopter.getBounds().getX() - this.bounds.getX() 	  <  150))	
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
			if (e.model == BARRIER && e.isOnScreen())
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
	
	public Enemy getOperatorServant(int servantType)
	{
		return operator.servants[servantType];
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
	
	protected int calculateHitPoints()
	{
		return type.getHitPoints() + hitPointVariance();
	}
	
	protected int hitPointVariance()
	{
		return 0;
	}
}