package de.helicopter_vs_aliens.model.helicopter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.*;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.Explosion;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.gui.Fonts;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.missile.MissileTypes;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpTypes;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelTypes.BARRIER;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.*;
import static de.helicopter_vs_aliens.gui.WindowTypes.GAME;
import static de.helicopter_vs_aliens.gui.WindowTypes.STARTSCREEN;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;

public abstract class Helicopter extends MovingObject implements Fonts, DamageFactors, MissileTypes
{			
	// Konstanten
    public static final int     			    	   
    	TELEPORT_KILL_TIME = 15,		// in dieser Zeit [frames] nach einer Teleportation vernichtete Gegner werden für den Extra-Bonus gewertet
    	NICE_CATCH_TIME = 22,			// nur wenn die Zeit [frames] zwischen Teleportation und Gegner-Abschuss kleiner ist, gibt es den "NiceCath-Bonus"
    	POWERUP_DURATION = 930,			// Zeit [frames] welche ein eingesammeltes PowerUp aktiv bleibt
    	RECENT_DMG_TIME = 50,
    	SLOW_TIME = 100,    	
    	TELEPORT_INVU_TIME = 45,
    	NO_COLLISION_DMG_TIME	= 20, 	// Zeitrate, mit der Helicopter Schaden durch Kollisionen mit Gegnern nehmen kann 
    	FIRE_RATE_POWERUP_LEVEL = 3,	// so vielen zusätzlichen Upgrades der Feuerrate entspricht die temporäre Steigerung der Feuerrate durch das entsprechende PowerUp
    	NR_OF_TYPES = HelicopterTypes.values().length,				// so viele Helikopter-Klassen gibt es
    	INVU_DMG_REDUCTION = 80,
    	ENERGY_DRAIN = 45,				// Energieabzug für den Helikopter bei Treffer
    	REDUCED_ENERGY_DRAIN = 10,
		STANDARD_PLATING_STRENGTH = 1,
		GOLIATH_PLATING_STRENGTH = 2,
    	    	    
    	// Upgrade-Kosten-Level (0 - sehr günstig bis 4 - sehr teuer) für die Standardupgrades
    	// für jede einzelne Helikopter-Klasse sowie Energiekosten für das Energieupgrade
    	COSTS[][] = {{4, 2, 0, 1, 2, 3, 50},	// Phoenix
    	             {1, 3, 4, 0, 4, 2, 30},	// Roch
    	             {0, 0, 1, 2, 3, 4, 20}, 	// Orochi
    	             {2, 1, 3, 4, 0, 1, 200}, 	// Kamaitachi
    	             {3, 4, 2, 3, 1, 0, 75}, 	// Pegasus
    	             {2, 2, 2, 2, 2, 2, 250}};	// Helios	       
    	 
    static final float     	
    	MISSILE_DMG 			 =  0.5f,
    	ENHANCED_RADIATION_PROB	 =  0.25f,
    	POWER_SHIELD_E_LOSS_RATE = -0.06f;
    
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
        	    	
    private static final int 
    	POWER_SHIELD_ACTIVATION_TRESHOLD = 75,
    	INTERPHASE_GENERATOR_ALPHA[] = {110, 70}; // Alpha-Wert zum Zeichnen des Helikopters bei Tag- und Nachtzeit nach einem Dimensionssprung
 	
    private static final float    
    	NOSEDIVE_SPEED = 12f,	// Geschwindigkeit des Helikopters bei Absturz
    	INVU_DMG_FACTOR = 1.0f - INVU_DMG_REDUCTION/100f;


	public int
		missileDrive,						// Geschwindigkeit [Pixel pro Frame] der Raketen
		currentFirepower,					// akuelle Feuerkraft unter Berücksichtigung des Upgrade-Levels und des evtl. erforschten Jumbo-Raketen-Spezial-Upgrades
		time_between_2_shots,				// Zeit [frames], die mindestens verstreichen muss, bis wieder geschossen werden kann		 
		shift_time,							// nur Pegasus-Klasse: Zeit [frames], die verstreichen muss, bis der Interphasengenerator aktiviert wird
		platingDurabilityFactor,					// SpezialUpgrade; = 2, wenn erforscht, sonst = 1; Faktor, der die Standardpanzerung erhöht
		nr_of_cannons,						// Anzahl der Kanonen; mögliche Werte: 1, 2 und 3
		rapidfire,							// nur Kamaitachi-Klasse: SpezialUpgrade; = 2, wenn erforscht, sonst = 0;	
		
		// Timer
		plasma_activation_timer,			// nur Kamaitachi-Klasse: Timer zur Überwachung der Zeit [frames], in der die Plasma-Raketen aktiviert sind
		generator_timer,					// nur Pegasus-Klasse: Timer stellt sicher, dass eine Mindestzeit zwischen zwei ausgelösten EMPs liegt
		slowed_timer,						// reguliert die Verlangsamung des Helicopters durch gegnerische Geschosse						
		recent_dmg_timer,					// aktiv, wenn Helicopter kürzlich Schaden genommen hat; für Animation der Hitpoint-Leiste
			interphaseGeneratorTimer,			// nur Pegasus-Klasse: Zeit [frames] seit der letzten Offensiv-Aktion; bestimmt, ob der Interphasengenerator aktiviert ist
		enhanced_radiation_timer,				
		
		// für die Spielstatistik
		nr_of_crashes,						// Anzahl der Abstürze
		nr_of_repairs,						// Anzahl der Reparaturen 
		missile_counter,					// Anzahl der abgeschossenen Raketen 
		hit_counter,						// Anzahl der getroffenen Gegner 
		enemies_seen,						// Anzahl der erschienenen Gegner 
		enemies_killed,						// Anzahl der vernichteten Gegner 
		mini_boss_seen,						// Anzahl der erschienenen Mini-Bosse 
		mini_boss_killed,					// Anzahl der vernichteten Mini-Bosse
				
		// nur für Phönix- und Kamaitachi-Klasse
		bonus_kills,						// Anzahl der Kills, für den aktuelken Mulikill-Award
		bonus_kills_money,					// Gesamtverdienst am Abschuss aller Gegner innerhalb des aktuellen Multikill-Awards ohne Bonus
		bonus_kills_timer,					// reguliert die Zeit, innerhalb welcher Kills für den Multikill-Award berücksichtigt werden
				
		powerUp_timer[] = new int [4], 				// Zeit [frames] in der das PowerUp (0: bonus dmg; 1: invincible; 2: endless energy; 3: bonus fire rate) noch aktiv ist
		   		
		// Standard-Upgrades: Index 0: Hauptrotor, 1: Raketenantrieb, 2: Panzerung, 3: Feuerkraft, 4: Schussrate, 5: Energie-Upgrade
		upgrade_costs[] = new int[6],    			// Preisniveau für alle 6 StandardUpgrades der aktuellen Helikopter-Klasse
		levelOfUpgrade[] = new int[6];			// Upgrade-Level aller 6 StandardUpgrades
	
	public long
    	past_teleport_time,						// nur Phönix-Klasse: Zeitpunkt der letzten Nutzung des Teleporters
    	scorescreen_times [] = new long [5];	// Zeit, die bis zum Besiegen jedes einzelnen der 5 Bossgegner vergangen ist
		
    public float
		rotorSystem,					// legt die aktuelle Geschwindigkeit des Helikopters fest
		jumboMissiles,					// Faktor, welcher die Feuerkraft beeinflusst; = jumbo_missle_dmg_factor, wenn Jumbo-Raketen erforscht, sonst = 1
		currentPlating,				// aktuelle Panzerung (immer <= maximale Panzerung)
    	energy,							// verfügbare Energie;
    	regeneration_rate,				// Energiezuwachs pro Simulationsschritt
    	spell_costs,					// Energiekosten für die Nutzung des Energie-Upgrades
    	rotor_position[] = new float[NR_OF_TYPES];	// Stellung des Helikopter-Hauptrotors für alle Klassen; genutzt für die Startscreen-Animation
    		
