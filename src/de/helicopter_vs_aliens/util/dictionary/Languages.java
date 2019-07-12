package de.helicopter_vs_aliens.util.dictionary;

public enum Languages
{
    ENGLISH("en"),
    GERMAN("de");
    
    private String code;
    
    
    Languages(String code)
    {
        this.code = code;
    }
    
    String getCode()
    {
        return this.code;
    }
}
