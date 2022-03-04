package de.helicopter_vs_aliens.gui.button;


import de.helicopter_vs_aliens.gui.window.Window;

import java.awt.Point;
import java.util.List;

public enum StartScreenButtonType implements ButtonSpecifier
{
    INFORMATION(0, 0, "informations"),
    HIGH_SCORE(0, 1, "highscore"),
    CONTACT(0,2, "contact"),
    SETTINGS(1,0, "settings"),
    RESUME_LAST_GAME(1,1, "resumeLastGame"),
    QUIT(1,2, "quit");
    
    
    private static final List<ButtonSpecifier>
        VALUES = List.of(values());
    
    private static final ButtonCategory
        CATEGORY = ButtonCategory.START_SCREEN;
    
    private static final String
        DICTIONARY_KEY_PREFIX = "buttonLabel.startScreen.";
    
    private static final Point
        OFFSET = new Point( 27, 110),
        BUTTON_DISTANCE = new Point(750,  40);
    
    static
    {
        VALUES.forEach(buttonSpecifier -> {
            StartScreenButtonType buttonType = (StartScreenButtonType)buttonSpecifier;
            buttonType.coordinates
                = new Point(OFFSET.x + buttonType.indizes.x * BUTTON_DISTANCE.x + Window.START_SCREEN_OFFSET_X,
                OFFSET.y + buttonType.indizes.y * BUTTON_DISTANCE.y);
            buttonType.dictionaryKey = DICTIONARY_KEY_PREFIX + buttonType.dictionaryKeyPostfix;
        });
    }
    
    
    private final Point
        indizes;
    
    private Point
        coordinates;
        
    private final String
        dictionaryKeyPostfix;
    
    private String
        dictionaryKey;
    
    
    StartScreenButtonType(int indexX, int indexY, String dictionaryKeyPostfix)
    {
        this.indizes = new Point(indexX, indexY);
        this.dictionaryKeyPostfix = dictionaryKeyPostfix;
    }
    
    public static List<ButtonSpecifier> getValues()
    {
        return VALUES;
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
    public String getPrimaryLabel()
    {
        return Window.dictionary.startScreenButtonLabel(dictionaryKey);
    }
}
