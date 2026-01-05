package de.helicopter_vs_aliens.model.scenery;

import de.helicopter_vs_aliens.control.entities.ManageablePaintable;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.PainterProvider;
import de.helicopter_vs_aliens.graphics.painter.SceneryObjectPainter;
import de.helicopter_vs_aliens.model.RectangularPaintableEntity;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.image.BufferedImage;


// TODO Alles Allgemeines zu Backgrounds, was sich nicht auf die Background-Objekte bezieht in eigene Klasse
// TODO diese Klasse bekommt dann auch einen eigenen Painter
public class SceneryObject extends RectangularPaintableEntity implements ManageablePaintable
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
		
	private static final int
		X_LIMIT_FOR_REMOVAL = -50;
	
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
    	x = GraphicsAdapter.VIRTUAL_DIMENSION.getWidth() + 25;
    	
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
        	type = SceneryObjectType.CACTUS;
            width = 50;
            layer = SceneryLayer.getRandomLayer();
            cactusTimer = 125;
        }                
        else if( random >= CACTUS_FREQUENCY && random < UP_TO_STONE_FREQUENCY)
        {   
        	// Steine
        	if(random < UP_TO_STONE_FREQUENCY - STONE_FREQUENCY/2 && !(hillTimer > 0)){layer = SceneryLayer.BACKGROUND;}
        	else{layer = SceneryLayer.FOREGROUND_OUTER;}
            type = SceneryObjectType.STONE;
            coordinatesOfComponents[0][0] = 0; 					   // position (x)
            coordinatesOfComponents[0][3] = 35; 					   // width
            coordinatesOfComponents[0][2] = 50 + Calculations.random(75); // height
            coordinatesOfComponents[0][1] = 12;  				   // position (y)
            width = 125 + coordinatesOfComponents[0][2];
            stoneTimer = 75;
        } 
        else if( random >= UP_TO_STONE_FREQUENCY && random < UP_TO_PALM_FREQUENCY)
        { 
            // Palme
        	type = SceneryObjectType.PALM;
            width = 225;
			layer = SceneryLayer.getRandomLayer();
            int a, b, c;
            a = 184 + Calculations.random(12);
            b = 150 + Calculations.random(12);
            c = 104 + Calculations.random(12);
            colors[1] = new Color(a, b,c);
            colors[0] = Colorations.adjustBrightness(colors[1], Colorations.NIGHT_DIM_FACTOR);
            coordinatesOfComponents[0][0] = 20 + Calculations.random(70);
            cactusTimer = 140;
			paintPalmStemImage();
			clearColors();
        }
        else if( random >= UP_TO_PALM_FREQUENCY && random < UP_TO_HILL_FREQUENCY)
        {            
            // Hügel
        	type = SceneryObjectType.HILL;
            layer = SceneryLayer.BACKGROUND;
            for(int i = 0; i < 2; i++)
            {
            	coordinatesOfComponents[i][3] = 75 + Calculations.random(120); 			 // height
            	coordinatesOfComponents[i][2] = (int)((1 + Math.random()/3)
            										 *coordinatesOfComponents[i][3]); // width
            	coordinatesOfComponents[i][1] = -coordinatesOfComponents[i][3]/4+8; 	 // position (y)
            }              
            coordinatesOfComponents[0][0] = 0; 										 // position (x)
            coordinatesOfComponents[1][0] = coordinatesOfComponents[0][2]/3
            							   + Calculations.random(coordinatesOfComponents[0][2]/2);
            width = Math.max(coordinatesOfComponents[1][0]
                                  + coordinatesOfComponents[1][2],
                                  coordinatesOfComponents[0][2]);
            backgroundObjectSelection = UP_TO_HILL_FREQUENCY;
            if(mutualExclusionFactor < width/2)
            {
            	mutualExclusionFactor = width/2;
            } 
            if(hillTimer < width/2){
				hillTimer = width/2;}
        }
        else if( random >= UP_TO_HILL_FREQUENCY && random < TOTAL_FREQUENCY)
        {            
            // Sand
        	type = SceneryObjectType.DESERT;
            layer = SceneryLayer.FOREGROUND_INNER;
            width = 600 + Calculations.random(400);
            probabilityReductionFactor = 17;
            if(mutualExclusionFactor < width/2)
            {
            	mutualExclusionFactor = width/2;
            }
            generalObjectTimer = 0;
            backgroundObjectSelection = UP_TO_PALM_FREQUENCY;
			paintDesertImages();
        }        
    }
	
	private void paintPalmStemImage()
	{
		// TODO image auf EnumMap TimeOfDay umstellen
		for(int i = 0; i < 2; i++)
		{
			setImage(i, new BufferedImage(20, 80 + getCoordinateOfComponent(0,0) + 6, BufferedImage.TYPE_INT_ARGB));
			GraphicsAdapter graphicsAdapter = Graphics2DAdapter.withAntialiasingOf(getImage(i));
			SceneryObjectPainter painter = PainterProvider.getPainterFor(SceneryObject.class).with(graphicsAdapter);
			painter.paintPalmStem(this, i);
		}
	}
	
	private void paintDesertImages()
	{
		// TODO ist vermutlich loop für Tageszeiten hier entsprechend Enum-Datentypen verwenden
		for(int i = 0; i < 2; i++)
		{
			setImage(i, new BufferedImage(getSceneryObjectWidth(), 35, BufferedImage.TYPE_INT_ARGB));
			GraphicsAdapter graphicsAdapter = Graphics2DAdapter.withAntialiasingOf(getImage(i));
			SceneryObjectPainter painter = PainterProvider.getPainterFor(SceneryObject.class).with(graphicsAdapter);
			painter.paintDesert(this, i);
		}
	}
	
	private void clearColors()
	{
		for(int i = 0; i < 2; i++)
		{
			colors[i] = null;
		}
	}
	
	void initializeAsFirstCactus()
    {
		x = 436;
		type = SceneryObjectType.CACTUS;
		layer = SceneryLayer.FOREGROUND_MIDDLE;
    }
    
    void initializeAsFirstHill()
    {
		layer = SceneryLayer.BACKGROUND;
		x = 638;
		type = SceneryObjectType.HILL;
		width = 151;
		coordinatesOfComponents[0][0] = 60;
		coordinatesOfComponents[0][1] = -9;
		coordinatesOfComponents[0][2] = 151;
		coordinatesOfComponents[0][3] = 75;
		coordinatesOfComponents[1][0] = 0;
		coordinatesOfComponents[1][1] = -28;
		coordinatesOfComponents[1][2] = 151;
		coordinatesOfComponents[1][3] = 112;
    }
    
    void initializeAsFirstDesert()
    {
        x = 1000;
        type = SceneryObjectType.DESERT;
        layer = SceneryLayer.FOREGROUND_INNER;
        width = 800;
        probabilityReductionFactor = 17;
        mutualExclusionFactor = width/2;
        generalObjectTimer = 0;
        backgroundObjectSelection = UP_TO_PALM_FREQUENCY;
		paintDesertImages();
    }
    
    void clearImage()
    {
    	images[0] = null;
        images[1] = null;
    }

	public int getCoordinateOfComponent(int i, int j)
	{
		return coordinatesOfComponents[i][j];
	}
	
	// TODO ggf. über bounds realisieren und dann die Methoden von RectangularPaintableEntity nutzen
	public float getSceneryObjectX()
	{
		return x;
	}
	
	public int getSceneryObjectWidth()
	{
		return width;
	}
	
	private float getSceneryObjectMaxX()
	{
		return x + width;
	}
	
	public BufferedImage getImage(int index)
	{
		return images[index];
	}
	
	public void setImage(int index, BufferedImage image)
	{
		images[index] = image;
	}

	public Color getColor(int index)
	{
		return colors[index];
	}
	
	boolean isInBackground()
	{
		return layer.isBackgroundLayer();
	}
	
	void move()
	{
		x -= BG_SPEED;
	}
	
	@Override
	public ManageablePaintableGroupType getGroupType()
	{
		return ManageablePaintableGroupType.SCENERY_OBJECT;
	}
    
    boolean isOutOfSight()
    {
		return getSceneryObjectMaxX() < X_LIMIT_FOR_REMOVAL;
	}
	
	public boolean belongsToLayer(SceneryLayer layer)
	{
		return this.layer == layer;
	}
}