package de.helicopter_vs_aliens.gui.button;

import java.awt.Dimension;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum ButtonCategory
{
    START_SCREEN(211, 30, ButtonSubcategory.TRANSLUCENT),
    START_SCREEN_MENU(150, 30,ButtonSubcategory.TRANSLUCENT),
    START_SCREEN_MENU_CANCEL(150, 30,ButtonSubcategory.TRANSLUCENT),
    MAIN_MENU(211, 35, ButtonSubcategory.STANDARD),
    GROUND(121, 25,ButtonSubcategory.STANDARD),
    STANDARD_UPRADE(193, 50, ButtonSubcategory.PURCHASE),
    SPECIAL_UPGRADE (184, 50, ButtonSubcategory.PURCHASE),
    MISSION(205, 50,ButtonSubcategory.TRANSLUCENT),
    REPAIR(205, 50, ButtonSubcategory.PURCHASE);
    
    
    private static final Set<ButtonCategory>
        BUTTONS_WITH_SECONDARY_LABEL = Collections.unmodifiableSet(EnumSet.range(STANDARD_UPRADE, REPAIR));
    
 
    private final boolean
        isPurchaseButton,
        isTranslucent;
    
    private final Dimension
        dimension;
    
    private enum ButtonSubcategory
    {
        STANDARD,       // not translucent 
        TRANSLUCENT,    
        PURCHASE        // always translucent
    }
    
    ButtonCategory(int width, int height, ButtonSubcategory subcategory)
    {
        this.dimension = new Dimension(width, height);
        this.isPurchaseButton = subcategory == ButtonSubcategory.PURCHASE;
        this.isTranslucent = subcategory != ButtonSubcategory.STANDARD;
    }
    
    public int getWidth()
    {
        return dimension.width;
    }
    
    public int getHeight()
    {
        return dimension.height;
    }
    
    public boolean isPurchaseButton()
    {
        return isPurchaseButton;
    }
    
    public boolean isTranslucent()
    {
        return isTranslucent;
    }
    
    public boolean canHaveSecondaryLabel()
    {
        return BUTTONS_WITH_SECONDARY_LABEL.contains(this);
    }
}
