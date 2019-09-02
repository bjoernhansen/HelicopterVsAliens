package de.helicopter_vs_aliens.model.enemy;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.*;

import de.helicopter_vs_aliens.*;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.EnemyMissileTypes;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpTypes;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.DESTROYED;
import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.INACTIVE;
import static de.helicopter_vs_aliens.control.TimesOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.background.BackgroundObject.BG_SPEED;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelTypes.*;
import static de.helicopter_vs_aliens.model.enemy.EnemyTypes.*;
import static de.helicopter_vs_aliens.model.enemy.BarrierPositionTypes.*;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.NICE_CATCH_TIME;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.TELEPORT_KILL_TIME;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileTypes.BUSTER;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileTypes.DISCHARGER;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.REPARATION;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;


public class Enemy extends MovingObject
{
    private static class FinalEnemysOperator
    {	
		Enemy[] servants;
    	int [] timeSinceDeath;
    	
    	FinalEnemysOperator()
    	{
    		this.servants = new Enemy [NR_OF_BOSS_5_SERVANTS];
    		this.timeSinceDeath = new int [NR_OF_BOSS_5_SERVANTS];
    	}
    }	
	
	// Konstanten	
	private static final Point2D 
		ZERO_SPEED = new Point2D.Float(0, 0);
	
	private static final Point 		
		TURN_DISTANCE = new Point(50, 10),
		TARGET_DISTANCE_VARIANCE = new Point(10, 3),
		SHIELD_MAKER_STAMPEDE_SPEED = new Point(10, 10),
		SHIELD_MAKER_CALM_DOWN_SPEED = new Point(3, 3);

	private static final float 	
		RADAR_STRENGTH 				= 0.2f,		// Alpha-Wert: legt fest, wie stark  ein getarnter Gegner bei aktiviertem Radar noch zu sehen ist
		HEIGHT_FACTOR 				= 0.28f,	// legt das Verhältnis von Höhe und Länge für die meisten Gegner fest
		HEIGHT_FACTOR_SUPERSIZE 	= 0.65f,	// legt das Verhältnis von Höhe und Länge für besonders hohe Gegner fest
		BARRIER_BORDER_SIZE 		= 0.23f,
		BARRIER_EYE_SIZE 			= 0.08f,
		ROCK_PROB					= 0.05f,
		KABOOM_PROB				    = 0.02f,	// Rate mit der Kaboom-Gegner erscheinen 
		POWER_UP_PROB				= 0.02f, 
		SPIN_SHOOTER_RATE 		   	= 0.55f,
		EXTRA_INACTIVE_TIME_FACTOR 	= 0.65f,

		// Multiplikatoren, welche den Grundschaden von Raketen unter bestimmten Voraussetzungen erhöhen
		RADIATION_DAMAGE_FACTOR = 1.5f,			// Phönix-Klasse, nach Erwerb von Nahkampfbestrahlung: Schaden im Verhältnis zum regulären Raketenschaden, den ein Gegner bei Kollsionen  mit dem Helikopter erleidet
		TELEPORT_DAMAGE_FACTOR = 4f,			// Phönix-Klasse: wie RADIATION_DAMAGE_FACTOR, aber für Kollisonen unmittelbar nach einem Transportvorgang
		EMP_DAMAGE_FACTOR_BOSS = 1.5f,			// Pegasus-Klasse: Schaden einer EMP-Welle im Verhältnis zum normalen Raketenschaden gegenüber von Boss-Gegnern // 1.5
		EMP_DAMAGE_FACTOR_ORDINARY = 2.5f,		// Pegasus-Klasse: wie EMP_DAMAGE_FACTOR_BOSS, nur für Nicht-Boss-Gegner // 3

		RETURN_PROB[]	= { 0.013f,	 	// SMALL_SHIELD_MAKER
             			    0.013f,  	// BIG_SHIELD_MAKER
             			    0.007f,  	// BODYGUARD
             			    0.01f,  	// HEALER
             			    0.04f}, 	// PROTECTOR
	
		STANDARD_MINI_BOSS_PROB = 0.05f,
		CHEAT_MINI_BOSS_PROB = 1.0f;
		
	private static final int 				
		// Raum-Konstanten
		SAVE_ZONE_WIDTH			= 116,
		APPEARANCE_DISTANCE		= 10,
		SHIELD_TARGET_DISTANCE  = 20,
		DISAPPEARANCE_DISTANCE	= 100,
		BARRIER_DISTANCE		= 100,
		ROCK_WIDTH				= 300,
		KABOOM_WIDTH			= 120,
		FINAL_BOSS_WIDTH		= 450,
		PROTECTOR_WIDTH 		= 90,
		KABOOM_Y_TURN_LINE		= GROUND_Y - (int)(HEIGHT_FACTOR*KABOOM_WIDTH),

		// Zeit-Konstanten
		CLOAKING_TIME			= 135, 	// Zeit, die beim Tarn- und Enttarnvorgang vergeht
		CLOAKED_TIME 			= 135, 	// Zeit, die ein Gegner getarnt bleibt
		ROCKFREE_TIME			= 250,	// Zeit die mind. vergeht, bis ein neuer Hindernis-Gegner erscheint
	 	EMP_SLOW_TIME			= 175, 	// Zeit, die von EMP getroffener Gegner verlangsamt bleibt // 113
	 	EMP_SLOW_TIME_BOSS		= 110,
	 	SNOOZE_TIME 			= 100,	// Zeit, die vergeht, bis sich ein aktives Hindernis in Bewegung setzt
	 	INACTIVATION_TIME		= 150,
	 	STUNNING_TIME_BASIS 	= 45,	// Basis-Wert zur Berechnung der Stun-Zeit nach Treffern von Stopp-Raketen
	 	SPEED_KILL_BONUS_TIME 	= 15,	// Zeit [frames], innerhalb welcher für einen Kamaitachi-Extra-Bonus Gegner besiegt werden müssen, erhöht sich um diesen Wert
	 	BORROW_TIME				= 65,
	 	MIN_TURN_TIME			= 31,
	 	MIN_TURN_NOISELESS_TIME = 15,
	 	STATIC_CHARGE_TIME		= 110,
	 	MAX_BARRIER_NUMBER		= 3,

	 	// Level-Voraussetzungen
	 	MIN_BARRIER_LEVEL		= 2,
	 	MIN_POWER_UP_LEVEL		= 3,
	 	MIN_FUTURE_LEVEL		= 8,
	 	MIN_KABOOM_LEVEL		= 12,
	 	MIN_SPIN_SHOOTER_LEVEL 	= 23,
	 	MIN_ROCK_LEVEL			= 27,
	 	MIN_BUSTER_LEVEL		= 29,

	 	// für Boss-Gegner
	 	NR_OF_BOSS_5_SERVANTS 	= 5,
	 	BOSS_5_HEAL_RATE		= 11,
		STANDARD_REWARD_FACTOR	= 1,
		MINI_BOSS_REWARD_FACTOR	= 4,

		// TODO die 4 austauschen / anders lösen
		PRE_READY 				= 1,
		READY 					= 0,
		ACTIVE_TIMER 			= 1,
		DISABLED 				= -1,

		MIN_ABSENT_TIME[]		= {175,	// SMALL_SHIELD_MAKER
		                 		   175, // BIG_SHIELD_MAKER
		                 		   900,	// BODYGUARD
		                 		   250, // HEALER
		                 		   90}; // PROTECTOR
	
	private static final Rectangle
		TURN_FRAME = new Rectangle(TURN_DISTANCE.x,
								   TURN_DISTANCE.y,
								   Main.VIRTUAL_DIMENSION.width 
								   	- 2*TURN_DISTANCE.x,
								   GROUND_Y 
									- SAVE_ZONE_WIDTH
									- 2*TURN_DISTANCE.y);

	private static final EnemySelector
		enemySelector = new EnemySelector();

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
		lastCarrier,  		// Referenz auf den zuletzt zerstörten Carrier-Gegner
		livingBarrier[] = new Enemy [MAX_BARRIER_NUMBER];
	
	public static EnemyTypes
		bossSelection;		 	// bestimmt, welche Boss-Typ erstellt wird
	
	private static int 
		selection,			// bestimmt welche Typen von Gegnern zufällig erscheinen können	
		selectionBarrier, 	// bestimmt den Typ der Hinernis-Gegner
		rockTimer,			// reguliert das Erscheinen von "Rock"-Gegnern
		barrierTimer;		// reguliert das Erscheinen von Hindernis-Gegnern
		
	// für die Tarnung nötige Variablen
    private static float[] 
    	scales = { 1f, 1f, 1f, RADAR_STRENGTH },
    	offsets = new float[4];	
	
    private static final RescaleOp 
		ROP_CLOAKED = new RescaleOp(scales, offsets, null);
    
	private static boolean
		creationStop =  false,	// = false: es werden keine neuen Gegner erzeugt, bis die Anzahl aktiver Gegner auf 0 fällt
		makeBossTwoServants =  false,	// make-Variablen: bestimmen, ob ein bestimmter Boss-Gegner zu erzeugen ist
		makeBoss4Servant =  false,
	    makeAllBoss5Servants =  false,
	    
