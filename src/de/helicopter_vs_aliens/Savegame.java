package de.helicopter_vs_aliens;

import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Savegame implements Constants, Serializable
{
	private static final int
		OVERALL_HIGHSCORE_INDEX = 6;
	
	private static final long
		serialVersionUID = 1L;	
	
	private static final String 
		FILENAME = "savegame";
	
	String
		currentPlayerName;
	
	private HighscoreEntry [][]
		highscore = new HighscoreEntry[7][10];
	
	public int	language,
		money,
		kills_after_levelup,
		level, 
		max_level,
		timeOfDay, 
		bonus_counter,
		extra_bonus_counter,		
		enemies_seen, 
		enemies_killed,
		mini_boss_seen,
		mini_boss_killed, 
		nr_of_crashes, 
		nr_of_repairs,
		missile_counter,
		hit_counter,
		platingDurabilityFactor,
		nr_of_cannons, 
		rapidfire, 
		level_of_upgrade [] = new int[6];
	
	public long
		playing_time, 
		recordTime[][] = new long [Helicopter.NR_OF_TYPES][5],
		scorescreen_times[] = new long [Helicopter.NR_OF_TYPES];
	
	public float
		jumbo_missiles, 
		current_plating,
		energy;
	
	public boolean
		originalResulution,
		standardBackgroundMusic,
		isSoundOn,
		valid, 
		has_shortrange_radiation,
		has_piercing_warheads, 
		spotlight,
		has_radar_device,
		has_interphase_generator,
		has_PowerUp_immobilizer,
		no_cheats_used,
		reachedLevelTwenty[] = new boolean [Helicopter.NR_OF_TYPES];
	
	public HelicopterTypes
		helicopterType;
		
		
	private Savegame()
	{
		this.valid = false;
		this.currentPlayerName = HighscoreEntry.currentPlayerName;
	}
	
	static Savegame initialize()
	{
		Savegame output;
		if((new File(FILENAME)).exists())
		{			
			Savegame temp = last_game();			
			
			HighscoreEntry.currentPlayerName = temp.currentPlayerName;
			Menu.language = temp.language;
			Menu.originalResulution = temp.originalResulution;
			Audio.standardBackgroundMusic = temp.standardBackgroundMusic && Audio.MICHAEL_MODE;
			Audio.isSoundOn = temp.isSoundOn;
			Events.recordTime = temp.recordTime.clone();
			Events.heliosMaxMoney = Events.get_helios_max_money();
			Events.reachedLevelTwenty = temp.reachedLevelTwenty.clone();
			Events.highscore = temp.highscore.clone();
			output = temp;
		}		
		else{output = new Savegame();}		
				
		return output;
	}
	
	private static Savegame last_game()
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
			
	void save_to_file(Helicopter helicopter, boolean validity)
	{				
		this.save(helicopter, validity);
		Menu.startscreen_button.get("11").enabled = validity;
		writeToFile();			
	}
		
	
	void writeToFile()
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
		this.kills_after_levelup = Events.kills_after_levelup;
		this.level = Events.level;
		this.max_level = Events.max_level;
		this.timeOfDay = Events.timeOfDay;
		this.bonus_counter = Events.overallEarnings;
		this.extra_bonus_counter = Events.extraBonusCounter;
		this.playing_time = Events.playing_time;		
		this.scorescreen_times = helicopter.scorescreen_times.clone();
		this.recordTime = Events.recordTime.clone();
		this.reachedLevelTwenty = Events.reachedLevelTwenty.clone();
		this.highscore = Events.highscore.clone();
		this.helicopterType = helicopter.getType();
		this.level_of_upgrade = helicopter.levelOfUpgrade.clone();
		this.spotlight = helicopter.spotlight;
		this.platingDurabilityFactor = helicopter.platingDurabilityFactor;
		this.has_shortrange_radiation = helicopter.hasShortrangeRadiation;
		this.has_piercing_warheads = helicopter.hasPiercingWarheads;
		this.jumbo_missiles = helicopter.jumboMissiles;
		this.nr_of_cannons = helicopter.nr_of_cannons;
		this.has_radar_device = helicopter.hasRadarDevice;
		this.rapidfire = helicopter.rapidfire;
		this.has_interphase_generator = helicopter.hasInterphaseGenerator;
		this.has_PowerUp_immobilizer = helicopter.hasPowerUpImmobilizer;
		this.current_plating = helicopter.currentPlating;
		this.energy = helicopter.energy;
		this.enemies_seen = helicopter.enemies_seen;
		this.enemies_killed = helicopter.enemies_killed;
		this.mini_boss_seen = helicopter.mini_boss_seen;
		this.mini_boss_killed = helicopter.mini_boss_killed;
		this.nr_of_crashes = helicopter.nr_of_crashes; 
		this.nr_of_repairs = helicopter.nr_of_repairs;
		this.no_cheats_used = helicopter.no_cheats_used;
		this.missile_counter = helicopter.missile_counter;
		this.hit_counter = helicopter.hit_counter;
	}	
	
	void save_in_highscore()
	{
		if(this.valid && (this.no_cheats_used || Events.save_anyway))
		{				
			HighscoreEntry temp_entry = new HighscoreEntry(this);
			HighscoreEntry.put_entry(Events.highscore[this.helicopterType.ordinal()], temp_entry);
			HighscoreEntry.put_entry(Events.highscore[OVERALL_HIGHSCORE_INDEX], temp_entry);
		}
	}
}