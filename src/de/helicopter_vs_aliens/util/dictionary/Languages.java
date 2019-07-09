package de.helicopter_vs_aliens.util.dictionary;

public enum Languages
{
    ENGLISH,
    GERMAN;
    
    String getLowercaseName()
    {
        return this.name().toLowerCase();
    }
}
