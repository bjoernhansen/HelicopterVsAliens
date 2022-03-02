package de.helicopter_vs_aliens.gui.button;


import de.helicopter_vs_aliens.gui.menu.Menu;

import java.awt.Point;
import java.util.List;
import java.util.function.Supplier;

public enum GroundButtonType implements ButtonSpecifier
{
    REPAIR_SHOP(451, Menu.dictionary::toTheRepairShop),
    MAIN_MENU(897, Menu.dictionary::mainMenu);
    
    
    private static final List<ButtonSpecifier>
        VALUES = List.of(REPAIR_SHOP, MAIN_MENU);
    
    private static final int
        GROUND_BUTTON_Y = 431;
    
    private final ButtonCategory
        category;
    
    private final Point
        coordinates;
    
    private final Supplier<String>
        labelSupplier;
        
    
    GroundButtonType(int offsetX, Supplier<String> labelSupplier)
    {
        this.category = ButtonCategory.GROUND;
        this.coordinates = new Point(offsetX, GROUND_BUTTON_Y);
        this.labelSupplier = labelSupplier;
    }
    
    public static List<ButtonSpecifier> getValues()
    {
        return VALUES;
    }
    
    @Override
    public ButtonCategory getCategory()
    {
        return category;
    }
    
    @Override
    public int getX()
    {
        return coordinates.x;
    }
    
    @Override
    public int getY()
    {
        return coordinates.y;
    }
    
    @Override
    public String getPrimaryLabel()
    {
        return labelSupplier.get();
    }
}
