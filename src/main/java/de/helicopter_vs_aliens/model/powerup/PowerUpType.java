package de.helicopter_vs_aliens.model.powerup;

import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;


public enum PowerUpType implements GameEntityFactory<PowerUp>
{
    TRIPLE_DAMAGE(4, Color.magenta, Color.black),
    INVINCIBLE(3, Color.green, Color.yellow),
    UNLIMITED_ENERGY(1, Color.blue, Color.cyan),
    BOOSTED_FIRE_RATE(5, Color.red, Color.orange),
    REPARATION(2, Color.white, Color.red),
    BONUS_INCOME(0, Color.orange, Colorations.golden);
    
    
    private static final List<PowerUpType>
        VALUES = List.of(values());
        
    private static final Set<PowerUpType>
        STATUS_BAR_POWER_UP_TYPES = Set.copyOf(EnumSet.range(TRIPLE_DAMAGE, BOOSTED_FIRE_RATE));
        
    private final int
        menuPosition;
    
    private final Color
        surfaceColor;

    private final Color
        crossColor;
    
    PowerUpType(int menuPosition, Color surfaceColor, Color crossColor)
    {
        this.menuPosition = menuPosition;
        this.surfaceColor = surfaceColor;
        this.crossColor = crossColor;
    }

    public static List<PowerUpType> getValues()
    {
        return VALUES;
    }
    
    public Color getSurfaceColor()
    {
        return surfaceColor;
    }
    
    public Color getCrossColor()
    {
        return crossColor;
    }
    
    public static int valueCount()
    {
        return getValues().size();
    }
    
    public int getMenuPosition()
    {
        return menuPosition;
    }
    
    public static Set<PowerUpType> getStatusBarPowerUpTypes()
    {
        return STATUS_BAR_POWER_UP_TYPES;
    }
    
    @Override
    public PowerUp makeInstance()
    {
        PowerUp powerUp = new PowerUp();
        powerUp.setType(this);
        return powerUp;
    }
    
    @Override
    // TODO eingeschränkter Wildcard-Typ als Rückgabewert sollte immer vermieden werden (siehe Effective Java)
    public Class<? extends PowerUp> getCorrespondingClass()
    {
        return PowerUp.class;
    }
}