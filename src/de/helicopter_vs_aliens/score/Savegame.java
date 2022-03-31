package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.button.StartScreenButtonType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.util.SizeLimitedPriorityQueue;
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
import java.util.TreeSet;

import static de.helicopter_vs_aliens.control.Events.NUMBER_OF_BOSS_LEVEL;

public class Savegame implements Serializable
{
	private static final int
		OVERALL_HIGH_SCORE_INDEX = 6;
	
	public static final int
		NUMBER_OF_ENTRIES = 10;
	
	private static final String
		FILENAME = "savegame";
	
	
	private String
		currentPlayerName;
	
	// TODO das sollte eine Map<HighscoreType, TreeSets<HighScoreEntry>> sein
	private HighScoreEntry[][]
		highScore = new HighScoreEntry[7][10];
	
	private Map<HighScoreType, SizeLimitedPriorityQueue<HighScoreEntry>>
		highScoreMap = new EnumMap<>(HighScoreType.class);
	
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
		this.currentPlayerName = Events.currentPlayerName;
		HighScoreType.getValues()
					 .forEach(highScoreType -> highScoreMap.put(highScoreType, new SizeLimitedPriorityQueue<>(NUMBER_OF_ENTRIES)));
	}
	
	public static Savegame initialize()
	{
		Savegame output;
		if((new File(FILENAME)).exists())
		{			
			Savegame temp = lastGame();
			
			Events.currentPlayerName = temp.currentPlayerName;
			Window.setLanguage(temp.language);
			Window.hasOriginalResolution = temp.originalResulution;
			Audio.standardBackgroundMusic = temp.standardBackgroundMusic && Audio.MICHAEL_MODE;
			Audio.isSoundOn = temp.isSoundOn;
			Events.recordTime = temp.recordTime.clone();
			Events.heliosMaxMoney = Events.getHeliosMaxMoney();
			Events.reachedLevelTwenty = temp.reachedLevelTwenty.clone();
			Events.highScore = temp.highScore.clone();
			
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
			
	public void saveToFile(Helicopter helicopter)
	{				
		this.save(helicopter);
		Window.buttons.get(StartScreenButtonType.RESUME_LAST_GAME).setEnabled(this.isValid);
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
		this.currentPlayerName = Events.currentPlayerName;
		this.language = Window.language;
		this.standardBackgroundMusic = Audio.standardBackgroundMusic;
		this.originalResulution = Window.hasOriginalResolution;
		this.isSoundOn = Audio.isSoundOn;
		this.money = Events.money;	
		this.killsAfterLevelUp = Events.killsAfterLevelUp;
		this.level = Events.level;
		this.maxLevel = Events.maxLevel;
		this.timeOfDay = Events.timeOfDay;
		this.bonusCounter = Events.overallEarnings;
		this.extraBonusCounter = Events.extraBonusCounter;
		this.playingTime = Events.playingTime;
		this.scorescreenTimes = helicopter.scoreScreenTimes.clone();
		this.recordTime = Events.recordTime.clone();
		this.reachedLevelTwenty = Events.reachedLevelTwenty.clone();
		this.highScore = Events.highScore.clone();
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
	
	public void saveInHighscore()
	{
	    if(this.isWorthyForHighscore())
		{				
			HighScoreEntry tempEntry = HighScoreEntry.of(this);
			putEntry(Events.highScore[helicopterType.ordinal()], tempEntry);
			putEntry(Events.highScore[OVERALL_HIGH_SCORE_INDEX], tempEntry);
			
			highScoreMap.get(HighScoreType.of(helicopterType)).add(tempEntry);
			highScoreMap.get(HighScoreType.OVERALL).add(tempEntry);
		}
	}
	
	static void putEntry(HighScoreEntry[] highScore, HighScoreEntry entry)
	{
		HighScoreEntry highscoreEntry, currentEntry = entry;
		for(int i = 0; i < NUMBER_OF_ENTRIES; i++)
		{
			if(highScore[i] == null)
			{
				highScore[i] = currentEntry;
				break;
			}
			else if(currentEntry.isBetterThan(highScore[i]))
			{
				highscoreEntry = highScore[i];
				highScore[i] = currentEntry;
				currentEntry = highscoreEntry;
			}
		}
	}
    
    private boolean isWorthyForHighscore()
    {
        return this.isValid && (!this.wasCreatedThroughCheating || Events.IS_SAVE_GAME_SAVED_ANYWAY);
    }
    
    public void becomeValid()
    {
        this.isValid = true;
    }
    
    public void loseValidity()
    {
        this.isValid = false;
    }
	
	public void saveWithoutValidity(Helicopter helicopter)
	{
		saveInHighscore();
		loseValidity();
		saveToFile(helicopter);
		Audio.play(Audio.emp);
	}
	
	public String getCurrentPlayerName()
	{
		return currentPlayerName;
	}
	
	public void setCurrentPlayerName(String currentPlayerName)
	{
		this.currentPlayerName = currentPlayerName;
	}
}