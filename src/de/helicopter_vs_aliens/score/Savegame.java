package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.util.dictionary.Language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import static de.helicopter_vs_aliens.control.Events.NUMBER_OF_BOSS_LEVEL;

public class Savegame implements Serializable
{
	private static final transient int
		OVERALL_HIGHSCORE_INDEX = 6;
	
	private static final transient String
		FILENAME = "savegame";
	
	String
		currentPlayerName;
	
	private HighscoreEntry[][]
		highscore = new HighscoreEntry[7][10];
	
	public int
		money,
        killsAfterLevelup,
		level,
		maxLevel,
		bonusCounter,
		extraBonusCounter,
		enemiesSeen,
		enemiesKilled,
		miniBossSeen,
		miniBossKilled,
		numberOfCrashes,
		numberOfRepairs,
		missileCounter,
		hitCounter,
		platingDurabilityFactor,
		numberOfCannons;
	
	public long
		playingTime,
		recordTime[][] = new long [HelicopterType.size()][NUMBER_OF_BOSS_LEVEL],
		scorescreenTimes[] = new long [HelicopterType.size()];

	public float
		currentPlating,
		currentEnergy;

	public boolean
		originalResulution,
		standardBackgroundMusic,
		isSoundOn,
        isValid,
		spotlight,
		hasPiercingWarheads,
		hasFifthSpecial,
        wasCreatedThroughCheating,
		reachedLevelTwenty[] = new boolean [HelicopterType.size()];
    
    public Map<StandardUpgradeType, Integer>
            levelsOfStandardUpgrades = new EnumMap<>(StandardUpgradeType.class);
	
	public HelicopterType
		helicopterType;
	
	public Language
		language;

	public TimeOfDay
		timeOfDay;
		
		
	private Savegame()
	{
		this.isValid = false;
		this.currentPlayerName = HighscoreEntry.currentPlayerName;
	}
	
	public static Savegame initialize()
	{
		Savegame output;
		if((new File(FILENAME)).exists())
		{			
			Savegame temp = lastGame();
			
			HighscoreEntry.currentPlayerName = temp.currentPlayerName;
			Menu.setLanguage(temp.language);
			Menu.hasOriginalResulution = temp.originalResulution;
			Audio.standardBackgroundMusic = temp.standardBackgroundMusic && Audio.MICHAEL_MODE;
			Audio.isSoundOn = temp.isSoundOn;
			Events.recordTime = temp.recordTime.clone();
			Events.heliosMaxMoney = Events.getHeliosMaxMoney();
			Events.reachedLevelTwenty = temp.reachedLevelTwenty.clone();
			Events.highscore = temp.highscore.clone();
			output = temp;
		}		
		else{output = new Savegame();}
				
		return output;
	}
	
	private static Savegame lastGame()
	{		
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME)))
		{			
			try
			{
				Object obj = ois.readObject();				
				if(obj instanceof Savegame)
				{
					return (Savegame)obj;
				}	
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		catch ( IOException e ) {e.printStackTrace();}
		return new Savegame();
	}
			
	public void saveToFile(Helicopter helicopter)
	{				
		this.save(helicopter);
		Menu.startscreenButton.get("11").enabled = this.isValid;
		writeToFile();			
	}
		
	
	public void writeToFile()
	{
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME)))
		{
			oos.writeObject(this);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void save(Helicopter helicopter)
	{
		this.currentPlayerName = HighscoreEntry.currentPlayerName;
		this.language = Menu.language;
		this.standardBackgroundMusic = Audio.standardBackgroundMusic;
		this.originalResulution = Menu.hasOriginalResulution;
		this.isSoundOn = Audio.isSoundOn;
		this.money = Events.money;	
		this.killsAfterLevelup = Events.killsAfterLevelUp;
		this.level = Events.level;
		this.maxLevel = Events.maxLevel;
		this.timeOfDay = Events.timeOfDay;
		this.bonusCounter = Events.overallEarnings;
		this.extraBonusCounter = Events.extraBonusCounter;
		this.playingTime = Events.playingTime;
		this.scorescreenTimes = helicopter.scorescreenTimes.clone();
		this.recordTime = Events.recordTime.clone();
		this.reachedLevelTwenty = Events.reachedLevelTwenty.clone();
		this.highscore = Events.highscore.clone();
		this.helicopterType = helicopter.getType();
		this.levelsOfStandardUpgrades = helicopter.getLevelsOfStandardUpgrades();
		this.spotlight = helicopter.hasSpotlights;
		this.platingDurabilityFactor = helicopter.platingDurabilityFactor;
		this.hasPiercingWarheads = helicopter.hasPiercingWarheads;
		this.numberOfCannons = helicopter.numberOfCannons;
		this.hasFifthSpecial = helicopter.hasFifthSpecial();
		this.currentPlating = helicopter.getCurrentPlating();
		this.currentEnergy = helicopter.getCurrentEnergy();
		this.enemiesSeen = helicopter.numberOfEnemiesSeen;
		this.enemiesKilled = helicopter.numberOfEnemiesKilled;
		this.miniBossSeen = helicopter.numberOfMiniBossSeen;
		this.miniBossKilled = helicopter.numberOfMiniBossKilled;
		this.numberOfCrashes = helicopter.numberOfCrashes;
		this.numberOfRepairs = helicopter.numberOfRepairs;
		this.wasCreatedThroughCheating = helicopter.isPlayedWithCheats;
		this.missileCounter = helicopter.missileCounter;
		this.hitCounter = helicopter.hitCounter;
	}	
	
	public void saveInHighscore()
	{
	    if(this.isWorthyForHighscore())
		{				
			HighscoreEntry tempEntry = new HighscoreEntry(this);
			HighscoreEntry.putEntry(Events.highscore[this.helicopterType.ordinal()], tempEntry);
			HighscoreEntry.putEntry(Events.highscore[OVERALL_HIGHSCORE_INDEX], tempEntry);
		}
	}
    
    private boolean isWorthyForHighscore()
    {
        return this.isValid && (!this.wasCreatedThroughCheating || Events.IS_SAVEGAME_SAVED_ANYWAY);
    }
    
    public void becomeValid()
    {
        this.isValid = true;
    }
    
    public void loseValidity()
    {
        this.isValid = false;
    }
}