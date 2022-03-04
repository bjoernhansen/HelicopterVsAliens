package de.helicopter_vs_aliens.gui.button;


import de.helicopter_vs_aliens.gui.window.Window;

import java.awt.Point;
import java.util.List;
import java.util.function.Supplier;

public enum LeftSideRepairShopButtonType implements ButtonSpecifier
{
    REPAIR(ButtonCategory.REPAIR, 287, Window.dictionary::repair),
    MISSION(ButtonCategory.MISSION, 395, Window.dictionary::mission);
    
    
    private static final List<ButtonSpecifier>
        VALUES = List.of(REPAIR, MISSION);
    
    static final int
        OFFSET_X = 23;
    
    private final ButtonCategory
        category;
    
    private final Point
        coordinates;
    
    private final Supplier<String>
        labelSupplier;
    
    
    LeftSideRepairShopButtonType(ButtonCategory category, int offsetY, Supplier<String> labelSupplier)
    {
        this.category = category;
        this.coordinates = new Point(OFFSET_X, offsetY);
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