    public boolean spotlight,					// = true: Helikopter hat Scheinwerfer
		hasShortrangeRadiation,	// = true: Helikopter verfügt über Nahkampfbestrahlng
		hasPiercingWarheads,		// = true: Helikopterraketen werden mit Durchstoß-Sprengköpfen bestückt
		hasRadarDevice,			// = true: Helikopter verfügt über eine Radar-Vorrichtung
		hasInterphaseGenerator,	// = true: Helikopter verfügt über einen Interphasen-Generator
		hasPowerUpImmobilizer,	// = true: Helikopter verfügt über einen Interphasen-Generator
     	next_missile_is_stunner,   	// = true: die nächste abgeschossene Rakete wird eine Stopp-Rakete
     	active,						// = false: Helikopter ist nicht in Bewegung und kann auch nicht starten, Raketen abschießen, etc. (vor dem ersten Start oder nach Absturz = false)
     	damaged,    				// = true: Helikopter hat einen Totalschaden erlitten
     	rotor_system_active,		// = true: Propeller dreht sich / Helikopter fliegt
     	continious_fire,			// = true: Dauerfeuer aktiv
     	search4teleportDestination,	// = true: es wird gerade ein Zielort für den Teleportationvorgang ausgewählt
     	is_moving_left,
     	no_cheats_used,				// = true: Spielstand kann in die Highscore übernommen werden, da keine cheats angewendet wurden
     	has_max_upgrade_level[] = new boolean[6],	// = true: für diese Upgrade wurde bereits die maximale Ausbaustufe erreich
     		     		
     	// nur für Roch-Klasse
     	power_shield_on;				// = true: Power-Shield ist aktiviert
     
    public Point
    	destination = new Point(), 				// dorthin fliegt der Helikopter
  		prior_teleport_location = new Point(); 	// nur für Phönix-Klasse: Aufenthaltsort vor Teleportation		 
	
	public Point2D
  		location = new Point2D.Float(),	// exakter Aufenthaltsort	
  		next_location = new Point2D.Float();
    
  	public Enemy 
  		tractor;			// Referenz auf den Gegner, der den Helikopter mit einem Traktorstrahl festhält
 	    
  	public Explosion
  	 	emp_wave;			// Pegasus-Klasse: Referenz auf zuletzt ausgelöste EMP-Schockwelle
	
	private int
    	fire_rate_timer;  	// reguliert die Zeit [frames], die mind. vergehen muss, bis wieder geschossen werden kann
    
    private float 
    	speed;     			// aktuelle Geschwindigkeit des Helikopters
    
    private boolean 
    	nosedive;			// Helikopter befindet sich im Sturzflug
      
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
    
    public void paint(Graphics2D g2d, int timeOfDay)
    {
    	paint(g2d, this.paintBounds.x, this.paintBounds.y, this.getType(), timeOfDay, false);
    }
    
    public void paint(Graphics2D g2d, int left, int top, HelicopterTypes helicopterType, int timeOfDay)
    {
    	paint(g2d, left, top, helicopterType, timeOfDay, false);
    }
    
