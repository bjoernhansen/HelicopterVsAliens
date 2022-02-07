package de.helicopter_vs_aliens.util;

import java.awt.geom.Point2D;
import java.util.Random;


public final class Calculations
{
    public final static double
		ROOT05 = Math.sqrt(2)/2.0;
	
	public final static Point2D 
		ZERO_POINT = new Point2D.Float(0,0);
  
	private static int []
		randomOrder = {0, 1, 2, 3, 4};
	
	private static Random
        random = new Random();
    
        
    private Calculations() throws Exception
    {
        throw new Exception();
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
	
	public static boolean tossUp()
    {
        return tossUp(0.5f);
    }
    
	public static boolean tossUp(float n)
	{		
		return Math.random() < n;
	}
	
	public static int randomDirection()
	{
		return tossUp() ? 1 : -1;
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
	
	public static int constrainToRange(int value, int min, int max)
    {
        return Math.max(min, Math.min(value, max));
    }
	
	public static float constrainToRange(float value, float min, float max)
	{
		return Math.max(min, Math.min(value, max));
	}
}