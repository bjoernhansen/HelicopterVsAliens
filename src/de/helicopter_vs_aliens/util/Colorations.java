package de.helicopter_vs_aliens.util;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.Color;
import java.awt.GradientPaint;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;
import static de.helicopter_vs_aliens.gui.WindowType.REPAIR_SHOP;
import static de.helicopter_vs_aliens.gui.WindowType.SCORE_SCREEN;
import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;

public final class Colorations
{
	// alle Farben, die sich nicht ändern, sollten final sein, alle final colors sollten in Upper-Snake-Case
	public static final int 
		FRAME = 3,
		NOZZLE = 4,
		EYES = 5,
		MAX_VALUE = 255;
	
	 // konstante Dimm-Faktoren
	public static final float 
		NIGHT_DIM_FACTOR = 0.5f,		// Dimm-Faktor für Objekte bei Nacht
		BARRIER_NIGHT_DIM_FACTOR = 0.7f,
		DESTRUCTION_DIM_FACTOR = 0.65f, // Dimm-Faktor für die Farbe zerstörter Gegner
		BLEACH_FACTOR = 0.6f;
		
	// Variablen für die Sonnenanimation
	public static int
        randomSunlightBlue = 190;	// Blauanteil der Lichtfarbe
    
	private static boolean
		lighter = false;			// bestimmt, ob die Lichtfarbe (variableLight) heller oder dunkler wird
       	
	// variable Farben
	public static Color 
		bg = Color.black,		// Hintergrundfarbe
		variableGreen,			// zur Darstellung von Unzerstörbarkeit (Gegner und Helikopter)
		variableRed,			// Färbung der Helikopter-Scheiben, wenn Bonus-Feuerkraft oder - Feuerrate-PowerUp aktiv
		variableBlue,			// nur Orochi-Klasse: Bordkanonenfarbe, wenn Stopp-Rakete aktiviert
		variableYellow,			// zentrale Schrift auf dem StartScreen
		variableMarkedButton,	// Schriftfarbe markierter Buttons (marked = true) 
		randomLight,			// Farbe der Sonne und der Scheinwerfer bei Nacht
		variableWhite,			// Rahmenfarbe markierter Buttons (marked = true); 
		plating;				// Schriftfarbe für Angabe der Panzerung in der Werkstatt
		
	public static GradientPaint
		gradientVariableWhite;    // Schriftfarbe der Überschriften (StartScreen, ScoreScreen, Werkstatt)
    	public static GradientPaint gradientVariableGray;	// Button-Hintergrundfarbe für hervorgehobene (highlighted = true) Buttons
	
	public static final Color[]
		scoreScreen = new Color[4];
	
	// konstante Farben
	public static final Color
		sky  = new Color (157,220,MAX_VALUE),
		pink = new Color (MAX_VALUE, 91,185),
		endlessEnergyViolet = new Color(170, 0, MAX_VALUE),
		
		windowBlue = new Color (30,40,95),
		cloakedBossEye = new Color(MAX_VALUE, 180, 180),
		darkBlue = new Color (0, 0, 80),		 
		green = new Color (65,205,140),
		
		hitPoints = new Color (80, 190, 140),
		shieldingBarrierTurquoise = new Color(122 ,177 ,171),
		cloaked = new Color(79, 110, 128),
		arrowGreen = new Color (0, 170, 0),
		darkArrowGreen = new Color (0, 100, 0),
		darkYellow = new Color (120, 120, 0),	
		golden = new Color (MAX_VALUE, MAX_VALUE, 180),
		lighterYellow = new Color (MAX_VALUE, MAX_VALUE, 225),
		translucentSun = new Color(MAX_VALUE, MAX_VALUE, 0, 20),
		lightOrange = new Color (MAX_VALUE, 210, 0),
	
