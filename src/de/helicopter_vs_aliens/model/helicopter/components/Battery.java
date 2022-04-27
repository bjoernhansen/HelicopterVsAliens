package de.helicopter_vs_aliens.model.helicopter.components;

import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.util.Calculations;

import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;


public class Battery
{
    private static final int
        START_ENERGY = 150;
    
    public static final float []
        REGENERATION = {0.030f, 0.036f, 0.044f, 0.053f, 0.063f, 0.076f, 0.092f, 0.111f, 0.134f, 0.162f};
    
    private final int
            maximumUpgradeLevel;
    
    private int
        upgradeLevel;
    
    private float
        currentCharge,
        capacity,
        regenerationRate;  // Energiezuwachs pro Simulationsschritt

    private final HelicopterType
        helicopterType;


    public static Battery createFor(HelicopterType helicopterType)
    {
        return new Battery(helicopterType);
    }

    private Battery(HelicopterType helicopterType)
    {
        this.helicopterType = helicopterType;
        int initialUpgradeLevel = this.helicopterType.getInitialUpgradeLevelFor(ENERGY_ABILITY);
        this.maximumUpgradeLevel = this.helicopterType.getMaximumUpgradeLevelFor(ENERGY_ABILITY);
        this.upgradeTo(initialUpgradeLevel);
        this.currentCharge = this.capacity;
    }
    
    private static float getMaximumCapacity(int upgradeLevel)
    {
        return START_ENERGY + ENERGY_ABILITY.getMagnitude(upgradeLevel);
    }
    
    public void upgradeTo(int newUpgradeLevel)
    {
        int previousUpgradeLevel = this.upgradeLevel;
        this.upgradeLevel = Calculations.constrainToRange(newUpgradeLevel, this.upgradeLevel, this.maximumUpgradeLevel);
        this.capacity = getMaximumCapacity(upgradeLevel);
        this.regenerationRate = regeneration(upgradeLevel);
        float energyBoost = this.capacity - Battery.getMaximumCapacity(previousUpgradeLevel);
        this.recharge(energyBoost);
    }
   
    public static float regeneration(int n)
    {
        if(n >= 1 && n <= 10){return REGENERATION[n-1];}
        return 0;
    }

    public void discharge()
    {
        this.currentCharge = 0;
    }
    
    public void restore()
    {
        this.currentCharge = this.capacity;
    }

    public float getCurrentCharge()
    {
        return this.currentCharge;
    }

    public void setCurrentCharge(float currentCharge)
    {
        this.currentCharge = Calculations.constrainToRange(currentCharge, 0, this.capacity);
    }
    
    public float getCapacity()
    {
        return this.capacity;
    }
    
    public void recharge(float energyBoost)
    {
        this.setCurrentCharge(this.currentCharge + energyBoost);
    }
    
    public void recharge()
    {
        this.setCurrentCharge(this.currentCharge + this.regenerationRate);
    }
    
    public float getMissingEnergy()
    {
        return this.capacity - this.currentCharge;
    }
    
    public void drain(float energyConsumption)
    {
        this.recharge(-energyConsumption);
    }
    
    public boolean isDischarged()
    {
        return this.currentCharge <= 0;
    }
    
    public float getStateOfCharge()
    {
        return this.currentCharge / this.capacity;
    }
    
    public float getRegenerationRate()
    {
        return regenerationRate;
    }
    
    public void boostCharge()
    {
        float energyBoost = Math.max(10, 2*this.getMissingEnergy()/3);
        this.recharge(energyBoost);
    }
    
    @Override
    public String toString()
    {
        return String.format(
                "Battery(%.2f/%.2f) of %s",
                this.currentCharge,
                this.capacity,
                Window.dictionary.helicopterName());
    }
}