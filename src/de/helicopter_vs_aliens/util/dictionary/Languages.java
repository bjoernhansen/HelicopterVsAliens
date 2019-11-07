package de.helicopter_vs_aliens.util.dictionary;


public enum Languages
{
    ENGLISH("en", 1),
    GERMAN("de", 0);
    
    private int objectPosition;
    private String code;

    public static Languages getDefault()
    {
        return ENGLISH;
    }

    Languages(String code, int objectPosition)
    {
        this.code = code;
        this.objectPosition = objectPosition;
    }

    int getObjectPosition()
    {
        return this.objectPosition;
    }
    
    String getCode()
    {
        return this.code;
    }
}