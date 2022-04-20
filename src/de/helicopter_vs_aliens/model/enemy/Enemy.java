package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.EnemyPainter;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
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
import java.awt.Dimension;
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
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.enemy.BarrierPositionType.BOTTOM;
import static de.helicopter_vs_aliens.model.enemy.BarrierPositionType.LEFT;
import static de.helicopter_vs_aliens.model.enemy.BarrierPositionType.NONE;
import static de.helicopter_vs_aliens.model.enemy.BarrierPositionType.RIGHT;
import static de.helicopter_vs_aliens.model.enemy.BarrierPositionType.TOP;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BARRIER_0;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BARRIER_1;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BARRIER_2;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BARRIER_3;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BARRIER_4;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BARRIER_5;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BARRIER_6;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BARRIER_7;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BIG_SHIELD_MAKER;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BODYGUARD;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOLT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_1;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_2;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_2_SERVANT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_3;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_4;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_4_SERVANT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.FINAL_BOSS;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.HEALER;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.KABOOM;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.PROTECTOR;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.ROCK;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.SMALL_SHIELD_MAKER;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.TINY;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.EMP;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.JUMBO;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PHASE_SHIFT;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PLASMA;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.STUNNING;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PEGASUS;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.NICE_CATCH_TIME;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.TELEPORT_KILL_TIME;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.BUSTER;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.DISCHARGER;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.REPARATION;
import static de.helicopter_vs_aliens.model.scenery.SceneryObject.BG_SPEED;


public class Enemy extends RectangularGameEntity
{
	private static class FinalEnemyOperator
    {
		Enemy[] servants;
    	int [] timeSinceDeath;
    	
    	FinalEnemyOperator()
    	{
    		this.servants = new Enemy [NR_OF_BOSS_5_SERVANTS];
    		this.timeSinceDeath = new int [NR_OF_BOSS_5_SERVANTS];
    	}
    }
	
	// Konstanten
	public static final int
		SPEED_KILL_BONUS_TIME 	= 15, // Zeit [frames], innerhalb welcher für einen Kamaitachi-Extra-Bonus Gegner besiegt werden müssen, erhöht sich um diesen Wert
		CLOAKING_TIME = 135, // Zeit, die beim Tarn- und Enttarnungsvorgang vergeht
		CLOAKED_TIME = 135,     // Zeit, die ein Gegner getarnt bleibt
		SNOOZE_TIME = 100,    // Zeit, die vergeht, bis sich ein aktives Hindernis in Bewegung setzt
		DISABLED = -1; // TODO unnötig machen
	
	public static final Point2D
		ZERO_SPEED = new Point2D.Float(0, 0);
	
	private static final float
		RADAR_STRENGTH 				= 0.2f,		// Alpha-Wert: legt fest, wie stark  ein getarnter Gegner bei aktiviertem Radar noch zu sehen ist
		HEIGHT_FACTOR 				= 0.28f,	// legt das Verhältnis von Höhe und Länge für die meisten Gegner fest
		HEIGHT_FACTOR_SUPERSIZE 	= 0.65f,	// legt das Verhältnis von Höhe und Länge für besonders hohe Gegner fest
		ROCK_PROB					= 0.05f,
		KABOOM_PROB				    = 0.02f,	// Rate mit der Kaboom-Gegner erscheinen
		POWER_UP_PROB				= 0.02f,
		SPIN_SHOOTER_RATE 		   	= 0.55f,
		EXTRA_INACTIVE_TIME_FACTOR 	= 0.65f,
	
	// Multiplikatoren, welche den Grundschaden von Raketen unter bestimmten Voraussetzungen erhöhen
	RADIATION_DAMAGE_FACTOR = 1.5f,			// Phönix-Klasse, nach Erwerb von Nahkampf-Bestrahlung: Schaden im Verhältnis zum regulären Raketenschaden, den ein Gegner bei Kollisionen mit dem Helikopter erleidet
		TELEPORT_DAMAGE_FACTOR = 4f,			// Phönix-Klasse: wie RADIATION_DAMAGE_FACTOR, aber für Kollisionen unmittelbar nach einem Transportvorgang
		EMP_DAMAGE_FACTOR_BOSS = 1.5f,			// Pegasus-Klasse: Schaden einer EMP-Welle im Verhältnis zum normalen Raketenschaden gegenüber von Boss-Gegnern // 1.5
		EMP_DAMAGE_FACTOR_ORDINARY = 2.5f,		// Pegasus-Klasse: wie EMP_DAMAGE_FACTOR_BOSS, nur für Nicht-Boss-Gegner // 3
		STANDARD_MINI_BOSS_PROB = 0.05f,
		CHEAT_MINI_BOSS_PROB = 1.0f;
	
	private static final float[]
		RETURN_PROB	= { 0.013f,	 	// SMALL_SHIELD_MAKER
		0.013f,  	// BIG_SHIELD_MAKER
		0.007f,  	// BODYGUARD
		0.01f,  	// HEALER
		0.04f}; 	// PROTECTOR
	
	private static final int
		// Raum-Konstanten
		SAVE_ZONE_WIDTH = 116,
		APPEARANCE_DISTANCE = 10,
		SHIELD_TARGET_DISTANCE = 20,
		DISAPPEARANCE_DISTANCE = 100,
		BARRIER_DISTANCE = 100,
		ROCK_WIDTH = 300,
		KABOOM_WIDTH = 120,
		FINAL_BOSS_STARTING_POSITION_Y = 98,
		FINAL_BOSS_WIDTH = 450,
		PROTECTOR_WIDTH = 90,
		KABOOM_Y_TURN_LINE = GROUND_Y - (int) (HEIGHT_FACTOR * KABOOM_WIDTH),
	
		// Zeit-Konstanten
		ROCK_FREE_TIME = 250,    // Zeit die mind. vergeht, bis ein neuer Hindernis-Gegner erscheint
		EMP_SLOW_TIME = 175,    // Zeit, die von EMP getroffener Gegner verlangsamt bleibt // 113
		EMP_SLOW_TIME_BOSS = 110,
		INACTIVATION_TIME = 150,
		STUNNING_TIME_BASIS = 45,    // Basis-Wert zur Berechnung der Stun-Zeit nach Treffern von Stopp-Raketen
		BORROW_TIME = 65,
		MIN_TURN_TIME = 31,
		MIN_TURN_NOISELESS_TIME = 15,
		STATIC_CHARGE_TIME = 110,
		MAX_BARRIER_NUMBER = 3,
	
		// Level-Voraussetzungen
		MIN_BARRIER_LEVEL = 2,
		MIN_POWER_UP_LEVEL = 3,
		MIN_FUTURE_LEVEL = 8,
		MIN_KABOOM_LEVEL = 12,
		MIN_SPIN_SHOOTER_LEVEL = 23,
		MIN_ROCK_LEVEL = 27,
		MIN_BUSTER_LEVEL = 29,
	
		// für Boss-Gegner
		NR_OF_BOSS_5_SERVANTS = 5,
		BOSS_5_HEAL_RATE = 11,
		STANDARD_REWARD_FACTOR = 1,
		MINI_BOSS_REWARD_FACTOR = 4,
	
		// TODO die 4 austauschen / anders lösen
		PRE_READY = 1,
		READY = 0,
		ACTIVE_TIMER = 1;
	
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
	
	private static final Dimension
		FINAL_BOSS_DIMENSION = new Dimension(FINAL_BOSS_WIDTH, (int)HEIGHT_FACTOR * FINAL_BOSS_WIDTH );
	
	private static final Rectangle
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
	
	public static float
		miniBossProb = 0.05f;// bestimmt die Häufigkeit, mit der Mini-Bosse erscheinen
		
	public static Enemy
		currentMiniBoss,	// Referenz auf den aktuellen Boss-Gegner
		currentRock,
		lastCarrier;  		// Referenz auf den zuletzt zerstörten Carrier-Gegner
	
	public static Enemy[]
		livingBarrier = new Enemy [MAX_BARRIER_NUMBER];
	
	public static EnemyType
		bossSelection;		 	// bestimmt, welche Boss-Typ erstellt wird
	
	private static int 
		selection,			// bestimmt welche Typen von Gegnern zufällig erscheinen können	
		selectionBarrier, 	// bestimmt den Typ der Hindernis-Gegner
		rockTimer,			// reguliert das Erscheinen von "Rock"-Gegnern
		barrierTimer;		// reguliert das Erscheinen von Hindernis-Gegnern
		
	// für die Tarnung nötige Variablen
    public static final float[]
    	scales = { 1f, 1f, 1f, RADAR_STRENGTH },
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
			
