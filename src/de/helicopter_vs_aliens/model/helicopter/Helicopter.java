package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.TimesOfDay;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpTypes;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.INACTIVE;
import static de.helicopter_vs_aliens.control.TimesOfDay.DAY;
import static de.helicopter_vs_aliens.control.TimesOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.WindowTypes.GAME;
import static de.helicopter_vs_aliens.gui.WindowTypes.STARTSCREEN;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelTypes.BARRIER;
import static de.helicopter_vs_aliens.model.enemy.EnemyTypes.KABOOM;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.Pegasus.INTERPHASE_GENERATOR_ALPHA;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.NICE_CATCH_TIME;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.TELEPORT_KILL_TIME;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.*;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.*;
import static de.helicopter_vs_aliens.util.dictionary.Languages.ENGLISH;


public abstract class Helicopter extends MovingObject
{			
	// Konstanten
    public static final int
		POWERUP_DURATION = 930,         // Zeit [frames] welche ein eingesammeltes PowerUp aktiv bleibt
    	RECENT_DMG_TIME = 50,
		SLOW_TIME = 100,
		NO_COLLISION_DMG_TIME	= 20,   // Zeitrate, mit der Helicopter Schaden durch Kollisionen mit Gegnern nehmen kann
    	FIRE_RATE_POWERUP_LEVEL = 3,    // so vielen zusätzlichen Upgrades der Feuerrate entspricht die temporäre Steigerung der Feuerrate durch das entsprechende PowerUp
    	NR_OF_TYPES = HelicopterTypes.values().length,                // so viele Helikopter-Klassen gibt es
    	INVULNERABILITY_DAMAGE_REDUCTION = 80,        // %-Wert der Schadensreduzierung bei Unverwundbarleit
    	ENERGY_DRAIN = 45,              // Energieabzug für den Helikopter bei Treffer
    	REDUCED_ENERGY_DRAIN = 10,
		STANDARD_PLATING_STRENGTH = 1,
		GOLIATH_PLATING_STRENGTH = 2,
		STANDARD_GOLIATH_COSTS = 75000,
		STANDARD_SPECIAL_COSTS = 125000,
		CHEAP_SPECIAL_COSTS = 10000;
	
	static final float
		// TODO wo wird das verwendet? Evtl. nicht mehr nötig?
		ENEMY_MISSILE_DAMAGE_FACTOR =  0.5f,
		STANDARD_MISSILE_DAMAGE_FACTOR =  1.0f;
    
    public static final double    	
		FOCAL_PNT_X_LEFT		= 39,
		FOCAL_PNT_X_RIGHT		= 83,
		FOCAL_PNT_Y_EXP			= 44,
		FOCAL_PNT_Y_POS	 		= 56;
        
    private static final int
    	NO_COLLISION_HEIGHT		= 6;
   
    private static final Dimension
		HELICOPTER_SIZE = new Dimension(122, 69);
    
    private static final Rectangle
    	INITIAL_BOUNDS = new Rectangle(	150, 
		    							GROUND_Y 
		    							- HELICOPTER_SIZE.height 
		    							- NO_COLLISION_HEIGHT, 
		    							HELICOPTER_SIZE.width, 
		    							HELICOPTER_SIZE.height);        
    	
    private static final float    
    	NOSEDIVE_SPEED = 12f,	// Geschwindigkeit des Helikopters bei Absturz
    	INVULNERABILITY_PROTECTION_FACTOR = 1.0f - INVULNERABILITY_DAMAGE_REDUCTION/100.0f;


	public int
		missileDrive,						// Geschwindigkeit [Pixel pro Frame] der Raketen
		currentBaseFirepower,					// akuelle Feuerkraft unter Berücksichtigung des Upgrade-Levels und des evtl. erforschten Jumbo-Raketen-Spezial-Upgrades
		timeBetweenTwoShots,				// Zeit [frames], die mindestens verstreichen muss, bis wieder geschossen werden kann
		shiftTime,							// nur Pegasus-Klasse: Zeit [frames], die verstreichen muss, bis der Interphasengenerator aktiviert wird
		platingDurabilityFactor,			// SpezialUpgrade; = 2, wenn erforscht, sonst = 1; Faktor, der die Standardpanzerung erhöht
		numberOfCannons,					// Anzahl der Kanonen; mögliche Werte: 1, 2 und 3
		
		// Timer
		plasmaActivationTimer,				// nur Kamaitachi-Klasse: Timer zur Überwachung der Zeit [frames], in der die Plasma-Raketen aktiviert sind
		empTimer,							// nur Pegasus-Klasse: Timer stellt sicher, dass eine Mindestzeit zwischen zwei ausgelösten EMPs liegt
		powerUpGeneratorTimer,
		slowedTimer,						// reguliert die Verlangsamung des Helicopters durch gegnerische Geschosse
		recentDamageTimer,					// aktiv, wenn Helicopter kürzlich Schaden genommen hat; für Animation der Hitpoint-Leiste
		interphaseGeneratorTimer,			// nur Pegasus-Klasse: Zeit [frames] seit der letzten Offensiv-Aktion; bestimmt, ob der Interphasengenerator aktiviert ist
		enhancedRadiationTimer,
		
		// für die Spielstatistik
		numberOfCrashes,					// Anzahl der Abstürze
		numberOfRepairs,					// Anzahl der Reparaturen
		missileCounter,						// Anzahl der abgeschossenen Raketen
		hitCounter,							// Anzahl der getroffenen Gegner
		numberOfEnemiesSeen,				// Anzahl der erschienenen Gegner
		numberOfEnemiesKilled,				// Anzahl der vernichteten Gegner
		numberOfMiniBossSeen,				// Anzahl der erschienenen Mini-Bosse
		numberOfMiniBossKilled,				// Anzahl der vernichteten Mini-Bosse
				
		// nur für Phönix- und Kamaitachi-Klasse
		bonusKills,							// Anzahl der Kills, für den aktuelken Mulikill-Award
		bonusKillsMoney,					// Gesamtverdienst am Abschuss aller Gegner innerhalb des aktuellen Multikill-Awards ohne Bonus
		bonusKillsTimer,					// reguliert die Zeit, innerhalb welcher Kills für den Multikill-Award berücksichtigt werden
				
		powerUpTimer[] = new int [4], 		// Zeit [frames] in der das PowerUp (0: bonus dmg; 1: invincible; 2: endless energy; 3: bonus fire rate) noch aktiv ist
		   		
		// Standard-Upgrades: Index 0: Hauptrotor, 1: Raketenantrieb, 2: Panzerung, 3: Feuerkraft, 4: Schussrate, 5: Energie-Upgrade
		upgradeCosts[] = new int[6],    			// Preisniveau für alle 6 StandardUpgrades der aktuellen Helikopter-Klasse
		levelOfUpgrade[] = new int[6];			// Upgrade-Level aller 6 StandardUpgrades
	
	public long
		pastTeleportTime,						// nur Phönix-Klasse: Zeitpunkt der letzten Nutzung des Teleporters
    	scorescreenTimes[] = new long [5];	// Zeit, die bis zum Besiegen jedes einzelnen der 5 Bossgegner vergangen ist
		
    public float
		rotorSystem,						// legt die aktuelle Geschwindigkeit des Helikopters fest
		currentPlating,						// aktuelle Panzerung (immer <= maximale Panzerung)
    	energy,								// verfügbare Energie;
		regenerationRate,					// Energiezuwachs pro Simulationsschritt
		spellCosts,							// Energiekosten für die Nutzung des Energie-Upgrades
    	rotorPosition[] = new float[NR_OF_TYPES];	// Stellung des Helikopter-Hauptrotors für alle Klassen; genutzt für die Startscreen-Animation
    		
