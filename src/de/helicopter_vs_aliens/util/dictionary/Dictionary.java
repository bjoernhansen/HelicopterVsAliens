package de.helicopter_vs_aliens.util.dictionary;

import de.helicopter_vs_aliens.gui.BlockMessage;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.model.helicopter.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static de.helicopter_vs_aliens.gui.BlockMessage.HELICOPTER_NOT_AVAILABLE;
import static de.helicopter_vs_aliens.gui.BlockMessage.HELIOS_NOT_AVAILABLE;
import static de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;
import static de.helicopter_vs_aliens.util.dictionary.Language.ENGLISH;


public final class Dictionary
{
    private static final String
        FILENAME_PREFIX = "inGameText_",
        FILENAME_EXTENSION = ".properties";
    
    private final Properties
        defaultLanguageProperties = new Properties(),
        languageProperties = new Properties(defaultLanguageProperties);

    private Language
        language;
    
    private HelicopterType
        helicopterType = HelicopterType.getDefault();

    private EnumMap<SpecialUpgradeType, String>
        specialUpgrades = new EnumMap<>(SpecialUpgradeType.class);
    
    private EnumMap<StandardUpgradeType, List<String>>
        standardUpgradesImprovements = new EnumMap<>(StandardUpgradeType.class);

    private EnumMap<HelicopterType, String>
        helicopterNames = new EnumMap<>(HelicopterType.class);

    private EnumMap<HelicopterType, List<String>>
        helicopterInfos = new EnumMap<>(HelicopterType.class);
    
    private EnumMap<BlockMessage, String[]>
            blockMessages = new EnumMap<>(BlockMessage.class);
    
    private List <String>
        columnNames = new ArrayList<>(),
        settingOptions = new ArrayList<>();
    
    
    public Dictionary()
    {
        this(Language.getDefault(), HelicopterType.getDefault());
    }

    public Dictionary(Language language, HelicopterType helicopterType)
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

    private void loadLanguageProperties(Language language, Properties properties)
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

    private String getFilename(Language language)
    {
        return FILENAME_PREFIX + language.getCode() + FILENAME_EXTENSION;
    }

    public void switchLanguageTo(Language language)
    {
        if(this.language != language)
        {
            this.language = language;
            reloadLanguageProperties();
            accountForLanguageChange();
        }
    }

    public void switchHelicopterTypeTo(HelicopterType helicopterType)
    {
        if(this.helicopterType != helicopterType)
        {
            this.helicopterType = helicopterType;
            accountForHelicopterChange();
        }
    }