	    makeBoss5Servant[]	= 	  {false,	// SMALL_SHIELD_MAKER
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
		hitpoints,						// aktuelle Hitpoints
		startingHitpoints,				// Anfangs-Hitpoints (bei Erstellung des Gegers)
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
	
	public EnemyTypes
		type;
	
	public BarrierPositionTypes
		touchedSite,
		lastTouchedSite;
	
	// Farben
    public Color 
    	farbe1,
    	farbe2; 

    public EnemyModelTypes
		model;				// legt das Aussehen (Model) des Gegners fest

    public Point 
    	direction = new Point();		// Flugrichtung
    	
	Enemy
		stoppingBarrier,		// Hindernis-Gegner, der diesen Gegner aufgehalten hat
		isPreviousStoppingBarrier;
		
	private int
		rewardModifier,			// für normale Gegner wird eine Zufallszahl zwischen -5 und 5 auf die Belohnung bei Abschuss addiert
		lifetime,				// Anzahl der Frames seit Erstellung des Gegners;  und vergangene Zeit seit Erstellung, Zeit	
			yCrashPos,			// Bestimmt wie tief ein Gegner nach Absturz im Boden versinken kann
		collisionAudioTimer,
		turnAudioTimer,
		explodingTimer,			// Timer zur überwachung der Zeit zwischen Abschuss und Absturz
		cloakingTimer,			// reguliert die Tarnung eines Gegners; = DISABLED: Gegner kann sich grundsätzlich nicht tarnen
		uncloakingSpeed,
		shieldMakerTimer,
		callBack,
		chaosTimer = 0,
		speedup,
		batchWiseMove,
		shootTimer,
		spawningHornetTimer,
		turnTimer,
		dodgeTimer,			// Zeit [frames], bis ein Gegner erneut ausweichen kann
		snoozeTimer,
		staticChargeTimer,
		
		// nur für Hindernis-Gegner releavant		
		rotorColor,
		barrierShootTimer,
		barrierTeleportTimer,
		shootPause,
		shootingRate,
		shotsPerCycle,
		shootingCycleLength,
		shotSpeed,
		shotRotationSpeed,
		
		// Regulation des Stuneffekte nach Treffer durch Stopp-Rakete der Orochi-Klasse
		nonStunableTimer,
		totalStunningTime,
		knockBackDirection;
		
	private float
		deactivationProb,
		dimFactor;
	
	private boolean
		canDodge,				// = true: Gegner kann Schüssen ausweichen
		canKamikaze,			// = true: Gegner geht auf Kollsionskurs, wenn die Distanz zum Helicopter klein ist
		canLearnKamikaze,		// = true: Gegner kann den Kamikaze-Modus einschalten, wenn der Helikopter zu nahe kommt
		canEarlyTurn,
		canMoveChaotic, 		// reguliert den zufälligen Richtungswechsel bei Chaosflug-Modus
		canSinusMove,			// Gegner fliegt in Kurven ähnlicher einer Sinus-Kurve
		canTurn,				// Gegner ändert bei Beschuss evtl.    seine Flugrichtung in Richtung Helikopter
		canInstantTurn,		// Gegner ändert bei Beschuss immer(!) seine Flugrichtung in Richtung Helikopter
		canFrontalSpeedup,	// Gegner wird schneller, wenn Helikopter ihm zu Nahe kommt
		canLoop,				// = true: Gegner fliegt Loopings
		canChaosSpeedup,		// erhöht die Geschwindigkeit, wenn in Helicopternähe

		isSpeedBoosted,
		isDestroyed,			// = true: Gegner wurde vernichtet
		hasHeightSet,			// = false --> heigt = height_factor * width; = true --> height wurde manuell festgelegt
		hasYPosSet,			// = false --> y-Position wurde nicht vorab festgelegt und muss automatisch ermittelt werden
		hasCrashed, 			// = true: Gegner ist abgestürzt
		isEmpShocked,			// = true: Gegner steht unter EMP-Schock -> ist verlangsamt
		isMarkedForRemoval,	// = true --> Gegner nicht mehr zu sehen; kann entsorgt werden
		isUpperShieldMaker,	// bestimmt die Position der Schild-Aufspannenden Servants von Boss 5
		isExplodable,			// = true: explodiert bei Kollisionen mit dem Helikopter
		isShielding,			// = true: Gegner spannt gerade ein Schutzschild für Boss 5 auf (nur für Schild-Generatoren von Boss 5)
		isStunnable,			// = false für Boss 5; bestimmt ob ein Gegner von Stopp-Raketen (Orochi-Klasse) gestunt werden kann
		isCarrier,				// = true
		isClockwiseBarrier,	// = true: der Rotor des Hindernis dreht im Uhrzeigersinn
		isRecoveringSpeed;
  
	private AbilityStatusTypes
		tractor;				// = DISABLED (Gegner ohne Traktor); = READY (Traktor nicht aktiv); = 1 (Traktor aktiv)
	
	private EnemyMissileTypes
		shotType;
	
	private GradientPaint 
		gradientColor;
	
	private Graphics2D []
		graphics = new Graphics2D [2];
	
	private FinalEnemysOperator
		operator;
	
	private BufferedImage [] 
		image = new BufferedImage[4];
		
	private Point2D
		targetSpeedLevel = new Point2D.Float(),		// Anfangsgeschwindigkeit
		speedLevel = new Point2D.Float(),			// auf Basis dieses Objektes wird die tatsächliche Geschwindigkeit berechnet
		speed = new Point2D.Float(),				// tatsächliche Geschwindigkeit
		shootingDirection = new Point2D.Float();   	// Schussrichtugn von schießenden Barrier-Gegnern
	
	
	public static void changeMiniBossProb()
	{
		miniBossProb = miniBossProb == STANDARD_MINI_BOSS_PROB ? CHEAT_MINI_BOSS_PROB: STANDARD_MINI_BOSS_PROB;
	}
	
	public void paint(Graphics2D g2d, Helicopter helicopter)
	{				
		boolean cloaked = (this.cloakingTimer > CLOAKING_TIME && this.cloakingTimer <= CLOAKING_TIME+CLOAKED_TIME);
		int g2DSel = this.direction.x == -1 ? 0 : 1;
		
		if(!cloaked)
		{
			if(this.isInvincible())
			{	
				this.paintImage(g2d, -this.direction.x, MyColor.variableGreen, false);
			}
			else if(this.alpha != 255)
			{
				if(this.alpha > 51 || !helicopter.canDetectCloakedVessels())
				{
					scales[3] = ((float)this.alpha)/255;			
					g2d.drawImage(	this.image[g2DSel],
									new RescaleOp(scales, offsets, null), 
									this.paintBounds.x - (this.direction.x == -1 ? this.paintBounds.width/36 : 0),
									this.paintBounds.y - this.paintBounds.height/4);
				}
				else
				{
					g2d.drawImage(	this.image[g2DSel + 2],
									this.paintBounds.x - (this.direction.x == -1 ? this.paintBounds.width/36 : 0),
									this.paintBounds.y - this.paintBounds.height/4, null);
				}			
			}
			else
			{
				g2d.drawImage(	this.image[g2DSel],
								this.paintBounds.x - (this.direction.x == -1 ? this.paintBounds.width/36 : 0),
								this.paintBounds.y - this.paintBounds.height/4, null);
			}
						
			// Dach
			if(!this.isDestroyed && (this.tractor == AbilityStatusTypes.ACTIVE || this.shootTimer > 0 || this.isShielding))
			{
				Color inputColorRoof
					= this.alpha < 255 
						? MyColor.setAlpha(MyColor.variableGreen, this.alpha) 								
						: MyColor.variableGreen;				
				
				this.paintCannon(g2d, this.paintBounds.x, this.paintBounds.y, -this.direction.x, inputColorRoof);
			}
						
			// blinkende Scheibe von Bossen und Mini-Bossen bzw. Eyes bei Hindernissen
			if(this.hasGlowingEyes())
			{				
				if(this.model != BARRIER){this.paintWindow(g2d);}
				else{this.paintBarrierEyes(g2d);}
			}			
						
			// Auspuff			
			if(!(this.isDestroyed || this.stunningTimer > 0))
			{
				int temp = 63 - (((int)(2 + 0.1f * Math.abs(this.speedLevel.getX())) * this.lifetime)%32); //d
				Color colorTemp = new Color(255, 192+temp, 129+temp, this.alpha);
				this.paintExhaustPipe(g2d, colorTemp);
			}			
					
			// die Schild- und Traktorstrahlen
			if(this.tractor == AbilityStatusTypes.ACTIVE){this.paintTractorBeam(g2d, helicopter);}
			else if(this.type == FINAL_BOSS)
			{
				for(int servantType = id(SMALL_SHIELD_MAKER); servantType <= id(BIG_SHIELD_MAKER); servantType++)
				{
					if( this.operator.servants[servantType] != null && 
						this.operator.servants[servantType].isShielding)
					{
						this.operator.servants[servantType].paintShieldBeam(g2d);
					}
				}
			}
			
			if(this.model == BARRIER && !this.isDestroyed)
			{
				this.paintRotor(g2d);
			}
		}
		else if(helicopter.canDetectCloakedVessels())
		{
			g2d.drawImage(	this.image[g2DSel + 2],
							this.paintBounds.x - (this.direction.x == -1 ? this.paintBounds.width/36 : 0),
							this.paintBounds.y - this.paintBounds.height/4,
							null);
		}
		
		//zu Testzwecken:	
        //g2d.setColor(Color.red);
        
        /*if(this.model == BARRIER)
        	{
        		g2d.drawString(   "Borrow: " + this.borrowTimer + " ; "
        	
        				+ "Stun: "   + this.stunningTimer + " ; "
        				+ "Snooze: " + this.snooze_timer + " ; ", 
        				(int)this.bounds.getX(),
        				(int) this.bounds.getY());
        	}*/
        //g2d.draw(TURN_FRAME);
	}
	
	private boolean hasGlowingEyes()
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
    		if(i < 2){this.graphics[i] = null;}
    	}
    }
    
    public void repaint()
	{
		for(int j = 0; j < 2; j++)
		{		
			if(this.model != BARRIER)
			{				
				this.graphics[j].setComposite(AlphaComposite.Src);
				this.graphics[j].setColor(MyColor.translucentDarkestBlack);
				this.graphics[j].fillRect(0, 0, this.image[j].getWidth(), this.image[j].getHeight());
			}			
			this.paintImage(this.graphics[j], 1-2*j, null, true);
		}
	}		
	
	private void paintRotor(Graphics2D g2d)
	{
		paintRotor(g2d, this.paintBounds.x, this.paintBounds.y);
	}
	
	private void paintRotor(Graphics2D g2d, int x, int y)
	{
		Helicopter.paintRotor(	g2d,
								!this.isDestroyed
									?(MyColor.setAlpha(MyColor.barrierColor[this.rotorColor][Events.timeOfDay.ordinal()], this.alpha))
									: MyColor.dimColor(MyColor.barrierColor[this.rotorColor][Events.timeOfDay.ordinal()], MyColor.DESTRUCTION_DIM_FACTOR),
								x, y, this.paintBounds.width, this.paintBounds.height, 5, (this.speedLevel.equals(ZERO_SPEED) ? (this.snoozeTimer <= SNOOZE_TIME ? 3 : 0) : 8) * (this.isClockwiseBarrier ? -1 : 1) * this.lifetime%360,
								24, BARRIER_BORDER_SIZE, this.snoozeTimer == 0);
		this.paintBarrierCannon(g2d, x, y);
	}
	
	private void paintBarrierCannon(Graphics2D g2d, int x, int y)
	{		
		Color tempColor;
		int distanceX, distanceY;
		for(int i = 0; i < 3; i++)
		{
			tempColor = (this.barrierShootTimer != DISABLED && this.barrierShootTimer <= this.shotsPerCycle * this.shootingRate && i != 0 && !this.isDestroyed)
							?  MyColor.variableGreen
							: !this.isDestroyed
								? MyColor.barrierColor[i][Events.timeOfDay.ordinal()]
								: MyColor.dimColor(MyColor.barrierColor[i][Events.timeOfDay.ordinal()], MyColor.DESTRUCTION_DIM_FACTOR);
			if(this.alpha != 255){tempColor = MyColor.setAlpha(tempColor, this.alpha);}
			g2d.setColor(tempColor);
			
			distanceX = (int) ((0.45f + i * 0.01f) * this.paintBounds.width);
			distanceY = (int) ((0.45f + i * 0.01f) * this.paintBounds.height);
						
			g2d.fillOval(x + distanceX,
				 	  	 y + distanceY,
				 	  	 this.paintBounds.width  - 2*distanceX,
				 	  	 this.paintBounds.height - 2*distanceY);
		}		
	}	

	private void paintCannon(Graphics2D g2d, int x, int y, int directionX, Color inputColor)
	{
		if(this.model == TIT)
		{
			paintBar(	g2d,
						x,	y, 
						this.paintBounds.width, this.paintBounds.height,
						0.02f, 0.007f, 0.167f, 0.04f, 0.6f,  
						directionX, true, inputColor);
		}
		else if(this.model == CARGO)
		{
			paintBar(	g2d,
						x, (int) (y + 0.48f * this.paintBounds.height),
						this.paintBounds.width, this.paintBounds.height,
						0, 0, 0.1f, 0.04f, 0.6f, 
						directionX, true, inputColor);
		}
	}	
	
	private void paintBarFrame(Graphics2D g2d, int x, int y,
							   float thicknessFactor,
							   float shift, float centerShift,
							   float dimFactor,
							   Color inputColor, Color backgroundColor,
							   boolean imagePaint)
	{		
		if(backgroundColor != null)
		{
			g2d.setPaint(new GradientPaint(	0, 
											y, 
											backgroundColor,
											0, 
											y + 0.3f*thicknessFactor*this.paintBounds.height,
											MyColor.dimColor(backgroundColor, 0.85f),
											true));
			
			g2d.fillRect(x + (int)(thicknessFactor/2 * this.paintBounds.width),
				  	     y + (int)(thicknessFactor/2 * this.paintBounds.height),
				  	     (int)((1f-thicknessFactor)  * this.paintBounds.width),
				  	     (int)((1f-thicknessFactor)  * this.paintBounds.height));
		}
		
		int xShift = (int) (shift * this.paintBounds.width),
			yShift = (int) (shift * this.paintBounds.height),
			xCenterShift = (int) (centerShift * this.paintBounds.width),
			yCenterShift = (int) (centerShift * this.paintBounds.height);
		
		
		if(imagePaint || (this.speedLevel.getX() != 0 && this.direction.x == 1))
		{
			paintBar(	g2d,
						x + xCenterShift,
						y + yShift,
						this.paintBounds.width,
						this.paintBounds.height - 2 * yShift,
						thicknessFactor,
						0.2f,
						dimFactor,
						false, 
						inputColor);
		}
		if(imagePaint || (this.speedLevel.getX() != 0 && this.direction.x ==  -1))
		{
			paintBar(	g2d,
						(int)(x + 1 + (1f-thicknessFactor)*this.paintBounds.width)-xCenterShift,
						y + yShift,
						this.paintBounds.width,
						this.paintBounds.height - 2 * yShift,
						thicknessFactor,
						0.2f,
						dimFactor,
						false, 
						inputColor);
		}
		if(imagePaint || (this.speedLevel.getY() != 0 && this.direction.y ==  1))
		{
			paintBar(	g2d,
						x + xShift,
						y + yCenterShift,
						this.paintBounds.width - 2 * xShift,
						this.paintBounds.height,
						thicknessFactor,
						0.2f,
						dimFactor,
						true,
						inputColor);
		}
		if(imagePaint || (this.speedLevel.getY() != 0 && this.direction.y == -1))
		{
			paintBar(	g2d,
						x + xShift,
						(int)(y + 1 + (1f-thicknessFactor)*this.paintBounds.height)-yCenterShift,
						this.paintBounds.width - 2 * xShift,
						this.paintBounds.height,
						thicknessFactor,
						0.2f,
						dimFactor,
						true, 
						inputColor);
		}
	}	
	
	private static void paintBar(Graphics2D g2d,
								 int x, int y,
								 int width, int height,
								 float thicknessFactor,
								 float rounding,
								 float dimFactor,
								 boolean horizontal,
								 Color inputColor)
	{		
		paintBar(	g2d,
					x, y,
					width, height, 
					0, 0,
					thicknessFactor,
					rounding,
					dimFactor,
					1,
					horizontal,
					inputColor);
	}
	
	private static void paintBar(Graphics2D g2d,
								 int x, int y,
								 int width, int height,
								 float xShiftLeft, float xShiftRight,
								 float thicknessFactor, float rounding,
								 float dimFactor, int directionX,
								 boolean horizontal, Color inputColor)
	{		
		g2d.setPaint( new GradientPaint(	(int) (horizontal ? 0 : x + 0.5f * thicknessFactor * width),
											(int) (horizontal ?     y + 0.5f * thicknessFactor * height : 0),
											inputColor,
											(int) (horizontal ? 0 : x + 1.0f * thicknessFactor * width),
											(int) (horizontal ?     y + 1.0f * thicknessFactor * height : 0),
											MyColor.dimColor(inputColor, dimFactor),
											true));		
		
		g2d.fillRoundRect(	(int) (x - (directionX == 1 ? xShiftLeft : xShiftRight) * width),
							y,  
							(int) (	horizontal ? (1 + xShiftLeft + xShiftRight) * width : thicknessFactor * width),
							(int) (	horizontal ? thicknessFactor * height : (1 + xShiftLeft + xShiftRight) * height ),
							(int) (	horizontal ? rounding * width : thicknessFactor * width),
							(int) (	horizontal ? thicknessFactor * height : rounding * height) );
	}	
	
	// malen der Seitenflügel mit Antriebsdüse
	private void paintExhaustPipe(Graphics2D g2d, Color color4)
	{
		paintExhaustPipe(g2d,
					   this.paintBounds.x,
					   this.paintBounds.y,
					   -this.direction.x, 
					   null,
					   color4);
	}

	// TODO bessere Namen für Bezeichner color2 , color 4
	private void paintExhaustPipe(Graphics2D g2d, int x, int y, int directionX, Color color2, Color color4)
	{			
		if(this.model == TIT)
		{			
			paintEngine(g2d, x, y, 0.45f, 0.27f, 0.5f, 0.4f, directionX, color2, color4);
		}
		else if(this.model == CARGO)
		{	
			paintEngine(g2d, x, y, 0.45f, 0.17f, 0.45f, 0.22f, directionX, color2, color4);
			paintEngine(g2d, x, y, 0.45f, 0.17f, 0.25f, 0.70f, directionX, color2, color4);
		}
		else if(this.model == BARRIER)
		{
			paintBarFrame(g2d, this.paintBounds.x, this.paintBounds.y,
							0.07f, 0.35f, 0.04f, 0.7f, color4, null, false);
		}
	}
		
	private void paintEngine(Graphics2D g2d,
							 int x, int y,
							 float width, float height,
							 float xShift, float yShift,
							 int directionX,
							 Color color2, Color color4)
	{			
		if(color2 != null)
		{
			paintPipe(g2d, x, y, width, height, xShift, 				  yShift, directionX, color2, false);
		}
			paintPipe(g2d, x, y, 0.05f, height, xShift + width - 0.05f, yShift, directionX, color4, true);
	}		
	
	private void paintPipe(Graphics2D g2d,
						   int x, int y,
						   float width, float height,
						   float xShift, float yShift,
						   int directionX, Color color, boolean isExhaust)
	{			
		g2d.setPaint(new GradientPaint(	0, 
										y + (yShift + 0.05f)  * this.paintBounds.height,
										color, 
										0, 
										y + (yShift + height) * this.paintBounds.height,
										MyColor.dimColor(color, 0.5f), 
										true));
		
		g2d.fillRoundRect(	(int) (x + (directionX == 1
										? xShift
										: 1f - xShift - width)	* this.paintBounds.width),
							(int) (y + 	yShift 			   	* this.paintBounds.height),
							(int) (		width  				   	* this.paintBounds.width),
							(int) (		height  			   	* this.paintBounds.height),
							(int) ((isExhaust ? 0f : height/2) * this.paintBounds.width),
							(int) ((isExhaust ? 0f : height  ) * this.paintBounds.height)  );
	}
	
	
	private void paintImage(Graphics2D g2d, int directionX, Color color, boolean imagePaint)
	{	
		int offsetX = (int)(imagePaint
								? (directionX == 1 ? 0.028f * this.paintBounds.width : 0)
								: this.paintBounds.x),
								
			offsetY = (int)(imagePaint
								? 0.25f * this.paintBounds.height
								: this.paintBounds.y);
		
		boolean getarnt = 	 this.cloakingTimer > CLOAKING_TIME
						  && this.cloakingTimer <= CLOAKING_TIME+CLOAKED_TIME;
						
		/*
		 * Festlegen der Farben
		 */
		Color mainColorLight, mainColorDark, barColor, inactiveNozzleColor;
		
		if(color == null)
		{
			if(this.isDestroyed && Events.timeOfDay == NIGHT)
			{
				mainColorLight = MyColor.dimColor(this.farbe1, 1.3f * MyColor.NIGHT_DIM_FACTOR);
				mainColorDark  = MyColor.dimColor(this.farbe2, 1.3f * MyColor.NIGHT_DIM_FACTOR);
			}
			else
			{
				mainColorLight = this.farbe1;
				mainColorDark  = this.farbe2;
			}
		}
		else
		{
			mainColorLight = color;
			mainColorDark = MyColor.dimColor(color, 1.5f);
		}		
		
		if(this.model == BARRIER){barColor = MyColor.barrierColor[MyColor.FRAME][Events.timeOfDay.ordinal()];}
		else if(!this.isDestroyed && (this.tractor == AbilityStatusTypes.ACTIVE || this.shootTimer > 0 || this.isShielding)){barColor = MyColor.variableGreen;}
		else if(!this.isDestroyed && !imagePaint && this.isInvincible()){barColor = Color.green;}
		else if(this.isMiniBoss){barColor = this.farbe2;}
		else{barColor = MyColor.enemyGray;}
		inactiveNozzleColor = MyColor.INACTIVE_NOZZLE;
		
		if(this.model == BARRIER && Events.timeOfDay == NIGHT)
		{
			inactiveNozzleColor = MyColor.barrierColor[MyColor.NOZZLE][Events.timeOfDay.ordinal()];
		}
		
		if(this.isDestroyed)
		{
			barColor = MyColor.dimColor(barColor, Events.timeOfDay == NIGHT ? 1.3f * MyColor.NIGHT_DIM_FACTOR : 1);
			inactiveNozzleColor = MyColor.dimColor(inactiveNozzleColor, Events.timeOfDay == NIGHT ? 1.3f * MyColor.NIGHT_DIM_FACTOR : 1);
		}
				
		//Malen des Gegners
		if(this.model != BARRIER)
		{
			paintVessel(	g2d,
							offsetX, offsetY,
							directionX,
							color, 
							getarnt, imagePaint,
							mainColorLight, mainColorDark,
							barColor, inactiveNozzleColor);
		}
		else
		{
			paintBarrier(	g2d,
							offsetX, offsetY,
							imagePaint,
							mainColorLight, mainColorDark,
							barColor, inactiveNozzleColor);
		}
	}	
	
	private void paintVessel(Graphics2D g2d, int offsetX, int offsetY,
							 int directionX, Color color, boolean getarnt,
							 boolean imagePaint,
							 Color mainColorLight,
							 Color mainColorDark,
							 Color cannonColor,
							 Color inactiveNozzleColor)
	{	
		if(this.model == CARGO)
		{
			this.paintRoof(g2d, cannonColor, offsetX, offsetY, directionX);
		}
		this.paintAirframe(g2d, mainColorLight, offsetX, offsetY, directionX);
		this.paintCannon(g2d, offsetX, offsetY, directionX, cannonColor);
		if(this.model == TIT)
		{
			this.paintVerticalStabilizer(g2d, offsetX, offsetY, directionX);
		}		
		this.paintExhaustPipe(	g2d, offsetX, offsetY, directionX,
							mainColorDark, inactiveNozzleColor);
		
		if(Color.red.equals(color) || !this.isLivingBoss())
		{		
			this.paintWindow(
					g2d, 
					offsetX,
					(int)(offsetY
							+ this.paintBounds.height
							  *(this.model == TIT ? 0.067f : 0.125f)), 
					Color.red.equals(color) ? MyColor.cloakedBossEye : null, 
					directionX,
					getarnt && !imagePaint);
		}		
		
		// das rote Kreuz		
		if(this.type == HEALER)
		{
			paintRedCross(
					g2d,
					(int)( offsetX + (directionX == 1
								? 0.7f * this.paintBounds.width
								: (1 - 0.7f - 0.18f) * this.paintBounds.width)),
					(int) (offsetY + 0.6f * this.paintBounds.height),
					(int) (			  0.18f * this.paintBounds.width));
		}
				
		/*g2d.setColor(Color.red);
		g2d.draw3DRect(offset_x, offset_y, this.bounds.getWidth() - 1, this.bounds.getHeight() - 1, true);*/
	}
	
	private void paintBarrier(Graphics2D g2d,
							  int offsetX, int offsetY,
							  boolean imagePaint,
							  Color mainColorLight,
							  Color mainColorDark,
							  Color barColor,
							  Color inactiveNozzleColor)
	{		
		// Rahmen & Antriebsbalken
		paintBarFrame(g2d, offsetX, offsetY, 0.15f, 0f,    0f,    0.5f, barColor, mainColorLight, true);
		paintBarFrame(g2d, offsetX, offsetY, 0.07f, 0.35f, 0.04f, 0.7f, inactiveNozzleColor, null, true);
		
		// "Augen"
		this.paintBarrierEyes(g2d,
								offsetX,
								offsetY,
								MyColor.barrierColor[MyColor.EYES][Events.timeOfDay.ordinal()],
								imagePaint);
		
		// Turbinen-Innenraum
		this.paintRotorInterior(g2d, mainColorDark, offsetX, offsetY );
		
		if(this.isDestroyed){this.paintRotor(g2d, offsetX, offsetY);}
		
		//g2d.setPaint(Color.red);	
		//g2d.drawRoundRect(offset_x, offset_y, this.bounds.getWidth()-1, this.bounds.getHeight()-1, this.bounds.getWidth()/2, this.bounds.getHeight()/2);
		//g2d.drawRect(offset_x, offset_y, this.bounds.getWidth() - 1, this.bounds.getHeight() - 1);
		//Menu.paint_frame(g2d, offset_x, offset_y, this.bounds.getWidth() - 4, this.bounds.getHeight() - 4, Color.yellow);
	}
	
	private void paintRotorInterior(Graphics2D g2d, Color mainColorDark,
									int offsetX, int offsetY)
	{
		int distanceX = (int) (BARRIER_BORDER_SIZE * this.paintBounds.width),
			distanceY = (int) (BARRIER_BORDER_SIZE * this.paintBounds.height);
					
		g2d.setPaint(new GradientPaint(	0, 
										offsetY,
										mainColorDark,
										0, 
										offsetY + 0.045f*this.paintBounds.height,
										MyColor.dimColor(mainColorDark, 0.85f),
										true));	
		
		g2d.fillOval(offsetX + distanceX,
					 offsetY + distanceY,
					 this.paintBounds.width  - 2 * distanceX,
					 this.paintBounds.height - 2 * distanceY);
	}

	private void paintRoof(Graphics2D g2d, Color roofColor, int offsetX,
						   int offsetY, int directionX)
	{
		g2d.setPaint(roofColor);
		g2d.fillRoundRect(	(int) (offsetX + (directionX == 1 ? 0.05f :  0.35f) * this.paintBounds.width),
							offsetY,
							(int) (0.6f   * this.paintBounds.width),
							(int) (0.125f * this.paintBounds.height),
							(int) (0.6f   * this.paintBounds.width),
							(int) (0.125f * this.paintBounds.height));
	}

	// malen des Schiffrumpfes	
	private void paintAirframe(Graphics2D g2d, Color mainColorLight,
							   int offsetX, int offsetY, int directionX)
	{		
		this.setAirframeColor(g2d, offsetY, mainColorLight);
		
		if(this.model == TIT)
		{			
			g2d.fillArc(offsetX,
						(int) (offsetY - 0.333f * this.paintBounds.height - 2),
						this.paintBounds.width,
						this.paintBounds.height, 180, 180);
			
			g2d.fillArc((int)(offsetX + (directionX == 1 ? 0.2f * this.paintBounds.width : 0)),
						(int)(offsetY - 0.667f * this.paintBounds.height),
						(int)(			 0.8f   * this.paintBounds.width),
						(int)(			 1.667f * this.paintBounds.height), 180, 180);
		}
		else if(this.model == CARGO)
		{
			g2d.fillOval(	(int)(offsetX + 0.02f * this.paintBounds.width),
					(int)(offsetY + 0.1f * this.paintBounds.height),
					(int)(0.96f * this.paintBounds.width),
					(int)(0.9f  * this.paintBounds.height));
			
			g2d.fillRect(	(int)(offsetX + (directionX == 1 ? 0.05f : 0.35f) * this.paintBounds.width),
							(int)(offsetY + 0.094f * this.paintBounds.height),
							(int)(0.6f * this.paintBounds.width),
							(int)(0.333f * this.paintBounds.height));

			g2d.fillRoundRect(	(int) (offsetX + (directionX == 1 ? 0.05f : 0.35f) * this.paintBounds.width),
								(int) (offsetY + 0.031 * this.paintBounds.height),
								(int) (0.6f * this.paintBounds.width),
								(int) (0.125f * this.paintBounds.height),
								(int) (0.6f * this.paintBounds.width),
								(int) (0.125f * this.paintBounds.height));
		
			// Rückflügel
			g2d.fillArc(	(int)(offsetX + (directionX == 1 ? 0.5f * this.paintBounds.width : 0)),
							(int)(offsetY - 0.3f * this.paintBounds.height),
							(int)(0.5f * this.paintBounds.width),
							this.paintBounds.height,
							directionX == 1 ? -32 : 155,
							57);
		}
	}

	private void paintVerticalStabilizer(Graphics2D g2d,
										 int offsetX, int offsetY,
										 int directionX)
	{
		g2d.setPaint(this.gradientColor);		
		g2d.fillArc((int)(offsetX + (directionX == 1 ? 0.4f : 0.1f) * this.paintBounds.width),
				(int)(offsetY - 						   0.917f * this.paintBounds.height),
				(int)(0.5f * this.paintBounds.width),
				 2 * this.paintBounds.height, directionX == 1 ? 0 : 160, 20);
	}
	
	private void setAirframeColor(Graphics2D g2d, int offsetY,
								  Color mainColorLight)
	{
		this.gradientColor = new GradientPaint(	
				0, 
				offsetY + (this.model == TIT ? 0.25f : 0.375f) * this.paintBounds.height,
				mainColorLight,
				0,
				offsetY + this.paintBounds.height,
				MyColor.dimColor(mainColorLight, 0.5f),
				true);
			
		g2d.setPaint(this.gradientColor);		
	}	
	
	private void paintBarrierEyes(Graphics2D g2d)
	{
		paintBarrierEyes(	g2d,
							this.paintBounds.x,
							this.paintBounds.y,
							this.alpha != 255 
								? MyColor.setAlpha(MyColor.variableRed, this.alpha) 
								: MyColor.variableRed,
							false);
	}
	
	public void paintBarrierEyes(Graphics2D g2d, int x, int y, Color color, boolean imagePaint)
	{		
		int borderDistance = (int)(0.85f * BARRIER_BORDER_SIZE * this.paintBounds.width),
			eyeSize = 		  (int)(	    BARRIER_EYE_SIZE    * this.paintBounds.width);
				
		g2d.setPaint(color);
		
		g2d.fillOval(x + borderDistance,
					 y + borderDistance,
					 eyeSize, eyeSize);
		
		g2d.fillOval(x - borderDistance + this.paintBounds.width  - eyeSize,
					 y - borderDistance + this.paintBounds.height - eyeSize,
					 eyeSize, eyeSize);
		
		if(!imagePaint && !(this.snoozeTimer > SNOOZE_TIME)){g2d.setPaint(MyColor.reversedRandomRed(color));}
		g2d.fillOval(x + borderDistance,
					 y - borderDistance + this.paintBounds.height - eyeSize,
					 eyeSize, eyeSize);
		
		g2d.fillOval(x - borderDistance + this.paintBounds.width  - eyeSize,
					 y + borderDistance,
					 eyeSize, eyeSize);
	}

	private static void paintRedCross(Graphics2D g2d, int x, int y, int height)
	{
		g2d.setColor(Color.red);				
		g2d.setStroke(new BasicStroke(height/5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));				
		g2d.drawLine(x + height/2, y + height/5, x + height/2, y + (4 * height)/5);
		g2d.drawLine(x + height/5, y + height/2, x + (4 * height)/5, y + height/2);			
		g2d.setStroke(new BasicStroke(1));
		//g2d.drawRect(x, y, height, height);
	}

	void paintTractorBeam(Graphics2D g2d, Helicopter helicopter)
	{		
		paintEnergyBeam(	g2d,
							this.paintBounds.x,
							this.paintBounds.y + 1,
							(int)(helicopter.bounds.getX() 
								+ (helicopter.isMovingLeft
									? Helicopter.FOCAL_PNT_X_LEFT 
									: Helicopter.FOCAL_PNT_X_RIGHT)),  // 114 
							(int)(helicopter.bounds.getY() 
								+ Helicopter.FOCAL_PNT_Y_EXP));
	}
	
	private void paintShieldBeam(Graphics2D g2d)
	{				
		paintEnergyBeam(g2d,	this.paintBounds.x + (this.direction.x + 1)/2 * this.paintBounds.width,
								this.paintBounds.y,
								Events.boss.paintBounds.x + Events.boss.paintBounds.width/48,
								Events.boss.paintBounds.y + Events.boss.paintBounds.width/48);
	}
	
	public static void paintEnergyBeam(Graphics2D g2d, int x1, int y1, int x2, int y2)
	{
		g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(MyColor.green);
		g2d.drawLine(x1, y1, x2, y2);
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(Color.green);
		g2d.drawLine(x1, y1+1, x2, y2);
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	}	
	
	private void paintWindow(Graphics2D g2d)
	{
		paintWindow(g2d,
					 this.paintBounds.x,
					 (int) (this.paintBounds.y
							+ this.paintBounds.height
							  *(this.model == TIT ? 0.067f : 0.125f)), 
					 this.alpha != 255 
						? MyColor.setAlpha(MyColor.variableRed, this.alpha) 
						: MyColor.variableRed,
					 -this.direction.x, 
					 false);
	}
	
	public void paintWindow(Graphics2D g2d, int x, int y, Color color, int directionX, boolean getarnt)
	{
		this.setWindowColor(g2d, color, getarnt);
						
		if(this.model == TIT)
		{
			g2d.fillArc(	(int) (x + (directionX == 1 ? 0.25f : 0.55f)
										* this.paintBounds.width),
							y, 
							(int) (0.2f   * this.paintBounds.width),
							(int) (0.267f * this.paintBounds.height),
							180, 
							180);
		}
		else if(this.model == CARGO)
		{
			g2d.fillArc(	(int) (x + (directionX == 1 ? 0.1 : 0.6)
										* this.paintBounds.width),
							y, 
							(int) (0.3f   * this.paintBounds.width),
							(int) (0.333f * this.paintBounds.height),
							directionX == 1 ? 90 : 0,
							90);
		}
	}
	
	private void setWindowColor(Graphics2D g2d, Color color, boolean getarnt)
	{
		if(color == null && !getarnt)
		{
			g2d.setColor(this.isLivingBoss()
				 	? (this.alpha == 255 
				 		? MyColor.variableRed 
				 		: MyColor.setAlpha(MyColor.variableRed, this.alpha)) 
				 	: (this.alpha == 255 
				 		? MyColor.windowBlue 
				 		: MyColor.setAlpha(MyColor.windowBlue, this.alpha)));
		}
		else{g2d.setColor(color);}		
	}

	/** 
	 ** 	Level-Anpassung
	 **/
	
	public static void adaptToLevel(Helicopter helicopter, int level, boolean isRealLevelUp)
	{		
		if(level == 1)
		{
			maxNr = 2;
			bossSelection = null;
			selection = 3;
			maxBarrierNr = 0;
			selectionBarrier = 1;
		}
		else if(level == 2){
			maxNr = 3;}
		else if(level == 3){selection = 6;}
		else if(level == 4){selection = 10; maxBarrierNr = 1;}
		else if(level == 5){selection = 15;}
		else if(level == 6)
		{			
			creationStop = false;
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
			creationStop = true;
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
			
			if(( helicopter.isPlayedWithoutCheats || Events.SAVE_ANYWAY)
				 && !Events.hasAnyBossBeenKilledBefore())
			{
				Menu.unlock(HELIOS);
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
			creationStop = false;
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
			creationStop = true;
			bossSelection = BOSS_2;
			selection = 0;
			helicopter.powerUpDecay();
			if((helicopter.isPlayedWithoutCheats ||Events.SAVE_ANYWAY) && !Events.reachedLevelTwenty[helicopter.getType().ordinal()])
			{
				Events.reachedLevelTwenty[helicopter.getType().ordinal()] = true;
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
			creationStop = false;
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
			creationStop = true;
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
			creationStop = false;
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
			creationStop = true;
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
			creationStop = false;
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
		else if(level == 50)
		{
			creationStop = true;
			bossSelection = FINAL_BOSS;
			selection = 0;
			helicopter.powerUpDecay();
		}
	}
	
	/** Methoden zur Erstellung von Gegnern
	 */	
	
	public static void generateNewEnemies(EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy, Helicopter helicopter)
	{
		Events.lastCreationTimer++;
		if(lastCarrier != null){
			createCarrierServants(helicopter, enemy);}
		else if(creationStop){
			verifyCreationStop(enemy, helicopter);}
		if(bossServantCreationApproved()){
			createBossServant(helicopter, enemy);}
		else if(enemyCreationApproved(enemy)){creation(helicopter, enemy);}
	}
	
	private static void createCarrierServants(Helicopter helicopter,
											  EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
	{
		for(int m = 0; 
				m < (lastCarrier.isMiniBoss
						? 5 + MyMath.random(3)
						: 2 + MyMath.random(2)); 
				m++)
			{
				creation(helicopter, enemy);
			}			
			lastCarrier = null;
	}

	private static void verifyCreationStop(EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy,
										   Helicopter helicopter)
	{
		if(	enemy.get(ACTIVE).isEmpty()
			&& lastCarrier == null
			&& !(helicopter.isPoweredUp()
				 && Events.isBossLevel()) )
		{
			creationStop = false;
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
										  EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
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
			EnemyTypes.getFinalBossServantTypes().forEach(type -> {
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
											EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
    {
    	makeBossTwoServants = false;
		creationStop = true;
		bossSelection = BOSS_2_SERVANT;
		maxNr = 12;
		for(int m = 0; m < maxNr; m++){creation(helicopter, enemy);}
    }
    
    private static void createAllBoss5Servants(Helicopter helicopter,
											   EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
    {
    	makeAllBoss5Servants = false;
    	EnemyTypes.getFinalBossServantTypes().forEach(type -> {
			bossSelection = type;
			creation(helicopter, enemy);
		});
    }	

    private static boolean enemyCreationApproved(EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
	{		
		int nrOfEnemies = enemy.get(ACTIVE).size();
		return !creationStop
				&&((Events.lastCreationTimer > 20  && !Events.isBossLevel()) ||
				   (Events.lastCreationTimer > 135 ) )
				&& nrOfEnemies < (maxNr + maxBarrierNr)
				&& MyMath.creationProbability(
						Events.isBossLevel()
							? 0
							: (maxNr + maxBarrierNr) - nrOfEnemies, 1)
				&& !(Events.level > 50)
				&& !(!enemy.get(ACTIVE).isEmpty()
						&& enemy.get(ACTIVE).getFirst().type.isMajorBoss());
	}
    
	private static void creation(Helicopter helicopter,
								 EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
	{
		Iterator<Enemy> i = enemy.get(INACTIVE).iterator();
		Enemy e;
		if(i.hasNext()){e = i.next(); i.remove();}	
		else{e = new Enemy();}
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
			this.farbe1 = MyColor.dimColor(this.farbe1, 1.3f);
			this.farbe2 = MyColor.dimColor(this.farbe1, this.dimFactor);
		}		
		if(this.canBecomeMiniBoss()){this.turnIntoMiniBoss(helicopter);}
		this.rewardModifier = this.isBoss() ? 0 : 5 - MyMath.random(11);
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
			this.graphics[i] = getGraphics(this.image[i]);			
			
			//this.graphics[i].setComposite(AlphaComposite.Src);
			
			this.paintImage(this.graphics[i], 1-2*i, null, true);
			if(this.cloakingTimer != DISABLED && helicopter.getType() == OROCHI)
			{
				BufferedImage 
					 tempImage = new BufferedImage((int)(1.028f * this.paintBounds.width),
							 						(int)(1.250f * this.paintBounds.height),
							 						BufferedImage.TYPE_INT_ARGB);
				
				this.image[2+i] = new BufferedImage((int)(1.028f * this.paintBounds.width),
													(int)(1.250f * this.paintBounds.height),
													BufferedImage.TYPE_INT_ARGB);
				
				this.paintImage(getGraphics(tempImage), 1-2*i, Color.red, true);
				(getGraphics(this.image[2+i])).drawImage(tempImage, ROP_CLOAKED, 0, 0);
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
		this.direction.setLocation(-1, MyMath.randomDirection());
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
		this.isExplodable = false;
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
		this.tractor = AbilityStatusTypes.DISABLED;
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
				&& (MyMath.tossUp(0.35f)
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
		this.isClockwiseBarrier = MyMath.tossUp();
				
		if(this.type == BARRIER_0 || this.type == BARRIER_1)
		{
			this.farbe1 = MyColor.bleach(Color.green, 0.6f);
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
			this.farbe1 = MyColor.bleach(Color.yellow, 0.6f);
			this.targetSpeedLevel.setLocation(0, 1 + 2*Math.random());
			this.setVarWidth(65);
			
			this.rotorColor = 2;
			this.staticChargeTimer = READY;
			this.isLasting = true;
		}
		// Level 15
		else if(this.type == BARRIER_3)
		{
			this.farbe1 = MyColor.bleach(new Color(255, 192, 0), 0.0f);
			this.targetSpeedLevel.setLocation(0.5 + 2*Math.random(), 0);
			this.setVarWidth(105);
			if(this.targetSpeedLevel.getX() >= 5){this.direction.x = 1;}

			this.setLocation(this.targetSpeedLevel.getX() >= 5
									? -this.bounds.getWidth()-APPEARANCE_DISTANCE
									: this.bounds.getX(),
							  GROUND_Y - this.bounds.getWidth() - (5 + MyMath.random(11)));
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
				= MyMath.tossUp(SPIN_SHOOTER_RATE) && Events.level >= MIN_SPIN_SHOOTER_LEVEL
					? MyMath.randomDirection()*(this.shootingRate /3 + MyMath.random(10))
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
			this.farbe1 = MyColor.bleach(Color.green, 0.6f);
			this.setVarWidth(80);
			
			this.isLasting = true;
		}
		// Level 44
		else if(this.type == BARRIER_7)
		{
			this.farbe1 = MyColor.bleach(MyColor.cloaked, 0.6f);
			this.setVarWidth(100);
						
			this.barrierTeleportTimer = READY;
			this.setBarrierShootingProperties();
			this.startBarrierUncloaking(helicopter);
						
			this.hasYPosSet = true;
			this.callBack = 1 + MyMath.random(4);
		}
		
		this.farbe2 = MyColor.dimColor(this.farbe1, 0.75f);
		this.deactivationProb = 1.0f / this.type.getStrength();
				
		if(Events.timeOfDay == NIGHT)
		{
			this.farbe1 = MyColor.dimColor(this.farbe1, MyColor.BARRIER_NIGHT_DIM_FACTOR);
			this.farbe2 = MyColor.dimColor(this.farbe2, MyColor.BARRIER_NIGHT_DIM_FACTOR);
		}		
		barrierTimer = (int)((helicopter.bounds.getWidth() + this.bounds.getWidth())/2);
	}
    
    private void assignRandomBarrierType()
    {
        int randomBarrierSelectionModifier = isBarrierFromFutureCreationApproved()
            ? MyMath.random(3)
            : 0;
        int selectedBarrierIndex = MyMath.random(Math.min(selectionBarrier + randomBarrierSelectionModifier, EnemyTypes.getBarrierTypes().size()));
        this.type = (EnemyTypes) EnemyTypes.getBarrierTypes().toArray()[selectedBarrierIndex];
    }
    
    private boolean isBarrierFromFutureCreationApproved()
    {
        return MyMath.tossUp(0.05f) && Events.level >= MIN_FUTURE_LEVEL;
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
				&& MyMath.tossUp(ROCK_PROB);
	}
	
	private void createRock(Helicopter helicopter)
	{
		currentRock = this;
		this.type = ROCK;
		this.model = CARGO;	
		helicopter.numberOfEnemiesSeen--;
		this.farbe1 = new Color((180 + MyMath.random(30)), (120 + MyMath.random(30)),(0 + MyMath.random(15)));
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
				&& MyMath.tossUp(KABOOM_PROB);
	}
	
	private void createKaboom(Helicopter helicopter)
	{
		this.type = KABOOM;
		this.farbe1 = Color.white;
		this.hitpoints = Integer.MAX_VALUE;	
		this.setVarWidth(KABOOM_WIDTH);
		helicopter.numberOfEnemiesSeen--;
		this.targetSpeedLevel.setLocation(0.5 + 0.5*Math.random(), 0); //d
		this.isExplodable = true;
		this.setInitialY(GROUND_Y - 2*this.bounds.getWidth()*HEIGHT_FACTOR);
	}
	
	private void createStandardEnemy()
	{
		this.type = enemySelector.getType(MyMath.random(selection));
		//this.type = CARRIER;

		switch(this.type)
		{
			// Level 1
			case TINY:
				this.farbe1 = new Color((180 + MyMath.random(30)),
						(120 + MyMath.random(30)),
						(0 + MyMath.random(15)));
				this.hitpoints = 2;
				this.setVarWidth(110);
				this.targetSpeedLevel.setLocation(0.5 + Math.random(), //d
						0.5 * Math.random());	//d
				this.isExplodable = true;
				this.dimFactor = 1.2f;
				
				break;

			// Level 3
			case SMALL:
				this.farbe1 = new Color((140 + MyMath.random(25)),
						(65 + MyMath.random(35)),
						(0 + MyMath.random(25)));
				this.hitpoints = 3 + MyMath.random(3);
				this.setVarWidth(125);
				this.targetSpeedLevel.setLocation(1 + 1.5*Math.random(), //d
						0.5*Math.random());	//d
				this.isExplodable = true;
				
				break;

			// level 5
			case RUNABOUT:
				this.farbe1 = new Color((100 + MyMath.random(30)),
						(100 + MyMath.random(30)),
						(40 + MyMath.random(25)));
				this.hitpoints = 2 + MyMath.random(2);
				this.setVarWidth(100);
				this.targetSpeedLevel.setLocation(2 + 2*Math.random(), //d
						2.5 + 1.5*Math.random());		//d
				this.isExplodable = true;
				
				break;

			// Level 7
			case FREIGHTER:
				this.model = CARGO;
				this.farbe1 = new Color((100 + MyMath.random(30)),
						(50 + MyMath.random(30)),
						(45 + MyMath.random(20)));
				this.setHitpoints(25);
				this.setVarWidth(145);
				this.targetSpeedLevel.setLocation(0.5 + Math.random(), //d
						0.5*Math.random());	//d
				this.canEarlyTurn = true;
				this.canTurn = true;
				
				break;

			// Level 11
			case BATCHWISE:
				this.farbe1 = new Color((135 + MyMath.random(30)),
						(80+MyMath.random(20)),
						(85 + MyMath.random(30)));
				this.setHitpoints(16);
				this.setVarWidth(130);
				this.targetSpeedLevel.setLocation(7 + 4*Math.random(), //d
						1 + 0.5*Math.random()); //d
				this.batchWiseMove = 1;
				
				break;

			// Level 13
			case SINUS:
				this.farbe1 = new Color((185 + MyMath.random(40)),
						( 70 + MyMath.random(30)),
						(135 + MyMath.random(40)));
				this.setHitpoints(6);
				this.setVarWidth(110);
				this.targetSpeedLevel.setLocation(2.5 + 2.5*Math.random(), 11); //d

				this.setInitialY(TURN_FRAME.getCenterY());
				this.canSinusMove = true;
				this.isExplodable = true;
				
				break;

			// Level 16
			case DODGER:
				this.farbe1 = new Color((85 + MyMath.random(20)),
						(35 + MyMath.random(30)),
						(95 + MyMath.random(30)));
				this.setHitpoints(24);
				this.setVarWidth(170);
				this.targetSpeedLevel.setLocation(1.5 + 1.5*Math.random(), //d
						0.5*Math.random());	//d
				this.canDodge = true;
				
				break;

			// Level 21
			case CHAOS:
				this.farbe1 = new Color((150 + MyMath.random(20)),
						(130 + MyMath.random(25)),
						( 75 + MyMath.random(30)));
				this.setHitpoints(22);
				this.setVarWidth(125);
				this.targetSpeedLevel.setLocation( 3.5 + 1.5*Math.random(), //d
						6.5 + 2*Math.random());	//d
				this.canMoveChaotic = true;
				this.isExplodable = true;
				
				break;

			// Level 24
			case CALLBACK:
				this.farbe1 = new Color((70 + MyMath.random(40)),
						(130 + MyMath.random(50)),
						(30 + MyMath.random(45)));
				this.setHitpoints(30);
				this.setVarWidth(95);
				this.targetSpeedLevel.setLocation( 5.5 + 2.5*Math.random(), //d
						5 + 2*Math.random());		//d
				this.isExplodable = true;
				this.callBack = 1;
				
				break;

			// Level 26
			case SHOOTER:
				this.model = CARGO;

				this.farbe1 = new Color(80 + MyMath.random(25),
						80 + MyMath.random(25),
						80 + MyMath.random(25));
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

				this.farbe1 = MyColor.cloaked;
				this.setHitpoints(100);
				this.setVarWidth(85);
				this.targetSpeedLevel.setLocation( 0.5 + Math.random(), //d
						1 + 0.5*Math.random());	//d
				this.canLearnKamikaze = true;
				this.canInstantTurn = true;
				this.cloakingTimer = CLOAKING_TIME + CLOAKED_TIME;
				this.uncloakingSpeed = 2;
				this.canEarlyTurn = true;
				this.isExplodable = true;
				
				break;

			// Level 35
			case BOLT:
				this.createScamperingVessel(lastCarrier != null);
				break;

			case CARRIER:
				this.model = CARGO;

				this.farbe1 = new Color(70 + MyMath.random(15),
						60 + MyMath.random(10),
						45 + MyMath.random(10)); // new Color(25 + MyMath.random(35), 70 + MyMath.random(45), 25 + MyMath.random(35));
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
				this.farbe1 = new Color((180 + MyMath.random(50)),
						(230 + MyMath.random(20)),
						(20 + MyMath.random(60)));
				this.setHitpoints(140);
				this.setVarWidth(115);
				this.targetSpeedLevel.setLocation( 4 + 2.5 * Math.random(), //d
						0.5 + Math.random());		//d
				this.isExplodable = true;
				this.canChaosSpeedup = true;
				this.canDodge = true;
				
				break;

			// Level 41
			case AMBUSH:
				this.farbe1 = new Color( 30 + MyMath.random(40),
						60 + MyMath.random(40),
						120 + MyMath.random(40));
				this.setHitpoints(150);
				this.setVarWidth(95);
				this.targetSpeedLevel.setLocation( 1 + 1.5*Math.random(), 0); //d

				this.isExplodable = true;
				this.speedup = READY;
				
				break;

			 // Level 43
			case LOOPING:
				this.farbe1 = MyColor.cloaked;
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
				this.farbe1 = new Color(  5 + MyMath.random(55),
						105 + MyMath.random(40),
						90 + MyMath.random(30));
				this.setHitpoints(520);
				this.setVarWidth(115);
				this.targetSpeedLevel.setLocation( 2.5 + 2*Math.random(), //d
						4.5 + 1.5*Math.random());//d
				this.tractor = AbilityStatusTypes.READY;
				this.isExplodable = true;
				
				break;

			// Level 46
			case TELEPORTER:
				this.model = CARGO;

				this.farbe1 = new Color(190 + MyMath.random(40),
						10 + MyMath.random(60),
						15 + MyMath.random(60));
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
		
		this.farbe1 = new Color(75 + MyMath.random(30), 
								75 + MyMath.random(30), 
								75 + MyMath.random(30) );
		this.setHitpoints(26);
		this.setVarWidth(70);
		
		if(explosionCreation)
		{
			this.setLocation(lastCarrier.bounds.getCenterX(),
							  lastCarrier.bounds.getCenterY());
			this.hasYPosSet = true;
		}
		this.isExplodable = true;
		if(explosionCreation)
		{
			this.targetSpeedLevel.setLocation( 10 + 7.5*Math.random(), //d
													0.5 + 3*Math.random());			//d	
			this.callBack = 1 + MyMath.random(3);
			this.direction.x = MyMath.randomDirection();
			this.invincibleTimer = 67;
		}
		else 
		{
			this.targetSpeedLevel.setLocation( 12 + 3.5*Math.random(), //d
													0.5 + 3*Math.random());		//d			
			if(MyMath.tossUp()){this.callBack = 1;}
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
			this.farbe1 = new Color(80 + MyMath.random(25), 80 + MyMath.random(25), 80 + MyMath.random(25));
			this.hitpoints = 15;					
			this.targetSpeedLevel.setLocation(3 + 10.5*Math.random(), //d
												  3 + 10.5*Math.random()); //d
		
			this.direction.x = MyMath.randomDirection();
			this.invincibleTimer = 67;
		}
		// Level 30
		else if( this.type == BOSS_3)
		{			
			this.setWidth(250);
			this.farbe1 = MyColor.cloaked;
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
								85 + MyMath.random(15),
							    this.bounds.getHeight());
			this.hasYPosSet = true;
			this.farbe1 = new Color(80 + MyMath.random(20), 80 + MyMath.random(20), 80 + MyMath.random(20));
			this.hitpoints = 100 + MyMath.random(50);					
			this.targetSpeedLevel.setLocation(6 + 2.5*Math.random(), //d
												  6 + 2.5*Math.random()); //d
			this.direction.x = MyMath.randomDirection();
			this.isExplodable = true;
		}	
		// Level 50
		else if(this.type == FINAL_BOSS)
		{			
			this.setInitialBounds(this.bounds.getX(),
								    98,
								    FINAL_BOSS_WIDTH,
								    FINAL_BOSS_WIDTH * HEIGHT_FACTOR);
						
			this.farbe1 = MyColor.brown;
			this.hitpoints = 25000;	
			this.targetSpeedLevel.setLocation(23.5, 0); //d

			maxNr = 5;
			this.operator = new FinalEnemysOperator();
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
				this.direction.x = MyMath.randomDirection();
				
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
				this.farbe1 = MyColor.cloaked;
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
				this.isClockwiseBarrier = MyMath.tossUp();
				this.farbe1 = MyColor.bleach(new Color(170, 0, 255), 0.6f);
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
				this.farbe2 = MyColor.dimColor(this.farbe1, 0.75f);		
				if(Events.timeOfDay == NIGHT)
				{
					this.farbe1 = MyColor.dimColor(this.farbe1, MyColor.BARRIER_NIGHT_DIM_FACTOR);
					this.farbe2 = MyColor.dimColor(this.farbe2, MyColor.BARRIER_NIGHT_DIM_FACTOR);
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
				&& MyMath.tossUp(miniBossProb)
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
		this.isExplodable = false;
		this.callBack += 2;
		this.canTurn = true;
		if(  (this.type.isCloakableAsMiniBoss() && !this.canLearnKamikaze && MyMath.tossUp(0.2f)) ||
		      this.shootTimer == 0 )
		{
			this.cloakingTimer = 0;
		}		
	}

	private void placeNearHelicopter(Helicopter helicopter)
	{		
		boolean isLeftOfHelicopter = !(helicopter.bounds.getMaxX() + (0.5f * this.bounds.getWidth() + BARRIER_DISTANCE) < 1024);
					
		int x, 
			y = (int)(helicopter.bounds.getY() 
				+ helicopter.bounds.getHeight()/2
				- this.bounds.getWidth()
				+ Math.random()*this.bounds.getWidth());
		
		if(isLeftOfHelicopter)
		{
			x = (int)(helicopter.bounds.getX() 
				-3*this.bounds.getWidth()/2  
				- 10
				+ Math.random()*(this.bounds.getWidth()/3));	
		}
		else
		{
			x = (int)(helicopter.bounds.getMaxX() 
				+ BARRIER_DISTANCE);		
		}
		this.setLocation(x,
						  Math.max(0, Math.min(y, GROUND_Y-this.bounds.getWidth())));
	}
	
	private void setBarrierShootingProperties()
	{
		if(this.barrierTeleportTimer != DISABLED || this.borrowTimer != DISABLED)
		{
			this.shootingRate = 35 + MyMath.random(15);
		}
		else
		{
			this.shootingRate = 25 + MyMath.random(25);
		}
			
		if(this.barrierTeleportTimer == DISABLED){this.shootPause = 2 * this.shootingRate + 20 + MyMath.random(40);}
		this.shotsPerCycle = 2 + MyMath.random(9);
		this.shootingCycleLength = this.shootPause + this.shootingRate * this.shotsPerCycle;
		this.shotSpeed = 5 + MyMath.random(6);
		if(this.barrierTeleportTimer != DISABLED || (MyMath.tossUp(0.35f) && Events.level >= MIN_BUSTER_LEVEL))
		{
			if(this.barrierTeleportTimer == DISABLED){this.farbe1 = MyColor.bleach(new Color(170, 0, 255), 0.6f);}
			this.shotType = BUSTER;
		}
		else
		{
			this.farbe1 = MyColor.bleach(Color.red, 0.6f);
			this.shotType = DISCHARGER;
		}
	}

	private void setHitpoints(int hitpoints)
	{
		this.hitpoints = hitpoints + MyMath.random(hitpoints/2);		
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
		setWidth(width + MyMath.random(width/(this.model == BARRIER ? 5 : 10)));
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

	private static Graphics2D getGraphics(BufferedImage bufferedImage)
	{
		Graphics2D g2d = (Graphics2D)bufferedImage.getGraphics();
		g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);	
		return g2d;
	}

	/* Die folgende Funktion reguliert die Gegner-Bewegung:
	 * 1.  Unter Berücksichtigung jeglicher Eventualitäten (special_manoever, ausweichen, etc.)
	 *	   werden die neuen Koordinaten berechnet.
	 * 2.  Der Gegner wird an Stelle seiner neuen Koordinaten gemalt.
	 */
		
	public static void updateAllActive(Controller controller,
									   Helicopter helicopter)
	{
		if(rockTimer > 0){
			rockTimer--;}
		if(BackgroundObject.backgroundMoves && barrierTimer > 0){
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

	private static void countBarriers(EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
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


	public static void paintAllActive(Graphics2D g2d,
									  Controller controller,
									  Helicopter helicopter)
	{
		for(int i = 0; i < currentNumberOfBarriers; i++)
		{    			
			livingBarrier[i].paint(g2d, helicopter);
		}
		for(Enemy enemy : controller.enemies.get(ACTIVE))
		{
			if(enemy.isVisableNonBarricadeVessel(helicopter.canDetectCloakedVessels()))
			{
				enemy.paint(g2d, helicopter);
			}
		}
	}
	
	private boolean isVisableNonBarricadeVessel(boolean hasRadarDevice)
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
		if(helicopter.getType() == PEGASUS){this.checkForEmpStrike(controller, helicopter);}
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

	private void calculateBossManeuver(EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
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
			MyMath.getIntersectionLength(	this.bounds.getMinX(),
											this.bounds.getMaxX(),
											barrier.bounds.getMinX(),
											barrier.bounds.getMaxX())										 
			<										 									 
			MyMath.getIntersectionLength(	this.bounds.getMinY(),
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
					
		// Chaosflug
		if(    this.canMoveChaotic
			&& this.chaosTimer == READY
			&& this.dodgeTimer == READY)
		{
			if( MyMath.tossUp(0.2f)
			    && this.type.isShieldMaker())
			{
				this.direction.x = -this.direction.x;
			}
			if( MyMath.tossUp(0.2f))
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
					&& helicopter.bounds.getMaxX() < this.bounds.getMinX() 
					&& this.bounds.getX() - helicopter.bounds.getX() < 620)
				||
				(this.direction.x == -1 
					&& this.bounds.getMaxX() < helicopter.bounds.getMinX() 
					&& helicopter.bounds.getX() - this.bounds.getX() < 620)))
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
		if(canTractor(helicopter)){
			startTractor(helicopter);}
				
		//Chaos-SpeedUp
		if(	this.canChaosSpeedup
			&& this.speedLevel.getX() == this.targetSpeedLevel.getX()
			&& helicopter.bounds.getX() - this.bounds.getX() > -350	)
		{				
			this.speedLevel.setLocation(6 + this.targetSpeedLevel.getX(), //d
										 this.speedLevel.getY());
		}
		if(this.canChaosSpeedup
		   && (helicopter.bounds.getX() - this.bounds.getX()) > -160)
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

	/*
	private void kaboom_action()
	{
		double speed = Math.max(1, 0.05*Math.abs(GROUND_Y-2*this.bounds.getHeight()-this.bounds.getY()));
		
		if( this.bounds.getMaxY() < GROUND_Y - this.bounds.getHeight())
		{
			this.direction.y = 1;				
		}
		else if( this.bounds.getMaxY() > GROUND_Y - this.bounds.getHeight())
		{
			this.direction.y = -1;
		}			
		if(	this.direction.y *(   this.bounds.getMaxY() 
				 			    + this.direction.y * speed/2)  
				>= this.direction.y *(GROUND_Y - this.bounds.getHeight()))
		{
			this.speedLevel.setLocation(this.speedLevel.getX(), 0);
		}
		else
		{
			this.speedLevel.setLocation(this.speedLevel.getX(), speed);
		}		
	}
	*/

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
								   Helicopter helicopter)
	{
		if(helicopter.empWave != null)
		{
			if(this.isEmpShockable(helicopter))
			{
				this.empShock(controller, helicopter);
			}
		}
		else{this.isEmpShocked = false;}
	}

	private void empShock(Controller controller, Helicopter helicopter)
    {
    	this.takeDamage((int)( (this.type.isMajorBoss()
    							? EMP_DAMAGE_FACTOR_BOSS
    							: EMP_DAMAGE_FACTOR_ORDINARY) 
    						 * MyMath.dmg(helicopter.levelOfUpgrade[ENERGY_ABILITY.ordinal()])));
		this.isEmpShocked = true;
		if(this.type == BOSS_4){this.spawningHornetTimer = READY;}
		this.disableSiteEffects(helicopter);
				
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
			this.reactToHit(helicopter, null);
			
			Explosion.start(controller.explosions, helicopter,
							this.bounds.getCenterX(), 
							this.bounds.getCenterY(), STUNNING, false);
		}
		else
		{
			Audio.play(Audio.explosion2);
			helicopter.empWave.kills++;
			helicopter.empWave.earnedMoney += this.calculateReward(helicopter);
			this.die(controller, helicopter, null, false);
		}
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
			rockTimer = ROCKFREE_TIME;
		}
		else if(this.isMiniBoss)
		{
			currentMiniBoss = null;
		}		
	}	
	
	private boolean isEmpShockable(Helicopter helicopter)
	{
		return     !this.isEmpShocked
				&& !this.isDestroyed
				&& !this.isInvincible()
				&& !(this.barrierTeleportTimer != DISABLED && this.barrierShootTimer == DISABLED)
				&& helicopter.empWave.ellipse.intersects(this.bounds);
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
			if(this.bounds.getY() < helicopter.bounds.getY()){this.direction.y = 1;}
			else{this.direction.y = -1;}	
		}		
	}
	
	private boolean atEyeLevel(Helicopter helicopter)
	{
		return this.bounds.intersects(Integer.MIN_VALUE/2, 
									  helicopter.bounds.getY(),
									  Integer.MAX_VALUE,
									  helicopter.bounds.getHeight());
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
				if(MyMath.tossUp(RETURN_PROB[serantType])
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
	
	private static int id(EnemyTypes type)
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
    		&& MyMath.tossUp(0.008f)
    		&& ( (this.bounds.getMinX() > helicopter.bounds.getMaxX() 
    			   && this.direction.x == 1) 
    			 ||
    			 (helicopter.bounds.getMinX() > this.bounds.getMaxX()
    			   && this.direction.x == -1)))		
    	{
			this.direction.x = -this.direction.x;	
			this.speedLevel.setLocation(0, this.speedLevel.getY());
		}		
		
    	if(((this.bounds.getMaxX() > helicopter.bounds.getMinX() && this.direction.x == -1)&&
			(this.bounds.getMaxX() - helicopter.bounds.getMinX() ) < 620) ||
		   ((helicopter.bounds.getMaxX() > this.bounds.getMinX() && this.direction.x == 1)&&
			(helicopter.bounds.getMaxX() - this.bounds.getMinX() < 620)))		    
		{			
			if(!this.canLearnKamikaze)
			{
				this.speedLevel.setLocation((this.type == BOSS_4 || this.type == BOSS_3) ? 12 : 8, //d
											 this.speedLevel.getY());
			}						
			if(this.direction.y == 1 
				&& helicopter.bounds.getY()  < this.bounds.getY())				
			{							
				this.direction.y = -1;				
				this.speedLevel.setLocation(this.speedLevel.getX(), 0);
			}
			else if(this.direction.y == -1 
					&& helicopter.bounds.getMaxY() > this.bounds.getMaxY())
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
				     && MyMath.tossUp(0.004f))
				    || 
				    (this.type == PROTECTOR 
				     && (helicopter.bounds.getX() > boss.getX() - 225) ))) 
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
			&& MyMath.tossUp(0.1f)
			&& this.bounds.getX() + this.bounds.getWidth() > 0
			&& !(this.cloakingTimer > CLOAKING_TIME && this.cloakingTimer <= CLOAKING_TIME + CLOAKED_TIME)
			&& ((this.direction.x == -1 
				 && helicopter.bounds.intersects(	
						 				this.bounds.getX() + Integer.MIN_VALUE/2, 
						 				this.bounds.getY() + (this.model == TIT ? 0 : this.bounds.getWidth()/2) - 15,
						 				Integer.MAX_VALUE/2, 
						 				EnemyMissile.DIAMETER+30))
				||
				((this.direction.x == 1 
				  && helicopter.bounds.intersects(
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
				|| (this.type == BIG_SHIELD_MAKER && MyMath.tossUp());
	}

	private void evaluateBarrierShooting(Controller controller,
										 Helicopter helicopter)
	{
		if(this.barrierShootTimer == 0)
		{
			this.barrierShootTimer = this.shootingCycleLength;
			if(	this.shotRotationSpeed == 0
				&&	  (helicopter.bounds.getX()    < this.bounds.getX()         && this.shootingDirection.getX() > 0)
					||(helicopter.bounds.getMaxX() > this.bounds.getMaxX() && this.shootingDirection.getX() < 0) )
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
						( (helicopter.bounds.getX() + (helicopter.isMovingLeft ? Helicopter.FOCAL_PNT_X_LEFT : Helicopter.FOCAL_PNT_X_RIGHT))
							  - (this.bounds.getX() +       this.bounds.getWidth()/2)), 
						  (helicopter.bounds.getY() + Helicopter.FOCAL_PNT_Y_EXP) 
						  	  - (this.bounds.getY() +       this.bounds.getHeight()/2)) ;
				float distance = (float) MyMath.ZERO_POINT.distance(this.shootingDirection);
				this.shootingDirection.setLocation(this.shootingDirection.getX()/distance,
													this.shootingDirection.getY()/distance);
			}
			this.shoot(controller.enemyMissiles, this.shotType, this.shotSpeed);
		}				
		this.barrierShootTimer--;
	}
	
	public void shoot(EnumMap<CollectionSubgroupTypes, LinkedList<EnemyMissile>> enemyMissiles, EnemyMissileTypes missileType, double missileSpeed)
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
		else if(this.barrierTeleportTimer == READY && MyMath.tossUp(0.004f))
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

	private void boss4Action(EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
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
			   	    || MyMath.tossUp(0.02f)))
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
	
	private boolean canTractor(Helicopter helicopter)
	{		
		return isTractorReady() && helicopter.canBeTractored();
	}

	private boolean isTractorReady() {
		return this.tractor == AbilityStatusTypes.READY
				&& !this.isEmpSlowed()
				&& this.cloakingTimer < 1
				&& this.bounds.getMaxX() < 982;
	}


	private void startTractor(Helicopter helicopter)
	{
		Audio.loop(Audio.tractorBeam);
		this.tractor = AbilityStatusTypes.ACTIVE;
		this.speedLevel.setLocation(ZERO_SPEED);
		helicopter.tractor = this;
		this.direction.x = -1;		
	}
	
	public void stopTractor()
	{
		this.tractor = AbilityStatusTypes.DISABLED;
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
							  + MyMath.random((int)(EXTRA_INACTIVE_TIME_FACTOR * INACTIVATION_TIME))
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
		if(!this.speed.equals(ZERO_SPEED)|| BackgroundObject.backgroundMoves)
		{
			this.setLocation(
					this.bounds.getX() 
						+ this.direction.x * this.speed.getX() 
						- (BackgroundObject.backgroundMoves ? BG_SPEED : 0),
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
	
	public static void paintAllDestroyed(Graphics2D g2d,
										 Controller controller,
										 Helicopter helicopter)
	{
		for(Enemy e : controller.enemies.get(DESTROYED))
		{
			e.paint(g2d, helicopter);
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

	private void updateDead(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion, Helicopter helicopter)
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
	
	private void handleCrashToTheGround(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion,
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
	
	boolean isLivingBoss()
	{		
		return this.isBoss() && !this.isDestroyed;
	}

	private void collision(Controller controller, Helicopter helicopter)
	{
		boolean playCollisionSound = this.collisionAudioTimer == READY;
		helicopter.beAffectedByCollisionWith(this, controller, playCollisionSound);
				
		if(playCollisionSound)
		{
			this.collisionAudioTimer = Helicopter.NO_COLLISION_DMG_TIME;
		}		
		this.collisionDamageTimer = Helicopter.NO_COLLISION_DMG_TIME;
			
		if(	this.isExplodable
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
			
			if(	helicopter.hasShortrangeRadiation
				&& !(this.type == KABOOM))
			{
				this.rewardFor(controller.powerUps,
								null, 
								helicopter, 
								helicopter.hasPerformedTeleportKill());
			}
			this.destroy(helicopter);
		}				
		if(	helicopter.currentPlating <= 0
			&& !helicopter.isDamaged)
		{
			helicopter.crash();
		}		
	}
	
	public void reactToRadiation(Controller controller, Helicopter helicopter)
	{
		if(	this.teleportTimer == READY){this.teleport();}
		else if(this.canTakeCollisonDamage())
		{
			this.takeDamage((int)(
				helicopter.currentBaseFirepower
				* (helicopter.bonusKillsTimer > NICE_CATCH_TIME - TELEPORT_KILL_TIME
					? TELEPORT_DAMAGE_FACTOR 
					: RADIATION_DAMAGE_FACTOR)));				
							
			if(this.model == BARRIER)
			{
				if(	helicopter.hasTripleDmg()
					&&  MyMath.tossUp(
							this.deactivationProb
							*(helicopter.bonusKillsTimer
								> NICE_CATCH_TIME
								  - TELEPORT_KILL_TIME ? 2 : 1)))
				{
					this.hitpoints = 0;
				}
				else if(MyMath.tossUp(this.deactivationProb *(helicopter.bonusKillsTimer > NICE_CATCH_TIME - TELEPORT_KILL_TIME ? 4 : 2)))
				{
					this.snooze(true);
				}
			}
			if(this.hasHPsLeft()){this.reactToHit(helicopter, null);}
			else
			{
				boolean beamKill = helicopter.bonusKillsTimer > 0;
				this.die(controller, helicopter, null, beamKill);
			}
		}		
	}

	private boolean canTakeCollisonDamage()
	{		
		return 	   !this.isDestroyed
				&& !this.isExplodable
				&& !this.isInvincible()
				&& !(this.barrierTeleportTimer != DISABLED && this.barrierShootTimer == DISABLED)
				&& this.collisionAudioTimer == READY;
	}
	
	public float collisionDamage(Helicopter helicopter)
	{		
		return helicopter.getProtectionFactor()
			   *(helicopter.isPowerShieldActivated && this.isExplodable ? 0.65f : 1.0f)
			   *(this.type == KABOOM && !this.isDestroyed && !helicopter.hasShortrangeRadiation
			     ? helicopter.kaboomDmg()
			     : (this.isExplodable && !this.isInvincible() && !this.isDestroyed)
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
	
	public void hitByMissile(Helicopter helicopter, Missile missile, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
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
				&& MyMath.tossUp(	0.5f
									* this.deactivationProb
									* (( (missile.typeOfExplosion == JUMBO
											|| missile.typeOfExplosion == PHASE_SHIFT)
										  && missile.extraDamage) ? 2 : 1)))
			{
				this.hitpoints = 0;
			}
			else if(MyMath.tossUp(this.deactivationProb *(missile.typeOfExplosion == PLASMA ? 2 : 1)))
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
	
	private void stun(Helicopter helicopter, Missile missile, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
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
			     if(this.bounds.getMinX() > helicopter.bounds.getMinX()){this.direction.x = -1;}
			else if(this.bounds.getMaxX() < helicopter.bounds.getMaxX()){this.direction.x = 1;}				
		}
		if(this.type == BOSS_4){this.spawningHornetTimer = READY;}
		
		if(this.cloakingTimer == READY && !(this.tractor == AbilityStatusTypes.ACTIVE))
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
					&& MyMath.tossUp(this.isMiniBoss
										? this.isCarrier ? 0.2f : 0.5f
										: this.isCarrier ? 0.1f : 0.25f);
	}

	private void explode(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion, Helicopter helicopter, Missile missile)
	{
		explode(explosion, helicopter, missile.speed, missile.typeOfExplosion, missile.extraDamage);
	}	
	void explode(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion, Helicopter helicopter)
	{
		explode(explosion, helicopter, 0, ORDINARY, false);
	}	
	private void explode(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion, Helicopter helicopter, double missileSpeed, ExplosionTypes explosionType, boolean extraDamage)
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
				 EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp,
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
		this.farbe1 = MyColor.dimColor(this.farbe1, MyColor.DESTRUCTION_DIM_FACTOR);
		this.farbe2 = MyColor.dimColor(this.farbe2, MyColor.DESTRUCTION_DIM_FACTOR);	
		
		this.repaint();
	
		if(helicopter.tractor != null && helicopter.tractor == this)
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
		this.farbe1 = MyColor.setAlpha(this.farbe1, 255);
		this.farbe2 = MyColor.setAlpha(this.farbe2, 255);
		this.cloakingTimer = nextCloakingState;
	}

	public void die(Controller controller, Helicopter helicopter,
					Missile missile, boolean beamKill)
	{		
		this.rewardFor(controller.powerUps, missile, helicopter, beamKill);
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

	private void rewardFor(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp,
						   Missile missile,
						   Helicopter helicopter,
						   boolean beamKill)
	{																					   
		if(helicopter.getType() != HELIOS)
		{
			Events.lastBonus = this.calculateReward(helicopter);
			Events.money += Events.lastBonus;
			Events.overallEarnings += Events.lastBonus;
			Events.lastExtraBonus = 0;
			if(missile != null				
				&& (helicopter.getType() == ROCH || helicopter.getType() == OROCHI))
			{
				if(missile.kills > 0
				   && helicopter.hasPiercingWarheads
				   && (     Missile.canTakeCredit(missile.sister[0], this)
						 || Missile.canTakeCredit(missile.sister[1], this)))
				{
					if(Missile.canTakeCredit(missile.sister[0], this))
					{
						missile.sister[0].credit();
					}
					else if(Missile.canTakeCredit(missile.sister[1], this))
					{
						missile.sister[1].credit();
					}	
				}
				else
				{
					missile.credit();
				}				
			}
			else if(beamKill)
			{
				helicopter.bonusKills++;
				helicopter.bonusKillsMoney += Events.lastBonus;
			}
			else if(helicopter.getType() == KAMAITACHI)
			{
				helicopter.bonusKillsTimer +=SPEED_KILL_BONUS_TIME;
				helicopter.bonusKills++;
				helicopter.bonusKillsMoney += Events.lastBonus;
			}
			Menu.moneyDisplayTimer = 0;
		}
		if(this.canCountForKillsAfterLevelUp())
		{
			Events.killsAfterLevelUp++;
		}		
		if(this.canDropPowerUp()){this.dropPowerUp(helicopter, powerUp);}
		if(this.isMiniBoss){Audio.play(Audio.applause2);}
	}
    
    private boolean canCountForKillsAfterLevelUp()
    {
        return this.type != BOSS_4_SERVANT && !this.type.isFinalBossServant();
    }
    
    private boolean canDropPowerUp()
	{		
		return this.model != BARRIER
			   &&( (!Events.isBossLevel()
				    &&( ( MyMath.tossUp(POWER_UP_PROB)
						  && Events.level >= MIN_POWER_UP_LEVEL) 
						|| this.isMiniBoss))
				|| this.type == BOSS_1
				|| this.type == BOSS_3
				|| this.type == BOSS_4 );
	}
	
	private void dropPowerUp(Helicopter helicopter,
							 EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp)
	{
		PowerUp.activate(helicopter, 
				 powerUp, 
				 this, 
				 MyMath.tossUp(0.14f)
					? REPARATION
					: PowerUpTypes.values()[MyMath.random(this.type.isMajorBoss() ? 5 : 6)], false);
		
	}
	
	private int calculateReward(Helicopter helicopter)
	{		
		return this.getEffectiveStrength()
		        * (helicopter.hasSpotlights
					? Events.NIGHT_BONUS_FACTOR 
					: Events.DAY_BONUS_FACTOR)
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
											   EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy,
											   EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
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
			Events.level = 51;
			Events.maxLevel = Events.level;
			helicopter.isDamaged = true;
		
			helicopter.destination.setLocation(helicopter.bounds.getX()+40, 
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
			this.isUpperShieldMaker = MyMath.tossUp();
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
	
	public void startStaticDischarge(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion,
									 Helicopter helicopter)
	{
		this.staticChargeTimer = STATIC_CHARGE_TIME;
		helicopter.receiveStaticCharged(2.5f);
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
				&& !( (     (helicopter.bounds.getX() - this.bounds.getMaxX() > -500)
						 && (helicopter.bounds.getX() - this.bounds.getX() 	  <  150))	
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
										   EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy,
										   EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
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
}