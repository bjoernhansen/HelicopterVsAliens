package de.helicopter_vs_aliens.util.dictionary;

import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.gui.PriceLevels;
import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;
import de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeTypes;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;
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
    
    private EnumMap<StandardUpgradeTypes, List<String>>
            standardUpgradesImprovements = new EnumMap<>(StandardUpgradeTypes.class);

    private EnumMap<HelicopterTypes, String>
        helicopterNames = new EnumMap<>(HelicopterTypes.class);

    private EnumMap<HelicopterTypes, List<String>>
        helicopterInfos = new EnumMap<>(HelicopterTypes.class);

    private List <String>
        columnNames = new ArrayList<>();
    
    
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
        for(SpecialUpgradeTypes specialUpgradeType : SpecialUpgradeTypes.values())
        {
            if(specialUpgradeType == SpecialUpgradeTypes.FIFTH_SPECIAL){break;}
            specialUpgrades.put(specialUpgradeType, this.languageProperties.getProperty(specialUpgradeType.getDictionaryKey()));
        }
    
        for(StandardUpgradeTypes standardUpgradeType: StandardUpgradeTypes.getValues())
        {
            if(standardUpgradeType == StandardUpgradeTypes.ENERGY_ABILITY){break;}
            String dictionaryKeyPraefix = standardUpgradeType.getDictionaryKey()+ ".";
            standardUpgradesImprovements.put(standardUpgradeType, getImprovementsStringList(dictionaryKeyPraefix));
        }
        
        for(HelicopterTypes type : HelicopterTypes.getValues())
        {
            helicopterNames.put(type, this.languageProperties.getProperty("helicopter." + type.getDesignation() + ".name"));
            List<String> infos = new ArrayList<>();
            for(int i = 1; i <= 3; i++)
            {
                infos.add(this.languageProperties.getProperty("helicopter." + type.getDesignation() + ".infos." + i));
            }
            helicopterInfos.put(type, infos);
        }
        
        for(int i = 0; i < Menu.NUMBER_OF_COLUMN_NAMES; i++)
        {
            columnNames.add(this.languageProperties.getProperty("highscore.columnNames." + i));
        }
        
        accountForHelicopterChange();
    }

    private void accountForHelicopterChange()
    {
        specialUpgrades.put(FIFTH_SPECIAL, getFifthSpecial());
        standardUpgradesImprovements.put(ENERGY_ABILITY, determineEnergyAbility());
    }
    
    private String getFifthSpecial()
    {
        return this.languageProperties.getProperty(this.helicopterType.getFifthSpecialdictionaryKey());
    }
    
    private List<String> determineEnergyAbility()
    {
        String dictionaryKeyPraefix = String.format("%s.%s.", ENERGY_ABILITY.getDictionaryKey(), helicopterType.getDesignation());
        return getImprovementsStringList(dictionaryKeyPraefix);
    }
    
    private List<String> getImprovementsStringList(String dictionaryKeyPraefix)
    {
        List<String> improvements = new ArrayList<>();
        for(int i = 1; i <= 2; i++)
        {
            improvements.add(this.languageProperties.getProperty(dictionaryKeyPraefix + i));
        }
        return improvements;
    }
    
    public String specialUpgrade(SpecialUpgradeTypes specialUpgradeType)
    {
        return specialUpgrades.get(specialUpgradeType);
    }
    
    public String extraCannons()
    {
        return specialUpgrades.get(EXTRA_CANNONS);
    }
    
    public String fifthSpecial()
    {
        return specialUpgrades.get(FIFTH_SPECIAL);
    }
    
    
    public String thirdCannon()
    {
        return this.languageProperties.getProperty("thirdCannon");
    }
    
    public String secondAndThirdCannon()
    {
        return this.languageProperties.getProperty("secondAndThirdCannon");
    }
    
    public String playingTime()
    {
        return this.languageProperties.getProperty("playingTime");
    }
    
    public String level()
    {
        return this.languageProperties.getProperty("level");
    }
    
    public String repair()
    {
        return this.languageProperties.getProperty("repair");
    }
    
    public String price()
    {
        return this.languageProperties.getProperty("price");
    }
    
    public String repairShop()
    {
        return this.languageProperties.getProperty("repairShop");
    }
    
    public String mainMenu()
    {
        return this.languageProperties.getProperty("mainMenu");
    }
    
    public String startNewGame()
    {
        return this.languageProperties.getProperty("startNewGame");
    }
    
    public String quit()
    {
        return this.languageProperties.getProperty("quit");
    }
    
    public String cancel()
    {
        return this.languageProperties.getProperty("cancel");
    }
    
    public String helicopterName(HelicopterTypes type)
    {
        return this.helicopterNames.get(type);
    }

    public List<String> helicopterInfos(HelicopterTypes type)
    {
        return this.helicopterInfos.get(type);
    }
    
    public String typeName(HelicopterTypes type)
    {
        return this.helicopterName(type) + this.languageProperties.getProperty("type");
    }

    public String priceLevel(PriceLevels priceLevel)
    {
        return this.languageProperties.getProperty(priceLevel.getDictionaryKey());
    }
    
    public List<String> columnNames()
    {
        return this.columnNames;
    }
    
    public List<String> standardUpgradesImprovements(StandardUpgradeTypes standardUpgradeType)
    {
        return standardUpgradesImprovements.get(standardUpgradeType);
    }
    
    public List<String> energyAbilityImprovements()
    {
        return standardUpgradesImprovements.get(ENERGY_ABILITY);
    }
    
    public String standardUpgradeName(StandardUpgradeTypes standardUpgradeType)
    {
        return standardUpgradesImprovements.get(standardUpgradeType).get(language.getObjectPosition());
    }
}