package de.helicopter_vs_aliens.gui.button;

import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;

import java.awt.Point;
import java.util.List;
import java.util.function.Function;

public enum StandardUpgradeButtonType implements UpgradeButtonSpecifier
{
    ROTOR_SYSTEM(StandardUpgradeType.ROTOR_SYSTEM),
    MISSILE_DRIVE(StandardUpgradeType.MISSILE_DRIVE),
    PLATING(StandardUpgradeType.PLATING),
    FIREPOWER(StandardUpgradeType.FIREPOWER),
    FIRE_RATE(StandardUpgradeType.FIRE_RATE),
    ENERGY_ABILITY(StandardUpgradeType.ENERGY_ABILITY);
    
    
    private static final List<ButtonSpecifier>
        VALUES = List.of(values());
    
    public static final Point
        OFFSET = new Point(559, 95);
    
    private static final Function<StandardUpgradeType, String>
        LABEL_FUNCTION = Window.dictionary::standardUpgradesImprovements;
    
    static
    {
        VALUES.forEach(buttonSpecifier -> {
            StandardUpgradeButtonType buttonType = (StandardUpgradeButtonType)buttonSpecifier;
            int index = buttonType.ordinal();
            buttonType.y = OFFSET.y + index * UPGRADE_BUTTON_DISTANCE;
        });
    }
    
    private final StandardUpgradeType
        standardUpgradeType;
    
    
    private int
        y;
        
    
    StandardUpgradeButtonType(StandardUpgradeType standardUpgradeType)
    {
        this.standardUpgradeType = standardUpgradeType;
    }
    
    public static List<ButtonSpecifier> getValues()
    {
        return VALUES;
    }
    
    
    @Override
    public ButtonCategory getCategory()
    {
        return ButtonCategory.STANDARD_UPRADE;
    }
    
    @Override
    public int getX()
    {
        return OFFSET.x;
    }
    
    @Override
    public int getY()
    {
        return y;
    }
    
    @Override
    public String getPrimaryLabel()
    {
        return LABEL_FUNCTION.apply(standardUpgradeType);
    }
    
    public StandardUpgradeType getStandardUpgradeType()
    {
        return standardUpgradeType;
    }
}
