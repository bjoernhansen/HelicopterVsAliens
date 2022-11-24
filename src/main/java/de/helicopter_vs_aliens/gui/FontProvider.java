package de.helicopter_vs_aliens.gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class FontProvider
{
    private final Map<String, Font> fonts = new HashMap<>();


    private Font getFont(int style, int size)
    {
        String key = String.format("%s%s", style, size);
        Font requestedFont = fonts.get(key);
        if(requestedFont == null)
        {
            requestedFont = new Font("Dialog", style,  size);
            fonts.put(key, requestedFont);
        }
        return requestedFont;
    }

    public Font getPlain(int size)
    {
        return getFont(Font.PLAIN, size);
    }

    public Font getBold(int size)
    {
        return getFont(Font.BOLD, size);
    }

    public Font getItalicBold(int size)
    {
        return getFont(Font.ITALIC + Font.BOLD, size);
    }
}