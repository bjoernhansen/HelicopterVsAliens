package de.helicopter_vs_aliens.util.dictionary;


public enum Language
{
    ENGLISH("English", "en", 1),
    GERMAN( "Deutsch", "de", 0);
    
    
    private final int
        objectPosition;
    
    private final String
        nativeName,
        code;

    
    public static Language getDefault()
    {
        return ENGLISH;
    }

    Language(String nativeName, String code, int objectPosition)
    {
        this.nativeName = nativeName;
        this.code = code;
        this.objectPosition = objectPosition;
    }

    public String getNativeName()
    {
        return this.nativeName;
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