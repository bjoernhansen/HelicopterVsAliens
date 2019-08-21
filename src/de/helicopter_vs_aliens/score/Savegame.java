package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.TimesOfDay;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;
import de.helicopter_vs_aliens.util.dictionary.Languages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Savegame implements Serializable
{
	private static final int
		OVERALL_HIGHSCORE_INDEX = 6;
	
	private static final long
		serialVersionUID = 1L;	
	
	private static final String 
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
		numberOfCannons,
		rapidfire, 
		levelOfUpgrade[] = new int[6];
	
	public long
		playingTime,
		recordTime[][] = new long [Helicopter.NR_OF_TYPES][5],
		scorescreenTimes[] = new long [Helicopter.NR_OF_TYPES];
	
	public float
		currentPlating,
		energy;
	
	public boolean
		originalResulution,
		standardBackgroundMusic,
		isSoundOn,
		valid,
		hasShortrangeRadiation,
		hasPiercingWarheads,
		spotlight,
		hasJumboMissiles,
		hasRadarDevice,
		hasInterphaseGenerator,
		hasPowerUpImmobilizer,
		noCheatsUsed,
		reachedLevelTwenty[] = new boolean [Helicopter.NR_OF_TYPES];
	
	public HelicopterTypes
		helicopterType;
	
	public Languages
		language;

	public TimesOfDay
		timeOfDay;
		
		
	private Savegame()
	{
		this.valid = false;
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
			Menu.originalResulution = temp.originalResulution;
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
			
	public void saveToFile(Helicopter helicopter, boolean validity)
	{				
		this.save(helicopter, validity);
		Menu.startscreenButton.get("11").enabled = validity;
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

	private void save(Helicopter helicopter, boolean validity)
	{		
		this.valid = validity;
		
		this.currentPlayerName = HighscoreEntry.currentPlayerName;
		this.language = Menu.language;
		this.standardBackgroundMusic = Audio.standardBackgroundMusic;
		this.originalResulution = Menu.originalResulution;
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
		this.levelOfUpgrade = helicopter.levelOfUpgrade.clone();
		this.spotlight = helicopter.spotlight;
		this.platingDurabilityFactor = helicopter.platingDurabilityFactor;
		this.hasShortrangeRadiation = helicopter.hasShortrangeRadiation;
		this.hasPiercingWarheads = helicopter.hasPiercingWarheads;
		this.numberOfCannons = helicopter.numberOfCannons;
		this.hasJumboMissiles = helicopter.hasJumboMissiles;
		this.hasRadarDevice = helicopter.hasRadarDevice;
		this.rapidfire = helicopter.rapidfire;
		this.hasInterphaseGenerator = helicopter.hasInterphaseGenerator;
		this.hasPowerUpImmobilizer = helicopter.hasPowerUpImmobilizer;
		this.currentPlating = helicopter.currentPlating;
		this.energy = helicopter.energy;
		this.enemiesSeen = helicopter.numberOfEnemiesSeen;
		this.enemiesKilled = helicopter.numberOfEnemiesKilled;
		this.miniBossSeen = helicopter.numberOfMiniBossSeen;
		this.miniBossKilled = helicopter.numberOfMiniBossKilled;
		this.numberOfCrashes = helicopter.numberOfCrashes;
		this.numberOfRepairs = helicopter.numberOfRepairs;
		this.noCheatsUsed = helicopter.isPlayedWithoutCheats;
		this.missileCounter = helicopter.missileCounter;
		this.hitCounter = helicopter.hitCounter;
	}	
	
	public void saveInHighscore()
	{
		if(this.valid && (this.noCheatsUsed || Events.SAVE_ANYWAY))
		{				
			HighscoreEntry tempEntry = new HighscoreEntry(this);
			HighscoreEntry.putEntry(Events.highscore[this.helicopterType.ordinal()], tempEntry);
			HighscoreEntry.putEntry(Events.highscore[OVERALL_HIGHSCORE_INDEX], tempEntry);
		}
	}
}