package de.helicopter_vs_aliens.gui.button;

import de.helicopter_vs_aliens.gui.WindowType;

import java.awt.Point;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum StartScreenMenuButtonType implements ButtonSpecifier
{
    BUTTON_1(),
    BUTTON_2(),
    BUTTON_3(),
    BUTTON_4(),
    BUTTON_5(),
    BUTTON_6(),
    BUTTON_7(),
    BUTTON_8();
    
    
    private static final List<ButtonSpecifier>
        VALUES = List.of(values());
    
    private static final int
        ROW_COUNT = 2;
    
    private static final Point
        OFFSET = new Point( 23, 370),
        BUTTON_DISTANCE = new Point(160,  40);
    
    private static final Set<StartScreenMenuButtonType>
        FAR_RIGHT_BUTTONS = EnumSet.of(BUTTON_1, BUTTON_2);
    
    static
    {
        VALUES.forEach(buttonSpecifier -> {
            StartScreenMenuButtonType buttonType = (StartScreenMenuButtonType)buttonSpecifier;
            buttonType.coordinates
                = new Point(OFFSET.x + buttonType.indizes.x * BUTTON_DISTANCE.x,
                            OFFSET.y + buttonType.indizes.y * BUTTON_DISTANCE.y);
        });
    }

    private final String
        buttonLabelKeyPostfix;
    
    private Point
        coordinates;
    
    private final Point
        indizes;
        
    
    StartScreenMenuButtonType()
    {
        int index = ordinal();
        this.buttonLabelKeyPostfix = Integer.toString(index);
        this.indizes = new Point(index/ROW_COUNT, index%ROW_COUNT);
    }
    

    public static List<ButtonSpecifier> getValues()
    {
        return VALUES;
    }
    
    
    @Override
    public ButtonCategory getCategory()
    {
        return ButtonCategory.START_SCREEN_MENU;
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
        return "";
    }
    
    
    public String getButtonLabelKey(WindowType associatedWindow)
    {
        return associatedWindow.getButtonLabelKeyPrefix() + buttonLabelKeyPostfix;
    }
    
    public boolean isFarRightButton()
    {
        return FAR_RIGHT_BUTTONS.contains(this);
    }
}
