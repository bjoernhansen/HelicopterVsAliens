package de.helicopter_vs_aliens.util;

import de.helicopter_vs_aliens.Positions;
import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;

import java.awt.geom.Point2D;
import java.util.HashMap;

public class MyMath implements Positions
{
	public final static double
		ROOT05 = Math.sqrt(2)/2;
	
	public final static Point2D 
		ZERO_POINT = new Point2D.Float(0,0);
	
	private static final int 
		START_ENERGY = 150;

	private static final int []
		MISSILE_DRIVE = {13, 16, 19, 22, 25, 28, 31, 34, 37, 40},
		DMG = {2, 3, 6, 10, 17, 28, 46, 75, 122, 198},
		FIRE_RATE = {80, 64, 51, 41, 33, 26, 21, 17, 13, 11, 9, 7, 6, 5, 4},
		ENERGY = {0, 100, 190, 270, 340, 400, 450, 490, 520, 540},
		SHIFT_TIME = {225, 185, 151, 124, 102, 83, 68, 56, 45, 37, 30, 25},
		OBJECT_ACTIVATION_PROBABILITY = {100, 50, 34, 25, 20, 17, 15, 13, 12, 10, 10, 9, 8, 8, 7},
		COST_LEVEL = {500, 2000, 6000, 16000, 36000, 80000, 176000, 368000, 792000};
		
	private static final float
		PLATING_MULTIPLIER = 1.3f;

	private static final float[]
		COST_FACTOR = {0.375f, 0.75f, 1f, 1.5f, 2.5f},
		PLASMA_DMG_FACTOR = {3.26f, 3.5f, 3.76f, 4.05f, 4.35f, 4.68f, 5.03f, 5.41f, 5.81f, 6.25f},	// Kamaitachi-Klasse: Faktor, um den sich die Schadenswirkung der Raketen erhöht, wenn diese Plasmaraketen sind
		SPEED = {3f, 3.4f, 3.8f, 4.2f, 4.8f, 5.4f, 6.0f, 6.8f, 7.6f, 8.5f},
		PLATING = {1.5f, 2.6f, 4.0f, 5.6f, 7.7f, 9.6f, 11.8f, 14.2f, 17.0f, 20f},
		REGENERATION = {0.030f, 0.036f, 0.044f, 0.053f, 0.063f, 0.076f, 0.092f, 0.111f, 0.134f, 0.162f};
	
	private static int []
		random_order = {0, 1, 2, 3, 4};
	
	// Die Kosten mancher Upgrades weichen für manche Helicopterklassen vom Standard ab.
	// Die HashMap "additional_costs" enthält die Modifikationswerte.
	private static final HashMap<String, Integer> 
		ADDITIONAL_COSTS = set_additional_costs();	
	
	private static HashMap<String, Integer> set_additional_costs()
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
		for(int i = 0; i < random_order.length; i++)
		{
			temp = random_order[i];
			random = random(random_order.length);
			random_order[i] = random_order[random];
			random_order[random] = temp;			
		}
    }
	
	public static int get_random_order_value(int n)
	{
		if(n < 0 || n >= random_order.length){return -1;}
		return random_order[n];
	}
	
	public static int random(int value_range)
    {        
        return (int)(Math.random() * value_range);  
    }
	
	public static boolean toss_up(){return toss_up(0.5f);}
	public static boolean toss_up(float n)
	{		
		return Math.random() < n;
	}
	
	public static int random_direction()
	{
		return toss_up() ? 1 : -1;
	}
	
	public static boolean creation_probability(int difference, int factor)
    {
    	int value; 
    	if(difference > 0 && difference < 16)
    	{
    		value = factor * OBJECT_ACTIVATION_PROBABILITY[difference-1];
    	}
    	else value = 6 * factor;
    	return random(value)==0;
    }
	
    private static int increase(int costs, int level)
    {
     	return (int) (COST_FACTOR[costs] * (COST_LEVEL[level-1]));  	
    }
     
    // bestimmt die tatsächlichen Kosten für ein Upgrades unter Berücksichtigung der "additional costs"
	public static int costs(HelicopterTypes helicopterType, int costs, int upgrade_level)
	{
		String key = "" + helicopterType.ordinal() + costs + upgrade_level;
		int extra_costs = 0;
		if(ADDITIONAL_COSTS.containsKey(key))
		{
			extra_costs = ADDITIONAL_COSTS.get(key);
		}
		return increase(costs, upgrade_level) + extra_costs;
	}
	
	public static float speed(int n)
    {
    	if(n > 0 && n < 11){return SPEED[n-1];}
		return 0;
    }
	
	public static float plating(int n)
    {
    	if(n >= 1 && n <= 10){return PLATING_MULTIPLIER * PLATING[n-1];}
		return 0;
    }    
    
    public static float plasma_dmg_factor(int n)
    {
    	if(n > 0 && n < 11){return PLASMA_DMG_FACTOR[n-1];}
		return 0;
    }
	
	public static int missile_drive(int n)
    {
    	if(n >= 1 && n <= 10){return MISSILE_DRIVE[n-1];}
		return 0;
    }
    
    public static int dmg(int n)
    {
    	if(n > 0 && n < 11){return DMG[n-1];}
		return 0;
    }
	
	public static int fire_rate(int n)
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
	
	public static int shift_time(int n)
    {
    	if(n > 1 && n < 14){return SHIFT_TIME[n-2];}
		return 500;
    }
	
	public static int energy(int n)
    {
    	if(n > 0 && n < 11){return START_ENERGY + ENERGY[n-1];}
		return 0;
    }
	
	public static int max_level(int upgrade_costs)
    {
    	return upgrade_costs == 4 ? 6 : upgrade_costs == 3 ? 8 : 10;
    }       
    
    public static boolean is_empty(long[][] int_array)
    {
		for(long[] anInt_array : int_array)
		{
			for (int j = 0; j < int_array[0].length; j++)
			{
				if (anInt_array[j] != 0)
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
	
	public static double get_intersection_length(double e_min, double e_max, 
	                                      double b_min, double b_max)
	{		
		return Math.min(e_max, b_max) - Math.max(e_min, b_min);
	}
	
	
	
	
}