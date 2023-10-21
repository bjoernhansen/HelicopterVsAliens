package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.TimeOfDay;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.button.StartScreenButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.Helios;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.util.dictionary.Language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
	
	private HighScore
		highScore;
	
	// TODO es sollte kein Array verwendet werden
	public ScoreScreenTimes
		scoreScreenTimes;

	public Set<HelicopterType>
		helicoptersThatReachedLevel20;
	
	RecordTimeManager
		recordTimeManager;
	
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
	

	private Savegame()
	{
		this.isValid = false;
		this.currentPlayerName = Events.currentPlayerName;
	}
	
	public static Savegame makeInstance()
	{
		if((new File(FILENAME)).exists())
		{			
			Optional<Savegame> loadedSavegame = lastGame();
			loadedSavegame.ifPresent(savegame -> {
				Events.currentPlayerName = savegame.currentPlayerName;
				Window.setLanguage(savegame.language);
				Window.hasOriginalResolution = savegame.originalResolution;
				Audio.standardBackgroundMusic = savegame.standardBackgroundMusic || !Audio.MICHAEL_MODE;
				Audio.isSoundOn = savegame.isSoundOn;
				Events.highScore = savegame.highScore;
				Events.recordTimeManager = savegame.recordTimeManager;
				Events.helicoptersThatReachedLevel20 = savegame.helicoptersThatReachedLevel20;
				Events.heliosMaxMoney = Helios.getMaxMoney();
			});
			return loadedSavegame.orElseGet(Savegame::new);
		}
		return new Savegame();
	}
	
	private static Optional<Savegame> lastGame()
	{		
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME)))
		{			
			try
			{
				Object obj = ois.readObject();				
				if(obj instanceof Savegame savegame)
				{
					return Optional.of(savegame);
				}	
			}
			catch(InvalidClassException e)
			{
				File savegameFile = new File(FILENAME);
				savegameFile.delete();
				System.out.println("The savegame was recreated due to incompatibility.");
				return Optional.empty();
			}
			catch(ClassNotFoundException e)
			{
				return Optional.empty();
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public void saveWithoutValidity(GameRessourceProvider gameRessourceProvider)
	{
		saveInHighscore();
		loseValidity();
		saveToFile(gameRessourceProvider);
		Audio.play(Audio.emp);
	}
	
	public void saveInHighscore()
	{
		Events.highScore.saveEntryFor(this);
	}
	
	public void saveToFile(GameRessourceProvider gameRessourceProvider)
	{
		save(gameRessourceProvider);
		Window.buttons.get(StartScreenButtonType.RESUME_LAST_GAME).setEnabled(isValid);
		writeToFile();
	}

	private void save(GameRessourceProvider gameRessourceProvider)
	{
		this.standardBackgroundMusic = Audio.standardBackgroundMusic;
		this.isSoundOn = Audio.isSoundOn;

		this.language = Window.language;
		this.originalResolution = Window.hasOriginalResolution;

		this.currentPlayerName = Events.currentPlayerName;
		this.money = Events.money;
		this.killsAfterLevelUp = Events.killsAfterLevelUp;
		this.level = Events.level;
		this.maxLevel = Events.maxLevel;
		this.timeOfDay = Events.timeOfDay;
		this.bonusCounter = Events.overallEarnings;
		this.extraBonusCounter = Events.extraBonusCounter;
		this.playingTime = Events.playingTime;
		this.recordTimeManager = Events.recordTimeManager;
		this.helicoptersThatReachedLevel20 = Events.helicoptersThatReachedLevel20;
		this.highScore = Events.highScore;

		var helicopter = gameRessourceProvider.getHelicopter();
		this.scoreScreenTimes = helicopter.scoreScreenTimes;
		this.helicopterType = helicopter.getType();
		this.levelsOfStandardUpgrades = helicopter.getLevelsOfStandardUpgrades();
		this.spotlight = helicopter.hasSpotlights;
		this.platingDurabilityFactor = helicopter.platingDurabilityFactor;
		this.hasPiercingWarheads = helicopter.hasPiercingWarheads;
		this.numberOfCannons = helicopter.numberOfCannons;
		this.hasFifthSpecial = helicopter.hasFifthSpecial();
		this.currentPlating = helicopter.getCurrentPlating();
		this.currentEnergy = helicopter.getCurrentEnergy();
		this.wasCreatedThroughCheating = helicopter.isPlayedWithCheats;

		GameStatisticsCalculator gameStatisticsCalculator = gameRessourceProvider.getGameStatisticsCalculator();
		this.enemiesSeen = gameStatisticsCalculator.getNumberOfEnemiesSeen();
		this.enemiesKilled = gameStatisticsCalculator.getNumberOfEnemiesKilled();
		this.miniBossSeen = gameStatisticsCalculator.getNumberOfMiniBossSeen();
		this.miniBossKilled = gameStatisticsCalculator.getNumberOfMiniBossKilled();
		this.numberOfCrashes = gameStatisticsCalculator.getNumberOfCrashes();
		this.numberOfRepairs = gameStatisticsCalculator.getNumberOfRepairs();
		this.missileCounter = gameStatisticsCalculator.getMissileCounter();
		this.hitCounter = gameStatisticsCalculator.getHitCounter();
	}

	private void writeToFile()
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
		
    boolean isWorthyForHighscore()
    {
        return this.isValid && (!this.wasCreatedThroughCheating || Events.IS_SAVE_GAME_SAVED_ANYWAY);
    }
}