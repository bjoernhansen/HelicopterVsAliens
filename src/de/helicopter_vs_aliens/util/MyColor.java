package de.helicopter_vs_aliens.util;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;
import java.awt.GradientPaint;

import static de.helicopter_vs_aliens.gui.WindowTypes.*;

public class MyColor
{
	public static final int 
		FRAME = 3,
		NOZZLE = 4,
		EYES = 5;
	
	 // konstante Dimm-Faktoren
	public static final float 
		NIGHT_DIM_FACTOR = 0.5f,		// Dimm-Faktor für Objekte bei Nacht
		BARRIER_NIGHT_DIM_FACTOR = 0.7f,
		DESTRUCTION_DIM_FACTOR = 0.65f;	// Dimm-Faktor für die Farbe zerstörter Gegner
		
	// Variablen für die Sonnenanimation
	public static int
        randomSunlightBlue = 190;	// Blauanteil der Lichtfarbe
    
	private static boolean
		lighter = false;			// bestimmt ob die Lichtfarbe (variableLight) heller oder dunkler wird
       	
	// variable Farben
	public static Color 
		bg = Color.black,		// Hintergrundfarbe
		variableGreen,			// zur Darstellung von Unzerstörbarkeit (Gegner und Helikopter)
		variableRed,			// Färbung der Helikopter-Scheiben, wenn Bonus-Feuerkraft oder - Feuerrate-PowerUp aktiv
		variableBlue,			// nur Orochi-Klasse: Bordkanonenfarbe, wenn Stopp-Rakete aktiviert
		variableYellow,			// zentrale Schrift auf dem Startscreen
		variableMarkedButton,	// Schriftfarbe markierter Buttons (marked = true) 
		randomLight,			// Farbe der Sonne und der Scheinwerfer bei Nacht
		variableWhite,			// Rahmenfarbe markierter Buttons (marked = true); 
		plating;				// Schriftfarbe für Angabe der Panzerung in der Werkstatt
		
	public static GradientPaint
		gradientVariableWhite;    // Schriftfarbe der Überschriften (Startscreen, Scorescreen, Werkstatt)
    	public static GradientPaint gradientVariableGray;	// Button-Hintergrundfarbe für hervorgehobene (higlighted = true) Buttons
	
	public static Color[]
		scorescreen = new Color[4];
	
	// konstante Farben
	public static final Color
		sky  = new Color (157,220,255),
		pink = new Color (255, 91,185),
		endlessEnergyViolet = new Color(170, 0, 255),
		windowBlue = new Color (30,40,95),
		cloakedBossEye = new Color(255, 180, 180),
		darkBlue = new Color (0, 0, 80),		 
		green = new Color (65,205,140),
		hitpoints = new Color (80, 190, 140),	
		arrowGreen = new Color (0, 170, 0),
		darkArrowGreen = new Color (0, 100, 0),
		darkYellow = new Color (120, 120, 0),	
		golden = new Color (255, 255, 180),
		lighterYellow = new Color (255, 255, 225),
		translucentSun = new Color(255, 255, 0, 20),
		lightOrange = new Color (255, 210, 0),
		darkerOrange = new Color(200, 120, 0),
		red = new Color(160, 40, 60),	
		brown = new Color (180,150,100),
		ground = new Color(160,140,114),	
		darkestGray = new Color (60, 60, 60),
		darkGray = new Color (100,100,100),
		gray = new Color (115,115,115),
		translucentBlack = new Color(0, 0, 0, 55),
		translucentDarkestBlack = new Color(0, 0, 0, 0),
		translucentGray = new Color (115,115,115,20),
		enemyGray = new Color (145,145,145),
		lightGray = new Color (160,160,160),
		lighterGray = new Color (180,180,180),
		lightestGray = new Color (210,210,210), 	
		translucentWhite = new Color(255, 255, 255, 35),
		cloaked = new Color(79, 110, 128),
		detected = new Color(255, 0, 0, 35),	
		INACTIVE_NOZZLE = new Color(255, 192, 129),
		HS_GREEN = new Color(130, 255, 130),
		HS_RED = new Color(255, 130, 130),
		MONEY_DISPLAY_NIGHT_RED = new Color (255, 165, 120),
		
	
		// Helikopterfarben und tageszeitabhängie Standardfarben
		cloud[] = {Color.DARK_GRAY, Color.white},
		radiation[] = 	{ new Color(255, 200, 200, 60), 
		              	  new Color(255, 200, 200, 90)},
		enhancedRadiation[] = { new Color(255, 200, 200, 120),
		                        new Color(255, 200, 200, 210)},
		shieldColor[] = { new Color(157, 220, 255, 80), 
	                      new Color(0, 0, 255, 40)},				    
		costsColor[] =  { new Color (130, 255, 130), 
						  new Color (210, 255, 180),
	                      new Color (255, 210,   0), 
	                      new Color (255, 165, 120), 
	                      new Color (255, 115, 105)},	                                      
	    sand[] =   {dimColor(new Color(200,185,120), NIGHT_DIM_FACTOR), 
	                     	  	     new Color(200,185,120)},    
	    stones[] = {dimColor(new Color(155,160,125), NIGHT_DIM_FACTOR),
	                     			 new Color(155,160,125)};                                    
	
