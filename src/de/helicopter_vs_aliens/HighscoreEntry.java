package de.helicopter_vs_aliens;

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
	public String player_name;
	
	public int max_level;
	public int crashes;
	public int repairs;
	public int bonus_income;
	
	public long playing_time;
	
	public HelicopterTypes
		helicopterType;
	
	
	HighscoreEntry(Savegame savegame)
	{
		this.player_name = currentPlayerName;
		this.helicopterType = savegame.helicopterType;
		this.max_level = savegame.max_level;
		this.playing_time = savegame.playing_time/60000;
		this.crashes = savegame.nr_of_crashes;
		this.repairs = savegame.nr_of_repairs;
		this.bonus_income = Events.bonusIncomePercentage();
	}
	
	// TODO use comparator
	private boolean isBetterThan(HighscoreEntry entry)
	{
			 if(this.max_level > entry.max_level) return true;
		else if(this.max_level < entry.max_level) return false;
		else if(this.playing_time < entry.playing_time) return true;
		else if(this.playing_time > entry.playing_time) return false;
		else if(this.crashes < entry.crashes) return true;
		else if(this.crashes > entry.crashes) return false;
		else if(this.repairs < entry.repairs) return true;
		else if(this.repairs > entry.repairs) return false;
		else return this.bonus_income > entry.bonus_income;
	}
	
	static void put_entry(HighscoreEntry[] highscore, HighscoreEntry entry)
	{
		HighscoreEntry temp_entry, current_entry = entry;
		for(int i = 0; i < NUMBER_OF_ENTRIES; i++)
		{
			if(highscore[i] == null)
			{
				highscore[i] = current_entry;
				break;
			}
			else if(current_entry.isBetterThan(highscore[i]))
			{
				temp_entry = highscore[i];
				highscore[i] = current_entry;
				current_entry = temp_entry;
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
					Menu.startscreen_menu_button.get("4").marked = true;
					Menu.startscreen_button.get("10").marked = true;
				}
				else
				{
					Menu.startscreen_menu_button.get("4").marked = false;
					Menu.startscreen_button.get("10").marked = false;
				}
				savegame.currentPlayerName = currentPlayerName;
				Events.settings_changed = true;
			}
		}	
	}
}
