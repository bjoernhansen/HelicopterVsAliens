package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.gui.button.StartScreenButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.util.SizeLimitedTreeSet;
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

// TODO getter und setter einführen. Es sollte nicht alles public sein
public class Savegame implements Serializable
{
	private static final String
		FILENAME = "savegame";

	public boolean
		originalResolution,
		standardBackgroundMusic,
		isSoundOn,
		spotlight,
		hasPiercingWarheads,
		hasFifthSpecial,
		wasCreatedThroughCheating;
	
	// TODO es sollte kein Array verwendet werden
	public boolean[]
		reachedLevelTwenty = new boolean [HelicopterType.size()];
	
	public int
		money,
		killsAfterLevelUp,
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
		playingTime;
	
	// TODO es sollte kein Array verwendet werden
	public long[]
		scoreScreenTimes = new long [HelicopterType.size()];
	
	// TODO es sollte kein Array verwendet werden
	public long[][]
		recordTime = new long [HelicopterType.size()][NUMBER_OF_BOSS_LEVEL];
	
	public float
		currentPlating,
		currentEnergy;
	
	public Map<StandardUpgradeType, Integer>
		levelsOfStandardUpgrades = new EnumMap<>(StandardUpgradeType.class);
	
	public HelicopterType
		helicopterType;
	
	public Language
		language;

	public TimeOfDay
		timeOfDay;
	
	private boolean
		isValid;
		
	private String
		currentPlayerName;
	
	private Map<HighScoreType, SizeLimitedTreeSet<HighScoreEntry>>
		highScoreMap = new EnumMap<>(HighScoreType.class);
	
	
	private Savegame()
	{
		this.isValid = false;
		this.currentPlayerName = Events.currentPlayerName;
		HighScoreType.getValues()
					 .forEach(highScoreType -> highScoreMap.put(highScoreType, new HighScoreEntrySet()));
	}
	
	public static Savegame initialize()
	{
		Savegame output;
		if((new File(FILENAME)).exists())
		{			
			Savegame temp = lastGame();
			
			Events.currentPlayerName = temp.currentPlayerName;
			Window.setLanguage(temp.language);
			Window.hasOriginalResolution = temp.originalResolution;
			Audio.standardBackgroundMusic = temp.standardBackgroundMusic && Audio.MICHAEL_MODE;
			Audio.isSoundOn = temp.isSoundOn;
			Events.recordTime = temp.recordTime.clone();
			Events.heliosMaxMoney = Events.getHeliosMaxMoney();
			Events.reachedLevelTwenty = temp.reachedLevelTwenty.clone();
			
			output = temp;
		}		
		else
		{
			output = new Savegame();
		}
		Events.highScoreMap = new EnumMap<>(output.highScoreMap);
				
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
	
	public void saveWithoutValidity(Helicopter helicopter)
	{
		saveInHighscore();
		loseValidity();
		saveToFile(helicopter);
		Audio.play(Audio.emp);
	}
	
	public void saveInHighscore()
	{
		if(this.isWorthyForHighscore())
		{
			HighScoreEntry tempEntry = HighScoreEntry.of(this);
			highScoreMap.get(HighScoreType.of(helicopterType)).add(tempEntry);
			highScoreMap.get(HighScoreType.OVERALL).add(tempEntry);
		}
	}
	
	public void saveToFile(Helicopter helicopter)
	{
		this.save(helicopter);
		Window.buttons.get(StartScreenButtonType.RESUME_LAST_GAME).setEnabled(this.isValid);
		writeToFile();
	}
	
	public void becomeValid()
	{
		this.isValid = true;
	}
	
	public void loseValidity()
	{
		this.isValid = false;
	}
	
	public boolean isValid()
	{
		return isValid;
	}
	
	public String getCurrentPlayerName()
	{
		return currentPlayerName;
	}
	
	public void setCurrentPlayerName(String currentPlayerName)
	{
		this.currentPlayerName = currentPlayerName;
	}
	
	private void save(Helicopter helicopter)
	{
		this.currentPlayerName = Events.currentPlayerName;
		this.language = Window.language;
		this.standardBackgroundMusic = Audio.standardBackgroundMusic;
		this.originalResolution = Window.hasOriginalResolution;
		this.isSoundOn = Audio.isSoundOn;
		this.money = Events.money;	
		this.killsAfterLevelUp = Events.killsAfterLevelUp;
		this.level = Events.level;
		this.maxLevel = Events.maxLevel;
		this.timeOfDay = Events.timeOfDay;
		this.bonusCounter = Events.overallEarnings;
		this.extraBonusCounter = Events.extraBonusCounter;
		this.playingTime = Events.playingTime;
		this.scoreScreenTimes = helicopter.scoreScreenTimes.clone();
		this.recordTime = Events.recordTime.clone();
		this.reachedLevelTwenty = Events.reachedLevelTwenty.clone();
		this.highScoreMap = new EnumMap<>(Events.highScoreMap);
		
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
		
    private boolean isWorthyForHighscore()
    {
        return this.isValid && (!this.wasCreatedThroughCheating || Events.IS_SAVE_GAME_SAVED_ANYWAY);
    }
}