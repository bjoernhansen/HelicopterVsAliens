package de.helicopter_vs_aliens.model.background;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.*;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.INACTIVE;
import static de.helicopter_vs_aliens.control.TimesOfDay.NIGHT;
import static de.helicopter_vs_aliens.model.background.BackgroundTypes.*;

public class BackgroundObject extends MovingObject
{	
	private static final int
		// Sternkoordinaten
		NR_OF_STARS = 40,
		STARS[][] = new int [2][NR_OF_STARS],
		
		// Häufigkeit mit der Hintergrundobjekte eines bestimmten Typs erscheinen
    	CACTUS_FREQUENCY = 8,
    	STONE_FREQUENCY = 40,
    	PALM_FREQUENCY = 10,
    	HILL_FREQUENCY = 15,
    	DESERT_FREQUENCY = 8,    	  
							
		UP_TO_STONE_FREQUENCY = CACTUS_FREQUENCY 
								+ STONE_FREQUENCY,
							
		UP_TO_PALM_FREQUENCY = 	UP_TO_STONE_FREQUENCY
								+ PALM_FREQUENCY, 
								
		UP_TO_HILL_FREQUENCY = 	UP_TO_PALM_FREQUENCY 
								+ HILL_FREQUENCY,
								
		TOTAL_FREQUENCY =		UP_TO_HILL_FREQUENCY
								+ DESERT_FREQUENCY;
	
	public static final	float
		BG_SPEED = 2.0f;
		
	private static final BufferedImage[]
    	CACTUS_IMG = paintCactusImage(),
    	PALM_CROWN_IMG = paintPalmCrownImage();
	
	// statische Variablen
	public static boolean
            backgroundMoves;		// = true: bewegter Hintergrund
	   
    private static int
		backgroundObjectSelection = TOTAL_FREQUENCY,
    	groundFactor = 3,			// legt fest, wie viele Objekte erscheinen (auf Wüste weniger als auf anderem Boden)
		mutualExclusionFactor,	// sorgt dafür, dass an der selben Stelle nicht Wüste und Berg gleichzeitig auftreten können
    	
    	// Timmer zur Sicherstellung eines zeitlichen Mindestabstand zwischen 2 Hintergrundobjekten
		generalObjectTimer,
		cactusTimer,
		stoneTimer,
    	hillTimer;
    
    private static float
		cloudX = 135;				// x-Koordinate der Wolke
    
    // Objekt-Attribute    
    public BackgroundTypes
        type;
    
    private int 
    	width,	// Gesamtbreite eines Hintergrundobjektes
    	plane,	// Ebene, in welche das Hintergrundobjekte gezeichnet wird 
		coordOfComponents[][] = new int[2][4]; 	// definiert Hintergrundobjekt-spezifische Koordinaten und Maße
   
	private float
		x;		// x-Koordinate
	
	private BufferedImage[] 
		image = new BufferedImage[2];		// Hintergrundobjekt-Bild (für Tag- udn Nachteinsatz)
    
