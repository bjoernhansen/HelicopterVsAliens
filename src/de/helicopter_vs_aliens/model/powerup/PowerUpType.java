package de.helicopter_vs_aliens.model.powerup;

import java.awt.*;

import static de.helicopter_vs_aliens.util.Coloration.golden;
import static java.awt.Color.*;


public enum PowerUpType
{
    TRIPLE_DAMAGE(4),
    INVINCIBLE(3),
    UNLIMITRED_ENERGY(1),
    BOOSTED_FIRE_RATE(5),
    REPARATION(2),
    BONUS_INCOME(0);
    
    
    private static final PowerUpType[]
        defensiveCopyOfValues = values();
        
    private static final Color[]
        surfaceColors = {magenta, green,  blue, red,    white, orange},
        crossColors =   {black,   yellow, cyan, orange, red,   golden};
    
    private final int
        menuPosition;
    
    
    PowerUpType(int menuPosition)
    {
        this.menuPosition = menuPosition;
    }
    
    public static PowerUpType[] getValues()
    {
        return defensiveCopyOfValues;
    }
    
    public Color getSurfaceColor()
    {
        return surfaceColors[this.ordinal()];
    }
    
    public Color getCrossColor()
    {
        return crossColors[this.ordinal()];
    }
    
    public static int size()
    {
        return getValues().length;
    }
    
    public int getMenuPosition()
    {
        return menuPosition;
    }
}