package de.helicopter_vs_aliens.gui.button;


import de.helicopter_vs_aliens.gui.Menu;

import java.awt.*;
import java.util.List;

public enum StartScreenButtonType implements ButtonSpecifier
{
    INFORMATIONS(0, 0, "informations"),
    HIGHSCORE(0, 1, "highscore"),
    CONTACT(0,2, "contact"),
    SETTINGS(1,0, "settings"),
    RESUME_LAST_GAME(1,1, "resumeLastGame"),
    QUIT(1,2, "quit");
    
    
    private static final ButtonCategory
        CATEGORY = ButtonCategory.START_SCREEN;
    
    private static final String
        DICTIONARY_KEY_PREFIX = "buttonLabel.startScreen.";
    
    private static final Point
        OFFSET = new Point( 27, 110),
        BUTTON_DISTANCE = new Point(750,  40);
        
    private static final List<StartScreenButtonType> VALUES = List.of(values());
    
    private final Point
        indizes;
    
    private Point
        coordinates;
        
    private final String
        key,
        dictionaryKeyPostfix;
    
    private String
        dictionaryKey;
    
    static
    {
        VALUES.forEach(buttonType -> {
            buttonType.coordinates
                = new Point(OFFSET.x + buttonType.indizes.x * BUTTON_DISTANCE.x + Menu.START_SCREEN_OFFSET_X,
                            OFFSET.y + buttonType.indizes.y * BUTTON_DISTANCE.y);
            buttonType.dictionaryKey = DICTIONARY_KEY_PREFIX + buttonType.dictionaryKeyPostfix;
        });
    }
    
    public static List<StartScreenButtonType> getValues()
    {
        return VALUES;
    }
    
    StartScreenButtonType(int indexX, int indexY, String dictionaryKeyPostfix)
    {
        this.indizes = new Point(indexX, indexY);
        this.key = indexX + "" + indexY;
        this.dictionaryKeyPostfix = dictionaryKeyPostfix;
    }
    
    @Override
    public ButtonCategory getCategory()
    {
        return CATEGORY;
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
    public String getLabel()
    {
        return Menu.dictionary.startScreenButtonLabel(dictionaryKey);
    }
    
    @Override
    public String getSecondLabel()
    {
        return "";
    }
    
    public String getKey()
    {
        return key;
    }
}
