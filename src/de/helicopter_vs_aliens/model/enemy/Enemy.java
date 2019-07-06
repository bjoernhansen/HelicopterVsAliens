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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.*;
import de.helicopter_vs_aliens.model.Explosion;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.EnemyMissileTypes;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.missile.MissileTypes;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpTypes;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelTypes.*;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileTypes.BUSTER;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileTypes.DISCHARGER;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.REPARATION;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;


public class Enemy extends MovingObject implements DamageFactors, MissileTypes, BossTypes
{
	private class FinalEnemysOperator
    {	
		Enemy[] servants;
    	int [] time_since_death;
    	
    	FinalEnemysOperator()
    	{
    		this.servants = new Enemy [NR_OF_BOSS_5_SERVANTS];
    		this.time_since_death = new int [NR_OF_BOSS_5_SERVANTS];
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
		
		RETURN_PROB[]	= { 0.013f,	 	// SMALL_SHIELD_MAKER
             			    0.013f,  	// BIG_SHIELD_MAKER
             			    0.007f,  	// BODYGUARD
             			    0.01f,  	// HEALER
             			    0.04f}; 	// PROTECTOR
		
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
		boss_selection,		 	// bestimmt, welche Boss-Typ erstellt wird
		max_nr,				 	// bestimmt wie viele Standard-Gegner gleichzeitig erscheinen können
		max_barrier_nr,			// bestimmt wie viele Hindernis-Gegner gleichzeitig erscheinen können
		current_nr_of_barriers; // aktuelle Anzahl von "lebenden" Hindernis-Gegnern
	
	public static float 	
		miniboss_prob = 0.05f;// bestimmt die Häufigkeit, mit der Mini-Bosse erscheinen
		
	public static Enemy 
		current_mini_boss,	// Referenz auf den aktuellen Boss-Gegner
			currentRock,
		last_carrier,  		// Referenz auf den zuletzt zerstörten Carrier-Gegner
		living_barrier[] = new Enemy [MAX_BARRIER_NUMBER];
	
	private static int 
		selection,			// bestimmt welche Typen von Gegnern zufällig erscheinen können	
		selection_barrier, 	// bestimmt den Typ der Hinernis-Gegner
		rock_timer,			// reguliert das Erscheinen von "Rock"-Gegnern
		barrier_timer;		// reguliert das Erscheinen von Hindernis-Gegnern
		
	// für die Tarnung nötige Variablen
    private static float[] 
    	scales = { 1f, 1f, 1f, RADAR_STRENGTH },
    	offsets = new float[4];	
	
    private static final RescaleOp 
		ROP_CLOAKED = new RescaleOp(scales, offsets, null);
    
	private static boolean	
		creation_stop			=  false,	// = false: es werden keine neuen Gegner erzeugt, bis die Anzahl aktiver Gegner auf 0 fällt
		make_boss2_servants 	=  false,	// make-Variablen: bestimmen, ob ein bestimmter Boss-Gegner zu erzeugen ist
		make_boss4_servant 		=  false,
	    make_all_boss5_servants =  false,
	    
	    make_boss5_servant[]	= {false,	// SMALL_SHIELD_MAKER
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
        type,
		hitpoints,						// aktuelle Hitpoints
		starting_hitpoints,				// Anfangs-Hitpoints (bei Erstellung des Gegers)
		strength,						// Stärke des Gegner, bestimmmt die Höhe der Belohnung bei Abschuss
		invincible_timer,				// reguliert die Zeit, die ein Gegner unverwundbar ist		
		teleport_timer,					// Zeit [frames], bis der Gegner sich erneut teleportieren kann
		shield,							// nur für Boss 5 relevant; kann die Werte 0 (kein Schild), 1 oder 2 annehmen 		
		touched_site,
		last_touched_site,
		alpha,
		borrow_timer,	
		untouched_counter,	
		stunning_timer,
		emp_slowed_timer,			// reguliert die Länge der Verlangsamung nach EMP-Treffer (Pegasus-Klasse)
		collision_damage_timer;			// Timer zur überwachung der Zeit zwischen zwei Helikopter-HP-Abzügen;
	
	public boolean	
		is_mini_boss,					// = true: Gegner ist ein Mini-Boss
		is_kaboom,		
		is_lasting,		
		is_touching_helicopter,
		has_unresolved_intersection;		
	
	// Farben
    public Color 
    	farbe1,
    	farbe2; 

    public EnemyModelTypes
		model;				// legt das Aussehen (Model) des Gegners fest

    public Point 
    	direction = new Point();		// Flugrichtung
    	
	Enemy
		stopping_barrier,		// Hindernis-Gegner, der diesen Gegner aufgehalten hat
		is_previous_stopping_barrier;
		
	private int 
		reward_modifier,		// für normale Gegner wird eine Zufallszahl zwischen -5 und 5 auf die Belohnung bei Abschuss addiert
		lifetime,				// Anzahl der Frames seit Erstellung des Gegners;  und vergangene Zeit seit Erstellung, Zeit	
		y_crash_pos,			// Bestimmt wie tief ein Gegner nach Absturz im Boden versinken kann  	
		collision_audio_timer,
		turn_audio_timer,
		exploding_timer,		// Timer zur überwachung der Zeit zwischen Abschuss und Absturz
		cloaking_timer,			// reguliert die Tarnung eines Gegners; = DISABLED: Gegner kann sich grundsätzlich nicht tarnen
		uncloaking_speed,		
		shield_maker_timer,
		call_back,
		chaos_timer = 0,
		speedup,
		batch_wise_move,
		shoot_timer,		
		spawning_hornet_timer,
		turn_timer,
		tractor,				// = DISABLED (Gegner ohne Traktor); = READY (Traktor nicht aktiv); = 1 (Traktor aktiv)
		dodge_timer,			// Zeit [frames], bis ein Gegner erneut ausweichen kann
		snooze_timer,
		static_charge_timer,
		
		// nur für Hindernis-Gegner releavant		
		rotor_color,
		barrier_shoot_timer,
		barrier_teleport_timer,
		shoot_pause,
		shooting_rate,
		shots_per_cycle,
		shooting_cycle_length,
		shot_speed,
		shot_rotation_speed,
		
		// Regulation des Stuneffekte nach Treffer durch Stopp-Rakete der Orochi-Klasse
		non_stunable_timer,
		total_stunning_time,
		knock_back_direction;
		
	private float
		deactivation_prob,
		dimFactor;
	
	private boolean
		can_dodge,				// = true: Gegner kann Schüssen ausweichen
		can_kamikaze,			// = true: Gegner geht auf Kollsionskurs, wenn die Distanz zum Helicopter klein ist
		can_learn_kamikaze,		// = true: Gegner kann den Kamikaze-Modus einschalten, wenn der Helikopter zu nahe kommt
		can_early_turn,
		can_move_chaotic, 		// reguliert den zufälligen Richtungswechsel bei Chaosflug-Modus
		can_sinus_move,			// Gegner fliegt in Kurven ähnlicher einer Sinus-Kurve
		can_turn,				// Gegner ändert bei Beschuss evtl.    seine Flugrichtung in Richtung Helikopter
		can_instant_turn,		// Gegner ändert bei Beschuss immer(!) seine Flugrichtung in Richtung Helikopter
		can_frontal_speedup,	// Gegner wird schneller, wenn Helikopter ihm zu Nahe kommt		
		can_loop,				// = true: Gegner fliegt Loopings
		can_chaos_speedup,		// erhöht die Geschwindigkeit, wenn in Helicopternähe
		
		is_speed_boosted,
		is_destroyed,			// = true: Gegner wurde vernichtet	
		has_height_set,			// = false --> heigt = height_factor * width; = true --> height wurde manuell festgelegt	
		has_y_pos_set,			// = false --> y-Position wurde nicht vorab festgelegt und muss automatisch ermittelt werden
		has_crashed, 			// = true: Gegner ist abgestürzt
		is_emp_shocked,			// = true: Gegner steht unter EMP-Schock -> ist verlangsamt				
		is_marked_for_removal,	// = true --> Gegner nicht mehr zu sehen; kann entsorgt werden 
		is_upper_shield_maker,	// bestimmt die Position der Schild-Aufspannenden Servants von Boss 5	
		is_explodable,			// = true: explodiert bei Kollisionen mit dem Helikopter
		is_shielding,			// = true: Gegner spannt gerade ein Schutzschild für Boss 5 auf (nur für Schild-Generatoren von Boss 5) 
		is_stunnable,			// = false für Boss 5; bestimmt ob ein Gegner von Stopp-Raketen (Orochi-Klasse) gestunt werden kann 
		is_carrier,				// = true
		is_clockwise_barrier,	// = true: der Rotor des Hindernis dreht im Uhrzeigersinn
		is_recovering_speed;
  
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
		target_speed_level = new Point2D.Float(),	// Anfangsgeschwindigkeit	
		speed_level = new Point2D.Float(),			// auf Basis dieses Objektes wird die tatsächliche Geschwindigkeit berechnet
		speed = new Point2D.Float(),				// tatsächliche Geschwindigkeit
		shooting_direction = new Point2D.Float();   // Schussrichtugn von schießenden Barrier-Gegnern

	private EnemyTypes enemyType;

	public void paint(Graphics2D g2d, Helicopter helicopter)
	{				
		boolean cloaked = (this.cloaking_timer > CLOAKING_TIME && this.cloaking_timer <= CLOAKING_TIME+CLOAKED_TIME);
		int g2d_sel = this.direction.x == -1 ? 0 : 1;
		
		if(!cloaked)
		{
			if(this.is_invincible())
			{	
				this.paint_image(g2d, -this.direction.x, MyColor.variableGreen, false);
			}
			else if(this.alpha != 255)
			{
				if(this.alpha > 51 || !helicopter.hasRadarDevice)
				{
					scales[3] = ((float)this.alpha)/255;			
					g2d.drawImage(	this.image[g2d_sel], 
									new RescaleOp(scales, offsets, null), 
									this.paintBounds.x - (this.direction.x == -1 ? this.paintBounds.width/36 : 0),
									this.paintBounds.y - this.paintBounds.height/4);
				}
				else
				{
					g2d.drawImage(	this.image[g2d_sel + 2], 
									this.paintBounds.x - (this.direction.x == -1 ? this.paintBounds.width/36 : 0),
									this.paintBounds.y - this.paintBounds.height/4, null);
				}			
			}
			else
			{
				g2d.drawImage(	this.image[g2d_sel],
								this.paintBounds.x - (this.direction.x == -1 ? this.paintBounds.width/36 : 0),
								this.paintBounds.y - this.paintBounds.height/4, null);
			}
						
			// Dach
			if(!this.is_destroyed && (this.tractor == ACTIVE || this.shoot_timer > 0 || this.is_shielding))
			{
				Color input_color_roof 
					= this.alpha < 255 
						? MyColor.setAlpha(MyColor.variableGreen, this.alpha) 								
						: MyColor.variableGreen;				
				
				this.paint_cannon(g2d, this.paintBounds.x, this.paintBounds.y, -this.direction.x, input_color_roof);
			}
						
			// blinkende Scheibe von Bossen und Mini-Bossen bzw. Eyes bei Hindernissen
			if(this.has_glowing_eyes())
			{				
				if(this.model != BARRIER){this.paint_window(g2d);}
				else{this.paint_barrier_eyes(g2d);}
			}			
						
			// Auspuff			
			if(!(this.is_destroyed || this.stunning_timer > 0))
			{
				int temp = 63 - (((int)(2 + 0.1f * Math.abs(this.speed_level.getX())) * this.lifetime)%32); //d
				Color color_temp = new Color(255, 192+temp, 129+temp, this.alpha);							
				this.paint_exhaust(g2d, color_temp);				
			}			
					
			// die Schild- und Traktorstrahlen
			if(this.tractor > 0){this.paint_tractor_beam(g2d, helicopter);}
			else if(this.type == FINAL_BOSS)
			{
				for(int servantType = id(SMALL_SHIELD_MAKER); servantType <= id(BIG_SHIELD_MAKER); servantType++)
				{
					if( this.operator.servants[servantType] != null && 
						this.operator.servants[servantType].is_shielding)
					{
						this.operator.servants[servantType].paint_shield_beam(g2d);
					}
				}
			}
			
			if(this.model == BARRIER && !this.is_destroyed)
			{
				this.paint_rotor(g2d);				
			}
		}
		else if(helicopter.hasRadarDevice)
		{
			g2d.drawImage(this.image[g2d_sel + 2], this.paintBounds.x - (this.direction.x == -1 ? this.paintBounds.width/36 : 0), this.paintBounds.y - this.paintBounds.height/4, null);
		}
		
		//zu Testzwecken:	
        //g2d.setColor(Color.red);
        
        /*if(this.model == BARRIER)
        	{
        		g2d.drawString(   "Borrow: " + this.borrow_timer + " ; "
        	
        				+ "Stun: "   + this.stunning_timer + " ; "
        				+ "Snooze: " + this.snooze_timer + " ; ", 
        				(int)this.bounds.getX(),
        				(int) this.bounds.getY());
        	}*/
        //g2d.draw(TURN_FRAME);
	}
	
	private boolean has_glowing_eyes()
	{
		return  !this.is_destroyed 
				&& (this.is_boss()
					|| this.is_kaboom
					|| (this.model == BARRIER 
						&& this.snooze_timer <= SNOOZE_TIME + 75));
	}

	private void clear_image()
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
			this.paint_image(this.graphics[j], 1-2*j, null, true);
		}
	}		
	
	private void paint_rotor(Graphics2D g2d)
	{
		paint_rotor(g2d, this.paintBounds.x, this.paintBounds.y);
	}
	
	private void paint_rotor(Graphics2D g2d, int x, int y)
	{
		Helicopter.paint_rotor(	g2d, 
								!this.is_destroyed
									?(MyColor.setAlpha(MyColor.barrierColor[this.rotor_color][Events.timeOfDay], this.alpha))
									: MyColor.dimColor(MyColor.barrierColor[this.rotor_color][Events.timeOfDay], MyColor.DESTRUCTION_DIM_FACTOR), 
								x, y, this.paintBounds.width, this.paintBounds.height, 5, (this.speed_level.equals(ZERO_SPEED) ? (this.snooze_timer <= SNOOZE_TIME ? 3 : 0) : 8) * (this.is_clockwise_barrier ? -1 : 1) * this.lifetime%360,
								24, BARRIER_BORDER_SIZE, this.snooze_timer == 0);
		this.paint_barrier_cannon(g2d, x, y);
	}
	
	private void paint_barrier_cannon(Graphics2D g2d, int x, int y)
	{		
		Color temp_color;
		int distance_x, distance_y;			
		for(int i = 0; i < 3; i++)
		{
			temp_color = (this.barrier_shoot_timer != DISABLED && this.barrier_shoot_timer <= this.shots_per_cycle * this.shooting_rate && i != 0 && !this.is_destroyed)
							?  MyColor.variableGreen
							: !this.is_destroyed
								? MyColor.barrierColor[i][Events.timeOfDay]
								: MyColor.dimColor(MyColor.barrierColor[i][Events.timeOfDay], MyColor.DESTRUCTION_DIM_FACTOR);
			if(this.alpha != 255){temp_color = MyColor.setAlpha(temp_color, this.alpha);}								
			g2d.setColor(temp_color);
			
			distance_x = (int) ((0.45f + i * 0.01f) * this.paintBounds.width);
			distance_y = (int) ((0.45f + i * 0.01f) * this.paintBounds.height);
						
			g2d.fillOval(x + distance_x,
				 	  	 y + distance_y, 
				 	  	 this.paintBounds.width  - 2*distance_x,
				 	  	 this.paintBounds.height - 2*distance_y);
		}		
	}	

	private void paint_cannon(Graphics2D g2d, int x, int y, int direction_x, Color input_color)
	{
		if(this.model == TIT)
		{
			paintBar(	g2d,
						x,	y, 
						this.paintBounds.width, this.paintBounds.height,
						0.02f, 0.007f, 0.167f, 0.04f, 0.6f,  
						direction_x, true, input_color);	
		}
		else if(this.model == CARGO)
		{
			paintBar(	g2d,
						x, (int) (y + 0.48f * this.paintBounds.height),
						this.paintBounds.width, this.paintBounds.height,
						0, 0, 0.1f, 0.04f, 0.6f, 
						direction_x, true, input_color);
		}
	}	
	