	private Color[] 
		myColor = new Color[2];  			// nur für Palmen: Stammfarbe;
    
               
    private static BufferedImage [] paintCactusImage()
    {
    	Graphics2D g2d;
    	GradientPaint[] myGradientColor = new GradientPaint[3];
    	BufferedImage[] cactusImage = new BufferedImage[2];
    	for(int i = 0; i < 2; i++)
    	{
    		cactusImage[i] = new BufferedImage(52, 197, BufferedImage.TYPE_INT_ARGB);
    		g2d =  (Graphics2D) cactusImage[i].getGraphics();
    		g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
									RenderingHints.VALUE_ANTIALIAS_ON);
    		myGradientColor[0] = new GradientPaint(	 2,  0, MyColor.myGreen[0][i],
													15,  0, MyColor.dimColor(MyColor.myGreen[0][i], 0.80f), true);
			myGradientColor[1] = new GradientPaint(	 8,  0, MyColor.myGreen[1][i], 
													17,  0, MyColor.dimColor(MyColor.myGreen[1][i], 0.80f), true);
			myGradientColor[2] = new GradientPaint(	 9,  0, MyColor.myGreen[2][i], 
													17,  0, MyColor.dimColor(MyColor.myGreen[2][i], 0.85f), true); 	     	
			g2d.setPaint(myGradientColor[0]);           
			g2d.fillOval( 11, 62, 30, 135);
			g2d.setPaint(myGradientColor[1]);
			g2d.fillOval( 33,  0, 19, 124);
			g2d.setPaint(myGradientColor[2]);
			g2d.fillOval(  0, 34, 17, 94);  
    	}	
    	return cactusImage;
    }
    
    private static BufferedImage [] paintPalmCrownImage()
    {
    	Graphics2D g2DPalmCrown;
    	BufferedImage[] palmCrownImage = new BufferedImage[2];
    	for(int i = 0; i < 2; i++)
    	{
    		palmCrownImage[i] = 	new BufferedImage(209, 27, BufferedImage.TYPE_INT_ARGB);
    		g2DPalmCrown = (Graphics2D) palmCrownImage[i].getGraphics();
    		g2DPalmCrown.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
												RenderingHints.VALUE_ANTIALIAS_ON);						
			g2DPalmCrown.setPaint(new GradientPaint(	0,  4, MyColor.myGreen[0][i],
 														0, 17, MyColor.dimColor(MyColor.myGreen[0][i],
 														0.65f), true));
			g2DPalmCrown.fillArc(115, 1, 94, 26, 0, 225);
			g2DPalmCrown.setPaint(new GradientPaint(	0,  3, MyColor.myGreen[1][i],
 														0, 13, MyColor.dimColor(MyColor.myGreen[1][i],
 														0.60f), true));
			g2DPalmCrown.fillArc(0, 0 , 125, 21, -45, 225);
			g2DPalmCrown.setPaint(new GradientPaint(	0, 17, MyColor.myGreen[2][i],
 														0, 23, MyColor.dimColor(MyColor.myGreen[2][i], 
 														0.65f), true));
			g2DPalmCrown.fillArc(55, 14, 68, 11, -45, 240);
    	}
    	return palmCrownImage;
    }
    
    private void paintPalmStemImage()
    {
    	for(int i = 0; i < 2; i++)
    	{
    		this.image[i] = new BufferedImage(20, 80 + this.coordOfComponents[0][0] + 6, BufferedImage.TYPE_INT_ARGB);
        	Graphics2D g2DPalmStem = (Graphics2D)this.image[i].getGraphics();
        	g2DPalmStem.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
    								RenderingHints.VALUE_ANTIALIAS_ON); 
        	
    		g2DPalmStem.setPaint( new GradientPaint(	12, 0,  this.myColor[i],
    			 										23, 0, 	MyColor.dimColor(this.myColor[i], 0.75f), true));
    		this.myColor[i] = null;
    		g2DPalmStem.fillRect(0, 0, 20, 80 + this.coordOfComponents[0][0]);
    		g2DPalmStem.fillArc( 0, 73 + this.coordOfComponents[0][0], 20, 12, 180, 180);
    	}
    }
          
    private void paintDesertImage()
    {
    	for(int i = 0; i < 2; i++)
    	{
    		this.image[i] = new BufferedImage(this.width, 35, BufferedImage.TYPE_INT_ARGB);
        	Graphics2D g2DDesert = (Graphics2D) this.image[i] .getGraphics();
        	g2DDesert.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
										RenderingHints.VALUE_ANTIALIAS_ON); 
        	g2DDesert.setPaint(new GradientPaint(	 0 + this.x,  0, MyColor.sand[i],
													10 + this.x, 20, MyColor.dimColor(MyColor.sand[i], 0.9f), true));
        	g2DDesert.fillArc(0, -35, 300, 70, 180, 90);
        	g2DDesert.fillRect(149, 0, this.width - 298, 35);
        	g2DDesert.fillArc(this.width - 300, -35, 300, 70, 270, 90);
    	}
    }
    
    private void paint(Graphics2D g2d)
    {
    	// Kaktus
        if(this.type == CACTUS)
        {        	
        	g2d.drawImage(CACTUS_IMG[Events.timeOfDay.ordinal()], (int) this.x, 235, null);
        }
        
        // Steine
        if(this.type == STONE)
        { 
        	g2d.setPaint(MyColor.gradientStones[Events.timeOfDay.ordinal()]);
            g2d.fillOval( (int)(this.x + this.coordOfComponents[0][0]), 
            			  400 + this.coordOfComponents[0][1],
            			  this.coordOfComponents[0][2], 
            			  this.coordOfComponents[0][3]);
        }
        
        // Berge im Hintergrund in Bodenfarbe
        if(this.type == HILL)
        {        	
        	g2d.setPaint(MyColor.gradientHills[Events.timeOfDay.ordinal()]);
            g2d.fillArc( (int)(this.x + this.coordOfComponents[0][0]), 
            			 400 + this.coordOfComponents[0][1], 
            			 this.coordOfComponents[0][2], 
            			 this.coordOfComponents[0][3], 0, 180);
            g2d.fillArc( (int)(this.x + this.coordOfComponents[1][0]), 
            			 400 + this.coordOfComponents[1][1], 
            			 this.coordOfComponents[1][2], 
            			 this.coordOfComponents[1][3], 0, 180);                                 
        } 
              
        // Sand
        if(this.type == DESERT)
        {
        	g2d.drawImage(this.image[Events.timeOfDay.ordinal()], (int)this.x, 426, null);
        }
        
        // Palme
        if(this.type == PALM)
        {
        	g2d.drawImage( this.image[Events.timeOfDay.ordinal()], (int)(this.x + 110),
        				   350 - this.coordOfComponents[0][0], null);
        	g2d.drawImage( PALM_CROWN_IMG[Events.timeOfDay.ordinal()], (int)this.x,
        				   340 - this.coordOfComponents[0][0], null);        	
        }
    }    
    
    private void preset()
    {        
    	this.x = Main.VIRTUAL_DIMENSION.width + 25;
    	
    	// Sicherstellen, dass zwischen dem Erscheinen von zwei Kakteen bzw. 
    	// zwei Steinen eine gewisse Mindestzeit vergangen ist
        int random = (cactusTimer > 0 ? CACTUS_FREQUENCY : 0)
        			 + MyMath.random(
        					 backgroundObjectSelection
        					 - (cactusTimer > 0 ? CACTUS_FREQUENCY : 0)
        					 - (stoneTimer > 0 ? STONE_FREQUENCY  : 0) );
        if(stoneTimer > 0 && random >= CACTUS_FREQUENCY)
        {
        	random += STONE_FREQUENCY;
        }    
        
        // Auswahl des Modells anhand der ermittelten Zufallszahl
        if( random >= 0 && random < CACTUS_FREQUENCY)
        {           
            // Kaktus
        	this.type = CACTUS; 
            this.width = 50;
            this.plane = MyMath.randomDirection();
            cactusTimer = 125;
        }                
        else if( random >= CACTUS_FREQUENCY && random < UP_TO_STONE_FREQUENCY)
        {   
        	// Steine
        	if(random < UP_TO_STONE_FREQUENCY - STONE_FREQUENCY/2 && !(hillTimer > 0)){this.plane = -1;}
        	else{this.plane = 2;}                      
            this.type = STONE;            
            this.coordOfComponents[0][0] = 0; 					   // position (x)            
            this.coordOfComponents[0][3] = 35; 					   // width
            this.coordOfComponents[0][2] = 50 + MyMath.random(75); // height
            this.coordOfComponents[0][1] = 12;  				   // position (y)
            this.width = 125 + this.coordOfComponents[0][2];
            stoneTimer = 75;
        } 
        else if( random >= UP_TO_STONE_FREQUENCY && random < UP_TO_PALM_FREQUENCY)
        { 
            // Palme
        	this.type = PALM; 
            this.width = 225;
            if(MyMath.tossUp()){this.plane = 1;}
            else{this.plane = -1;}                      
            int a, b, c;
            a = 184 + MyMath.random(12);
            b = 150 + MyMath.random(12);
            c = 104 + MyMath.random(12);
            this.myColor[1] = new Color(a, b,c);
            this.myColor[0] = MyColor.dimColor(this.myColor[1], MyColor.NIGHT_DIM_FACTOR);
            this.coordOfComponents[0][0] = 20 + MyMath.random(70);
            cactusTimer = 140;
            this.paintPalmStemImage();
        }
        else if( random >= UP_TO_PALM_FREQUENCY && random < UP_TO_HILL_FREQUENCY)
        {            
            // Hügel
        	this.type = HILL;
            this.plane = -1;
            for(int i = 0; i < 2; i++)
            {
            	this.coordOfComponents[i][3] = 75 + MyMath.random(120); 			 // height  
            	this.coordOfComponents[i][2] = (int)((1 + Math.random()/3) 
            										 *this.coordOfComponents[i][3]); // width
            	this.coordOfComponents[i][1] = -this.coordOfComponents[i][3]/4+8; 	 // position (y)
            }              
            this.coordOfComponents[0][0] = 0; 										 // position (x)
            this.coordOfComponents[1][0] = this.coordOfComponents[0][2]/3 
            							   + MyMath.random(this.coordOfComponents[0][2]/2);
            this.width = Math.max(this.coordOfComponents[1][0] 
                                  + this.coordOfComponents[1][2], 
                                  this.coordOfComponents[0][2]); 
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
            this.plane = 0;
            this.width = 600 + MyMath.random(400);            
            groundFactor = 17;
            if(mutualExclusionFactor < this.width/2)
            {
            	mutualExclusionFactor = this.width/2;
            }
            generalObjectTimer = 0;
            backgroundObjectSelection = UP_TO_PALM_FREQUENCY;
            this.paintDesertImage();
        }        
    }
    
    private static void paintBackground(Graphics2D g2d)
    {
    	// Sonne bzw. Mond
    	int coronaRadiusIncrease = 0;
        if(Events.timeOfDay == NIGHT) {g2d.setColor(MyColor.lighterYellow); }
        else
        {
        	g2d.setColor(MyColor.randomLight);
        	coronaRadiusIncrease = (MyColor.randomSunlightBlue - 175)/20;
        }      
        g2d.fillOval(865, 30, 60, 60);
        g2d.setColor(MyColor.translucentSun);  
        g2d.setStroke(new BasicStroke(35));
        g2d.drawOval(855-coronaRadiusIncrease, 20-coronaRadiusIncrease,
        			 80+2*coronaRadiusIncrease, 80+2*coronaRadiusIncrease);
        g2d.setStroke(new BasicStroke(1));        
                 
        // Sterne
        if(Events.timeOfDay == NIGHT)
        {
            g2d.setColor(Color.white);
            for(int m = 0; m < 40; m++)
            {                
                g2d.drawLine(STARS[0][m], STARS[1][m], 
                			 STARS[0][m], STARS[1][m]);
            }
        }
        
        // Wolke
        g2d.setPaint(MyColor.gradientCloud[Events.timeOfDay.ordinal()]);
        g2d.fillOval((int) cloudX, 51,  82, 45);
        g2d.fillOval((int)(cloudX + 41), 63, 150, 60);
        g2d.fillOval((int)(cloudX + 68), 40,  60, 53);
    }   
        
    private void makeFirstCactus()
    {
		this.x = 436;
		this.type = CACTUS;
		this.plane = 1;
    }
    
    private void makeFirstHill()
    {
		this.plane = -1;
		this.x = 638;
		this.type = HILL;
		this.width = 151;		
		this.coordOfComponents[0][0] = 60;
		this.coordOfComponents[0][1] = -9;
		this.coordOfComponents[0][2] = 151;
		this.coordOfComponents[0][3] = 75;
		this.coordOfComponents[1][0] = 0;
		this.coordOfComponents[1][1] = -28;
		this.coordOfComponents[1][2] = 151;
		this.coordOfComponents[1][3] = 112;
    }
    
    private void makeFirstDesert()
    {
        this.x = 1000;
        this.type = DESERT;
        this.plane = 0;
        this.width = 800;            
        groundFactor = 17;
        mutualExclusionFactor = this.width/2;
        generalObjectTimer = 0;
        backgroundObjectSelection = UP_TO_PALM_FREQUENCY;
        this.paintDesertImage();
    }    
    
    public static void reset(EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> backgroundObjects)
    {
    	backgroundObjects.get(INACTIVE).addAll(backgroundObjects.get(ACTIVE));
    	backgroundObjects.get(ACTIVE).clear();
    	Iterator<BackgroundObject> i = backgroundObjects.get(INACTIVE).iterator();
    	BackgroundObject bgo;
		if(i.hasNext()){bgo = i.next(); i.remove();}	
		else{bgo = new BackgroundObject();}
		bgo.makeFirstCactus();
		backgroundObjects.get(ACTIVE).add(bgo);
		if(i.hasNext()){bgo = i.next(); i.remove();}	
		else{bgo = new BackgroundObject();}
		bgo.makeFirstHill();		
		backgroundObjects.get(ACTIVE).add(bgo);
		if(i.hasNext()){bgo = i.next(); i.remove();}	
		else{bgo = new BackgroundObject();}
		bgo.makeFirstDesert();		
		backgroundObjects.get(ACTIVE).add(bgo);
		cloudX = 135;
    }
    
    public static void initialize(EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> backgroundObjects)
    {    	
    	BackgroundObject firstCactus = new BackgroundObject();
    	firstCactus.makeFirstCactus();
    	backgroundObjects.get(ACTIVE).add(firstCactus);
    	BackgroundObject firstHill = new BackgroundObject();
    	firstHill.makeFirstHill();
    	backgroundObjects.get(ACTIVE).add(firstHill);
    	BackgroundObject firstDesert = new BackgroundObject();
    	firstDesert.makeFirstDesert();
    	backgroundObjects.get(ACTIVE).add(firstDesert);
    	initializeStars();
    	paintCactusImage();
    	paintPalmCrownImage();
    }
    
    private static void initializeStars()
    {
    	for(int i = 0; i < NR_OF_STARS; i++)
		{
			STARS[0][i] = MyMath.random(982);
			STARS[1][i] = MyMath.random(GROUND_Y);
		}
    }
    
    private static void moveCloud()
    {    	
    	cloudX -= backgroundMoves ? 0.5f : 0.125f;
		if(cloudX < -250){
			cloudX = 1000;}
    }
    
    private static void updateBackgroundTimer()
    {
    	if(mutualExclusionFactor == 1 )
		{
			backgroundObjectSelection = TOTAL_FREQUENCY;
			groundFactor = 3;
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
    
    private void clearImage()
    {
    	this.image[0] = null; 
        this.image[1] = null;
    }

	public static void paintBackground(Graphics2D g2d, EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> backgroundObjects)
	{		
		paintBackground(g2d);
		paintBgObjects(g2d, backgroundObjects);
	}

	public static void update(Controller controller, EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> backgroundObjects)
	{		
		backgroundMoves = isBackgroundMoving(controller.enemies, controller.getHelicopter());
		for(Iterator<BackgroundObject> i = backgroundObjects.get(ACTIVE).iterator(); i.hasNext();)
		{
			BackgroundObject bgo = i.next();
			if(backgroundMoves){bgo.x -= BG_SPEED;}
			if(bgo.x < -(bgo.width + 50))
			{
				bgo.clearImage();
				i.remove();
				backgroundObjects.get(INACTIVE).add(bgo);
			}
		}		
		moveCloud();
		generateNewBackgroundObjects(backgroundObjects);
		if(backgroundMoves){
            updateBackgroundTimer();}
	}

	private static boolean isBackgroundMoving(EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy, Helicopter helicopter)
	{
		return helicopter.isRotorSystemActive
				&& !isMajorBossActive(enemy)
				&& helicopter.tractor == null;
	}
	
	private static boolean isMajorBossActive(EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemy)
    {
    	return    !enemy.get(ACTIVE).isEmpty()
				&& enemy.get(ACTIVE).getFirst().type.isMajorBoss();
	}

	private static void paintBgObjects(Graphics2D g2d, EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> backgroundObjects)
	{
        for (BackgroundObject bgo : backgroundObjects.get(ACTIVE))
        {
            if (bgo.plane == -1)
            {
                bgo.paint(g2d);
            }
        }
	}

	private static void generateNewBackgroundObjects(EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> backgroundObjects)
	{
		int numberOfBackgroundObjects = backgroundObjects.get(ACTIVE).size();
		if( numberOfBackgroundObjects < 20 && MyMath.creationProbability( 20 - numberOfBackgroundObjects,
				groundFactor) &&
			generalObjectTimer == 0 && backgroundMoves)
		{
			generalObjectTimer = 20;
			Iterator<BackgroundObject> i = backgroundObjects.get(INACTIVE).iterator();
	    	BackgroundObject bgo;
			if(i.hasNext()){bgo = i.next(); i.remove();}	
			else{bgo = new BackgroundObject();}
			bgo.preset();
			backgroundObjects.get(ACTIVE).add(bgo);
		}
	}

	public static void paintForeground(Graphics2D g2d, EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> backgroundObjects)
	{
		// der Boden
		g2d.setPaint(MyColor.gradientGround[Events.timeOfDay.ordinal()]);
        g2d.fillRect(0, GROUND_Y, Main.VIRTUAL_DIMENSION.width, 35);
        
        // Objekte vor dem Helikopter
    	for(int j = 0; j < 3; j++)
		{
            for (BackgroundObject bgo : backgroundObjects.get(ACTIVE))
            {
                if (bgo.plane == j)
                {
                    bgo.paint(g2d);
                }
            }
		}
	}
}