	public static 
		Point2D boss = new Point2D.Float();	// Koordinaten vom aktuellen Boss; wichtig für Gegner-produzierende Boss-Gegner	
		
	/*
	 * 	Attribute der Enemy-Objekte	
	 */

	public int
		hitpoints,						// aktuelle HitPoints
		startingHitpoints,				// Anfangs-HitPoints (bei Erstellung des Gegners)
		invincibleTimer,				// reguliert die Zeit, die ein Gegner unverwundbar ist
		teleportTimer,					// Zeit [frames], bis der Gegner sich erneut teleportieren kann
		shield,							// nur für Boss 5 relevant; kann die Werte 0 (kein Schild), 1 oder 2 annehmen
		alpha,
		borrowTimer,
		untouchedCounter,
		stunningTimer,
		empSlowedTimer,			// reguliert die Länge der Verlangsamung nach EMP-Treffer (Pegasus-Klasse)
		collisionDamageTimer;			// Timer zur überwachung der Zeit zwischen zwei Helikopter-HP-Abzügen;
	
	public boolean
		isMiniBoss,					// = true: Gegner ist ein Mini-Boss
		isLasting,
		isTouchingHelicopter,
		hasUnresolvedIntersection;
		
	
	public EnemyType
		type;
	
	public BarrierPositionType
		touchedSite,
		lastTouchedSite;
	
	// Farben
    public Color 
    	farbe1,
    	farbe2; 

    public EnemyModelType
		model;				// legt das Aussehen (Model) des Gegners fest

    public Point 
    	direction = new Point();		// Flugrichtung
    	
	Enemy
		stoppingBarrier,		// Hindernis-Gegner, der diesen Gegner aufgehalten hat
		isPreviousStoppingBarrier;
		
	private int
		rewardModifier,			// für normale Gegner wird eine Zufallszahl zwischen -5 und 5 auf die Belohnung bei Abschuss addiert
		lifetime,				// Anzahl der Frames seit Erstellung des Gegners; und vergangene Zeit seit Erstellung, Zeit
		yCrashPos,				// Bestimmt wie tief ein Gegner nach Absturz im Boden versinken kann
		collisionAudioTimer,
		turnAudioTimer,
		explodingTimer,			// Timer zur überwachung der Zeit zwischen Abschuss und Absturz
		cloakingTimer,			// reguliert die Tarnung eines Gegners; = DISABLED: Gegner kann sich grundsätzlich nicht tarnen
		uncloakingSpeed,
		shieldMakerTimer,
		callBack,
		chaosTimer,
		speedup,
		batchWiseMove,
		shootTimer,
		spawningHornetTimer,
		turnTimer,
		dodgeTimer,			// Zeit [frames], bis ein Gegner erneut ausweichen kann
		snoozeTimer,
		staticChargeTimer,
		
		// nur für Hindernis-Gegner relevant
		rotorColor,
		barrierShootTimer,
		barrierTeleportTimer,
		shootPause,
		shootingRate,
		shotsPerCycle,
		shootingCycleLength,
		shotSpeed,
		shotRotationSpeed,
		
		// Regulation des Stun-Effektes nach Treffer durch Stopp-Rakete der Orochi-Klasse
		nonStunableTimer,
		totalStunningTime,
		knockBackDirection;
		
	private float
		deactivationProb,
		dimFactor;
    
    private boolean
		canExplode,			// = true: explodiert bei Kollisionen mit dem Helikopter
        canDodge,				// = true: Gegner kann Schüssen ausweichen
        canKamikaze,			// = true: Gegner geht auf Kollisionskurs, wenn die Distanz zum Helicopter klein ist
        canLearnKamikaze,		// = true: Gegner kann den Kamikaze-Modus einschalten, wenn der Helikopter zu nahe kommt
        canEarlyTurn,
        canMoveChaotic, 		// reguliert den zufälligen Richtungswechsel bei Chaos-Flug-Modus
        canSinusMove,			// Gegner fliegt in Kurven ähnlicher einer Sinus-Kurve
        canTurn,				// Gegner ändert bei Beschuss eventuell seine Flugrichtung in Richtung Helikopter
        canInstantTurn,		    // Gegner ändert bei Beschuss immer(!) seine Flugrichtung in Richtung Helikopter
        canFrontalSpeedup,	    // Gegner wird schneller, wenn Helikopter ihm zu Nahe kommt
        canLoop,				// = true: Gegner fliegt Loopings
        canChaosSpeedup,		// erhöht die Geschwindigkeit, wenn in Helicopter-Nähe
        isSpeedBoosted,
        isDestroyed,			// = true: Gegner wurde vernichtet
        hasHeightSet,			// = false --> height = height_factor * width; = true --> height wurde manuell festgelegt
        hasYPosSet,			    // = false --> y-Position wurde nicht vorab festgelegt und muss automatisch ermittelt werden
        hasCrashed, 			// = true: Gegner ist abgestürzt
        isEmpShocked,			// = true: Gegner steht unter EMP-Schock --> ist verlangsamt
        isMarkedForRemoval,	    // = true --> Gegner nicht mehr zu sehen; kann entsorgt werden
        isUpperShieldMaker,	    // bestimmt die Position der Schild-Aufspannenden Servants von Boss 5
        isShielding,			// = true: Gegner spannt gerade ein Schutzschild für Boss 5 auf (nur für Schild-Generatoren von Boss 5)
        isStunnable,			// = false für Boss 5; bestimmt, ob ein Gegner von Stopp-Raketen (Orochi-Klasse) "gestunt" werden kann
        isCarrier,				// = true
        isClockwiseBarrier,	    // = true: der Rotor des Hindernisses dreht im Uhrzeigersinn
        isRecoveringSpeed;
  
	private AbilityStatusType
		tractor;				// = DISABLED (Gegner ohne Traktor); = READY (Traktor nicht aktiv); = 1 (Traktor aktiv)
	
	private EnemyMissileType
		shotType;
	
	
	private final GraphicsAdapter []
		graphicsAdapters = new GraphicsAdapter[2];
	
	private FinalEnemyOperator
		operator;
	
	private final BufferedImage []
		image = new BufferedImage[4];
		
	private final Point2D
		targetSpeedLevel = new Point2D.Float(),		// Anfangsgeschwindigkeit
		speedLevel = new Point2D.Float(),			// auf Basis dieses Objektes wird die tatsächliche Geschwindigkeit berechnet
		speed = new Point2D.Float(),				// tatsächliche Geschwindigkeit
		shootingDirection = new Point2D.Float();   	// Schussrichtugn von schießenden Barrier-Gegnern
	
	
	public static void changeMiniBossProb()
	{
		miniBossProb = miniBossProb == STANDARD_MINI_BOSS_PROB ? CHEAT_MINI_BOSS_PROB: STANDARD_MINI_BOSS_PROB;
	}
	
	public void dimmedRepaint()
	{
		farbe1 = Colorations.dimColor(farbe1, Colorations.BARRIER_NIGHT_DIM_FACTOR);
		farbe2 = Colorations.dimColor(farbe2, Colorations.BARRIER_NIGHT_DIM_FACTOR);
		repaint();
	}
	
	public boolean hasGlowingEyes()
	{
		return  !this.isDestroyed
				&& (this.isBoss()
					|| this.type == KABOOM
					|| (this.model == BARRIER
						&& this.snoozeTimer <= SNOOZE_TIME + 75));
	}

	private void clearImage()
    {
    	for(int i = 0; i < this.image.length; i++)
    	{
    		this.image[i] = null; 
    		if(i < 2){this.graphicsAdapters[i] = null;}
    	}
    }
    
