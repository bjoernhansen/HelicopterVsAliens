package de.helicopter_vs_aliens.gui.button;

import de.helicopter_vs_aliens.gui.Menu;

import java.awt.Point;
import java.util.List;

public enum StartScreenMenuCancelButtonType implements ButtonSpecifier
{
    CANCEL;
    
    private static final Point
        POSITION = new Point(849, 410);
    
    private static final List<ButtonSpecifier>
        VALUES = List.of(CANCEL);
    
    
    public static List<ButtonSpecifier> getValues()
    {
        return VALUES;
    }
    
    
    @Override
    public ButtonCategory getCategory()
    {
        return ButtonCategory.START_SCREEN_MENU_CANCEL;
    }
    
    @Override
    public int getX()
    {
        return POSITION.x;
    }
    
    @Override
    public int getY()
    {
        return POSITION.y;
    }
    
    @Override
    public String getPrimaryLabel()
    {
        return Menu.dictionary.cancel();
    }
}
