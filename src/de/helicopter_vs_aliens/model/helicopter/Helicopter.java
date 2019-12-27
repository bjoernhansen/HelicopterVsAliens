package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.model.RectanglularGameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.helicopter.components.Battery;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.Calculation;
import de.helicopter_vs_aliens.util.Coloration;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.control.Events.NUMBER_OF_BOSS_LEVEL;
import static de.helicopter_vs_aliens.control.TimeOfDay.DAY;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.PriceLevel.EXTORTIONATE;
import static de.helicopter_vs_aliens.gui.WindowType.GAME;
import static de.helicopter_vs_aliens.gui.WindowType.STARTSCREEN;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.KABOOM;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.ORDINARY;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.*;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.NICE_CATCH_TIME;
import static de.helicopter_vs_aliens.model.helicopter.Phoenix.TELEPORT_KILL_TIME;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.*;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.*;
import static de.helicopter_vs_aliens.util.dictionary.Language.ENGLISH;


public abstract class Helicopter extends RectanglularGameEntity
{
    public static final int
		// TODO einstellen auf 60 Frames per Second
		POWERUP_DURATION = 930,         // Zeit [frames] welche ein eingesammeltes PowerUp aktiv bleibt
		NO_COLLISION_DAMAGE_TIME = 20,   // Zeitrate, mit der Helicopter Schaden durch Kollisionen mit Gegnern nehmen kann
    	INVULNERABILITY_DAMAGE_REDUCTION = 80,        // %-Wert der Schadensreduzierung bei Unverwundbarleit
		STANDARD_SPECIAL_COSTS = 125000,
		CHEAP_SPECIAL_COSTS = 10000;
    
    public static final double
        FOCAL_PNT_X_LEFT		= 39,
        FOCAL_PNT_X_RIGHT		= 83,
        FOCAL_PNT_Y_EXP			= 44,
        FOCAL_PNT_Y_POS	 		= 56;
    
    static final int
        GOLIATH_PLATING_STRENGTH = 2,
        STANDARD_GOLIATH_COSTS = 75000,
        NO_COLLISION_HEIGHT	= 6;
    
    static final float
        ENEMY_MISSILE_DAMAGE_FACTOR =  0.5f,
        STANDARD_MISSILE_DAMAGE_FACTOR =  1.0f;
        
    private static final int
        RECENT_DAMAGE_TIME = 50,        // Zeitrate in der die Lebenspunktleiste nach Kollisionen blinkt
        SLOW_TIME = 100,
        FIRE_RATE_POWERUP_LEVEL = 3,    // so vielen zusätzlichen Upgrades der Feuerrate entspricht die temporäre Steigerung der Feuerrate durch das entsprechende PowerUp
        STATIC_CHARGE_ENERGY_DRAIN = 45,              // Energieabzug für den Helikopter bei Treffer
        STANDARD_PLATING_STRENGTH = 1,
        SLOW_ROTATIONAL_SPEED	= 7,
        FAST_ROTATIONAL_SPEED	= 12,
        DAY_BONUS_FACTOR = 60,
        NIGHT_BONUS_FACTOR = 90,
		START_ENERGY = 150,
		SPOTLIGHT_COSTS = 35000;
    
    private static final float
        NOSEDIVE_SPEED = 12f,	        // Geschwindigkeit des Helikopters bei Absturz
        INVULNERABILITY_PROTECTION_FACTOR = 1.0f - INVULNERABILITY_DAMAGE_REDUCTION/100.0f,
        STANDARD_PROTECTION_FACTOR = 1.0f,
        STANDARD_BASE_PROTECTION_FACTOR = 1.0f,
        PLATING_MULTIPLIER = 1.3f;
    
    private static final Point
		HELICOPTER_MENU_PAINT_POS = new Point(692, 360);
    
    private static final Dimension
		HELICOPTER_SIZE = new Dimension(122, 69);
    
    private static final Rectangle
    	INITIAL_BOUNDS = new Rectangle(	150, 
		    							GROUND_Y 
		    							- HELICOPTER_SIZE.height 
		    							- NO_COLLISION_HEIGHT, 
		    							HELICOPTER_SIZE.width, 
		    							HELICOPTER_SIZE.height);
       
    public int
		missileDrive,						// Geschwindigkeit [Pixel pro Frame] der Raketen
		currentBaseFirepower,				// akuelle Feuerkraft unter Berücksichtigung des Upgrade-Levels und des evtl. erforschten Jumbo-Raketen-Spezial-Upgrades
		platingDurabilityFactor = STANDARD_PLATING_STRENGTH,    // SpezialUpgrade; = 2, wenn erforscht, sonst = 1; Faktor, der die Standardpanzerung erhöht
		numberOfCannons = 1,				// Anzahl der Kanonen; mögliche Werte: 1, 2 und 3
		recentDamageTimer,					// aktiv, wenn Helicopter kürzlich Schaden genommen hat; für Animation der Hitpoint-Leiste
		
		// für die Spielstatistik
		numberOfCrashes,					// Anzahl der Abstürze
		numberOfRepairs,					// Anzahl der Reparaturen
		missileCounter,						// Anzahl der abgeschossenen Raketen
		hitCounter,							// Anzahl der getroffenen Gegner
		numberOfEnemiesSeen,				// Anzahl der erschienenen Gegner
		numberOfEnemiesKilled,				// Anzahl der vernichteten Gegner
		numberOfMiniBossSeen,				// Anzahl der erschienenen Mini-Bosse
		numberOfMiniBossKilled,				// Anzahl der vernichteten Mini-Bosse

