package de.helicopter_vs_aliens.util.dictionary;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;


public final class Dictionary
{
    private static final String
        FILENAME_EXTENSION = ".properties";
    
    final Properties
        defaultLanguageProperties = new Properties(),
        languageProperties = new Properties(defaultLanguageProperties);
        
    private Languages language;
    
    
    public Dictionary(Languages language)
    {
        loadDefaultLanguageProperties();
        this.setLanguage(language);
    }
    
    private void loadDefaultLanguageProperties()
    {
        try
        {
            URL defaultLanguageUrl = Dictionary.class.getResource("english.properties");
            defaultLanguageProperties.load(defaultLanguageUrl.openStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void setLanguage(Languages language)
    {
        this.language = language;
        reloadLanguageProperties();
    }
    
    private void reloadLanguageProperties()
    {
        String fileName = this.language.getLowercaseName() + FILENAME_EXTENSION;
        URL url = Dictionary.class.getResource(fileName);
        try
        {
            this.languageProperties.load(url.openStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public String getWord(String key)
    {
        return this.languageProperties.getProperty(key);
    }
}