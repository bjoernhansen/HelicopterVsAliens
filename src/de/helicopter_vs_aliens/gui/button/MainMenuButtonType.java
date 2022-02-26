package de.helicopter_vs_aliens.gui.button;

import de.helicopter_vs_aliens.gui.Menu;

import java.awt.Point;
import java.util.List;
import java.util.function.Supplier;

public enum MainMenuButtonType implements ButtonSpecifier
{
    NEW_GAME_1(116, Menu.dictionary::startNewGame),
    STOP_MUSIC(161, Menu.dictionary::audioActivation ),
    NEW_GAME_2(206, Menu.dictionary::quit),
    CANCEL(251, Menu.dictionary::cancel);
    
    
    private static final List<ButtonSpecifier>
        VALUES = List.of(values());
        
    private static final int
        MAIN_MENU_BUTTON_X = 385;
    
    
    private final ButtonCategory
        category;
    
    
    private final Point
        coordinates;
    
    private final Supplier<String>
        labelSupplier;
    
    
    MainMenuButtonType(int offsetY, Supplier<String> labelSupplier)
    {
        this.category = ButtonCategory.MAIN_MENU;
        this.coordinates = new Point(MAIN_MENU_BUTTON_X, offsetY);
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
