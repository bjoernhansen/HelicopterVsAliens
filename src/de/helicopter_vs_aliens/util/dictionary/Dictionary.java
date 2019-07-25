package de.helicopter_vs_aliens.util.dictionary;

import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static de.helicopter_vs_aliens.util.dictionary.Languages.ENGLISH;


public final class Dictionary
{
    private static final String
        FILENAME_PREFIX = "inGameText_",
        FILENAME_EXTENSION = ".properties";
    
    private final Properties
        defaultLanguageProperties = new Properties(),
        languageProperties = new Properties(defaultLanguageProperties);
        
    private Languages
        language;
    
    private HelicopterTypes
        helicopterType = HelicopterTypes.getDefault();

    private List<String>
        specialUpgrades = new ArrayList<>(5);


    public Dictionary(Languages language, HelicopterTypes helicopterType)
    {
        loadDefaultLanguageProperties();
        this.setLanguage(language);
        this.setHelicopterType(helicopterType);
    }

    private void loadDefaultLanguageProperties()
    {
        loadLanguageProperties(ENGLISH, defaultLanguageProperties);
    }

    private void reloadLanguageProperties()
    {
        loadLanguageProperties(this.language, languageProperties);
    }

    private void loadLanguageProperties(Languages language, Properties properties)
    {
        try
        {
            String filename = getFilename(language);
            URL url = Dictionary.class.getResource(filename);
            properties.load(url.openStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void accountForLanguageChange()
    {
        specialUpgrades.clear();
        specialUpgrades.add(this.languageProperties.getProperty("upgrades.special.spotlight"));
        specialUpgrades.add(this.languageProperties.getProperty("upgrades.special.goliath"));
        specialUpgrades.add(this.languageProperties.getProperty("upgrades.special.warheads"));
        specialUpgrades.add(this.languageProperties.getProperty("upgrades.special.secondcannon"));
        accountForHelicopterChange();
    }
    
    private void accountForHelicopterChange()
    {
        specialUpgrades.set(4, this.languageProperties.getProperty("upgrades.special.fifth." + this.helicopterType.getSpecialUpgrade()));
    }
    

    public void setLanguage(Languages language)
    {
        if(this.language != language)
        {
            this.language = language;
            reloadLanguageProperties();
            accountForLanguageChange();
        }
    }

    public void setHelicopterType(HelicopterTypes helicopterType)
    {
        if(this.helicopterType != helicopterType)
        {
            this.helicopterType = helicopterType;
            accountForHelicopterChange();
        }
    }

    public List<String> getSpecialUpgrades()
    {
        return specialUpgrades;
    }

    public String getWord(String key)
    {
        return this.languageProperties.getProperty(key);
    }
    
    private String getFilename(Languages language)
    {
        return FILENAME_PREFIX + language.getCode() + FILENAME_EXTENSION;
    }
}