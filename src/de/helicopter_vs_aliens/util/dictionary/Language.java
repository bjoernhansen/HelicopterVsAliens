package de.helicopter_vs_aliens.util.dictionary;


public enum Language
{
    ENGLISH("en", 1),
    GERMAN("de", 0);
    
    private int objectPosition;
    private String code;

    public static Language getDefault()
    {
        return ENGLISH;
    }

    Language(String code, int objectPosition)
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