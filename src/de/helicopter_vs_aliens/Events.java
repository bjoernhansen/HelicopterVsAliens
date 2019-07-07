package de.helicopter_vs_aliens;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.enemy.BossTypes;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.gui.Button;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.gui.WindowTypes;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterFactory;
import de.helicopter_vs_aliens.model.helicopter.HelicopterTypes;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.gui.Button.STARTSCREEN_MENU_BUTTON;
import static de.helicopter_vs_aliens.model.helicopter.Helicopter.GOLIATH_PLATING_STRENGTH;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.*;
import static de.helicopter_vs_aliens.PriceLevels.REGULAR;
import static de.helicopter_vs_aliens.gui.WindowTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.util.dictionary.Languages.ENGLISH;
import static de.helicopter_vs_aliens.util.dictionary.Languages.GERMAN;


public class Events implements Constants, Costs, BossTypes
{
	// Konstanten zur Berechnung der Reparaturkosten und der Boni bei Abschuss von Gegnern
	public static final int
			DAY_BONUS_FACTOR = 60,
			NIGHT_BONUS_FACTOR = 90,
			COMPARISON_RECORD_TIME = 60;	// angenommene Bestzeit für besiegen von Boss 5

	public static HighscoreEntry [][]
		highscore = new HighscoreEntry[7][10];
	
	static Point 
		cursor = new Point();	// die letzten Maus-Koordinaten
	
	public static int 
		level = 1,				// aktuelle Level [1 - 51]
		max_level = 1,			// höchstes erreichtes Level
		timeOfDay = 1,			// Tageszeit; = 0: Nacht; = 1: Tag
		money = 0, 				// Guthaben
		kills_after_levelup,	// Anhand dieser Anzahl wird ermittelt, ob ein Level-Up erfolgen muss.
		last_creation_timer,	// Timer stellt sicher, dass ein zeitlicher Mindestabstand zwischen der Erstellung zweier Gegner liegt
        overallEarnings, 		// Gesamtverdienst
        extraBonusCounter, 	// Summe aller Extra-Boni (Multi-Kill-Belohnungen, Abschuss von Mini-Bossen und Geld-PowerUps)
		last_bonus, 			// für die Guthabenanzeige: zuletzt erhaltener Standard-Verdienst
		last_extra_bonus,		// für die Guthabenanzeige: zuletzt erhaltener Extra-Bonus
		last_multi_kill,		// für die Multi-Kill-Anzeige: Art des letzten Multi-Kill 
            commendationTimer,		// reguliert, wie lange die Multi-Kill-Anzeige zu sehen ist
		heliosMaxMoney;
	
    public static long
    	playing_time;            // bisher vergangene Spielzeit
    	public static long time_aktu;				// Zeitpunkt der letzten Aktualisierung von playing_time
	
	public static long
		recordTime[][] = new long [Helicopter.NR_OF_TYPES][5];	// für jede Helikopter-Klasse die jeweils beste Zeit bis zum Besiegen eines der 5 Boss-Gegner
    
    public static boolean
    	restart_window_visible,				// = true: Neustart-Fenster wird angezeigt
    	reachedLevelTwenty[] = new boolean[Helicopter.NR_OF_TYPES],
    	settingsChanged = false,
    	all_playable = false,
    	save_anyway = false;
	
	public static WindowTypes
		window = STARTSCREEN;	// legt das aktuelle Spiel-Menü fest; siehe interface Constants
  
	// Variablen zur Nutzung von Cheats und Freischaltung von Helikoptern
	private static boolean 
		cheating_mode = false,				// = true: Cheat-Modus aktiviert
		cheats_activatable = true;

	private static String 
		cheat_string = "";					
	
	private static final String 
		cheat_code = "+cheats";			// Code, mit welchem Cheats aktiviert werden können
	
	public static Enemy
		boss;	// Referenz auf den aktuellen Endgegner

	public static HelicopterTypes
		nextHelicopterType = HelicopterTypes.getDefault();


