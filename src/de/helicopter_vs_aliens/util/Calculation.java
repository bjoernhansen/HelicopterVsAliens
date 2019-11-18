package de.helicopter_vs_aliens.util;

import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Random;

import java.util.Optional;


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
		SPEED = {3f, 3.4f, 3.8f, 4.2f, 4.8f, 5.4f, 6.0f, 6.8f, 7.6f, 8.5f},
		REGENERATION = {0.030f, 0.036f, 0.044f, 0.053f, 0.063f, 0.076f, 0.092f, 0.111f, 0.134f, 0.162f};
	
	private static int []
		randomOrder = {0, 1, 2, 3, 4};
	
	private static Random
        random = new Random();
	
	// Die Kosten mancher Upgrades weichen für manche Helicopterklassen vom Standard ab.
	// Die HashMap "additional_costs" enthält die Modifikationswerte.
    
    // TODO verschieben ins HelicopterTypesEnum
	public static final HashMap<String, Integer>
            ADDITIONAL_STANDARD_UPGRADE_COSTS = setAdditionalCosts();
		
	// TODO Auslagern nach HelicopterTypes
	private static HashMap<String, Integer> setAdditionalCosts()
	{
		HashMap<String, Integer> hashMap = new HashMap<> ();		
		
		// Roch
		hashMap.put("103", -250);    	
		hashMap.put("136", 126000);
	    hashMap.put("137", 502000);
	    hashMap.put("141", 250);
	    hashMap.put("144", 63000);
	    hashMap.put("145", 251000);
	    
	    // Orochi
	    hashMap.put("203", -250); 
	    hashMap.put("231", 250); 
	    hashMap.put("236", 113000);
	    hashMap.put("237", 450000);
	    hashMap.put("241", 250); 
	    hashMap.put("244", 56000);
	    hashMap.put("245", 225000);  
	    
	    // Kamaitachi
	    hashMap.put("331", 250);
	    hashMap.put("336", 40000);
	    hashMap.put("337", 160000);
	    hashMap.put("341", 250);
	    hashMap.put("344", 20000);
	    hashMap.put("345", 80000);   
	    
	    // Pegasus
	    hashMap.put("403", -250);
	    hashMap.put("436", 74000);
	    hashMap.put("437", 299000);
	    hashMap.put("444", 37000);
	    hashMap.put("445", 150000);  
		
	    // Helios
	    
		return hashMap;
	}
	
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
	
	public static float regeneration(int n)
    {
    	if(n >= 1 && n <= 10){return REGENERATION[n-1];}
		return 0;
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