		powerUpTimer[] = new int [4], 		// Zeit [frames] in der das PowerUp (0: bonus dmg; 1: invincible; 2: endless energy; 3: bonus fire rate) noch aktiv ist
		   		
		// TODO private machen und lesenden Zugriff über getUpgradeLevel
		levelOfUpgrade[] = new int[StandardUpgradeType.size()];	// Upgrade-Level aller 6 StandardUpgrades
	
	public long
    	scorescreenTimes[] = new long [NUMBER_OF_BOSS_LEVEL];	// Zeit, die bis zum Besiegen jedes einzelnen der 5 Bossgegner vergangen ist
		
    public float
		rotorSystem;						// legt die aktuelle Geschwindigkeit des Helikopters fest
	

	private float
        currentEnergy;						// verfügbare Energie;
    
    int
    	rotorPosition;						// Stellung des Helikopter-Hauptrotors für alle Klassen; genutzt für die Startscreen-Animation
    
    float
        regenerationRate,					// Energiezuwachs pro Simulationsschritt
        spellCosts;							// Energiekosten für die Nutzung des Energie-Upgrades
    
    public boolean
		hasSpotlights,						// = true: Helikopter hat Scheinwerfer
		hasPiercingWarheads,				// = true: Helikopterraketen werden mit Durchstoß-Sprengköpfen bestückt
		isActive,							// = false: Helikopter ist nicht in Bewegung und kann auch nicht starten, Raketen abschießen, etc. (vor dem ersten Start oder nach Absturz = false)
        isDamaged,    						// = true: Helikopter hat einen Totalschaden erlitten
		// TODO kann evtl. genutzt werden, um Malen des Helicopters und Drehen des Propellers zu trennen
		isRotorSystemActive,				// = true: Propeller dreht sich / Helikopter fliegt
		isContiniousFireEnabled,			// = true: Dauerfeuer aktiv
		
		isMovingLeft,
		isPlayedWithoutCheats = true;			// = true: Spielstand kann in die Highscore übernommen werden, da keine cheats angewendet wurden
    
    public Point
    	destination = new Point(); 				// dorthin fliegt der Helikopter

    // TODO noch Phoenix auslagern
    public Point
    	priorTeleportLocation = new Point(); 	// nur für Phönix-Klasse: Aufenthaltsort vor Teleportation
    public boolean
        isSearchingForTeleportDestination;	    // = true: es wird gerade ein Zielort für den Teleportationvorgang ausgewählt
    
	public int
		// nur für Phönix- und Kamaitachi-Klasse
		// TODO auslagern in Phönix- und Kamaitachi-Klasse
		bonusKills,							// Anzahl der Kills, für den aktuelken Mulikill-Award
		bonusKillsMoney,					// Gesamtverdienst am Abschuss aller Gegner innerhalb des aktuellen Multikill-Awards ohne Bonus
		bonusKillsTimer;					// reguliert die Zeit, innerhalb welcher Kills für den Multikill-Award berücksichtigt werden
    
    Battery
        battery = Battery.createFor(this.getType());
    
	public Point2D
  		location = new Point2D.Float();	        // exakter Aufenthaltsort
    
    Point2D
        nextLocation = new Point2D.Float();
    
  	public Enemy 
  		tractor;			// Referenz auf den Gegner, der den Helikopter mit einem Traktorstrahl festhält
 	    
  	public Explosion
		empWave;			// Pegasus-Klasse: Referenz auf zuletzt ausgelöste EMP-Schockwelle
	
	private int
		fireRateTimer,  	// reguliert die Zeit [frames], die mind. vergehen muss, bis wieder geschossen werden kann
        timeBetweenTwoShots,// Zeit [frames], die mindestens verstreichen muss, bis wieder geschossen werden kann
        slowedTimer;		// reguliert die Verlangsamung des Helicopters durch gegnerische Geschosse
    
    private float 
    	speed,     			// aktuelle Geschwindigkeit des Helikopters
        currentPlating;		// aktuelle Panzerung (immer <= maximale Panzerung)
    
    private boolean
		isCrashing;			// Helikopter befindet sich im Sturzflug
      
    // Grundfarben zur Berechnung der Gradientenfarben
    // TODO ggf. eigene Klase für Farben einführen
    Color
    	inputColorCannon, 
    	inputColorHull, 
    	inputColorWindow, 
    	inputColorFuss1, 
    	inputColorFuss2, 
    	inputGray,
    	inputLightGray, 
    	inputLamp;                             
    