	static void keyTyped( KeyEvent e, Controller controller,
	                      Helicopter helicopter, Savegame savegame)
	{	
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE && !helicopter.damaged)
		{
			if(window == GAME){changeVisibilityOfInGameMenu(helicopter);}
			else if(window == STARTSCREEN){
				Controller.shut_down();}
			else if(window != REPAIR_SHOP)
			{
				cancel(controller.bgObject, helicopter, savegame);
			}
		}		
		else if(window == SETTINGS && Menu.page == 4)
		{
			int name_length = HighscoreEntry.currentPlayerName.length();
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				Menu.page = 0;
				HighscoreEntry.checkName(savegame);
			}
			else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			{				
				if(name_length > 0)
				{
					HighscoreEntry.currentPlayerName
					= HighscoreEntry.currentPlayerName.substring(0, name_length-1);
				}
			}
			else if(name_length < 15 
					&& ((e.getKeyCode() >= 65 && e.getKeyCode() <= 90)
						|| e.getKeyCode() == 0 
						|| e.getKeyCode() == 32
						|| e.getKeyCode() == 45))
			{
				HighscoreEntry.currentPlayerName += e.getKeyChar();
			}			
		}		
		else if(e.getKeyChar() =='f')
		{
			controller.switch_FPS_visible_state();
		}		
		else if(e.getKeyChar() =='p')
		{
			if(window == GAME && !helicopter.damaged)
			{
				changeVisibilityOfInGameMenu(helicopter);
			}
		}		
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if(	window == GAME
				&&	helicopter.active
				&& !helicopter.damaged 
				&& !Menu.menue_visible)
			{
				helicopter.turn_around();
			}
		}		
		else if(cheating_mode)
		{			
			if(e.getKeyChar() == 'e')
			{
				if(window == GAME || window == REPAIR_SHOP)
				{
					if(money == 0)
					{
						if(window == GAME){last_bonus = MAX_MONEY - money;}
						money = MAX_MONEY;
						helicopter.no_cheats_used = false;
					}
					else
					{
						last_bonus = 0;
						money = 0;					
					}				
					Menu.money_display_timer = START;
				}
			}				
			else if(e.getKeyChar() == 'u')
			{
				if(window == GAME || window == REPAIR_SHOP)
				{
					if(!helicopter.has_all_upgrades())
					{
						Audio.play(Audio.cash);
						if(helicopter.hasSomeUpgrades()){helicopter.obtainAllUpgrades();}
						else {helicopter.obtainSomeUpgrades();}
					}
					else{Audio.play(Audio.block);}
				}					
			}
			else if(window == GAME)
			{				
				if(e.getKeyChar() == 'l')
				{				
					if(level < 50)
					{
						int nr_of_levelUp =    level - (level%5)
											+ (level % 5 == 0 && !isBossLevel() ? 0 : 5);
						nr_of_levelUp = nr_of_levelUp 
										+ (nr_of_levelUp%10 == 0 ? 0 : 1) 
										- level;
						if(isBossLevel()){nr_of_levelUp = 1;}
						playing_time += (nr_of_levelUp + MyMath.random(nr_of_levelUp)) * 60000;
						level_up(controller, nr_of_levelUp);
						helicopter.no_cheats_used = false;
					}					
				}
				else if(e.getKeyChar() == '+')
				{
					if(level < 50)
					{
						playing_time += (1 + (MyMath.toss_up(0.4f) ? 1 : 0)) * 60000;
						level_up(controller, 1);
						helicopter.no_cheats_used = false;
					}
				}
				else if(e.getKeyChar() =='s')
				{
					Menu.special_info_selection = (Menu.special_info_selection+1)%14;
				}
				else if(e.getKeyChar() == 'd'){helicopter.getPowerUp(controller.powerUp, TRIPLE_DMG, 	   true);}
				else if(e.getKeyChar() == 'i'){helicopter.getPowerUp(controller.powerUp, INVINCIBLE, 	   true);}
				else if(e.getKeyChar() == 'c'){helicopter.getPowerUp(controller.powerUp, UNLIMITRED_ENERGY, true);}
				else if(e.getKeyChar() == 'y'){helicopter.getPowerUp(controller.powerUp, BOOSTED_FIRE_RATE, true);}
				else if(e.getKeyChar() == 'a')
				{
					if(level < 51)
					{
						helicopter.repair(true, true);
						restart_window_visible = false;	
						helicopter.no_cheats_used = false;
					}								
				}
				else if(e.getKeyChar() == 'm')
				{
					Enemy.miniboss_prob = Enemy.miniboss_prob == 0.05f ? 1.0f : 0.05f;
					helicopter.no_cheats_used = false;
				}
				else if(e.getKeyChar() == 'n')
				{
					helicopter.currentPlating = 0f;
					helicopter.crash();
				}
				else if(e.getKeyChar() == 't'){playing_time += 60000;}
			}
			else if(window == STARTSCREEN)
			{
				// Resetten der Helicopter-Bestzeiten
				if(e.getKeyChar() == 'x')
				{					
					all_playable = !all_playable;
				}
				else if(e.getKeyChar() == '-')
				{					
					if(!MyMath.is_empty(recordTime))
					{
						for(int i = 0; i < Helicopter.NR_OF_TYPES; i++)
						{
							Arrays.fill(recordTime[i], 0);
							reachedLevelTwenty[i] = false;
						}
						heliosMaxMoney = get_helios_max_money();
						savegame.save_in_highscore();
						savegame.save_to_file(helicopter, false);
						Audio.play(Audio.emp);
					}
				}
				else if(e.getKeyChar() == '#')
				{					
					if((helicopter.no_cheats_used || save_anyway) && savegame.valid)
					{
						savegame.save_in_highscore();
					}
					savegame.save_to_file(helicopter, false);
					Audio.play(Audio.emp);
				}
			}
		}
		else if(cheats_activatable)
		{
			if(e.getKeyChar() == cheat_code.charAt(cheat_string.length())){cheat_string += e.getKeyChar();}
			else{cheat_string = "";}
			if(cheat_string.equals(cheat_code)){cheating_mode = true;}		
		}		
	}
	
	static void mousePressed(MouseEvent e,
	                         Controller controller,
	                         Helicopter helicopter)
	{
		if(e.getButton() == 1)
		{
			cursor.setLocation(e.getX()-Main.displayShift.width,
							   e.getY()-Main.displayShift.height);
			mousePressedLeft(controller, helicopter);
		}
		else if( 	
					window == GAME
					&&	helicopter.active
					&& !helicopter.damaged 
					&& !Menu.menue_visible)
		{
			if(e.getButton() == 3)
			{
				helicopter.energy_ability_used(controller.powerUp, controller.explosion);
			}
			else{helicopter.turn_around();}
		}	
	}
	
	private static void mousePressedLeft(Controller controller, Helicopter helicopter)
	{		
		controller.bgRepaint = READY;
		if(window == GAME)
		{
			inGameMousePressedLeft(controller, helicopter);
		}
		else if(window == REPAIR_SHOP)
		{
			repairShopMousePressedLeft(helicopter, controller.enemy, controller.bgObject);
		}
		else if(window == STARTSCREEN)
		{
			startscreenMousePressedLeft(helicopter);
		}
		else if(Menu.startscreen_menu_button.get("Cancel").bounds.contains(cursor))
		{
			cancel(controller.bgObject, helicopter, Controller.savegame);
		}
		else 
		{		
			startscreenMenuButtonClicked(controller.offGraphics,
										 helicopter,
										 Controller.savegame);
		}
	}	

	private static void inGameMousePressedLeft(Controller controller, Helicopter helicopter)
	{
		if(!helicopter.damaged)
		{
			if(Menu.menue_visible)
			{
				if(Menu.inGameButton.get("MMNewGame1").bounds.contains(cursor))
				{					
					Controller.savegame.save_to_file(helicopter, true);
					conditionalReset(controller, helicopter, true);
					restart_game(helicopter, controller.bgObject);
					Audio.applause1.stop();
				}
				else if(Menu.inGameButton.get("MMStopMusic").bounds.contains(cursor))
				{
					Audio.play(Audio.choose);
					switchAudioActivationState(Controller.savegame);
				}
				else if(Menu.inGameButton.get("MMNewGame2").bounds.contains(cursor))
				{					
					Controller.savegame.save_to_file(helicopter, true);
					Controller.shut_down();
				}
				else if( 	(Menu.inGameButton.get("MainMenu").bounds.contains(cursor)
							&& helicopter.is_on_the_ground())
						 || (Menu.inGameButton.get("MMCancel").bounds.contains(cursor)))
				{
					changeVisibilityOfInGameMenu(helicopter);
				}
			}			
			else if(helicopter.is_on_the_ground())
			{
				if(Menu.inGameButton.get("RepairShop").bounds.contains(cursor))
				{
					// Betreten der Werkstatt über den Werkstatt-Button
					conditionalReset(controller, helicopter, false);
					enterRepairShop(helicopter);
				}
				else if(Menu.inGameButton.get("MainMenu").bounds.contains(cursor))
				{
					changeVisibilityOfInGameMenu(helicopter);
				}				
				else if(!helicopter.active && cursor.y < 426)
				{
					helicopter.set_activation_state(true);
				}
			}
			else if(helicopter.active){helicopter.continious_fire = true;}
		}
		else if(	Menu.inGameButton.get("MMNewGame2").bounds.contains(cursor)
				 && restart_window_visible)
		{
			// Betreten der Werkstatt nach Absturz bzw. Neustart bei Geldmangel				
			conditionalReset(controller, helicopter, true);
			if(money < repair_fee(helicopter, true) || level > 50)
			{
				if(level > 50)
				{
					playing_time = 60000 * helicopter.scorescreen_times[4];
				}
				else
				{
					playing_time = playing_time 
						     	   + System.currentTimeMillis() 
						           - time_aktu;
					helicopter.scorescreen_times[4] = playing_time/60000;
				}				
							   
				changeWindow(SCORESCREEN);	
															
				helicopter.damaged = false;
				Controller.savegame.save_to_file(helicopter, true);
				MyColor.update_scorescreen_colors(helicopter);
			}
			else{enterRepairShop(helicopter);}
		}
	}
	
	private static void repairShopMousePressedLeft( Helicopter helicopter, 
	                                                ArrayList<LinkedList<Enemy>> enemy, 
	                                                ArrayList<LinkedList<BackgroundObject>> bgObject)
	{
		// Reparatur des Helikopters
		if(Menu.repairShopButton.get("RepairButton").bounds.contains(cursor))
		{
			if(	helicopter.currentPlating == helicopter.max_plating())
			{
				Menu.block(1);
			}
			else if(money < repair_fee(helicopter, helicopter.damaged))
			{
				Menu.block(9);
			}
			else
			{
				money -= repair_fee(helicopter, helicopter.damaged);				
				timeOfDay = (!helicopter.spotlight || MyMath.toss_up(0.33f)) ? DAY : NIGHT;
				Menu.repairShopButton.get("Einsatz").label = Button.MISSION[Menu.language.ordinal()][timeOfDay];
												
				if(!(level == 50 && helicopter.has_all_upgrades()))
				{
					enemy.get(INACTIVE).addAll(enemy.get(ACTIVE));
					enemy.get(ACTIVE).clear();			
					level = level - ((level - 1) % 5);						
					Enemy.adapt_to_level(helicopter, level, false);
					if(level < 6){
						BackgroundObject.reset(bgObject);}
					kills_after_levelup = 0;
					enemy.get(INACTIVE).addAll(enemy.get(DESTROYED));
					enemy.get(DESTROYED).clear();
					Menu.repairShopButton.get("RepairButton").costs = 0;
					Enemy.currentRock = null;
				}
				else
				{
					Enemy.boss_selection = -7;
					Enemy.max_nr = 1;
					Enemy.max_barrier_nr = 0;
				}
				
				helicopter.repair(level == 50, false);
			}
		}		
		// Einsatz fliegen
		else if(Menu.repairShopButton.get("Einsatz").bounds.contains(cursor))
		{
			if(helicopter.damaged){
				Menu.block(2);}
			else
			{				
				Menu.stopButtonHighlighting(Menu.repairShopButton);
				startMission(helicopter);				
			}
		}
				
		/*
		 * Die Spezial-Upgrades
		 */
		
		// Scheinwerfer
		else if(Menu.repairShopButton.get("Special0").bounds.contains(cursor))
		{
			if(helicopter.damaged){
				Menu.block(4);}
			else if(helicopter.spotlight){
				Menu.block(7);}
			else if(money < SPOTLIGHT_COSTS){
				Menu.block(6);}
			else
			{
				Audio.play(Audio.cash);
				money -= SPOTLIGHT_COSTS;				
				helicopter.spotlight = true;
				timeOfDay = NIGHT;				
				Menu.repairShopButton.get("Einsatz").label = Button.MISSION[Menu.language.ordinal()][timeOfDay];
				Menu.repairShopButton.get("Einsatz").second_label = Button.SOLD[Menu.language.ordinal()][helicopter.spotlight ? 1 : 0];
				Menu.repairShopButton.get("Special" + 0).costs = 0;
				for(Enemy e : enemy.get(DESTROYED))
				{
					e.repaint();
				}
				for(Enemy e : enemy.get(ACTIVE))
				{
					if (Enemy.currentRock != e)
					{
						e.farbe1 = MyColor.dimColor(e.farbe1, MyColor.BARRIER_NIGHT_DIM_FACTOR);
						e.farbe2 = MyColor.dimColor(e.farbe2, MyColor.BARRIER_NIGHT_DIM_FACTOR);
						e.repaint();
					}
				}
			}
		}
		// Goliath-Panzerung
		else if(Menu.repairShopButton.get("Special1").bounds.contains(cursor))
		{
			if(helicopter.damaged){
				Menu.block(4);}
			else if(helicopter.hasGoliathPlating()){
				Menu.block(7);}
			else if(money < STANDARD_GOLIATH_COSTS && !((helicopter.getType() == PHOENIX ||(helicopter.getType() == HELIOS && recordTime[PHOENIX.ordinal()][4]!=0)) && money >= PHOENIX_GOLIATH_COSTS))
			{
				Menu.block(6);
			}
			else
			{
				Audio.play(Audio.cash);						
				money -= (helicopter.getType() == PHOENIX ||(helicopter.getType() == HELIOS && recordTime[PHOENIX.ordinal()][4]!=0)) ? PHOENIX_GOLIATH_COSTS : STANDARD_GOLIATH_COSTS;
				helicopter.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
				helicopter.currentPlating += MyMath.plating(helicopter.levelOfUpgrade[PLATING]);
				helicopter.setPlatingColor();
				Menu.repairShopButton.get("Special" + 1).costs = 0;
			}
		}
		// Durchstoßsprengköpfe
		else if(Menu.repairShopButton.get("Special2").bounds.contains(cursor))
		{
			if(helicopter.damaged){
				Menu.block(4);}
			else if(helicopter.hasPiercingWarheads){
				Menu.block(7);}
			else if(money < STANDARD_SPECIAL_COSTS && !((helicopter.getType() == ROCH ||(helicopter.getType() == HELIOS && recordTime[ROCH.ordinal()][4]!=0)) && money >= CHEAP_SPECIAL_COSTS))
			{
				Menu.block(6);
			}
			else
			{
				Audio.play(Audio.cash);
				if((helicopter.getType() == ROCH ||(helicopter.getType() == HELIOS && recordTime[ROCH.ordinal()][4]!=0))){money -= CHEAP_SPECIAL_COSTS;}
				else{money -= STANDARD_SPECIAL_COSTS;}
				helicopter.hasPiercingWarheads = true;
				Menu.repairShopButton.get("Special" + 2).costs = 0;
			}
		}
		// zusätzliche Bordkanonen
		else if(Menu.repairShopButton.get("Special3").bounds.contains(cursor))
		{
			if(helicopter.damaged){
				Menu.block(4);}
			else if(helicopter.hasAllCannons())
			{
				Menu.block(7);
			}
			else if(	(money < STANDARD_SPECIAL_COSTS || (helicopter.getType() == ROCH && money < ROCH_SECOND_CANNON_COSTS)) &&
						!((helicopter.getType() == OROCHI ||(helicopter.getType() == HELIOS && recordTime[OROCHI.ordinal()][4]!=0)) && money >= CHEAP_SPECIAL_COSTS && helicopter.nr_of_cannons == 1))
			{
				Menu.block(6);
			}
			else
			{
				Audio.play(Audio.cash);
				if((helicopter.getType() == OROCHI ||(helicopter.getType() == HELIOS && recordTime[OROCHI.ordinal()][4]!=0)) && helicopter.nr_of_cannons == 1)
				{
					money -= CHEAP_SPECIAL_COSTS;
					if(helicopter.getType() == OROCHI)
					{
						Menu.repairShopButton.get("Special" + 3).costs = STANDARD_SPECIAL_COSTS;
						Menu.repairShopButton.get("Special" + 3).label = Menu.THIRD_CANNON[Menu.language.ordinal()];
						Menu.repairShopButton.get("Special" + 3).cost_color = MyColor.costsColor[REGULAR.ordinal()];
					}
					else
					{
						Menu.repairShopButton.get("Special" + 3).costs = 0;
					}					
				}
				else
				{
					money -= helicopter.getType() == ROCH ? ROCH_SECOND_CANNON_COSTS : STANDARD_SPECIAL_COSTS;
					Menu.repairShopButton.get("Special" + 3).costs = 0;
				}
				helicopter.nr_of_cannons++;			
			}
		}
		// das klassenspezifische SpezialUpgrade
		else if(Menu.repairShopButton.get("Special4").bounds.contains(cursor))
		{
			if(helicopter.damaged){
				Menu.block(4);}
			else if(helicopter.hasFifthSpecial()){
				Menu.block(7);}
			else if(money < CHEAP_SPECIAL_COSTS || (helicopter.getType() == ROCH && money < JUMBO_MISSILE_COSTS))
			{
				Menu.block(6);
			}
			else
			{
				Audio.play(Audio.cash);
				money -= helicopter.getType() == ROCH ? JUMBO_MISSILE_COSTS : CHEAP_SPECIAL_COSTS;
				helicopter.obtainFifthSpecial();
				if(helicopter.getType() == KAMAITACHI || helicopter.getType() == PEGASUS){helicopter.adjustFireRate(false);}
				Menu.repairShopButton.get("Special" + 4).costs = 0;
			}
		}
			
		/*
		 *  Die Standard-Upgrades
		 */			
		else 
		{	
			int selection = Integer.MIN_VALUE;
			for(int i = 0; i < 6; i++)
			{
				if(Menu.repairShopButton.get("StandardUpgrade" + i).bounds.contains(cursor))
				{
					if(helicopter.damaged){
						Menu.block(4);}
					else if(helicopter.has_max_upgrade_level[i]){
						Menu.block(5);}
					else if(money < MyMath.costs(helicopter.getType(), helicopter.upgrade_costs[i], helicopter.levelOfUpgrade[i]))
					{
						Menu.block(6);
					}
					else
					{
						Audio.play(Audio.cash);
						money -= MyMath.costs(helicopter.getType(), helicopter.upgrade_costs[i], helicopter.levelOfUpgrade[i]);
						helicopter.levelOfUpgrade[i]++;
						if(helicopter.levelOfUpgrade[i] >= MyMath.max_level(helicopter.upgrade_costs[i]))
						{
							helicopter.has_max_upgrade_level[i] = true;
							Menu.repairShopButton.get("StandardUpgrade" + i).costs = 0;
						}
						else
						{
							Menu.repairShopButton.get("StandardUpgrade" + i).costs = MyMath.costs(helicopter.getType(), helicopter.upgrade_costs[i], helicopter.levelOfUpgrade[i]);
						}
						selection = i;
					}					
					break;
				}
			}
			if(selection == Integer.MIN_VALUE){/**/}
			else if(selection == 0)
			{
				helicopter.rotorSystem = MyMath.speed(helicopter.levelOfUpgrade[ROTOR_SYSTEM]);
			}
			else if(selection == 1)
			{
				helicopter.missileDrive = MyMath.missile_drive(helicopter.levelOfUpgrade[MISSILE_DRIVE]);
			}
			else if(selection == 2)
			{
				helicopter.currentPlating
					+= helicopter.platingDurabilityFactor
					   * ( MyMath.plating(helicopter.levelOfUpgrade[PLATING])
						   -MyMath.plating(helicopter.levelOfUpgrade[PLATING]-1));
				helicopter.setPlatingColor();
			}
			else if(selection == 3)
			{
				helicopter.currentFirepower = (int)(helicopter.jumboMissiles * MyMath.dmg(helicopter.levelOfUpgrade[FIREPOWER]));
			}
			else if(selection == 4)
			{
				helicopter.adjustFireRate(false);
			}
			else if(selection == 5)
			{
				helicopter.upgrade_energy_ability();
			}
		}
	}
		
	private static void startscreenMousePressedLeft(Helicopter helicopter)
	{
		if(Menu.triangle[0].contains(cursor))
		{
			Menu.crossPosition = (Menu.crossPosition + 1)%Helicopter.NR_OF_TYPES;
			Menu.cross = Menu.getCrossPolygon();
			Menu.helicopter_selection = (Menu.helicopter_selection + Helicopter.NR_OF_TYPES - 1)%Helicopter.NR_OF_TYPES;
			Audio.play(Audio.choose);
		}
		else if(Menu.triangle[1].contains(cursor))
		{
			Menu.crossPosition = (Menu.crossPosition + Helicopter.NR_OF_TYPES - 1)%Helicopter.NR_OF_TYPES;
			Menu.cross = Menu.getCrossPolygon();
			Menu.helicopter_selection = (Menu.helicopter_selection + 1)%Helicopter.NR_OF_TYPES;
			Audio.play(Audio.choose);
		}
		else if(Menu.helicopter_frame[0].contains(cursor)||
				Menu.helicopter_frame[1].contains(cursor)||
				Menu.helicopter_frame[2].contains(cursor)||
				Menu.helicopter_frame[3].contains(cursor))
		{				
			if(all_playable || Helicopter.isUnlocked(Events.nextHelicopterType))
			{
				startGame(Events.nextHelicopterType, Controller.savegame, true);
			}
			else
			{
				Audio.play(Audio.block);
				Menu.crossPosition = (Events.nextHelicopterType.ordinal() - Menu.helicopter_selection + Helicopter.NR_OF_TYPES)%Helicopter.NR_OF_TYPES;
				Menu.cross = Menu.getCrossPolygon();
				Menu.crossTimer = 1;
				Menu.messageTimer = 1;
				Menu.setStartscreenMessage(Events.nextHelicopterType);
			}
		}
		else if(Menu.startscreen_button.get("00").bounds.contains(cursor))
		{			
			newStartscreenMenuWindow(INFORMATIONS, true);
			Menu.startscreen_menu_button.get("2").marked = true;
		}
		else if(Menu.startscreen_button.get("01").bounds.contains(cursor))
		{			
			newStartscreenMenuWindow(HIGHSCORE, true);
		}
		else if(Menu.startscreen_button.get("02").bounds.contains(cursor))
		{			
			newStartscreenMenuWindow(CONTACT, true);
		}
		else if(Menu.startscreen_button.get("10").bounds.contains(cursor))
		{			
			newStartscreenMenuWindow(SETTINGS, true);
			if(HighscoreEntry.currentPlayerName.equals("John Doe"))
			{
				Menu.startscreen_menu_button.get("4").marked = true;
			}
		}
		else if(Menu.startscreen_button.get("11").bounds.contains(cursor))
		{
			if(Controller.savegame.valid)
			{
				Audio.play(Audio.level_up);
				startGame(Controller.savegame);
			}
			else{Audio.play(Audio.block);}			
		}		
		else if(Menu.startscreen_button.get("12").bounds.contains(cursor))
		{					
			Controller.shut_down();
		}		
	}
	
	private static void cancel( ArrayList<LinkedList<BackgroundObject>> bgObject,
	                            Helicopter helicopter, Savegame savegame)
	{
		Audio.play(Audio.choose);
		if(window == SCORESCREEN)
		{				
			savegame.save_in_highscore();
			restart_game(helicopter, bgObject);	
			savegame.save_to_file(helicopter, false);										
			Menu.startscreen_menu_button.get("Cancel").highlighted = false;
		}
		else if(window == DESCRIPTION)
		{
			if(Menu.page == 5){
				Menu.label.setBounds(Main.displayShift.width  + 42,
													   Main.displayShift.height + 83, 940, 240);}
			newStartscreenMenuWindow(INFORMATIONS, false);
			Menu.startscreen_menu_button.get("2").marked = true;
			Menu.startscreen_menu_button.get("6").marked = false;
		}
		else if(window == HELICOPTER_TYPES)
		{
			if(Menu.page == 1){
				Menu.label.setVisible(true);}
			newStartscreenMenuWindow(DESCRIPTION, false);
			Menu.startscreen_menu_button.get("6").marked = true;
		}
		else
		{
			if(window == INFORMATIONS)
			{
				Menu.startscreen_menu_button.get("2").marked = false;
			}
			else if(window == SETTINGS)
			{
				Menu.startscreen_menu_button.get("4").marked = false;
				HighscoreEntry.checkName(savegame);										
				if(settingsChanged)
				{					
					savegame.writeToFile();
					settingsChanged = false;
				}
			}
			Menu.label.setVisible(false);
			Menu.stopButtonHighlighting(Menu.startscreen_menu_button);
			window = STARTSCREEN;
		}
	}
	
	private static void startscreenMenuButtonClicked(Graphics2D offGraphics,
													 Helicopter helicopter,
													 Savegame savegame)
	{
		for(int m = 0; m < 8; m++)
		{
			Button current_button = Menu.startscreen_menu_button.get(Integer.toString(m));
			if(	 current_button.bounds.contains(cursor) && 
				 !current_button.label.equals("") && 
				(Menu.page != m || window == SETTINGS))
			{						
				int old_page = Menu.page;
				if(window == DESCRIPTION && Menu.page == 5)
				{
					Menu.label.setBounds(Main.displayShift.width  + 42,
										    Main.displayShift.height + 83, 940, 240);
				}
				else if(window == HELICOPTER_TYPES && Menu.page == 1)
				{
					Menu.label.setVisible(true);
				}
				Menu.page = m;
				if(window == DESCRIPTION && Menu.page == 5)
				{
					Menu.label.setBounds(Main.displayShift.width  + 92,
											Main.displayShift.height + 83, 890, 240);
				}
				else if(window == HELICOPTER_TYPES && Menu.page == 1)
				{
					Menu.label.setVisible(false);
				}
				if(window == INFORMATIONS && Menu.page == 2)
				{							
					newStartscreenMenuWindow(DESCRIPTION, false);	
					Menu.startscreen_menu_button.get("2").marked = false;
					Menu.startscreen_menu_button.get("6").marked = true;
				}
				else if(window == DESCRIPTION && Menu.page == 6)
				{
					newStartscreenMenuWindow(HELICOPTER_TYPES, false);
					Menu.startscreen_menu_button.get("6").marked = false;
				}
				else if(window == SETTINGS)
				{					
					settingsMousePressedLeft(helicopter, 
											 current_button, 
											 offGraphics, 
											 savegame, 
											 old_page);
				}
				else
				{
					Audio.play(Audio.choose);					
					Menu.update_labeltext();
				}
				break;
			}
		}		
	}
	
	private static void settingsMousePressedLeft( Helicopter helicopter, 
	                                              Button current_button,
												  Graphics2D offGraphics, 
												  Savegame savegame,
												  int old_page)
	{
		if(Menu.page == 0)
		{	
			Main.switchDisplayMode(current_button);
		}
		else if(Menu.page == 1)
		{			
			switchAntialiasingActivationState(offGraphics, current_button);			
		}						
		else if(Menu.page == 2)
		{
			switchAudioActivationState(savegame);
		}
		else if(Menu.page == 3)
		{
			Menu.changeLanguage(helicopter, savegame);
		}		
		else if(Menu.page == 6)
		{
			Audio.changeBgMusicMode(savegame);
		}
		if(old_page == 4)
		{
			if(Menu.page == 4){
				Menu.page = 0;}
			HighscoreEntry.checkName(savegame);
		}
		Menu.update_labeltext();
	}
	
	private static void switchAntialiasingActivationState(	Graphics2D offGraphics,
															Button current_button)
	{
		Controller.antialiasing = !Controller.antialiasing;
		offGraphics.setRenderingHint( 
				RenderingHints.KEY_ANTIALIASING,
				Controller.antialiasing ?
					RenderingHints.VALUE_ANTIALIAS_ON : 
					RenderingHints.VALUE_ANTIALIAS_OFF);
		STARTSCREEN_MENU_BUTTON[ENGLISH.ordinal()][2][1]
		    = Button.ANTIALIAZING[ENGLISH.ordinal()][Controller.antialiasing ? 0 : 1];
		STARTSCREEN_MENU_BUTTON[GERMAN.ordinal()][2][1]
		    = Button.ANTIALIAZING[GERMAN.ordinal()][Controller.antialiasing ? 0 : 1];
		current_button.label
		    = Button.ANTIALIAZING[Menu.language.ordinal()][Controller.antialiasing ? 0 : 1];
	}

	static void mouseReleased(MouseEvent e, Helicopter helicopter)
	{
		if(e.getButton() == 1)
		{
			helicopter.continious_fire = false;
		}
		else if(window == GAME && e.getButton() == 3 && !helicopter.damaged)
		{			
			if(helicopter.getType() == PHOENIX)
			{
				helicopter.teleport_to(	e.getX()-Main.displayShift.width,
										e.getY()-Main.displayShift.height);
			}			
			else if(helicopter.getType() == OROCHI)
			{
				helicopter.next_missile_is_stunner = false;
			}		
		}		
	}
	
	// Aktualisierung der Ziel-Koordinaten, auf welche der Helikopter zufliegt
	static void mouseMovedOrDragged(MouseEvent e, Helicopter helicopter)
	{		
		if(!helicopter.damaged || window == REPAIR_SHOP)
		{
			if(!helicopter.search4teleportDestination)
			{
				helicopter.destination.setLocation(
						e.getX()-Main.displayShift.width,
						e.getY()-Main.displayShift.height);
			}
			else
			{
				helicopter.destination.setLocation(helicopter.prior_teleport_location);
			}
		}	
	}
	
	private static void initialize(Helicopter helicopter, 
	                               Savegame savegame, 
	                               boolean new_game)
	{			
		if(new_game){reset();}
		else{restore(savegame);}
		changeWindow(GAME);	
		time_aktu = System.currentTimeMillis();			
		for(int i =  level - ((level - 1) % 5); i <= level; i++)
		{
			Enemy.adapt_to_level(helicopter, i, false);
		}
	}

	// Rücksetzen einiger Variablen bei Neustart des Spiels
	private static void reset()
	{
		money = 0;
		level = 1;
		max_level = 1;
		timeOfDay = DAY;
		overallEarnings = 0;
		extraBonusCounter = 0;
		playing_time = 0;
	}

	// Wiederherstellen einiger Variablen anhand eines gespeicherten Spielstandes
	private static void restore(Savegame savegame)
	{
		money = savegame.money;
		kills_after_levelup = savegame.kills_after_levelup;
		level = savegame.level;
		max_level = savegame.max_level;
		timeOfDay = savegame.timeOfDay;
		overallEarnings = savegame.bonus_counter;
		extraBonusCounter = savegame.extra_bonus_counter;					
		playing_time = savegame.playing_time;
	}

	/* Reset: Zurücksetzem diverser spielinterner Variablen; 
	 * bedingter (conditional) Rest, da unterschieden wird, ob nur die 
	 * Werkstatt betreten oder das Spiel komplett neu gestartet wird
	 */
	private static void conditionalReset(Controller controller, Helicopter helicopter, boolean total_reset)
	{
		Audio.play(Audio.choose);
		
		helicopter.bonus_kills_timer = 1;
		if(helicopter.getType() == KAMAITACHI){helicopter.evaluate_bonus_kills();}
		
		helicopter.reset_state(total_reset);		
		boss = null;		
		last_extra_bonus = 0;
		last_multi_kill = 0;
		commendationTimer = 0;		
		restart_window_visible = false;
		Menu.menue_visible = false;
		last_bonus = 0;
		Menu.money_display_timer = DISABLED;
		Menu.level_display_timer = START;
		Menu.unlocked_timer = 0;
				
		// kein "active enemy"-Reset, wenn Bossgegner 2 Servants aktiv
		if(!controller.enemy.get(ACTIVE).isEmpty()
		   && !(!total_reset && controller.enemy.get(ACTIVE).getFirst().type == BOSS_2_SERVANT))
		{
			// Boss-Level 4 oder 5: nach Werkstatt-Besuch erscheint wieder der Hauptendgegner
			if(	level == 40 || level == 50)
			{
				Enemy.boss_selection = level == 40 ? -4 : -7;
				Enemy.max_nr = 1;
				Enemy.max_barrier_nr = 0;
			}			
			if(total_reset)
			{
				controller.enemy.get(INACTIVE).addAll(controller.enemy.get(ACTIVE));
				controller.enemy.get(ACTIVE).clear();
				Enemy.currentRock = null;
			}
			else
			{
				for(Iterator<Enemy> i = controller.enemy.get(ACTIVE).iterator(); i.hasNext();)
				{
					Enemy e = i.next();					
					if(!e.is_lasting)
					{
						controller.enemy.get(INACTIVE).add(e);
						i.remove();
					}
				}
			}
			Enemy.current_mini_boss = null;
		}
		if(total_reset)
		{
			kills_after_levelup = 0;
			controller.enemy.get(INACTIVE).addAll(controller.enemy.get(DESTROYED));
			controller.enemy.get(DESTROYED).clear();
			if(level < 6){
				BackgroundObject.reset(controller.bgObject);}
		}									
		controller.explosion.get(INACTIVE).addAll(controller.explosion.get(ACTIVE));
		controller.explosion.get(ACTIVE).clear();
		controller.missile.get(INACTIVE).addAll(controller.missile.get(ACTIVE));
		controller.missile.get(ACTIVE).clear();
		controller.enemyMissile.get(INACTIVE).addAll(controller.enemyMissile.get(ACTIVE));
		controller.enemyMissile.get(ACTIVE).clear();
		controller.powerUp.get(INACTIVE).addAll(controller.powerUp.get(ACTIVE));
		controller.powerUp.get(ACTIVE).clear();
		if(Menu.collected_PowerUp[3] != null)
		{
			helicopter.adjustFireRate(false);
		}
		for(int i = 0; i < 4; i++){
			Menu.collected_PowerUp[i] = null;}
	}

	static private void startGame(Savegame savegame)
	{
		startGame(savegame.helicopterType, savegame, false);
	}

	static private void startGame(HelicopterTypes helicopterType, Savegame savegame, boolean isNewGame)
	{
		Helicopter helicopter = HelicopterFactory.create(helicopterType);
		Controller.getInstance().setHelicopter(helicopter);

		if(isNewGame)
		{
			Audio.play(Audio.applause1);
			savegame.save_in_highscore();
		}
		Menu.stopButtonHighlighting(Menu.startscreen_button);
		Menu.cross = null;
		Menu.crossTimer = 0;
		Menu.messageTimer = 0;
		Audio.play(Audio.choose);

		helicopter.initialize(isNewGame, savegame);
		initialize(helicopter, savegame, isNewGame);
		Button.initialize(helicopter);
		if(isNewGame){
			Controller.savegame.save_to_file(helicopter, true);}
		else{
			Menu.update_repairShopButtons(helicopter);}
	}
		
	private static void restart_game(Helicopter helicopter, ArrayList<LinkedList<BackgroundObject>> bgObject)
	{		
		changeWindow(STARTSCREEN);	
		helicopter.reset();
		BackgroundObject.reset(bgObject);
	}

	private static void startMission(Helicopter helicopter)
	{		
		changeWindow(GAME);		
		Audio.play(Audio.choose);	
		time_aktu = System.currentTimeMillis();
		helicopter.rotor_position[helicopter.getType().ordinal()] = 0;
		Controller.savegame.save_to_file(helicopter, true);
	}
	
	private static void enterRepairShop(Helicopter helicopter)
	{
		changeWindow(REPAIR_SHOP);		
		
		Audio.applause1.stop();
		playing_time += System.currentTimeMillis() - time_aktu;
		Menu.repairShopTime = Menu.returnTimeDisplayText(playing_time);
		helicopter.setPlatingColor();					
		if(helicopter.currentPlating < helicopter.max_plating())
	    {
			Menu.repairShopButton.get("RepairButton").costs = repair_fee(helicopter, helicopter.damaged);
	    }
		else{
			Menu.repairShopButton.get("RepairButton").costs = 0;}
		Menu.clear_message();
		Menu.messageTimer = 0;
	}

	public static int repair_fee(Helicopter helicopter, boolean total_loss)
	{		
		return (total_loss 
					? TOTAL_LOSS_REPAIR_BASE_FEE 
					: DEFAULT_REPAIR_BASE_FEE) 
				+ 25 * Math.round( 6.5f * ( helicopter.max_plating() 
								    		- helicopter.currentPlating));
	}
	
	private static void changeWindow(WindowTypes newWindow)
	{		
		window = newWindow;
		Audio.refreshBackgroundMusic();
		MyColor.bg = newWindow == GAME && timeOfDay == DAY ? MyColor.sky: Color.black;
	}

	private static void newStartscreenMenuWindow(WindowTypes new_window, boolean hasJustEntered)
	{
		if(hasJustEntered){
			Menu.stopButtonHighlighting(Menu.startscreen_button);}
		Audio.play(Audio.choose);		
		window = new_window;
		Menu.adaptToNewWindow(hasJustEntered);
		Button.updateScreenMenueButtons(window);
	}

	// überprüfen, ob Level-Up Voraussetzungen erfüll. Wenn ja: Schwierigkeitssteigerung
	static void checkForLevelup(Controller controller, Helicopter helicopter)
	{
		if( kills_after_levelup >= MyMath.kills(level) && level < 50)
		{
			level_up(controller, 1);
		}
	}

	// erhöht das Spiel-Level auf "nr_of_levelUp" mit allen Konsequenzen
	private static void level_up(Controller controller,
								 int nr_of_levelUp)
	{
		Helicopter helicopter = controller.getHelicopter();
		Audio.play(level + nr_of_levelUp <= 50
					? Audio.level_up 
					: Audio.applause1);
				
		kills_after_levelup = 0;
		int previous_level = level;
		level += nr_of_levelUp;	
				
		if(isBossLevel()){Enemy.get_rid_of_some_enemies(helicopter, controller.enemy, controller.explosion);}
		if(helicopter.getType() == HELIOS && level > max_level){getHeliosIncome(previous_level);}
		
		max_level = level;
		
		if(	isBossLevel() || isBossLevel(previous_level) || level == 49)
		{
			Audio.refreshBackgroundMusic();
			if(previous_level % 10 == 0){Audio.play(Audio.applause1);}
		}
		Menu.level_display_timer = START;
		Enemy.adapt_to_level(helicopter, level, true);
	}

	private static void getHeliosIncome(int previous_level)
	{
		int bonus_sum = 0;
		for(int i = Math.max(previous_level, max_level); i < level; i++)
		{
			bonus_sum += (int)((i/1225f)* heliosMaxMoney);
		}
		last_bonus = (int) (bonus_sum * (timeOfDay == NIGHT ? 1 : ((float)DAY_BONUS_FACTOR)/NIGHT_BONUS_FACTOR));
		money += last_bonus;
		overallEarnings += last_bonus;			
		Menu.money_display_timer = START;
	}

	// Stellt sicher, dass mit dem Besiegen des End-Gegners direkt das nächste Level erreicht wird
	public static void set_boss_level_up_conditions()
	{
			 if(level == 10){kills_after_levelup = 14;}
		else if(level == 20){kills_after_levelup = 7;}
		else if(level == 30){kills_after_levelup = 24;}
		else if(level == 40){kills_after_levelup = 29;}
		else if(level == 50){kills_after_levelup = 34;}
	}
	
	// Bonus-Verdienst bei Multi-Kill
	public static void extra_reward( int kills, int earned_money, float basis,
	                          float increase, float limit)
	{
		Menu.money_display_timer = START;
		if(kills > 1){last_bonus = earned_money;}
		last_extra_bonus = (int)(Math.min(basis + increase * (kills-2), limit) * earned_money);
		last_extra_bonus = Math.round(last_extra_bonus/10f)*10;
		money += last_extra_bonus;
		overallEarnings += last_extra_bonus;
		extraBonusCounter += last_extra_bonus;
		last_multi_kill = kills;
		commendationTimer = 90 + (Math.max(kills, 6)-2) * 25; 
		Audio.praise(kills);
	}
	
	static private void changeVisibilityOfInGameMenu(Helicopter helicopter)
	{
		Audio.play(Audio.choose);
		if(!Menu.menue_visible)
		{
			Menu.menue_visible = true;
			BackgroundObject.background_moves = false;
			playing_time += System.currentTimeMillis() - time_aktu;
		}
		else
		{
			Menu.menue_visible = false;
			time_aktu = System.currentTimeMillis();
			if(helicopter.is_on_the_ground())
			{
				helicopter.set_activation_state(false);
			}
		}
	}

	private static void switchAudioActivationState(Savegame savegame)
	{		
		Audio.isSoundOn = !Audio.isSoundOn;
		savegame.isSoundOn = Audio.isSoundOn;
		settingsChanged = true;
		Audio.refreshBackgroundMusic();
		STARTSCREEN_MENU_BUTTON[ENGLISH.ordinal()][2][2] = Button.MUSIC[ENGLISH.ordinal()][Audio.isSoundOn ? 0 : 1];
		STARTSCREEN_MENU_BUTTON[GERMAN.ordinal()][2][2] = Button.MUSIC[GERMAN.ordinal()][Audio.isSoundOn ? 0 : 1];
		Menu.inGameButton.get("MMStopMusic").label = Button.MUSIC[Menu.language.ordinal()][Audio.isSoundOn ? 0 : 1];
		Menu.startscreen_menu_button.get("2").label = Button.MUSIC[Menu.language.ordinal()][Audio.isSoundOn ? 0 : 1];
	}
	
	
	

	public static void determine_highscore_times(Helicopter helicopter)
	{
		int boss_nr = get_boss_nr();
		long highscore_time = (playing_time + System.currentTimeMillis() - time_aktu)/60000;
		helicopter.scorescreen_times[boss_nr] = highscore_time;			
				
		if(helicopter.no_cheats_used||save_anyway)
		{			
			recordTime[helicopter.getType().ordinal()][boss_nr]
				= recordTime[helicopter.getType().ordinal()][boss_nr] == 0
				  ? highscore_time 
				  : Math.min(recordTime[helicopter.getType().ordinal()][boss_nr], highscore_time);
			heliosMaxMoney = get_helios_max_money();
		}			
	}
	
	static int get_boss_nr()
	{
		if(level%10 != 1) return -1;
		return level/10-1;
	}
	
	public static boolean boss1_killed_b4()
	{
		for(int i = 0; i < Helicopter.NR_OF_TYPES; i++)
		{
			if(recordTime[i][0] != 0) return true;
		}
		return false;
	}
	
	public static int get_helios_max_money()
	{
		int max_helios_money = 0;
		for(int i = 0; i < Helicopter.NR_OF_TYPES - 1; i++)
		{
			max_helios_money += get_highest_record_money(recordTime[i]);
		}	
		return max_helios_money;
	}
	
	// TODO check method function why unused asssignment
	public static int get_highest_record_money(long [] array)
	{	
		if(array[0] == 0){return 0;}
		int index = 0;
		boolean index_set = false;
		int maxMoney = helios_record_entry_money((int)(array[index]), index);
		for(int i = 1; i < array.length; i++)
		{			
			if(array[i] == 0)
			{
				index = i-1; 
				index_set = true;
				break;
			}
			maxMoney = Math.max(maxMoney, helios_record_entry_money((int)(array[i]), i));
		}	
		if(!index_set)
		{
			maxMoney = Math.max(maxMoney, helios_record_entry_money((int)(array[array.length-1]), array.length-1));
		}
		return maxMoney;
	}
	
	public static int helios_record_entry_money(int arrayElement, int index)
	{
		return (int)(( MAX_MONEY * COMPARISON_RECORD_TIME * (index + 1)) / (37.5f * arrayElement*(5-index)*(5-index)));
	}

	public static boolean isBossLevel(){return isBossLevel(level);}
	public static boolean isBossLevel(int game_level){return game_level%10 == 0;}

	public static int bonusIncomePercentage()
	{		
		return MyMath.percentage(extraBonusCounter, overallEarnings);
	}

	public static void update_timer()
	{
		if(commendationTimer > 0){
            commendationTimer--;}		
	}
}