	public static final Color [][]
	    myGreen = {{dimColor(new Color(65,205,140), NIGHT_DIM_FACTOR),
							 new Color(65,205,140)},
				   {dimColor(new Color(80,230,160), NIGHT_DIM_FACTOR),
							 new Color(80,230,160)},
				   {dimColor(new Color(80,190,140), NIGHT_DIM_FACTOR),
							 new Color(80,190,140)}},
	    barrierColor = {{dimColor(new Color(140,140,140), BARRIER_NIGHT_DIM_FACTOR),
								  new Color(140,140,140)},
					    {dimColor(new Color(165,165,165), BARRIER_NIGHT_DIM_FACTOR),
								  new Color(165,165,165)},
					    {dimColor(new Color(190,190,190), BARRIER_NIGHT_DIM_FACTOR),
						  		  new Color(190,190,190)},
					    {dimColor(Color.white, BARRIER_NIGHT_DIM_FACTOR),
								  Color.white},
					    {dimColor(INACTIVE_NOZZLE, BARRIER_NIGHT_DIM_FACTOR),
							  	  INACTIVE_NOZZLE},
					    {dimColor(new Color (39,52,123), BARRIER_NIGHT_DIM_FACTOR),
								  new Color (39,52,123)}};
	
	public static final GradientPaint
		cannolHoleGreen = new GradientPaint(0, 0, darkArrowGreen, 0, 0, darkArrowGreen);
    
	// tagezeitabhängige Gradienten-Farben für Hintergrundobjekte
    public static final GradientPaint []
    	gradientGround = { new GradientPaint( 0,  28, dimColor(ground, NIGHT_DIM_FACTOR), 
    										  0,  42, dimColor(ground, NIGHT_DIM_FACTOR * 0.75f), true),
    					   new GradientPaint( 0,  28, ground, 
    							   			  0,  42, dimColor(ground, 0.75f), true)};
    public static final GradientPaint [] gradientHills =  { new GradientPaint( 0,   0, dimColor(ground, NIGHT_DIM_FACTOR),
			  								  0,  20, dimColor(ground, NIGHT_DIM_FACTOR * 0.8f), true),
			  			   new GradientPaint( 0,   0, ground, 0,  20, dimColor(ground, 0.8f), true)};
    public static final GradientPaint [] gradientCloud =  { new GradientPaint( 0,  60, cloud[0],
    										  0, 120, dimColor(cloud[0], 0.40f), true), 
                           new GradientPaint( 0,  60, cloud[1],
                        		   			  0, 120, dimColor(cloud[1], 0.65f), false)};
    public static final GradientPaint [] gradientStones = { new GradientPaint( 0, 412, stones[0],
    										  0, 447, dimColor(stones[0], 0.65f), true),
    					   new GradientPaint( 0,  412, stones[1],
    							   			  0, 447, dimColor(stones[1], 0.65f), true)};
                   
	public static Color dimColor(Color color, float dim)
    {
    	int r = Math.min(255, (int)(color.getRed()*dim));
    	int g = Math.min(255, (int)(color.getGreen()*dim));
    	int b = Math.min(255, (int)(color.getBlue()*dim));
    	return new Color(r, g, b, color.getAlpha());
    }
        
    public static Color setAlpha(Color color, int alpha)
    {
    	if(color.getAlpha() == 255 && alpha >= 255)
    	{
    		return color;
    	}
    	else if(alpha > 0)
    	{
    		return new Color( color.getRed(), 
    						  color.getGreen(),
    						  color.getBlue(), Math.min(255, alpha));
    	}
		return new Color(color.getRed(), 
					     color.getGreen(), 
					     color.getBlue(), 0);
    }

