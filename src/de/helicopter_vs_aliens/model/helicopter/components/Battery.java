package de.helicopter_vs_aliens.model.helicopter.components;

import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.util.Calculation;

import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;

// TODO Battery Klasse fertig stellen
public class Battery
{
    public static final float []
        REGENERATION = {0.030f, 0.036f, 0.044f, 0.053f, 0.063f, 0.076f, 0.092f, 0.111f, 0.134f, 0.162f};

    private int
        upgradeLevel,
        maximumUpgradeLevel;

    private float
        currentCharge,
        capacity;

    public float
        regenerationRate;  // Energiezuwachs pro Simulationsschritt

    HelicopterType
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

    public void upgrade()
    {
        upgradeTo(this.upgradeLevel + 1);
    }

    public void upgradeTo(int newUpgradeLevel)
    {
        this.upgradeLevel = Calculation.constrainToRange(newUpgradeLevel, this.upgradeLevel, this.maximumUpgradeLevel);
        this.update();
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
        this.currentCharge = currentCharge;

    }
}