    public boolean
		spotlight,							// = true: Helikopter hat Scheinwerfer
		hasPiercingWarheads,				// = true: Helikopterraketen werden mit Durchstoß-Sprengköpfen bestückt
		
		hasShortrangeRadiation,				// = true: Helikopter verfügt über Nahkampfbestrahlng
		//hasJumboMissiles,					// = true: Helikopter verschießt Jumbo-Raketen
		//hasRadarDevice,						// = true: Helikopter verfügt über eine Radar-Vorrichtung
		//hasRapidFire,						// = true: Helikopter verfügt über eine Schnellschussvorrichtung
		//hasInterphaseGenerator,				// = true: Helikopter verfügt über einen Interphasen-Generator
		//hasPowerUpImmobilizer,				// = true: Helikopter verfügt über einen Interphasen-Generator
		
		isNextMissileStunner,   			// = true: die nächste abgeschossene Rakete wird eine Stopp-Rakete
		isActive,							// = false: Helikopter ist nicht in Bewegung und kann auch nicht starten, Raketen abschießen, etc. (vor dem ersten Start oder nach Absturz = false)
        isDamaged,    						// = true: Helikopter hat einen Totalschaden erlitten
		isRotorSystemActive,				// = true: Propeller dreht sich / Helikopter fliegt
		isContiniousFireEnabled,			// = true: Dauerfeuer aktiv
		isSearchingForTeleportDestination,	// = true: es wird gerade ein Zielort für den Teleportationvorgang ausgewählt
		isMovingLeft,
		isPlayedWithoutCheats,				// = true: Spielstand kann in die Highscore übernommen werden, da keine cheats angewendet wurden
     	hasMaxUpgradeLevel[] = new boolean[6],	// = true: für diese Upgrade wurde bereits die maximale Ausbaustufe erreich
     		     		
     	// nur für Roch-Klasse
		isPowerShieldActivated;				// = true: Power-Shield ist aktiviert
     
    public Point
    	destination = new Point(), 				// dorthin fliegt der Helikopter
  		priorTeleportLocation = new Point(); 	// nur für Phönix-Klasse: Aufenthaltsort vor Teleportation
	
	public Point2D
  		location = new Point2D.Float(),	// exakter Aufenthaltsort	
  		nextLocation = new Point2D.Float();
    
  	public Enemy 
  		tractor;			// Referenz auf den Gegner, der den Helikopter mit einem Traktorstrahl festhält
 	    
  	public Explosion
		empWave;			// Pegasus-Klasse: Referenz auf zuletzt ausgelöste EMP-Schockwelle
	
	private int
		fireRateTimer;  	// reguliert die Zeit [frames], die mind. vergehen muss, bis wieder geschossen werden kann
    
    private float 
    	speed;     			// aktuelle Geschwindigkeit des Helikopters
    
    private boolean
		isCrashing;			// Helikopter befindet sich im Sturzflug
      
    // Grundfarben zur Berechnung der Gradientenfarben
    private Color
    	inputColorCannon, 
    	inputColorHull, 
    	inputColorWindow, 
    	inputColorFuss1, 
    	inputColorFuss2, 
    	inputGray,
    	inputLightGray, 
    	inputLamp;                             
    
    // Gradientenfarben
    private GradientPaint 	
    	gradientHull, 					// Hauptfarbe des Helikopters
    	gradientCannon1,				// Farbe der ersten Bordkanone
    	gradientWindow, 				// Fensterfarbe
    	gradientCannon2and3,			// Farbe der zweiten und dritten Bordkanone
    	gradientFuss1, 					// Farben der Landekufen
    	gradientFuss2, 					
    	gradientCannonHole;				// Farbe der Bordkanonen-Öffnung
	   
    public Helicopter()
    {
    	this.paintBounds.setSize(HELICOPTER_SIZE);
    	this.reset();
    }

    // TODO Alles was mit "malen" zusammenhängt in eine eigene Klasse auslagern
    public void paint(Graphics2D g2d, TimesOfDay timeOfDay)
    {
    	paint(g2d, this.paintBounds.x, this.paintBounds.y, this.getType(), timeOfDay, false);
    }
    
    public void paint(Graphics2D g2d, int left, int top, HelicopterTypes helicopterType, TimesOfDay timeOfDay)
    {
    	paint(g2d, left, top, helicopterType, timeOfDay, false);
    }
    
