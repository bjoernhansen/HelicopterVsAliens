package de.helicopter_vs_aliens.model.helicopter.components;

public class Battery
{
    public static final float []
            REGENERATION = {0.030f, 0.036f, 0.044f, 0.053f, 0.063f, 0.076f, 0.092f, 0.111f, 0.134f, 0.162f};
    
    int capacity;
    float regenerationRate;
    
    
    
    public static float regeneration(int n)
    {
        if(n >= 1 && n <= 10){return REGENERATION[n-1];}
        return 0;
    }
    
}