	private void paint_bar_frame(Graphics2D g2d, int x, int y, 
	                             float thicknessFactor,
	                             float shift, float center_shift,
	                             float dimFactor,
	                             Color inputColor, Color bg_color,
	                             boolean image_paint)
	{		
		if(bg_color != null)
		{
			g2d.setPaint(new GradientPaint(	0, 
											y, 
											bg_color, 
											0, 
											y + 0.3f*thicknessFactor*this.paintBounds.height,
											MyColor.dimColor(bg_color, 0.85f), 
											true));
			
			g2d.fillRect(x + (int)(thicknessFactor/2 * this.paintBounds.width),
				  	     y + (int)(thicknessFactor/2 * this.paintBounds.height),
				  	     (int)((1f-thicknessFactor)  * this.paintBounds.width),
				  	     (int)((1f-thicknessFactor)  * this.paintBounds.height));
		}
		
		int x_shift = (int) (shift * this.paintBounds.width),
			y_shift = (int) (shift * this.paintBounds.height),
			x_center_shift = (int) (center_shift * this.paintBounds.width),
			y_center_shift = (int) (center_shift * this.paintBounds.height);
		
		
		if(image_paint || (this.speed_level.getX() != 0 && this.direction.x == 1))
		{
			paintBar(	g2d,
						x + x_center_shift,
						y + y_shift,
						this.paintBounds.width,
						this.paintBounds.height - 2 * y_shift,
						thicknessFactor,
						0.2f,
						dimFactor,
						false, 
						inputColor);
		}
		if(image_paint || (this.speed_level.getX() != 0 && this.direction.x ==  -1))
		{
			paintBar(	g2d,
						(int)(x + 1 + (1f-thicknessFactor)*this.paintBounds.width)-x_center_shift,
						y + y_shift,  
						this.paintBounds.width,
						this.paintBounds.height - 2 * y_shift,
						thicknessFactor,
						0.2f,
						dimFactor,
						false, 
						inputColor);
		}
		if(image_paint || (this.speed_level.getY() != 0 && this.direction.y ==  1))
		{
			paintBar(	g2d,
						x + x_shift,
						y + y_center_shift,
						this.paintBounds.width - 2 * x_shift,
						this.paintBounds.height,
						thicknessFactor,
						0.2f,
						dimFactor,
						true,
						inputColor);
		}
		if(image_paint || (this.speed_level.getY() != 0 && this.direction.y == -1))
		{
			paintBar(	g2d,
						x + x_shift,
						(int)(y + 1 + (1f-thicknessFactor)*this.paintBounds.height)-y_center_shift,
						this.paintBounds.width - 2 * x_shift,
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
								 float thickness_factor,
								 float rounding,
								 float dimFactor,
								 boolean horizontal,
								 Color inputColor)
	{		
		paintBar(	g2d,
					x, y,
					width, height, 
					0, 0,
					thickness_factor,
					rounding,
					dimFactor,
					1,
					horizontal,
					inputColor);
	}
	
	private static void paintBar(Graphics2D g2d,
								 int x, int y,
								 int width, int height,
								 float x_shift_left, float x_shift_right,
								 float thickness_factor, float rounding,
								 float dim_factor, int direction_x,
								 boolean horizontal, Color input_color)
	{		
		g2d.setPaint( new GradientPaint(	(int) (horizontal ? 0 : x + 0.5f * thickness_factor * width), 
											(int) (horizontal ?     y + 0.5f * thickness_factor * height : 0),
											input_color, 
											(int) (horizontal ? 0 : x + 1.0f * thickness_factor * width), 
											(int) (horizontal ?     y + 1.0f * thickness_factor * height : 0),
											MyColor.dimColor(input_color, dim_factor), 
											true));		
		
		g2d.fillRoundRect(	(int) (x - (direction_x == 1 ? x_shift_left : x_shift_right) * width), 
							y,  
							(int) (	horizontal ? (1 + x_shift_left + x_shift_right) * width : thickness_factor * width), 
							(int) (	horizontal ? thickness_factor * height : (1 + x_shift_left + x_shift_right) * height ), 
							(int) (	horizontal ? rounding * width : thickness_factor * width), 
							(int) (	horizontal ? thickness_factor * height : rounding * height) );
	}	
	
	// malen der Seitenflügel mit Antriebsdüse
	private void paint_exhaust(Graphics2D g2d, Color color4)
	{
		paint_exhaust(g2d, 
					   this.paintBounds.x,
					   this.paintBounds.y,
					   -this.direction.x, 
					   null,
					   color4);
	}
	
	private void paint_exhaust(Graphics2D g2d, int x, int y, int direction_x, Color color2, Color color4)
	{			
		if(this.model == TIT)
		{			
			paint_engine(g2d, x, y, 0.45f, 0.27f, 0.5f, 0.4f, direction_x, color2, color4);	
		}
		else if(this.model == CARGO)
		{	
			paint_engine(g2d, x, y, 0.45f, 0.17f, 0.45f, 0.22f, direction_x, color2, color4);		
			paint_engine(g2d, x, y, 0.45f, 0.17f, 0.25f, 0.70f, direction_x, color2, color4);		
		}
		else if(this.model == BARRIER)
		{
			paint_bar_frame(g2d, this.paintBounds.x, this.paintBounds.y,
							0.07f, 0.35f, 0.04f, 0.7f, color4, null, false);
		}
	}
		
	private void paint_engine(Graphics2D g2d, 
                              int x, int y, 
                              float width, float height,
                              float x_shift, float y_shift, 
                              int direction_x, 
                              Color color2, Color color4)
	{			
		if(color2 != null)
		{
			paint_pipe(g2d, x, y, width, height, x_shift, 				  y_shift, direction_x, color2, false);				
		}
			paint_pipe(g2d, x, y, 0.05f, height, x_shift + width - 0.05f, y_shift, direction_x, color4, true);
	}		
	
	private void paint_pipe(Graphics2D g2d, 
	                                  int x, int y, 
	                                  float width, float height,
	                                  float x_shift, float y_shift, 
	                                  int direction_x, Color color, boolean is_exhaust)
	{			
		g2d.setPaint(new GradientPaint(	0, 
										y + (y_shift + 0.05f)  * this.paintBounds.height,
										color, 
										0, 
										y + (y_shift + height) * this.paintBounds.height,
										MyColor.dimColor(color, 0.5f), 
										true));
		
		g2d.fillRoundRect(	(int) (x + (direction_x == 1 
										? x_shift 
										: 1f - x_shift - width)	* this.paintBounds.width),
							(int) (y + 	y_shift 			   	* this.paintBounds.height),
							(int) (		width  				   	* this.paintBounds.width),
							(int) (		height  			   	* this.paintBounds.height),
							(int) ((is_exhaust ? 0f : height/2) * this.paintBounds.width),
							(int) ((is_exhaust ? 0f : height  ) * this.paintBounds.height)  );
	}
	
	
	private void paint_image(Graphics2D g2d, int direction_x, Color color, boolean image_paint)
	{	
		int offset_x = (int)(image_paint 
								? (direction_x == 1 ? 0.028f * this.paintBounds.width : 0)
								: this.paintBounds.x),
								
			offset_y = (int)(image_paint
								? 0.25f * this.paintBounds.height
								: this.paintBounds.y);
		
		boolean getarnt = 	 this.cloaking_timer > CLOAKING_TIME 
						  && this.cloaking_timer <= CLOAKING_TIME+CLOAKED_TIME;
						
		/*
		 * Festlegen der Farben
		 */
		Color main_color_light, main_color_dark, bar_color, inactive_nozzle_color;		
		
		if(color == null)
		{
			if(this.is_destroyed && Events.timeOfDay == NIGHT)
			{
				main_color_light = MyColor.dimColor(this.farbe1, 1.3f * MyColor.NIGHT_DIM_FACTOR);
				main_color_dark  = MyColor.dimColor(this.farbe2, 1.3f * MyColor.NIGHT_DIM_FACTOR);
			}
			else
			{
				main_color_light = this.farbe1;
				main_color_dark  = this.farbe2;
			}
		}
		else
		{
			main_color_light = color;
			main_color_dark = MyColor.dimColor(color, 1.5f);
		}		
		
		if(this.model == BARRIER){bar_color = MyColor.barrierColor[MyColor.FRAME][Events.timeOfDay];}
		else if(!this.is_destroyed && (this.tractor == ACTIVE || this.shoot_timer > 0 || this.is_shielding)){bar_color = MyColor.variableGreen;}
		else if(!this.is_destroyed && !image_paint && this.is_invincible()){bar_color = Color.green;}
		else if(this.is_mini_boss){bar_color = this.farbe2;}
		else{bar_color = MyColor.enemyGray;}			
		inactive_nozzle_color = MyColor.inactive_nozzle;
		
		if(this.model == BARRIER && Events.timeOfDay == NIGHT)
		{
			inactive_nozzle_color = MyColor.barrierColor[MyColor.NOZZLE][Events.timeOfDay];
		}
		
		if(this.is_destroyed)
		{
			bar_color = MyColor.dimColor(bar_color, Events.timeOfDay == NIGHT ? 1.3f * MyColor.NIGHT_DIM_FACTOR : 1);
			inactive_nozzle_color = MyColor.dimColor(inactive_nozzle_color, Events.timeOfDay == NIGHT ? 1.3f * MyColor.NIGHT_DIM_FACTOR : 1);
		}
				
		//Malen des Gegners
		if(this.model != BARRIER)
		{
			paint_vessel(	g2d, 
							offset_x, offset_y, 
							direction_x, 
							color, 
							getarnt, image_paint, 
							main_color_light, main_color_dark, 
							bar_color, inactive_nozzle_color);
		}
		else
		{
			paint_barrier(	g2d, 
							offset_x, offset_y, 
							image_paint, 
							main_color_light, main_color_dark, 
							bar_color, inactive_nozzle_color);
		}
	}	
	
	private void paint_vessel( Graphics2D g2d, int offset_x, int offset_y, 
	                           int direction_x, Color color, boolean getarnt, 
	                           boolean image_paint,
	                           Color main_color_light, 
	                           Color main_color_dark,
	                           Color cannon_color, 
	                           Color inactive_nozzle_color)
	{	
		if(this.model == CARGO)
		{
			this.paint_roof(g2d, cannon_color, offset_x, offset_y, direction_x);				
		}
		this.paint_airframe(g2d, main_color_light, offset_x, offset_y, direction_x);
		this.paint_cannon(g2d, offset_x, offset_y, direction_x, cannon_color);		
		if(this.model == TIT)
		{
			this.paint_vertical_stabilizer(g2d, offset_x, offset_y, direction_x);
		}		
		this.paint_exhaust(	g2d, offset_x, offset_y, direction_x, 
							main_color_dark, inactive_nozzle_color);
		
		if(Color.red.equals(color) || !this.is_living_boss())
		{		
			this.paint_window(	
					g2d, 
					offset_x, 
					(int)(offset_y 
							+ this.paintBounds.height
							  *(this.model == TIT ? 0.067f : 0.125f)), 
					Color.red.equals(color) ? MyColor.cloakedBossEye : null, 
					direction_x, 
					getarnt && !image_paint);
		}		
		
		// das rote Kreuz		
		if(this.type == HEALER)
		{
			paint_red_cross(
					g2d,
					(int)( offset_x + (direction_x == 1 
								? 0.7f * this.paintBounds.width
								: (1 - 0.7f - 0.18f) * this.paintBounds.width)),
					(int) (offset_y + 0.6f * this.paintBounds.height),
					(int) (			  0.18f * this.paintBounds.width));
		}
				
		/*g2d.setColor(Color.red);
		g2d.draw3DRect(offset_x, offset_y, this.bounds.getWidth() - 1, this.bounds.getHeight() - 1, true);*/
	}
	
	private void paint_barrier(Graphics2D g2d,
	                           int offset_x, int offset_y, 
	                           boolean image_paint, 
	                           Color main_color_light, 
	                           Color main_color_dark, 
	                           Color bar_color, 
	                           Color inactive_nozzle_color)
	{		
		// Rahmen & Antriebsbalken
		paint_bar_frame(g2d, offset_x, offset_y, 0.15f, 0f,    0f,    0.5f, bar_color, main_color_light, true);			
		paint_bar_frame(g2d, offset_x, offset_y, 0.07f, 0.35f, 0.04f, 0.7f, inactive_nozzle_color, null, true);
		
		// "Augen"
		this.paint_barrier_eyes(g2d, 
								offset_x, 
								offset_y, 
								MyColor.barrierColor[MyColor.EYES][Events.timeOfDay], 
								image_paint);
		
		// Turbinen-Innenraum
		this.paint_rotor_interior(g2d, main_color_dark, offset_x, offset_y );
		
		if(this.is_destroyed){this.paint_rotor(g2d, offset_x, offset_y);}
		
		//g2d.setPaint(Color.red);	
		//g2d.drawRoundRect(offset_x, offset_y, this.bounds.getWidth()-1, this.bounds.getHeight()-1, this.bounds.getWidth()/2, this.bounds.getHeight()/2);
		//g2d.drawRect(offset_x, offset_y, this.bounds.getWidth() - 1, this.bounds.getHeight() - 1);
		//Menu.paint_frame(g2d, offset_x, offset_y, this.bounds.getWidth() - 4, this.bounds.getHeight() - 4, Color.yellow);
	}
	
	private void paint_rotor_interior(Graphics2D g2d, Color main_color_dark,
	                                  int offset_x, int offset_y)
	{
		int distance_x = (int) (BARRIER_BORDER_SIZE * this.paintBounds.width),
			distance_y = (int) (BARRIER_BORDER_SIZE * this.paintBounds.height);
					
		g2d.setPaint(new GradientPaint(	0, 
										offset_y, 
										main_color_dark, 
										0, 
										offset_y + 0.045f*this.paintBounds.height,
										MyColor.dimColor(main_color_dark, 0.85f), 
										true));	
		
		g2d.fillOval(offset_x + distance_x, 
					 offset_y + distance_y, 
					 this.paintBounds.width  - 2 * distance_x,
					 this.paintBounds.height - 2 * distance_y);
	}

	private void paint_roof(Graphics2D g2d, Color roof_color, int offset_x,
							int offset_y, int direction_x)
	{
		g2d.setPaint(roof_color);
		g2d.fillRoundRect(	(int) (offset_x + (direction_x == 1 ? 0.05f :  0.35f) * this.paintBounds.width),
							offset_y, 
							(int) (0.6f   * this.paintBounds.width),
							(int) (0.125f * this.paintBounds.height),
							(int) (0.6f   * this.paintBounds.width),
							(int) (0.125f * this.paintBounds.height));
	}

	// malen des Schiffrumpfes	
	private void paint_airframe(Graphics2D g2d, Color main_color_light,
	                            int offset_x, int offset_y, int direction_x)
	{		
		this.set_airframe_color(g2d, offset_y, main_color_light);
		
		if(this.model == TIT)
		{			
			g2d.fillArc(offset_x,
						(int) (offset_y - 0.333f * this.paintBounds.height - 2),
						this.paintBounds.width,
						this.paintBounds.height, 180, 180);
			
			g2d.fillArc((int)(offset_x + (direction_x == 1 ? 0.2f * this.paintBounds.width : 0)),
						(int)(offset_y - 0.667f * this.paintBounds.height),
						(int)(			 0.8f   * this.paintBounds.width),
						(int)(			 1.667f * this.paintBounds.height), 180, 180);
		}
		else if(this.model == CARGO)
		{
			g2d.fillOval(	(int)(offset_x + 0.02f * this.paintBounds.width),
					(int)(offset_y + 0.1f * this.paintBounds.height),
					(int)(0.96f * this.paintBounds.width),
					(int)(0.9f  * this.paintBounds.height));
			
			g2d.fillRect(	(int)(offset_x + (direction_x == 1 ? 0.05f : 0.35f) * this.paintBounds.width),
							(int)(offset_y + 0.094f * this.paintBounds.height),
							(int)(0.6f * this.paintBounds.width),
							(int)(0.333f * this.paintBounds.height));

			g2d.fillRoundRect(	(int) (offset_x + (direction_x == 1 ? 0.05f : 0.35f) * this.paintBounds.width),
								(int) (offset_y + 0.031 * this.paintBounds.height),
								(int) (0.6f * this.paintBounds.width),
								(int) (0.125f * this.paintBounds.height),
								(int) (0.6f * this.paintBounds.width),
								(int) (0.125f * this.paintBounds.height));
		
			// Rückflügel
			g2d.fillArc(	(int)(offset_x + (direction_x == 1 ? 0.5f * this.paintBounds.width : 0)),
							(int)(offset_y - 0.3f * this.paintBounds.height),
							(int)(0.5f * this.paintBounds.width),
							this.paintBounds.height,
							direction_x == 1 ? -32 : 155,  
							57);
		}
	}

	private void paint_vertical_stabilizer(Graphics2D g2d, 
	                                       int offset_x, int offset_y,
	                                       int direction_x)
	{
		g2d.setPaint(this.gradientColor);		
		g2d.fillArc((int)(offset_x + (direction_x == 1 ? 0.4f : 0.1f) * this.paintBounds.width),
				(int)(offset_y - 						   0.917f * this.paintBounds.height),
				(int)(0.5f * this.paintBounds.width),
				 2 * this.paintBounds.height, direction_x == 1 ? 0 : 160, 20);
	}
	
	private void set_airframe_color(Graphics2D g2d, int offset_y,
									Color main_color_light)
	{
		this.gradientColor = new GradientPaint(	
				0, 
				offset_y + (this.model == TIT ? 0.25f : 0.375f) * this.paintBounds.height,
				main_color_light,
				0,
				offset_y + this.paintBounds.height,
				MyColor.dimColor(main_color_light, 0.5f),
				true);
			
		g2d.setPaint(this.gradientColor);		
	}	
	
	private void paint_barrier_eyes(Graphics2D g2d)
	{
		paint_barrier_eyes(	g2d, 
							this.paintBounds.x,
							this.paintBounds.y,
							this.alpha != 255 
								? MyColor.setAlpha(MyColor.variableRed, this.alpha) 
								: MyColor.variableRed,
							false);
	}
	
	public void paint_barrier_eyes(Graphics2D g2d, int x, int y, Color color, boolean image_paint)
	{		
		int border_distance = (int)(0.85f * BARRIER_BORDER_SIZE * this.paintBounds.width),
			eye_size = 		  (int)(	    BARRIER_EYE_SIZE    * this.paintBounds.width);
				
		g2d.setPaint(color);
		
		g2d.fillOval(x + border_distance, 
					 y + border_distance,
					 eye_size, eye_size);
		
		g2d.fillOval(x - border_distance + this.paintBounds.width  - eye_size,
					 y - border_distance + this.paintBounds.height - eye_size,
					 eye_size, eye_size);		
		
		if(!image_paint && !(this.snooze_timer > SNOOZE_TIME)){g2d.setPaint(MyColor.reversed_RandomRed(color));}
		g2d.fillOval(x + border_distance, 
					 y - border_distance + this.paintBounds.height - eye_size,
					 eye_size, eye_size);		
		
		g2d.fillOval(x - border_distance + this.paintBounds.width  - eye_size,
					 y + border_distance,
					 eye_size, eye_size);
	}

	private static void paint_red_cross(Graphics2D g2d, int x, int y, int height)
	{
		g2d.setColor(Color.red);				
		g2d.setStroke(new BasicStroke(height/5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));				
		g2d.drawLine(x + height/2, y + height/5, x + height/2, y + (4 * height)/5);
		g2d.drawLine(x + height/5, y + height/2, x + (4 * height)/5, y + height/2);			
		g2d.setStroke(new BasicStroke(1));
		//g2d.drawRect(x, y, height, height);
	}

	void paint_tractor_beam(Graphics2D g2d, Helicopter helicopter)
	{		
		paint_energy_beam(	g2d,	
							this.paintBounds.x,
							this.paintBounds.y + 1,
							(int)(helicopter.bounds.getX() 
								+ (helicopter.is_moving_left 
									? Helicopter.FOCAL_PNT_X_LEFT 
									: Helicopter.FOCAL_PNT_X_RIGHT)),  // 114 
							(int)(helicopter.bounds.getY() 
								+ Helicopter.FOCAL_PNT_Y_EXP));
	}
	
	private void paint_shield_beam(Graphics2D g2d)
	{				
		paint_energy_beam(g2d,	this.paintBounds.x + (this.direction.x + 1)/2 * this.paintBounds.width,
								this.paintBounds.y,
								Events.boss.paintBounds.x + Events.boss.paintBounds.width/48,
								Events.boss.paintBounds.y + Events.boss.paintBounds.width/48);
	}
	
	public static void paint_energy_beam(Graphics2D g2d, int x1, int y1, int x2, int y2)
	{
		g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(MyColor.green);
		g2d.drawLine(x1, y1, x2, y2);
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(Color.green);
		g2d.drawLine(x1, y1+1, x2, y2);
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	}	
	
	private void paint_window(Graphics2D g2d)
	{
		paint_window(g2d, 
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
	
	public void paint_window(Graphics2D g2d, int x, int y, Color color, int direction_x, boolean getarnt)
	{
		this.set_window_color(g2d, color, getarnt);
						
		if(this.model == TIT)
		{
			g2d.fillArc(	(int) (x + (direction_x == 1 ? 0.25f : 0.55f) 
										* this.paintBounds.width),
							y, 
							(int) (0.2f   * this.paintBounds.width),
							(int) (0.267f * this.paintBounds.height),
							180, 
							180);
		}
		else if(this.model == CARGO)
		{
			g2d.fillArc(	(int) (x + (direction_x == 1 ? 0.1 : 0.6) 
										* this.paintBounds.width),
							y, 
							(int) (0.3f   * this.paintBounds.width),
							(int) (0.333f * this.paintBounds.height),
							direction_x == 1 ? 90 : 0,
							90);
		}
	}
	
	private void set_window_color(Graphics2D g2d, Color color, boolean getarnt)
	{
		if(color == null && !getarnt)
		{
			g2d.setColor(this.is_living_boss() 
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
	
	public static void adapt_to_level(Helicopter helicopter, int level, boolean real_level_up)
	{		
		if(level == 1)
		{
			max_nr = 2;
			boss_selection = STANDARD;
			selection = 3;
			max_barrier_nr = 0;
			selection_barrier = 1;
		}
		else if(level == 2){max_nr = 3;}
		else if(level == 3){selection = 6;}
		else if(level == 4){selection = 10; max_barrier_nr = 1;}
		else if(level == 5){selection = 15;}
		else if(level == 6)
		{			
			creation_stop = false;
			max_nr = 3;			
			boss_selection = STANDARD;
			selection = 25;
			max_barrier_nr = 1;
			selection_barrier = 2;
			
		}
		else if(level == 7){selection = 30; max_barrier_nr = 2;}
		else if(level == 8){max_nr = 4;}				
		else if(level == 9)
		{
			selection = 35; 
			max_nr = 3;
			max_barrier_nr = 3;
		}
		else if(level == 10)
		{
			creation_stop = true;
			boss_selection = BOSS_1;
			selection = 0;
			helicopter.powerUp_decay();
		}	  
		else if(level == 11)
		{
			max_nr = 3;
			boss_selection = STANDARD;
			selection = 75;
			max_barrier_nr = 1;
			selection_barrier = 2;
			
			if(( helicopter.no_cheats_used || Events.save_anyway)
				 && !Events.boss1_killed_b4())
			{
				Menu.unlock(HELIOS);
			}
			if(real_level_up){Events.determine_highscore_times(helicopter);}	
		}				
		else if(level == 12){selection_barrier = 3;}
		else if(level == 13){max_barrier_nr = 2; selection = 105;}
		else if(level == 14){selection = 135;} 
		else if(level == 15){selection_barrier = 4;}
		else if(level == 16)
		{			
			creation_stop = false;
			max_nr = 4;
			boss_selection = STANDARD;
			selection = 155;	
			max_barrier_nr = 2;
			selection_barrier = 4;
		}
		else if(level == 17){selection = 175;}
		else if(level == 18){selection_barrier = 5;}
		else if(level == 19){max_barrier_nr = 3;}
		else if(level == 20)
		{
			creation_stop = true; 
			boss_selection = BOSS_2;
			selection = 0;
			helicopter.powerUp_decay();
			if((helicopter.no_cheats_used||Events.save_anyway) && !Events.reachedLevelTwenty[helicopter.getType().ordinal()])
			{
				helicopter.update_unlocked_helicopters();
			}
		}
		else if(level == 21)
		{
			max_nr = 3;
			boss_selection = STANDARD;
			selection = 400;
			max_barrier_nr = 2;
			selection_barrier = 5;
			
			if(real_level_up){Events.determine_highscore_times(helicopter);}	
		}
		else if(level == 22){selection = 485;}
		else if(level == 23){selection = 570;}
		else if(level == 24){max_nr = 4;}
		else if(level == 25){selection = 660;}		
		else if(level == 26)
		{
			creation_stop = false;
			max_nr = 4;
			boss_selection = STANDARD;
			selection = 735;	
			max_barrier_nr = 2;
			selection_barrier = 5;
		}		 
		else if(level == 27){selection = 835;}
		else if(level == 28){max_nr = 5;}
		else if(level == 29){max_nr = 4; max_barrier_nr = 3;}
		else if(level == 30)
		{
			creation_stop = true; 
			boss_selection = BOSS_3;
			selection = 0;	
			helicopter.powerUp_decay();
		}
		else if(level == 31)
		{
			max_nr = 3;
			boss_selection = STANDARD;
			selection = 1670;
			max_barrier_nr = 2;
			selection_barrier = 5;
			
			if(real_level_up){Events.determine_highscore_times(helicopter);}		
		}
		else if(level == 32){selection_barrier = 6;}
		else if(level == 33){selection = 2175;}
		else if(level == 34){max_nr = 4;} 
		else if(level == 35){selection = 3180;} 
		else if(level == 36)
		{
			creation_stop = false;
			max_nr = 4;
			boss_selection = STANDARD;
			selection = 4185;
			max_barrier_nr = 2;
			selection_barrier = 6;
		} 
		else if(level == 37){selection = 5525;} 
		else if(level == 38){max_nr = 5;} 
		else if(level == 39){max_nr = 4; max_barrier_nr = 3;}
		else if(level == 40)
		{
			creation_stop = true; 
			boss_selection = BOSS_4;
			selection = 0;
			helicopter.powerUp_decay();
		}			  
		else if(level == 41)
		{
			max_nr = 3;
			boss_selection = STANDARD;
			selection = 15235;	
			max_barrier_nr = 2;
			selection_barrier = 6;
			
			if(real_level_up){Events.determine_highscore_times(helicopter);}	
		}
		else if(level == 42){selection_barrier = 7; max_nr = 4;}
		else if(level == 43){selection = 20760;}
		else if(level == 44){selection_barrier = 8; max_nr = 5;}
		else if(level == 45){selection = 26285;}
		else if(level == 46)
		{
			creation_stop = false;
			max_nr = 5;
			boss_selection = STANDARD;
			selection = 31810;
			max_barrier_nr = 2 ;
			selection_barrier = 8;
		}
		else if(level == 47){max_nr = 6;} 
		else if(level == 48){max_barrier_nr = 3;}
		else if(level == 49){max_nr = 7;}
		else if(level == 50)
		{
			creation_stop = true; 
			boss_selection = FINAL_BOSS;
			selection = 0;
			helicopter.powerUp_decay();
		}
	}
	
	/** Methoden zur Erstellung von Gegnern
	 */	
	
	public static void generateNewEnemies(ArrayList<LinkedList<Enemy>> enemy, Helicopter helicopter)
	{
		Events.last_creation_timer++;
		if(last_carrier != null){create_carrier_servants(helicopter, enemy);}
		else if(creation_stop){verify_creation_stop(enemy, helicopter);}		
		if(boss_servant_creation_approved()){create_boss_servant(helicopter, enemy);}
		else if(enemy_creation_approved(enemy)){creation(helicopter, enemy);}
	}
	
	private static void create_carrier_servants(Helicopter helicopter,
												ArrayList<LinkedList<Enemy>> enemy)
	{
		for(int m = 0; 
				m < (last_carrier.is_mini_boss 
						? 5 + MyMath.random(3)
						: 2 + MyMath.random(2)); 
				m++)
			{
				creation(helicopter, enemy);
			}			
			last_carrier = null;		
	}

	private static void verify_creation_stop(ArrayList<LinkedList<Enemy>> enemy, 
	                                         Helicopter helicopter)
	{
		if(	enemy.get(ACTIVE).isEmpty() 
			&& last_carrier == null 
			&& !(helicopter.is_poweredUp() 
				 && Events.isBossLevel()) )
		{
			creation_stop = false;
			if(Events.isBossLevel())
			{
				max_nr = 1;
				max_barrier_nr = 0;
				Events.set_boss_level_up_conditions();
			}
		}
	}	
		
	private static boolean boss_servant_creation_approved()
	{
		return     make_boss2_servants	
				|| make_boss4_servant  
				|| make_all_boss5_servants
				|| has_to_make_boss_5_servants();
	}
	
	private static boolean has_to_make_boss_5_servants()
	{			
		for(int type = 0; type < NR_OF_BOSS_5_SERVANTS; type++)
		{
			if(make_boss5_servant[type]){return true;}
		}
		return false;
	}
	
	private static void create_boss_servant(Helicopter helicopter, 
	                                        ArrayList<LinkedList<Enemy>> enemy)
	{
		if(make_boss2_servants)
		{
			create_boss2_servants(helicopter, enemy);
		}
		else if(make_boss4_servant)
		{								 
            make_boss4_servant = false;
			creation(helicopter, enemy);                
		}
		else if(make_all_boss5_servants)
		{
			create_all_boss5_servants(helicopter, enemy);							
		}
		else for(int type = 0; type < NR_OF_BOSS_5_SERVANTS; type++)
		{
			if(make_boss5_servant[type])
			{
				make_boss5_servant[type] = false;
				boss_selection = id(type);						
				creation(helicopter, enemy);	
			}
		}			
	}
	
	private static void create_boss2_servants(Helicopter helicopter, 
	                                          ArrayList<LinkedList<Enemy>> enemy)
    {
    	make_boss2_servants = false;
		creation_stop = true; 
		boss_selection = BOSS_2_SERVANT;
		max_nr = 12;
		for(int m = 0; m < max_nr; m++){creation(helicopter, enemy);}
    }
    
    private static void create_all_boss5_servants(Helicopter helicopter, 
                                                  ArrayList<LinkedList<Enemy>> enemy)
    {
    	make_all_boss5_servants = false;
    	for(int i = -8; i > -13; i--)
    	{
    		boss_selection = i;
    		creation(helicopter, enemy);
    	}
    }	

    private static boolean enemy_creation_approved(ArrayList<LinkedList<Enemy>> enemy)
	{		
		int nr_of_enemies = enemy.get(ACTIVE).size();
		return !creation_stop
				&&((Events.last_creation_timer > 20  && !Events.isBossLevel()) ||
				   (Events.last_creation_timer > 135 ) )
				&& nr_of_enemies < (max_nr + max_barrier_nr)
				&& MyMath.creation_probability(
						Events.isBossLevel()
							? 0
							: (max_nr + max_barrier_nr) - nr_of_enemies, 1)
				&& !(Events.level > 50)
				&& !(!enemy.get(ACTIVE).isEmpty()
						&& enemy.get(ACTIVE).getFirst().isMajorBoss());
	}
    
	private static void creation(Helicopter helicopter, 
	                             ArrayList<LinkedList<Enemy>> enemy)
	{
		Iterator<Enemy> i = enemy.get(INACTIVE).iterator();
		Enemy e;
		if(i.hasNext()){e = i.next(); i.remove();}	
		else{e = new Enemy();}
		enemy.get(ACTIVE).add(e);
		Events.last_creation_timer = 0;
		helicopter.enemies_seen++;
		e.create(helicopter, enemy.get(ACTIVE).size());
	}	
    	
	private void create(Helicopter helicopter, int nr_of_enemies)
	{			
		this.reset();		
		if(last_carrier != null){this.create_scampering_vessel(true);}
		else if(barrier_creation_approved(nr_of_enemies)){this.create_barrier(helicopter);}
		else if(rock_creation_approved()){this.create_rock(helicopter);}
		else if(kaboom_creation_approved()){this.create_kaboom(helicopter);}
		else if(boss_selection == 0){this.create_standard_enemy();}
		else{this.create_boss(helicopter);}
		
		if(this.model != BARRIER)
		{
			this.farbe1 = MyColor.dimColor(this.farbe1, 1.3f);
			this.farbe2 = MyColor.dimColor(this.farbe1, this.dimFactor);
		}		
		if(this.can_become_miniboss()){this.turn_into_miniboss(helicopter);}		
		this.reward_modifier = this.is_boss() ? 0 : 5 - MyMath.random(11);
		this.starting_hitpoints = this.hitpoints;
		
		// Festlegen der Höhe und der y-Position des Gegners
		if(!this.has_height_set){this.set_height();}	
		if(!this.has_y_pos_set){this.set_initial_y();}
				
		this.initialize_shoot_direction();
		this.speed_level.setLocation(this.target_speed_level);
		this.set_paint_bounds((int)this.bounds.getWidth(),
							  (int)this.bounds.getHeight());
		this.assign_image(helicopter);
	}

	private void assign_image(Helicopter helicopter)
	{
		for(int i = 0; i < 2; i++)
		{
			this.image[i] = new BufferedImage((int)(1.028f * this.paintBounds.width),
											  (int)(1.250f * this.paintBounds.height),
											  BufferedImage.TYPE_INT_ARGB);
			this.graphics[i] = getGraphics(this.image[i]);			
			
			//this.graphics[i].setComposite(AlphaComposite.Src);
			
			this.paint_image(this.graphics[i], 1-2*i, null, true);
			if(this.cloaking_timer != DISABLED && helicopter.getType() == OROCHI)
			{
				BufferedImage 
					 temp_image = new BufferedImage((int)(1.028f * this.paintBounds.width),
							 						(int)(1.250f * this.paintBounds.height),
							 						BufferedImage.TYPE_INT_ARGB);
				
				this.image[2+i] = new BufferedImage((int)(1.028f * this.paintBounds.width),
													(int)(1.250f * this.paintBounds.height),
													BufferedImage.TYPE_INT_ARGB);
				
				this.paint_image(getGraphics(temp_image), 1-2*i, Color.red, true);								
				(getGraphics(this.image[2+i])).drawImage(temp_image, ROP_CLOAKED, 0, 0);
			}
		}		
	}

	private void reset()
	{
		this.lifetime = 0;
		this.type = STANDARD;
		this.model = TIT;
		this.target_speed_level.setLocation(ZERO_SPEED);		
		this.set_x(Main.VIRTUAL_DIMENSION.width + APPEARANCE_DISTANCE);
		this.direction.setLocation(-1, MyMath.random_direction());	
		this.strength = 0;				
		this.call_back = 0;
		this.shield = 0;
		this.dimFactor = 1.5f;
		this.operator = null;	
		this.alpha = 255;
		
		this.is_destroyed = false;
		this.is_marked_for_removal = false;	
		this.has_unresolved_intersection = false;
		this.can_move_chaotic = false;
		this.can_dodge = false;
		this.can_chaos_speedup = false;
		this.can_kamikaze = false;
		this.can_early_turn = false;
		this.is_lasting = false;
		this.is_touching_helicopter = false;
		this.is_speed_boosted = false;
		this.is_explodable = false;
		this.can_learn_kamikaze = false;
		this.can_frontal_speedup = false;
		this.can_sinus_move = false;
		this.is_shielding = false;
		this.can_turn = false;
		this.can_loop = false;
		this.is_clockwise_barrier = true;
		this.stopping_barrier = null;
		this.is_previous_stopping_barrier = null;
		this.is_stunnable = true;
		this.is_mini_boss = false;
		this.has_crashed = false;
		this.can_instant_turn = false;
		this.is_carrier = false;	
		this.is_recovering_speed = false;
		this.is_kaboom = false;
		this.has_height_set = false;
		this.has_y_pos_set = false;		
		this.is_emp_shocked = false;
		
		this.collision_damage_timer = READY;
		this.collision_audio_timer = READY;
		this.turn_audio_timer = READY;		
		this.dodge_timer = READY;
		this.turn_timer = READY;
		this.exploding_timer = READY;
		this.emp_slowed_timer = READY;		
		this.invincible_timer = READY;
		this.chaos_timer = READY;
		this.snooze_timer = READY;
		this.non_stunable_timer = READY;
		
		this.spawning_hornet_timer = DISABLED;		
		this.cloaking_timer = DISABLED;
		this.teleport_timer = DISABLED;
		this.shield_maker_timer = DISABLED;
		this.shoot_timer = DISABLED;
		this.barrier_shoot_timer = DISABLED;
		this.barrier_teleport_timer = DISABLED;
		this.borrow_timer = DISABLED;
		this.static_charge_timer = DISABLED;		
		this.speedup = DISABLED;
		
		this.uncloaking_speed = 1;
		this.tractor = DISABLED;
		this.batch_wise_move = 0;
		
		this.shooting_direction.setLocation(0, 0);		
		this.shoot_pause = 0;
		this.shooting_rate = 0;
		this.shots_per_cycle = 0;
		this.shooting_cycle_length = 0;				
		this.shot_speed = 0;		
		this.shot_rotation_speed = 0;
		this.shotType = DISCHARGER;
		
		this.touched_site = NONE;
		this.last_touched_site = NONE;
		
		this.untouched_counter = 0;
		this.rotor_color = 0;
		this.deactivation_prob = 0f;
		this.stunning_timer = 0;
		
		this.total_stunning_time = 0;
		this.knock_back_direction = 0;
	}

	private static boolean barrier_creation_approved(int nr_of_enemies)
	{		
		return Events.level >= MIN_BARRIER_LEVEL 
				&& !Events.isBossLevel()
				&& barrier_timer == 0  
				&& (MyMath.toss_up(0.35f) 
					|| (nr_of_enemies - current_nr_of_barriers >= max_nr))
				&& current_nr_of_barriers < max_barrier_nr;
	}
	
	private void create_barrier(Helicopter helicopter)
	{
		this.model = BARRIER;
		this.type = MyMath.random(selection_barrier);	
		if(MyMath.toss_up(0.05f) && Events.level >= MIN_FUTURE_LEVEL)
		{
			this.type = Math.min(selection_barrier + MyMath.random(2), 7);			
		}
		//this.type = (MyMath.toss_up() ? 2 : 5);
		//this.type = 2;
		
		helicopter.enemies_seen--;
		this.hitpoints = Integer.MAX_VALUE;
		this.rotor_color = 1;
		this.is_clockwise_barrier = MyMath.toss_up();
				
		if(this.type < 2)
		{
			this.farbe1 = MyColor.bleach(Color.green, 0.6f);
			this.is_lasting = true;
			this.deactivation_prob = 0.25f;
			
			// Level 2
			if(this.type == 0){this.set_var_width(65);}
			
			// Level 6
			else if(this.type == 1)
			{
				this.set_var_width(150);				
				this.set_initial_y(GROUND_Y - this.bounds.getWidth());
			}
		}	
		// Level 12
		else if(this.type == 2)
		{
			this.farbe1 = MyColor.bleach(Color.yellow, 0.6f);
			this.target_speed_level.setLocation(0, 1 + 2*Math.random());	//d //1
			this.deactivation_prob = 0.2f;
			this.set_var_width(65);
			
			this.rotor_color = 2;
			this.static_charge_timer = READY; 
			this.is_lasting = true;
		}
		// Level 15
		else if(this.type == 3)
		{
			this.farbe1 = MyColor.bleach(new Color(255, 192, 0), 0.0f);
			this.target_speed_level.setLocation(0.5 + 2*Math.random(), 0); //d			
			this.deactivation_prob = 0.167f;
			this.set_var_width(105);			
			if(this.target_speed_level.getX() >= 5){this.direction.x = 1;}	
			
			
			this.set_location(this.target_speed_level.getX() >= 5 
									? -this.bounds.getWidth()-APPEARANCE_DISTANCE
									: this.bounds.getX(),
							  GROUND_Y - this.bounds.getWidth() - (5 + MyMath.random(11)));
			this.has_y_pos_set = true;
		}
		// Level 18
		else if(this.type == 4)
		{									
			this.deactivation_prob = 0.143f;
			this.set_var_width(85);		
			this.has_y_pos_set = true;
			this.barrier_shoot_timer = READY;
			this.set_barrier_shooting_properties();
			this.shot_rotation_speed 
				= MyMath.toss_up(SPIN_SHOOTER_RATE) && Events.level >= MIN_SPIN_SHOOTER_LEVEL 
					? MyMath.random_direction()*(this.shooting_rate/3 + MyMath.random(10)) 
					: 0;	
			
			this.is_lasting = true;
		}
		// Level 32
		else if(this.type == 5)
		{					
			this.deactivation_prob = 0.11f;
			
			this.set_var_width(80);				
			this.set_initial_y(GROUND_Y - this.bounds.getWidth()/8);
			
			this.borrow_timer = READY;
			this.set_barrier_shooting_properties();		
									
			this.is_lasting = true;
		}
		// Level 42
		else if(this.type == 6)
		{
			//this.farbe1 = MyColor.bleach(new Color(0, 255, 255), 0.6f);			
			this.farbe1 = MyColor.bleach(Color.green, 0.6f);
			this.deactivation_prob = 0.25f;
			this.set_var_width(80);	
			
			this.is_lasting = true;
		}
		// Level 44
		else if(this.type == 7)
		{
			this.farbe1 = MyColor.bleach(MyColor.cloaked, 0.6f);	
			//this.farbe1 = new Color(MyMath.random(255), MyMath.random(255), MyMath.random(255));	
			this.deactivation_prob = 0.067f;
			this.set_var_width(100);	
						
			this.barrier_teleport_timer = READY;
			this.set_barrier_shooting_properties();	
			this.start_barrier_uncloaking(helicopter);							
						
			this.has_y_pos_set = true;	
			this.call_back = 1 + MyMath.random(4);
		}
		
		this.farbe2 = MyColor.dimColor(this.farbe1, 0.75f);		
				
		if(Events.timeOfDay == NIGHT)
		{
			this.farbe1 = MyColor.dimColor(this.farbe1, MyColor.BARRIER_NIGHT_DIM_FACTOR);
			this.farbe2 = MyColor.dimColor(this.farbe2, MyColor.BARRIER_NIGHT_DIM_FACTOR);
		}		
		barrier_timer = (int)((helicopter.bounds.getWidth() + this.bounds.getWidth())/2);		
		this.strength = (int)(1.0f/this.deactivation_prob);
	}
	
	private void place_at_pause_position()
	{
		this.call_back--;
		this.uncloak(DISABLED);
		this.barrier_teleport_timer = READY;		
		this.set_y(GROUND_Y + 2 * this.bounds.getWidth());
	}

	private static boolean rock_creation_approved()
	{
		return currentRock == null
				&& Events.level >= MIN_ROCK_LEVEL 
				&& !Events.isBossLevel()
				&& rock_timer == 0 
				&& MyMath.toss_up(ROCK_PROB);
	}
	
	private void create_rock(Helicopter helicopter)
	{
		currentRock = this;
		this.type = Integer.MAX_VALUE;
		this.model = CARGO;	
		helicopter.enemies_seen--;
		this.farbe1 = new Color((180 + MyMath.random(30)), (120 + MyMath.random(30)),(0 + MyMath.random(15)));
		this.hitpoints = 1;
		this.invincible_timer = Integer.MAX_VALUE;
			
		this.bounds.setRect(this.bounds.getX(), 
							GROUND_Y - ROCK_WIDTH * (HEIGHT_FACTOR_SUPERSIZE - 0.05f), // 0.05
							ROCK_WIDTH, 
							ROCK_WIDTH * HEIGHT_FACTOR_SUPERSIZE);
		this.has_height_set = true;
		this.has_y_pos_set = true;
		this.is_lasting = true;
		
		this.strength = 0;	
	}
	
	private static boolean kaboom_creation_approved()
	{		
		return Events.level >= MIN_KABOOM_LEVEL 
				&& !Events.isBossLevel()
				&& MyMath.toss_up(KABOOM_PROB);
	}
	
	private void create_kaboom(Helicopter helicopter)
	{
		this.type = Integer.MAX_VALUE;
		this.is_kaboom = true;
		this.farbe1 = Color.white;
		this.hitpoints = Integer.MAX_VALUE;	
		this.set_var_width(KABOOM_WIDTH);
		helicopter.enemies_seen--;				
		this.target_speed_level.setLocation(0.5 + 0.5*Math.random(), 0); //d
		this.is_explodable = true;		
		this.set_initial_y(GROUND_Y - 2*this.bounds.getWidth()*HEIGHT_FACTOR);
				
		this.strength = 0;		
	}
	
	private void create_standard_enemy()
	{
		this.type = MyMath.random(selection);
		//this.type = 30;
		this.enemyType = enemySelector.getType(this.type);

		switch(this.enemyType)
		{
			// Level 1
			case TINY:
				this.farbe1 = new Color((180 + MyMath.random(30)),
						(120 + MyMath.random(30)),
						(0 + MyMath.random(15)));
				this.hitpoints = 2;
				this.set_var_width(110);
				this.target_speed_level.setLocation(0.5 + Math.random(), //d
						0.5 * Math.random());	//d
				this.is_explodable = true;
				this.dimFactor = 1.2f;

				this.strength = 1;
				break;

			// Level 3
			case SMALL:
				this.farbe1 = new Color((140 + MyMath.random(25)),
						(65 + MyMath.random(35)),
						(0 + MyMath.random(25)));
				this.hitpoints = 3 + MyMath.random(3);
				this.set_var_width(125);
				this.target_speed_level.setLocation(1 + 1.5*Math.random(), //d
						0.5*Math.random());	//d
				this.is_explodable = true;

				this.strength = 2;
				break;

			// level 5
			case RUNABOUT:
				this.farbe1 = new Color((100 + MyMath.random(30)),
						(100 + MyMath.random(30)),
						(40 + MyMath.random(25)));
				this.hitpoints = 2 + MyMath.random(2);
				this.set_var_width(100);
				this.target_speed_level.setLocation(2 + 2*Math.random(), //d
						2.5 + 1.5*Math.random());		//d
				this.is_explodable = true;

				this.strength = 2;
				break;

			// Level 7
			case FREIGHTER:
				this.model = CARGO;
				this.farbe1 = new Color((100 + MyMath.random(30)),
						(50 + MyMath.random(30)),
						(45 + MyMath.random(20)));
				this.set_hitpoints(25);
				this.set_var_width(145);
				this.target_speed_level.setLocation(0.5 + Math.random(), //d
						0.5*Math.random());	//d
				this.can_early_turn = true;
				this.can_turn = true;

				this.strength = 4;
				break;

			// Level 11
			case BATCHWISE:
				this.farbe1 = new Color((135 + MyMath.random(30)),
						(80+MyMath.random(20)),
						(85 + MyMath.random(30)));
				this.set_hitpoints(16);
				this.set_var_width(130);
				this.target_speed_level.setLocation(7 + 4*Math.random(), //d
						1 + 0.5*Math.random()); //d
				this.batch_wise_move = 1;

				this.strength = 6;
				break;

			// Level 13
			case SINUS:
				this.farbe1 = new Color((185 + MyMath.random(40)),
						( 70 + MyMath.random(30)),
						(135 + MyMath.random(40)));
				this.set_hitpoints(6);
				this.set_var_width(110);
				this.target_speed_level.setLocation(2.5 + 2.5*Math.random(), 11); //d

				this.set_initial_y(TURN_FRAME.getCenterY());
				this.can_sinus_move = true;
				this.is_explodable = true;
				this.strength = 6;
				break;

			// Level 16
			case DODGER:
				this.farbe1 = new Color((85 + MyMath.random(20)),
						(35 + MyMath.random(30)),
						(95 + MyMath.random(30)));
				this.set_hitpoints(24);
				this.set_var_width(170);
				this.target_speed_level.setLocation(1.5 + 1.5*Math.random(), //d
						0.5*Math.random());	//d
				this.can_dodge = true;

				this.strength = 9;
				break;

			// Level 21
			case CHAOS:
				this.farbe1 = new Color((150 + MyMath.random(20)),
						(130 + MyMath.random(25)),
						( 75 + MyMath.random(30)));
				this.set_hitpoints(22);
				this.set_var_width(125);
				this.target_speed_level.setLocation( 3.5 + 1.5*Math.random(), //d
						6.5 + 2*Math.random());	//d
				this.can_move_chaotic = true;
				this.is_explodable = true;

				this.strength = 11;
				break;

			// Level 24
			case CALLBACK:
				this.farbe1 = new Color((70 + MyMath.random(40)),
						(130 + MyMath.random(50)),
						(30 + MyMath.random(45)));
				this.set_hitpoints(30);
				this.set_var_width(95);
				this.target_speed_level.setLocation( 5.5 + 2.5*Math.random(), //d
						5 + 2*Math.random());		//d
				this.is_explodable = true;
				this.call_back = 1;

				this.strength = 10;
				break;

			// Level 26
			case SHOOTER:
				this.model = CARGO;

				this.farbe1 = new Color(80 + MyMath.random(25),
						80 + MyMath.random(25),
						80 + MyMath.random(25));
				this.set_hitpoints(60);
				this.set_var_width(80);
				this.target_speed_level.setLocation( 0.5 + Math.random(), //d
						0.5 * Math.random());	//d
				this.can_dodge = true;
				this.shoot_timer = 0;
				this.shooting_rate = 35;

				this.strength = 12;
				break;

			// Level 31
			case CLOAK:
				this.model = CARGO;

				this.farbe1 = MyColor.cloaked;
				this.set_hitpoints(100);
				this.set_var_width(85);
				this.target_speed_level.setLocation( 0.5 + Math.random(), //d
						1 + 0.5*Math.random());	//d
				this.can_learn_kamikaze = true;
				this.can_instant_turn = true;
				this.cloaking_timer = CLOAKING_TIME + CLOAKED_TIME;
				this.uncloaking_speed = 2;
				this.can_early_turn = true;
				this.is_explodable = true;

				this.strength = 16;
				break;

			// Level 35
			case BOLT:
				this.create_scampering_vessel(last_carrier != null);
				break;

			case CARRIER:
				this.model = CARGO;

				this.farbe1 = new Color(70 + MyMath.random(15),
						60 + MyMath.random(10),
						45 + MyMath.random(10)); // new Color(25 + MyMath.random(35), 70 + MyMath.random(45), 25 + MyMath.random(35));
				this.set_hitpoints(450);
				this.set_var_width(165);
				this.target_speed_level.setLocation( 0.5 + Math.random(), //d
						0.5 * Math.random());	//d
				this.can_early_turn = true;
				this.is_carrier = true;
				this.can_turn = true;

				this.strength = 19;
				break;

			// Level 37
			case YELLOW:
				this.farbe1 = new Color((180 + MyMath.random(50)),
						(230 + MyMath.random(20)),
						(20 + MyMath.random(60)));
				this.set_hitpoints(140);
				this.set_var_width(115);
				this.target_speed_level.setLocation( 4 + 2.5 * Math.random(), //d
						0.5 + Math.random());		//d
				this.is_explodable = true;
				this.can_chaos_speedup = true;
				this.can_dodge = true;

				this.strength = 22;
				break;

			// Level 41
			case AMBUSH:
				this.farbe1 = new Color( 30 + MyMath.random(40),
						60 + MyMath.random(40),
						120 + MyMath.random(40));
				this.set_hitpoints(150);
				this.set_var_width(95);
				this.target_speed_level.setLocation( 1 + 1.5*Math.random(), 0); //d

				this.is_explodable = true;
				this.speedup = READY;

				this.strength = 30;
				break;

			 // Level 43
			case LOOPING:
				this.farbe1 = MyColor.cloaked;
				this.set_hitpoints(330);
				this.set_var_width(105);
				this.target_speed_level.setLocation(9, 11);	//d

				this.direction.y = -1;
				this.set_initial_y(TURN_FRAME.getCenterY());
				this.cloaking_timer = 0;
				this.can_loop = true;

				this.strength = 30;
				break;

			// Level 45
			case CAPTURER:
				this.farbe1 = new Color(  5 + MyMath.random(55),
						105 + MyMath.random(40),
						90 + MyMath.random(30));
				this.set_hitpoints(520);
				this.set_var_width(115);
				this.target_speed_level.setLocation( 2.5 + 2*Math.random(), //d
						4.5 + 1.5*Math.random());//d
				this.tractor = READY;
				this.is_explodable = true;

				this.strength = 30;
				break;

			// Level 46
			case TELEPORTER:
				this.model = CARGO;

				this.farbe1 = new Color(190 + MyMath.random(40),
						10 + MyMath.random(60),
						15 + MyMath.random(60));
				this.set_hitpoints(500);
				this.set_var_width(130);
				this.target_speed_level.setLocation( 1 + Math.random(), //d
						0.5*Math.random());//d
				this.teleport_timer = READY;
				this.can_kamikaze = true;

				this.strength = 35;
				break;
		}
	}
	
	private void create_scampering_vessel(boolean explosion_creation)
	{
		if(explosion_creation){this.type = 3000;}
		
		this.farbe1 = new Color(75 + MyMath.random(30), 
								75 + MyMath.random(30), 
								75 + MyMath.random(30) );
		this.set_hitpoints(26);	
		this.set_var_width(70);
		
		if(explosion_creation)
		{
			this.set_location(last_carrier.bounds.getCenterX(), 			
							  last_carrier.bounds.getCenterY());
			this.has_y_pos_set = true;
		}
		this.is_explodable = true;
		if(explosion_creation)
		{
			this.target_speed_level.setLocation( 10 + 7.5*Math.random(), //d
													0.5 + 3*Math.random());			//d	
			this.call_back = 1 + MyMath.random(3);
			this.direction.x = MyMath.random_direction();
			this.invincible_timer = 67;			
		}
		else 
		{
			this.target_speed_level.setLocation( 12 + 3.5*Math.random(), //d
													0.5 + 3*Math.random());		//d			
			if(MyMath.toss_up()){this.call_back = 1;}
		}		
		
		this.strength = 14;
	}
	
	private void create_boss(Helicopter helicopter)
	{
		this.type = boss_selection;
		
		// Level 10	
		if( this.type == BOSS_1)
		{
			this.farbe1 = new Color(115, 70, 100);
			this.hitpoints = 225;			
			this.set_width(275);			
			this.target_speed_level.setLocation(2, 0.5); //d
			
			this.can_kamikaze = true;
			
			this.strength = 75;		
			Events.boss = this;
		}		
		// Level 20
		else if( this.type == BOSS_2)
		{						
			this.model = CARGO;			
			this.farbe1 = new Color(85, 85, 85);
			this.hitpoints = 500;
			this.set_width(250);
			this.target_speed_level.setLocation(7, 8); //d
		
			this.can_move_chaotic = true;
			this.shoot_timer = 0;
			this.shooting_rate = 5;	
			this.shot_speed = 3;
			this.can_instant_turn = true;
			
			this.strength = 100;		
			Events.boss = this;
		}		
		else if( this.type == BOSS_2_SERVANT)
		{	
			this.bounds.setRect(boss.getX(),
								boss.getY(),
								65,
								this.bounds.getHeight());
			this.has_y_pos_set = true;		
			this.farbe1 = new Color(80 + MyMath.random(25), 80 + MyMath.random(25), 80 + MyMath.random(25));
			this.hitpoints = 15;					
			this.target_speed_level.setLocation(3 + 10.5*Math.random(), //d
												  3 + 10.5*Math.random()); //d
		
			this.direction.x = MyMath.random_direction();
			this.invincible_timer = 67;
			
			this.strength = 5;
		}
		// Level 30
		else if( this.type == BOSS_3)
		{			
			this.set_width(250);
			this.farbe1 = MyColor.cloaked;
			this.hitpoints = 1750;
			this.target_speed_level.setLocation(5, 4); //d

			this.can_move_chaotic = true;
			this.can_kamikaze = true;			
			this.cloaking_timer = READY;		
			this.can_dodge = true;
			this.shoot_timer = 0;
			this.shooting_rate = 10;
			this.shot_speed = 10;
			this.can_instant_turn = true;
			
			this.strength = 500;
			Events.boss = this;
		}
		// Level 40
		else if(this.type == BOSS_4)
		{						
			this.set_width(250);	
			this.farbe1 = Color.red;
			this.hitpoints = 10000;	
			this.target_speed_level.setLocation(10, 10); //d

			this.spawning_hornet_timer = 30;
			boss_selection = BOSS_4_SERVANT;
			max_nr = 15;	
			this.can_turn = true;
			
			this.strength = 1250;
			Events.boss = this;
		}		
		else if(this.type == BOSS_4_SERVANT)
		{	
			this.bounds.setRect(boss.getX(),
								boss.getY(),
								85 + MyMath.random(15),
							    this.bounds.getHeight());
			this.has_y_pos_set = true;
			this.farbe1 = new Color(80 + MyMath.random(20), 80 + MyMath.random(20), 80 + MyMath.random(20));
			this.hitpoints = 100 + MyMath.random(50);					
			this.target_speed_level.setLocation(6 + 2.5*Math.random(), //d
												  6 + 2.5*Math.random()); //d
			this.direction.x = MyMath.random_direction();
			this.is_explodable = true;	
			
			this.strength = 1;			
		}	
		// Level 50
		else if(this.type == FINAL_BOSS)
		{			
			this.set_initial_bounds(this.bounds.getX(),
								    98,
								    FINAL_BOSS_WIDTH,
								    FINAL_BOSS_WIDTH * HEIGHT_FACTOR);
						
			this.farbe1 = MyColor.brown;
			this.hitpoints = 25000;	
			this.target_speed_level.setLocation(23.5, 0); //d

			max_nr = 5;
			this.operator = new FinalEnemysOperator();
			this.is_stunnable = false;
			this.dimFactor = 1.3f;
			
			this.strength = 5000;
			Events.boss = this;
		}		
		else if(this.is_final_boss_servant())
		{
			Events.boss.operator.servants[id(this.type)] = this;
			this.has_y_pos_set = true;
			
			if(this.is_shield_maker())
			{			
				this.bounds.setRect(boss.getX(),
									boss.getY(),
									this.type == SMALL_SHIELD_MAKER ? 125 : 145,
								    this.bounds.getHeight());			
				this.direction.x = MyMath.random_direction();
				
				this.shield_maker_timer = READY;
				this.set_shielding_position();			
								
				if(this.type == SMALL_SHIELD_MAKER)
				{				
					this.target_speed_level.setLocation(7, 6.5); //d
					this.farbe1 = new Color(25, 125, 105);				
					this.hitpoints = 3000;								
					this.strength = 55;
				}			
				else
				{				
					this.target_speed_level.setLocation(6.5, 7); //d
					this.farbe1 = new Color(105, 135, 65);				
					this.hitpoints = 4250; 				
					this.strength = 80;	
					
					this.shoot_timer = 0;
					this.shooting_rate = 25;
					this.shot_speed = 1;	
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
				this.target_speed_level.setLocation(1, 2); //d

				this.cloaking_timer = 0;
				this.can_instant_turn = true;
				
				this.strength = 150;			
				Events.boss.operator.servants[id(BODYGUARD)] = this;
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
				this.target_speed_level.setLocation(2.5, 3); //d
				
				this.can_dodge = true;
				
				this.strength = 65;			
				Events.boss.operator.servants[id(HEALER)] = this;
			}
			else if(this.type == PROTECTOR)
			{
				this.model = BARRIER;
				
				this.bounds.setRect(boss.getX() + 200, 
									GROUND_Y, 
									PROTECTOR_WIDTH, 
									this.bounds.getHeight());
				
				helicopter.enemies_seen--;
				this.hitpoints = Integer.MAX_VALUE;
				this.is_clockwise_barrier = MyMath.toss_up();					
				this.farbe1 = MyColor.bleach(new Color(170, 0, 255), 0.6f);
				this.target_speed_level.setLocation(ZERO_SPEED);	
				
				this.deactivation_prob = 0.04f;			
				this.borrow_timer = READY;			
				this.shooting_rate = 25;			
				this.shots_per_cycle = 5; 
				this.shooting_cycle_length = this.shoot_pause 
											 + this.shooting_rate 
											   * this.shots_per_cycle;				
				this.shot_speed = 10;	
				this.shotType = BUSTER;
				this.is_stunnable = false;			
				this.farbe2 = MyColor.dimColor(this.farbe1, 0.75f);		
				if(Events.timeOfDay == NIGHT)
				{
					this.farbe1 = MyColor.dimColor(this.farbe1, MyColor.BARRIER_NIGHT_DIM_FACTOR);
					this.farbe2 = MyColor.dimColor(this.farbe2, MyColor.BARRIER_NIGHT_DIM_FACTOR);
				}			
				this.strength = (int)(1.0f/this.deactivation_prob);
				Events.boss.operator.servants[id(PROTECTOR)] = this;			
			}			
		}	
	}

	private boolean can_become_miniboss()
	{		
		return 	current_mini_boss == null 
				&& Events.level > 4 
				&& this.model != BARRIER
				&& !(currentRock == this)
				&& !this.is_kaboom
				&& MyMath.toss_up(miniboss_prob) 
				&& this.type > 2;
	}

	private void turn_into_miniboss(Helicopter helicopter)
	{
		helicopter.mini_boss_seen++;
		current_mini_boss = this;
		this.hitpoints = 1+5*this.hitpoints;		
		this.bounds.setRect(this.bounds.getX(),
							this.bounds.getY(), 
							1.44 * this.bounds.getWidth(), 
							1.44 * this.bounds.getHeight());		
		this.is_mini_boss = true;
		this.is_explodable = false;
		this.strength  *= 4;
		this.call_back += 2;
		this.can_turn = true;
		if(  (this.type >= 2175 && !this.can_learn_kamikaze && MyMath.toss_up(0.2f)) ||
		      this.shoot_timer == 0 )
		{
			this.cloaking_timer = 0;
		}		
	}

	private void place_near_helicopter(Helicopter helicopter)
	{		
		boolean left;
		if(     helicopter.bounds.getMaxX()  
			+ (0.5f * this.bounds.getWidth() + BARRIER_DISTANCE) < 1024)
		{
			 left = false;
		}
		else left = true;
					
		int x, 
			y = (int)(helicopter.bounds.getY() 
				+ helicopter.bounds.getHeight()/2
				- this.bounds.getWidth()
				+ Math.random()*this.bounds.getWidth());
		
		if(left)
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
		this.set_location(x, 
						  Math.max(0, Math.min(y, GROUND_Y-this.bounds.getWidth())));
	}
	
	private void set_barrier_shooting_properties()
	{
		if(this.barrier_teleport_timer != DISABLED || this.borrow_timer != DISABLED)
		{
			this.shooting_rate = 35 + MyMath.random(15);
		}
		else
		{
			this.shooting_rate = 25 + MyMath.random(25);
		}
			
		if(this.barrier_teleport_timer == DISABLED){this.shoot_pause = 2 * this.shooting_rate + 20 + MyMath.random(40);}			
		this.shots_per_cycle = 2 + MyMath.random(9); 
		this.shooting_cycle_length = this.shoot_pause + this.shooting_rate * this.shots_per_cycle;				
		this.shot_speed = 5 + MyMath.random(6);		
		if(this.barrier_teleport_timer != DISABLED || (MyMath.toss_up(0.35f) && Events.level >= MIN_BUSTER_LEVEL))
		{
			if(this.barrier_teleport_timer == DISABLED){this.farbe1 = MyColor.bleach(new Color(170, 0, 255), 0.6f);}
			this.shotType = BUSTER;
		}
		else
		{
			this.farbe1 = MyColor.bleach(Color.red, 0.6f);
			this.shotType = DISCHARGER;
		}
	}

	private void set_hitpoints(int hitpoints)
	{
		this.hitpoints = hitpoints + MyMath.random(hitpoints/2);		
	}
	
	private void set_x(double x)
	{
		this.bounds.setRect(x, 
							this.bounds.getY(), 
							this.bounds.getWidth(), 
							this.bounds.getHeight());
	}
	
	private void set_y(double y)
	{
		this.bounds.setRect(this.bounds.getX(), 
							y, 
							this.bounds.getWidth(), 
							this.bounds.getHeight());
	}
	
	private void set_location(double x, double y)
	{
		this.bounds.setRect(x, 
							y, 
							this.bounds.getWidth(), 
							this.bounds.getHeight());
	}
			
	private void set_width(double width)
	{
		this.bounds.setRect(this.bounds.getX(), 
							this.bounds.getY(),
							width,
							this.bounds.getHeight());
	}
	
	private void set_var_width(int width)
	{
		set_width(width + MyMath.random(width/(this.model == BARRIER ? 5 : 10)));
	}
	
	private void set_height()
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
	
	private void set_initial_y()
	{
		if(this.model == BARRIER)
		{
			set_initial_y(Math.random()*(GROUND_Y - this.bounds.getHeight()));
		}
		else
		{
			set_initial_y(90 + Math.random()*(220 - this.bounds.getHeight()));
		}
	}	
	
	private void set_initial_y(double y)
	{
		this.set_y(y);
		this.has_y_pos_set = true;
	}	
	
	private void set_initial_bounds(double x, double y, 
	                                double width, double height)
	{
		this.bounds.setRect(x, y, width, height);
		this.has_y_pos_set = true;
		this.has_height_set = true;
	}
	
	private void initialize_shoot_direction()
	{
		if(this.shoot_timer == READY)
		{
			this.shooting_direction.setLocation( this.direction.x == -1 
													? -1f 
													:  1f, 0f);
		}
		else if(this.barrier_shoot_timer == READY )
		{
			double temp_random 
				= Math.PI * (1 + Math.random()/2) 
					+ (this.bounds.getY() + this.bounds.getHeight()/2 < GROUND_Y/2 
						? Math.PI/2 
						: 0);
		
			this.shooting_direction.setLocation(
				Math.sin(temp_random), 
				Math.cos(temp_random) );
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
		if(rock_timer > 0){rock_timer--;}	
		if(BackgroundObject.background_moves && barrier_timer > 0){barrier_timer--;}
		count_barriers(controller.enemy);
		
		for(Iterator<Enemy> i = controller.enemy.get(ACTIVE).iterator(); i.hasNext();)
		{
			Enemy enemy = i.next();			
			if(!enemy.is_destroyed && !enemy.is_marked_for_removal)
			{								
				enemy.update(controller, helicopter);
			}
			else if(enemy.is_destroyed)
			{
				i.remove(); 
				controller.enemy.get(DESTROYED).add(enemy);
			}			
			else
			{
				enemy.clear_image();
				i.remove(); 
				controller.enemy.get(INACTIVE).add(enemy);
			}			
		}
	}	

	private static void count_barriers(ArrayList<LinkedList<Enemy>> enemy)
	{
		Arrays.fill(living_barrier, null);
		current_nr_of_barriers = 0;
		for(Enemy e : enemy.get(ACTIVE))
		{
			if (e.model == BARRIER
				&& !e.is_destroyed
				&& !e.is_marked_for_removal)
			{
				living_barrier[current_nr_of_barriers] = e;
				current_nr_of_barriers++;
			}
		}		
	}

	private static boolean tournaround_is_turn_away(double dir, double enemy_center,
													double barrier_center)
	{
		return 	   dir ==  1 && enemy_center < barrier_center
				|| dir == -1 && enemy_center > barrier_center;
	}


	public static void paintAllActive(Graphics2D g2d,
									  Controller controller,
									  Helicopter helicopter)
	{
		for(int i = 0; i < current_nr_of_barriers; i++)
		{    			
			living_barrier[i].paint(g2d, helicopter);
		}
		for(Enemy e : controller.enemy.get(ACTIVE))
		{
			if (currentRock != e
				&& e.model != BARRIER
				&& !(e.cloaking_timer > CLOAKING_TIME
				&& e.cloaking_timer <= CLOAKING_TIME + CLOAKED_TIME
				&& !helicopter.hasRadarDevice))
			{
				e.paint(g2d, helicopter);
			}
		}		
	}

	private void update(Controller controller, Helicopter helicopter)
	{												
		this.lifetime++;		
		this.update_timer();	
		if(this.call_back > 0 && Events.isBossLevel()){this.call_back = 0;}
		if(currentRock != this){check_for_barrier_collision();}
		if(this.stunning_timer == READY)
		{
			this.update_stoppable_timer();
			if(this.isMajorBoss()){this.calculate_boss_maneuver(controller.enemy);}
			this.calculate_flight_maneuver(controller, helicopter);
			this.validate_turns();
		}		
		this.calculate_speed(helicopter);	
		this.move();
		
		if(helicopter.can_collide_with(this)){this.collision(controller, helicopter);}
		if(helicopter.getType() == PEGASUS){this.check_for_EMP_strike(controller, helicopter);}
		if(this.has_deadly_ground_contact()){this.destroy(helicopter, controller.powerUp, false);}
		if(this.isToBeRemoved()){this.prepare_removal();}
		this.set_paint_bounds();
	}	
	
	
	private void update_stoppable_timer()
	{
		if(this.snooze_timer > 0){this.snooze_timer--;}
	}

	private boolean has_deadly_ground_contact()
	{	
		return this.bounds.getMaxY() > GROUND_Y
			   && this.model != BARRIER 
			   && !this.is_destroyed
			   && currentRock != this;
	}

	private void calculate_boss_maneuver(ArrayList<LinkedList<Enemy>> enemy)
	{
		     if(this.type == BOSS_4)    {this.boss_4_action(enemy);}
		else if(this.type == FINAL_BOSS){this.final_boss_action();}		
		else if(this.shield_maker_timer != DISABLED){this.shield_maker_action();}     
		else if(this.type == BODYGUARD) {this.bodyguard_action();}
		else if(this.type == HEALER 
				&& this.dodge_timer == READY){this.healer_action();}
	}

	private void check_for_barrier_collision()
	{
		this.is_previous_stopping_barrier = this.stopping_barrier;
		if(this.stopping_barrier == null || !this.stopping_barrier.bounds.intersects(this.bounds))
		{
			this.stopping_barrier = null;
			for(int i = 0; i < current_nr_of_barriers; i++)
			{
				if(	   living_barrier[i] != this
					&& living_barrier[i].bounds.intersects(this.bounds))
					
				{
					this.stopping_barrier = living_barrier[i];
					break;
				}
			}
		}
	}

	private void try_to_turn_at_barrier()
	{		
		this.turn_timer = MIN_TURN_TIME;
		if(this.is_on_screen() 
		   && this.stopping_barrier.is_on_screen()
		   && this.stopping_barrier != this.is_previous_stopping_barrier
		   && this.turn_audio_timer == READY)
		{
			Audio.play(Audio.landing);
			this.turn_audio_timer = MIN_TURN_NOISELESS_TIME;
		}
				
		if(this.has_lateral_face_touch_with(this.stopping_barrier))
		{
			if(	tournaround_is_turn_away(this.direction.x, 
			   							 this.bounds.getCenterX(), 
			   							 this.stopping_barrier.bounds.getCenterX())
			   	// Gegner sollen nicht an Barriers abdrehen, bevor sie im Bild waren.					
				&& this.is_on_screen()) 
			{							
				this.perform_x_turn_at_barrier();
			}
		}
		else
		{
			if(tournaround_is_turn_away(this.direction.y, 
										this.bounds.getCenterY(), 
										this.stopping_barrier.bounds.getCenterY()))
			{	
				this.direction.y = -this.direction.y;				
			}
		}		
	}
		
	boolean has_lateral_face_touch_with(Enemy barrier)
	{
		return  
			MyMath.get_intersection_length(	this.bounds.getMinX(),
											this.bounds.getMaxX(),
											barrier.bounds.getMinX(),
											barrier.bounds.getMaxX())										 
			<										 									 
			MyMath.get_intersection_length(	this.bounds.getMinY(),
											this.bounds.getMaxY(),
											barrier.bounds.getMinY(),
											barrier.bounds.getMaxY());	
	}

	private void perform_x_turn_at_barrier()
	{
		this.direction.x = -this.direction.x;
		if(this.call_back <= (this.is_mini_boss ? 2 : 0))
		{
			this.call_back++;
		}		
	}

	private void update_timer()
	{		
		if(	this.collision_damage_timer > 0) {this.collision_damage_timer--;}	
		if(	this.collision_audio_timer  > 0) {this.collision_audio_timer--;}	
		if(	this.turn_audio_timer  	    > 0) {this.turn_audio_timer--;}		
		if(	this.emp_slowed_timer		> 0) {this.emp_slowed_timer--;}
		if(	this.static_charge_timer	> 0) {this.static_charge_timer--;}
		if(	this.invincible_timer	    > 0) {this.invincible_timer--;}
		if(	this.chaos_timer			> 0) {this.chaos_timer--;}
		if(	this.non_stunable_timer 	> 0) {this.non_stunable_timer--;}		
		if( this.turn_timer	  		    > 0) {this.turn_timer--;}
		if( this.stunning_timer		    > 0) {this.stunning_timer--;}
	}
	


	private void calculate_flight_maneuver(Controller controller,
										   Helicopter helicopter)
	{				
		// Beschleunigung
		if(    this.speedup != DISABLED 
			|| this.can_frontal_speedup)
		{
			evaluate_speedup(helicopter);
		}	
				
		// Schubweises Fliegen
		if(this.batch_wise_move != 0){evaluate_batch_wise_move();}
					
		// Chaosflug
		if(    this.can_move_chaotic
			&& this.chaos_timer == READY
			&& this.dodge_timer == READY)
		{
			if( MyMath.toss_up(0.2f)
			    && this.is_shield_maker())
			{
				this.direction.x = -this.direction.x;
			}
			if( MyMath.toss_up(0.2f))
			{
				this.direction.y = -this.direction.y;
			}			
			this.chaos_timer = 5;
		}
		
		// Early x-Turn
		if( this.can_early_turn 
			&& this.bounds.getMinX() < 0.85 * Main.VIRTUAL_DIMENSION.width)
		{
			this.can_early_turn = false;
			this.direction.x = 1;
		}
							
		// Frontal-Angriff
		if( this.can_learn_kamikaze		
				
			&& ((this.direction.x == 1 
					&& helicopter.bounds.getMaxX() < this.bounds.getMinX() 
					&& this.bounds.getX() - helicopter.bounds.getX() < 620)
				||
				(this.direction.x == -1 
					&& this.bounds.getMaxX() < helicopter.bounds.getMinX() 
					&& helicopter.bounds.getX() - this.bounds.getX() < 620)))
		{
			this.start_kamikaze_mode();				
			this.direction.x = -1;
		}			
		if(	this.can_kamikaze && !(this.teleport_timer > 0)){this.kamikaze(helicopter);}
			
		// Vergraben			
		if(this.borrow_timer != DISABLED && !(this.snooze_timer > 0))
		{				
			evaluateBorrowProcedure(helicopter);
		}
								
		// Shooting
		if(this.shoot_timer != DISABLED){evaluateShooting(controller, helicopter);}
		if(this.barrier_shoot_timer != DISABLED)
		{
			evaluateBarrierShooting(controller, helicopter);
		}									
		
		// Snooze bei Hindernissen									
		if(this.snooze_timer == PRE_READY){this.end_snooze();}		
		
		// Barrier-Teleport			
		if(this.barrier_teleport_timer != DISABLED 
			&& !(this.snooze_timer > 0))
		{				
			evaluate_barrier_teleport(helicopter);
		}				
				
		// Sinus- und Loop-Flug
		if(this.can_sinus_move || this.can_loop){this.sinusloop();}
		
		// tarnen
		if(this.cloaking_timer > 0 
			&& (!this.is_emp_slowed() || this.can_learn_kamikaze))
		{
			this.cloaking();
		}
		
		// Tractor					
		if(is_tractor_ready(helicopter)){start_tractor(helicopter);}
				
		//Chaos-SpeedUp
		if(	this.can_chaos_speedup 
			&& this.speed_level.getX() == this.target_speed_level.getX() 
			&& helicopter.bounds.getX() - this.bounds.getX() > -350	)
		{				
			this.speed_level.setLocation(6 + this.target_speed_level.getX(), //d
										 this.speed_level.getY());
		}
		if(this.can_chaos_speedup 
		   && (helicopter.bounds.getX() - this.bounds.getX()) > -160)
		{			
			this.can_move_chaotic = true;
			this.speed_level.setLocation(this.speed_level.getX(),
										 9 + 4.5*Math.random()); //d
		}
				
		// Ausweichen
		if(this.dodge_timer > 0){evaluate_dodge();}	
		
		// Beamen
		if(this.teleport_timer > 0)
		{	
			this.teleport_timer--;
			if(	this.teleport_timer == READY)
			{
				this.speed_level.setLocation(this.target_speed_level);				
			}				
		}		
	}
	
	private void end_snooze()
	{
		if(	this.borrow_timer == DISABLED)
		{
			this.speed_level.setLocation(this.target_speed_level);
		}
		else{this.end_interrupted_borrow_procedure();}
		
		if(	this.barrier_teleport_timer != DISABLED)
		{
			this.cloaking_timer = 1;
			this.barrier_teleport_timer = CLOAKING_TIME;
		}		
	}

	private void end_interrupted_borrow_procedure()
	{
		if(this.borrow_timer > BORROW_TIME + this.shooting_rate * this.shots_per_cycle)
		{
			this.borrow_timer = 2 * BORROW_TIME + this.shooting_rate * this.shots_per_cycle - this.borrow_timer;
		}
		else if(this.borrow_timer > BORROW_TIME)
		{
			this.borrow_timer = BORROW_TIME;
		}
		this.direction.y = 1;
		this.speed_level.setLocation(0, 1);		
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
			this.speed_level.setLocation(this.speed_level.getX(), 0);
		}
		else
		{
			this.speed_level.setLocation(this.speed_level.getX(), speed);
		}		
	}
	*/

	private void validate_turns()
	{
		if(	this.stopping_barrier != null
			&& this.borrow_timer == DISABLED)
		{
			this.try_to_turn_at_barrier();
		}
		else if(this.has_to_turn_at_x_border()){this.change_x_direction();}		
		else if(this.has_to_turn_at_y_border()){this.change_y_direction();}	
	}
	
	private void check_for_EMP_strike(Controller controller,
									  Helicopter helicopter)
	{
		if(helicopter.emp_wave != null)
		{
			if(this.is_emp_shockable(helicopter))
			{
				this.emp_shock(controller, helicopter);
			}
		}
		else{this.is_emp_shocked = false;}
	}

	private void emp_shock(Controller controller, Helicopter helicopter)
    {
    	this.take_dmg((int)( (this.isMajorBoss()
    							? EMP_DAMAGE_FACTOR_BOSS
    							: EMP_DAMAGE_FACTOR_ORDINARY) 
    						 * MyMath.dmg(helicopter.levelOfUpgrade[ENERGY_ABILITY])));
		this.is_emp_shocked = true;		
		if(this.type == BOSS_4){this.spawning_hornet_timer = READY;}
		this.disable_site_effects(helicopter);
				
		if(this.has_HPs_left())
		{			
			Audio.play(Audio.stun);
			if(this.model == BARRIER){this.snooze(true);}
			else if(this.teleport_timer == READY ){this.teleport();}
			else if(this.is_stunnable && !this.is_shielding)
			{
				this.emp_slowed_timer = this.is_main_boss() 
											? EMP_SLOW_TIME_BOSS 
											: EMP_SLOW_TIME;
			}
			this.react_to_hit(helicopter, null);			
			
			Explosion.start(controller.explosion, helicopter,
							this.bounds.getCenterX(), 
							this.bounds.getCenterY(), 2, false);
		}
		else
		{
			Audio.play(Audio.explosion2);
			helicopter.emp_wave.kills++;			
			helicopter.emp_wave.earned_money += this.calculate_reward(helicopter);
			this.die(controller, helicopter, null, false);
		}
    }
	
	private boolean has_to_turn_at_x_border()
	{
		return this.barrier_teleport_timer == DISABLED
			   //&& this.stopping_barrier == null
			   && this.turn_timer == READY
			   &&(	(this.direction.x == -1 
			   			&&(((this.call_back > 0 || this.isMajorBoss()) && this.bounds.getMinX() < TURN_FRAME.getMinX())
			   					|| (this.type == HEALER && this.bounds.getX() < 563)))
			   		||
			   		(this.direction.x == 1 
			   			&&(((this.call_back > 0 || this.isMajorBoss()) && this.bounds.getMaxX() > TURN_FRAME.getMaxX() && !this.can_learn_kamikaze)
			   					|| (this.type == BODYGUARD && (this.bounds.getX() + this.bounds.getWidth() > 660)))));
	}

	private void change_x_direction()
	{
		this.direction.x = -this.direction.x;
		//this.turn_timer = MIN_TURN_TIME;
		if(this.call_back > 0){this.call_back--;}
	}
	
	private boolean has_to_turn_at_y_border()
	{		
		return 	this.borrow_timer == DISABLED
				&&( (this.bounds.getMinY() <= (this.model == BARRIER ? 0 : TURN_FRAME.getMinY()) 
			   	  	 && this.direction.y < 0) 
			        ||
			        (this.bounds.getMaxY() >= (this.model == BARRIER ? GROUND_Y : TURN_FRAME.getMaxY()) 
			   	     &&  this.direction.y > 0 
			   	     && !this.is_destroyed) );
	}

	private void change_y_direction()
	{
		this.direction.y = -this.direction.y;
		if(this.can_sinus_move){this.speed_level.setLocation(this.speed_level.getX(), 1);} //d
		if(this.model == BARRIER)
		{
			if(this.direction.y == -1){Audio.play(Audio.landing);}
			this.snooze(false);
		}		
	}

	private boolean isToBeRemoved()
	{		
		return this.type != BOSS_2_SERVANT
			   && this.barrier_teleport_timer == DISABLED
			   && !this.is_dodging()
			   && (this.call_back == 0 || !this.speed.equals(ZERO_SPEED))
			   && (    (this.bounds.getMinX() > Main.VIRTUAL_DIMENSION.width + DISAPPEARANCE_DISTANCE
					   	 && this.direction.x ==  1)
				    || (this.bounds.getMaxX() < -DISAPPEARANCE_DISTANCE));
	}
	
	private boolean is_dodging()
	{		
		return this.dodge_timer > 0;
	}

	public boolean is_on_screen()
	{		
		return this.bounds.getMaxX() > 0 
			   && this.bounds.getMinX() < Main.VIRTUAL_DIMENSION.width;
	}
	
	private void prepare_removal()
	{
		this.is_marked_for_removal = true;				
		if(currentRock == this)
		{
			currentRock = null;
			rock_timer = ROCKFREE_TIME;
		}
		else if(this.is_mini_boss)
		{
			current_mini_boss = null;
		}		
	}	
	
	private boolean is_emp_shockable(Helicopter helicopter)
	{
		return     !this.is_emp_shocked
				&& !this.is_destroyed 
				&& !this.is_invincible()
				&& !(this.barrier_teleport_timer != DISABLED && this.barrier_shoot_timer == DISABLED)
				&& helicopter.emp_wave.ellipse.intersects(this.bounds);
	}	

	private void evaluate_speedup(Helicopter helicopter)
	{
		if(  this.speed_level.getX() < (this.speedup == DISABLED ? 12 : 19)  //d
			 && (this.speedup  > 0 || this.can_frontal_speedup) )
		{
			this.speed_level.setLocation(this.speed_level.getX()+0.5, //d
					 					 this.speed_level.getY());
		}			
		if(	this.speedup == 0 && this.at_eye_level(helicopter))
		{
			this.speedup = 1;
			this.can_sinus_move = true;
		}
		else if(this.speedup == 1 && !this.at_eye_level(helicopter))
		{
			this.speedup = 2;
		}
		else if(this.speedup == 2 && this.at_eye_level(helicopter))
		{
			this.speedup = 3;
			this.can_sinus_move = false;
			this.speed_level.setLocation(this.speed_level.getX(), 1.5); //d
			if(this.bounds.getY() < helicopter.bounds.getY()){this.direction.y = 1;}
			else{this.direction.y = -1;}	
		}		
	}
	
	private boolean at_eye_level(Helicopter helicopter)
	{
		return this.bounds.intersects(Integer.MIN_VALUE/2, 
									  helicopter.bounds.getY(),
									  Integer.MAX_VALUE,
									  helicopter.bounds.getHeight());
	}
	
	private void final_boss_action()
    {
		if(this.speed_level.getX() > 0)
		{
			if(this.speed_level.getX() - 0.5 <= 0) //d
			{
				this.speed_level.setLocation(ZERO_SPEED);
				boss.setLocation(this.bounds.getCenterX(), 
						 		 this.bounds.getCenterY());
				make_all_boss5_servants = true;
			}
			else
			{
				this.speed_level.setLocation(this.speed_level.getX()-0.5,	0); //d
			}
		}
		else for(int serant_type = 0; serant_type < NR_OF_BOSS_5_SERVANTS; serant_type++)
		{
			if(this.operator.servants[serant_type] == null)
			{	
				if(MyMath.toss_up(RETURN_PROB[serant_type]) 
					&& this.operator.time_since_death[serant_type] > MIN_ABSENT_TIME[serant_type])
				{
					make_boss5_servant[serant_type] = true;
				}
				else{this.operator.time_since_death[serant_type]++;}	
			}
		}		
	}
    
	private static int id(int type)
	{		
		return -type-8;
	}	
		
	private void evaluate_batch_wise_move()
	{
		if(this.batch_wise_move == 1)
		{
			this.speed_level.setLocation(this.speed_level.getX()+0.5, //d
					 					 this.speed_level.getY());
		}
		else if(this.batch_wise_move == -1)
		{
			this.speed_level.setLocation(this.speed_level.getX()-0.5, //d
					 					 this.speed_level.getY());
		}
		if(this.speed_level.getX() <= 0){this.batch_wise_move = 1;}
		if(this.speed_level.getX() >= this.target_speed_level.getX()){this.batch_wise_move = -1;}		
	}
	
	private void bodyguard_action()
	{
		if(Events.boss.shield < 1)
		{
			this.can_kamikaze = true;
			this.speed_level.setLocation(7.5, this.speed_level.getY());//d
		}
		else
		{
			this.can_kamikaze = false;
			this.speed_level.setLocation(this.target_speed_level);					
		}		
	}
	
	private void start_kamikaze_mode()
	{
		this.can_kamikaze = true;
		this.can_frontal_speedup = true;
	}
	
	private void kamikaze(Helicopter helicopter)
    {
    	// Boss-Gegner mit der Fähigkeit "Kamikaze" drehen mit einer bestimmten
    	// Wahrscheinlichkeit um, wenn sie dem Helikopter das Heck zugekehrt haben.
    	if(	this.is_boss() 
    		&& MyMath.toss_up(0.008f) 
    		&& ( (this.bounds.getMinX() > helicopter.bounds.getMaxX() 
    			   && this.direction.x == 1) 
    			 ||
    			 (helicopter.bounds.getMinX() > this.bounds.getMaxX()
    			   && this.direction.x == -1)))		
    	{
			this.direction.x = -this.direction.x;	
			this.speed_level.setLocation(0, this.speed_level.getY());
		}		
		
    	if(((this.bounds.getMaxX() > helicopter.bounds.getMinX() && this.direction.x == -1)&&
			(this.bounds.getMaxX() - helicopter.bounds.getMinX() ) < 620) ||
		   ((helicopter.bounds.getMaxX() > this.bounds.getMinX() && this.direction.x == 1)&&
			(helicopter.bounds.getMaxX() - this.bounds.getMinX() < 620)))		    
		{			
			if(!this.can_learn_kamikaze)
			{
				this.speed_level.setLocation((this.type == BOSS_4 || this.type == BOSS_3) ? 12 : 8, //d
											 this.speed_level.getY());
			}						
			if(this.direction.y == 1 
				&& helicopter.bounds.getY()  < this.bounds.getY())				
			{							
				this.direction.y = -1;				
				this.speed_level.setLocation(this.speed_level.getX(), 0);
			}
			else if(this.direction.y == -1 
					&& helicopter.bounds.getMaxY() > this.bounds.getMaxY())
			{
				this.direction.y = 1;
				this.speed_level.setLocation(this.speed_level.getX(), 0);
			}
			
			if(this.speed_level.getY() < 8) //d
			{
				this.speed_level.setLocation(this.speed_level.getX(), 
											 this.speed_level.getY()+0.5); //d
			}
		}
		else if(!this.can_frontal_speedup && this.dodge_timer == READY)
		{			
			if(this.type == BODYGUARD && Events.boss.shield < 1)
			{
				this.speed_level.setLocation(7.5, this.target_speed_level.getY());	
			}
			else
			{
				this.speed_level.setLocation(this.target_speed_level);
			}
		}
    }
	
	private void evaluateBorrowProcedure(Helicopter helicopter)
	{		
		if(this.borrow_timer > 0){this.borrow_timer--;}
		if(this.borrow_timer == BORROW_TIME + this.shooting_rate * this.shots_per_cycle)
		{					
			this.barrier_shoot_timer = this.shooting_rate * this.shots_per_cycle;
			this.speed_level.setLocation(ZERO_SPEED);
		}
		else if(this.borrow_timer == BORROW_TIME)
		{
			this.barrier_shoot_timer = DISABLED;
			this.speed_level.setLocation(0, 1); //d
			this.direction.y = 1;
		}
		else if(this.borrow_timer == 1)
		{
			this.speed_level.setLocation(ZERO_SPEED);
		}
		else if(this.borrow_timer == READY 
				&&( (this.type != PROTECTOR 
				     && MyMath.toss_up(0.004f))
				    || 
				    (this.type == PROTECTOR 
				     && (helicopter.bounds.getX() > boss.getX() - 225) ))) 
		{			
			this.borrow_timer = 2 * BORROW_TIME 
								+ this.shooting_rate * this.shots_per_cycle 
								+ (this.bounds.getY() == GROUND_Y 
									? PROTECTOR_WIDTH/8
									: 0)
								- 1;
			this.speed_level.setLocation(0, 1);  //d
			this.direction.y = -1;
		}	
	}
	
	private void evaluateShooting(Controller controller, Helicopter helicopter)
	{
		if(	this.shoot_timer == 0 
			&& !this.is_emp_slowed()	
			&& MyMath.toss_up(0.1f)
			&& this.bounds.getX() + this.bounds.getWidth() > 0
			&& !(this.cloaking_timer > CLOAKING_TIME && this.cloaking_timer <= CLOAKING_TIME + CLOAKED_TIME) 
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
			this.shoot(	controller.enemyMissile,
						this.has_deadly_shots() ? BUSTER : DISCHARGER,
						this.shot_speed + 3*Math.random()+5);
			
			this.shoot_timer = this.shooting_rate;
		}
		if(this.shoot_timer > 0){this.shoot_timer--;}
	}
	
	private boolean has_deadly_shots()
	{		
		return this.type == BOSS_3 
				|| this.is_mini_boss
				|| (this.type == BIG_SHIELD_MAKER && MyMath.toss_up());
	}

	private void evaluateBarrierShooting(Controller controller,
										 Helicopter helicopter)
	{
		if(this.barrier_shoot_timer == 0)
		{
			this.barrier_shoot_timer = this.shooting_cycle_length; 
			if(	this.shot_rotation_speed == 0   
				&&	  (helicopter.bounds.getX()    < this.bounds.getX()         && this.shooting_direction.getX() > 0)
					||(helicopter.bounds.getMaxX() > this.bounds.getMaxX() && this.shooting_direction.getX() < 0) )
			{
				this.shooting_direction.setLocation(-this.shooting_direction.getX(), this.shooting_direction.getY());
			}
		}		
		if( this.barrier_shoot_timer <= this.shots_per_cycle * this.shooting_rate 
			&& this.bounds.getX() + this.bounds.getWidth() > 0
			&& this.barrier_shoot_timer%this.shooting_rate == 0)
		{					
			if(this.shot_rotation_speed != 0)
			{
				float temp_value = 0.0005f * this.shot_rotation_speed * this.lifetime;
				this.shooting_direction.setLocation(
						Math.sin(temp_value), 
						Math.cos(temp_value) );
			}
			if(this.borrow_timer != DISABLED || this.barrier_teleport_timer != DISABLED)
			{
				// Schussrichtung wird auf Helicopter ausgerichtet
				this.shooting_direction.setLocation(
						( (helicopter.bounds.getX() + (helicopter.is_moving_left ? Helicopter.FOCAL_PNT_X_LEFT : Helicopter.FOCAL_PNT_X_RIGHT)) 
							  - (this.bounds.getX() +       this.bounds.getWidth()/2)), 
						  (helicopter.bounds.getY() + Helicopter.FOCAL_PNT_Y_EXP) 
						  	  - (this.bounds.getY() +       this.bounds.getHeight()/2)) ;
				float distance = (float) MyMath.ZERO_POINT.distance(this.shooting_direction);
				this.shooting_direction.setLocation(this.shooting_direction.getX()/distance,
													this.shooting_direction.getY()/distance);
			}
			this.shoot(controller.enemyMissile, this.shotType, this.shot_speed);
		}				
		this.barrier_shoot_timer--;
	}
	
	public void shoot(ArrayList<LinkedList<EnemyMissile>> enemyMissiles, EnemyMissileTypes missileType, double missileSpeed)
    {
    	Iterator<EnemyMissile> i = enemyMissiles.get(INACTIVE).iterator();
		EnemyMissile em;
		if(i.hasNext()){em = i.next(); i.remove();}
		else{em = new EnemyMissile();}
		enemyMissiles.get(ACTIVE).add(em);
		em.launch(this, missileType, missileSpeed, this.shooting_direction);
		Audio.play(Audio.launch3);
    }
	
	private void evaluate_barrier_teleport(Helicopter helicopter)
	{
		if(this.barrier_teleport_timer == CLOAKING_TIME + this.shooting_rate * this.shots_per_cycle)
		{					
			this.barrier_shoot_timer = this.shooting_rate * this.shots_per_cycle;
			this.uncloak(DISABLED);
		}
		else if(this.barrier_teleport_timer == CLOAKING_TIME)
		{
			this.barrier_shoot_timer = DISABLED;	
			this.cloaking_timer = ACTIVE;
			if(this.bounds.getMaxX() > 0){Audio.play(Audio.cloak);}
		}	
		else if(this.barrier_teleport_timer == READY && MyMath.toss_up(0.004f)) 
		{
			this.start_barrier_uncloaking(helicopter);
		}
		
		if(this.barrier_teleport_timer != READY)
		{
			this.barrier_teleport_timer--;
			if(this.barrier_teleport_timer == READY)
			{				
				if(this.call_back > 0)
				{					
					this.place_at_pause_position();
				}
				else{this.is_marked_for_removal = true;}
			}
		}		
	}
	
	private void start_barrier_uncloaking(Helicopter helicopter)
	{
		this.barrier_teleport_timer = 2 * CLOAKING_TIME + this.shooting_rate * this.shots_per_cycle;	
		this.cloaking_timer = CLOAKED_TIME + CLOAKING_TIME;			
		this.place_near_helicopter(helicopter);		
	}

	private void boss_4_action(ArrayList<LinkedList<Enemy>> enemy)
    {
    	if(    this.bounds.getX() < 930 
    		&& this.bounds.getX() > 150)
    	{
    		this.spawning_hornet_timer++;
    	}
		if(this.spawning_hornet_timer == 1)
		{
			this.speed_level.setLocation(11, 11); //d
			this.can_move_chaotic = true;
			this.can_kamikaze = true;
		}
		else if(this.spawning_hornet_timer >= 50)
		{
			if(this.spawning_hornet_timer == 50)
			{
				this.speed_level.setLocation(3, 3); //d
				this.can_move_chaotic = false;
				this.can_kamikaze = false;
			}
			else if(this.spawning_hornet_timer == 90)
			{
				this.speed_level.setLocation(ZERO_SPEED);						
			}						
			if(enemy.get(ACTIVE).size() < 15
			   && (    this.spawning_hornet_timer == 60
			   	    || this.spawning_hornet_timer == 90
			   	    || MyMath.toss_up(0.02f)))
			{
				boss.setLocation(	this.bounds.getX() + this.bounds.getWidth() /2, 
									this.bounds.getY() + this.bounds.getHeight()/2);
				make_boss4_servant = true;                              
			}
		}
    }
	
	private void sinusloop()
    {
		this.speed_level.setLocation(
				this.speed_level.getX(), 
				Math.max(4.0, 0.15f*(145-Math.abs(this.bounds.getY()-155))));   //d
		
    	if(this.can_loop)
    	{
    		if(this.direction.x == -1 && this.bounds.getY()-155>0)
    		{
    			this.direction.x = 1;
    			this.speed_level.setLocation(11, this.speed_level.getY()); //d
    		}
    		else if(this.direction.x == 1 && this.bounds.getY()-155<0)
    		{
    			this.direction.x = -1;
    			this.speed_level.setLocation(7.5, this.speed_level.getY()); //d
    		}
    	}
    }	
	
	private void cloaking()
    {
    	if(!this.can_learn_kamikaze || this.can_frontal_speedup){this.cloaking_timer += this.uncloaking_speed;}
    	if(this.cloaking_timer <= CLOAKING_TIME)
		{						 
			this.alpha = 255 - 255*this.cloaking_timer/CLOAKING_TIME;
    	}
		else if(this.cloaking_timer > CLOAKING_TIME + CLOAKED_TIME && this.cloaking_timer <= CLOAKED_TIME + 2 * CLOAKING_TIME)
		{
			if(this.cloaking_timer == CLOAKING_TIME + CLOAKED_TIME + this.uncloaking_speed){Audio.play(Audio.cloak);}
			this.alpha = 255*(this.cloaking_timer - CLOAKED_TIME - CLOAKING_TIME)/CLOAKING_TIME;			
			if(this.cloaking_timer >= CLOAKED_TIME + 2 * CLOAKING_TIME)
			{
				if(this.can_learn_kamikaze){this.uncloak(DISABLED);}
				else{this.uncloak(READY);}
			}
		}
		else {this.alpha = 255;}
    }
	
	private boolean is_tractor_ready(Helicopter helicopter)
	{		
		return 
			this.tractor == READY
			&& helicopter.interphaseGeneratorTimer <= helicopter.shift_time
			&& !this.is_emp_slowed() 
			&& this.cloaking_timer < ACTIVE 
			&& helicopter.tractor == null
			&& helicopter.bounds.getX() - this.bounds.getX() > -750
			&& helicopter.bounds.getX() - this.bounds.getX() < -50
			&& this.bounds.getMaxX() < 982
			&&(helicopter.bounds.getY()+56 > this.bounds.getY() + 0.2 * this.bounds.getHeight()
			&& helicopter.bounds.getY()+60 < this.bounds.getY() + 0.8 * this.bounds.getHeight());
	}
	
	private void start_tractor(Helicopter helicopter)
	{
		Audio.loop(Audio.tractor_beam);
		this.tractor = ACTIVE;
		this.speed_level.setLocation(ZERO_SPEED);
		helicopter.tractor = this;
		this.direction.x = -1;		
	}
	
	public void stop_tractor()
	{
		this.tractor = DISABLED;
		this.speed_level.setLocation(this.target_speed_level);
	}
	
	private void shield_maker_action()
	{			   
		this.shield_maker_timer++;									
		if(this.shield_maker_timer > 100)
		{
			if(this.shield_maker_timer == 101){this.calm_down();}						
			this.correct_shield_maker_direction();				
			if(this.can_start_shielding()){this.start_shielding();}
		}		
	}
	
	private void calm_down()
	{
		this.speed_level.setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);	 //d	
		this.target_speed_level.setLocation(SHIELD_MAKER_CALM_DOWN_SPEED);
		this.can_move_chaotic = false;		
	}

	private void start_shielding()
	{
		Audio.play(Audio.shield_up);
		this.speed_level.setLocation(ZERO_SPEED);	
		this.direction.x = -1;
		this.is_shielding = true;
		Events.boss.shield++;
		this.can_dodge = true;
		this.shield_maker_timer = DISABLED;
	}

	private void correct_shield_maker_direction()
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
		
		if(		 this.is_upper_shield_maker
				 && this.bounds.getMaxY() 
				 	  < Events.boss.bounds.getMinY() 
				 	    - SHIELD_TARGET_DISTANCE
				 	    - TARGET_DISTANCE_VARIANCE.y
			     ||
			     !this.is_upper_shield_maker
			     && this.bounds.getMinY() 
			     	  < Events.boss.bounds.getMaxY() 
			     	  	+ SHIELD_TARGET_DISTANCE
			     	  	- TARGET_DISTANCE_VARIANCE.y)
		{
			this.direction.y = 1;
		}   
		else if( this.is_upper_shield_maker
				 && this.bounds.getMaxY() > Events.boss.bounds.getMinY() - SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y
				 ||
				 !this.is_upper_shield_maker
				 && this.bounds.getMinY() > Events.boss.bounds.getMaxY() + SHIELD_TARGET_DISTANCE + TARGET_DISTANCE_VARIANCE.y)
		{
			this.direction.y = -1;
		}		
	}

	private boolean can_start_shielding()
	{		
		return 	   this.shield_maker_timer > 200
				&& !this.is_recovering_speed
				&& TARGET_DISTANCE_VARIANCE.x	
				     > Math.abs(Events.boss.bounds.getCenterX() 
						        -this.bounds.getX()) 				
			    &&  TARGET_DISTANCE_VARIANCE.y
					  > (this.is_upper_shield_maker 
						 ? Math.abs(this.bounds.getMaxY() 
								    - Events.boss.bounds.getMinY() 
								    + SHIELD_TARGET_DISTANCE) 
						 : Math.abs(this.bounds.getMinY()
								    - Events.boss.bounds.getMaxY()
								    - SHIELD_TARGET_DISTANCE));
	}

	private void healer_action()
    {    	
		if(Events.boss.hitpoints < Events.boss.starting_hitpoints)
		{						
			if(this.speed_level.getX() != 0)
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
					this.speed_level.setLocation(ZERO_SPEED);					
					this.direction.x = -1;
					this.can_dodge = true;
				}				
			}
			else
			{
				Events.boss.hitpoints 
					= Math.min(Events.boss.hitpoints + BOSS_5_HEAL_RATE, 
							   Events.boss.starting_hitpoints);
			}						 
		}
		else
		{
			this.speed_level.setLocation(this.target_speed_level);
		}
    }
		
	private void evaluate_dodge()
	{
		this.dodge_timer--;			
		if(this.dodge_timer == READY)
		{				
			this.speed_level.setLocation(this.target_speed_level);
			this.direction.x = -1;												   
		}		
	}

	private void snooze(boolean inactivation)
	{
		this.snooze_timer 
			= Math.max(	this.snooze_timer, 
						SNOOZE_TIME 
						+ (inactivation
							? INACTIVATION_TIME
							  + MyMath.random((int)(EXTRA_INACTIVE_TIME_FACTOR * INACTIVATION_TIME))
							:0));
		this.speed_level.setLocation(ZERO_SPEED);
		if(this.target_speed_level.getY() != 0 
		   && this.bounds.getMaxY() + 1.5 * this.speed.getY() > GROUND_Y)
		{
			this.set_y(GROUND_Y - this.bounds.getHeight());
		}		
		if(this.borrow_timer != DISABLED)
		{						
			this.barrier_shoot_timer = DISABLED;
		}		
		else if(this.cloaking_timer != DISABLED)
		{
			this.barrier_teleport_timer = DISABLED;
			this.barrier_shoot_timer = DISABLED;			
		}
		else if(this.barrier_shoot_timer != DISABLED)
		{
			this.barrier_shoot_timer = this.snooze_timer - SNOOZE_TIME + this.shooting_cycle_length;
		}
	}
	
	private void move()
	{			
		if(!this.speed.equals(ZERO_SPEED)|| BackgroundObject.background_moves)
		{
			this.set_location(	
					this.bounds.getX() 
						+ this.direction.x * this.speed.getX() 
						- (BackgroundObject.background_moves ? BG_SPEED : 0),
					Math.max( this.model == BARRIER ? 0 : Integer.MIN_VALUE,
							currentRock == this ? this.bounds.getY() :
								Math.min( this.can_be_positioned_below_ground()
											? Integer.MAX_VALUE
											: GROUND_Y - this.bounds.getHeight(),
										this.bounds.getY() 
											+ this.direction.y 
											* this.speed.getY())));
		}
	}

	private boolean can_be_positioned_below_ground()
	{		
		return !(this.model == BARRIER
			     && this.borrow_timer == DISABLED)
			   || this.is_destroyed
			   || currentRock == this;
	}

	private void calculate_speed_dead()
	{		
		if(this.exploding_timer <= 0)
		{
			this.speed.setLocation(this.speed_level); //d
		}
		else
		{
			this.exploding_timer--;			
			this.speed.setLocation(this.speed_level.getX(), 0);
			if(this.exploding_timer == 0){this.exploding_timer = DISABLED;}
		}
	}
	
	private void calculate_speed(Helicopter helicopter)
	{		
		if(this.stunning_timer != READY)
		{
			this.adjust_speed_to(helicopter.missileDrive);
			if(this.stunning_timer == 1)
			{
				if(this.model == BARRIER){this.snooze(true);}
				else{this.is_recovering_speed = true;}				
			}
		}
		if(this.model != BARRIER){this.evaluate_speed_boost();}		
		if(this.is_recovering_speed){this.recover_speed();}		
		
		this.speed.setLocation(this.speed_level);			//d
		
		if(this.is_emp_slowed())
		{
			// relevant, wenn mit der PEGASUS-Klasse gespielt wird
			this.speed.setLocation(	
				this.speed.getX()
					*((EMP_SLOW_TIME-this.emp_slowed_timer)/EMP_SLOW_TIME),
				this.speed.getY()
					*((EMP_SLOW_TIME-this.emp_slowed_timer)/EMP_SLOW_TIME));
		}
				
		if(	this.stopping_barrier != null 
			&& this.borrow_timer == DISABLED
			&& !(this.model == BARRIER && this.type == 1))
		{
			this.adjust_speed_to_barrier(helicopter);
		}
	}
	
	private boolean is_emp_slowed()
	{		
		return this.emp_slowed_timer > 0;
	}

	private void adjust_speed_to(int missile_drive)
	{
		if( !this.speed_level.equals(ZERO_SPEED)
			&& 
			( this.total_stunning_time - 13 == this.stunning_timer 
			  || this.bounds.getMaxX() 
			  	 + 18 
			  	 + missile_drive/2 > Main.VIRTUAL_DIMENSION.width 
			  	 								+ 2 * this.bounds.getWidth()/3  
			  || this.bounds.getMinX() 
			  	 - 18 
			  	 - missile_drive/2 < - 2 * this.bounds.getWidth()/3))
		{
			this.speed_level.setLocation(ZERO_SPEED);
		}
	}	
	
	private void evaluate_speed_boost()
	{		
		int bottom_turn_line = this.is_kaboom 
								  ? KABOOM_Y_TURN_LINE 
								  : (int)TURN_FRAME.getMaxY();
									
		if(this.is_speed_boosted)
		{
			if(    this.bounds.getMinY() > TURN_FRAME.getMinY()
			    && this.bounds.getMaxY() < bottom_turn_line)
			{
				this.speed_level.setLocation(this.target_speed_level);
				this.is_speed_boosted = false;
			}						
		}
		else if(this.stopping_barrier != null
				&&(     this.bounds.getMinY() < TURN_FRAME.getMinY() 
				    || (this.bounds.getMaxY() > bottom_turn_line)))
		{
			this.is_speed_boosted = true; 
			this.speed_level.setLocation(Math.max(
											this.speed_level.getX(),
											this.target_speed_level.getX() //d
												+ 7.5), 
										 Math.max(this.speed_level.getY(), 5.5)); //d
			
			// Wenn Gegner droht am Boden durch Barrier zerdrückt zu werden, dann nimmt Gegner den kürzesten Weg.
			if(this.must_avoid_ground_collision(bottom_turn_line))
			{
				this.perform_x_turn_at_barrier();
			}
		}
	}	

	private boolean must_avoid_ground_collision(int y_turn_line)
	{		
		return this.bounds.getMaxY() > y_turn_line
				   &&(   (this.direction.getX() ==  1 
				   			&& this.bounds.getCenterX() < this.stopping_barrier.bounds.getCenterX())
					   ||(this.direction.getX() == -1 
					   		&& this.bounds.getCenterX() > this.stopping_barrier.bounds.getCenterX()));
	}
	
	private void recover_speed()
	{
		if(	this.borrow_timer != DISABLED || this.has_reached_target_speed())
		{
			this.is_recovering_speed = false;
			if(this.borrow_timer != DISABLED)
			{
				this.speed_level.setLocation(0, 1);
			}
			else{this.speed_level.setLocation(this.target_speed_level);}
		}		
		else if(this.speed_level.getX() < this.target_speed_level.getX())
		{					
			this.speed_level.setLocation(this.speed_level.getX()+0.025, //d
					 					 this.speed_level.getY());
		}
		if(this.speed_level.getY() < this.target_speed_level.getY())
		{
			this.speed_level.setLocation(this.speed_level.getX(),
					 					 this.speed_level.getY()+0.025); //d
		}		
	}

	private boolean has_reached_target_speed()
	{		
		return    this.speed_level.getX() >= this.target_speed_level.getX() 
			   && this.speed_level.getY() >= this.target_speed_level.getY();
	}

	private void adjust_speed_to_barrier(Helicopter helicopter)
	{
		if(   this.stopping_barrier.direction.x == this.direction.x
		   && this.stopping_barrier.bounds.getCenterX()*this.direction.x 
		   		             < this.bounds.getCenterX()*this.direction.x
		   && this.stopping_barrier.speed.getX() > this.speed.getX())
		{
			this.speed.setLocation(	this.stopping_barrier.is_on_screen() 
									&& !this.is_on_screen()
			   							? 0 
			   							: this.stopping_barrier.speed.getX(),
			   						this.speed.getY());
		}
		else if( this.stopping_barrier.direction.y == this.direction.y
				 && this.stopping_barrier.bounds.getCenterY()*this.direction.y
				 		           < this.bounds.getCenterY()*this.direction.y
				 && this.stopping_barrier.speed.getY() > this.speed.getY()
				 && this.borrow_timer == DISABLED)
		{
			this.speed.setLocation(this.speed.getX(), this.stopping_barrier.speed.getY());
			if(helicopter.tractor == this){helicopter.stop_tractor();}
		}		
	}
	
	public static void paintAllDestroyed(Graphics2D g2d,
										 Controller controller,
										 Helicopter helicopter)
	{
		for(Enemy e : controller.enemy.get(DESTROYED))
		{
			e.paint(g2d, helicopter);
		}
	}

	public static void update_all_destroyed(Controller controller,
											Helicopter helicopter)
	{
		for(Iterator<Enemy> i = controller.enemy.get(DESTROYED).iterator(); i.hasNext();)
		{
			Enemy e = i.next();
			e.update_dead(controller.explosion, helicopter);
			
			if(	helicopter.basic_collision_requirements_satisfied(e) 
				&& !e.has_crashed)
			{
				e.collision(controller, helicopter);
			}				
			if(e.is_marked_for_removal)
			{
				e.clear_image();
				i.remove(); 
				controller.enemy.get(INACTIVE).add(e);
			}				
		}		// this.slowed_timer
	}

	private void update_dead( ArrayList<LinkedList<Explosion>> explosion, Helicopter helicopter)
	{				
		if(this.collision_damage_timer > 0){this.collision_damage_timer--;}	
		if(this.collision_audio_timer  > 0){this.collision_audio_timer--;}		
		if( !this.has_crashed 
		    && this.bounds.getMaxY() + this.speed.getY() >= this.y_crash_pos)
		{
			this.handle_crash_to_the_ground(explosion, helicopter);
		}		
		this.calculate_speed_dead();	
		this.move();
		if(this.bounds.getMaxX() < 0){this.is_marked_for_removal = true;}	
		this.set_paint_bounds();
	}
	
	private void handle_crash_to_the_ground(ArrayList<LinkedList<Explosion>> explosion,
											Helicopter helicopter)
	{
		this.has_crashed = true;
		this.speed_level.setLocation(ZERO_SPEED);
		this.set_y(this.y_crash_pos - this.bounds.getHeight());			
		if(this.is_servant()){this.is_marked_for_removal = true;}
		Audio.play(this.is_kaboom ? Audio.explosion4 : Audio.explosion3);
		Explosion.start(explosion, 
						helicopter, 
						this.bounds.getCenterX(),
						this.bounds.getCenterY(),
						this.is_kaboom ? JUMBO : STANDARD, 
						this.is_kaboom ? true : false);
	}
		
	public boolean isMajorBoss()
	{
		return this.type < 0;
	}
	
	private boolean is_boss()
	{
		return this.is_mini_boss || this.isMajorBoss();
	}
	
	boolean is_living_boss()
	{		
		return this.is_boss() && !this.is_destroyed;
	}
		
	boolean is_main_boss()
	{
		return this.isMajorBoss()
			   && !this.is_servant();
	}
	
	boolean is_minor_servant()
	{		
		return this.type == BOSS_2_SERVANT 
			   || this.type == BOSS_4_SERVANT;
	}
	
	private boolean is_shield_maker()
	{		
		return this.type == SMALL_SHIELD_MAKER 
			   || this.type == BIG_SHIELD_MAKER;
	}
	
	boolean is_final_boss_servant()
	{
		return this.type < FINAL_BOSS;
	}
	
	boolean is_servant()
	{		
		return	this.is_minor_servant()
				|| this.is_final_boss_servant();
	}

	private void collision(Controller controller, Helicopter helicopter)
	{
		boolean play_collision_sound = this.collision_audio_timer == READY;
		helicopter.be_affected_by_collision_with(this, controller, play_collision_sound);
				
		if(play_collision_sound)
		{
			this.collision_audio_timer = Helicopter.NO_COLLISION_DMG_TIME;
		}		
		this.collision_damage_timer = Helicopter.NO_COLLISION_DMG_TIME;
			
		if(	this.is_explodable 
			&& !this.is_invincible()
			&& !this.is_destroyed) 
		{
			this.explode( controller.explosion,
						  helicopter, 
						  0, 
						  this.is_kaboom 
						  	? JUMBO 
						  	: STANDARD, 
						  this.is_kaboom);
			
			if(	helicopter.hasShortrangeRadiation
				&& !this.is_kaboom)
			{
				this.reward_for(controller.powerUp,
								null, 
								helicopter, 
								helicopter.has_performed_teleport_kill());
			}
			this.destroy(helicopter);			
		}				
		if(	helicopter.currentPlating <= 0
			&& !helicopter.damaged)
		{
			helicopter.crash();
		}		
	}
	
	public void react_to_radiation(Controller controller, Helicopter helicopter)
	{
		if(	this.teleport_timer == READY){this.teleport();}
		else if(this.can_take_collison_dmg())
		{
			this.take_dmg((int)(
				helicopter.currentFirepower
				* (helicopter.bonus_kills_timer > Helicopter.NICE_CATCH_TIME - Helicopter.TELEPORT_KILL_TIME 
					? TELEPORT_DAMAGE_FACTOR 
					: RADIATION_DAMAGE_FACTOR)));				
							
			if(this.model == BARRIER)
			{
				if(	helicopter.has_triple_dmg() 
					&&  MyMath.toss_up(
							this.deactivation_prob
							*(helicopter.bonus_kills_timer 
								> Helicopter.NICE_CATCH_TIME 
								  - Helicopter.TELEPORT_KILL_TIME ? 2 : 1)))
				{
					this.hitpoints = 0;
				}
				else if(MyMath.toss_up(this.deactivation_prob*(helicopter.bonus_kills_timer > Helicopter.NICE_CATCH_TIME - Helicopter.TELEPORT_KILL_TIME ? 4 : 2)))
				{
					this.snooze(true);
				}
			}
			if(this.has_HPs_left()){this.react_to_hit(helicopter, null);}
			else
			{
				boolean beam_kill = helicopter.bonus_kills_timer > 0 ? true : false;
				this.die(controller, helicopter, null, beam_kill);
			}
		}		
	}

	private boolean can_take_collison_dmg()
	{		
		return 	   !this.is_destroyed 
				&& !this.is_explodable 
				&& !this.is_invincible()
				&& !(this.barrier_teleport_timer != DISABLED && this.barrier_shoot_timer == DISABLED)
				&& this.collision_audio_timer == READY;
	}
	
	public float collision_dmg(Helicopter helicopter)
	{		
		return helicopter.get_dmg_factor() 
			   *(helicopter.power_shield_on && this.is_explodable ? 0.65f : 1.0f)
			   *(this.is_kaboom && !this.is_destroyed && !helicopter.hasShortrangeRadiation
			     ? helicopter.kaboom_dmg() 
			     : (this.is_explodable && !this.is_invincible() && !this.is_destroyed)
					? 1.0f 
					: this.collision_damage_timer > 0 
						? 0.0325f 
						: 0.65f);
	}
	
	void take_dmg(int dmg)
	{
		this.hitpoints -= dmg;
		if(!this.has_HPs_left()){this.hitpoints = 0;}
	}
	
	public void hit_by_missile(Helicopter helicopter, Missile missile, ArrayList<LinkedList<Explosion>> explosion)
	{		
		helicopter.hit_counter++;
		if( missile.type == JUMBO  
			|| missile.type == PHASE_SHIFT 
			|| missile.extra_dmg)
		{
			Audio.play(Audio.explosion4);
		}
		else{Audio.play(Audio.explosion2);}
		missile.hits.put(this.hashCode(), this);
		this.take_dmg(missile.dmg);
		if(this.model == BARRIER)
		{
			if((missile.type == JUMBO || missile.type == PHASE_SHIFT || missile.extra_dmg) 
				&& MyMath.toss_up(	0.5f
									* this.deactivation_prob
									* (( (missile.type == JUMBO 
											|| missile.type == PHASE_SHIFT) 
										  && missile.extra_dmg) ? 2 : 1)))
			{
				this.hitpoints = 0;
			}
			else if(MyMath.toss_up(this.deactivation_prob*(missile.type == PLASMA ? 2 : 1)))
			{
				this.snooze(true);
			}
		}		
		if(missile.type == STUNNING 
		   && this.is_stunnable
		   && this.non_stunable_timer == READY)
		{			
			this.stun(helicopter, missile, explosion);
		}
	}	
	
	private void stun(Helicopter helicopter, Missile missile, ArrayList<LinkedList<Explosion>> explosion)
	{
		if(this.has_HPs_left()){Audio.play(Audio.stun);}
		this.explode(explosion, helicopter, missile);	
		this.non_stunable_timer = (int)(this.is_main_boss() || this.is_final_boss_servant()
										  ? 2.25f*Events.level 
										  : 0);
		this.knock_back_direction = missile.speed > 0 ? 1 : -1;		
				
		this.speed_level.setLocation( 
				(this.knock_back_direction == this.direction.x ? 1 : -1)
				  *(this.is_main_boss() || this.is_final_boss_servant()
				    ? (10f + helicopter.missileDrive)/(Events.level/10)
					:  10f + helicopter.missileDrive),
				0);
						
		this.stunning_timer = this.total_stunning_time 
			= (int)(17 + STUNNING_TIME_BASIS 
					     * (this.isMajorBoss() ? (10f/Events.level) : 2.5f));
				
		this.disable_site_effects(helicopter);
	}

	private void disable_site_effects(Helicopter helicopter)
	{
		if(helicopter.tractor == this){helicopter.stop_tractor();}
		if(!this.can_learn_kamikaze 
		   && this.cloaking_timer > 0 
		   && this.type != BOSS_3)
		{
			this.uncloak(READY);
		}		
	}

	public void react_to_hit(Helicopter helicopter, Missile missile)
	{
		if(this.is_ready_to_dodge(helicopter)){this.dodge();}
		
		if(this.can_do_hit_triggered_turn())
		{
			if(this.can_learn_kamikaze){this.start_kamikaze_mode();}
			     if(this.bounds.getMinX() > helicopter.bounds.getMinX()){this.direction.x = -1;}
			else if(this.bounds.getMaxX() < helicopter.bounds.getMaxX()){this.direction.x = 1;}				
		}
		if(this.type == BOSS_4){this.spawning_hornet_timer = READY;}
		
		if(this.cloaking_timer == READY && !(this.tractor == ACTIVE))
		{
			Audio.play(Audio.cloak); 
			this.cloaking_timer = ACTIVE;
		}
		if( missile != null 
		    && missile.type == STUNNING 
		    && this.cloaking_timer != DISABLED)
		{
			this.uncloak(this.model == BARRIER ? DISABLED : READY);
		}
		else if( !this.can_learn_kamikaze 
				 && this.cloaking_timer > CLOAKING_TIME 
				 && this.cloaking_timer <= CLOAKING_TIME+CLOAKED_TIME)
		{
			this.cloaking_timer = CLOAKED_TIME+1;
		}
	}	
		
	private boolean can_do_hit_triggered_turn()
	{		
		return this.can_instant_turn 
				|| this.can_turn 
					&& !this.can_early_turn 
					&& MyMath.toss_up(this.is_mini_boss 
										? this.is_carrier ? 0.2f : 0.5f 
										: this.is_carrier ? 0.1f : 0.25f);
	}

	private void explode(ArrayList<LinkedList<Explosion>> explosion, Helicopter helicopter, Missile missile)
	{
		explode(explosion, helicopter, missile.speed, missile.type, missile.extra_dmg);
	}	
	void explode(ArrayList<LinkedList<Explosion>> explosion, Helicopter helicopter)
	{
		explode(explosion, helicopter, 0, 0, false);
	}	
	private void explode(ArrayList<LinkedList<Explosion>> explosion, Helicopter helicopter, double missile_speed, int missile_type, boolean extra_dmg)
	{		
		if(this.exploding_timer == 0){this.exploding_timer = 7;}
		Explosion.start(explosion, 
						helicopter, 
						this.bounds.getX() + ((missile_type != EMP && this.model != BARRIER) 
							? (missile_speed < 0 ? 2 : 1) * this.bounds.getWidth()/3 
							: this.bounds.getWidth()/2), 
						this.bounds.getY() + this.bounds.getHeight()/2, 
						missile_type, extra_dmg);
	}
	
	void destroy(Helicopter helicopter){destroy(helicopter, null, true);}
	void destroy(Helicopter helicopter, 
	             ArrayList<LinkedList<PowerUp>> powerUp,
	             boolean was_destroyed_by_player)
	{
		if(was_destroyed_by_player)
		{
			if(this.model != BARRIER){helicopter.enemies_killed++;}
			if(this.is_mini_boss){helicopter.mini_boss_killed++;}
		}	
		else
		{
			if(this.can_drop_powerUp()){this.drop_powerUp(helicopter, powerUp);}
		}
		this.is_destroyed = true;
		if(this.cloaking_timer > 0){this.uncloak(DISABLED);}
		this.teleport_timer = DISABLED;
		this.farbe1 = MyColor.dimColor(this.farbe1, MyColor.DESTRUCTION_DIM_FACTOR);
		this.farbe2 = MyColor.dimColor(this.farbe2, MyColor.DESTRUCTION_DIM_FACTOR);	
		
		this.repaint();
	
		if(helicopter.tractor != null && helicopter.tractor == this)
		{
			helicopter.stop_tractor();
		}		
		this.speed_level.setLocation(0, 12); //d		
		this.direction.y = 1;
		
		this.emp_slowed_timer = READY;
		this.y_crash_pos = (int)(this.bounds.getMaxY() >= GROUND_Y 
									? this.bounds.getMaxY()
									: GROUND_Y 
									  + 1
									  + Math.random()
									    *(this.bounds.getHeight()/4));
	}
	
	private void uncloak(int next_cloaking_state)
	{
		this.alpha = 255;
		this.farbe1 = MyColor.setAlpha(this.farbe1, 255);
		this.farbe2 = MyColor.setAlpha(this.farbe2, 255);
		this.cloaking_timer = next_cloaking_state;
	}

	public void die(Controller controller, Helicopter helicopter,
					Missile missile, boolean beam_kill)
	{		
		this.reward_for(controller.powerUp, missile, helicopter, beam_kill);
		this.destroy(helicopter);		
		if(this.is_shielding){this.stop_shielding();}
		if(this.cloaking_timer != DISABLED){Audio.play(Audio.cloak);}
		
		if(missile == null)
		{
			this.explode(controller.explosion, helicopter);
		}		
		else if(missile.type != STUNNING)
		{
			this.explode(controller.explosion, helicopter, missile);
		}		
		
		this.evaluate_boss_destruction_effect(helicopter, 
											  controller.enemy,
											  controller.explosion);
			
		if(this.is_carrier){last_carrier = this;}		
		if(missile != null){missile.hits.remove(this.hashCode());}
	}	

	private void stop_shielding()
	{
		if(Events.boss.shield == 1){Audio.shield_up.stop();} 
		Events.boss.shield--;
		this.is_shielding = false;		
	}

	private void reward_for(ArrayList<LinkedList<PowerUp>> powerUp,
	                        Missile missile, 
	                        Helicopter helicopter, 
	                        boolean beam_kill)
	{																					   
		if(helicopter.getType() != HELIOS)
		{
			Events.last_bonus = this.calculate_reward(helicopter);
			Events.money += Events.last_bonus;
			Events.overallEarnings += Events.last_bonus;
			Events.last_extra_bonus = 0;		
			if(missile != null				
				&& (helicopter.getType() == ROCH || helicopter.getType() == OROCHI))
			{
				if(missile.kills > 0
				   && helicopter.hasPiercingWarheads
				   && (     Missile.can_take_credit(missile.sister[0], this)
						 || Missile.can_take_credit(missile.sister[1], this)))
				{
					if(Missile.can_take_credit(missile.sister[0], this))
					{
						missile.sister[0].credit();
					}
					else if(Missile.can_take_credit(missile.sister[1], this))
					{
						missile.sister[1].credit();
					}	
				}
				else
				{
					missile.credit();
				}				
			}
			else if(beam_kill)
			{
				helicopter.bonus_kills++;
				helicopter.bonus_kills_money += Events.last_bonus;
			}
			else if(helicopter.getType() == KAMAITACHI)
			{
				helicopter.bonus_kills_timer+=SPEED_KILL_BONUS_TIME;
				helicopter.bonus_kills++;
				helicopter.bonus_kills_money += Events.last_bonus;
			}
			Menu.money_display_timer = 0;	
		}
		if(this.type != BOSS_4_SERVANT && this.type >= FINAL_BOSS)
		{
			Events.kills_after_levelup++;
		}		
		if(this.can_drop_powerUp()){this.drop_powerUp(helicopter, powerUp);}
		if(this.is_mini_boss){Audio.play(Audio.applause2);}
	}
	
	private boolean can_drop_powerUp()
	{		
		return this.model != BARRIER
			   &&( (!Events.isBossLevel()
				    &&( ( MyMath.toss_up(POWER_UP_PROB) 
						  && Events.level >= MIN_POWER_UP_LEVEL) 
						|| this.is_mini_boss)) 
				|| this.type == BOSS_1
				|| this.type == BOSS_3
				|| this.type == BOSS_4 );
	}
	
	private void drop_powerUp(Helicopter helicopter,
							  ArrayList<LinkedList<PowerUp>> powerUp)
	{
		PowerUp.activate(helicopter, 
				 powerUp, 
				 this, 
				 MyMath.toss_up(0.14f)
					? REPARATION
					: PowerUpTypes.values()[MyMath.random(this.type < 0 ? 5 : 6)], false);
		
	}
	
	private int calculate_reward(Helicopter helicopter)
	{		
		return this.strength  
			   * (helicopter.spotlight 
					? Events.NIGHT_BONUS_FACTOR 
					: Events.DAY_BONUS_FACTOR) 
			   + this.reward_modifier;
	}

	private void evaluate_boss_destruction_effect(	Helicopter helicopter,
													ArrayList<LinkedList<Enemy>> enemy,
													ArrayList<LinkedList<Explosion>> explosion)
	{
		if(this.is_mini_boss)
		{
			current_mini_boss = null;  
		}			
		else if(this.type == BOSS_2)
		{								
			boss.setLocation(this.bounds.getCenterX(), 
							 this.bounds.getCenterY());
			make_boss2_servants = true;
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
			Events.restart_window_visible = true;
			Events.level = 51;
			Events.max_level = Events.level;
			helicopter.damaged = true;
		
			helicopter.destination.setLocation(helicopter.bounds.getX()+40, 
											   520.0);	
			Events.determine_highscore_times(helicopter);
		}
		else if(this.is_final_boss_servant())
		{
			Events.boss.operator.servants[id(this.type)] = null;
			Events.boss.operator.time_since_death[id(this.type)] = 0;
		}
		if(this.is_main_boss() && this.type != FINAL_BOSS){Events.boss = null;}
	}

	public void dodge()
	{
		if(this.type == BOSS_3)
		{			
			this.speed_level.setLocation(this.speed_level.getX(), 9);	//d		
			this.dodge_timer = 16;
		}
		else if(this.shoot_timer != DISABLED || this.can_chaos_speedup)
		{			
			this.speed_level.setLocation(this.speed_level.getX(), 8.5);	//d		
			this.dodge_timer = 13;
		}
		else
		{
			this.speed_level.setLocation(6, 6);	//d
			if(this.bounds.getMaxX() < 934){this.direction.x = 1;}
			this.dodge_timer = 16;
		}															   
		
		if(this.bounds.getY() > 143){this.direction.y = -1;}
		else{this.direction.y = 1;}
		
		if(this.is_shield_maker()){this.stampede_shield_maker();}
		else if(this.type == HEALER){this.can_dodge = false;}
	}
	
	private void stampede_shield_maker()
	{
		this.shield_maker_timer = READY;	
		this.speed_level.setLocation(SHIELD_MAKER_STAMPEDE_SPEED); //d	
		this.target_speed_level.setLocation(SHIELD_MAKER_STAMPEDE_SPEED);
		this.can_move_chaotic = true;
		this.can_dodge = false;		
		this.set_shielding_position();	
		if(this.is_shielding){this.stop_shielding();}		
	}

	private void set_shielding_position()
	{
		if(Events.boss.operator.servants[this.shielding_brother_id()] == null)
		{
			this.is_upper_shield_maker = MyMath.toss_up();
		}
		else
		{
			this.is_upper_shield_maker 
				= !Events.boss.operator.servants[this.shielding_brother_id()].is_upper_shield_maker;
		}		
	}

	private int shielding_brother_id()
	{		
		return this.type == SMALL_SHIELD_MAKER
				 ? id(BIG_SHIELD_MAKER)
				 : id(SMALL_SHIELD_MAKER);
	}

	public void teleport()
	{
		Audio.play(Audio.teleport2);		
		this.set_location(260.0 + Math.random()*(660.0 - this.bounds.getWidth()),
						   20.0 + Math.random()*(270.0 - this.bounds.getHeight()));
		this.speed_level.setLocation(ZERO_SPEED);
		this.teleport_timer = 60;
		this.invincible_timer = 40;
	}
	
	public boolean is_statically_charged()
	{		
		return this.static_charge_timer == READY
   	 		   && this.snooze_timer <= SNOOZE_TIME;
	}
	
	public void start_static_discharge(ArrayList<LinkedList<Explosion>> explosion,
	                                   Helicopter helicopter)
	{
		this.static_charge_timer = STATIC_CHARGE_TIME;
		helicopter.receive_static_charged(2.5f);
		Audio.play(Audio.emp);
		Explosion.start(explosion, helicopter, (int)this.bounds.getCenterX(), (int)this.bounds.getCenterY(), 2, false, this);				
	}

	public boolean is_hitable(Missile missile)
	{		
		return !this.is_destroyed 
			   && !(this.barrier_teleport_timer != DISABLED && this.alpha != 255)
			   && missile.intersects(this)
			   && !missile.hits.containsKey(this.hashCode());
	}

	public boolean is_ready_to_dodge(Helicopter helicopter)
	{		
		return 	    this.can_dodge
				&&  this.dodge_timer == READY
				&& !this.is_emp_slowed()
				&& !this.is_destroyed
				&& !(this.type == HEALER 
					 && Events.boss.shield > 0 
					 && this.bounds.getMinX() > Events.boss.bounds.getMinX() 
				     && this.bounds.getMaxX() < Events.boss.bounds.getMaxX())
				&& !( (     (helicopter.bounds.getX() - this.bounds.getMaxX() > -500)
						 && (helicopter.bounds.getX() - this.bounds.getX() 	  <  150))	
					  && this.can_kamikaze 
					  && this.direction.x == -1);
	}

	public boolean is_invincible()
	{		
		return    this.invincible_timer > 0 
			   || this.shield > 0;
	}

	public void evaluate_pos_adaption(Helicopter helicopter)
	{
		if(helicopter.is_location_adaption_approved(this))
		{			
			this.has_unresolved_intersection = true;
		}
		else
		{
			this.has_unresolved_intersection = false;
			if(!this.is_touching_helicopter
			   && this.touched_site != this.last_touched_site)    						
			{
				Audio.play(Audio.landing);
				this.is_touching_helicopter = true;
			}
		}		
	}

	public static void get_rid_of_some_enemies( Helicopter helicopter,
		                                        ArrayList<LinkedList<Enemy>> enemy,
												ArrayList<LinkedList<Explosion>> explosion)
	{
		for(Enemy e : enemy.get(ACTIVE))
		{
			if (e.model == BARRIER && e.is_on_screen())
			{
				e.explode(explosion, helicopter);
				e.destroy(helicopter);
			} else if (!e.is_on_screen())
			{
				e.is_marked_for_removal = true;
			}
		}		
	}

	public boolean has_HPs_left()
	{
		return this.hitpoints >= 1;
	}
}