		orange = new Color(MAX_VALUE, MAX_VALUE/2, 0),
		bleachedOrange = bleach(orange, BLEACH_FACTOR),
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
		translucentWhite = new Color(MAX_VALUE, MAX_VALUE, MAX_VALUE, 35),
		bleachedRed = bleach(Color.red, BLEACH_FACTOR),
		bleachedGreen = Colorations.bleach(Color.green, BLEACH_FACTOR),
		bleachedYellow = Colorations.bleach(Color.yellow, BLEACH_FACTOR),
		bleachedViolet = bleach(Colorations.endlessEnergyViolet, BLEACH_FACTOR),
		bleachedCloaked = Colorations.bleach(Colorations.cloaked, BLEACH_FACTOR),
		detected = new Color(MAX_VALUE, 0, 0, 35),	 // TODO wieso unused?
		INACTIVE_NOZZLE = new Color(MAX_VALUE, 192, 129),
		HS_GREEN = new Color(130, MAX_VALUE, 130),
		HS_RED = new Color(MAX_VALUE, 130, 130),
		MONEY_DISPLAY_NIGHT_RED = new Color (MAX_VALUE, 165, 120);
	
	public static final Color[]
		// Helikopter-Farben und tageszeit-abhängige Standardfarben
		cloud = {Color.DARK_GRAY, Color.white},
		radiation = { new Color(MAX_VALUE, 200, 200, 60),
		              new Color(MAX_VALUE, 200, 200, 90)},
		enhancedRadiation = { new Color(MAX_VALUE, 200, 200, 120),
		                      new Color(MAX_VALUE, 200, 200, 210)},
		shieldColor = { new Color(157, 220, MAX_VALUE, 80),
	                    new Color(0, 0, MAX_VALUE, 40)},
	    sand =   {dimColor( new Color(200,185,120), NIGHT_DIM_FACTOR),
			 			    new Color(200,185,120)},
	    stones = {dimColor( new Color(155,160,125), NIGHT_DIM_FACTOR),
			                new Color(155,160,125)};
	
	public static final Color [][]
	    myGreen = {{dimColor(new Color(65,205,140), NIGHT_DIM_FACTOR),
							 new Color(65,205,140)},
				   {dimColor(new Color(80,230,160), NIGHT_DIM_FACTOR),
							 new Color(80,230,160)},
				   {dimColor(new Color(80,190,140), NIGHT_DIM_FACTOR),
							 new Color(80,190,140)}},
		// TODO enum einführen, der FRAME, NOZZLE, EYES ersetzt und hier entsprechende EnumMap verwenden
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
        cannonHoleGreen = new GradientPaint(0, 0, darkArrowGreen, 0, 0, darkArrowGreen);
    
	// tageszeit-abhängige Gradienten-Farben für Hintergrundobjekte
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
	
	private Colorations() throws Exception
	{
		throw new Exception();
	}
	
	public static Color dimColor(Color color, float dim)
    {
    	int r = Math.min(MAX_VALUE, (int)(color.getRed()*dim));
    	int g = Math.min(MAX_VALUE, (int)(color.getGreen()*dim));
    	int b = Math.min(MAX_VALUE, (int)(color.getBlue()*dim));
    	return new Color(r, g, b, color.getAlpha());
    }
        
    public static Color setAlpha(Color color, int alpha)
    {
    	if(color.getAlpha() == MAX_VALUE && alpha >= MAX_VALUE)
    	{
    		return color;
    	}
    	else if(alpha > 0)
    	{
    		return new Color( color.getRed(), 
    						  color.getGreen(),
    						  color.getBlue(), Math.min(MAX_VALUE, alpha));
    	}
		return new Color(color.getRed(), 
					     color.getGreen(), 
					     color.getBlue(), 0);
    }
	
