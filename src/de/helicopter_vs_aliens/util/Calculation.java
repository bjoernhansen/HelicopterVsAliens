package de.helicopter_vs_aliens.util;

import java.awt.geom.Point2D;
import java.util.Random;


// TODO alles auslagern, was nicht direkt mit Mathematik zutun hat
// TODO Klasse umbenennen, vielleicht Calculations

public class Calculation
{
	public final static double
		ROOT05 = Math.sqrt(2)/2.0;
	
	public final static Point2D 
		ZERO_POINT = new Point2D.Float(0,0);
	
	private static final int []
		MISSILE_DRIVE = {13, 16, 19, 22, 25, 28, 31, 34, 37, 40},
		DMG = {2, 3, 6, 10, 17, 28, 46, 75, 122, 198},
		FIRE_RATE = {80, 64, 51, 41, 33, 26, 21, 17, 13, 11, 9, 7, 6, 5, 4},

		OBJECT_ACTIVATION_PROBABILITY = {100, 50, 34, 25, 20, 17, 15, 13, 12, 10, 10, 9, 8, 8, 7};
	
	private static final float[]
		PLASMA_DMG_FACTOR = {3.26f, 3.5f, 3.76f, 4.05f, 4.35f, 4.68f, 5.03f, 5.41f, 5.81f, 6.25f},	// Kamaitachi-Klasse: Faktor, um den sich die Schadenswirkung der Raketen erhöht, wenn diese Plasmaraketen sind
		SPEED = {3f, 3.4f, 3.8f, 4.2f, 4.8f, 5.4f, 6.0f, 6.8f, 7.6f, 8.5f};
	
	private static int []
		randomOrder = {0, 1, 2, 3, 4};
	
	private static Random
        random = new Random();
	
	public static void randomize()
    {        
		int random, temp;
		for(int i = 0; i < randomOrder.length; i++)
		{
			temp = randomOrder[i];
			random = random(randomOrder.length);
			randomOrder[i] = randomOrder[random];
			randomOrder[random] = temp;
		}
    }
	
	public static int getRandomOrderValue(int n)
	{
		if(n < 0 || n >= randomOrder.length){return -1;}
		return randomOrder[n];
	}
	
	public static int random(int valueRange)
    {
        return random.nextInt(valueRange);
    }
	
	public static boolean tossUp(){return tossUp(0.5f);}
	public static boolean tossUp(float n)
	{		
		return Math.random() < n;
	}
	
	public static int randomDirection()
	{
		return tossUp() ? 1 : -1;
	}
	
	public static boolean creationProbability(int difference, int factor)
    {
    	int value; 
    	if(difference > 0 && difference < 16)
    	{
    		value = factor * OBJECT_ACTIVATION_PROBABILITY[difference-1];
    	}
    	else value = 6 * factor;
    	return random(value)==0;
    }
    
	public static float speed(int n)
    {
    	if(n > 0 && n < 11){return SPEED[n-1];}
		return 0;
    }
    
    public static float plasmaDamageFactor(int n)
    {
    	if(n > 0 && n < 11){return PLASMA_DMG_FACTOR[n-1];}
		return 0;
    }
	
	public static int missileDrive(int n)
    {
    	if(n >= 1 && n <= 10){return MISSILE_DRIVE[n-1];}
		return 0;
    }
    
    public static int dmg(int n)
    {
    	if(n > 0 && n < 11){return DMG[n-1];}
		return 0;
    }
	
	public static int fireRate(int n)
    {
    	if(n > 0 && n < 16){return FIRE_RATE[n-1];}
		return 200;
    }    
    
    public static int kills(int level)
    {
        return 5 - 5 * (int)((float)(level-1)/10) + level;
    }
	
	

    public static boolean isEmpty(long[][] intArray)
    {
		for(long[] anIntArray : intArray)
		{
			for (int j = 0; j < intArray[0].length; j++)
			{
				if (anIntArray[j] != 0)
				{
					return false;
				}
			}
		}
    	return true;
    }
    
    static float max(double a, double b, double c)
    {
    	return (float) Math.max(Math.max(a, b), c);
    }

	public static int percentage(int amount, int base)
	{		
		return base > 0 ? 100*amount/base : 0;
	} 
	
	public static double getIntersectionLength(double eMin, double eMax,
                                               double bMin, double bMax)
	{		
		return Math.min(eMax, bMax) - Math.max(eMin, bMin);
	}
}