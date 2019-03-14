package de.helicopterdefence;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class Audio implements Constants
{	
	static final boolean 
		MICHAEL_MODE = true;		// Legt fest, ob der Michael-Modus bei der Hintergrundmusikauswahl verfügbar ist
	
	static boolean 
		sound_on = true,			// = true: Hintergrundmusik wird abgespielt
		standard_bg_music = true;	// = true: Verwenden der Standard-Hintergrund-Musikauswahl 
		
	public static AudioClip
	
		current_bg,			// Die aktuell abgespielte Hintergrund-Musik
		
		// Menü-Sounds
		block, 
		cash,
		choose,
		
		// Game-Sounds		
		cloak,
		emp,
		explosion1,
		explosion2,
		explosion3,
		explosion4,
		explosion5,
		landing, 
		launch1,
		launch2,
		launch3,
		level_up,
		phase_shift,
		plasma_on,
		plasma_off, 
		pu_fade1,
		pu_fade2,
		rebound,
		shield_up,
		stun, 
		stun_activated,
		teleport1,
		teleport2,
		tractor_beam, 
		
		// Announcer
		applause1,
		applause2,		
		nicecatch,
		doublekill,
		tripplekill, 
		multikill,
		megakill,
		monsterkill,		
		pu_announcer [],	
			
		// Hintergrundmusik für den Standard-Modus
		bg_music1,
		bg_music2, 
		bg_music3,		
		
		// Hintergrundmusik für Michael-Modus
		main_menue,
		repair_shop,
		scorescreen,
		level_01_09,
		level_11_19,
		level_21_29,
		level_31_39,
		level_41_48,
		level_49,
		boss_level,
		final_boss_level,
		victory;		 
		
		
	private static AudioClip getAudioClip(String string)
	{
		URL url = HelicopterDefence.class.getResource("sounds/" +string);

		return Applet.newAudioClip(url);	
	}
    
    static void initialize()
    {    	
    	/*
        launch1 =			getAudioClip("launch1.wav");
		explosion1 = 		getAudioClip("explosion1.wav");
		explosion2 = 		getAudioClip("explosion2.wav");
		explosion3 = 		getAudioClip("explosion3.wav");
		explosion4 = 		getAudioClip("explosion4.wav");
		bg_music1 = 		getAudioClip("bg_music1.wav");
		bg_music2 = 		getAudioClip("bg_music2.wav");
		bg_music3 = 		getAudioClip("bg_music3.wav");
		level_up = 			getAudioClip("level_up.wav");
		choose = 			getAudioClip("choose.wav");
		cash = 				getAudioClip("cash.wav");
		block = 			getAudioClip("block.wav");
		teleport2 = 		getAudioClip("teleport2.wav");
		cloak = 			getAudioClip("cloak.wav");
		launch2 = 			getAudioClip("launch2.wav");
		rebound = 			getAudioClip("rebound.wav");
		tractor_beam = 		getAudioClip("tractor_beam.wav");
		shield_up = 		getAudioClip("shield_up.wav");
		applause1 = 		getAudioClip("applause1.wav");
		launch3 = 			getAudioClip("launch3.wav");
		explosion5 = 		getAudioClip("explosion5.wav");
		teleport1 = 		getAudioClip("teleport1.wav");
		phase_shift = 		getAudioClip("phase_shift.wav");
		emp = 				getAudioClip("emp.wav");
		stun = 				getAudioClip("stun.wav");
		applause2 = 		getAudioClip("applause2.wav");
		doublekill = 		getAudioClip("doublekill.wav");
		tripplekill = 		getAudioClip("tripplekill.wav");
		megakill = 			getAudioClip("megakill.wav");
		multikill = 		getAudioClip("multikill.wav");
		monsterkill = 		getAudioClip("monsterkill.wav");
		nicecatch = 		getAudioClip("nicecatch.wav");		
		landing = 			getAudioClip("landing.wav");
		plasma_on = 		getAudioClip("plasma_on.wav");
		plasma_off = 		getAudioClip("plasma_off.wav");
		pu_fade1 = 			getAudioClip("pu_fade1.wav");
		pu_fade2 = 			getAudioClip("pu_fade2.wav");
		stun_activated =	getAudioClip("stun_activated.wav");
		
		if(MICHAEL_MODE)
		{
			main_menue =		getAudioClip("main_menu.wav");
			repair_shop =		getAudioClip("repair_shop.wav");
			scorescreen =		getAudioClip("scorescreen.wav");
			level_01_09 =		getAudioClip("level_01_09.wav");
			level_11_19 =		getAudioClip("level_11_19.wav");
			level_21_29 =		getAudioClip("level_21_29.wav");
			level_31_39 =		getAudioClip("level_31_39.wav");
			level_41_48 =		getAudioClip("level_41_48.wav");
			level_49 =			getAudioClip("level_49.wav");
			boss_level =		getAudioClip("boss_level.wav");
			final_boss_level =	getAudioClip("final_boss_level.wav");
			victory =			getAudioClip("victory.wav");
		}		
						
		pu_announcer = new AudioClip[6];		
		pu_announcer[TRIPLE_DMG] = getAudioClip("announcer_triple_dmg.wav");
		pu_announcer[INVINCIBLE] = getAudioClip("announcer_invincible.wav");
		pu_announcer[UNLIMITRED_ENERGY] = getAudioClip("announcer_unlimited_energy.wav");
		pu_announcer[BOOSTED_FIRE_RATE] = getAudioClip("announcer_fire_rate_boosted.wav");
		pu_announcer[REPARATION] = getAudioClip("announcer_reparation.wav");
		pu_announcer[BONUS_INCOME] = getAudioClip("announcer_bonus_credit.wav");
		*/
    }
           
    static void refresh_bg_music()
    {
    	/*
        if(current_bg != null){current_bg.stop();}
    	if(sound_on)
    	{
    		current_bg = get_bg_music();
    		current_bg.loop();
    	}
    	*/
    }    
    
    static void change_bg_music_mode(Savegame savegame)
    {
    	standard_bg_music = !standard_bg_music; 
    	savegame.standard_bg_music = standard_bg_music;
    	refresh_bg_music();
    	Events.settings_changed = true;
    } 

	// Rückgabe der aktuell zu spielenden Hintergrundmusik
    private static AudioClip get_bg_music()
    {
    	if(standard_bg_music)
        {
    		if(Events.window == GAME && !Events.is_boss_level()) 
    		{
    			return bg_music2;
    		}
    		else if(Events.window == REPAIR_SHOP || Events.window == SCORESCREEN)
    		{
    			return bg_music1;
    		}
    		return bg_music3;            
        }    	
    	else if(Events.window == REPAIR_SHOP){return repair_shop;}
    	else if(Events.window == SCORESCREEN)
    	{
    		if(Events.level == 51){return victory;}
    		return scorescreen;
    	}
    	else if(   Events.window == INFORMATIONS
    			|| Events.window == DESCRIPTION
    			|| Events.window == SETTINGS
    			|| Events.window == CONTACT
    			|| Events.window == HELICOPTER_TYPES
    			|| Events.window == HIGHSCORE
    			|| Events.window == STARTSCREEN)
    	{
    		return main_menue;
    	}    	
    	else if(Events.level >= 1 && Events.level < 10)
		{
    		return level_01_09;
		}
		else if(Events.level == 10)
		{
			return boss_level;
		}
		else if(Events.level >= 11 && Events.level < 20)
		{
    		return level_11_19;
		}
		else if(Events.level == 20)
		{
			return boss_level;
		}
		else if(Events.level >= 21 && Events.level < 30)
		{
    		return level_21_29;
		}
		else if(Events.level == 30)
		{
			return boss_level;
		}
		else if(Events.level >= 31 && Events.level < 40)
		{
    		return level_31_39;
		}
		else if(Events.level == 40)
		{
			return boss_level;
		}
		else if(Events.level >= 41 && Events.level < 49)
		{
    		return level_41_48;
		}
		else if(Events.level == 49)
		{
			return level_49;
		}
		else if(Events.level == 50)
		{
			return final_boss_level;
		}
		return victory;      
    }
          
    public static void play(AudioClip clip)
    {
    	/*
        clip.stop();
    	if(sound_on){clip.play();}
    	*/
    }   
    
    public static void loop(AudioClip clip)
	{
		/*
	    if(sound_on){clip.loop();}
	    */
	}
        
    // Abspielen eines Lob-Sounds entsprechend der Anzahl mit einem Mal besiegter Gegner
    static void praise(int nr)
    {
    	if(nr == 1)
    	{
    		play(nicecatch);
    		play(applause2);
    	}
    	else if(nr == 2){play(doublekill);}
    	else if(nr == 3)
    	{    		
    		play(tripplekill);
    		play(applause2);
    	}
    	else if(nr == 4)
    	{    		
    		play(megakill);
    		play(applause2);
    	}
    	else 
    	{
    		stop_applause();
    		play(applause1);
    		if(nr == 5){play(multikill);}
    		else if(nr >= 6){play(monsterkill);}
    	}	
    }
    
    private static void stop_applause()
    {
    	/*
        applause1.stop();
    	applause2.stop();
    	*/
    }
}