    private void accountForLanguageChange()
    {
        for(SpecialUpgradeType specialUpgradeType : SpecialUpgradeType.getValues())
        {
            if(specialUpgradeType == SpecialUpgradeType.FIFTH_SPECIAL){break;}
            specialUpgrades.put(specialUpgradeType, this.languageProperties.getProperty(specialUpgradeType.getDictionaryKey()));
        }
    
        for(StandardUpgradeType standardUpgradeType: StandardUpgradeType.getValues())
        {
            if(standardUpgradeType == StandardUpgradeType.ENERGY_ABILITY){break;}
            String dictionaryKeyPraefix = standardUpgradeType.getDictionaryKey()+ ".";
            standardUpgradesImprovements.put(standardUpgradeType, getImprovementsStringList(dictionaryKeyPraefix));
        }
        
        for(HelicopterType type : HelicopterType.getValues())
        {
            helicopterNames.put(type, this.languageProperties.getProperty("helicopter." + type.getDesignation() + ".name"));
            List<String> infos = new ArrayList<>();
            for(int i = 1; i <= 3; i++)
            {
                infos.add(this.languageProperties.getProperty("helicopter." + type.getDesignation() + ".infos." + i));
            }
            helicopterInfos.put(type, infos);
        }
    
        for(BlockMessage blockMessage : BlockMessage.getValues())
        {
            String [] message = new String[4];
            for(int i = 1; i <= 4; i++)
            {
                message[i-1]  = this.languageProperties.getProperty(blockMessage.getKey() + i);
            }
            blockMessages.put(blockMessage, message);
        }
        
        columnNames.clear();
        for(int i = 1; i <= Menu.NUMBER_OF_COLUMN_NAMES; i++)
        {
            columnNames.add(this.languageProperties.getProperty("highscore.columnNames." + i));
        }
    
        settingOptions.clear();
        for(int i = 1; i <= Menu.NUMBER_OF_SETTING_OPTIONS; i++)
        {
            settingOptions.add(this.languageProperties.getProperty("settingOption." + i));
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
        return this.languageProperties.getProperty(this.helicopterType.getFifthSpecialDictionaryKey());
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
    
    public String specialUpgrade(SpecialUpgradeType specialUpgradeType)
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
    
    public String toTheRepairShop()
    {
        return this.languageProperties.getProperty("toTheRepairShop");
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
    
    public String credit()
    {
        return this.languageProperties.getProperty("credit");
    }
    
    public String mission()
    {
        return this.languageProperties.getProperty("mission");
    }
    
    public String state()
    {
        return this.languageProperties.getProperty("state");
    }
    
    public String damaged()
    {
        return this.languageProperties.getProperty("state.damaged");
    }
    
    public String developedBy()
    {
        return this.languageProperties.getProperty("developedBy");
    }
    
    public String unavailable()
    {
        return this.languageProperties.getProperty("unavailable");
    }
    
    public String unlocked()
    {
        return this.languageProperties.getProperty("unlocked");
    }
    
    public String pleaseWait()
    {
        return this.languageProperties.getProperty("pleaseWait");
    }
    
    
    public String settingOption(int optionNumber)
    {
        return settingOptions.get(optionNumber);
    }
    
    public String helicopterSelectionRequest()
    {
        return this.languageProperties.getProperty("helicopterSelectionRequest");
    }
    
    public String settings()
    {
        return this.languageProperties.getProperty("settings");
    }
    
    public String stateCondition(boolean damaged)
    {
        return this.languageProperties.getProperty(damaged ? "state.damaged" : "state.ready");
    }
    
    public String currentLevel()
    {
        return this.languageProperties.getProperty("currentLevel");
    }
    
    public List<String> helicopterInfos(HelicopterType type)
    {
        return this.helicopterInfos.get(type);
    }
    
    public String typeName()
    {
        return typeName(helicopterType);
    }
    
    public String typeName(HelicopterType type)
    {
        return this.helicopterName(type) + this.languageProperties.getProperty("type");
    }
    
    public String helicopterName()
    {
        return this.helicopterName(this.helicopterType);
    }
    
    public String helicopterName(HelicopterType type)
    {
        return this.helicopterNames.get(type);
    }
    
    public String priceLevel(PriceLevel priceLevel)
    {
        return this.languageProperties.getProperty(priceLevel.getDictionaryKey());
    }
    
    public List<String> columnNames()
    {
        return this.columnNames;
    }
    
    public List<String> standardUpgradesImprovements(StandardUpgradeType standardUpgradeType)
    {
        return standardUpgradesImprovements.get(standardUpgradeType);
    }
    
    public List<String> energyAbilityImprovements()
    {
        return standardUpgradesImprovements.get(ENERGY_ABILITY);
    }
    
    public String standardUpgradeName(StandardUpgradeType standardUpgradeType)
    {
        return standardUpgradesImprovements.get(standardUpgradeType).get(language.getObjectPosition());
    }
    
    public String [] blockMessage(BlockMessage blockMessage)
    {
        return blockMessages.get(blockMessage);
    }
    
    public String [] helicopterNotAvailable(HelicopterType helicopterType)
    {
        String [] message = {"", "", "", ""};
        switch(helicopterType)
        {
            case HELIOS:
                return this.blockMessage(HELIOS_NOT_AVAILABLE);
            case OROCHI:
            case KAMAITACHI:
            case PEGASUS:
                message[0] = String.format(
                                blockMessages.get(HELICOPTER_NOT_AVAILABLE)[0],
                                this.typeName(helicopterType));
                message[1] = String.format(
                                blockMessages.get(HELICOPTER_NOT_AVAILABLE)[1]);
                message[2] = String.format(
                                blockMessages.get(HELICOPTER_NOT_AVAILABLE)[2],
                                this.helicopterName(helicopterType.getUnlockerTypes().get(0)),
                                this.typeName(helicopterType.getUnlockerTypes().get(1)));
        }
        return message;
    }
}