    public void repaint()
	{
		for(int j = 0; j < 2; j++)
		{		
			if(this.model != BARRIER)
			{				
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
		bossSelection = null;
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
			bossSelection = null;
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
			bossSelection = BOSS_1;
			selection = 0;
			helicopter.powerUpDecay();
		}	  
		else if(level == 11)
		{
			maxNr = 3;
			bossSelection = null;
			selection = 75;
			maxBarrierNr = 1;
			selectionBarrier = 2;
			
			if(	 helicopter.isCountingAsFairPlayedHelicopter()
				 && !Events.recordTimeManager.hasAnyBossBeenKilledBefore())
			{
				Window.unlock(HELIOS);
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
			bossSelection = null;
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
			bossSelection = BOSS_2;
			selection = 0;
			helicopter.powerUpDecay();
			if(helicopter.isCountingAsFairPlayedHelicopter() && !helicopter.getType().hasReachedLevel20())
			{
				Events.helicoptersThatReachedLevel20.add(helicopter.getType());
				helicopter.updateUnlockedHelicopters();
			}
		}
		else if(level == 21)
		{
			maxNr = 3;
			bossSelection = null;
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
			bossSelection = null;
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
			bossSelection = BOSS_3;
			selection = 0;	
			helicopter.powerUpDecay();
		}
		else if(level == 31)
		{
			maxNr = 3;
			bossSelection = null;
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
			bossSelection = null;
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
			bossSelection = BOSS_4;
			selection = 0;
			helicopter.powerUpDecay();
		}			  
		else if(level == 41)
		{
			maxNr = 3;
			bossSelection = null;
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
			bossSelection = null;
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
			bossSelection = FINAL_BOSS;
			selection = 0;
			helicopter.powerUpDecay();
		}
	}
	
	/** Methoden zur Erstellung von Gegnern
	 */	
	
	// TODO die Begrenzung nach Anzahl funktioniert nicht mehr
	public static void generateNewEnemies(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy, Helicopter helicopter)
	{
		Events.lastCreationTimer++;
		if(lastCarrier != null){
			createCarrierServants(helicopter, enemy);}
		else if(wasEnemyCreationPaused){
			verifyCreationStop(enemy, helicopter);}
		if(bossServantCreationApproved()){
			createBossServant(helicopter, enemy);}
		else if(enemyCreationApproved(enemy))
		{
			creation(helicopter, enemy);
		}
	}
	
	private static void createCarrierServants(Helicopter helicopter,
											  EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
	{
		for(int m = 0; 
				m < (lastCarrier.isMiniBoss
						? 5 + Calculations.random(3)
						: 2 + Calculations.random(2));
				m++)
			{
				creation(helicopter, enemy);
			}			
			lastCarrier = null;
	}

	private static void verifyCreationStop(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy,
										   Helicopter helicopter)
	{
		if(	enemy.get(ACTIVE).isEmpty()
			&& lastCarrier == null
			&& !(helicopter.hasPowerUpsDisallowedAtBossLevel()
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
		
	private static boolean bossServantCreationApproved()
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
					bossSelection = type;
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
		bossSelection = BOSS_2_SERVANT;
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
			bossSelection = type;
			creation(helicopter, enemy);
		});
    }	

    private static boolean enemyCreationApproved(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemies)
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
	
	private static void creation(Helicopter helicopter,
								 EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
	{
		Iterator<Enemy> i = enemy.get(INACTIVE).iterator();
		Enemy e;
		if (i.hasNext())
		{
			e = i.next();
			i.remove();
		}
		else{e = EnemyFactory.createEnemy();}
		enemy.get(ACTIVE).add(e);
		Events.lastCreationTimer = 0;
		helicopter.numberOfEnemiesSeen++;
		e.create(helicopter, enemy.get(ACTIVE).size());
	}
    	
	private void create(Helicopter helicopter, int nrOfEnemies)
	{			
		this.reset();		
		if(lastCarrier != null){this.createScamperingVessel(true);}
		else if(barrierCreationApproved(nrOfEnemies)){this.createBarrier(helicopter);}
		else if(rockCreationApproved()){this.createRock(helicopter);}
		else if(kaboomCreationApproved()){this.createKaboom(helicopter);}
		else if(bossSelection == null){this.createStandardEnemy();}
		else{this.createBoss(helicopter);}
		
		if(this.model != BARRIER)
		{
			this.farbe1 = Colorations.dimColor(this.farbe1, 1.3f);
			this.farbe2 = Colorations.dimColor(this.farbe1, this.dimFactor);
		}		
		if(this.canBecomeMiniBoss()){this.turnIntoMiniBoss(helicopter);}
		this.rewardModifier = this.isBoss() ? 0 : 5 - Calculations.random(11);
		this.startingHitpoints = this.hitpoints;
		
		// Festlegen der Höhe und der y-Position des Gegners
		if(!this.hasHeightSet){this.setHeight();}
		if(!this.hasYPosSet){this.setInitialY();}
				
		this.initializeShootDirection();
		this.speedLevel.setLocation(this.targetSpeedLevel);
		this.setPaintBounds((int)this.bounds.getWidth(),
							  (int)this.bounds.getHeight());
		this.assignImage(helicopter);
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
			if(this.cloakingTimer != DISABLED && helicopter.getType() == OROCHI)
			{
				BufferedImage 
					 tempImage = new BufferedImage((int)(1.028f * this.paintBounds.width),
							 						(int)(1.250f * this.paintBounds.height),
							 						BufferedImage.TYPE_INT_ARGB);
				
				this.image[2+i] = new BufferedImage((int)(1.028f * this.paintBounds.width),
													(int)(1.250f * this.paintBounds.height),
													BufferedImage.TYPE_INT_ARGB);
				
				enemyPainter.paintImage(getGraphicAdapter(tempImage), this,1-2*i, Color.red, true);
				(getGraphicAdapter(this.image[2+i])).drawImage(tempImage, ROP_CLOAKED, 0, 0);
			}
		}		
	}

	private void reset()
	{
		this.lifetime = 0;
		this.type = TINY;
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
		this.isStunnable = true;
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
		this.borrowTimer = DISABLED;
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

	private static boolean barrierCreationApproved(int numberOfEnemies)
	{		
		return Events.level >= MIN_BARRIER_LEVEL 
				&& !Events.isBossLevel()
				&& barrierTimer == 0
				&& (Calculations.tossUp(0.35f)
					|| (numberOfEnemies - currentNumberOfBarriers >= maxNr))
				&& currentNumberOfBarriers < maxBarrierNr;
	}
	
	private void createBarrier(Helicopter helicopter)
	{
        this.model = BARRIER;
		this.assignRandomBarrierType();
		
		helicopter.numberOfEnemiesSeen--;
		this.hitpoints = Integer.MAX_VALUE;
		this.rotorColor = 1;
		this.isClockwiseBarrier = Calculations.tossUp();
				
		if(this.type == BARRIER_0 || this.type == BARRIER_1)
		{
			this.farbe1 = Colorations.bleach(Color.green, 0.6f);
			this.isLasting = true;
			
			// Level 2
			if(this.type == BARRIER_0){this.setVarWidth(65);}
			
			// Level 6
			else if(this.type == BARRIER_1)
			{
				this.setVarWidth(150);
				this.setInitialY(GROUND_Y - this.bounds.getWidth());
			}
		}	
		// Level 12
		else if(this.type == BARRIER_2)
		{
			this.farbe1 = Colorations.bleach(Color.yellow, 0.6f);
			this.targetSpeedLevel.setLocation(0, 1 + 2*Math.random());
			this.setVarWidth(65);
			
			this.rotorColor = 2;
			this.staticChargeTimer = READY;
			this.isLasting = true;
		}
		// Level 15
		else if(this.type == BARRIER_3)
		{
			this.farbe1 = Colorations.bleach(new Color(255, 192, 0), 0.0f);
			this.targetSpeedLevel.setLocation(0.5 + 2*Math.random(), 0);
			this.setVarWidth(105);
			if(this.targetSpeedLevel.getX() >= 5){this.direction.x = 1;}

			this.setLocation(this.targetSpeedLevel.getX() >= 5
									? -this.bounds.getWidth()-APPEARANCE_DISTANCE
									: this.bounds.getX(),
							  GROUND_Y - this.bounds.getWidth() - (5 + Calculations.random(11)));
			this.hasYPosSet = true;
		}
		// Level 18
		else if(this.type == BARRIER_4)
		{
			this.setVarWidth(85);
			this.hasYPosSet = true;
			this.barrierShootTimer = READY;
			this.setBarrierShootingProperties();
			this.shotRotationSpeed
				= Calculations.tossUp(SPIN_SHOOTER_RATE) && Events.level >= MIN_SPIN_SHOOTER_LEVEL
					? Calculations.randomDirection()*(this.shootingRate /3 + Calculations.random(10))
					: 0;	
			
			this.isLasting = true;
		}
		// Level 32
		else if(this.type == BARRIER_5)
		{
			this.setVarWidth(80);
			this.setInitialY(GROUND_Y - this.bounds.getWidth()/8);
			
			this.borrowTimer = READY;
			this.setBarrierShootingProperties();
									
			this.isLasting = true;
		}
		// Level 42
		else if(this.type == BARRIER_6)
		{
			this.farbe1 = Colorations.bleach(Color.green, 0.6f);
			this.setVarWidth(80);
			
			this.isLasting = true;
		}
		// Level 44
		else if(this.type == BARRIER_7)
		{
			this.farbe1 = Colorations.bleach(Colorations.cloaked, 0.6f);
			this.setVarWidth(100);
						
			this.barrierTeleportTimer = READY;
			this.setBarrierShootingProperties();
			this.startBarrierUncloaking(helicopter);
						
			this.hasYPosSet = true;
			this.callBack = 1 + Calculations.random(4);
		}
		
		this.farbe2 = Colorations.dimColor(this.farbe1, 0.75f);
		this.deactivationProb = 1.0f / this.type.getStrength();
				
		if(Events.timeOfDay == NIGHT)
		{
			this.farbe1 = Colorations.dimColor(this.farbe1, Colorations.BARRIER_NIGHT_DIM_FACTOR);
			this.farbe2 = Colorations.dimColor(this.farbe2, Colorations.BARRIER_NIGHT_DIM_FACTOR);
		}		
		barrierTimer = (int)((helicopter.getBounds().getWidth() + this.bounds.getWidth())/2);
	}
    
    private void assignRandomBarrierType()
    {
        int randomBarrierSelectionModifier = isBarrierFromFutureCreationApproved()
            ? Calculations.random(3)
            : 0;
        int selectedBarrierIndex = Calculations.random(Math.min(selectionBarrier + randomBarrierSelectionModifier, EnemyType.getBarrierTypes().size()));
        this.type = (EnemyType) EnemyType.getBarrierTypes().toArray()[selectedBarrierIndex];
    }
    
    private boolean isBarrierFromFutureCreationApproved()
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
	
	private void createRock(Helicopter helicopter)
	{
		currentRock = this;
		this.type = ROCK;
		this.model = CARGO;	
		helicopter.numberOfEnemiesSeen--;
		this.farbe1 = new Color((180 + Calculations.random(30)),
								(120 + Calculations.random(30)),
								(  0 + Calculations.random(15)));
		this.hitpoints = 1;
		this.invincibleTimer = Integer.MAX_VALUE;
			
		this.bounds.setRect(this.bounds.getX(), 
							GROUND_Y - ROCK_WIDTH * (HEIGHT_FACTOR_SUPERSIZE - 0.05f), // 0.05
							ROCK_WIDTH, 
							ROCK_WIDTH * HEIGHT_FACTOR_SUPERSIZE);
		this.hasHeightSet = true;
		this.hasYPosSet = true;
		this.isLasting = true;
	}
	
	private static boolean kaboomCreationApproved()
	{		
		return Events.level >= MIN_KABOOM_LEVEL 
				&& !Events.isBossLevel()
				&& Calculations.tossUp(KABOOM_PROB);
	}
	
	private void createKaboom(Helicopter helicopter)
	{
		this.type = KABOOM;
		this.farbe1 = Color.white;
		this.hitpoints = Integer.MAX_VALUE;	
		this.setVarWidth(KABOOM_WIDTH);
		helicopter.numberOfEnemiesSeen--;
		this.targetSpeedLevel.setLocation(0.5 + 0.5*Math.random(), 0); //d
		this.canExplode = true;
		this.setInitialY(GROUND_Y - 2*this.bounds.getWidth()*HEIGHT_FACTOR);
	}
	
	private void createStandardEnemy()
	{
		// TODO Switch sollte entfallen,
		this.type = ENEMY_SELECTOR.getType(Calculations.random(selection));
		//this.type = CARRIER;

		switch(this.type)
		{
			// Level 1
			case TINY:
				this.farbe1 = new Color((180 + Calculations.random(30)),
										(120 + Calculations.random(30)),
										(  0 + Calculations.random(15)));
				this.hitpoints = 2;
				this.setVarWidth(110);
				this.targetSpeedLevel.setLocation(0.5 + Math.random(), //d
						0.5 * Math.random());	//d
				this.canExplode = true;
				this.dimFactor = 1.2f;
				
				break;

			// Level 3
			case SMALL:
				this.farbe1 = new Color((140 + Calculations.random(25)),
										( 65 + Calculations.random(35)),
										(  0 + Calculations.random(25)));
				this.hitpoints = 3 + Calculations.random(3);
				this.setVarWidth(125);
				this.targetSpeedLevel.setLocation(1 + 1.5*Math.random(), //d
						0.5*Math.random());	//d
				this.canExplode = true;
				
				break;

			// level 5
			case RUNABOUT:
				this.farbe1 = new Color((100 + Calculations.random(30)),
						(100 + Calculations.random(30)),
						(40 + Calculations.random(25)));
				this.hitpoints = 2 + Calculations.random(2);
				this.setVarWidth(100);
				this.targetSpeedLevel.setLocation(2 + 2*Math.random(), //d
						2.5 + 1.5*Math.random());		//d
				this.canExplode = true;
				
				break;

			// Level 7
			case FREIGHTER:
				this.model = CARGO;
				this.farbe1 = new Color((100 + Calculations.random(30)),
						(50 + Calculations.random(30)),
						(45 + Calculations.random(20)));
				this.setHitpoints(25);
				this.setVarWidth(145);
				this.targetSpeedLevel.setLocation(0.5 + Math.random(), //d
						0.5*Math.random());	//d
				this.canEarlyTurn = true;
				this.canTurn = true;
				
				break;

			// Level 11
			case BATCHWISE:
				this.farbe1 = new Color((135 + Calculations.random(30)),
						(80+ Calculations.random(20)),
						(85 + Calculations.random(30)));
				this.setHitpoints(16);
				this.setVarWidth(130);
				this.targetSpeedLevel.setLocation(7 + 4*Math.random(), //d
						1 + 0.5*Math.random()); //d
				this.batchWiseMove = 1;
				
				break;

			// Level 13
			case SINUS:
				this.farbe1 = new Color((185 + Calculations.random(40)),
						( 70 + Calculations.random(30)),
						(135 + Calculations.random(40)));
				this.setHitpoints(6);
				this.setVarWidth(110);
				this.targetSpeedLevel.setLocation(2.5 + 2.5*Math.random(), 11); //d

				this.setInitialY(TURN_FRAME.getCenterY());
				this.canSinusMove = true;
				this.canExplode = true;
				
				break;

			// Level 16
			case DODGER:
				this.farbe1 = new Color((85 + Calculations.random(20)),
						(35 + Calculations.random(30)),
						(95 + Calculations.random(30)));
				this.setHitpoints(24);
				this.setVarWidth(170);
				this.targetSpeedLevel.setLocation(1.5 + 1.5*Math.random(), //d
						0.5*Math.random());	//d
				this.canDodge = true;
				
				break;

			// Level 21
			case CHAOS:
				this.farbe1 = new Color((150 + Calculations.random(20)),
						(130 + Calculations.random(25)),
						( 75 + Calculations.random(30)));
				this.setHitpoints(22);
				this.setVarWidth(125);
				this.targetSpeedLevel.setLocation( 3.5 + 1.5*Math.random(), //d
						6.5 + 2*Math.random());	//d
				this.canMoveChaotic = true;
				this.canExplode = true;
				
				break;

			// Level 24
			case CALLBACK:
				this.farbe1 = new Color((70 + Calculations.random(40)),
						(130 + Calculations.random(50)),
						(30 + Calculations.random(45)));
				this.setHitpoints(30);
				this.setVarWidth(95);
				this.targetSpeedLevel.setLocation( 5.5 + 2.5*Math.random(), //d
						5 + 2*Math.random());		//d
				this.canExplode = true;
				this.callBack = 1;
				
				break;

			// Level 26
			case SHOOTER:
				this.model = CARGO;

				this.farbe1 = new Color(80 + Calculations.random(25),
						80 + Calculations.random(25),
						80 + Calculations.random(25));
				this.setHitpoints(60);
				this.setVarWidth(80);
				this.targetSpeedLevel.setLocation( 0.5 + Math.random(), //d
						0.5 * Math.random());	//d
				this.canDodge = true;
				this.shootTimer = 0;
				this.shootingRate = 35;
				
				break;

			// Level 31
			case CLOAK:
				this.model = CARGO;

				this.farbe1 = Colorations.cloaked;
				this.setHitpoints(100);
				this.setVarWidth(85);
				this.targetSpeedLevel.setLocation( 0.5 + Math.random(), //d
						1 + 0.5*Math.random());	//d
				this.canLearnKamikaze = true;
				this.canInstantTurn = true;
				this.cloakingTimer = CLOAKING_TIME + CLOAKED_TIME;
				this.uncloakingSpeed = 2;
				this.canEarlyTurn = true;
				this.canExplode = true;
				
				break;

			// Level 35
			case BOLT:
				this.createScamperingVessel(lastCarrier != null);
				break;

			case CARRIER:
				this.model = CARGO;

				this.farbe1 = new Color(70 + Calculations.random(15),
						60 + Calculations.random(10),
						45 + Calculations.random(10)); // new Color(25 + MyMath.random(35), 70 + MyMath.random(45), 25 + MyMath.random(35));
				this.setHitpoints(450);
				this.setVarWidth(165);
				this.targetSpeedLevel.setLocation( 0.5 + Math.random(), //d
						0.5 * Math.random());	//d
				this.canEarlyTurn = true;
				this.isCarrier = true;
				this.canTurn = true;
				
				break;

			// Level 37
			case YELLOW:
				this.farbe1 = new Color((180 + Calculations.random(50)),
						(230 + Calculations.random(20)),
						(20 + Calculations.random(60)));
				this.setHitpoints(140);
				this.setVarWidth(115);
				this.targetSpeedLevel.setLocation( 4 + 2.5 * Math.random(), //d
						0.5 + Math.random());		//d
				this.canExplode = true;
				this.canChaosSpeedup = true;
				this.canDodge = true;
				
				break;

			// Level 41
			case AMBUSH:
				this.farbe1 = new Color( 30 + Calculations.random(40),
						60 + Calculations.random(40),
						120 + Calculations.random(40));
				this.setHitpoints(150);
				this.setVarWidth(95);
				this.targetSpeedLevel.setLocation( 1 + 1.5*Math.random(), 0); //d

				this.canExplode = true;
				this.speedup = READY;
				
				break;

			 // Level 43
			case LOOPING:
				this.farbe1 = Colorations.cloaked;
				this.setHitpoints(330);
				this.setVarWidth(105);
				this.targetSpeedLevel.setLocation(9, 11);	//d

				this.direction.y = -1;
				this.setInitialY(TURN_FRAME.getCenterY());
				this.cloakingTimer = 0;
				this.canLoop = true;
				
				break;

			// Level 45
			case CAPTURER:
				this.farbe1 = new Color(  5 + Calculations.random(55),
						105 + Calculations.random(40),
						90 + Calculations.random(30));
				this.setHitpoints(520);
				this.setVarWidth(115);
				this.targetSpeedLevel.setLocation( 2.5 + 2*Math.random(), //d
						4.5 + 1.5*Math.random());//d
				this.tractor = AbilityStatusType.READY;
				this.canExplode = true;
				
				break;

			// Level 46
			case TELEPORTER:
				this.model = CARGO;

				this.farbe1 = new Color(190 + Calculations.random(40),
						10 + Calculations.random(60),
						15 + Calculations.random(60));
				this.setHitpoints(500);
				this.setVarWidth(130);
				this.targetSpeedLevel.setLocation( 1 + Math.random(), //d
						0.5*Math.random());//d
				this.teleportTimer = READY;
				this.canKamikaze = true;
				
				break;
		}
	}
	
	private void createScamperingVessel(boolean explosionCreation)
	{
		if(explosionCreation){this.type = BOLT;}
		
		this.farbe1 = new Color(75 + Calculations.random(30),
								75 + Calculations.random(30),
								75 + Calculations.random(30) );
		this.setHitpoints(26);
		this.setVarWidth(70);
		
		if(explosionCreation)
		{
			this.setLocation(lastCarrier.bounds.getCenterX(),
							  lastCarrier.bounds.getCenterY());
			this.hasYPosSet = true;
		}
		this.canExplode = true;
		if(explosionCreation)
		{
			this.targetSpeedLevel.setLocation( 10 + 7.5*Math.random(), //d
													0.5 + 3*Math.random());			//d	
			this.callBack = 1 + Calculations.random(3);
			this.direction.x = Calculations.randomDirection();
			this.invincibleTimer = 67;
		}
		else 
		{
			this.targetSpeedLevel.setLocation( 12 + 3.5*Math.random(), //d
													0.5 + 3*Math.random());		//d			
			if(Calculations.tossUp()){this.callBack = 1;}
		}
	}
	
	private void createBoss(Helicopter helicopter)
	{
		this.type = bossSelection;
		
		// Level 10	
		if( this.type == BOSS_1)
		{
			this.farbe1 = new Color(115, 70, 100);
			this.hitpoints = 225;			
			this.setWidth(275);
			this.targetSpeedLevel.setLocation(2, 0.5); //d
			
			this.canKamikaze = true;
			
			Events.boss = this;
		}		
		// Level 20
		else if( this.type == BOSS_2)
		{						
			this.model = CARGO;			
			this.farbe1 = new Color(85, 85, 85);
			this.hitpoints = 500;
			this.setWidth(250);
			this.targetSpeedLevel.setLocation(7, 8); //d
		
			this.canMoveChaotic = true;
			this.shootTimer = 0;
			this.shootingRate = 5;
			this.shotSpeed = 3;
			this.canInstantTurn = true;
			
			Events.boss = this;
		}		
		else if( this.type == BOSS_2_SERVANT)
		{	
			this.bounds.setRect(boss.getX(),
								boss.getY(),
								65,
								this.bounds.getHeight());
			this.hasYPosSet = true;
			this.farbe1 = new Color(80 + Calculations.random(25), 80 + Calculations.random(25), 80 + Calculations.random(25));
			this.hitpoints = 15;					
			this.targetSpeedLevel.setLocation(3 + 10.5*Math.random(), //d
												  3 + 10.5*Math.random()); //d
		
			this.direction.x = Calculations.randomDirection();
			this.invincibleTimer = 67;
		}
		// Level 30
		else if( this.type == BOSS_3)
		{			
			this.setWidth(250);
			this.farbe1 = Colorations.cloaked;
			this.hitpoints = 1750;
			this.targetSpeedLevel.setLocation(5, 4); //d

			this.canMoveChaotic = true;
			this.canKamikaze = true;
			this.cloakingTimer = READY;
			this.canDodge = true;
			this.shootTimer = 0;
			this.shootingRate = 10;
			this.shotSpeed = 10;
			this.canInstantTurn = true;
			
			Events.boss = this;
		}
		// Level 40
		else if(this.type == BOSS_4)
		{						
			this.setWidth(250);
			this.farbe1 = Color.red;
			this.hitpoints = 10000;	
			this.targetSpeedLevel.setLocation(10, 10); //d

			this.spawningHornetTimer = 30;
			bossSelection = BOSS_4_SERVANT;
			maxNr = 15;
			this.canTurn = true;
			
			Events.boss = this;
		}		
		else if(this.type == BOSS_4_SERVANT)
		{	
			this.bounds.setRect(boss.getX(),
								boss.getY(),
								85 + Calculations.random(15),
							    this.bounds.getHeight());
			this.hasYPosSet = true;
			this.farbe1 = new Color(80 + Calculations.random(20), 80 + Calculations.random(20), 80 + Calculations.random(20));
			this.hitpoints = 100 + Calculations.random(50);
			this.targetSpeedLevel.setLocation(6 + 2.5*Math.random(), //d
												  6 + 2.5*Math.random()); //d
			this.direction.x = Calculations.randomDirection();
			this.canExplode = true;
		}	
		// Level 50
		else if(this.type == FINAL_BOSS)
		{
			this.bounds.setRect(this.bounds.getX(),
								FINAL_BOSS_STARTING_POSITION_Y,
								FINAL_BOSS_DIMENSION.width,
								FINAL_BOSS_DIMENSION.height);
			this.hasYPosSet = true;
			this.hasHeightSet = true;
			
			this.farbe1 = Colorations.brown;
			this.hitpoints = 25000;	
			this.targetSpeedLevel.setLocation(23.5, 0); //d

			maxNr = 5;
			this.operator = new FinalEnemyOperator();
			this.isStunnable = false;
			this.dimFactor = 1.3f;
			
			Events.boss = this;
		}		
		else if(this.type.isFinalBossServant())
		{
			Events.boss.operator.servants[this.id()] = this;
			this.hasYPosSet = true;
			
			if(this.type.isShieldMaker())
			{			
				this.bounds.setRect(boss.getX(),
									boss.getY(),
									this.type == SMALL_SHIELD_MAKER ? 125 : 145,
								    this.bounds.getHeight());			
				this.direction.x = Calculations.randomDirection();
				
				this.shieldMakerTimer = READY;
				this.setShieldingPosition();
								
				if(this.type == SMALL_SHIELD_MAKER)
				{				
					this.targetSpeedLevel.setLocation(7, 6.5); //d
					this.farbe1 = new Color(25, 125, 105);				
					this.hitpoints = 3000;
				}			
				else
				{				
					this.targetSpeedLevel.setLocation(6.5, 7); //d
					this.farbe1 = new Color(105, 135, 65);				
					this.hitpoints = 4250;
					
					this.shootTimer = 0;
					this.shootingRate = 25;
					this.shotSpeed = 1;
				}
			}			  
			else if( this.type == BODYGUARD)
			{			
				this.bounds.setRect(boss.getX(),
									boss.getY(),
									225,
								    this.bounds.getHeight());					
				this.farbe1 = Colorations.cloaked;
				this.hitpoints = 7500;					
				this.targetSpeedLevel.setLocation(1, 2); //d

				this.cloakingTimer = 0;
				this.canInstantTurn = true;
				
				Events.boss.operator.servants[this.id()] = this;
			}		
			else if(this.type == HEALER)
			{				
				this.model = CARGO;
				
				this.bounds.setRect(boss.getX(),
									boss.getY(),
									115,
								    this.bounds.getHeight());				
				this.farbe1 = Color.white;
				this.hitpoints = 3500;
				this.targetSpeedLevel.setLocation(2.5, 3); //d
				
				this.canDodge = true;
				
				Events.boss.operator.servants[this.id()] = this;
			}
			else if(this.type == PROTECTOR)
			{
				this.model = BARRIER;
				
				this.bounds.setRect(boss.getX() + 200, 
									GROUND_Y, 
									PROTECTOR_WIDTH, 
									this.bounds.getHeight());
				
				helicopter.numberOfEnemiesSeen--;
				this.hitpoints = Integer.MAX_VALUE;
				this.isClockwiseBarrier = Calculations.tossUp();
				this.farbe1 = Colorations.bleach(new Color(170, 0, 255), 0.6f);
				this.targetSpeedLevel.setLocation(ZERO_SPEED);
				
				this.deactivationProb = 0.04f;
				this.borrowTimer = READY;
				this.shootingRate = 25;
				this.shotsPerCycle = 5;
				this.shootingCycleLength = this.shootPause
											 + this.shootingRate
											   * this.shotsPerCycle;
				this.shotSpeed = 10;
				this.shotType = BUSTER;
				this.isStunnable = false;
				this.farbe2 = Colorations.dimColor(this.farbe1, 0.75f);
				if(Events.timeOfDay == NIGHT)
				{
					this.farbe1 = Colorations.dimColor(this.farbe1, Colorations.BARRIER_NIGHT_DIM_FACTOR);
					this.farbe2 = Colorations.dimColor(this.farbe2, Colorations.BARRIER_NIGHT_DIM_FACTOR);
				}
				Events.boss.operator.servants[this.id()] = this;
			}			
		}	
	}

	private boolean canBecomeMiniBoss()
	{		
		return 	currentMiniBoss == null
				&& Events.level > 4 
				&& this.model != BARRIER
				&& !(this.type == ROCK)
				&& !(this.type == KABOOM)
				&& Calculations.tossUp(miniBossProb)
				&& this.type.isSuitableMiniBoss();
	}

	private void turnIntoMiniBoss(Helicopter helicopter)
	{
		helicopter.numberOfMiniBossSeen++;
		currentMiniBoss = this;
		this.hitpoints = 1+5*this.hitpoints;		
		this.bounds.setRect(this.bounds.getX(),
							this.bounds.getY(), 
							1.44 * this.bounds.getWidth(), 
							1.44 * this.bounds.getHeight());		
		this.isMiniBoss = true;
		this.canExplode = false;
		this.callBack += 2;
		this.canTurn = true;
		if(  (this.type.isCloakableAsMiniBoss() && !this.canLearnKamikaze && Calculations.tossUp(0.2f)) ||
		      this.shootTimer == 0 )
		{
			this.cloakingTimer = 0;
		}		
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
	
	private void setBarrierShootingProperties()
	{
		if(this.barrierTeleportTimer != DISABLED || this.borrowTimer != DISABLED)
		{
			this.shootingRate = 35 + Calculations.random(15);
		}
		else
		{
			this.shootingRate = 25 + Calculations.random(25);
		}
			
		if(this.barrierTeleportTimer == DISABLED){this.shootPause = 2 * this.shootingRate + 20 + Calculations.random(40);}
		this.shotsPerCycle = 2 + Calculations.random(9);
		this.shootingCycleLength = this.shootPause + this.shootingRate * this.shotsPerCycle;
		this.shotSpeed = 5 + Calculations.random(6);
		if(this.barrierTeleportTimer != DISABLED || (Calculations.tossUp(0.35f) && Events.level >= MIN_BUSTER_LEVEL))
		{
			if(this.barrierTeleportTimer == DISABLED){this.farbe1 = Colorations.bleach(new Color(170, 0, 255), 0.6f);}
			this.shotType = BUSTER;
		}
		else
		{
			this.farbe1 = Colorations.bleach(Color.red, 0.6f);
			this.shotType = DISCHARGER;
		}
	}

	private void setHitpoints(int hitpoints)
	{
		this.hitpoints = hitpoints + Calculations.random(hitpoints/2);
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
	
	private void setLocation(double x, double y)
	{
		this.bounds.setRect(x, 
							y, 
							this.bounds.getWidth(), 
							this.bounds.getHeight());
	}
			
	private void setWidth(double width)
	{
		this.bounds.setRect(this.bounds.getX(), 
							this.bounds.getY(),
							width,
							this.bounds.getHeight());
	}
	
	private void setVarWidth(int width)
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
	
	private void setInitialY(double y)
	{
		this.setY(y);
		this.hasYPosSet = true;
	}	
	
	private void setInitialBounds(double x, double y,
								  double width, double height)
	{
		this.bounds.setRect(x, y, width, height);
		this.hasYPosSet = true;
		this.hasHeightSet = true;
	}
	
	private void initializeShootDirection()
	{
		if(this.shootTimer == READY)
		{
			this.shootingDirection.setLocation( this.direction.x == -1
													? -1f 
													:  1f, 0f);
		}
		else if(this.barrierShootTimer == READY )
		{
			double tempRandom
				= Math.PI * (1 + Math.random()/2) 
					+ (this.bounds.getY() + this.bounds.getHeight()/2 < GROUND_Y/2 
						? Math.PI/2 
						: 0);
		
			this.shootingDirection.setLocation(
				Math.sin(tempRandom),
				Math.cos(tempRandom) );
		}		
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
				controller.enemies.get(INACTIVE).add(enemy);
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

	private static boolean tournaroundIsTurnAway(double dir, double enemyCenter,
												 double barrierCenter)
	{
		return 	   dir ==  1 && enemyCenter < barrierCenter
				|| dir == -1 && enemyCenter > barrierCenter;
	}
	
	public boolean isVisibleNonBarricadeVessel(boolean hasRadarDevice)
	{
		return this.type != ROCK
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
		if(this.type != ROCK){
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
		if(helicopter.getType() == PEGASUS){this.checkForEmpStrike(controller, (Pegasus)helicopter);}
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
			   && this.type != ROCK;
	}

	private void calculateBossManeuver(EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemy)
	{
		     if(this.type == BOSS_4)    {this.boss4Action(enemy);}
		else if(this.type == FINAL_BOSS){this.finalBossAction();}
		else if(this.shieldMakerTimer != DISABLED){this.shieldMakerAction();}
		else if(this.type == BODYGUARD) {this.bodyguardAction();}
		else if(this.type == HEALER 
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
			if(	tournaroundIsTurnAway(this.direction.x,
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
			if(tournaroundIsTurnAway(this.direction.y,
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
		if(this.borrowTimer != DISABLED && !(this.snoozeTimer > 0))
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
		if(this.canSinusMove || this.canLoop){this.sinusloop();}
		
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
			this.speedLevel.setLocation(6 + this.targetSpeedLevel.getX(), //d
										 this.speedLevel.getY());
		}
		if(this.canChaosSpeedup
		   && (helicopter.getBounds().getX() - this.bounds.getX()) > -160)
		{			
			this.canMoveChaotic = true;
			this.speedLevel.setLocation(this.speedLevel.getX(),
										 9 + 4.5*Math.random()); //d
		}
				
		// Ausweichen
		if(this.dodgeTimer > 0){
			evaluateDodge();}
		
		// Beamen
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
		if(	this.borrowTimer == DISABLED)
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
		if(this.borrowTimer > BORROW_TIME + this.shootingRate * this.shotsPerCycle)
		{
			this.borrowTimer = 2 * BORROW_TIME + this.shootingRate * this.shotsPerCycle - this.borrowTimer;
		}
		else if(this.borrowTimer > BORROW_TIME)
		{
			this.borrowTimer = BORROW_TIME;
		}
		this.direction.y = 1;
		this.speedLevel.setLocation(0, 1);
	}

	private void validateTurns()
	{
		if(	this.stoppingBarrier != null
			&& this.borrowTimer == DISABLED)
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
		if(this.type == BOSS_4){this.spawningHornetTimer = READY;}
		this.disableSiteEffects(pegasus);
				
		if(this.hasHPsLeft())
		{			
			Audio.play(Audio.stun);
			if(this.model == BARRIER){this.snooze(true);}
			else if(this.teleportTimer == READY ){this.teleport();}
			else if(this.isStunnable && !this.isShielding)
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
			   					|| (this.type == HEALER && this.bounds.getX() < 563)))
			   		||
			   		(this.direction.x == 1 
			   			&&(((this.callBack > 0 || this.type.isMajorBoss()) && this.bounds.getMaxX() > TURN_FRAME.getMaxX() && !this.canLearnKamikaze)
			   					|| (this.type == BODYGUARD && (this.bounds.getX() + this.bounds.getWidth() > 660)))));
	}

	private void changeXDirection()
	{
		this.direction.x = -this.direction.x;
		//this.turn_timer = MIN_TURN_TIME;
		if(this.callBack > 0){this.callBack--;}
	}
	
	private boolean hasToTurnAtYBorder()
	{		
		return 	this.borrowTimer == DISABLED
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
		if(this.canSinusMove){this.speedLevel.setLocation(this.speedLevel.getX(), 1);} //d
		if(this.model == BARRIER)
		{
			if(this.direction.y == -1){Audio.play(Audio.landing);}
			this.snooze(false);
		}		
	}

	private boolean isToBeRemoved()
	{		
		return this.type != BOSS_2_SERVANT
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
		if(this.type == ROCK)
		{
			currentRock = null;
			rockTimer = ROCK_FREE_TIME;
		}
		else if(this.isMiniBoss)
		{
			currentMiniBoss = null;
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
		if(  this.speedLevel.getX() < (this.speedup == DISABLED ? 12 : 19)  //d
			 && (this.speedup  > 0 || this.canFrontalSpeedup) )
		{
			this.speedLevel.setLocation(this.speedLevel.getX()+0.5, //d
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
			this.speedLevel.setLocation(this.speedLevel.getX(), 1.5); //d
			if(this.bounds.getY() < helicopter.getBounds().getY()){this.direction.y = 1;}
			else{this.direction.y = -1;}	
		}		
	}
	
	private boolean atEyeLevel(Helicopter helicopter)
	{
		return this.bounds.intersects(Integer.MIN_VALUE/2, 
									  helicopter.getBounds().getY(),
									  Integer.MAX_VALUE,
									  helicopter.getBounds().getHeight());
	}
	
	private void finalBossAction()
    {
		if(this.speedLevel.getX() > 0)
		{
			if(this.speedLevel.getX() - 0.5 <= 0) //d
			{
				this.speedLevel.setLocation(ZERO_SPEED);
				boss.setLocation(this.bounds.getCenterX(), 
						 		 this.bounds.getCenterY());
				makeAllBoss5Servants = true;
			}
			else
			{
				this.speedLevel.setLocation(this.speedLevel.getX()-0.5,	0); //d
			}
		}
		else for(int serantType = 0; serantType < NR_OF_BOSS_5_SERVANTS; serantType++)
		{
			if(this.operator.servants[serantType] == null)
			{	
				if(Calculations.tossUp(RETURN_PROB[serantType])
					&& this.operator.timeSinceDeath[serantType] > MIN_ABSENT_TIME[serantType])
				{
					makeBoss5Servant[serantType] = true;
				}
				else{this.operator.timeSinceDeath[serantType]++;}
			}
		}		
	}
	
	private int id()
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
			this.speedLevel.setLocation(this.speedLevel.getX()+0.5, //d
					 					 this.speedLevel.getY());
		}
		else if(this.batchWiseMove == -1)
		{
			this.speedLevel.setLocation(this.speedLevel.getX()-0.5, //d
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
			this.speedLevel.setLocation(7.5, this.speedLevel.getY());//d
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
				this.speedLevel.setLocation((this.type == BOSS_4 || this.type == BOSS_3) ? 12 : 8, //d
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
			
			if(this.speedLevel.getY() < 8) //d
			{
				this.speedLevel.setLocation(this.speedLevel.getX(),
											 this.speedLevel.getY()+0.5); //d
			}
		}
		else if(!this.canFrontalSpeedup && this.dodgeTimer == READY)
		{			
			if(this.type == BODYGUARD && Events.boss.shield < 1)
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
		if(this.borrowTimer > 0){this.borrowTimer--;}
		if(this.borrowTimer == BORROW_TIME + this.shootingRate * this.shotsPerCycle)
		{					
			this.barrierShootTimer = this.shootingRate * this.shotsPerCycle;
			this.speedLevel.setLocation(ZERO_SPEED);
		}
		else if(this.borrowTimer == BORROW_TIME)
		{
			this.barrierShootTimer = DISABLED;
			this.speedLevel.setLocation(0, 1); //d
			this.direction.y = 1;
		}
		else if(this.borrowTimer == 1)
		{
			this.speedLevel.setLocation(ZERO_SPEED);
		}
		else if(this.borrowTimer == READY
				&&( (this.type != PROTECTOR 
				     && Calculations.tossUp(0.004f))
				    || 
				    (this.type == PROTECTOR 
				     && (helicopter.getBounds().getX() > boss.getX() - 225) ))) 
		{			
			this.borrowTimer = 2 * BORROW_TIME
								+ this.shootingRate * this.shotsPerCycle
								+ (this.bounds.getY() == GROUND_Y 
									? PROTECTOR_WIDTH/8
									: 0)
								- 1;
			this.speedLevel.setLocation(0, 1);  //d
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
						 				this.bounds.getX() + Integer.MIN_VALUE/2, 
						 				this.bounds.getY() + (this.model == TIT ? 0 : this.bounds.getWidth()/2) - 15,
						 				Integer.MAX_VALUE/2, 
						 				EnemyMissile.DIAMETER+30))
				||
				((this.direction.x == 1 
				  && helicopter.getBounds().intersects(
						  				this.bounds.getX(), 
						  				this.bounds.getY() + (this.model == TIT ? 0 : this.bounds.getWidth()/2) - 15,
								 		Integer.MAX_VALUE/2, 
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
		return this.type == BOSS_3 
				|| this.isMiniBoss
				|| (this.type == BIG_SHIELD_MAKER && Calculations.tossUp());
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
			if(this.borrowTimer != DISABLED || this.barrierTeleportTimer != DISABLED)
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
	
	private void startBarrierUncloaking(Helicopter helicopter)
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
			this.speedLevel.setLocation(11, 11); //d
			this.canMoveChaotic = true;
			this.canKamikaze = true;
		}
		else if(this.spawningHornetTimer >= 50)
		{
			if(this.spawningHornetTimer == 50)
			{
				this.speedLevel.setLocation(3, 3); //d
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
	
	private void sinusloop()
    {
		this.speedLevel.setLocation(
				this.speedLevel.getX(),
				Math.max(4.0, 0.15f*(145-Math.abs(this.bounds.getY()-155))));   //d
		
    	if(this.canLoop)
    	{
    		if(this.direction.x == -1 && this.bounds.getY()-155>0)
    		{
    			this.direction.x = 1;
    			this.speedLevel.setLocation(11, this.speedLevel.getY()); //d
    		}
    		else if(this.direction.x == 1 && this.bounds.getY()-155<0)
    		{
    			this.direction.x = -1;
    			this.speedLevel.setLocation(7.5, this.speedLevel.getY()); //d
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
		this.speedLevel.setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);	 //d
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
		if(Events.boss.hitpoints < Events.boss.startingHitpoints)
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
				Events.boss.hitpoints 
					= Math.min(Events.boss.hitpoints + BOSS_5_HEAL_RATE, 
							   Events.boss.startingHitpoints);
			}						 
		}
		else
		{
			this.speedLevel.setLocation(this.targetSpeedLevel);
		}
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
		if(this.borrowTimer != DISABLED)
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
							this.type == ROCK ? this.bounds.getY() :
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
			     && this.borrowTimer == DISABLED)
			   || this.isDestroyed
			   || this.type == ROCK;
	}

	private void calculateSpeedDead()
	{		
		if(this.explodingTimer <= 0)
		{
			this.speed.setLocation(this.speedLevel); //d
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
					*((EMP_SLOW_TIME-this.empSlowedTimer)/EMP_SLOW_TIME),
				this.speed.getY()
					*((EMP_SLOW_TIME-this.empSlowedTimer)/EMP_SLOW_TIME));
		}
				
		if(	this.stoppingBarrier != null
			&& this.borrowTimer == DISABLED
			&& !(this.model == BARRIER && this.type == BARRIER_1))
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
			  	 + missileDrive/2 > Main.VIRTUAL_DIMENSION.width
			  	 								+ 2 * this.bounds.getWidth()/3  
			  || this.bounds.getMinX() 
			  	 - 18 
			  	 - missileDrive/2 < - 2 * this.bounds.getWidth()/3))
		{
			this.speedLevel.setLocation(ZERO_SPEED);
		}
	}	
	
	private void evaluateSpeedBoost()
	{		
		int bottomTurnLine = this.type == KABOOM
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
											this.targetSpeedLevel.getX() //d
												+ 7.5), 
										 Math.max(this.speedLevel.getY(), 5.5)); //d
			
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
		if(	this.borrowTimer != DISABLED || this.hasReachedTargetSpeed())
		{
			this.isRecoveringSpeed = false;
			if(this.borrowTimer != DISABLED)
			{
				this.speedLevel.setLocation(0, 1);
			}
			else{this.speedLevel.setLocation(this.targetSpeedLevel);}
		}		
		else if(this.speedLevel.getX() < this.targetSpeedLevel.getX())
		{					
			this.speedLevel.setLocation(this.speedLevel.getX()+0.025, //d
					 					 this.speedLevel.getY());
		}
		if(this.speedLevel.getY() < this.targetSpeedLevel.getY())
		{
			this.speedLevel.setLocation(this.speedLevel.getX(),
					 					 this.speedLevel.getY()+0.025); //d
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
				 && this.borrowTimer == DISABLED)
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
				controller.enemies.get(INACTIVE).add(e);
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
		Audio.play(this.type == KABOOM ? Audio.explosion4 : Audio.explosion3);
		Explosion.start(explosion, 
						helicopter, 
						this.bounds.getCenterX(),
						this.bounds.getCenterY(),
						this.type == KABOOM ? JUMBO : ORDINARY,
						this.type == KABOOM);
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
						  this.type == KABOOM
						  	? JUMBO 
						  	: ORDINARY,
						  this.type == KABOOM);
			
			if(	helicopter.canObtainCollisionReward()
				&& !(this.type == KABOOM))
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
				if(	helicopter.hasTripleDmg()
					&&  Calculations.tossUp(
							this.deactivationProb
							*(helicopter.bonusKillsTimer
								> NICE_CATCH_TIME
								  - TELEPORT_KILL_TIME ? 2 : 1)))
				{
					this.hitpoints = 0;
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
		this.hitpoints -= dmg;
		if(!this.hasHPsLeft()){this.hitpoints = 0;}
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
				this.hitpoints = 0;
			}
			else if(Calculations.tossUp(this.deactivationProb *(missile.typeOfExplosion == PLASMA ? 2 : 1)))
			{
				this.snooze(true);
			}
		}		
		if(missile.typeOfExplosion == STUNNING
		   && this.isStunnable
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
				    ? (10f + helicopter.missileDrive)/(Events.level/10)
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
		   && this.type != BOSS_3)
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
		if(this.type == BOSS_4){this.spawningHornetTimer = READY;}
		
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
			if(this.canDropPowerUp()){this.dropPowerUp(helicopter, powerUp);}
		}
		this.isDestroyed = true;
		if(this.cloakingTimer > 0){this.uncloak(DISABLED);}
		this.teleportTimer = DISABLED;
		this.farbe1 = Colorations.dimColor(this.farbe1, Colorations.DESTRUCTION_DIM_FACTOR);
		this.farbe2 = Colorations.dimColor(this.farbe2, Colorations.DESTRUCTION_DIM_FACTOR);
		
		this.repaint();
	
		if(helicopter.tractor == this)
		{
			helicopter.stopTractor();
		}		
		this.speedLevel.setLocation(0, 12); //d
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
		this.farbe1 = Colorations.setAlpha(this.farbe1, 255);
		this.farbe2 = Colorations.setAlpha(this.farbe2, 255);
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
			lastCarrier = this;}
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
        return this.type != BOSS_4_SERVANT && !this.type.isFinalBossServant();
    }
    
    public boolean canDropPowerUp()
	{		
		return this.model != BARRIER
			   &&( (!Events.isBossLevel()
				    &&( ( Calculations.tossUp(POWER_UP_PROB)
						  && Events.level >= MIN_POWER_UP_LEVEL) 
						|| this.isMiniBoss))
				|| this.type == BOSS_1
				|| this.type == BOSS_3
				|| this.type == BOSS_4 );
	}
	
	public void dropPowerUp(Helicopter helicopter,
							EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp)
	{
		PowerUp.activate(helicopter, 
				 powerUp, 
				 this, 
				 Calculations.tossUp(0.14f)
					? REPARATION
					: PowerUpType.getValues()[Calculations.random(this.type.isMajorBoss() ? PowerUpType.size() - 1 : PowerUpType.size())], false);
		
	}
	
	public int calculateReward(Helicopter helicopter)
	{		
		return this.getEffectiveStrength()
		        * helicopter.getBonusFactor()
			    + this.rewardModifier;
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
			currentMiniBoss = null;
		}			
		else if(this.type == BOSS_2)
		{								
			boss.setLocation(this.bounds.getCenterX(), 
							 this.bounds.getCenterY());
			makeBossTwoServants = true;
		}					
		else if(this.type == BOSS_4)
		{
			for(Enemy e : enemy.get(ACTIVE))
			{
				e.explode(explosion, helicopter);
				if (e.type != BOSS_4)
				{
					e.destroy(helicopter);
				}
			}
		}
		else if(this.type == FINAL_BOSS)
		{
			for(Enemy e : enemy.get(ACTIVE))
			{
				e.explode(explosion, helicopter);
				if (e.type != FINAL_BOSS)
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
		if(this.type.isMainBoss() && this.type != FINAL_BOSS){Events.boss = null;}
	}

	public void dodge()
	{
		if(this.type == BOSS_3)
		{			
			this.speedLevel.setLocation(this.speedLevel.getX(), 9);	//d
			this.dodgeTimer = 16;
		}
		else if(this.shootTimer != DISABLED || this.canChaosSpeedup)
		{			
			this.speedLevel.setLocation(this.speedLevel.getX(), 8.5);	//d
			this.dodgeTimer = 13;
		}
		else
		{
			this.speedLevel.setLocation(6, 6);	//d
			if(this.bounds.getMaxX() < 934){this.direction.x = 1;}
			this.dodgeTimer = 16;
		}															   
		
		if(this.bounds.getY() > 143){this.direction.y = -1;}
		else{this.direction.y = 1;}
		
		if(this.type.isShieldMaker()){this.stampedeShieldMaker();}
		else if(this.type == HEALER){this.canDodge = false;}
	}
	
	private void stampedeShieldMaker()
	{
		this.shieldMakerTimer = READY;
		this.speedLevel.setLocation(SHIELD_MAKER_STAMPEDE_SPEED); //d
		this.targetSpeedLevel.setLocation(SHIELD_MAKER_STAMPEDE_SPEED);
		this.canMoveChaotic = true;
		this.canDodge = false;
		this.setShieldingPosition();
		if(this.isShielding){this.stopShielding();}
	}

	private void setShieldingPosition()
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
		return this.type == SMALL_SHIELD_MAKER
				 ? id(BIG_SHIELD_MAKER)
				 : id(SMALL_SHIELD_MAKER);
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

	public boolean isHitable(Missile missile)
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
				&& !(this.type == HEALER 
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
		return this.hitpoints >= 1;
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
		return this.type == KABOOM && !this.isDestroyed;
	}

	public void grantGeneralRewards(Helicopter helicopter, EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUps)
	{
		if(this.canCountForKillsAfterLevelUp())
		{
			Events.killsAfterLevelUp++;
		}
		if(this.canDropPowerUp()){this.dropPowerUp(helicopter, powerUps);}
		if(this.isMiniBoss){Audio.play(Audio.applause2);}
	}
	
	public boolean isRock()
	{
		return type == ROCK;
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
}