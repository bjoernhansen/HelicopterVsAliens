package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.SceneryObjectPainter;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.CACTUS;
import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.DESERT;
import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.HILL;
import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.PALM;
import static de.helicopter_vs_aliens.model.scenery.SceneryObjectType.STONE;

// TODO Alles Allgemeines zu Backgrounds, was sich nicht auf die Background-Objekte bezieht in eigene Klasse
// TODO diese Klasse bekommt dann auch einen eigenen Painter
public class SceneryObject extends RectangularGameEntity
{
	private static final int
		// Häufigkeit mit der Hintergrundobjekte eines bestimmten Typs erscheinen
    	CACTUS_FREQUENCY = 8,
    	STONE_FREQUENCY = 40,
    	PALM_FREQUENCY = 10,
    	HILL_FREQUENCY = 15,
    	DESERT_FREQUENCY = 8,    	  
							
		UP_TO_STONE_FREQUENCY = CACTUS_FREQUENCY + STONE_FREQUENCY,
		UP_TO_PALM_FREQUENCY = 	UP_TO_STONE_FREQUENCY + PALM_FREQUENCY,
		UP_TO_HILL_FREQUENCY = 	UP_TO_PALM_FREQUENCY + HILL_FREQUENCY,
		TOTAL_FREQUENCY =		UP_TO_HILL_FREQUENCY + DESERT_FREQUENCY;
	
	public static final	float
		BG_SPEED = 2.0f;
	
	// TODO statische variablen teilweise verschieben nach Scenery und zu instanz-Variablen machen
	
	static int
		probabilityReductionFactor = 3, // legt fest, wie viele Objekte erscheinen (auf Wüste weniger als auf anderem Boden)
		generalObjectTimer; 			// Timer zur Sicherstellung eines zeitlichen Mindestabstands zwischen 2 Hintergrundobjekten
	
    private static int
		backgroundObjectSelection = TOTAL_FREQUENCY,
		mutualExclusionFactor, // sorgt dafür, dass an derselben Stelle nicht Wüste und Berg gleichzeitig auftreten können
	
		// weitere Timer zur Sicherstellung eines zeitlichen Mindestabstands zwischen 2 Hintergrundobjekten
		cactusTimer,
		stoneTimer,
		hillTimer;
    
    // Objekt-Attribute    
    public SceneryObjectType
        type;
    
    private int 
    	width;	// Gesamtbreite eines Hintergrundobjektes
	
	private final int[][]
		coordinatesOfComponents = new int[2][4]; 	// definiert Hintergrundobjekt-spezifische Koordinaten und Maße
   
	private float
		x;		// x-Koordinate
	
	private final BufferedImage[]
		images = new BufferedImage[2];        // Hintergrundobjekt-Bild (für Tag- udn Nachteinsatz)
	
	private final Color[]
		colors = new Color[2];            // nur für Palmen: Stammfarbe;
	
	private final SceneryObjectPainter
		sceneryObjectPainter = GraphicsManager.getInstance().getPainter(SceneryObject.class);
	
	private SceneryLayer
		layer;	// Ebene, in welcher das Hintergrundobjekt gezeichnet wird
	
	
	static void updateBackgroundTimer()
	{
		// TODO diese Methode gehört eher in die Scenery-Klasse
		// TODO Timer-Klasse verwenden
		if(mutualExclusionFactor == 1 )
		{
			backgroundObjectSelection = TOTAL_FREQUENCY;
			probabilityReductionFactor = 3;
		}
		if(mutualExclusionFactor > 0 ){
			mutualExclusionFactor--;}
		if(hillTimer > 0 ){
			hillTimer--;}
		if(generalObjectTimer > 0){
			generalObjectTimer--;}
		if(cactusTimer > 0){
			cactusTimer--;}
		if(stoneTimer > 0){
			stoneTimer--;}
	}
	