    public void paint(Graphics2D g2d, int left, int top, HelicopterTypes helicopterType, TimesOfDay timeOfDay, boolean unlockedPainting)
    {
    	// TODO: die paint Methode zerstückeln damit inheritance leichter umzusetzen wird
    	// TODO Farben in Menü und Spiel fpr Hülle und Kanone passen nicht merh
    	// die Farben
    	if(unlockedPainting)
    	{
    		this.inputColorCannon = helicopterType.getStandardSecondaryHullColor();
    	}
    	else if(this.plasmaActivationTimer >= POWERUP_DURATION/4  ||
    		(Events.window == STARTSCREEN && helicopterType == KAMAITACHI && Menu.effectTimer[KAMAITACHI.ordinal()] > 0 && Menu.effectTimer[KAMAITACHI.ordinal()] < 35))
    	{
    		this.inputColorCannon = Color.green;
    	}
    	else if(this.plasmaActivationTimer == 0)
    	{
    		if(helicopterType == OROCHI
    					  &&( (this.isNextMissileStunner
    							&& (this.energy >= this.spellCosts
    								|| this.hasUnlimitedEnergy()))
    						  || 
    						  (Events.window == STARTSCREEN 
    						  	&& Menu.effectTimer[OROCHI.ordinal()] > 1
    						  	&& Menu.effectTimer[OROCHI.ordinal()] < 80)) ) // 70
    		{
    			this.inputColorCannon = MyColor.variableBlue;
    		}
    		else if(Events.window == STARTSCREEN
    				&& ( (helicopterType == KAMAITACHI
    						&& Menu.effectTimer[KAMAITACHI.ordinal()] >= 35
    						&& Menu.effectTimer[KAMAITACHI.ordinal()] < 100)
    					 ||
    					 (helicopterType == PHOENIX
    					 	&& Menu.effectTimer[PHOENIX.ordinal()] > 1
    					 	&& Menu.effectTimer[PHOENIX.ordinal()] < 55) ))
    		{
    			this.inputColorCannon = MyColor.variableGreen;
    		}
    		else{this.inputColorCannon 
    				= this.isInvincible()
    					? MyColor.variableGreen 
    					: this.getSecondaryHullColor();}
    	}
    	else
    	{
    		this.inputColorCannon 
    			= this.isInvincible()
    				? MyColor.reversedRandomGreen()
    				: MyColor.variableGreen;
    	}
    	
    	this.inputColorHull = unlockedPainting
								? helicopterType.getStandardPrimaryHullColor()
								: (    !this.isInvincible()
    						     	&& !(Events.window == STARTSCREEN
    						        && helicopterType == PHOENIX
    						        && Menu.effectTimer[PHOENIX.ordinal()] > 1
    						        && Menu.effectTimer[PHOENIX.ordinal()] < 55))
    						  			? helicopterType.getStandardPrimaryHullColor()
    						  			: MyColor.variableGreen;
    	
    	this.inputColorWindow = !unlockedPainting
    								&& (this.hasTripleDmg()
    									|| this.hasBoostedFireRate())
    								|| (Events.window == STARTSCREEN 
    										&& helicopterType == HELIOS
    										&& Menu.effectTimer[HELIOS.ordinal()] > 0
    										&& Menu.effectTimer[HELIOS.ordinal()] < 65)
    								? MyColor.variableRed 
    								: MyColor.windowBlue;
    	this.inputColorFuss1 = MyColor.lighterGray;
    	this.inputColorFuss2 = MyColor.enemyGray;
    	this.inputGray = MyColor.gray;
    	this.inputLightGray = MyColor.lightGray;
    	this.inputLamp = (!unlockedPainting && Events.timeOfDay == NIGHT && Events.window == GAME) ? MyColor.randomLight : MyColor.darkYellow;
    	    	
    	if(!unlockedPainting && this.interphaseGeneratorTimer > this.shiftTime)
    	{    		
    		this.inputColorCannon = MyColor.setAlpha(this.inputColorCannon, INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
    		this.inputColorHull =   MyColor.setAlpha(this.inputColorHull, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
    		this.inputColorWindow = MyColor.setAlpha(this.inputColorWindow, INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()] );
    		this.inputColorFuss1 =  MyColor.setAlpha(this.inputColorFuss1, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
    		this.inputColorFuss2 =  MyColor.setAlpha(this.inputColorFuss2, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
    		this.inputGray = 		MyColor.setAlpha(this.inputGray, 		INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
    		this.inputLightGray = 	MyColor.setAlpha(this.inputLightGray, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
    		this.inputLamp = 		MyColor.setAlpha(this.inputLamp, 		INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
    	}   	
    	
    	this.gradientHull = new GradientPaint(0, top-10, MyColor.dimColor(this.inputColorHull, 1.65f),
    										  0, top+ 2, MyColor.dimColor(this.inputColorHull, 0.75f), true);
    	this.gradientCannon1 = new GradientPaint(0, top+56, MyColor.dimColor(this.inputColorCannon, 1.65f),
												 0, top+64, MyColor.dimColor(this.inputColorCannon, 0.55f), true);
    	this.gradientWindow = new GradientPaint(0, top-10, MyColor.dimColor(this.inputColorWindow, 2.2f),
												0, top+ 2, MyColor.dimColor(this.inputColorWindow, 0.70f), true);
    	this.gradientCannon2and3 = new GradientPaint(0, top+28, MyColor.dimColor(this.inputColorCannon, 1.7f),
													 0, top+35, MyColor.dimColor(this.inputColorCannon, 0.4f), true);    	
    	this.gradientFuss1 = new GradientPaint(left+61, 0, this.inputColorFuss1, left+68, 0, MyColor.dimColor(this.inputColorFuss1, 0.44f), true);
    	this.gradientFuss2 = new GradientPaint(0, top+72, this.inputColorFuss2, 0, top+76, MyColor.dimColor(this.inputColorFuss2, 0.55f), true);
    	this.gradientCannonHole = (this.plasmaActivationTimer == 0 || unlockedPainting)  ? this.gradientHull : MyColor.cannolHoleGreen;
    	
    	boolean movementLeft = this.isMovingLeft && Events.window == GAME && !unlockedPainting;
    	    	
    	// Nahkampfbestrahlung 
    	if(!unlockedPainting && this.hasShortrangeRadiation)
        {            
            g2d.setColor(this.enhancedRadiationTimer == 0
            				? MyColor.radiation[Events.timeOfDay.ordinal()]
            				: MyColor.enhancedRadiation[Events.timeOfDay.ordinal()]);
            g2d.fillOval(left+(movementLeft ? -9 : 35), top+19, 96, 54);
        }
    	    	
    	// Propeller-Stange
    	g2d.setColor(this.inputLightGray);  
    	g2d.setStroke(new BasicStroke(2));
    	g2d.drawLine(left+(movementLeft ? 39 : 83), top+14, left+(movementLeft ? 39 : 83), top+29);
    	
    	// Fußgestell
    	g2d.setPaint(this.gradientFuss2);
        g2d.fillRoundRect(left+(movementLeft ? 25 : 54), top+70, 43, 5, 5, 5);
        g2d.setPaint(this.gradientFuss1);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2d.drawLine(left+61, top+66, left+61, top+69); 
        g2d.drawLine(left+(movementLeft ? 33 : 89), top+66, left+(movementLeft ? 33 : 89), top+69);
        g2d.setStroke(new BasicStroke(1));
        
        // der Helikopter-Rumpf
        g2d.setPaint(this.gradientHull);
        g2d.fillOval(left+(movementLeft ?  2 : 45), top+29, 75, 34);
        g2d.fillRect(left+(movementLeft ? 92 : -7), top+31, 37,  8);
        g2d.fillArc (left+(movementLeft ? 34 : 23), top+11, 65, 40, 180, 180);
        g2d.setPaint(this.gradientWindow);        
        g2d.fillArc (left+(movementLeft ?  1 : 69), top+33, 52, 22, (movementLeft ? 75 : -15), 120);
                
        // die Kanonen
        g2d.setPaint(this.gradientCannon1);        
        g2d.fillRoundRect(left+(movementLeft ? 26 : 53), top+52, 43, 13, 12, 12);
        g2d.setPaint(this.gradientCannonHole); 
        g2d.fillOval(left+(movementLeft ? 27 : 90), top+54, 5, 9);
        if(!unlockedPainting && this.numberOfCannons >= 2)
        {           
            g2d.setPaint(this.gradientCannon2and3);            
            g2d.fillRoundRect(left+(movementLeft ? 32 : 27), top+27, 63, 6, 6, 6);
            g2d.setPaint(this.gradientCannonHole);
            g2d.fillOval(left+(movementLeft ? 33 : 86), top+28, 3, 4);
        }
        if(!unlockedPainting && this.numberOfCannons >= 3)
        {
        	g2d.setPaint(this.gradientCannon2and3);
        	g2d.fillRoundRect(left+(movementLeft ? 38 : 37), top+41, 47, 6, 6, 6);
        	g2d.setPaint(this.gradientCannonHole);
            g2d.fillOval(left+(movementLeft ? 39 : 80), top+42, 3, 4);
        }
        
        //der Scheinwerfer
        if(!unlockedPainting && this.spotlight)
        {            
        	if(Events.timeOfDay == NIGHT && Events.window == GAME)
        	{
        		g2d.setColor(MyColor.translucentWhite);
                g2d.fillArc(left+(movementLeft ? -135 : -43), top-96, 300, 300, (movementLeft ? 165 : -15), 30);
        	}        	
        	g2d.setPaint(this.gradientHull);
            g2d.fillRect(left+(movementLeft ? 4 : 106), top+50, 12, 8);
        	g2d.setColor(this.inputLamp);
            g2d.fillArc(left+(movementLeft ? -1 : 115), top+50, 8, 8, (movementLeft ? -90 : 90), 180);
        }        
                
        //die Propeller        
        paintRotor(g2d,
        			this.inputGray, 
        			left+(movementLeft ? -36 : 8),
        			top-5, 
        			150, 37, 3, 
        			(int)(this.rotorPosition[helicopterType.ordinal()]),
        			12, 
        			this.isRotorSystemActive,
        			false);
        
        paintRotor(g2d,
        			this.inputGray,
        			left+(movementLeft ?  107 : -22),
        			top+14,
        			37, 37, 3,
        			(int)(this.rotorPosition[helicopterType.ordinal()]),
        			12, 
        			this.isRotorSystemActive,
        			false);
        
        if(Events.window == STARTSCREEN 
        	&& helicopterType == PEGASUS
        	&& Menu.effectTimer[helicopterType.ordinal()] > 0
        	&& this.empWave != null)
        {
        	if(this.empWave.time >= this.empWave.maxTime)
        	{
        		this.empWave = null;
        	}
        	else
        	{
        		this.empWave.update();
        		this.empWave.paint(g2d);
        	}
        }
        
        // Energie-Schild der Roch-Klasse
        if(!unlockedPainting
        	&& (this.isPowerShieldActivated
    			|| (Events.window == STARTSCREEN 
    				&& helicopterType == ROCH
    				&& Menu.effectTimer[ROCH.ordinal()] > 0
    				&& Menu.effectTimer[ROCH.ordinal()] < 68))) // 60
        {            
            g2d.setColor(MyColor.shieldColor[timeOfDay.ordinal()]);
            g2d.fillOval(left+(movementLeft ? -9 : 35), top+19, 96, 54);
        }
               
        if(Events.recordTime[helicopterType.ordinal()][4] > 0 && Events.window == STARTSCREEN)
        {            
            g2d.setFont(Menu.fontProvider.getBold(12));
            g2d.setColor(Color.yellow);
            g2d.drawString(Menu.language == ENGLISH ? "Record time:" : "Bestzeit:", left-27, top+67);
            g2d.drawString(Menu.minuten(Events.recordTime[helicopterType.ordinal()][4]),left-27, top+80);
        } 
        
        if(helicopterType == HELIOS && Events.window == STARTSCREEN)
        {
			g2d.setFont(Menu.fontProvider.getBold(12));
            g2d.setColor(MyColor.brown);
            g2d.drawString("Hardcore" + (Menu.language == ENGLISH ? " mode" : "-Modus:"), left-27, top-4);
        }  
            
        //zu Testzwecken: 
        /*
        g2d.setColor(Color.red);
        g2d.draw(this.bounds);
        g2d.fillOval((int) this.location.getX()-2, (int) this.location.getY()-2, 4, 4); */
    }

	public static void paintRotor(Graphics2D g2d, Color color,
								  int x, int y, int width, int height,
								  int nrOfBlades, int pos, int bladeWidth,
								  float borderDistance, boolean active)
	{
    	int distanceX = (int) (borderDistance * width),
    		distanceY = (int) (borderDistance * height);
    	paintRotor(g2d, color,
    				x+distanceX,
    				y+distanceY,
    				width-2*distanceX,
    				height-2*distanceY,
    				nrOfBlades, pos, bladeWidth, active, true);
	}   
    
	static void paintRotor(Graphics2D g2d, Color color,
						   int x, int y, int width, int height,
						   int numberOfBlades, int pos, int bladeWidth,
						   boolean active, boolean enemiePaint)
	{
		if(active)
	    {
	       	g2d.setColor((Events.timeOfDay == DAY || enemiePaint) ? MyColor.translucentGray : MyColor.translucentWhite);
	       	g2d.fillOval(x, y, width, height); 
	    }
	    g2d.setColor(color);	        
	    for(int i = 0; i < numberOfBlades; i++)
	    {
	       	g2d.fillArc(x, y, width, height, -10-pos+i*(360/ numberOfBlades), bladeWidth);
	    }		
	}

	public Color getPrimaryHullColor()
	{
		return this.hasGoliathPlating()
				? this.getType().getPlatedPrimaryHullColor()
				: this.getType().getStandardPrimaryHullColor();
	}

	public Color getSecondaryHullColor()
	{
		return this.hasGoliathPlating()
				? this.getType().getPlatedSecondaryHullColor()
				: this.getType().getStandardSecondaryHullColor();
	}

	public void update(EnumMap<CollectionSubgroupTypes, LinkedList<Missile>> missile,
					   EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
	{
		this.updateTimer();
		if(this.canRegenerateEnergy()){this.regenerateEnergy();}
		if(this.isPowerShieldActivated && this.energy == 0)
		{
			this.shutDownPowerShield();
		}
		this.evaluateFire(missile);
		this.move(explosion);
	}
    
	boolean canRegenerateEnergy()
	{		
		return !this.isDamaged;
	}

	void updateTimer()
	{
		if(this.recentDamageTimer > 0)		{this.recentDamageTimer--;}
		if(this.enhancedRadiationTimer > 0)	{this.enhancedRadiationTimer--;}
		if(this.slowedTimer > 0)			{this.slowedTimer--;}
		this.evaluatePowerUpActivationStates();
	}
	
	void regenerateEnergy()
    {
    	float
			maxEnergy = MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()]),
			newEnergy = this.energy + calculateEnergyRegenerationRate();

    	this.energy = Math.max(0, Math.min(newEnergy, maxEnergy));
    }

    float calculateEnergyRegenerationRate()
	{
		return this.regenerationRate;
	}

	private void evaluateFire(EnumMap<CollectionSubgroupTypes, LinkedList<Missile>> missile)
	{
    	if(this.isReadyForShooting()){this.shoot(missile);}
    	this.fireRateTimer++;
	}
	
	public boolean hasTripleDmg()
	{		
		return this.powerUpTimer[TRIPLE_DMG.ordinal()] > 0;
	}
	
	public boolean isInvincible()
	{		
		return this.powerUpTimer[INVINCIBLE.ordinal()] > 0;
	}
	
	public boolean hasUnlimitedEnergy()
	{		
		return this.powerUpTimer[UNLIMITRED_ENERGY.ordinal()] > 0;
	}
	
	private boolean hasBoostedFireRate()
	{		
		return this.powerUpTimer[BOOSTED_FIRE_RATE.ordinal()] > 0;
	}
		
	private boolean isReadyForShooting()
	{
		return   	this.isContiniousFireEnabled
    			&& !this.isDamaged
    			&& !this.isOnTheGround()
    			&&  this.fireRateTimer >= this.timeBetweenTwoShots;
	}

	// TODO Code Duplizierungen auflösen
	void shoot(EnumMap<CollectionSubgroupTypes, LinkedList<Missile>> missiles)
	{
    	if(this.hasPiercingWarheads){Audio.play(Audio.launch2);}
		else{Audio.play(Audio.launch1);}
		this.fireRateTimer = 0;
		this.missileCounter += this.numberOfCannons;

		boolean stunningMissile = isShootingStunningMissile();
		Missile sister = null;

		if(this.numberOfCannons >= 1)
		{
			Iterator<Missile> i = missiles.get(INACTIVE).iterator();
			Missile missile;
			if(i.hasNext()){missile = i.next(); i.remove();}
			else{missile = new Missile();}
			if(this.getType() == ROCH || this.getType() == OROCHI)
			{
				missile.sister[0] = null;
				missile.sister[1] = null;
				sister = missile;
			}
			missiles.get(ACTIVE).add(missile);
			missile.launch(this, stunningMissile, 56);
		}
		if(this.numberOfCannons >= 2)
		{
			Iterator<Missile> i = missiles.get(INACTIVE).iterator();
			Missile missile;
			if(i.hasNext()){missile = i.next(); i.remove();}
			else{missile = new Missile();}
			if(  sister != null && sister.sister != null &&
			    (this.getType() == ROCH || this.getType() == OROCHI))
			{
				missile.sister[0] = sister;
				missile.sister[1] = null;
				sister.sister[0] = missile;
				sister = missile;
			}
			missiles.get(ACTIVE).add(missile);
			missile.launch(this, stunningMissile, 28);
		}
		if(this.numberOfCannons >= 3)
		{
			Iterator<Missile> i = missiles.get(INACTIVE).iterator();
			Missile missile;
			if(i.hasNext()){missile = i.next(); i.remove();}
			else{missile = new Missile();}
			if(  sister != null && sister.sister != null &&
			    (this.getType() == ROCH || this.getType() == OROCHI))
			{
				missile.sister[0] = sister.sister[0];
				missile.sister[1] = sister;
				sister.sister[0].sister[1] = missile;
				sister.sister[1] = missile;
			}
			missiles.get(ACTIVE).add(missile);
			missile.launch(this, stunningMissile, 42);
		}
	}
	private void move(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
		if(this.isOnTheGround())
		{
			this.isRotorSystemActive = false;
		}

		float
    		nextX = (float) this.location.getX(),
    		nextY = (float) this.location.getY();

    	if(this.isCrashing)
    	{
    		nextY += NOSEDIVE_SPEED;
    	}
    	else if(this.isActive && this.tractor == null)
    	{
    		this.speed = (this.slowedTimer > 0 ) ? 1.5f : this.rotorSystem;
    		float fraction = (float) (this.speed/this.location.distance(this.destination.x, this.destination.y));

    		if(fraction < 1)
        	{
        		if(!(   this.bounds.getMaxY() + NO_COLLISION_HEIGHT  >= GROUND_Y
        			 && this.destination.y >= GROUND_Y))
        		{
        			nextX += (float)(fraction*(this.destination.x - this.location.getX()) - 1);
        		}
        		    nextY += (float)(fraction*(this.destination.y - this.location.getY()));
        	}
    		else
        	{
    			nextX = this.destination.x;
        		nextY = this.destination.y;
        	}
    	}

    	boolean isInTheAir = this.location.getY() != 407d;
    	float lastX = (float)this.location.getX();

    	this.nextLocation.setLocation(nextX, nextY);
    	this.correctAndSetCoordinates();

    	if(Enemy.currentNumberOfBarriers > 0 && !this.isDamaged)
    	{
    		for(int i = 0; i < Enemy.currentNumberOfBarriers; i++)
    		{
    			Enemy enemy = Enemy.livingBarrier[i];
    			enemy.lastTouchedSite = enemy.touchedSite;
    			if(this.isLocationAdaptionApproved(enemy))
    			{
    				this.adaptPosTo(enemy);
    	   	 		this.correctAndSetCoordinates();
    	   	 		if(enemy.isStaticallyCharged())
    	   	 		{
    	   	 			enemy.startStaticDischarge(explosion, this);
    	   	 		}
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
    		for(int i = 0; i < Enemy.currentNumberOfBarriers; i++)
    		{
    			Enemy.livingBarrier[i].evaluatePosAdaption(this);
    		}
    	}

    	if(this.isActive && this.tractor == null)
    	{
    		if(!this.isCrashing)
        	{
        		if(this.bounds.getMaxY() + NO_COLLISION_HEIGHT != GROUND_Y
        			|| lastX != (float)this.location.getX())
        		{
        			this.isRotorSystemActive = true;
        		}
        		if(isInTheAir && !(this.location.getY() != 407d)){Audio.play(Audio.landing);}
        	}
        	else if(isInTheAir && this.location.getY() == 407d)
        	{
        		this.crashed(explosion);
        	}
    	}
    	if(this.isRotorSystemActive){this.rotatePropeller(12);}
    	this.setPaintBounds();
    }

	boolean isShootingStunningMissile()
	{
		return false;
	}

	public boolean isLocationAdaptionApproved(Enemy enemy)
	{		
		return enemy.bounds.intersects(this.bounds)
				&& this.interphaseGeneratorTimer <= this.shiftTime
				&& enemy.alpha == 255 
				&& enemy.borrowTimer != 0;
	}

	void adaptPosTo(Enemy enemy)
	{		
		double 
			x = this.bounds.getCenterX() - enemy.bounds.getCenterX(),
		 	y = this.bounds.getCenterY() - enemy.bounds.getCenterY(),
			pseudoAngle = (x/MyMath.ZERO_POINT.distance(x, y)),
			distance,
			localSpeed = enemy.hasUnresolvedIntersection ? this.speed : Double.MAX_VALUE;
			
		if(pseudoAngle > MyMath.ROOT05)
		{
			// Right	
			// new pos x: enemy.getMaxX() + (this.moves_left ? 39 : 83) 
			distance = (enemy.bounds.getX() + enemy.bounds.getWidth()) + (this.isMovingLeft ? 39 : 83) - this.location.getX();
			this.nextLocation.setLocation(
				this.location.getX() + (distance > localSpeed ? localSpeed : distance),
				this.location.getY());
			enemy.setTouchedSiteToRight();

		}
		else if(pseudoAngle < -MyMath.ROOT05)
		{
			// Left
			// new pos x: enemy.bounds.x - this.bounds.getWidth() + (this.moves_left ? 39 : 83)
			distance = this.location.getX() - enemy.bounds.getX() + this.bounds.getWidth() - (this.isMovingLeft ? 39 : 83);
			this.nextLocation.setLocation(
				this.location.getX() - (distance > localSpeed ? localSpeed : distance),
				this.location.getY());
			enemy.setTouchedSiteToLeft();
		}
		else 
		{			
			if(this.bounds.getCenterY() > enemy.bounds.getCenterY())
			{
				// Bottom	
				// new pos y: enemy.bounds.getMaxY() + 56
				distance = enemy.bounds.getMaxY() + 56 - this.location.getY();
				this.nextLocation.setLocation(
					this.location.getX(),
					this.location.getY() + (distance > localSpeed ? localSpeed : distance));
				enemy.setTouchedSiteToBottom();
			}
			else
			{
				// Top	
				// new pos y: enemy.bounds.y - this.bounds.getHeight() + 56
				distance = this.location.getY() - enemy.bounds.getY() + this.bounds.getHeight() - 56;
				this.nextLocation.setLocation(
					this.location.getX(),
					this.location.getY() - (distance > localSpeed ? localSpeed : distance));
				enemy.setTouchedSiteToTop();
			}
			if(this.tractor != null){this.stopTractor();}
		}
	}
	
	private void correctAndSetCoordinates()
	{    	
    	this.location.setLocation
		(
			Math.max(40, Math.min(1024, this.nextLocation.getX())),
			Math.max(32, Math.min(407, this.nextLocation.getY()))
		);    	    	
   	 	this.setBounds();
	}

	void setBounds()
	{
		this.bounds.setRect(
	   	 	this.location.getX() 
	   	 		- (this.isMovingLeft
	   	 			? FOCAL_PNT_X_LEFT
	   	 			: FOCAL_PNT_X_RIGHT),
	   	 	this.location.getY() - FOCAL_PNT_Y_POS,
	   	 	this.bounds.getWidth(),
	   	 	this.bounds.getHeight());
	}
	
	public void initialize(boolean newGame, Savegame savegame)
    {
    	for(int i = 0; i < 6; i++)
	    {
    		this.hasMaxUpgradeLevel[i] = false;
    		this.upgradeCosts[i] =  getUpgradeCosts(i);
    		if(newGame){this.levelOfUpgrade[i] = this.upgradeCosts[i] < 2 ? 2 : 1;}
	    }
    	if(!newGame){this.restoreLastGameState(savegame);}
    	this.updateProperties(newGame);
    	this.fireRateTimer = this.timeBetweenTwoShots;
        this.rotorPosition[this.getType().ordinal()] = 0;
        this.empWave = null;
    }
	
	int getUpgradeCosts(int i)
	{
		return this.getType().getUpgradeCosts(i);
	}
	
	private void restoreLastGameState(Savegame savegame)
	{
		this.levelOfUpgrade = savegame.levelOfUpgrade.clone();
		this.spotlight = savegame.spotlight;
		this.platingDurabilityFactor = savegame.platingDurabilityFactor;
		this.hasPiercingWarheads = savegame.hasPiercingWarheads;
		this.numberOfCannons = savegame.numberOfCannons;
		this.currentPlating = savegame.currentPlating;
		this.energy = savegame.energy;
		
		if(savegame.hasFifthSpecial)
		{
			this.obtainFifthSpecial();
		}
		
		this.numberOfEnemiesSeen = savegame.enemiesSeen;
		this.numberOfEnemiesKilled = savegame.enemiesKilled;
		this.numberOfMiniBossSeen = savegame.miniBossSeen;
		this.numberOfMiniBossKilled = savegame.miniBossKilled;
		this.numberOfCrashes = savegame.numberOfCrashes;
		this.numberOfRepairs = savegame.numberOfRepairs;
		this.isPlayedWithoutCheats = savegame.noCheatsUsed;
		this.missileCounter = savegame.missileCounter;
		this.hitCounter = savegame.hitCounter;
		
		this.scorescreenTimes = savegame.scorescreenTimes.clone();
	}	
    
    public void reset()
    {
        // TODO ggf. muss einiges nicht mehr resettet werden, da immer ein neuer Helicopter erzeugt wird
    	for(int i = 0; i < 4; i++){this.rotorPosition[i] = 0;}
        this.resetState();
        this.placeAtStartpos();
        this.isDamaged = false;
		this.isPlayedWithoutCheats = true;
		this.resetCounterForHighscore();
		this.resetSpecialUpgrades();
        Arrays.fill(this.scorescreenTimes, 0);
    }
	
	public void resetState()
	{
		// TODO boolscher Parameter - anders lösen
		resetState(true);
	}
	
	public void resetState(boolean resetStartPos)
	{
		this.setActivationState(false);
		this.isSearchingForTeleportDestination = false;
		this.isNextMissileStunner = false;
		this.isCrashing = false;
		this.interphaseGeneratorTimer = 0;
		this.plasmaActivationTimer = 0;
		this.isPowerShieldActivated = false;
		this.slowedTimer = 0;
		this.recentDamageTimer = 0;
		for(int i = 0; i < 4; i++){this.powerUpTimer[i] = 0;}
		this.empWave = null;
		this.rotorPosition[this.getType().ordinal()] = 0;
		if(resetStartPos){this.placeAtStartpos();}
		this.fireRateTimer = this.timeBetweenTwoShots;
	}
	
	private void resetCounterForHighscore()
	{
		this.numberOfCrashes = 0;
		this.numberOfRepairs = 0;
		this.missileCounter = 0;
		this.hitCounter = 0;
		this.numberOfEnemiesSeen = 0;
		this.numberOfEnemiesKilled = 0;
		this.numberOfMiniBossSeen = 0;
		this.numberOfMiniBossKilled = 0;
	}
	
	private void resetSpecialUpgrades()
	{
		this.spotlight = false;
		this.platingDurabilityFactor = STANDARD_PLATING_STRENGTH;
		this.hasPiercingWarheads = false;
		this.numberOfCannons = 1;
		this.resetFifthSpecial();
	}
	
	abstract void resetFifthSpecial();
		
    public void repair(boolean restoreEnergy, boolean cheatRepair)
    {
    	Audio.play(Audio.cash);
    	this.numberOfRepairs++;
		this.isDamaged = false;
		this.isCrashing = false;
    	this.getMaxPlating();
    	this.setPlatingColor();
		if(restoreEnergy){this.energy = MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()]);}
		if(!cheatRepair){this.placeAtStartpos();}
		Menu.repairShopButton.get("RepairButton").costs = 0;
    }
	
	public void obtainAllUpgrades()
    {
    	for(int i = 0; i < 6; i++)
    	{
    		this.levelOfUpgrade[i] = MyMath.maxLevel(this.upgradeCosts[i]);
    	}
    	this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
    	this.hasPiercingWarheads = true;
    	this.getMaximumNumberOfCannons();
    	this.updateProperties(true);
		this.isDamaged = false;
    	Menu.updateRepairShopButtons(this);
    	this.isPlayedWithoutCheats = false;
    }
	
	void getMaximumNumberOfCannons()
	{
		this.numberOfCannons = 2;
	}
	
	public void obtainSomeUpgrades()
    {
		this.spotlight = true;
    	this.obtainFifthSpecial();
    	for(int i = 0; i < 6; i++)
    	{
    		if(this.levelOfUpgrade[i] < 6){this.levelOfUpgrade[i] = 6;}
    	}        		
    	this.updateProperties(true);
		this.isDamaged = false;
    	Menu.updateRepairShopButtons(this);
    	this.isPlayedWithoutCheats = false;
    }
	
	public boolean hasSomeUpgrades()
    {
    	for(int i = 0; i < 6; i++){if(this.levelOfUpgrade[i] < 6) return false;}
    	if(!this.spotlight) return false;
    	else return this.hasFifthSpecial();
    }
	
	abstract public boolean hasFifthSpecial();

	abstract public void obtainFifthSpecial();
    
    private boolean hasAllSpecials()
    {
		return this.spotlight
			&& this.hasGoliathPlating()
			&& this.hasPiercingWarheads
			&& this.hasAllCannons()
			&& hasFifthSpecial();
	}
    
    public boolean hasGoliathPlating()
	{
		return this.platingDurabilityFactor == GOLIATH_PLATING_STRENGTH;
	}
    
    public boolean hasAllCannons()
	{
		return this.numberOfCannons == 2;
	}
	
	public boolean hasAllUpgrades()
    {
        for(int i = 0; i < 6; i++){if(!this.hasMaxUpgradeLevel[i]){return false;}}
		return hasAllSpecials();
	}
	
	public void rotatePropeller(float rotationalSpeed){
		rotatePropeller(this.getType(), rotationalSpeed);}
	public void rotatePropeller(HelicopterTypes type, float rotationalSpeed)
    {
    	this.rotorPosition[type.ordinal()] += rotationalSpeed;
		if(this.rotorPosition[type.ordinal()] > 360){this.rotorPosition[type.ordinal()] -= 360;}
    }    
    
    private void placeAtStartpos()
    {
    	this.isMovingLeft = false;
    	this.bounds.setRect(INITIAL_BOUNDS);
    	this.location.setLocation(this.bounds.getX() + FOCAL_PNT_X_RIGHT, 
    							  INITIAL_BOUNDS.y + FOCAL_PNT_Y_POS);    	
    	this.setPaintBounds();
    }
    
    public void stopTractor()
	{
		Audio.tractorBeam.stop();
		this.tractor.stopTractor();
		this.tractor = null;
	}
    
    public void crash()
    {
    	this.isDamaged = true;
		this.isRotorSystemActive = false;
		this.energy = 0;
		this.destination.setLocation(this.bounds.getX() + 40, 520);	
		this.plasmaActivationTimer = 0;
		if(this.isPowerShieldActivated){this.shutDownPowerShield();}
		if(this.tractor != null){this.stopTractor();}
		this.numberOfCrashes++;
		if(this.location.getY() == 407d){this.crashed(Controller.getInstance().explosions);}
		else{this.isCrashing = true;}
    }
    
    private void crashed(EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
    {
    	this.isActive = false;
    	this.powerUpDecay();
		if(Events.level < 51 && explosion != null)
		{
			Audio.play(Audio.explosion3);
			Explosion.start(explosion, 
							this, 
							(int)(this.bounds.getX() 
								+ (this.isMovingLeft
									? FOCAL_PNT_X_LEFT 
									: FOCAL_PNT_X_RIGHT)), 
							(int)(this.bounds.getY() + FOCAL_PNT_Y_EXP),
                    ORDINARY,
							false);
		}
		Events.isRestartWindowVisible = true;
		this.isCrashing = false;
    }
	
	public void teleportTo(int x, int y)
    {
    	this.isSearchingForTeleportDestination = false;
		this.destination.setLocation(x, y);
		
		if(	(this.energy >= this.spellCosts || this.hasUnlimitedEnergy())
			&& !this.isDamaged
			&& !Menu.isMenueVisible
			&& !(this.bounds.getMaxY() + NO_COLLISION_HEIGHT >= GROUND_Y
					&& y >= GROUND_Y) 
			&& !(	   x > this.bounds.getX() + 33 
					&& x < this.bounds.getX() + 133 
					&& y > this.bounds.getY() + 6
					&& y < this.bounds.getY() + 106))
		{
			Audio.play(Audio.teleport1);
			this.energy -= this.hasUnlimitedEnergy() ? 0 : this.spellCosts;
			this.pastTeleportTime = System.currentTimeMillis();
						
			this.nextLocation.setLocation(x, y);
			this.correctAndSetCoordinates();
						
			if(!this.isActive || !this.isRotorSystemActive){this.setActivationState(true);}
			if(this.tractor != null){this.stopTractor();}
			this.powerUpTimer[INVINCIBLE.ordinal()] = Math.max(this.powerUpTimer[INVINCIBLE.ordinal()], Phoenix.TELEPORT_INVU_TIME);
			this.bonusKills = 0;
			this.enhancedRadiationTimer = Phoenix.TELEPORT_INVU_TIME;
			this.bonusKillsTimer = NICE_CATCH_TIME;
			this.bonusKillsMoney = 0;
		}
    }
	
	public void evaluateBonusKills()
	{
    	if(this.bonusKillsTimer > 0)
		{
			this.bonusKillsTimer--;
			if(	this.getType() == PHOENIX
			    && this.bonusKillsTimer == NICE_CATCH_TIME - TELEPORT_KILL_TIME
			    && this.bonusKills > 1)
			{
				Events.extraReward(this.bonusKills,
									this.bonusKillsMoney,
									0.75f, 0.75f, 3.5f);
			}
			else if(this.getType() == KAMAITACHI && this.bonusKillsTimer == 0)
			{
				if(this.bonusKills > 1)
				{
					Events.extraReward(this.bonusKills,
										this.bonusKillsMoney,
										0.5f, 0.75f, 3.5f); // 0.25f, 0.5f, 3.0f);
				}				
				this.bonusKillsMoney = 0;
				this.bonusKills = 0;
			}
		}
	}

	private void evaluatePowerUpActivationStates()
	{
    	for(int i = 0; i < 4; i++)
		{
			if(this.powerUpTimer[i] > 0)
			{
				this.powerUpTimer[i]--;
				if(this.powerUpTimer[i] == 0 && Menu.collectedPowerUp[i] != null)
				{
					Audio.play(Audio.powerUpFade2);
					Menu.collectedPowerUp[i].collected = true;
					Menu.collectedPowerUp[i] = null;
					if(i == 3){this.adjustFireRate(false);}
				}
				else if(this.powerUpTimer[i] == POWERUP_DURATION/4)
				{
					Audio.play(Audio.powerUpFade1);
				}
				else if(this.powerUpTimer[i] < POWERUP_DURATION/4 && Menu.collectedPowerUp[i] != null)
				{
					if(this.powerUpTimer[i]%32 > 15)
			    	{
			    		Menu.collectedPowerUp[i].surface = MyColor.setAlpha(Menu.collectedPowerUp[i].surface, 17 * ((this.powerUpTimer[i])%16));
			    		Menu.collectedPowerUp[i].cross =   MyColor.setAlpha(Menu.collectedPowerUp[i].cross,   17 * ((this.powerUpTimer[i])%16));
			    	}
					else
					{
						Menu.collectedPowerUp[i].surface = MyColor.setAlpha(Menu.collectedPowerUp[i].surface, 255 - 17 * ((this.powerUpTimer[i])%16));
						Menu.collectedPowerUp[i].cross = MyColor.setAlpha(Menu.collectedPowerUp[i].cross,     255 - 17 * ((this.powerUpTimer[i])%16));
					}
				}
			}
		}		
	}

	void updateInterphaseGenerator()
	{
    	this.interphaseGeneratorTimer++;
		if(this.interphaseGeneratorTimer == this.shiftTime + 1)
		{
			Audio.play(Audio.phaseShift);
			if(this.tractor != null){this.stopTractor();}
		}		
	}
	
	public void takeMissileDamage()
    {
		this.currentPlating = Math.max(this.currentPlating - this.getProtectionFactor() * ENEMY_MISSILE_DAMAGE_FACTOR, 0f);
		if(this.enhancedRadiationTimer == 0)
		{
			this.recentDamageTimer = RECENT_DMG_TIME;
		}
		if(this.isPowerShieldActivated)
		{
			this.shutDownPowerShield();
			this.energy = 0;
		}
		if(this.currentPlating <= 0 && !this.isDamaged)
		{
			this.crash();
		}
    }   
        
    private void updateProperties(boolean fullPlating)
    {
    	this.rotorSystem = MyMath.speed(this.levelOfUpgrade[ROTOR_SYSTEM.ordinal()]);
    	this.missileDrive = MyMath.missileDrive(this.levelOfUpgrade[MISSILE_DRIVE.ordinal()]);
    	if(fullPlating)
    	{
    		this.getMaxPlating();
    		this.energy = MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()]);
    	}
    	this.setPlatingColor();
		this.setCurrentBaseFirepower();
    	this.adjustFireRate(this.hasBoostedFireRate());
		this.regenerationRate = MyMath.regeneration(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()]);
		if(Events.window != GAME){this.fireRateTimer = this.timeBetweenTwoShots;}
		for(int i = 0; i < 6; i++)
		{
			if(this.levelOfUpgrade[i] >= MyMath.maxLevel(this.upgradeCosts[i]))
			{
				this.hasMaxUpgradeLevel[i] = true;
			}
		}
		this.setSpellCosts();
	}      

	void setSpellCosts()
	{
		this.spellCosts = this.getType().getSpellCosts();
	}

	public boolean isPoweredUp()
	{
		if(this.getType() == HELIOS){return false;}
		for(int i = 0; i < 4; i++)
		{
			if(this.powerUpTimer[i] != 0
				&& this.powerUpTimer[i] < Integer.MAX_VALUE/2)
			{
				return true;
			}
		}
		return false;
	}

	public void powerUpDecay()
	{
		for(int i = 0; i < 4; i++){if(this.powerUpTimer[i] < Integer.MAX_VALUE/2)
		{
			this.powerUpTimer[i] = Math.min(POWERUP_DURATION/4 + 1, this.powerUpTimer[i]);}
		}		
	}

	public void setPlatingColor()
	{		
		MyColor.plating = MyColor.percentColor((this.currentPlating)/this.maxPlating());
	}
	
	public void getPowerUp(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp,
	                PowerUpTypes powerUpType,
	                boolean lastingEffect)
	{
		getPowerUp(powerUp, powerUpType, lastingEffect, true);
	}
	
	void getPowerUp(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp,
	                PowerUpTypes powerUpType,
	                boolean lastingEffect,
	                boolean playSound)
	{
		if(lastingEffect && this.powerUpTimer[powerUpType.ordinal()] > 0)
		{
			if(playSound){Audio.play(Audio.powerUpFade2);}
			this.powerUpTimer[powerUpType.ordinal()] = 0;
			Menu.collectedPowerUp[powerUpType.ordinal()].collected = true;
			Menu.collectedPowerUp[powerUpType.ordinal()] = null;
			if(powerUpType == BOOSTED_FIRE_RATE){this.adjustFireRate(false);}
		} 
		else
		{
			if(playSound){Audio.play(Audio.powerAnnouncer[powerUpType.ordinal()]);}
			this.powerUpTimer[powerUpType.ordinal()] = lastingEffect
												? Integer.MAX_VALUE 
												: Math.max(
													this.powerUpTimer[powerUpType.ordinal()],
													POWERUP_DURATION);
			if(Menu.collectedPowerUp[powerUpType.ordinal()] == null)
			{								
				PowerUp.activate(this, powerUp, null, powerUpType, true);
				if(powerUpType == BOOSTED_FIRE_RATE){this.adjustFireRate(true);}
			}
			else
			{
				Menu.collectedPowerUp[powerUpType.ordinal()].surface
					= MyColor.setAlpha(Menu.collectedPowerUp[powerUpType.ordinal()].surface, 255);
				Menu.collectedPowerUp[powerUpType.ordinal()].cross
					= MyColor.setAlpha(Menu.collectedPowerUp[powerUpType.ordinal()].cross, 255);
			}			
		}		
	}
	
	public void setActivationState(boolean activationState)
	{
		this.isActive = activationState;
		this.isRotorSystemActive = activationState;
	}

	public void adjustFireRate(boolean poweredUp)
	{
		this.timeBetweenTwoShots = MyMath.fireRate(calculateSumOfFireRateBooster(poweredUp));
	}

	public int calculateSumOfFireRateBooster(boolean poweredUp)
	{
		return this.levelOfUpgrade[FIRE_RATE.ordinal()]
				+ (poweredUp ? FIRE_RATE_POWERUP_LEVEL : 0);
	}

	abstract public void updateUnlockedHelicopters();

	public void useReparationPowerUp()
	{
		Audio.play(Audio.cash);
		float maxPlating = this.maxPlating();
		if(this.currentPlating < maxPlating)
		{
			this.currentPlating
				= Math.min(
					maxPlating,
					this.currentPlating
						+ Math.max(1, (   maxPlating
										- this.currentPlating)/2));
		}
	}
	
	public static int heliosCosts(int upgradeNumber)
	{
		int heli;
		if(upgradeNumber <= 1){heli = 2;}
		else if(upgradeNumber == 2 || upgradeNumber == 3)
		{
			heli = upgradeNumber - 2;
		}
		else {heli = upgradeNumber - 1;}
		for(int i = 0; i < 4; i++)
		{
			if(Events.recordTime[heli][i] == 0) return 4-i;
		}
		return 0;		
	}

	public void menuePaint(Graphics2D g2d, HelicopterTypes helicopterType)
	{		
    	this.rotatePropeller(helicopterType, 7);
    	this.paint(g2d, 692, 360, helicopterType, DAY);
	}

	public boolean isPowerShieldProtected(Enemy enemy)
	{		
		return this.isPowerShieldActivated
			   && (this.hasUnlimitedEnergy()
				   || this.energy 
				   		>= this.spellCosts * enemy.collisionDamage(this));
	}

	public float kaboomDmg()
	{		
		return Math.max(4, 2*this.currentPlating /3);
	}
	
	public float maxPlating()
	{
		return MyMath.plating(this.levelOfUpgrade[PLATING.ordinal()])
			   * this.platingDurabilityFactor;
	}
	
	private void getMaxPlating()
	{
		this.currentPlating = this.maxPlating();
	}

	public void receiveStaticCharged(float degree)
	{
		if(!this.isInvincible())
		{			
			this.energy 
				= this.hasUnlimitedEnergy()
					? this.energy 
					: Math.max( 0, 
								this.energy 
								-degree*(this.isPowerShieldActivated
									? REDUCED_ENERGY_DRAIN 
									: ENERGY_DRAIN));			
			if(!this.isPowerShieldActivated)
			{
				this.slowedTimer = SLOW_TIME;
			}
		}
	}	
	
	public boolean canCollideWith(Enemy e)
	{		
		return this.basicCollisionRequirementsSatisfied(e)
			   && !(e.model == BARRIER 
						&& (    e.alpha != 255 
							||  e.borrowTimer == 0
							|| !e.hasUnresolvedIntersection));
	}

	public boolean basicCollisionRequirementsSatisfied(Enemy e)
	{		
		return !this.isDamaged
				&& e.isOnScreen()
				&& e.bounds.intersects(this.bounds);
	}
	
	public float getProtectionFactor()
	{		
		return this.isInvincible()
				? INVULNERABILITY_PROTECTION_FACTOR
				: 1.0f;
	}

	public boolean isEnergyAbilityActivatable()
	{		
		return this.hasEnoughEnergyForAbility();
	}

	boolean hasEnoughEnergyForAbility()
	{
		return this.energy >= this.spellCosts || this.hasUnlimitedEnergy();
	}

	public void upgradeEnergyAbility()
	{
		this.energy += MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()])
					   - MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()]-1);
		this.regenerationRate = MyMath.regeneration(this.levelOfUpgrade[ENERGY_ABILITY.ordinal()]);
	}

	public void becomesCenterOf(Explosion exp)
	{
		exp.ellipse.setFrameFromCenter(
			this.bounds.getX() + (this.isMovingLeft ? FOCAL_PNT_X_LEFT : FOCAL_PNT_X_RIGHT),
			this.bounds.getY() + FOCAL_PNT_Y_EXP, 
			this.bounds.getX() + (this.isMovingLeft ? FOCAL_PNT_X_LEFT : FOCAL_PNT_X_RIGHT),
			this.bounds.getY() + FOCAL_PNT_Y_EXP);
	}
	    
    void shutDownPowerShield()
    {
    	Audio.play(Audio.plasmaOff);
		this.isPowerShieldActivated = false;
    }

	public boolean isOnTheGround()
	{		
		return this.bounds.getMaxY() + NO_COLLISION_HEIGHT == GROUND_Y;
	}

	public void turnAround()
	{
		this.isMovingLeft = !this.isMovingLeft;
		this.setBounds();
	}

	public void tryToUseEnergyAbility(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp,
									  EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion)
	{
		if(this.isEnergyAbilityActivatable())
		{
			useEnergyAbility(powerUp, explosion);
		}
	}

	public void useEnergyAbility(EnumMap<CollectionSubgroupTypes, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupTypes, LinkedList<Explosion>> explosion){};

	public int abilityId(int i)
    {
    	return i == 5 ? 1 + i + this.getType().ordinal() : i;
    }

	public void beAffectedByCollisionWith(Enemy enemy,
										  Controller controller,
										  boolean playCollisionSound)
	{
		if(playCollisionSound)
		{
			Audio.play(enemy.type == KABOOM
					? Audio.explosion4
					: this.enhancedRadiationTimer == 0
					? Audio.explosion1
					: Audio.explosion2);
		}
		this.slowedTimer = 2;
		this.currentPlating
				= Math.max(
				this.currentPlating - enemy.collisionDamage(this),
				0);
	}

	public boolean hasPerformedTeleportKill()
	{		
		return this.bonusKillsTimer > 0;
	}

	public abstract HelicopterTypes getType();
	
	public void installGoliathPlating()
	{
		this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
		this.currentPlating += MyMath.plating(this.levelOfUpgrade[PLATING.ordinal()]);
		this.setPlatingColor();
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
		this.hasPiercingWarheads = true;
	}

	public boolean canBeTractored() {
		return this.tractor == null
				&& this.bounds.getX() - this.bounds.getX() > -750
				&& this.bounds.getX() - this.bounds.getX() < -50
				&& (this.bounds.getY() + 56 > this.bounds.getY() + 0.2 * this.bounds.getHeight()
				&& this.bounds.getY() + 60 < this.bounds.getY() + 0.8 * this.bounds.getHeight());
	}
	
	public float getMissileDamageFactor()
	{
		return STANDARD_MISSILE_DAMAGE_FACTOR;
	}

	public ExplosionTypes getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
	{
		return ORDINARY;
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
		this.currentBaseFirepower = (int)(this.getMissileDamageFactor() * MyMath.dmg(this.levelOfUpgrade[FIREPOWER.ordinal()]));
	}
	
	public boolean isFifthSpecialOnMaximumStrength()
	{
		return true;
	}
}