    // Gradientenfarben
    GradientPaint
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
    }
    
    public void paint(Graphics2D g2d)
    {
    	paint(g2d, this.paintBounds.x, this.paintBounds.y);
    }
    
    // TODO left / top ersetzen durch Point position
    public void paint(Graphics2D g2d, int left, int top)
    {
    	// TODO Alles was mit "malen" zusammenhängt in eine eigene Klasse auslagern
        this.determineColors(left, top);
    	this.paintComponents(g2d, left, top);
            
        //zu Testzwecken: 
        /*
        g2d.setColor(Color.red);
        g2d.draw(this.bounds);
        g2d.fillOval((int) this.location.getX()-2, (int) this.location.getY()-2, 4, 4); */
    }
    
    private void determineColors(int left, int top)
    {
        this.determineInputColors();
        this.determineGradientColors(left, top);
    }
    
    void paintComponents(Graphics2D g2d, int left, int top)
    {
        this.paintRotorHead(g2d, left, top);
        this.paintSkids(g2d, left, top);
        this.paintHull(g2d, left, top);
        this.paintCannons(g2d, left, top);
        this.paintSpotlights(g2d, left, top);
        this.paintMainRotor(g2d, left, top);
        this.paintTailRotor(g2d, left, top);
    }
    
    private void paintRotorHead(Graphics2D g2d, int left, int top)
    {
        g2d.setColor(this.inputLightGray);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(left+(this.hasLeftMovingAppearance() ? 39 : 83), top+14, left+(this.hasLeftMovingAppearance() ? 39 : 83), top+29);
    }
    
    boolean hasLeftMovingAppearance()
    {
        return this.isMovingLeft && Menu.window == GAME;
    }
    
    private void paintSkids(Graphics2D g2d, int left, int top)
    {
        g2d.setPaint(this.gradientFuss2);
        g2d.fillRoundRect(left+(this.hasLeftMovingAppearance() ? 25 : 54), top+70, 43, 5, 5, 5);
        g2d.setPaint(this.gradientFuss1);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2d.drawLine(left+61, top+66, left+61, top+69);
        g2d.drawLine(left+(this.hasLeftMovingAppearance() ? 33 : 89), top+66, left+(this.hasLeftMovingAppearance() ? 33 : 89), top+69);
        g2d.setStroke(new BasicStroke(1));
    }
    
    private void paintHull(Graphics2D g2d, int left, int top)
    {
        g2d.setPaint(this.gradientHull);
        g2d.fillOval(left+(this.hasLeftMovingAppearance() ?  2 : 45), top+29, 75, 34);
        g2d.fillRect(left+(this.hasLeftMovingAppearance() ? 92 : -7), top+31, 37,  8);
        g2d.fillArc (left+(this.hasLeftMovingAppearance() ? 34 : 23), top+11, 65, 40, 180, 180);
        g2d.setPaint(this.gradientWindow);
        g2d.fillArc (left+(this.hasLeftMovingAppearance() ?  1 : 69), top+33, 52, 22, (this.hasLeftMovingAppearance() ? 75 : -15), 120);
    }
    
    private void paintSpotlights(Graphics2D g2d, int left, int top)
    {
        if(this.hasSpotlights)
        {
            if(Events.timeOfDay == NIGHT && Menu.window == GAME)
            {
                g2d.setColor(Coloration.translucentWhite);
                g2d.fillArc(left+(this.hasLeftMovingAppearance() ? -135 : -43), top-96, 300, 300, (this.hasLeftMovingAppearance() ? 165 : -15), 30);
            }
            g2d.setPaint(this.gradientHull);
            g2d.fillRect(left+(this.hasLeftMovingAppearance() ? 4 : 106), top+50, 12, 8);
            g2d.setColor(this.inputLamp);
            g2d.fillArc(left+(this.hasLeftMovingAppearance() ? -1 : 115), top+50, 8, 8, (this.hasLeftMovingAppearance() ? -90 : 90), 180);
        }
    }
    
    void paintCannons(Graphics2D g2d, int left, int top)
    {
        g2d.setPaint(this.gradientCannon1);
        g2d.fillRoundRect(left+(this.hasLeftMovingAppearance() ? 26 : 53), top+52, 43, 13, 12, 12);
        g2d.setPaint(this.gradientCannonHole);
        g2d.fillOval(left+(this.hasLeftMovingAppearance() ? 27 : 90), top+54, 5, 9);
        if(this.numberOfCannons >= 2)
        {
            g2d.setPaint(this.gradientCannon2and3);
            g2d.fillRoundRect(left+(this.hasLeftMovingAppearance() ? 32 : 27), top+27, 63, 6, 6, 6);
            g2d.setPaint(this.gradientCannonHole);
            g2d.fillOval(left+(this.hasLeftMovingAppearance() ? 33 : 86), top+28, 3, 4);
        }
    }
    
    private void determineGradientColors(int left, int top)
	{
		this.gradientHull = new GradientPaint(0, top-10, Coloration.dimColor(this.inputColorHull, 1.65f),
			0, top+ 2, Coloration.dimColor(this.inputColorHull, 0.75f), true);
		this.gradientCannon1 = new GradientPaint(0, top+56, Coloration.dimColor(this.inputColorCannon, 1.65f),
			0, top+64, Coloration.dimColor(this.inputColorCannon, 0.55f), true);
		this.gradientWindow = new GradientPaint(0, top-10, Coloration.dimColor(this.inputColorWindow, 2.2f),
			0, top+ 2, Coloration.dimColor(this.inputColorWindow, 0.70f), true);
		this.gradientCannon2and3 = new GradientPaint(0, top+28, Coloration.dimColor(this.inputColorCannon, 1.7f),
			0, top+35, Coloration.dimColor(this.inputColorCannon, 0.4f), true);
		this.gradientFuss1 = new GradientPaint(left+61, 0, this.inputColorFuss1, left+68, 0, Coloration.dimColor(this.inputColorFuss1, 0.44f), true);
		this.gradientFuss2 = new GradientPaint(0, top+72, this.inputColorFuss2, 0, top+76, Coloration.dimColor(this.inputColorFuss2, 0.55f), true);
		this.gradientCannonHole = this.getGradientCannonHoleColor();
	}
    
    GradientPaint getGradientCannonHoleColor()
    {
        return this.gradientHull;
    }
    
    void determineInputColors()
	{
		this.inputColorCannon = this.getInputColorCannon();
		this.inputColorHull = this.getInputColorHull();
		this.inputColorWindow = this.getInputColorWindow();
		this.inputColorFuss1 = Coloration.lighterGray;
		this.inputColorFuss2 = Coloration.enemyGray;
		this.inputGray = Coloration.gray;
		this.inputLightGray = Coloration.lightGray;
		this.inputLamp = this.hasSpotlightsTurnedOn() ? Coloration.randomLight : Coloration.darkYellow;
	}
	
	Color getInputColorCannon()
	{
		return this.isInvincible()
			? Coloration.variableGreen
			: this.getSecondaryHullColor();
	}
	
	private Color getInputColorHull()
	{
		return this.isInvincible()
			? Coloration.variableGreen
			: this.getPrimaryHullColor();
	}
	
	private Color getInputColorWindow()
	{
		return this.hasTripleDmg() || this.hasBoostedFireRate()
			? Coloration.variableRed
			: Coloration.windowBlue;
	}
	
	private void paintMainRotor(Graphics2D g2d, int left, int top)
	{
		paintRotor(g2d,
			this.inputGray,
			left+(this.hasLeftMovingAppearance() ? -36 : 8),
			top-5,
			150, 37, 3,
            this.rotorPosition,
			12,
			this.isRotorSystemActive,
			false);
	}
	
	private void paintTailRotor(Graphics2D g2d, int left, int top)
	{
		paintRotor(g2d,
			this.inputGray,
			left+(this.hasLeftMovingAppearance() ?  107 : -22),
			top+14,
			37, 37, 3,
            this.rotorPosition,
			12,
			this.isRotorSystemActive,
			false);
	}
	
	public void startScreenMenuPaint(Graphics2D g2d)
	{
		this.rotatePropellerSlow();
		this.paint(g2d, HELICOPTER_MENU_PAINT_POS.x, HELICOPTER_MENU_PAINT_POS.y);
	}
    
    public void startScreenPaint(Graphics2D g2d, int left, int top)
    {
        this.paint(g2d, left, top);
        if(Events.recordTime[this.getType().ordinal()][4] > 0 && Menu.window == STARTSCREEN)
        {
            g2d.setFont(Menu.fontProvider.getBold(12));
            g2d.setColor(Color.yellow);
            g2d.drawString(Menu.language == ENGLISH ? "Record time:" : "Bestzeit:", left-27, top+67);
            g2d.drawString(Menu.minuten(Events.recordTime[this.getType().ordinal()][4]),left-27, top+80);
        }
    
        if(this.getType() == HELIOS && Menu.window == STARTSCREEN)
        {
            g2d.setFont(Menu.fontProvider.getBold(12));
            g2d.setColor(Coloration.brown);
            g2d.drawString(Menu.language == ENGLISH ? "Special mode" : "Spezial-Modus:", left-27, top-4);
        }
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
	       	g2d.setColor((Events.timeOfDay == DAY || enemiePaint) ? Coloration.translucentGray : Coloration.translucentWhite);
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

	public void update(EnumMap<CollectionSubgroupType, LinkedList<Missile>> missile,
					   EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{
		this.updateTimer();
		if(this.canRegenerateEnergy()){this.regenerateEnergy();}
		this.evaluateFire(missile);
		this.move(explosion);
	}
	
	private boolean hasSpotlightsTurnedOn()
	{
		return this.hasSpotlights
				&& Events.timeOfDay == NIGHT
				&& Menu.window == GAME;
	}
 
	boolean canRegenerateEnergy()
	{
		return !this.isDamaged;
	}

	void updateTimer()
	{
		if(this.recentDamageTimer > 0)		{this.recentDamageTimer--;}
		if(this.slowedTimer > 0)			{this.slowedTimer--;}
		this.evaluatePowerUpActivationStates();
	}
	
	void regenerateEnergy()
    {
    	this.rechargeEnergy(this.getRegenerationRate());
    }

    float getRegenerationRate()
	{
		return this.regenerationRate;
	}

	private void evaluateFire(EnumMap<CollectionSubgroupType, LinkedList<Missile>> missile)
	{
    	if(this.isReadyForShooting()){this.shoot(missile);}
    	this.fireRateTimer++;
	}
	
	public boolean hasTripleDmg()
	{		
		return this.powerUpTimer[TRIPLE_DAMAGE.ordinal()] > 0;
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
	void shoot(EnumMap<CollectionSubgroupType, LinkedList<Missile>> missiles)
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
	private void move(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
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
    	if(this.isRotorSystemActive){this.rotatePropellerFast();}
    	this.setPaintBounds();
    }

	boolean isShootingStunningMissile()
	{
		return false;
	}

	public boolean isLocationAdaptionApproved(Enemy enemy)
	{		
		return enemy.bounds.intersects(this.bounds)
				&& enemy.alpha == 255 
				&& enemy.borrowTimer != 0;
	}

	void adaptPosTo(Enemy enemy)
	{		
		double 
			x = this.bounds.getCenterX() - enemy.bounds.getCenterX(),
		 	y = this.bounds.getCenterY() - enemy.bounds.getCenterY(),
			pseudoAngle = (x/ Calculation.ZERO_POINT.distance(x, y)),
			distance,
			localSpeed = enemy.hasUnresolvedIntersection ? this.speed : Double.MAX_VALUE;
			
		if(pseudoAngle > Calculation.ROOT05)
		{
			// Right	
			// new pos x: enemy.getMaxX() + (this.moves_left ? 39 : 83) 
			distance = (enemy.bounds.getX() + enemy.bounds.getWidth()) + (this.isMovingLeft ? 39 : 83) - this.location.getX();
			this.nextLocation.setLocation(
				this.location.getX() + (distance > localSpeed ? localSpeed : distance),
				this.location.getY());
			enemy.setTouchedSiteToRight();
		}
		else if(pseudoAngle < -Calculation.ROOT05)
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
	
	void correctAndSetCoordinates()
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
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
	    {
    		if(newGame){this.levelOfUpgrade[standardUpgradeType.ordinal()] = this.getPriceLevelFor(standardUpgradeType).isCheap() ? 2 : 1;}
	    }
    	if(!newGame){this.restoreLastGameState(savegame);}
    	this.updateProperties(newGame);
    	this.fireRateTimer = this.timeBetweenTwoShots;
        this.empWave = null;
        this.placeAtStartpos();
        this.prepareForMission();
    }
	
	private void restoreLastGameState(Savegame savegame)
	{
		this.levelOfUpgrade = savegame.levelOfUpgrade.clone();
		this.hasSpotlights = savegame.spotlight;
		this.platingDurabilityFactor = savegame.platingDurabilityFactor;
		this.hasPiercingWarheads = savegame.hasPiercingWarheads;
		this.numberOfCannons = savegame.numberOfCannons;
		this.currentPlating = savegame.currentPlating;
		
		this.setCurrentEnergy(savegame.energy);
		
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
		this.resetStateGeneral(true);
        this.resetStateTypeSpecific();
        this.isDamaged = false;
		this.isPlayedWithoutCheats = true;
		this.resetCounterForHighscore();
		this.resetSpecialUpgrades();
        Arrays.fill(this.scorescreenTimes, 0);
    }
	
	
	public void resetStateGeneral(boolean resetStartPos)
	{
		// TODO boolscher Parameter - anders lösen
        this.setActivationState(false);
		this.isCrashing = false;
		this.slowedTimer = 0;
		this.recentDamageTimer = 0;
		for(int i = 0; i < 4; i++){this.powerUpTimer[i] = 0;}
        this.resetRotorPosition();
		this.fireRateTimer = this.timeBetweenTwoShots;
		if(resetStartPos){this.placeAtStartpos();}
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
		this.hasSpotlights = false;
		this.platingDurabilityFactor = STANDARD_PLATING_STRENGTH;
		this.hasPiercingWarheads = false;
		this.numberOfCannons = 1;
		this.resetFifthSpecial();
	}
	
	abstract void resetFifthSpecial();
		
    public void repair()
    {
    	Audio.play(Audio.cash);
    	this.numberOfRepairs++;
		this.isDamaged = false;
		this.isCrashing = false;
    	this.restorePlating();
    	this.setRelativePlatingDisplayColor();
		Menu.repairShopButton.get("RepairButton").costs = 0;
    }
	
	public void obtainAllUpgrades()
    {
    	for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
    	{
    		this.maximizeUpgrade(standardUpgradeType);
    	}
    	this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
    	this.hasPiercingWarheads = true;
    	this.getMaximumNumberOfCannons();
    	this.updateProperties(true);
		this.isDamaged = false;
    	Menu.updateRepairShopButtons(this);
    	this.isPlayedWithoutCheats = false;
    }
    
    private void maximizeUpgrade(StandardUpgradeType standardUpgradeType)
    {
        this.levelOfUpgrade[standardUpgradeType.ordinal()] = this.getPriceLevelFor(standardUpgradeType).getMaxUpgradeLevel();
    }
    
    void getMaximumNumberOfCannons()
	{
		this.numberOfCannons = 2;
	}
	
	public void obtainSomeUpgrades()
    {
		this.hasSpotlights = true;
    	this.obtainFifthSpecial();
		for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
    	{
    		if(this.getUpgradeLevelOf(standardUpgradeType) < EXTORTIONATE.getMaxUpgradeLevel())
    		{
    			this.levelOfUpgrade[standardUpgradeType.ordinal()] = EXTORTIONATE.getMaxUpgradeLevel();
    		}
    	}        		
    	this.updateProperties(true);
		this.isDamaged = false;
    	Menu.updateRepairShopButtons(this);
    	this.isPlayedWithoutCheats = false;
    }
	
	public boolean hasSomeUpgrades()
    {
    	for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
    	{
    		if(this.getUpgradeLevelOf(standardUpgradeType) < EXTORTIONATE.getMaxUpgradeLevel()){return false;}
    	}
    	if(!this.hasSpotlights){return false;}
    	else return this.hasFifthSpecial();
    }
	
	abstract public boolean hasFifthSpecial();

	abstract public void obtainFifthSpecial();
    
    private boolean hasAllSpecialUpgrades()
    {
		return this.hasSpotlights
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
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
        	if(!this.hasMaximumUpgradeLevelFor(standardUpgradeType))
        	{
        	    return false;
        	}
        }
		return hasAllSpecialUpgrades();
	}

	public void rotatePropellerSlow()
    {
    	this.rotatePropeller(SLOW_ROTATIONAL_SPEED);
    }
	
	public void rotatePropellerFast()
	{
		this.rotatePropeller(FAST_ROTATIONAL_SPEED);
	}
	
	private void rotatePropeller(int rotationalSpeed)
	{
		this.rotorPosition = (this.rotorPosition + rotationalSpeed)%360;
	}
	
    public void placeAtStartpos()
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
		this.discharge();
		this.destination.setLocation(this.bounds.getX() + 40, 520);
		if(this.tractor != null){this.stopTractor();}
		this.numberOfCrashes++;
		if(this.location.getY() == 407d){this.crashed(Controller.getInstance().explosions);}
		else{this.isCrashing = true;}
    }
	
	void discharge()
	{
		this.setCurrentEnergy(0.0f);
	}
    
    private void setCurrentEnergy(float currentEnergy)
    {
        this.currentEnergy = Math.max(0, Math.min(this.getMaximumEnergy(), currentEnergy));
    }
    
    private void crashed(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
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
		
    // TODO auslagern nach Phoenix und Kamaitachi
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
					Menu.collectedPowerUp[i].collect();
					Menu.collectedPowerUp[i] = null;
					// TODO magic number
					if(i == 3){this.adjustFireRate(false);}
				}
				else if(this.powerUpTimer[i] == POWERUP_DURATION/4)
				{
					Audio.play(Audio.powerUpFade1);
				}
				else if(this.powerUpTimer[i] < POWERUP_DURATION/4 && Menu.collectedPowerUp[i] != null)
				{
					int alphaStepSize = 17 * ((this.powerUpTimer[i])%16);
				    if(this.powerUpTimer[i]%32 > 15)
			    	{
			    		Menu.collectedPowerUp[i].setAlpha(alphaStepSize);
			    	}
					else
					{
                        Menu.collectedPowerUp[i].setAlpha(Coloration.MAX_VALUE - alphaStepSize);
					}
				}
			}
		}		
	}
	
	public void takeMissileDamage()
    {
        this.currentPlating = Math.max(this.currentPlating - this.getProtectionFactor() * ENEMY_MISSILE_DAMAGE_FACTOR, 0f);
        this.startRecentDamageTimer();
		if(this.isDestinedToCrash())
		{
			this.crash();
		}
    }
    
    public boolean hasDestroyedPlating()
    {
        return this.currentPlating <= 0;
    }
    
    void startRecentDamageTimer()
    {
        this.recentDamageTimer = RECENT_DAMAGE_TIME;
    }
    
    private void updateProperties(boolean fullPlating)
    {
    	this.updateRotorSystem();
    	this.updateMissileDrive();
    	if(fullPlating)
    	{
    		this.restorePlating();
    		this.restoreEnergy();
    	}
    	this.setRelativePlatingDisplayColor();
		this.setCurrentBaseFirepower();
    	this.adjustFireRate(this.hasBoostedFireRate());
        this.updateRegenerationRate();
		if(Menu.window != GAME){this.fireRateTimer = this.timeBetweenTwoShots;}
		this.setSpellCosts();
	}
    
    private void updateMissileDrive()
    {
        this.missileDrive = this.getMissileDrive();
    }
    
    private void updateRotorSystem()
	{
		rotorSystem = this.getSpeed();
	}
	
	void setSpellCosts()
	{
		this.spellCosts = this.getType().getSpellCosts();
	}

	public boolean hasPowerUpsDisallowedAtBossLevel()
	{
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

	public void setRelativePlatingDisplayColor()
	{		
		Coloration.plating = Coloration.percentColor(this.getRelativePlating());
	}
	
	public void getPowerUp(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp,
	                PowerUpType powerUpType,
	                boolean lastingEffect)
	{
		getPowerUp(powerUp, powerUpType, lastingEffect, true);
	}
	
	void getPowerUp(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp,
	                PowerUpType powerUpType,
	                boolean lastingEffect,
	                boolean playSound)
	{
		if(lastingEffect && this.powerUpTimer[powerUpType.ordinal()] > 0)
		{
			if(playSound){Audio.play(Audio.powerUpFade2);}
			this.powerUpTimer[powerUpType.ordinal()] = 0;
			Menu.collectedPowerUp[powerUpType.ordinal()].collect();
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
                Menu.collectedPowerUp[powerUpType.ordinal()].setOpaque();
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
	    // TODO überprüfen ob man direkt hier hasBoostedFireRate() nutzen kann und somit Parameter wegfallen kann
		this.timeBetweenTwoShots = this.getFireRate(poweredUp);
	}

	public int calculateSumOfFireRateBooster(boolean poweredUp)
	{
		return this.getUpgradeLevelOf(StandardUpgradeType.FIRE_RATE)
				+ (poweredUp ? FIRE_RATE_POWERUP_LEVEL : 0);
	}

	abstract public void updateUnlockedHelicopters();

	public void useReparationPowerUp()
	{
		Audio.play(Audio.cash);
		if(!this.hasMaximumPlating())
		{
			this.currentPlating
				= Math.min(
                    this.getMaximumPlating(),
					this.currentPlating + Math.max(1, this.missingPlating()/2));
		}
	}
    
    public boolean hasMaximumPlating()
    {
        return currentPlating >= this.getMaximumPlating();
    }
    
    public float kaboomDamage()
	{		
		return Math.max(4, 2*this.currentPlating/3);
	}
    
    
    private void restorePlating()
    {
        this.currentPlating = this.getMaximumPlating();
    }
	
	public float getMaximumPlating()
	{
		return this.platingDurabilityFactor * this.getBasePlating();
	}
    
    public float getBasePlating()
    {
        return getBasePlating(this.getPlatingLevel());
    }
    
    private float getBasePlating(int platingLevel)
    {
        return PLATING_MULTIPLIER * PLATING.getMagnitude(platingLevel);
    }
    
    private int getPlatingLevel()
    {
        return this.getUpgradeLevelOf(PLATING);
    }
    
    private void updatePlating()
    {
        this.currentPlating += this.getLastPlatingDurabilityIncrease();
        this.setRelativePlatingDisplayColor();
    }
    
    public float getLastPlatingDurabilityIncrease()
    {
        return this.platingDurabilityFactor * (this.getBasePlating() - this.getPreviousBasePlating());
    }
    
    private float getPreviousBasePlating()
    {
        int previousPlatingLevel = this.getPlatingLevel() - 1;
        return this.getBasePlating(previousPlatingLevel);
    }
    
    public float getCurrentPlating()
    {
        return currentPlating;
    }
    
	public void receiveStaticCharge(float degree)
	{
		if(!this.isInvincible())
		{
            this.slowDown();
		    if(!this.hasUnlimitedEnergy())
            {
                float energyConsumption = degree * this.getStaticChargeEnergyDrain();
                this.drainEnergy(energyConsumption);
            }
		}
	}
    
    void slowDown()
    {
        this.slowedTimer = SLOW_TIME;
    }
    
    float getStaticChargeEnergyDrain()
    {
        return STATIC_CHARGE_ENERGY_DRAIN;
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
				: STANDARD_PROTECTION_FACTOR;
	}

	public boolean isEnergyAbilityActivatable()
	{		
		return this.hasEnoughEnergyForAbility();
	}

	boolean hasEnoughEnergyForAbility()
	{
		return this.getCurrentEnergy() >= this.spellCosts || this.hasUnlimitedEnergy();
	}

	public void updateEnergyAbility()
	{
		int previousLevelOfEnergyAbility = this.getUpgradeLevelOf(ENERGY_ABILITY) - 1;
		float energyBoost = this.getMaximumEnergy() - this.getMaximumEnergy(previousLevelOfEnergyAbility);
		this.rechargeEnergy(energyBoost);
		this.updateRegenerationRate();
	}
    
    private void updateRegenerationRate()
    {
        this.regenerationRate = Battery.regeneration(this.getUpgradeLevelOf(ENERGY_ABILITY));
    }
    
    public void becomesCenterOf(Explosion exp)
	{
		exp.ellipse.setFrameFromCenter(
			this.bounds.getX() + (this.isMovingLeft ? FOCAL_PNT_X_LEFT : FOCAL_PNT_X_RIGHT),
			this.bounds.getY() + FOCAL_PNT_Y_EXP, 
			this.bounds.getX() + (this.isMovingLeft ? FOCAL_PNT_X_LEFT : FOCAL_PNT_X_RIGHT),
			this.bounds.getY() + FOCAL_PNT_Y_EXP);
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

	public void tryToUseEnergyAbility(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp,
									  EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{
		if(this.isEnergyAbilityActivatable())
		{
			useEnergyAbility(powerUp, explosion);
		}
	}

	public void useEnergyAbility(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp, EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion){};

	public void beAffectedByCollisionWith(Enemy enemy,
										  Controller controller,
										  boolean playCollisionSound)
	{
		this.startRecentDamageEffect(enemy);
		if(playCollisionSound)
		{
			Audio.play(enemy.type == KABOOM
					? Audio.explosion4
					: this.getCollisionAudio());
		}
		this.slowedTimer = 2;
		this.currentPlating = Math.max(0, this.currentPlating - enemy.collisionDamage(this));
	}
    
    void startRecentDamageEffect(Enemy enemy)
    {
        this.startRecentDamageTimer();
    }
    
    AudioClip getCollisionAudio()
    {
        return Audio.explosion1;
    }
    
    public boolean hasPerformedTeleportKill()
	{		
		return this.bonusKillsTimer > 0;
	}

	public abstract HelicopterType getType();
	
	public void installGoliathPlating()
	{
		this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
		this.currentPlating += this.getBasePlating();
		this.setRelativePlatingDisplayColor();
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
		this.currentBaseFirepower = (int)(this.getMissileDamageFactor() * this.getFirepower());
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
		Audio.playSpecialSound(this.getType());
	}
	
	public void updateMenuEffect()
	{
		this.rotatePropellerSlow();
		if(Menu.effectTimer[this.getType().ordinal()] == 1)
		{
			this.stoptMenuEffect();
		}
	}
	
	abstract public void stoptMenuEffect();
    
    public boolean isTakingKaboomDamageFrom(Enemy enemy)
    {
        return enemy.isKaboomDamageDealer();
    }
    
    public float getBaseDamage()
    {
        return this.currentBaseFirepower;
    }
    
    public void rightMouseButtonReleaseAction(MouseEvent mouseEvent){}
    
    public boolean canObtainCollisionReward()
    {
        return false;
    }
    
    public int getBonusFactor()
    {
        return this.hasSpotlights ? NIGHT_BONUS_FACTOR : DAY_BONUS_FACTOR;
    }
    
    public void resetRotorPosition()
    {
        this.rotorPosition = 0;
    }
    
    public float getBaseProtectionFactor(boolean isExplodable)
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
        this.resetRotorPosition();
    }
    
    public boolean deservesMantisReward(long missileLaunchingTime)
    {
        return false;
    }

	public void receiveRewardFor(Enemy enemy, Missile missile, boolean beamKill)
	{
		Events.updateFinance(enemy, this);
		this.typeSpecificRewards(enemy, missile, beamKill);
		Menu.moneyDisplayTimer = Events.START;
	}

	public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill) {}
    
    public void levelUpEffect(int previousLevel){}
    
    public PriceLevel getPriceLevelFor(StandardUpgradeType standardUpgradeType)
    {
        return this.getType().getPriceLevelFor(standardUpgradeType);
    }
	   
    private float getSpeed()
    {
        return ROTOR_SYSTEM.getMagnitude(this.getUpgradeLevelOf(ROTOR_SYSTEM));
    }
    
    private int getMissileDrive()
    {
        return (int)MISSILE_DRIVE.getMagnitude(this.getUpgradeLevelOf(StandardUpgradeType.MISSILE_DRIVE));
    }
    
    private int getFirepower()
    {
        return (int)FIREPOWER.getMagnitude(this.getUpgradeLevelOf(FIREPOWER));
    }
    
    public int getEmpDamage()
    {
        return (int)FIREPOWER.getMagnitude(this.getUpgradeLevelOf(ENERGY_ABILITY));
    }
    
    private int getFireRate(boolean poweredUp)
    {
        return (int)FIRE_RATE.getMagnitude(calculateSumOfFireRateBooster(poweredUp));
    }
    
    private float getMaximumEnergy(int level)
    {
        return START_ENERGY + ENERGY_ABILITY.getMagnitude(level);
    }
    
    // TODO eine Klasse Battery oder ähnliches erstellen und dort
	public float getCurrentEnergy()
	{
		return this.currentEnergy;
	}

	public void rechargeEnergy(float energyBoost)
	{
	    this.setCurrentEnergy(this.getCurrentEnergy() + energyBoost);
	}

	public float getMaximumEnergy()
	{
		int levelOfEnergyAbility = this.getUpgradeLevelOf(ENERGY_ABILITY);
		return this.getMaximumEnergy(levelOfEnergyAbility);
	}

	public void restoreEnergy()
	{
		this.setCurrentEnergy(this.getMaximumEnergy());
	}
    
    public float getMissingEnergy()
    {
        return this.getMaximumEnergy() - this.getCurrentEnergy();
    }
    
    protected void consumeSpellCosts()
    {
        this.drainEnergy(this.getEffectiveSpellCosts());
    }
    
	void drainEnergy(float energyConsumption)
	{
	    this.rechargeEnergy(-energyConsumption);
	}
	   
    protected float getEffectiveSpellCosts()
    {
        return this.hasUnlimitedEnergy() ? 0.0f : this.spellCosts;
    }
    
    protected boolean isDischarged()
    {
        return this.getCurrentEnergy() <= 0;
    }
    
	public int getUpgradeLevelOf(StandardUpgradeType standardUpgradeType)
	{
		return this.levelOfUpgrade[standardUpgradeType.ordinal()];
	}

	public void upgrade(StandardUpgradeType standardUpgradeType)
	{
		this.levelOfUpgrade[standardUpgradeType.ordinal()]++;
		switch (standardUpgradeType)
        {
            case ROTOR_SYSTEM:
                this.updateRotorSystem();
                break;
            case MISSILE_DRIVE:
                this.updateMissileDrive();
                break;
            case PLATING:
                this.updatePlating();
                break;
            case FIREPOWER:
                this.setCurrentBaseFirepower();
                break;
            case FIRE_RATE:
                this.adjustFireRate(false);
                break;
            case ENERGY_ABILITY:
                this.updateEnergyAbility();
                break;
        }
	}
    
    public boolean hasMaximumUpgradeLevelFor(StandardUpgradeType standardUpgradeType)
	{
		return this.getUpgradeLevelOf(standardUpgradeType) >= this.getPriceLevelFor(standardUpgradeType).getMaxUpgradeLevel();
	}
	
	public int getUpgradeCostFor(StandardUpgradeType standardUpgradeType)
	{
		int upgradeLevel = this.getUpgradeLevelOf(standardUpgradeType);
		PriceLevel priceLevel = this.getPriceLevelFor(standardUpgradeType);
		int baseUpgradeCosts = priceLevel.getBaseUpgradeCosts(upgradeLevel);
        int additionalUpgradeCosts = this.getAdditionalCosts(standardUpgradeType, upgradeLevel);
		
		return baseUpgradeCosts + additionalUpgradeCosts;
	}
    
    private int getAdditionalCosts(StandardUpgradeType standardUpgradeType, int upgradeLevel)
    {
        return this.getType().getAdditionalCosts(standardUpgradeType, upgradeLevel);
    }
    
    public float missingPlating()
    {
        return this.getMaximumPlating() - this.getCurrentPlating();
    }
    
    public void destroyPlating()
    {
        this.currentPlating = 0f;
    }
    
    public float getRelativePlating()
    {
        return this.getCurrentPlating() / this.getMaximumPlating();
    }
    
    public float getRelativeEnergy()
    {
        return this.getCurrentEnergy() / this.getMaximumEnergy();
    }
    
    public boolean isDestinedToCrash()
    {
        return this.hasDestroyedPlating() && !this.isDamaged;
    }
    
    public boolean hasTimeRecordingMissiles()
    {
    	return false;
    }
	
	public boolean hasKillCountingMissiles()
	{
		return false;
	}
    
    public void inactivate(EnumMap<CollectionSubgroupType, LinkedList<Missile>> missiles, Missile missile)
    {
        missiles.get(INACTIVE).add(missile);
    }

	public int getFifthSpecialCosts()
	{
		return CHEAP_SPECIAL_COSTS;
	}

	public int getSpotlightCosts()
	{
		return SPOTLIGHT_COSTS;
	}
}