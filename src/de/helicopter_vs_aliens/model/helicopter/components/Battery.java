package de.helicopter_vs_aliens.model.helicopter.components;

import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;

// TODO Battery Klasse fertig stellen
public class Battery
{
    public static final float []
            REGENERATION = {0.030f, 0.036f, 0.044f, 0.053f, 0.063f, 0.076f, 0.092f, 0.111f, 0.134f, 0.162f};

    private int
        upgradeLevel;

    private float
        capacity,
        regenerationRate;

    HelicopterType
        helicopterType;


    private Battery(HelicopterType helicopterType)
    {
        this(helicopterType, 1);
    }

    private Battery(HelicopterType helicopterType, int upgradeLevel)
    {
        this.helicopterType = helicopterType;
        this.upgradeLevel = upgradeLevel;
        this.update();
    }


    public static Battery createFor(HelicopterType helicopterType)
    {
        return new Battery(helicopterType);
    }



    public void upgrade()
    {
        if(this.upgradeLevel >= this.helicopterType.getPriceLevelFor(ENERGY_ABILITY).getMaxUpgradeLevel())
        {
            this.upgradeLevel++;
            this.update();
        }



    }

    private void update()
    {
        this.capacity = ENERGY_ABILITY.getMagnitude(upgradeLevel);
        this.regenerationRate = regeneration(upgradeLevel);
    }


    public static float regeneration(int n)
    {
        if(n >= 1 && n <= 10){return REGENERATION[n-1];}
        return 0;
    }
    
    
    public void discharge()
    {
        this.capacity = 0;
    }
    
    
    public void restore()
    {
        //this.capacity = this.getMaximumEnergy();
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }
    
    
}
