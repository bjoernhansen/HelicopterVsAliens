package de.helicopter_vs_aliens.gui.button;

import java.awt.*;

public enum ButtonCategory
{
    START_SCREEN(211, 30, ButtonSubcategory.TRANSLUCENT),
    START_SCREEN_MENU(150, 30,ButtonSubcategory.TRANSLUCENT),
    START_SCREEN_MENU_CANCEL(150, 30,ButtonSubcategory.TRANSLUCENT),
    MAIN_MENU(211, 35, ButtonSubcategory.STANDARD),
    GROUND(121, 25,ButtonSubcategory.STANDARD),
    STANDARD_UPRADE(193, 50, ButtonSubcategory.COST),
    SPECIAL_UPGRADE (184, 50, ButtonSubcategory.COST),
    MISSION(205, 50,ButtonSubcategory.TRANSLUCENT),
    REPAIR(205, 50, ButtonSubcategory.COST);
    
 
    private final boolean
        isCostButton,
        isTranslucent;
    
    private final Dimension
        dimension;
    
    private enum ButtonSubcategory
    {
        STANDARD,       // not translucent 
        TRANSLUCENT,    
        COST            // always translucent
    }
    
    ButtonCategory(int width, int height, ButtonSubcategory subcategory)
    {
        this.dimension = new Dimension(width, height);
        this.isCostButton = subcategory == ButtonSubcategory.COST;
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
    
    public boolean isCostButton()
    {
        return isCostButton;
    }
    
    public boolean isTranslucent()
    {
        return isTranslucent;
    }
}