    public void paint(Graphics2D g2d, int left, int top, HelicopterTypes helicopterType, int timeOfDay, boolean unlockedPainting)
    {
    	// die Farben    	
    	if(unlockedPainting)
    	{
    		this.inputColorCannon = MyColor.helicopterColor[helicopterType.ordinal()][this.hasGoliathPlating() ? 3 : 2];
    	}
    	else if(this.plasma_activation_timer >= POWERUP_DURATION/4  || 
    		(Events.window == STARTSCREEN && helicopterType == KAMAITACHI && Menu.effect_timer[KAMAITACHI.ordinal()] > 0 && Menu.effect_timer[KAMAITACHI.ordinal()] < 35))
    	{
    		this.inputColorCannon = Color.green;
    	}
    	else if(this.plasma_activation_timer == 0)
    	{
    		if(helicopterType == OROCHI
    					  &&( (this.next_missile_is_stunner 
    							&& (this.energy >= this.spell_costs
    								|| this.has_unlimited_energy())) 
    						  || 
    						  (Events.window == STARTSCREEN 
    						  	&& Menu.effect_timer[OROCHI.ordinal()] > 1
    						  	&& Menu.effect_timer[OROCHI.ordinal()] < 80)) ) // 70
    		{
    			this.inputColorCannon = MyColor.variableBlue;
    		}
    		else if(Events.window == STARTSCREEN
    				&& ( (helicopterType == KAMAITACHI
    						&& Menu.effect_timer[KAMAITACHI.ordinal()] >= 35
    						&& Menu.effect_timer[KAMAITACHI.ordinal()] < 100)
    					 ||
    					 (helicopterType == PHOENIX
    					 	&& Menu.effect_timer[PHOENIX.ordinal()] > 1
    					 	&& Menu.effect_timer[PHOENIX.ordinal()] < 55) ))
    		{
    			this.inputColorCannon = MyColor.variableGreen;
    		}
    		else{this.inputColorCannon 
    				= this.is_invincible() 
    					? MyColor.variableGreen 
    					: MyColor.helicopterColor[helicopterType.ordinal()][this.hasGoliathPlating() ? 3 : 2];}
    	}
    	else
    	{
    		this.inputColorCannon 
    			= this.is_invincible() 
    				? MyColor.reversed_RandomGreen() 
    				: MyColor.variableGreen;
    	}
    	this.inputColorHull = unlockedPainting
    						  || (!this.is_invincible() 
    						     && !(Events.window == STARTSCREEN 
    						        && helicopterType == PHOENIX
    						        && Menu.effect_timer[PHOENIX.ordinal()] > 1
    						        && Menu.effect_timer[PHOENIX.ordinal()] < 55))
    						  ? MyColor.helicopterColor[helicopterType.ordinal()][unlockedPainting ? 0 : this.hasGoliathPlating() ? 1 : 0]
    						  : MyColor.variableGreen;
    	this.inputColorWindow = !unlockedPainting
    								&& (this.has_triple_dmg() 
    									|| this.has_boosted_fire_rate()) 
    								|| (Events.window == STARTSCREEN 
    										&& helicopterType == HELIOS
    										&& Menu.effect_timer[HELIOS.ordinal()] > 0
    										&& Menu.effect_timer[HELIOS.ordinal()] < 65)
    								? MyColor.variableRed 
    								: MyColor.windowBlue;
    	this.inputColorFuss1 = MyColor.lighterGray;
    	this.inputColorFuss2 = MyColor.enemyGray;
    	this.inputGray = MyColor.gray;
    	this.inputLightGray = MyColor.lightGray;
    	this.inputLamp = (!unlockedPainting && Events.timeOfDay == NIGHT && Events.window == GAME) ? MyColor.randomLight : MyColor.darkYellow;
    	    	
    	if(!unlockedPainting && this.interphaseGeneratorTimer > this.shift_time)
    	{    		
    		this.inputColorCannon = MyColor.setAlpha(this.inputColorCannon, INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay]);
    		this.inputColorHull =   MyColor.setAlpha(this.inputColorHull, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay]);
    		this.inputColorWindow = MyColor.setAlpha(this.inputColorWindow, INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay] );
    		this.inputColorFuss1 =  MyColor.setAlpha(this.inputColorFuss1, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay]);
    		this.inputColorFuss2 =  MyColor.setAlpha(this.inputColorFuss2, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay]);
    		this.inputGray = 		MyColor.setAlpha(this.inputGray, 		INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay]);
    		this.inputLightGray = 	MyColor.setAlpha(this.inputLightGray, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay]);
    		this.inputLamp = 		MyColor.setAlpha(this.inputLamp, 		INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay]);
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
    	this.gradientCannonHole = (this.plasma_activation_timer == 0 || unlockedPainting)  ? this.gradientHull : MyColor.cannolHoleGreen;
    	
    	boolean movement_left = this.is_moving_left && Events.window == GAME && !unlockedPainting;
    	    	
    	// Nahkampfbestrahlung 
    	if(!unlockedPainting && this.hasShortrangeRadiation)
        {            
            g2d.setColor(this.enhanced_radiation_timer == 0 
            				? MyColor.radiation[Events.timeOfDay]
            				: MyColor.enhanced_radiation[Events.timeOfDay]);
            g2d.fillOval(left+(movement_left ? -9 : 35), top+19, 96, 54);
        }
    	    	
    	// Propeller-Stange
    	g2d.setColor(this.inputLightGray);  
    	g2d.setStroke(new BasicStroke(2));
    	g2d.drawLine(left+(movement_left ? 39 : 83), top+14, left+(movement_left ? 39 : 83), top+29);
    	
    	// Fußgestell
    	g2d.setPaint(this.gradientFuss2);
        g2d.fillRoundRect(left+(movement_left ? 25 : 54), top+70, 43, 5, 5, 5);
        g2d.setPaint(this.gradientFuss1);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2d.drawLine(left+61, top+66, left+61, top+69); 
        g2d.drawLine(left+(movement_left ? 33 : 89), top+66, left+(movement_left ? 33 : 89), top+69); 
        g2d.setStroke(new BasicStroke(1));
        
        // der Helikopter-Rumpf
        g2d.setPaint(this.gradientHull);
        g2d.fillOval(left+(movement_left ?  2 : 45), top+29, 75, 34);
        g2d.fillRect(left+(movement_left ? 92 : -7), top+31, 37,  8);        
        g2d.fillArc (left+(movement_left ? 34 : 23), top+11, 65, 40, 180, 180); 
        g2d.setPaint(this.gradientWindow);        
        g2d.fillArc (left+(movement_left ?  1 : 69), top+33, 52, 22, (movement_left ? 75 : -15), 120);
                
        // die Kanonen
        g2d.setPaint(this.gradientCannon1);        
        g2d.fillRoundRect(left+(movement_left ? 26 : 53), top+52, 43, 13, 12, 12);    
        g2d.setPaint(this.gradientCannonHole); 
        g2d.fillOval(left+(movement_left ? 27 : 90), top+54, 5, 9);
        if(!unlockedPainting && this.nr_of_cannons >= 2)
        {           
            g2d.setPaint(this.gradientCannon2and3);            
            g2d.fillRoundRect(left+(movement_left ? 32 : 27), top+27, 63, 6, 6, 6);
            g2d.setPaint(this.gradientCannonHole);
            g2d.fillOval(left+(movement_left ? 33 : 86), top+28, 3, 4);
        }
        if(!unlockedPainting && this.nr_of_cannons >= 3)
        {
        	g2d.setPaint(this.gradientCannon2and3);
        	g2d.fillRoundRect(left+(movement_left ? 38 : 37), top+41, 47, 6, 6, 6);
        	g2d.setPaint(this.gradientCannonHole);
            g2d.fillOval(left+(movement_left ? 39 : 80), top+42, 3, 4);
        }
        
        //der Scheinwerfer
        if(!unlockedPainting && this.spotlight)
        {            
        	if(Events.timeOfDay == NIGHT && Events.window == GAME)
        	{
        		g2d.setColor(MyColor.translucentWhite);
                g2d.fillArc(left+(movement_left ? -135 : -43), top-96, 300, 300, (movement_left ? 165 : -15), 30);
        	}        	
        	g2d.setPaint(this.gradientHull);
            g2d.fillRect(left+(movement_left ? 4 : 106), top+50, 12, 8);
        	g2d.setColor(this.inputLamp);
            g2d.fillArc(left+(movement_left ? -1 : 115), top+50, 8, 8, (movement_left ? -90 : 90), 180);
        }        
                
        //die Propeller        
        paint_rotor(g2d, 
        			this.inputGray, 
        			left+(movement_left ? -36 : 8), 
        			top-5, 
        			150, 37, 3, 
        			(int)(this.rotor_position[helicopterType.ordinal()]),
        			12, 
        			this.rotor_system_active, 
        			false);
        
        paint_rotor(g2d, 
        			this.inputGray,
        			left+(movement_left ?  107 : -22),
        			top+14,
        			37, 37, 3,
        			(int)(this.rotor_position[helicopterType.ordinal()]),
        			12, 
        			this.rotor_system_active, 
        			false);
        
        if(Events.window == STARTSCREEN 
        	&& helicopterType == PEGASUS
        	&& Menu.effect_timer[helicopterType.ordinal()] > 0
        	&& this.emp_wave != null)
        {
        	if(this.emp_wave.time >= this.emp_wave.max_time)
        	{
        		this.emp_wave = null;
        	}
        	else
        	{
        		this.emp_wave.update();
        		this.emp_wave.paint(g2d);
        	}
        }
        
        // Energie-Schild der Roch-Klasse
        if(!unlockedPainting
        	&& (this.power_shield_on 
    			|| (Events.window == STARTSCREEN 
    				&& helicopterType == ROCH
    				&& Menu.effect_timer[ROCH.ordinal()] > 0
    				&& Menu.effect_timer[ROCH.ordinal()] < 68))) // 60
        {            
            g2d.setColor(MyColor.shieldColor[timeOfDay]);
            g2d.fillOval(left+(movement_left ? -9 : 35), top+19, 96, 54);
        }
               
        if(Events.recordTime[helicopterType.ordinal()][4] > 0 && Events.window == STARTSCREEN)
        {            
            g2d.setFont(BOLD12);
            g2d.setColor(Color.yellow);
            g2d.drawString(Menu.language == ENGLISH ? "Record time:" : "Bestzeit:", left-27, top+67);
            g2d.drawString(Menu.minuten(Events.recordTime[helicopterType.ordinal()][4]),left-27, top+80);
        } 
        
        if(helicopterType == HELIOS && Events.window == STARTSCREEN)
        {            
            g2d.setFont(BOLD12);
            g2d.setColor(MyColor.brown);
            g2d.drawString("Hardcore" + (Menu.language == ENGLISH ? " mode" : "-Modus:"), left-27, top-4);
        }  
            
        //zu Testzwecken: 
        /*
        g2d.setColor(Color.red);
        g2d.draw(this.bounds);
        g2d.fillOval((int) this.location.getX()-2, (int) this.location.getY()-2, 4, 4); */
    }

	public static void paint_rotor(Graphics2D g2d, Color color, 
                            int x, int y, int width, int height, 
	                        int nr_of_blades, int pos, int blade_width,
	                        float border_distance, boolean active)
	{
    	int distance_x = (int) (border_distance * width),
    		distance_y = (int) (border_distance * height);
    	paint_rotor(g2d, color, 
    				x+distance_x,
    				y+distance_y, 
    				width-2*distance_x,
    				height-2*distance_y, 
    				nr_of_blades, pos, blade_width, active, true);
	}   
    
	static void paint_rotor(Graphics2D g2d, Color color, 
	                        int x, int y, int width, int height, 
	                        int nr_of_blades, int pos, int blade_width,
	                        boolean active, boolean enemie_paint)
	{
		if(active)
	    {
	       	g2d.setColor((Events.timeOfDay == DAY || enemie_paint) ? MyColor.translucentGray : MyColor.translucentWhite); 
	       	g2d.fillOval(x, y, width, height); 
	    }
	    g2d.setColor(color);	        
	    for(int i = 0; i < nr_of_blades; i++)
	    {
	       	g2d.fillArc(x, y, width, height, -10-pos+i*(360/nr_of_blades), blade_width);   
	    }		
	}

	public void update(	ArrayList<LinkedList<Missile>> missile,
	                   	ArrayList<LinkedList<Explosion>> explosion)
	{
		this.update_timer();
		if(this.can_regenerate_energy()){this.regenerate_energy();}
		if(this.power_shield_on && this.energy == 0)
		{
			this.shut_down_power_shield();
		}
		this.evaluate_fire(missile);
		this.move(explosion);
	}
    
	private boolean can_regenerate_energy()
	{		
		return 	!this.damaged
	    		&& !this.power_shield_on
	    		&& !this.next_missile_is_stunner;
	}

	private void update_timer()
	{
		if(this.recent_dmg_timer	     > 0){this.recent_dmg_timer--;}	
		if(this.enhanced_radiation_timer > 0){this.enhanced_radiation_timer--;}
		if(this.generator_timer		     > 0){this.generator_timer--;}
		if(this.slowed_timer		     > 0){this.slowed_timer--;}	
			
		this.evaluate_power_up_activation_states();				
		if(this.plasma_activation_timer > 0)
		{
			this.plasma_activation_timer--;
			if(this.plasma_activation_timer == 30){
                Audio.play(Audio.plasma_off);}
		}		
		if(this.getType() == PHOENIX || this.getType() == KAMAITACHI)
		{
			this.evaluate_bonus_kills();
		}				
		if(this.hasInterphaseGenerator && !this.damaged)
		{
			this.update_interphase_generator();			
		}
	}
	
	void regenerate_energy()
    {
    	this.energy 
			= Math.max(
				0,
				Math.min(	
					this.energy 
						+ (this.power_shield_on 
							? this.has_unlimited_energy() 
								? 0
								: POWER_SHIELD_E_LOSS_RATE
							: this.regeneration_rate), 
					MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY])));
    }
			
	private void evaluate_fire(ArrayList<LinkedList<Missile>> missile)
	{
    	if(this.is_ready_for_shooting()){this.shoot(missile);}		
    	this.fire_rate_timer++;
	}
	
	public boolean has_triple_dmg()
	{		
		return this.powerUp_timer[TRIPLE_DMG.ordinal()] > 0;
	}
	
	public boolean is_invincible()
	{		
		return this.powerUp_timer[INVINCIBLE.ordinal()] > 0;
	}
	
	public boolean has_unlimited_energy()
	{		
		return this.powerUp_timer[UNLIMITRED_ENERGY.ordinal()] > 0;
	}
	
	private boolean has_boosted_fire_rate()
	{		
		return this.powerUp_timer[BOOSTED_FIRE_RATE.ordinal()] > 0;
	}
		
	private boolean is_ready_for_shooting()
	{
		return   	this.continious_fire 
    			&& !this.damaged 
    			&& !this.is_on_the_ground() 
    			&&  this.fire_rate_timer >= this.time_between_2_shots;
	}

	private void shoot(ArrayList<LinkedList<Missile>> missile)
	{
    	if(this.hasPiercingWarheads){Audio.play(Audio.launch2);}
		else{Audio.play(Audio.launch1);}
		this.fire_rate_timer = 0;
		this.missile_counter += this.nr_of_cannons;				
		boolean stunning_missile = false;
		if(this.interphaseGeneratorTimer > this.shift_time
		   ||(this.next_missile_is_stunner 
			  && (this.energy >= this.spell_costs
			      || this.has_unlimited_energy())))
		{
			if(this.getType() == OROCHI)
			{
				this.energy -= this.has_unlimited_energy() 
								? 0 
								: this.spell_costs;
			}
			stunning_missile = true;
		}
		Missile sister = null;
		if(this.nr_of_cannons >= 1)
		{
			Iterator<Missile> i = missile.get(INACTIVE).iterator();
			Missile m;					
			if(i.hasNext()){m = i.next(); i.remove();}	
			else{m = new Missile();}					
			if(this.getType() == ROCH || this.getType() == OROCHI)
			{
				m.sister[0] = null;
				m.sister[1] = null;						
				sister = m;
			}
			missile.get(ACTIVE).add(m);
			m.launch(this, stunning_missile, 56);					
		}
		if(this.nr_of_cannons >= 2)
		{
			Iterator<Missile> i = missile.get(INACTIVE).iterator();
			Missile m;
			if(i.hasNext()){m = i.next(); i.remove();}	
			else{m = new Missile();}											
			if(  sister != null && sister.sister != null && 
			    (this.getType() == ROCH || this.getType() == OROCHI))
			{
				m.sister[0] = sister;
				m.sister[1] = null;	
				sister.sister[0] = m;
				sister = m;
			}
			missile.get(ACTIVE).add(m);
			m.launch(this, stunning_missile, 28);				
		}
		if(this.nr_of_cannons >= 3)
		{
			Iterator<Missile> i = missile.get(INACTIVE).iterator();
			Missile m;
			if(i.hasNext()){m = i.next(); i.remove();}	
			else{m = new Missile();}				
			if(  sister != null && sister.sister != null && 
			    (this.getType() == ROCH || this.getType() == OROCHI))
			{
				m.sister[0] = sister.sister[0];
				m.sister[1] = sister;
				sister.sister[0].sister[1] = m;
				sister.sister[1] = m;
			}
			missile.get(ACTIVE).add(m);
			m.launch(this, stunning_missile, 42);
		}
		if(this.hasInterphaseGenerator)
		{
			Audio.phase_shift.stop();
			this.interphaseGeneratorTimer = 0;
		}		
	}

	private void move(ArrayList<LinkedList<Explosion>> explosion)
    {
		if(this.is_on_the_ground())
		{
			this.rotor_system_active = false;
		}
		
		float
    		next_x = (float) this.location.getX(),
    		next_y = (float) this.location.getY();
    	
    	if(this.nosedive)
    	{
    		next_y += NOSEDIVE_SPEED;
    	}
    	else if(this.active && this.tractor == null)
    	{
    		this.speed = (this.slowed_timer > 0 ) ? 1.5f : this.rotorSystem;
    		float fraction = (float) (this.speed/this.location.distance(this.destination.x, this.destination.y));
    		
    		if(fraction < 1)
        	{
        		if(!(   this.bounds.getMaxY() + NO_COLLISION_HEIGHT  >= GROUND_Y 
        			 && this.destination.y >= GROUND_Y))
        		{
        			next_x += (float)(fraction*(this.destination.x - this.location.getX()) - 1);
        		}        		   					
        		    next_y += (float)(fraction*(this.destination.y - this.location.getY()));
        	}    		
    		else
        	{
    			next_x = this.destination.x;
        		next_y = this.destination.y;
        	}
    	}    
    	
    	boolean in_the_air = this.location.getY() != 407d;
    	float last_x = (float)this.location.getX();
    	
    	this.next_location.setLocation(next_x, next_y);    	
    	this.correct_and_set_coordinates();   	
    	
    	if(Enemy.current_nr_of_barriers > 0 && !this.damaged)
    	{
    		for(int i = 0; i < Enemy.current_nr_of_barriers; i++)
    		{    			
    			Enemy enemy = Enemy.living_barrier[i];    			
    			enemy.last_touched_site = enemy.touched_site;
    			if(this.is_location_adaption_approved(enemy))
    			{
    				this.adaptPosTo(enemy);
    	   	 		this.correct_and_set_coordinates();
    	   	 		if(enemy.is_statically_charged())
    	   	 		{
    	   	 			enemy.start_static_discharge(explosion, this);
    	   	 		}
    			}
    			else
    			{
    				enemy.touched_site = NONE;
    			}
    			if(enemy.touched_site == NONE)
    			{
    				enemy.untouched_counter++;
    				if(enemy.untouched_counter > 2)
    				{
    					enemy.untouched_counter = 0;
    					enemy.is_touching_helicopter = false;
    				}
    			}
    			else
    			{
    				enemy.untouched_counter = 0;
    			}    			
    		}
    		for(int i = 0; i < Enemy.current_nr_of_barriers; i++)
    		{    			
    			Enemy.living_barrier[i].evaluate_pos_adaption(this);
    		}
    	}  
    	    	
    	if(this.active && this.tractor == null)
    	{   
    		if(!this.nosedive)
        	{    		
        		if(this.bounds.getMaxY() + NO_COLLISION_HEIGHT != GROUND_Y
        			|| last_x != (float)this.location.getX())
        		{
        			this.rotor_system_active = true;
        		}
        		if(in_the_air && !(this.location.getY() != 407d)){Audio.play(Audio.landing);}
        	} 
        	else if(in_the_air && this.location.getY() == 407d)
        	{
        		this.crashed(explosion);
        	}
    	}
    	if(this.rotor_system_active){this.rotate_propeller(12);}
    	this.set_paint_bounds();
    }
   


	public boolean is_location_adaption_approved(Enemy enemy)
	{		
		return enemy.bounds.intersects(this.bounds)
				&& this.interphaseGeneratorTimer <= this.shift_time
				&& enemy.alpha == 255 
				&& enemy.borrow_timer != READY;
	}

	void adaptPosTo(Enemy enemy)
	{		
		double 
			x = this.bounds.getCenterX() - enemy.bounds.getCenterX(),
		 	y = this.bounds.getCenterY() - enemy.bounds.getCenterY(),
			pseudoAngle = (x/MyMath.ZERO_POINT.distance(x, y)),
			distance,
			local_speed = enemy.has_unresolved_intersection ? this.speed : Double.MAX_VALUE;
			
		if(pseudoAngle > MyMath.ROOT05)
		{
			// Right	
			// new pos x: enemy.getMaxX() + (this.moves_left ? 39 : 83) 
			distance = (enemy.bounds.getX() + enemy.bounds.getWidth()) + (this.is_moving_left ? 39 : 83) - this.location.getX();
			this.next_location.setLocation(
				this.location.getX() + (distance > local_speed ? local_speed : distance),
				this.location.getY());
			enemy.touched_site = RIGHT;
		}
		else if(pseudoAngle < -MyMath.ROOT05)
		{
			// Left
			// new pos x: enemy.bounds.x - this.bounds.getWidth() + (this.moves_left ? 39 : 83)
			distance = this.location.getX() - enemy.bounds.getX() + this.bounds.getWidth() - (this.is_moving_left ? 39 : 83);
			this.next_location.setLocation(
				this.location.getX() - (distance > local_speed ? local_speed : distance),
				this.location.getY());
			enemy.touched_site = LEFT;
		}
		else 
		{			
			if(this.bounds.getCenterY() > enemy.bounds.getCenterY())
			{
				// Bottom	
				// new pos y: enemy.bounds.getMaxY() + 56
				distance = enemy.bounds.getMaxY() + 56 - this.location.getY();
				this.next_location.setLocation(
					this.location.getX(),
					this.location.getY() + (distance > local_speed ? local_speed : distance));		
				enemy.touched_site = BOTTOM;
			}
			else
			{
				// Top	
				// new pos y: enemy.bounds.y - this.bounds.getHeight() + 56
				distance = this.location.getY() - enemy.bounds.getY() + this.bounds.getHeight() - 56;
				this.next_location.setLocation(
					this.location.getX(),
					this.location.getY() - (distance > local_speed ? local_speed : distance));
				enemy.touched_site = TOP;
			}
			if(this.tractor != null){this.stop_tractor();}
		}
	}
	
	private void correct_and_set_coordinates()
	{    	
    	this.location.setLocation
		(
			Math.max(40, Math.min(1024, this.next_location.getX())), 
			Math.max(32, Math.min(407, this.next_location.getY()))
		);    	    	
   	 	this.set_bounds();
	}

	void set_bounds()
	{
		this.bounds.setRect(
	   	 	this.location.getX() 
	   	 		- (this.is_moving_left 
	   	 			? FOCAL_PNT_X_LEFT
	   	 			: FOCAL_PNT_X_RIGHT),
	   	 	this.location.getY() - FOCAL_PNT_Y_POS,
	   	 	this.bounds.getWidth(),
	   	 	this.bounds.getHeight());
	}
	
	public void initialize(boolean new_game, Savegame savegame)
    {
    	for(int i = 0; i < 6; i++)
	    {
    		this.has_max_upgrade_level[i] = false;
    		this.upgrade_costs[i] = this.getType() == HELIOS ? helios_costs(i) : COSTS[this.getType().ordinal()][i];
    		if(new_game){this.levelOfUpgrade[i] = this.upgrade_costs[i] < 2 ? 2 : 1;}
	    }
    	if(!new_game){this.restore_last_game_state(savegame);}    	
    	this.update_properties(new_game);
    	this.fire_rate_timer = this.time_between_2_shots;
        this.rotor_position[this.getType().ordinal()] = 0;
        this.emp_wave = null;
    }
    
	private void restore_last_game_state(Savegame savegame)
	{
		this.levelOfUpgrade = savegame.level_of_upgrade.clone();
		this.spotlight = savegame.spotlight;
		this.platingDurabilityFactor = savegame.platingDurabilityFactor;
		this.hasShortrangeRadiation = savegame.has_shortrange_radiation;
		this.hasPiercingWarheads = savegame.has_piercing_warheads;
		this.jumboMissiles = savegame.jumbo_missiles;
		this.nr_of_cannons = savegame.nr_of_cannons;
		this.hasRadarDevice = savegame.has_radar_device;
		this.rapidfire = savegame.rapidfire;
		this.hasInterphaseGenerator = savegame.has_interphase_generator;
		this.hasPowerUpImmobilizer = savegame.has_PowerUp_immobilizer;
		this.currentPlating = savegame.current_plating;
		this.energy = savegame.energy;		
		this.enemies_seen = savegame.enemies_seen;
		this.enemies_killed = savegame.enemies_killed;
		this.mini_boss_seen = savegame.mini_boss_seen;
		this.mini_boss_killed = savegame.mini_boss_killed;
		this.nr_of_crashes = savegame.nr_of_crashes; 
		this.nr_of_repairs = savegame.nr_of_repairs;
		this.no_cheats_used = savegame.no_cheats_used;
		this.missile_counter = savegame.missile_counter;
		this.hit_counter = savegame.hit_counter;
		
		this.scorescreen_times = savegame.scorescreen_times.clone();
	}	
    
    public void reset()
    {

        for(int i=0; i < 4; i++){this.rotor_position[i]=0;}
        this.reset_state();
        this.placeAtStartpos();
        this.damaged = false;
        this.nr_of_crashes = 0;
        this.nr_of_repairs = 0;
        this.no_cheats_used = true;
        this.missile_counter = 0;
        this.hit_counter = 0;
        this.enemies_seen = 0;
        this.enemies_killed = 0;
        this.mini_boss_seen = 0;
        this.mini_boss_killed = 0;
        this.spotlight = false;
        this.platingDurabilityFactor = STANDARD_PLATING_STRENGTH;
        this.hasShortrangeRadiation = false;
        this.hasPiercingWarheads = false;
        this.jumboMissiles = 1;
        this.nr_of_cannons = 1;
        this.hasRadarDevice = false;
        this.rapidfire = 0;
        this.hasInterphaseGenerator = false;
        this.hasPowerUpImmobilizer = false;
        
        Arrays.fill(this.scorescreen_times, 0);
    }
       
    public void reset_state(){reset_state(true);}
    public void reset_state(boolean reset_start_pos)
    {
    	this.set_activation_state(false);
    	this.search4teleportDestination = false;
		this.next_missile_is_stunner = false;
		this.nosedive = false;
		this.interphaseGeneratorTimer = 0;
		this.plasma_activation_timer = 0;
		this.power_shield_on = false;
		this.slowed_timer = 0;		
		this.recent_dmg_timer = 0; 
		this.enhanced_radiation_timer = 0;
		for(int i = 0; i < 4; i++){this.powerUp_timer[i] = 0;}	
		this.generator_timer = 0;
		this.emp_wave = null;
		this.rotor_position[this.getType().ordinal()] = 0;
		if(reset_start_pos){this.placeAtStartpos();}
		this.fire_rate_timer = this.time_between_2_shots;
    }
    
    public void repair(boolean restore_energy, boolean cheat_repair)
    {
    	Audio.play(Audio.cash);
    	this.nr_of_repairs++;
		this.damaged = false;
		this.nosedive = false;
    	this.get_max_plating();
    	this.setPlatingColor();
		if(restore_energy){this.energy = MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY]);}
		if(!cheat_repair){this.placeAtStartpos();}
		Menu.repairShopButton.get("RepairButton").costs = 0;
    }
	
	public void obtainAllUpgrades()
    {
    	for(int i = 0; i < 6; i++)
    	{
    		this.levelOfUpgrade[i] = MyMath.max_level(this.upgrade_costs[i]);
    	}
    	this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
    	this.hasPiercingWarheads = true;
    	if(this.getType() == OROCHI){this.nr_of_cannons = 3;}
    	else{this.nr_of_cannons = 2;}
    	this.update_properties(true);
		this.damaged = false;
    	Menu.update_repairShopButtons(this);
    	this.no_cheats_used = false;
    }
	
	public void obtainSomeUpgrades()
    {
    	this.spotlight = true;
    	if(this.getType() == PHOENIX){this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;}
    	else if(this.getType() == ROCH){this.hasPiercingWarheads = true;}
    	else if(this.getType() == OROCHI && this.nr_of_cannons < 3){this.nr_of_cannons = 2;}
    	this.obtainFifthSpecial();
    	for(int i = 0; i < 6; i++)
    	{
    		if(this.levelOfUpgrade[i] < 6){this.levelOfUpgrade[i] = 6;}
    	}        		
    	this.update_properties(true);
		this.damaged = false;
    	Menu.update_repairShopButtons(this);    
    	this.no_cheats_used = false;
    }
	
	public boolean hasSomeUpgrades()
    {
    	for(int i = 0; i < 6; i++){if(this.levelOfUpgrade[i] < 6) return false;}
    	if(!this.spotlight) return false;
    	else return this.hasFifthSpecial();
    }
	
	public boolean hasFifthSpecial()
    {
    	switch (this.getType())
    	{
    		case PHOENIX:
				return this.hasShortrangeRadiation;
			case ROCH:
				return this.jumboMissiles > 2;
			case OROCHI:
				return this.hasRadarDevice;
			case KAMAITACHI:
				return this.rapidfire == 2;
			case PEGASUS:
				return this.hasInterphaseGenerator;
			case HELIOS:
				return this.hasPowerUpImmobilizer;
			default: return false;
    	}
    }
	
	public void obtainFifthSpecial()
    {
    	switch (this.getType())
    	{
    		case PHOENIX:
    			this.hasShortrangeRadiation = true;
    			break;
    		case ROCH:
    			this.jumboMissiles = JUMBO_MISSILE_DMG_FACTOR;
    			this.currentFirepower = (int)(this.jumboMissiles * MyMath.dmg(this.levelOfUpgrade[FIREPOWER]));
    			break;
    		case OROCHI:
    			this.hasRadarDevice = true;
    			break;
    		case KAMAITACHI:
    			this.rapidfire = 2;
    			break;
    		case PEGASUS:
    			this.hasInterphaseGenerator = true;
    			break;	
    		case HELIOS:
    			this.hasPowerUpImmobilizer = true;
    			break;    		
    		default:
				break;
    	}
    }
    
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
		return this.nr_of_cannons == 3 || (this.nr_of_cannons == 2 && this.getType() != OROCHI);
	}
	
	public boolean has_all_upgrades()
    {
        for(int i = 0; i < 6; i++){if(!this.has_max_upgrade_level[i]){return false;}}
		return hasAllSpecials();
	}
	
	public void rotate_propeller(float rotational_speed){rotate_propeller(this.getType(), rotational_speed);}
	public void rotate_propeller(HelicopterTypes type, float rotational_speed)
    {
    	this.rotor_position[type.ordinal()] += rotational_speed;
		if(this.rotor_position[type.ordinal()] > 360){this.rotor_position[type.ordinal()] -= 360;}
    }    
    
    private void placeAtStartpos()
    {
    	this.is_moving_left = false;
    	this.bounds.setRect(INITIAL_BOUNDS);
    	this.location.setLocation(this.bounds.getX() + FOCAL_PNT_X_RIGHT, 
    							  INITIAL_BOUNDS.y + FOCAL_PNT_Y_POS);    	
    	this.set_paint_bounds();
    }
    
    public void stop_tractor()
	{
		Audio.tractor_beam.stop();
		this.tractor.stop_tractor();		
		this.tractor = null;
	}
    
    public void crash()
    {
    	this.damaged = true;
		this.rotor_system_active = false;
		this.energy = 0;
		this.destination.setLocation(this.bounds.getX() + 40, 520);	
		this.plasma_activation_timer = 0;
		
		if(this.power_shield_on){this.shut_down_power_shield();}
		if(this.tractor != null){this.stop_tractor();}
		if(this.hasInterphaseGenerator){Audio.phase_shift.stop();}
		this.nr_of_crashes++;
		if(this.location.getY() == 407d){this.crashed(Controller.getInstance().explosion);}
		else{this.nosedive = true;}
    }
    
    private void crashed(ArrayList<LinkedList<Explosion>> explosion)
    {
    	this.active = false;
    	this.powerUp_decay();
		if(Events.level < 51 && explosion != null)
		{
			Audio.play(Audio.explosion3);
			Explosion.start(explosion, 
							this, 
							(int)(this.bounds.getX() 
								+ (this.is_moving_left 
									? FOCAL_PNT_X_LEFT 
									: FOCAL_PNT_X_RIGHT)), 
							(int)(this.bounds.getY() + FOCAL_PNT_Y_EXP), 
							0, 
							false);
		}
		Events.restart_window_visible = true;
		this.nosedive = false;
    }
	
	public void teleport_to(int x, int y)
    {
    	this.search4teleportDestination = false;		
		this.destination.setLocation(x, y);
		
		if(	(this.energy >= this.spell_costs || this.has_unlimited_energy())
			&& !this.damaged
			&& !Menu.menue_visible 
			&& !(this.bounds.getMaxY() + NO_COLLISION_HEIGHT >= GROUND_Y
					&& y >= GROUND_Y) 
			&& !(	   x > this.bounds.getX() + 33 
					&& x < this.bounds.getX() + 133 
					&& y > this.bounds.getY() + 6
					&& y < this.bounds.getY() + 106))
		{
			Audio.play(Audio.teleport1);
			this.energy -= this.has_unlimited_energy() ? 0 : this.spell_costs;
			this.past_teleport_time = System.currentTimeMillis();
						
			this.next_location.setLocation(x, y);
			this.correct_and_set_coordinates();
						
			if(!this.active || !this.rotor_system_active){this.set_activation_state(true);}
			if(this.tractor != null){this.stop_tractor();}
			this.powerUp_timer[INVINCIBLE.ordinal()] = Math.max(this.powerUp_timer[INVINCIBLE.ordinal()], TELEPORT_INVU_TIME);
			this.bonus_kills = 0; 
			this.enhanced_radiation_timer = TELEPORT_INVU_TIME;
			this.bonus_kills_timer = NICE_CATCH_TIME;
			this.bonus_kills_money = 0; 
		}
    }
	
	public void evaluate_bonus_kills()
	{
    	if(this.bonus_kills_timer > 0)
		{
			this.bonus_kills_timer--;
			if(	this.getType() == PHOENIX
			    && this.bonus_kills_timer == NICE_CATCH_TIME - TELEPORT_KILL_TIME 
			    && this.bonus_kills > 1)
			{
				Events.extra_reward(this.bonus_kills, 
									this.bonus_kills_money, 
									0.75f, 0.75f, 3.5f);
			}
			else if(this.getType() == KAMAITACHI && this.bonus_kills_timer == 0)
			{
				if(this.bonus_kills > 1)
				{
					Events.extra_reward(this.bonus_kills, 
										this.bonus_kills_money, 
										0.5f, 0.75f, 3.5f); // 0.25f, 0.5f, 3.0f);
				}				
				this.bonus_kills_money = 0;
				this.bonus_kills = 0;
			}
		}
	}

	private void evaluate_power_up_activation_states()
	{
    	for(int i = 0; i < 4; i++)
		{
			if(this.powerUp_timer[i] > 0)
			{
				this.powerUp_timer[i]--;
				if(this.powerUp_timer[i] == 0 && Menu.collected_PowerUp[i] != null)
				{
					Audio.play(Audio.pu_fade2);
					Menu.collected_PowerUp[i].collected = true;
					Menu.collected_PowerUp[i] = null;	
					if(i == 3){this.adjustFireRate(false);}
				}
				else if(this.powerUp_timer[i] == POWERUP_DURATION/4)
				{
					Audio.play(Audio.pu_fade1);
				}
				else if(this.powerUp_timer[i] < POWERUP_DURATION/4 && Menu.collected_PowerUp[i] != null)
				{
					if(this.powerUp_timer[i]%32 > 15)
			    	{
			    		Menu.collected_PowerUp[i].surface = MyColor.setAlpha(Menu.collected_PowerUp[i].surface, 17 * ((this.powerUp_timer[i])%16));	
			    		Menu.collected_PowerUp[i].cross =   MyColor.setAlpha(Menu.collected_PowerUp[i].cross,   17 * ((this.powerUp_timer[i])%16));
			    	}
					else
					{
						Menu.collected_PowerUp[i].surface = MyColor.setAlpha(Menu.collected_PowerUp[i].surface, 255 - 17 * ((this.powerUp_timer[i])%16));
						Menu.collected_PowerUp[i].cross = MyColor.setAlpha(Menu.collected_PowerUp[i].cross,     255 - 17 * ((this.powerUp_timer[i])%16));
					}
				}
				
			}
		}		
	}

	private void update_interphase_generator()
	{
    	this.interphaseGeneratorTimer++;
		if(this.interphaseGeneratorTimer == this.shift_time + 1)
		{
			Audio.play(Audio.phase_shift);
			if(this.tractor != null){this.stop_tractor();}
		}		
	}
	
	public void take_missile_damage()
    {   	
    	if(!(this.power_shield_on 
    		 && (this.energy >= this.get_dmg_factor() 
    							* MISSILE_DMG 
    							* this.spell_costs 
    			 || this.has_unlimited_energy())))
		{
			this.currentPlating = Math.max(this.currentPlating - this.get_dmg_factor() * MISSILE_DMG, 0f);
			if(this.enhanced_radiation_timer == 0)
			{
				this.recent_dmg_timer = RECENT_DMG_TIME;
			}
			if(this.power_shield_on)
			{						
				this.shut_down_power_shield();
				this.energy = 0;
			}
			if(this.currentPlating <= 0 && !this.damaged)
			{
				this.crash();
			}
		}
		else
		{
			Audio.play(Audio.shield_up);
			this.energy -= this.has_unlimited_energy() 
							? 0 
							: this.get_dmg_factor() 
							  * MISSILE_DMG 
							  * this.spell_costs;
		}
    }   
        
    private void update_properties(boolean full_plating)
    {
    	this.rotorSystem = MyMath.speed(this.levelOfUpgrade[ROTOR_SYSTEM]);
    	this.missileDrive = MyMath.missile_drive(this.levelOfUpgrade[MISSILE_DRIVE]);
    	if(full_plating)
    	{
    		this.get_max_plating();
    		this.energy = MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY]);
    	}
    	this.setPlatingColor();
    	this.currentFirepower = (int)(this.jumboMissiles * MyMath.dmg(this.levelOfUpgrade[FIREPOWER]));
    	this.adjustFireRate(this.has_boosted_fire_rate());
		this.regeneration_rate = MyMath.regeneration(this.levelOfUpgrade[ENERGY_ABILITY]);
		if(Events.window != GAME){this.fire_rate_timer = this.time_between_2_shots;}
		for(int i = 0; i < 6; i++)
		{
			if(this.levelOfUpgrade[i] >= MyMath.max_level(this.upgrade_costs[i]))
			{
				this.has_max_upgrade_level[i] = true;				
			}
		}
		this.set_spell_costs();
	}      

	private void set_spell_costs()
	{
		this.spell_costs = COSTS[this.getType().ordinal()][SPELL]
			- (this.getType() != OROCHI
				? 0 
				: 2 *(this.levelOfUpgrade[ENERGY_ABILITY]-1));
	}

	public boolean is_poweredUp()
	{
		if(this.getType() == HELIOS){return false;}
		for(int i = 0; i < 4; i++)
		{
			if(this.powerUp_timer[i] != 0
				&& this.powerUp_timer[i] < Integer.MAX_VALUE/2)
			{
				return true;
			}
		}
		return false;
	}

	public void powerUp_decay()
	{
		for(int i = 0; i < 4; i++){if(this.powerUp_timer[i] < Integer.MAX_VALUE/2)
		{
			this.powerUp_timer[i] = Math.min(POWERUP_DURATION/4 + 1, this.powerUp_timer[i]);}
		}		
	}

	public static boolean isUnlocked(HelicopterTypes type)
	{
		return  type == PHOENIX
			||  type == ROCH
			|| (type == OROCHI && (Events.reachedLevelTwenty[PHOENIX.ordinal()] || Events.reachedLevelTwenty[PEGASUS.ordinal()]))
			|| (type == KAMAITACHI && (Events.reachedLevelTwenty[ROCH.ordinal()] || Events.reachedLevelTwenty[PEGASUS.ordinal()]))
			|| (type == PEGASUS && (Events.reachedLevelTwenty[OROCHI.ordinal()] || Events.reachedLevelTwenty[KAMAITACHI.ordinal()]))
			|| (type == HELIOS && Events.boss1_killed_b4());
	}
	
	public void setPlatingColor()
	{		
		MyColor.plating = MyColor.percentColor((this.currentPlating)/this.max_plating());
	}
	
	public void getPowerUp(ArrayList<LinkedList<PowerUp>> powerUp,
	                PowerUpTypes powerUpType,
	                boolean lastingEffect)
	{
		getPowerUp(powerUp, powerUpType, lastingEffect, true);
	}
	
	void getPowerUp(ArrayList<LinkedList<PowerUp>> powerUp, 
	                PowerUpTypes powerUpType,
	                boolean lastingEffect,
	                boolean playSound)
	{
		if(lastingEffect && this.powerUp_timer[powerUpType.ordinal()] > 0)
		{
			if(playSound){Audio.play(Audio.pu_fade2);}
			this.powerUp_timer[powerUpType.ordinal()] = 0;
			Menu.collected_PowerUp[powerUpType.ordinal()].collected = true;
			Menu.collected_PowerUp[powerUpType.ordinal()] = null;
			if(powerUpType == BOOSTED_FIRE_RATE){this.adjustFireRate(false);}
		} 
		else
		{
			if(playSound){Audio.play(Audio.pu_announcer[powerUpType.ordinal()]);}
			this.powerUp_timer[powerUpType.ordinal()] = lastingEffect
												? Integer.MAX_VALUE 
												: Math.max(
													this.powerUp_timer[powerUpType.ordinal()],
													POWERUP_DURATION);
			if(Menu.collected_PowerUp[powerUpType.ordinal()] == null)
			{								
				PowerUp.activate(this, powerUp, null, powerUpType, true);
				if(powerUpType == BOOSTED_FIRE_RATE){this.adjustFireRate(true);}
			}
			else
			{
				Menu.collected_PowerUp[powerUpType.ordinal()].surface
					= MyColor.setAlpha(Menu.collected_PowerUp[powerUpType.ordinal()].surface, 255);
				Menu.collected_PowerUp[powerUpType.ordinal()].cross
					= MyColor.setAlpha(Menu.collected_PowerUp[powerUpType.ordinal()].cross, 255);
			}			
		}		
	}
	
	public void set_activation_state(boolean activation_state)
	{
		this.active = activation_state;
		this.rotor_system_active = activation_state;
	}
	
	public void adjustFireRate(boolean powered_up)
	{
		this.time_between_2_shots 
			= MyMath.fire_rate( this.levelOfUpgrade[FIRE_RATE]
			                    + this.rapidfire 
		                        + (powered_up ? FIRE_RATE_POWERUP_LEVEL : 0));
		if(this.hasInterphaseGenerator)
		{
			this.shift_time 
				= MyMath.shift_time( this.levelOfUpgrade[FIRE_RATE]
			                         + (powered_up ? FIRE_RATE_POWERUP_LEVEL : 0));
		}
	}

	public void update_unlocked_helicopters()
	{
		Events.reachedLevelTwenty[this.getType().ordinal()] = true;
		
		if((this.getType() == PHOENIX && !Events.reachedLevelTwenty[PEGASUS.ordinal()]) ||
		   (this.getType() == PEGASUS && !Events.reachedLevelTwenty[PHOENIX.ordinal()]))
		{
			Menu.unlock(OROCHI);
		}
		else if((this.getType() == ROCH && !Events.reachedLevelTwenty[PEGASUS.ordinal()]) ||
				(this.getType() == PEGASUS && !Events.reachedLevelTwenty[ROCH.ordinal()]))
		{
			Menu.unlock(KAMAITACHI);
		}
		else if((this.getType() == OROCHI && !Events.reachedLevelTwenty[KAMAITACHI.ordinal()]) ||
				(this.getType() == KAMAITACHI && !Events.reachedLevelTwenty[OROCHI.ordinal()]))
		{
			Menu.unlock(PEGASUS);
		}
	}

	public void use_reparation_PU()
	{
		Audio.play(Audio.cash);
		float max_plating = this.max_plating();
		if(this.currentPlating < max_plating)
		{
			this.currentPlating
				= Math.min(
					max_plating, 
					this.currentPlating
						+ Math.max(1, (   max_plating 
										- this.currentPlating)/2));
		}
	}
	
	public static int helios_costs(int upgrade_number)
	{
		int heli;
		if(upgrade_number <= 1){heli = 2;}
		else if(upgrade_number == 2 || upgrade_number == 3)
		{
			heli = upgrade_number - 2;
		}
		else {heli = upgrade_number - 1;}			
		for(int i = 0; i < 4; i++)
		{
			if(Events.recordTime[heli][i] == 0) return 4-i;
		}
		return 0;		
	}

	public void menue_paint(Graphics2D g2d, HelicopterTypes helicopterType)
	{		
    	this.rotate_propeller(helicopterType, 7);
    	this.paint(g2d, 692, 360, helicopterType, 1);
	}

	public boolean is_power_shield_protected(Enemy enemy)
	{		
		return this.power_shield_on 
			   && (this.has_unlimited_energy()
				   || this.energy 
				   		>= this.spell_costs * enemy.collision_dmg(this));
	}

	public float kaboom_dmg()
	{		
		return Math.max(4, 2*this.currentPlating /3);
	}
	
	public float max_plating()
	{
		return MyMath.plating(this.levelOfUpgrade[PLATING])
			   * this.platingDurabilityFactor;
	}
	
	private void get_max_plating()
	{
		this.currentPlating = this.max_plating();
	}

	public void receive_static_charged(float degree)
	{
		if(!this.is_invincible())
		{			
			this.energy 
				= this.has_unlimited_energy()
					? this.energy 
					: Math.max( 0, 
								this.energy 
								-degree*(this.power_shield_on 
									? REDUCED_ENERGY_DRAIN 
									: ENERGY_DRAIN));			
			if(!this.power_shield_on)
			{
				this.slowed_timer = SLOW_TIME;
			}
		}
	}	
	
	public boolean can_collide_with(Enemy e)
	{		
		return this.basic_collision_requirements_satisfied(e)					
			   && !(e.model == BARRIER 
						&& (    e.alpha != 255 
							||  e.borrow_timer == READY 
							|| !e.has_unresolved_intersection));
	}

	public boolean basic_collision_requirements_satisfied(Enemy e)
	{		
		return this.interphaseGeneratorTimer <= this.shift_time
				&& !this.damaged
				&& e.is_on_screen()
				&& e.bounds.intersects(this.bounds);
	}
	
	public float get_dmg_factor()
	{		
		return this.enhanced_radiation_timer == READY 
					? this.is_invincible() 
						? INVU_DMG_FACTOR
						: 1.0f 
					: 0.0f;
	}

	public boolean enhanced_radiation_approved(Enemy enemy)
	{		
		return this.hasShortrangeRadiation
				&& enemy.collision_damage_timer == 0 
				&& !enemy.is_kaboom
				&& this.enhanced_radiation_timer == READY
				&& MyMath.toss_up(ENHANCED_RADIATION_PROB);
	}
	
	public boolean is_energy_ability_activateable()
	{		
		return  (this.getType() == ROCH && this.energy >= POWER_SHIELD_ACTIVATION_TRESHOLD)
				|| !(this.generator_timer > 0 
					 ||(this.energy < this.spell_costs
					 	&& !this.has_unlimited_energy()));
	}

	public void upgrade_energy_ability()
	{
		this.energy += MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY])
					   - MyMath.energy(this.levelOfUpgrade[ENERGY_ABILITY]-1);
		this.regeneration_rate = MyMath.regeneration(this.levelOfUpgrade[ENERGY_ABILITY]);
		
		if(this.getType() == OROCHI)
		{
			this.set_spell_costs();
		}
	}

	public void becomes_center_of(Explosion exp)
	{
		exp.ellipse.setFrameFromCenter(
			this.bounds.getX() + (this.is_moving_left ? FOCAL_PNT_X_LEFT : FOCAL_PNT_X_RIGHT), 
			this.bounds.getY() + FOCAL_PNT_Y_EXP, 
			this.bounds.getX() + (this.is_moving_left ? FOCAL_PNT_X_LEFT : FOCAL_PNT_X_RIGHT), 
			this.bounds.getY() + FOCAL_PNT_Y_EXP);
	}

	public void prepare_teleportation()
	{
		this.search4teleportDestination = true;
		this.prior_teleport_location.setLocation(
			this.bounds.getX() + (this.is_moving_left 
										? FOCAL_PNT_X_LEFT
										: FOCAL_PNT_X_RIGHT),
			this.bounds.getY() + FOCAL_PNT_Y_POS);
	}
	
	public void turn_on_power_shield()
	{
    	Audio.play(Audio.shield_up);
		this.power_shield_on = true;
	}
	    
    void shut_down_power_shield()
    {
    	Audio.play(Audio.plasma_off);
		this.power_shield_on = false;
    }
    
    public void activate_plasma()
	{
		Audio.play(Audio.plasma_on);
		this.energy -= this.has_unlimited_energy() ? 0 : this.spell_costs;
		this.plasma_activation_timer = POWERUP_DURATION;
	}	

	public void release_EMP(ArrayList<LinkedList<Explosion>> explosion)
	{
		this.generator_timer = 67;
		this.energy -= this.has_unlimited_energy() ? 0 : this.spell_costs;
		Audio.play(Audio.emp);
		Explosion.start(explosion, 
						this,							
						(int)(this.bounds.getX() 
								+ (this.is_moving_left 
									? FOCAL_PNT_X_LEFT 
									: FOCAL_PNT_X_RIGHT)), 
						(int)(this.bounds.getY() 
								+ FOCAL_PNT_Y_EXP), 							
						3, 
						false);	
		this.interphaseGeneratorTimer = 0;
	}

	public void activate_PU_generator(ArrayList<LinkedList<PowerUp>> powerUp)
	{
		this.generator_timer = (int)(0.4f * POWERUP_DURATION);			
		this.energy -= this.has_unlimited_energy() ? 0 : this.spell_costs;			
		MyMath.randomize();			
		for(int i = 0; i < 3; i++)
		{
			if(MyMath.get_random_order_value(i) == REPARATION.ordinal())
			{
				if(i == 0){Audio.play(Audio.pu_announcer[REPARATION.ordinal()]);}
				this.use_reparation_PU();
			}
			else
			{
				this.getPowerUp(	powerUp, PowerUpTypes.values()[MyMath.get_random_order_value(i)],
										false, i == 0);
			}
			if(MyMath.toss_up(0.7f)){break;}
		}		
	}

	public boolean is_on_the_ground()
	{		
		return this.bounds.getMaxY() + NO_COLLISION_HEIGHT == GROUND_Y;
	}

	public void turn_around()
	{
		this.is_moving_left = !this.is_moving_left;
		this.set_bounds();		
	}
	
	public void energy_ability_used(ArrayList<LinkedList<PowerUp>> powerUp,
	                         ArrayList<LinkedList<Explosion>> explosion)
	{
		if(	this.getType() == PHOENIX
			&& this.is_energy_ability_activateable())
		{
			this.prepare_teleportation();
		}		
		else if(this.power_shield_on)
		{
			this.shut_down_power_shield();	
		}
		else if(this.getType() == ROCH
				&& this.is_energy_ability_activateable())
		{			
			this.turn_on_power_shield();			
		}		
		else if(this.getType() == OROCHI
				&& !this.next_missile_is_stunner)
		{
			Audio.play(Audio.stun_activated);
			this.next_missile_is_stunner = true;				
		}
		else if(this.getType() == KAMAITACHI
				&& this.is_energy_ability_activateable())
		{
			this.activate_plasma();
		}
		else if(this.getType() == PEGASUS
				&& this.is_energy_ability_activateable())
		{
			this.release_EMP(explosion);
		}		
		else if(this.getType() == HELIOS
				&& this.is_energy_ability_activateable())
		{			
			this.activate_PU_generator(powerUp);
		}
	}
	
	public int ability_id(int i)
    {
    	return i == 5 ? 1 + i + this.getType().ordinal() : i;
    }

	public void be_affected_by_collision_with(Enemy enemy, 
	                                          Controller controller,
	                                          boolean play_collision_sound)
	{
		if(!this.is_power_shield_protected(enemy))
		{			
			if(play_collision_sound)
			{
				Audio.play(enemy.is_kaboom 
							? Audio.explosion4 
							: this.enhanced_radiation_timer == 0
								? Audio.explosion1
								: Audio.explosion2);
			}			
			
			this.slowed_timer = 2;
			
			if(this.enhanced_radiation_approved(enemy))
			{
				this.enhanced_radiation_timer 
					= Math.max(	this.enhanced_radiation_timer, 
								NO_COLLISION_DMG_TIME);
			}
			else if(this.enhanced_radiation_timer == 0)
			{					
				this.recent_dmg_timer = RECENT_DMG_TIME; 
			}
			
			this.currentPlating
				= Math.max(
					this.currentPlating - enemy.collision_dmg(this),
					0);
						
			if(this.power_shield_on)
			{						
				this.shut_down_power_shield();
				this.energy = 0;
			}
			
			if(this.hasShortrangeRadiation)
			{
				enemy.react_to_radiation(controller, this);
			}
		}
		else
		{
			this.energy 
				-= this.has_unlimited_energy()
					? 0.0 
					: this.spell_costs * enemy.collision_dmg(this);
			if(this.is_invincible())
			{
				if(play_collision_sound){Audio.play(Audio.shield_up);}				
			}		
			else if(play_collision_sound){Audio.play(Audio.explosion1);}
		}		
	}

	public boolean has_performed_teleport_kill()
	{		
		return this.bonus_kills_timer > 0;
	}

	public abstract HelicopterTypes getType();
}    