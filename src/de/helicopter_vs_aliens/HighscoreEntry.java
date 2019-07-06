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
		current_player_name = "John Doe";
	
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
		this.player_name = current_player_name;		
		this.helicopterType = savegame.helicopterType;
		this.max_level = savegame.max_level;
		this.playing_time = savegame.playing_time/60000;
		this.crashes = savegame.nr_of_crashes;
		this.repairs = savegame.nr_of_repairs;
		this.bonus_income = Events.bonusIncomePercentage();
	}
	
	boolean is_better_than(HighscoreEntry entry)
	{
			 if(this.max_level > entry.max_level) return true;
		else if(this.max_level < entry.max_level) return false;
		else if(this.playing_time < entry.playing_time) return true;
		else if(this.playing_time > entry.playing_time) return false;
		else if(this.crashes < entry.crashes) return true;
		else if(this.crashes > entry.crashes) return false;
		else if(this.repairs < entry.repairs) return true;
		else if(this.repairs > entry.repairs) return false;
		else if(this.bonus_income > entry.bonus_income) return true;	 
		return false;
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
			else if(current_entry.is_better_than(highscore[i]))
			{
				temp_entry = highscore[i];
				highscore[i] = current_entry;
				current_entry = temp_entry;
			}
		}
	}

	public static void checkName(Savegame savegame)
	{
		if(!current_player_name.equals(savegame.current_player_name))
		{
			if(current_player_name.equals(""))
			{
				current_player_name = savegame.current_player_name;
			}
			else
			{
				if(current_player_name.equals("John Doe"))
				{
					Menu.startscreen_menu_button.get("4").marked = true;
					Menu.startscreen_button.get("10").marked = true;
				}
				else
				{
					Menu.startscreen_menu_button.get("4").marked = false;
					Menu.startscreen_button.get("10").marked = false;
				}
				savegame.current_player_name = current_player_name;
				Events.settings_changed = true;
			}
		}	
	}
}