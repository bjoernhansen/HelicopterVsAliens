package de.helicopter_vs_aliens.gui.button;

import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType;

import java.awt.Point;
import java.util.List;
import java.util.function.Function;

public enum SpecialUpgradeButtonType implements UpgradeButtonSpecifier
{
    SPOTLIGHT(SpecialUpgradeType.SPOTLIGHT),
    GOLIATH_PLATING(SpecialUpgradeType.GOLIATH_PLATING),
    PIERCING_WARHEADS(SpecialUpgradeType.PIERCING_WARHEADS),
    EXTRA_CANNONS(SpecialUpgradeType.EXTRA_CANNONS),
    FIFTH_SPECIAL(SpecialUpgradeType.FIFTH_SPECIAL);
    
    
    private static final List<ButtonSpecifier>
        VALUES = List.of(values());
    
    private static final Point
        OFFSET = new Point(771, 155);
    
    private static final Function<SpecialUpgradeType, String>
        LABEL_FUNCTION = Window.dictionary::specialUpgrade;
    
    static
    {
        VALUES.forEach(buttonSpecifier -> {
            SpecialUpgradeButtonType buttonType = (SpecialUpgradeButtonType)buttonSpecifier;
            int index = buttonType.ordinal();
            buttonType.y = OFFSET.y + index * UPGRADE_BUTTON_DISTANCE;
        });
    }
    
    
    private final SpecialUpgradeType
        specialUpgradeType;
    
  
    private int
        y;
    
    
    SpecialUpgradeButtonType(SpecialUpgradeType specialUpgradeType)
    {
        this.specialUpgradeType = specialUpgradeType;
    }
    
    public static List<ButtonSpecifier> getValues()
    {
        return VALUES;
    }
    
    
    @Override
    public ButtonCategory getCategory()
    {
        return ButtonCategory.SPECIAL_UPGRADE;
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
        return LABEL_FUNCTION.apply(specialUpgradeType);
    }
    
    public SpecialUpgradeType getSpecialUpgradeType()
    {
        return specialUpgradeType;
    }
}
