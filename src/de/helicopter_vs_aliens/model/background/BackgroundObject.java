package de.helicopter_vs_aliens.model.background;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.*;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

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
    	CACTUS_IMG = paint_cactus_image(), 
    	PALM_CROWN_IMG = paint_palm_crown_image();
	
	// statische Variablen
	public static boolean
		background_moves;		// = true: bewegter Hintergrund
	   
    private static int
    	bgObject_selection = TOTAL_FREQUENCY,	   	
    	ground_factor = 3,			// legt fest, wie viele Objekte erscheinen (auf Wüste weniger als auf anderem Boden)
    	mutual_exclusion_factor,	// sorgt dafür, dass an der selben Stelle nicht Wüste und Berg gleichzeitig auftreten können
    	
    	// Timmer zur Sicherstellung eines zeitlichen Mindestabstand zwischen 2 Hintergrundobjekten
    	generalObject_timer,	
    	cactus_timer,
    	stone_timer,
    	hill_timer;
    
    private static float
    	cloud_x = 135;				// x-Koordinate der Wolke
    
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
    
               
    private static BufferedImage [] paint_cactus_image()
    {
    	Graphics2D g2d;
    	GradientPaint[] myGradientColor = new GradientPaint[3];
    	BufferedImage[] cactus_image = new BufferedImage[2];
    	for(int i = 0; i < 2; i++)
    	{
    		cactus_image[i] = new BufferedImage(52, 197, BufferedImage.TYPE_INT_ARGB);
    		g2d =  (Graphics2D) cactus_image[i].getGraphics();
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
    	return cactus_image;
    }
    
    private static BufferedImage [] paint_palm_crown_image()
    {
    	Graphics2D g2d_palm_crown;
    	BufferedImage[] palm_crown_image = new BufferedImage[2];    	
    	for(int i = 0; i < 2; i++)
    	{
    		palm_crown_image[i] = 	new BufferedImage(209, 27, BufferedImage.TYPE_INT_ARGB);    		
    		g2d_palm_crown = (Graphics2D) palm_crown_image[i].getGraphics();    		
    		g2d_palm_crown.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
												RenderingHints.VALUE_ANTIALIAS_ON);						
			g2d_palm_crown.setPaint(new GradientPaint(	0,  4, MyColor.myGreen[0][i], 
 														0, 17, MyColor.dimColor(MyColor.myGreen[0][i],
 														0.65f), true));
			g2d_palm_crown.fillArc(115, 1, 94, 26, 0, 225);			
			g2d_palm_crown.setPaint(new GradientPaint(	0,  3, MyColor.myGreen[1][i], 
 														0, 13, MyColor.dimColor(MyColor.myGreen[1][i],
 														0.60f), true));
			g2d_palm_crown.fillArc(0, 0 , 125, 21, -45, 225);			
			g2d_palm_crown.setPaint(new GradientPaint(	0, 17, MyColor.myGreen[2][i], 
 														0, 23, MyColor.dimColor(MyColor.myGreen[2][i], 
 														0.65f), true));
			g2d_palm_crown.fillArc(55, 14, 68, 11, -45, 240);
    	}
    	return palm_crown_image;
    }
    
    private void paint_palm_stem_image()
    {
    	for(int i = 0; i < 2; i++)
    	{
    		this.image[i] = new BufferedImage(20, 80 + this.coordOfComponents[0][0] + 6, BufferedImage.TYPE_INT_ARGB);
        	Graphics2D g2d_palm_stem = (Graphics2D)this.image[i].getGraphics();
        	g2d_palm_stem.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
    								RenderingHints.VALUE_ANTIALIAS_ON); 
        	
    		g2d_palm_stem.setPaint( new GradientPaint(	12, 0,  this.myColor[i], 
    			 										23, 0, 	MyColor.dimColor(this.myColor[i], 0.75f), true));
    		this.myColor[i] = null;
    		g2d_palm_stem.fillRect(0, 0, 20, 80 + this.coordOfComponents[0][0]);
    		g2d_palm_stem.fillArc( 0, 73 + this.coordOfComponents[0][0], 20, 12, 180, 180);	
    	}
    }
          
    private void paint_desert_image()
    {
    	for(int i = 0; i < 2; i++)
    	{
    		this.image[i] = new BufferedImage(this.width, 35, BufferedImage.TYPE_INT_ARGB);
        	Graphics2D g2d_desert = (Graphics2D) this.image[i] .getGraphics();
        	g2d_desert.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
										RenderingHints.VALUE_ANTIALIAS_ON); 
        	g2d_desert.setPaint(new GradientPaint(	 0 + this.x,  0, MyColor.sand[i], 
													10 + this.x, 20, MyColor.dimColor(MyColor.sand[i], 0.9f), true));
        	g2d_desert.fillArc(0, -35, 300, 70, 180, 90);
        	g2d_desert.fillRect(149, 0, this.width - 298, 35); 
        	g2d_desert.fillArc(this.width - 300, -35, 300, 70, 270, 90);
    	}
    }
    
    private void paint(Graphics2D g2d)
    {
    	// Kaktus
        if(this.type == CACTUS)
        {        	
        	g2d.drawImage(CACTUS_IMG[Events.timeOfDay], (int) this.x, 235, null);
        }
        
        // Steine
        if(this.type == STONE)
        { 
        	g2d.setPaint(MyColor.gradientStones[Events.timeOfDay]);
            g2d.fillOval( (int)(this.x + this.coordOfComponents[0][0]), 
            			  400 + this.coordOfComponents[0][1],
            			  this.coordOfComponents[0][2], 
            			  this.coordOfComponents[0][3]);
        }
        
        // Berge im Hintergrund in Bodenfarbe
        if(this.type == HILL)
        {        	
        	g2d.setPaint(MyColor.gradientHills[Events.timeOfDay]);
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
        	g2d.drawImage(this.image[Events.timeOfDay], (int)this.x, 426, null);
        }
        
        // Palme
        if(this.type == PALM)
        {
        	g2d.drawImage( this.image[Events.timeOfDay], (int)(this.x + 110), 
        				   350 - this.coordOfComponents[0][0], null);
        	g2d.drawImage( PALM_CROWN_IMG[Events.timeOfDay], (int)this.x, 
        				   340 - this.coordOfComponents[0][0], null);        	
        }
    }    
    
    private void preset()
    {        
    	this.x = Main.VIRTUAL_DIMENSION.width + 25;
    	
    	// Sicherstellen, dass zwischen dem Erscheinen von zwei Kakteen bzw. 
    	// zwei Steinen eine gewisse Mindestzeit vergangen ist
        int random = (cactus_timer > 0 ? CACTUS_FREQUENCY : 0) 
        			 + MyMath.random(
        					 bgObject_selection 
        					 - (cactus_timer > 0 ? CACTUS_FREQUENCY : 0) 
        					 - (stone_timer  > 0 ? STONE_FREQUENCY  : 0) );
        if(stone_timer > 0 && random >= CACTUS_FREQUENCY)
        {
        	random += STONE_FREQUENCY;
        }    
        
        // Auswahl des Modells anhand der ermittelten Zufallszahl
        if( random >= 0 && random < CACTUS_FREQUENCY)
        {           
            // Kaktus
        	this.type = CACTUS; 
            this.width = 50;
            this.plane = MyMath.random_direction();
            cactus_timer = 125;
        }                
        else if( random >= CACTUS_FREQUENCY && random < UP_TO_STONE_FREQUENCY)
        {   
        	// Steine
        	if(random < UP_TO_STONE_FREQUENCY - STONE_FREQUENCY/2 && !(hill_timer > 0)){this.plane = -1;}
        	else{this.plane = 2;}                      
            this.type = STONE;            
            this.coordOfComponents[0][0] = 0; 					   // position (x)            
            this.coordOfComponents[0][3] = 35; 					   // width
            this.coordOfComponents[0][2] = 50 + MyMath.random(75); // height
            this.coordOfComponents[0][1] = 12;  				   // position (y)
            this.width = 125 + this.coordOfComponents[0][2];
            stone_timer = 75;
        } 
        else if( random >= UP_TO_STONE_FREQUENCY && random < UP_TO_PALM_FREQUENCY)
        { 
            // Palme
        	this.type = PALM; 
            this.width = 225;
            if(MyMath.toss_up()){this.plane = 1;}
            else{this.plane = -1;}                      
            int a, b, c;
            a = 184 + MyMath.random(12);
            b = 150 + MyMath.random(12);
            c = 104 + MyMath.random(12);
            this.myColor[1] = new Color(a, b,c);
            this.myColor[0] = MyColor.dimColor(this.myColor[1], MyColor.NIGHT_DIM_FACTOR);
            this.coordOfComponents[0][0] = 20 + MyMath.random(70);
            cactus_timer = 140;            
            this.paint_palm_stem_image();
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
            bgObject_selection = UP_TO_HILL_FREQUENCY;
            if(mutual_exclusion_factor < this.width/2)
            {
            	mutual_exclusion_factor = this.width/2;
            } 
            if(hill_timer < this.width/2){hill_timer = this.width/2;}            
        }
        else if( random >= UP_TO_HILL_FREQUENCY && random < TOTAL_FREQUENCY)
        {            
            // Sand
        	this.type = DESERT;
            this.plane = 0;
            this.width = 600 + MyMath.random(400);            
            ground_factor = 17;
            if(mutual_exclusion_factor < this.width/2)
            {
            	mutual_exclusion_factor = this.width/2;
            }
            generalObject_timer = 0;
            bgObject_selection = UP_TO_PALM_FREQUENCY;
            this.paint_desert_image();
        }        
    }
    
    private static void paintBackground(Graphics2D g2d)
    {
    	// Sonne bzw. Mond
    	int corona_radius_increase = 0;
        if(Events.timeOfDay == NIGHT) {g2d.setColor(MyColor.lighterYellow); }
        else
        {
        	g2d.setColor(MyColor.randomLight);
        	corona_radius_increase = (MyColor.randomSunlightBlue - 175)/20;
        }      
        g2d.fillOval(865, 30, 60, 60);
        g2d.setColor(MyColor.translucentSun);  
        g2d.setStroke(new BasicStroke(35));
        g2d.drawOval(855-corona_radius_increase, 20-corona_radius_increase, 
        			 80+2*corona_radius_increase, 80+2*corona_radius_increase);
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
        g2d.setPaint(MyColor.gradientCloud[Events.timeOfDay]);
        g2d.fillOval((int) cloud_x      , 51,  82, 45);                           
        g2d.fillOval((int)(cloud_x + 41), 63, 150, 60);                               
        g2d.fillOval((int)(cloud_x + 68), 40,  60, 53); 
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
        ground_factor = 17;     
        mutual_exclusion_factor = this.width/2;
        generalObject_timer = 0;
        bgObject_selection = UP_TO_PALM_FREQUENCY;      
        this.paint_desert_image();
    }    
    
    public static void reset(ArrayList<LinkedList<BackgroundObject>> bgObject)
    {
    	bgObject.get(INACTIVE).addAll(bgObject.get(ACTIVE));
    	bgObject.get(ACTIVE).clear();   	
    	Iterator<BackgroundObject> i = bgObject.get(INACTIVE).iterator();
    	BackgroundObject bgo;
		if(i.hasNext()){bgo = i.next(); i.remove();}	
		else{bgo = new BackgroundObject();}
		bgo.makeFirstCactus();
		bgObject.get(ACTIVE).add(bgo);
		if(i.hasNext()){bgo = i.next(); i.remove();}	
		else{bgo = new BackgroundObject();}
		bgo.makeFirstHill();		
		bgObject.get(ACTIVE).add(bgo);		
		if(i.hasNext()){bgo = i.next(); i.remove();}	
		else{bgo = new BackgroundObject();}
		bgo.makeFirstDesert();		
		bgObject.get(ACTIVE).add(bgo);
		cloud_x = 135;
    }
    
    public static void initialize(ArrayList<LinkedList<BackgroundObject>> bgObject)
    {    	
    	BackgroundObject firstCactus = new BackgroundObject();
    	firstCactus.makeFirstCactus();
    	bgObject.get(ACTIVE).add(firstCactus);
    	BackgroundObject firstHill = new BackgroundObject();
    	firstHill.makeFirstHill();
    	bgObject.get(ACTIVE).add(firstHill);
    	BackgroundObject firstDesert = new BackgroundObject();
    	firstDesert.makeFirstDesert();
    	bgObject.get(ACTIVE).add(firstDesert);
    	initialize_stars();    	    	
    	paint_cactus_image();    
    	paint_palm_crown_image();
    }
    
    private static void initialize_stars()
    {
    	for(int i = 0; i < NR_OF_STARS; i++)
		{
			STARS[0][i] = MyMath.random(982);
			STARS[1][i] = MyMath.random(GROUND_Y);
		}
    }
    
    private static void moveCloud()
    {    	
    	cloud_x -= background_moves ? 0.5f : 0.125f;
		if(cloud_x < -250){cloud_x = 1000;}
    }
    
    private static void updateBackgroundTimer()
    {
    	if(mutual_exclusion_factor == 1 )
		{
			bgObject_selection = TOTAL_FREQUENCY;
			ground_factor = 3;
		}
		if(mutual_exclusion_factor > 0 ){mutual_exclusion_factor--;}
		if(hill_timer > 0 ){hill_timer--;}
		if(generalObject_timer > 0){generalObject_timer--;}			
		if(cactus_timer > 0){cactus_timer--;}
		if(stone_timer > 0){stone_timer--;}
    }
    
    private void clearImage()
    {
    	this.image[0] = null; 
        this.image[1] = null;
    }

	public static void paintBackground(Graphics2D g2d,
									   ArrayList<LinkedList<BackgroundObject>> bgObject)
	{		
		paintBackground(g2d);
		paint_bgObjects(g2d, bgObject);		
	}

	public static void update(Controller controller,
							  ArrayList<LinkedList<BackgroundObject>> bgObject)
	{		
		background_moves = isBackgroundMoving(controller.enemy, controller.getHelicopter());
		for(Iterator<BackgroundObject> i = bgObject.get(ACTIVE).iterator(); i.hasNext();)
		{
			BackgroundObject bgo = i.next();
			if(background_moves){bgo.x -= BG_SPEED;}
			if(bgo.x < -(bgo.width + 50))
			{
				bgo.clearImage();
				i.remove();
				bgObject.get(INACTIVE).add(bgo);
			}
		}		
		moveCloud();
		generate_new_bgObjects(bgObject);					
		if(background_moves){
            updateBackgroundTimer();}
	}

	private static boolean isBackgroundMoving(ArrayList<LinkedList<Enemy>> enemy, Helicopter helicopter)
	{
		return helicopter.rotor_system_active
				&& !isMajorBossActive(enemy)
				&& helicopter.tractor == null;
	}
	
	private static boolean isMajorBossActive(ArrayList<LinkedList<Enemy>> enemy)
    {
    	return    !enemy.get(ACTIVE).isEmpty()
				&& enemy.get(ACTIVE).getFirst().isMajorBoss();
	}

	private static void	paint_bgObjects(Graphics2D g2d, ArrayList<LinkedList<BackgroundObject>> bgObject)
	{
        for (BackgroundObject bgo : bgObject.get(ACTIVE))
        {
            if (bgo.plane == -1)
            {
                bgo.paint(g2d);
            }
        }
	}

	private static void generate_new_bgObjects(	ArrayList<LinkedList<BackgroundObject>> bgObject)
	{
		int nr_of_bgObjects = bgObject.get(ACTIVE).size();
		if( nr_of_bgObjects < 20 && MyMath.creation_probability( 20 - nr_of_bgObjects,
													ground_factor) &&
			generalObject_timer == 0 && background_moves)
		{
			generalObject_timer = 20;
			Iterator<BackgroundObject> i = bgObject.get(INACTIVE).iterator();
	    	BackgroundObject bgo;
			if(i.hasNext()){bgo = i.next(); i.remove();}	
			else{bgo = new BackgroundObject();}
			bgo.preset();
			bgObject.get(ACTIVE).add(bgo);
		}
	}

	public static void paintForeground(Graphics2D g2d, ArrayList<LinkedList<BackgroundObject>> bgObject)
	{
		// der Boden
		g2d.setPaint(MyColor.gradientGround[Events.timeOfDay]);
        g2d.fillRect(0, GROUND_Y, Main.VIRTUAL_DIMENSION.width, 35);
        
        // Objekte vor dem Helikopter
    	for(int j = 0; j < 3; j++)
		{
            for (BackgroundObject bgo : bgObject.get(ACTIVE))
            {
                if (bgo.plane == j)
                {
                    bgo.paint(g2d);
                }
            }
		}
	}
}