	public static Color setOpaque(Color color)
	{
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), MAX_VALUE);
	}
    
    public static void calculateVariableGameColors(int counter)
    {    	
    	if(counter%20 > 9)
    	{
    		variableGreen = new Color(28 * (counter%10), MAX_VALUE, 100);
    		variableRed =   new Color(MAX_VALUE, 21 * (counter%10), 21 * (counter%10));
    		variableBlue =  new Color(75 + 7 * (counter%10), 75 + 7 * (counter%10), MAX_VALUE);
    	}
		else
		{
			variableGreen = new Color(MAX_VALUE - 28 * (counter%10), MAX_VALUE, 100);
			variableRed =   new Color(MAX_VALUE, 192 - 21 * (counter%10), 192 - 21 * (counter%10));
			variableBlue =  new Color(75 + 60 - 7 * (counter%10), 75 + 60 - 7 * (counter%10), MAX_VALUE);
		}	
    	if(WindowManager.window == GAME)
    	{
    		randomSunlightBlue += lighter ? 1 : -1;
			if(randomSunlightBlue > 205){lighter = false;}
			else if(randomSunlightBlue < 175){lighter = true;}
			else if(Calculations.tossUp(0.045f)){lighter = !lighter;}
			randomLight = new Color(MAX_VALUE, MAX_VALUE, randomSunlightBlue);
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
		if(WindowManager.window != REPAIR_SHOP)
		{			
			gradientVariableGray = new GradientPaint( - value,  -value, 
													  dimColor(variableWhite, 0.5f),
													  40 - value, 40 - value,
													  dimColor(variableWhite, 0.2f), true);
			if(WindowManager.window == START_SCREEN)
			{
				variableYellow = new Color( 175 + 2 * helligkeit/3, 
										    175 + 2 * helligkeit/3, 
										   	 		  helligkeit);
			}
			if(WindowManager.window != SCORE_SCREEN && WindowManager.window != GAME)
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
    
    public static void updateScoreScreenColors(Helicopter helicopter)
    {       
    	scoreScreen[0] = percentColor(Events.bonusIncomePercentage());
    	scoreScreen[1] = percentColor(Calculations.percentage(helicopter.numberOfEnemiesKilled, helicopter.numberOfEnemiesSeen));
    	scoreScreen[2] = percentColor(Calculations.percentage(helicopter.numberOfMiniBossKilled, helicopter.numberOfMiniBossSeen));
    	scoreScreen[3] = percentColor(Calculations.percentage(helicopter.hitCounter, helicopter.missileCounter));
    }
    
    static Color percentColor(int percentage){return percentColor(((float)percentage)/100);}
	public static Color percentColor(float percentage)
    {
    	float cappedValue = Math.min(1, percentage);
    	return new Color((int)(cappedValue > 0.5
    					 		? MAX_VALUE - 2*(cappedValue-0.5)*MAX_VALUE
    					 		: MAX_VALUE), 
    					 (int)(cappedValue <= 0.5
    					 		? 2 * cappedValue * MAX_VALUE
    					 		: MAX_VALUE), 
    					 0);
    }
	
	public static Color reversedRandomGreen()
    {
    	return new Color(MAX_VALUE - variableGreen.getRed(), MAX_VALUE, 100);
    }
    
    public static Color reversedRandomRed(Color color)
    {
    	return new Color(MAX_VALUE, 192-color.getGreen(), 192-color.getBlue(), color.getAlpha());
    }
    
    
    public static Color brightenUp(Color color)
    {
    	float colorMax = Calculations.max(color.getRed(), color.getGreen(), color.getBlue());
    	float factor = MAX_VALUE/colorMax;
    	return new Color(Math.min(MAX_VALUE, (int)(color.getRed()*factor)), Math.min(MAX_VALUE, (int)(color.getGreen()*factor)),Math.min(MAX_VALUE, (int)(color.getBlue()*factor)));	
    }
    
    public static Color bleach(Color color, float factor)
    {
    	if(factor >= 1f){return Color.white;}
    	else if(factor <= 0f){return color;}
    	    	
    	int r = (int)(color.getRed  () + factor * (MAX_VALUE - color.getRed()));
    	int g = (int)(color.getGreen() + factor * (MAX_VALUE - color.getGreen()));
    	int b = (int)(color.getBlue () + factor * (MAX_VALUE - color.getBlue()));
    	    	 	
    	return new Color(Math.min(MAX_VALUE, r), Math.min(MAX_VALUE, g), Math.min(MAX_VALUE, b));
    }
}