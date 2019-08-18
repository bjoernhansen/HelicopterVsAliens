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


    public Dictionary()
    {
        this(Languages.getDefault(), HelicopterTypes.getDefault());
    }

    public Dictionary(Languages language, HelicopterTypes helicopterType)
    {
        loadDefaultLanguageProperties();
        this.switchLanguageTo(language);
        this.switchHelicopterTypeTo(helicopterType);
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

    private String getFilename(Languages language)
    {
        return FILENAME_PREFIX + language.getCode() + FILENAME_EXTENSION;
    }

    public void switchLanguageTo(Languages language)
    {
        if(this.language != language)
        {
            this.language = language;
            reloadLanguageProperties();
            accountForLanguageChange();
        }
    }

    public void switchHelicopterTypeTo(HelicopterTypes helicopterType)
    {
        if(this.helicopterType != helicopterType)
        {
            this.helicopterType = helicopterType;
            accountForHelicopterChange();
        }
    }

    private void accountForLanguageChange()
    {
        specialUpgrades.clear();
        specialUpgrades.add(this.languageProperties.getProperty("upgrades.special.spotlight"));
        specialUpgrades.add(this.languageProperties.getProperty("upgrades.special.goliath"));
        specialUpgrades.add(this.languageProperties.getProperty("upgrades.special.warheads"));
        specialUpgrades.add(this.languageProperties.getProperty("upgrades.special.secondCannon"));
        specialUpgrades.add(determineFifthSpacial());
    }

    private void accountForHelicopterChange()
    {
        specialUpgrades.set(4, determineFifthSpacial());
    }

    private String determineFifthSpacial()
    {
        return this.languageProperties.getProperty("upgrades.special.fifth." + this.helicopterType.getSpecialUpgrade());
    }


    // Textausgaben

    public List<String> getSpecialUpgrades()
    {
        return specialUpgrades;
    }

    // TODO Enum einführen für Special-Upgrades
    // TODO ggf. eine eigene Klasse für die Transkations einführen, welche dann das Dictionary verwendet

    public String getSpotlight()
    {
        return specialUpgrades.get(0);
    }

    public String getGoliathPlating()
    {
        return specialUpgrades.get(1);
    }

    public String getPiercingWarheads()
    {
        return specialUpgrades.get(2);
    }

    public String getSecondCannon()
    {
        return specialUpgrades.get(3);
    }

    public String getFifthSpecial()
    {
        return specialUpgrades.get(4);
    }

    public String getThirdCannon()
    {
        return this.languageProperties.getProperty("thirdCannon");
    }
}