    void preset()
    {        
    	this.x = Main.VIRTUAL_DIMENSION.width + 25;
    	
    	// Sicherstellen, dass zwischen dem Erscheinen von zwei Kakteen bzw. 
    	// zwei Steinen eine gewisse Mindestzeit vergangen ist
        int random = (cactusTimer > 0 ? CACTUS_FREQUENCY : 0)
        			 + Calculations.random(
        					 backgroundObjectSelection
        					 - (cactusTimer > 0 ? CACTUS_FREQUENCY : 0)
        					 - (stoneTimer > 0 ? STONE_FREQUENCY  : 0) );
        if(stoneTimer > 0 && random >= CACTUS_FREQUENCY)
        {
        	random += STONE_FREQUENCY;
        }    
        
        // Auswahl des Modells anhand der ermittelten Zufallszahl
		// TODO Auswahl der Modelle in eine Factory Auslagern und mit Subtypen von BackgroundObject arbeiten
        if( random >= 0 && random < CACTUS_FREQUENCY)
        {           
            // Kaktus
        	this.type = CACTUS; 
            this.width = 50;
            this.layer = SceneryLayer.getRandomLayer();
            cactusTimer = 125;
        }                
        else if( random >= CACTUS_FREQUENCY && random < UP_TO_STONE_FREQUENCY)
        {   
        	// Steine
        	if(random < UP_TO_STONE_FREQUENCY - STONE_FREQUENCY/2 && !(hillTimer > 0)){this.layer = SceneryLayer.BACKGROUND;}
        	else{this.layer = SceneryLayer.FOREGROUND_OUTER;}
            this.type = STONE;            
            this.coordinatesOfComponents[0][0] = 0; 					   // position (x)
            this.coordinatesOfComponents[0][3] = 35; 					   // width
            this.coordinatesOfComponents[0][2] = 50 + Calculations.random(75); // height
            this.coordinatesOfComponents[0][1] = 12;  				   // position (y)
            this.width = 125 + this.coordinatesOfComponents[0][2];
            stoneTimer = 75;
        } 
        else if( random >= UP_TO_STONE_FREQUENCY && random < UP_TO_PALM_FREQUENCY)
        { 
            // Palme
        	this.type = PALM; 
            this.width = 225;
			this.layer = SceneryLayer.getRandomLayer();
            int a, b, c;
            a = 184 + Calculations.random(12);
            b = 150 + Calculations.random(12);
            c = 104 + Calculations.random(12);
            this.colors[1] = new Color(a, b,c);
            this.colors[0] = Colorations.dimColor(this.colors[1], Colorations.NIGHT_DIM_FACTOR);
            this.coordinatesOfComponents[0][0] = 20 + Calculations.random(70);
            cactusTimer = 140;
			sceneryObjectPainter.paintPalmStemImage(this);
			clearColors();
        }
        else if( random >= UP_TO_PALM_FREQUENCY && random < UP_TO_HILL_FREQUENCY)
        {            
            // Hügel
        	this.type = HILL;
            this.layer = SceneryLayer.BACKGROUND;
            for(int i = 0; i < 2; i++)
            {
            	this.coordinatesOfComponents[i][3] = 75 + Calculations.random(120); 			 // height
            	this.coordinatesOfComponents[i][2] = (int)((1 + Math.random()/3)
            										 *this.coordinatesOfComponents[i][3]); // width
            	this.coordinatesOfComponents[i][1] = -this.coordinatesOfComponents[i][3]/4+8; 	 // position (y)
            }              
            this.coordinatesOfComponents[0][0] = 0; 										 // position (x)
            this.coordinatesOfComponents[1][0] = this.coordinatesOfComponents[0][2]/3
            							   + Calculations.random(this.coordinatesOfComponents[0][2]/2);
            this.width = Math.max(this.coordinatesOfComponents[1][0]
                                  + this.coordinatesOfComponents[1][2],
                                  this.coordinatesOfComponents[0][2]);
            backgroundObjectSelection = UP_TO_HILL_FREQUENCY;
            if(mutualExclusionFactor < this.width/2)
            {
            	mutualExclusionFactor = this.width/2;
            } 
            if(hillTimer < this.width/2){
				hillTimer = this.width/2;}
        }
        else if( random >= UP_TO_HILL_FREQUENCY && random < TOTAL_FREQUENCY)
        {            
            // Sand
        	this.type = DESERT;
            this.layer = SceneryLayer.FOREGROUND_INNER;
            this.width = 600 + Calculations.random(400);
            probabilityReductionFactor = 17;
            if(mutualExclusionFactor < this.width/2)
            {
            	mutualExclusionFactor = this.width/2;
            }
            generalObjectTimer = 0;
            backgroundObjectSelection = UP_TO_PALM_FREQUENCY;
			sceneryObjectPainter.paintDesertImage(this);
        }        
    }
	
	private void clearColors()
	{
		for(int i = 0; i < 2; i++)
		{
			this.colors[i] = null;
		}
	}
	
	void makeFirstCactus()
    {
		this.x = 436;
		this.type = CACTUS;
		this.layer = SceneryLayer.FOREGROUND_MIDDLE;
    }
    
    void makeFirstHill()
    {
		this.layer = SceneryLayer.BACKGROUND;
		this.x = 638;
		this.type = HILL;
		this.width = 151;		
		this.coordinatesOfComponents[0][0] = 60;
		this.coordinatesOfComponents[0][1] = -9;
		this.coordinatesOfComponents[0][2] = 151;
		this.coordinatesOfComponents[0][3] = 75;
		this.coordinatesOfComponents[1][0] = 0;
		this.coordinatesOfComponents[1][1] = -28;
		this.coordinatesOfComponents[1][2] = 151;
		this.coordinatesOfComponents[1][3] = 112;
    }
    
    void makeFirstDesert()
    {
        this.x = 1000;
        this.type = DESERT;
        this.layer = SceneryLayer.FOREGROUND_INNER;
        this.width = 800;            
        probabilityReductionFactor = 17;
        mutualExclusionFactor = this.width/2;
        generalObjectTimer = 0;
        backgroundObjectSelection = UP_TO_PALM_FREQUENCY;
        sceneryObjectPainter.paintDesertImage(this);
    }
    
    void clearImage()
    {
    	this.images[0] = null;
        this.images[1] = null;
    }

	public int getCoordinateOfComponent(int i, int j)
	{
		return coordinatesOfComponents[i][j];
	}
	
	public float getX()
	{
		return x;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public float getMaxX()
	{
		return x + width;
	}

	public SceneryLayer getLayer()
	{
		return layer;
	}
	
	public BufferedImage getImage(int index)
	{
		return images[index];
	}
	
	public void setImage(int index, BufferedImage image)
	{
		this.images[index] = image;
	}

	public Color getColor(int index)
	{
		return colors[index];
	}
	
	public boolean isInBackground()
	{
		return layer.isBackgroundLayer();
	}
	
	public void move()
	{
		x -= BG_SPEED;
	}
}