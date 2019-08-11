package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;

import java.io.Serializable;

public class HighscoreEntry implements Serializable
{		
	private static final long 
		serialVersionUID = 1L;	
	
	public static final int
			NUMBER_OF_ENTRIES = 10;
	
	public static String
        currentPlayerName = "John Doe";
	
	// Variablen eines Highscore-Eintrages
	public String playerName;
	
	public int maxLevel;
	public int crashes;
	public int repairs;
	public int bonusIncome;
	
	public long playingTime;
	
	public HelicopterTypes
		helicopterType;
	
	
	HighscoreEntry(Savegame savegame)
	{
		this.playerName = currentPlayerName;
		this.helicopterType = savegame.helicopterType;
		this.maxLevel = savegame.maxLevel;
		this.playingTime = savegame.playingTime/60000;
		this.crashes = savegame.numberOfCrashes;
		this.repairs = savegame.numberOfRepairs;
		this.bonusIncome = Events.bonusIncomePercentage();
	}
	
	// TODO use comparator
	private boolean isBetterThan(HighscoreEntry entry)
	{
			 if(this.maxLevel > entry.maxLevel) return true;
		else if(this.maxLevel < entry.maxLevel) return false;
		else if(this.playingTime < entry.playingTime) return true;
		else if(this.playingTime > entry.playingTime) return false;
		else if(this.crashes < entry.crashes) return true;
		else if(this.crashes > entry.crashes) return false;
		else if(this.repairs < entry.repairs) return true;
		else if(this.repairs > entry.repairs) return false;
		else return this.bonusIncome > entry.bonusIncome;
	}
	
	static void putEntry(HighscoreEntry[] highscore, HighscoreEntry entry)
	{
		HighscoreEntry highscoreEntry, currentEntry = entry;
		for(int i = 0; i < NUMBER_OF_ENTRIES; i++)
		{
			if(highscore[i] == null)
			{
				highscore[i] = currentEntry;
				break;
			}
			else if(currentEntry.isBetterThan(highscore[i]))
			{
				highscoreEntry = highscore[i];
				highscore[i] = currentEntry;
				currentEntry = highscoreEntry;
			}
		}
	}

	public static void checkName(Savegame savegame)
	{
		if(!currentPlayerName.equals(savegame.currentPlayerName))
		{
			if(currentPlayerName.equals(""))
			{
				currentPlayerName = savegame.currentPlayerName;
			}
			else
			{
				if(currentPlayerName.equals("John Doe"))
				{
					Menu.startscreenMenuButton.get("4").marked = true;
					Menu.startscreenButton.get("10").marked = true;
				}
				else
				{
					Menu.startscreenMenuButton.get("4").marked = false;
					Menu.startscreenButton.get("10").marked = false;
				}
				savegame.currentPlayerName = currentPlayerName;
				Events.settingsChanged = true;
			}
		}	
	}
}