    public static void calculateVariableGameColors(int counter)
    {    	
    	if(counter%20 > 9)
    	{
    		variableGreen = new Color(28 * (counter%10), 255, 100);
    		variableRed =   new Color(255, 21 * (counter%10), 21 * (counter%10));
    		variableBlue =  new Color(75 + 7 * (counter%10), 75 + 7 * (counter%10), 255);
    	}
		else
		{
			variableGreen = new Color(255 - 28 * (counter%10), 255, 100);
			variableRed =   new Color(255, 192 - 21 * (counter%10), 192 - 21 * (counter%10));
			variableBlue =  new Color(75 + 60 - 7 * (counter%10), 75 + 60 - 7 * (counter%10), 255);
		}	
    	if(Menu.window == GAME)
    	{
    		randomSunlightBlue += lighter ? 1 : -1;
			if(randomSunlightBlue > 205){lighter = false;}
			else if(randomSunlightBlue < 175){lighter = true;}
			else if(MyMath.tossUp(0.045f)){lighter = !lighter;}
			randomLight = new Color(255, 255, randomSunlightBlue);
    	}
    }
    
    public static void calculateVariableMenuColors(int counter)
	{
		int value = (3*counter)%240;
		int helligkeit = value > 120 ? 240-value : value;	
				
		variableWhite = new Color( 130 + helligkeit, 
								   130 + helligkeit, 
								   130 + helligkeit);
		gradientVariableWhite = new GradientPaint(value,  value, variableWhite,
												  40 + value, 40 + value,
												  dimColor(variableWhite, 0.55f), true);
		if(Menu.window != REPAIR_SHOP)
		{			
			gradientVariableGray = new GradientPaint( - value,  -value, 
													  dimColor(variableWhite, 0.5f),
													  40 - value, 40 - value,
													  dimColor(variableWhite, 0.2f), true);
			if(Menu.window == STARTSCREEN)
			{
				variableYellow = new Color( 175 + 2 * helligkeit/3, 
										    175 + 2 * helligkeit/3, 
										   	 		  helligkeit);
			}
			if(Menu.window != SCORESCREEN && Menu.window != GAME)
			{					
				variableMarkedButton = new Color( 175 + 2 * helligkeit/3, 
												  135 +     helligkeit, 
												        7 * helligkeit/4);	
			}
		}
		else
		{
			variableWhite = new Color(50 + helligkeit/4, 50 + helligkeit/4, 50 + helligkeit/4);				
			gradientVariableGray = new GradientPaint( -value,  -value, dimColor(variableWhite, 1.2f),
													  40 - value, 40 - value, variableWhite, true);
		}		
	}
    
    public static void updateScorescreenColors(Helicopter helicopter)
    {       
    	scorescreen[0] = percentColor(Events.bonusIncomePercentage());
    	scorescreen[1] = percentColor(MyMath.percentage(helicopter.numberOfEnemiesKilled, helicopter.numberOfEnemiesSeen));
    	scorescreen[2] = percentColor(MyMath.percentage(helicopter.numberOfMiniBossKilled, helicopter.numberOfMiniBossSeen));
    	scorescreen[3] = percentColor(MyMath.percentage(helicopter.hitCounter, helicopter.missileCounter));
    }
    
    static Color percentColor(int percentage){return percentColor(((float)percentage)/100);}
	public static Color percentColor(float percentage)
    {
    	float cappedValue = Math.min(1, percentage);
    	return new Color((int)(cappedValue > 0.5
    					 		? 255 - 2*(cappedValue-0.5)*255
    					 		: 255), 
    					 (int)(cappedValue <= 0.5
    					 		? 2 * cappedValue * 255
    					 		: 255), 
    					 0);
    }
	
	public static Color reversedRandomGreen()
    {
    	return new Color(255 - variableGreen.getRed(), 255, 100);
    }
    
    public static Color reversedRandomRed(Color color)
    {
    	return new Color(255, 192-color.getGreen(), 192-color.getBlue(), color.getAlpha());
    }
    
    
    public static Color brightenUp(Color color)
    {
    	float colorMax = MyMath.max(color.getRed(), color.getGreen(), color.getBlue());
    	float factor = 255/colorMax;
    	return new Color(Math.min(255, (int)(color.getRed()*factor)), Math.min(255, (int)(color.getGreen()*factor)),Math.min(255, (int)(color.getBlue()*factor)));	
    }
    
    public static Color bleach(Color color, float factor)
    {
    	if(factor >= 1f){return Color.white;}
    	else if(factor <= 0f){return color;}
    	    	
    	int r = (int)(color.getRed  () + factor * (255 - color.getRed()));
    	int g = (int)(color.getGreen() + factor * (255 - color.getGreen()));
    	int b = (int)(color.getBlue () + factor * (255 - color.getBlue()));
    	    	 	
    	return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }
}