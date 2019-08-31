package de.helicopter_vs_aliens.util.dictionary;

import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;
import de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeTypes;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeTypes.*;
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

    private EnumMap<SpecialUpgradeTypes, String>
        specialUpgrades = new EnumMap<>(SpecialUpgradeTypes.class);

    private EnumMap<HelicopterTypes, String>
        helicopterNames = new EnumMap<>(HelicopterTypes.class);

    private EnumMap<HelicopterTypes, List<String>>
        helicopterInfos = new EnumMap<>(HelicopterTypes.class);


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
        specialUpgrades.put(SPOTLIGHT, this.languageProperties.getProperty("upgrades.special.spotlight"));
        specialUpgrades.put(GOLIATH_PLATING, this.languageProperties.getProperty("upgrades.special.goliath"));
        specialUpgrades.put(PIERCING_WARHEADS, this.languageProperties.getProperty("upgrades.special.warheads"));
        specialUpgrades.put(EXTRA_CANNONS, this.languageProperties.getProperty("upgrades.special.secondCannon"));

        for(HelicopterTypes type : HelicopterTypes.values())
        {
            helicopterNames.put(type, this.languageProperties.getProperty("helicopter." + type.getDesignation() + ".name"));
            List<String> infos = new ArrayList<>();
            for(int i = 1; i <= 3; i++)
            {
                infos.add(this.languageProperties.getProperty("helicopter." + type.getDesignation() + ".infos." + i));
            }
            helicopterInfos.put(type, infos);
        }
        accountForHelicopterChange();
    }

    private void accountForHelicopterChange()
    {
        specialUpgrades.put(FIFTH_SPECIAL, determineFifthSpecial());
    }

    private String determineFifthSpecial()
    {
        return this.languageProperties.getProperty("upgrades.special.fifth." + this.helicopterType.getSpecialUpgrade());
    }

    public Languages getLanguage() {
        return language;
    }
    
    /** Textausgaben **/
    public EnumMap<SpecialUpgradeTypes, String> getSpecialUpgrades()
    {
        return specialUpgrades;
    }
    
    public String getSpecialUpgrade(SpecialUpgradeTypes specialUpgradeType)
    {
        return specialUpgrades.get(specialUpgradeType);
    }

    public String getSpotlight()
    {
        return specialUpgrades.get(SPOTLIGHT);
    }

    public String getGoliathPlating()
    {
        return specialUpgrades.get(GOLIATH_PLATING);
    }

    public String getPiercingWarheads()
    {
        return specialUpgrades.get(PIERCING_WARHEADS);
    }

    public String getSecondCannon()
    {
        return specialUpgrades.get(EXTRA_CANNONS);
    }

    public String getFifthSpecial()
    {
        return specialUpgrades.get(FIFTH_SPECIAL);
    }

    public String getThirdCannon()
    {
        return this.languageProperties.getProperty("thirdCannon");
    }

    public String getHelicopterName(HelicopterTypes type)
    {
        return this.helicopterNames.get(type);
    }

    public List<String> getHelicopterInfos(HelicopterTypes type)
    {
        return this.helicopterInfos.get(type);
    }
    
    public String getTypeName(HelicopterTypes type)
    {
        return this.getHelicopterName(type) + this.languageProperties.getProperty("type");
    }
}