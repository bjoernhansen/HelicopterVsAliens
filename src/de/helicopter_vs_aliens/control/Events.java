package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.score.HighscoreEntry;
import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.gui.Button;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.gui.WindowTypes;
import de.helicopter_vs_aliens.model.helicopter.*;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;

import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.DESTROYED;
import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.INACTIVE;
import static de.helicopter_vs_aliens.control.TimesOfDay.DAY;
import static de.helicopter_vs_aliens.control.TimesOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.Button.STARTSCREEN_MENU_BUTTON;
import static de.helicopter_vs_aliens.gui.Menu.window;
import static de.helicopter_vs_aliens.model.enemy.EnemyTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.*;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.*;
import static de.helicopter_vs_aliens.gui.PriceLevels.REGULAR;
import static de.helicopter_vs_aliens.gui.WindowTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.util.dictionary.Languages.ENGLISH;
import static de.helicopter_vs_aliens.util.dictionary.Languages.GERMAN;

public class Events
{
	// Konstanten zur Berechnung der Reparaturkosten und der Boni bei Abschuss von Gegnern
	public static final int
		DAY_BONUS_FACTOR = 60,
		NIGHT_BONUS_FACTOR = 90,
		SPOTLIGHT_COSTS = 35000,
		START = 0;						// Timer Start
	
	private static final int
		COMPARISON_RECORD_TIME = 60,	// angenommene Bestzeit für besiegen von Boss 5
		TOTAL_LOSS_REPAIR_BASE_FEE = 875,
		DEFAULT_REPAIR_BASE_FEE = 350,
		MAX_MONEY = 5540500;			// für Komplettausbau erforderliche Geldmenge

	public static final boolean
		CHEATS_ACTIVATABLE = true,
		SAVE_ANYWAY = true;

	public static HighscoreEntry[][]
		highscore = new HighscoreEntry[7][10];
	
	static Point 
		cursor = new Point();	// die letzten Maus-Koordinaten
	
	public static int 
		level = 1,				// aktuelle Level [1 - 51]
		maxLevel = 1,			// höchstes erreichtes Level
		money = 0, 				// Guthaben
		killsAfterLevelUp,		// Anhand dieser Anzahl wird ermittelt, ob ein Level-Up erfolgen muss.
		lastCreationTimer,		// Timer stellt sicher, dass ein zeitlicher Mindestabstand zwischen der Erstellung zweier Gegner liegt
        overallEarnings, 		// Gesamtverdienst
        extraBonusCounter, 		// Summe aller Extra-Boni (Multi-Kill-Belohnungen, Abschuss von Mini-Bossen und Geld-PowerUps)
		lastBonus, 				// für die Guthabenanzeige: zuletzt erhaltener Standard-Verdienst
			lastExtraBonus,		// für die Guthabenanzeige: zuletzt erhaltener Extra-Bonus
        lastMultiKill,			// für die Multi-Kill-Anzeige: Art des letzten Multi-Kill
        commendationTimer,		// reguliert, wie lange die Multi-Kill-Anzeige zu sehen ist
		heliosMaxMoney;

	public static long
		playingTime;            		// bisher vergangene Spielzeit
    	public static long timeAktu;	// Zeitpunkt der letzten Aktualisierung von playing_time
	
	public static long
		recordTime[][] = new long [Helicopter.NR_OF_TYPES][5];	// für jede Helikopter-Klasse die jeweils beste Zeit bis zum Besiegen eines der 5 Boss-Gegner
    
    public static boolean
		isRestartWindowVisible,				// = true: Neustart-Fenster wird angezeigt
    	reachedLevelTwenty[] = new boolean[Helicopter.NR_OF_TYPES],
    	settingsChanged = false,
    	allPlayable = false;

	public static TimesOfDay
		timeOfDay = DAY;		// Tageszeit [NIGHT, DAY]

	// Variablen zur Nutzung von Cheats und Freischaltung von Helikoptern
	private static boolean
		cheatingMode = false;				// = true: Cheat-Modus aktiviert

	private static String
		cheatString = "";
	
	private static final String
		cheatCode = "+cheats";			// Code, mit welchem Cheats aktiviert werden können
	
	public static Enemy
		boss;	// Referenz auf den aktuellen Endgegner

	public static HelicopterTypes
		nextHelicopterType = HelicopterTypes.getDefault();


	static void keyTyped( KeyEvent e, Controller controller,
	                      Helicopter helicopter, Savegame savegame)
	{	
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE && !helicopter.isDamaged)
		{
			if(window == GAME){changeVisibilityOfInGameMenu(helicopter);}
			else if(window == STARTSCREEN){
				Controller.shutDown();}
			else if(window != REPAIR_SHOP)
			{
				cancel(controller.backgroundObjects, helicopter, savegame);
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
			controller.switchFpsVisibleState();
		}		
		else if(e.getKeyChar() =='p')
		{
			if(window == GAME && !helicopter.isDamaged)
			{
				changeVisibilityOfInGameMenu(helicopter);
			}
		}		
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if(	window == GAME
				&&	helicopter.isActive
				&& !helicopter.isDamaged
				&& !Menu.isMenuVisible)
			{
				helicopter.turnAround();
			}
		}		
		else if(cheatingMode)
		{			
			if(e.getKeyChar() == 'e')
			{
				if(window == GAME || window == REPAIR_SHOP)
				{
					if(money == 0)
					{
						if(window == GAME)
						{
							lastBonus = MAX_MONEY - money;
						}
						money = MAX_MONEY;
						helicopter.isPlayedWithoutCheats = false;
					}
					else
					{
						lastBonus = 0;
						money = 0;					
					}				
					Menu.moneyDisplayTimer = START;
				}
			}				
			else if(e.getKeyChar() == 'u')
			{
				if(window == GAME || window == REPAIR_SHOP)
				{
					if(!helicopter.hasAllUpgrades())
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
						playingTime += (nr_of_levelUp + MyMath.random(nr_of_levelUp)) * 60000;
						levelUp(controller, nr_of_levelUp);
						helicopter.isPlayedWithoutCheats = false;
					}					
				}
				else if(e.getKeyChar() == '+')
				{
					if(level < 50)
					{
						playingTime += (1 + (MyMath.tossUp(0.4f) ? 1 : 0)) * 60000;
						levelUp(controller, 1);
						helicopter.isPlayedWithoutCheats = false;
					}
				}
				else if(e.getKeyChar() =='s')
				{
					Menu.specialInfoSelection = (Menu.specialInfoSelection +1)%14;
				}
				else if(e.getKeyChar() == 'd'){helicopter.getPowerUp(controller.powerUps, TRIPLE_DMG, 	   true);}
				else if(e.getKeyChar() == 'i'){helicopter.getPowerUp(controller.powerUps, INVINCIBLE, 	   true);}
				else if(e.getKeyChar() == 'c'){helicopter.getPowerUp(controller.powerUps, UNLIMITRED_ENERGY, true);}
				else if(e.getKeyChar() == 'y'){helicopter.getPowerUp(controller.powerUps, BOOSTED_FIRE_RATE, true);}
				else if(e.getKeyChar() == 'a')
				{
					if(level < 51)
					{
						helicopter.repair(true, true);
						isRestartWindowVisible = false;
						helicopter.isPlayedWithoutCheats = false;
					}								
				}
				else if(e.getKeyChar() == 'm')
				{
					Enemy.changeMiniBossProb();
					helicopter.isPlayedWithoutCheats = false;
				}
				else if(e.getKeyChar() == 'n')
				{
					helicopter.currentPlating = 0f;
					helicopter.crash();
				}
				else if(e.getKeyChar() == 't'){
					playingTime += 60000;}
			}
			else if(window == STARTSCREEN)
			{
				// Resetten der Helicopter-Bestzeiten
				if(e.getKeyChar() == 'x')
				{					
					allPlayable = !allPlayable;
				}
				else if(e.getKeyChar() == '-')
				{					
					if(!MyMath.isEmpty(recordTime))
					{
						for(int i = 0; i < Helicopter.NR_OF_TYPES; i++)
						{
							Arrays.fill(recordTime[i], 0);
							reachedLevelTwenty[i] = false;
						}
						heliosMaxMoney = getHeliosMaxMoney();
						savegame.saveInHighscore();
						savegame.saveToFile(helicopter, false);
						Audio.play(Audio.emp);
					}
				}
				else if(e.getKeyChar() == '#')
				{					
					if((helicopter.isPlayedWithoutCheats || SAVE_ANYWAY) && savegame.valid)
					{
						savegame.saveInHighscore();
					}
					savegame.saveToFile(helicopter, false);
					Audio.play(Audio.emp);
				}
			}
		}
		else if(CHEATS_ACTIVATABLE)
		{
			if(e.getKeyChar() == cheatCode.charAt(cheatString.length())){
				cheatString += e.getKeyChar();}
			else{
				cheatString = "";}
			if(cheatString.equals(cheatCode)){
				cheatingMode = true;}
		}		
	}
	
	static void mousePressed(MouseEvent e,
	                         Controller controller,
	                         Helicopter helicopter)
	{
		if(e.getButton() == 1)
		{
			cursor.setLocation(e.getX()- Main.displayShift.width,
							   e.getY()-Main.displayShift.height);
			mousePressedLeft(controller, helicopter);
		}
		else if( 	
					window == GAME
					&&	helicopter.isActive
					&& !helicopter.isDamaged
					&& !Menu.isMenuVisible)
		{
			if(e.getButton() == 3)
			{
				helicopter.tryToUseEnergyAbility(controller.powerUps, controller.explosions);
			}
			else{helicopter.turnAround();}
		}	
	}
	
	private static void mousePressedLeft(Controller controller, Helicopter helicopter)
	{		
		controller.backgroundRepaintTimer = 0;
		if(window == GAME)
		{
			inGameMousePressedLeft(controller, helicopter);
		}
		else if(window == REPAIR_SHOP)
		{
			repairShopMousePressedLeft(helicopter, controller.enemies, controller.backgroundObjects);
		}
		else if(window == STARTSCREEN)
		{
			startscreenMousePressedLeft(helicopter);
		}
		else if(Menu.startscreenMenuButton.get("Cancel").bounds.contains(cursor))
		{
			cancel(controller.backgroundObjects, helicopter, Controller.savegame);
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
		if(!helicopter.isDamaged)
		{
			if(Menu.isMenuVisible)
			{
				if(Menu.inGameButton.get("MMNewGame1").bounds.contains(cursor))
				{					
					Controller.savegame.saveToFile(helicopter, true);
					conditionalReset(controller, helicopter, true);
					restartGame(helicopter, controller.backgroundObjects);
					Audio.applause1.stop();
				}
				else if(Menu.inGameButton.get("MMStopMusic").bounds.contains(cursor))
				{
					Audio.play(Audio.choose);
					switchAudioActivationState(Controller.savegame);
				}
				else if(Menu.inGameButton.get("MMNewGame2").bounds.contains(cursor))
				{					
					Controller.savegame.saveToFile(helicopter, true);
					Controller.shutDown();
				}
				else if( 	(Menu.inGameButton.get("MainMenu").bounds.contains(cursor)
							&& helicopter.isOnTheGround())
						 || (Menu.inGameButton.get("MMCancel").bounds.contains(cursor)))
				{
					changeVisibilityOfInGameMenu(helicopter);
				}
			}			
			else if(helicopter.isOnTheGround())
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
				else if(!helicopter.isActive && cursor.y < 426)
				{
					helicopter.setActivationState(true);
				}
			}
			else if(helicopter.isActive){helicopter.isContiniousFireEnabled = true;}
		}
		else if(	Menu.inGameButton.get("MMNewGame2").bounds.contains(cursor)
				 && isRestartWindowVisible)
		{
			// Betreten der Werkstatt nach Absturz bzw. Neustart bei Geldmangel				
			conditionalReset(controller, helicopter, true);
			if(money < repairFee(helicopter, true) || level > 50)
			{
				if(level > 50)
				{
					playingTime = 60000 * helicopter.scorescreenTimes[4];
				}
				else
				{
					playingTime = playingTime
						     	   + System.currentTimeMillis() 
						           - timeAktu;
					helicopter.scorescreenTimes[4] = playingTime /60000;
				}				
							   
				changeWindow(SCORESCREEN);	
															
				helicopter.isDamaged = false;
				Controller.savegame.saveToFile(helicopter, true);
				MyColor.updateScorescreenColors(helicopter);
			}
			else{enterRepairShop(helicopter);}
		}
	}
	
	private static void repairShopMousePressedLeft(Helicopter helicopter,
												   EnumMap<CollectionSubgroupTypes, LinkedList<Enemy>> enemies,
												   EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> bgObject)
	{
		// Reparatur des Helikopters
		if(Menu.repairShopButton.get("RepairButton").bounds.contains(cursor))
		{
			if(	helicopter.currentPlating == helicopter.maxPlating())
			{
				Menu.block(1);
			}
			else if(money < repairFee(helicopter, helicopter.isDamaged))
			{
				Menu.block(9);
			}
			else
			{
				money -= repairFee(helicopter, helicopter.isDamaged);
				timeOfDay = (!helicopter.spotlight || MyMath.tossUp(0.33f)) ? DAY : NIGHT;
				Menu.repairShopButton.get("Einsatz").label = Button.MISSION[Menu.language.ordinal()][timeOfDay.ordinal()];
												
				if(!(level == 50 && helicopter.hasAllUpgrades()))
				{
					enemies.get(INACTIVE).addAll(enemies.get(ACTIVE));
					enemies.get(ACTIVE).clear();
					level = level - ((level - 1) % 5);						
					Enemy.adaptToLevel(helicopter, level, false);
					if(level < 6){
						BackgroundObject.reset(bgObject);}
					killsAfterLevelUp = 0;
					enemies.get(INACTIVE).addAll(enemies.get(DESTROYED));
					enemies.get(DESTROYED).clear();
					Menu.repairShopButton.get("RepairButton").costs = 0;
					Enemy.currentRock = null;
				}
				else
				{
					Enemy.bossSelection = FINAL_BOSS;
					Enemy.maxNr = 1;
					Enemy.maxBarrierNr = 0;
				}
				
				helicopter.repair(level == 50, false);
			}
		}		
		// Einsatz fliegen
		else if(Menu.repairShopButton.get("Einsatz").bounds.contains(cursor))
		{
			if(helicopter.isDamaged){
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
			if(helicopter.isDamaged){
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
				Menu.repairShopButton.get("Einsatz").label = Button.MISSION[Menu.language.ordinal()][timeOfDay.ordinal()];
				Menu.repairShopButton.get("Einsatz").secondLabel = Button.SOLD[Menu.language.ordinal()][helicopter.spotlight ? 1 : 0];
				Menu.repairShopButton.get("Special" + 0).costs = 0;
				for(Enemy enemy : enemies.get(DESTROYED))
				{
					enemy.repaint();
				}
				for(Enemy enemy : enemies.get(ACTIVE))
				{
					if (enemy.type != ROCK)
					{
						enemy.farbe1 = MyColor.dimColor(enemy.farbe1, MyColor.BARRIER_NIGHT_DIM_FACTOR);
						enemy.farbe2 = MyColor.dimColor(enemy.farbe2, MyColor.BARRIER_NIGHT_DIM_FACTOR);
						enemy.repaint();
					}
				}
			}
		}
		// Goliath-Panzerung
		else if(Menu.repairShopButton.get("Special1").bounds.contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(4);}
			else if(helicopter.hasGoliathPlating()){
				Menu.block(7);}
			else if(money < helicopter.getGoliathCosts())
			{
				Menu.block(6);
			}
			else
			{
				Audio.play(Audio.cash);						
				money -= helicopter.getGoliathCosts();
				helicopter.installGoliathPlating();
				Menu.repairShopButton.get("Special" + 1).costs = 0;
			}
		}
		// Durchstoßsprengköpfe
		else if(Menu.repairShopButton.get("Special2").bounds.contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(4);}
			else if(helicopter.hasPiercingWarheads){
				Menu.block(7);}
			else if(money < helicopter.getPiercingWarheadsCosts())
			{
				Menu.block(6);
			}
			else
			{
				Audio.play(Audio.cash);
                money -= helicopter.getPiercingWarheadsCosts();
                helicopter.installPiercingWarheads();
				Menu.repairShopButton.get("Special" + 2).costs = 0;
			}
		}
		// zusätzliche Bordkanonen
		else if(Menu.repairShopButton.get("Special3").bounds.contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(4);}
			else if(helicopter.hasAllCannons())
			{
				Menu.block(7);
			}
			else if(	(money < Helicopter.STANDARD_SPECIAL_COSTS || (helicopter.getType() == ROCH && money < Roch.ROCH_SECOND_CANNON_COSTS)) &&
						!((helicopter.getType() == OROCHI ||(helicopter.getType() == HELIOS && recordTime[OROCHI.ordinal()][4]!=0)) && money >= Helicopter.CHEAP_SPECIAL_COSTS && helicopter.numberOfCannons == 1))
			{
				Menu.block(6);
			}
			else
			{
				Audio.play(Audio.cash);
				if((helicopter.getType() == OROCHI ||(helicopter.getType() == HELIOS && recordTime[OROCHI.ordinal()][4]!=0)) && helicopter.numberOfCannons == 1)
				{
					money -= Helicopter.CHEAP_SPECIAL_COSTS;
					if(helicopter.getType() == OROCHI)
					{
						Menu.repairShopButton.get("Special" + 3).costs = Helicopter.STANDARD_SPECIAL_COSTS;
						Menu.repairShopButton.get("Special" + 3).label = Menu.THIRD_CANNON[Menu.language.ordinal()];
						Menu.repairShopButton.get("Special" + 3).costColor = MyColor.costsColor[REGULAR.ordinal()];
					}
					else
					{
						Menu.repairShopButton.get("Special" + 3).costs = 0;
					}					
				}
				else
				{
					money -= helicopter.getType() == ROCH ? Roch.ROCH_SECOND_CANNON_COSTS : Helicopter.STANDARD_SPECIAL_COSTS;
					Menu.repairShopButton.get("Special" + 3).costs = 0;
				}
				helicopter.numberOfCannons++;
			}
		}
		// das klassenspezifische SpezialUpgrade
		else if(Menu.repairShopButton.get("Special4").bounds.contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(4);}
			else if(helicopter.hasFifthSpecial()){
				Menu.block(7);}
			else if(money < Helicopter.CHEAP_SPECIAL_COSTS || (helicopter.getType() == ROCH && money < Roch.JUMBO_MISSILE_COSTS))
			{
				Menu.block(6);
			}
			else
			{
				Audio.play(Audio.cash);
				money -= helicopter.getType() == ROCH ? Roch.JUMBO_MISSILE_COSTS : Helicopter.CHEAP_SPECIAL_COSTS;
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
					if(helicopter.isDamaged){
						Menu.block(4);}
					else if(helicopter.hasMaxUpgradeLevel[i]){
						Menu.block(5);}
					else if(money < MyMath.costs(helicopter.getType(), helicopter.upgradeCosts[i], helicopter.levelOfUpgrade[i]))
					{
						Menu.block(6);
					}
					else
					{
						Audio.play(Audio.cash);
						money -= MyMath.costs(helicopter.getType(), helicopter.upgradeCosts[i], helicopter.levelOfUpgrade[i]);
						helicopter.levelOfUpgrade[i]++;
						if(helicopter.levelOfUpgrade[i] >= MyMath.maxLevel(helicopter.upgradeCosts[i]))
						{
							helicopter.hasMaxUpgradeLevel[i] = true;
							Menu.repairShopButton.get("StandardUpgrade" + i).costs = 0;
						}
						else
						{
							Menu.repairShopButton.get("StandardUpgrade" + i).costs = MyMath.costs(helicopter.getType(), helicopter.upgradeCosts[i], helicopter.levelOfUpgrade[i]);
						}
						selection = i;
					}					
					break;
				}
			}
			if(selection == Integer.MIN_VALUE){/**/}
			else if(selection == 0)
			{
				helicopter.rotorSystem = MyMath.speed(helicopter.levelOfUpgrade[ROTOR_SYSTEM.ordinal()]);
			}
			else if(selection == 1)
			{
				helicopter.missileDrive = MyMath.missileDrive(helicopter.levelOfUpgrade[MISSILE_DRIVE.ordinal()]);
			}
			else if(selection == 2)
			{
				helicopter.currentPlating
					+= helicopter.platingDurabilityFactor
					   * ( MyMath.plating(helicopter.levelOfUpgrade[PLATING.ordinal()])
						   -MyMath.plating(helicopter.levelOfUpgrade[PLATING.ordinal()]-1));
				helicopter.setPlatingColor();
			}
			else if(selection == 3)
			{
				helicopter.setCurrentBaseFirepower();
			}
			else if(selection == 4)
			{
				helicopter.adjustFireRate(false);
			}
			else if(selection == 5)
			{
				helicopter.upgradeEnergyAbility();
			}
		}
	}
		
	private static void startscreenMousePressedLeft(Helicopter helicopter)
	{
		if(Menu.triangle[0].contains(cursor))
		{
			Menu.crossPosition = (Menu.crossPosition + 1)%Helicopter.NR_OF_TYPES;
			Menu.cross = Menu.getCrossPolygon();
			Menu.helicopterSelection = (Menu.helicopterSelection + Helicopter.NR_OF_TYPES - 1)%Helicopter.NR_OF_TYPES;
			Audio.play(Audio.choose);
		}
		else if(Menu.triangle[1].contains(cursor))
		{
			Menu.crossPosition = (Menu.crossPosition + Helicopter.NR_OF_TYPES - 1)%Helicopter.NR_OF_TYPES;
			Menu.cross = Menu.getCrossPolygon();
			Menu.helicopterSelection = (Menu.helicopterSelection + 1)%Helicopter.NR_OF_TYPES;
			Audio.play(Audio.choose);
		}
		else if(Menu.helicopterFrame[0].contains(cursor)||
				Menu.helicopterFrame[1].contains(cursor)||
				Menu.helicopterFrame[2].contains(cursor)||
				Menu.helicopterFrame[3].contains(cursor))
		{				
			if(allPlayable || Events.nextHelicopterType.isUnlocked())
			{
				startGame(Events.nextHelicopterType, Controller.savegame, true);
			}
			else
			{
				Audio.play(Audio.block);
				Menu.crossPosition = (Events.nextHelicopterType.ordinal() - Menu.helicopterSelection + Helicopter.NR_OF_TYPES)%Helicopter.NR_OF_TYPES;
				Menu.cross = Menu.getCrossPolygon();
				Menu.crossTimer = 1;
				Menu.messageTimer = 1;
				Menu.setStartscreenMessage(Events.nextHelicopterType);
			}
		}
		else if(Menu.startscreenButton.get("00").bounds.contains(cursor))
		{			
			newStartscreenMenuWindow(INFORMATIONS, true);
			Menu.startscreenMenuButton.get("2").marked = true;
		}
		else if(Menu.startscreenButton.get("01").bounds.contains(cursor))
		{			
			newStartscreenMenuWindow(HIGHSCORE, true);
		}
		else if(Menu.startscreenButton.get("02").bounds.contains(cursor))
		{			
			newStartscreenMenuWindow(CONTACT, true);
		}
		else if(Menu.startscreenButton.get("10").bounds.contains(cursor))
		{			
			newStartscreenMenuWindow(SETTINGS, true);
			if(HighscoreEntry.currentPlayerName.equals("John Doe"))
			{
				Menu.startscreenMenuButton.get("4").marked = true;
			}
		}
		else if(Menu.startscreenButton.get("11").bounds.contains(cursor))
		{
			if(Controller.savegame.valid)
			{
				Audio.play(Audio.levelUp);
				startGame(Controller.savegame);
			}
			else{Audio.play(Audio.block);}			
		}		
		else if(Menu.startscreenButton.get("12").bounds.contains(cursor))
		{					
			Controller.shutDown();
		}		
	}
	
	private static void cancel(EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> bgObject,
							   Helicopter helicopter, Savegame savegame)
	{
		Audio.play(Audio.choose);
		if(window == SCORESCREEN)
		{				
			savegame.saveInHighscore();
			restartGame(helicopter, bgObject);
			savegame.saveToFile(helicopter, false);
			Menu.startscreenMenuButton.get("Cancel").highlighted = false;
		}
		else if(window == DESCRIPTION)
		{
			if(Menu.page == 5){
				Menu.label.setBounds(Main.displayShift.width  + 42,
													   Main.displayShift.height + 83, 940, 240);}
			newStartscreenMenuWindow(INFORMATIONS, false);
			Menu.startscreenMenuButton.get("2").marked = true;
			Menu.startscreenMenuButton.get("6").marked = false;
		}
		else if(window == HELICOPTER_TYPES)
		{
			if(Menu.page == 1){
				Menu.label.setVisible(true);}
			newStartscreenMenuWindow(DESCRIPTION, false);
			Menu.startscreenMenuButton.get("6").marked = true;
		}
		else
		{
			if(window == INFORMATIONS)
			{
				Menu.startscreenMenuButton.get("2").marked = false;
			}
			else if(window == SETTINGS)
			{
				Menu.startscreenMenuButton.get("4").marked = false;
				HighscoreEntry.checkName(savegame);										
				if(settingsChanged)
				{					
					savegame.writeToFile();
					settingsChanged = false;
				}
			}
			Menu.label.setVisible(false);
			Menu.stopButtonHighlighting(Menu.startscreenMenuButton);
			window = STARTSCREEN;
		}
	}
	
	private static void startscreenMenuButtonClicked(Graphics2D offGraphics,
													 Helicopter helicopter,
													 Savegame savegame)
	{
		for(int m = 0; m < 8; m++)
		{
			Button currentButton = Menu.startscreenMenuButton.get(Integer.toString(m));
			if(	 currentButton.bounds.contains(cursor) &&
				 !currentButton.label.equals("") &&
				(Menu.page != m || window == SETTINGS))
			{						
				int oldPage = Menu.page;
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
					Menu.startscreenMenuButton.get("2").marked = false;
					Menu.startscreenMenuButton.get("6").marked = true;
				}
				else if(window == DESCRIPTION && Menu.page == 6)
				{
					newStartscreenMenuWindow(HELICOPTER_TYPES, false);
					Menu.startscreenMenuButton.get("6").marked = false;
				}
				else if(window == SETTINGS)
				{					
					settingsMousePressedLeft(helicopter, 
											 currentButton,
											 offGraphics, 
											 savegame, 
											 oldPage);
				}
				else
				{
					Audio.play(Audio.choose);					
					Menu.updateLabeltext();
				}
				break;
			}
		}		
	}
	
	private static void settingsMousePressedLeft( Helicopter helicopter, 
	                                              Button currentButton,
												  Graphics2D offGraphics, 
												  Savegame savegame,
												  int oldPage)
	{
		if(Menu.page == 0)
		{	
			Main.switchDisplayMode(currentButton);
		}
		else if(Menu.page == 1)
		{			
			switchAntialiasingActivationState(offGraphics, currentButton);
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
		if(oldPage == 4)
		{
			if(Menu.page == 4){
				Menu.page = 0;}
			HighscoreEntry.checkName(savegame);
		}
		Menu.updateLabeltext();
	}
	
	private static void switchAntialiasingActivationState(	Graphics2D offGraphics,
															Button currentButton)
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
		currentButton.label
		    = Button.ANTIALIAZING[Menu.language.ordinal()][Controller.antialiasing ? 0 : 1];
	}

	static void mouseReleased(MouseEvent e, Helicopter helicopter)
	{
		if(e.getButton() == 1)
		{
			helicopter.isContiniousFireEnabled = false;
		}
		else if(window == GAME && e.getButton() == 3 && !helicopter.isDamaged)
		{			
			if(helicopter.getType() == PHOENIX)
			{
				helicopter.teleportTo(	e.getX()-Main.displayShift.width,
										e.getY()-Main.displayShift.height);
			}			
			else if(helicopter.getType() == OROCHI)
			{
				helicopter.isNextMissileStunner = false;
			}		
		}		
	}
	
	// Aktualisierung der Ziel-Koordinaten, auf welche der Helikopter zufliegt
	static void mouseMovedOrDragged(MouseEvent e, Helicopter helicopter)
	{		
		if(!helicopter.isDamaged || window == REPAIR_SHOP)
		{
			if(!helicopter.isSearchingForTeleportDestination)
			{
				helicopter.destination.setLocation(
						e.getX()-Main.displayShift.width,
						e.getY()-Main.displayShift.height);
			}
			else
			{
				helicopter.destination.setLocation(helicopter.priorTeleportLocation);
			}
		}	
	}
	
	private static void initialize(Helicopter helicopter, 
	                               Savegame savegame, 
	                               boolean newGame)
	{			
		if(newGame){reset();}
		else{restore(savegame);}
		changeWindow(GAME);	
		timeAktu = System.currentTimeMillis();
		for(int i =  level - ((level - 1) % 5); i <= level; i++)
		{
			Enemy.adaptToLevel(helicopter, i, false);
		}
	}

	// Rücksetzen einiger Variablen bei Neustart des Spiels
	private static void reset()
	{
		money = 0;
		level = 1;
		maxLevel = 1;
		timeOfDay = DAY;
		overallEarnings = 0;
		extraBonusCounter = 0;
		playingTime = 0;
	}

	// Wiederherstellen einiger Variablen anhand eines gespeicherten Spielstandes
	private static void restore(Savegame savegame)
	{
		money = savegame.money;
		killsAfterLevelUp = savegame.killsAfterLevelup;
		level = savegame.level;
		maxLevel = savegame.maxLevel;
		timeOfDay = savegame.timeOfDay;
		overallEarnings = savegame.bonusCounter;
		extraBonusCounter = savegame.extraBonusCounter;
		playingTime = savegame.playingTime;
	}

	/* Reset: Zurücksetzem diverser spielinterner Variablen; 
	 * bedingter (conditional) Rest, da unterschieden wird, ob nur die 
	 * Werkstatt betreten oder das Spiel komplett neu gestartet wird
	 */
	private static void conditionalReset(Controller controller, Helicopter helicopter, boolean totalReset)
	{
		Audio.play(Audio.choose);
		
		helicopter.bonusKillsTimer = 1;
		if(helicopter.getType() == KAMAITACHI){helicopter.evaluateBonusKills();}
		
		helicopter.resetState(totalReset);
		boss = null;		
		lastExtraBonus = 0;
		lastMultiKill = 0;
		commendationTimer = 0;		
		isRestartWindowVisible = false;
		lastBonus = 0;
		
		Menu.conditionalReset();
		
		// kein "active enemy"-Reset, wenn Bossgegner 2 Servants aktiv
		if(!controller.enemies.get(ACTIVE).isEmpty()
		   && !(!totalReset && controller.enemies.get(ACTIVE).getFirst().type == BOSS_2_SERVANT))
		{
			// Boss-Level 4 oder 5: nach Werkstatt-Besuch erscheint wieder der Hauptendgegner
			if(	level == 40 || level == 50)
			{
				Enemy.bossSelection = level == 40 ? BOSS_4 : FINAL_BOSS;
				Enemy.maxNr = 1;
				Enemy.maxBarrierNr = 0;
			}			
			if(totalReset)
			{
				controller.enemies.get(INACTIVE).addAll(controller.enemies.get(ACTIVE));
				controller.enemies.get(ACTIVE).clear();
				Enemy.currentRock = null;
			}
			else
			{
				for(Iterator<Enemy> i = controller.enemies.get(ACTIVE).iterator(); i.hasNext();)
				{
					Enemy e = i.next();					
					if(!e.isLasting)
					{
						controller.enemies.get(INACTIVE).add(e);
						i.remove();
					}
				}
			}
			Enemy.currentMiniBoss = null;
		}
		if(totalReset)
		{
			killsAfterLevelUp = 0;
			controller.enemies.get(INACTIVE).addAll(controller.enemies.get(DESTROYED));
			controller.enemies.get(DESTROYED).clear();
			if(level < 6){
				BackgroundObject.reset(controller.backgroundObjects);}
		}									
		controller.explosions.get(INACTIVE).addAll(controller.explosions.get(ACTIVE));
		controller.explosions.get(ACTIVE).clear();
		controller.missiles.get(INACTIVE).addAll(controller.missiles.get(ACTIVE));
		controller.missiles.get(ACTIVE).clear();
		controller.enemyMissiles.get(INACTIVE).addAll(controller.enemyMissiles.get(ACTIVE));
		controller.enemyMissiles.get(ACTIVE).clear();
		controller.powerUps.get(INACTIVE).addAll(controller.powerUps.get(ACTIVE));
		controller.powerUps.get(ACTIVE).clear();
		if(Menu.collectedPowerUp[3] != null)
		{
			helicopter.adjustFireRate(false);
		}
		for(int i = 0; i < 4; i++)
		{
			Menu.collectedPowerUp[i] = null;
		}
	}

	static private void startGame(Savegame savegame)
	{
		startGame(savegame.helicopterType, savegame, false);
	}

	static private void startGame(HelicopterTypes helicopterType, Savegame savegame, boolean isNewGame)
	{
		Audio.play(Audio.choose);
		Helicopter helicopter = HelicopterFactory.create(helicopterType);
		Controller.getInstance().setHelicopter(helicopter);
		if(isNewGame)
		{
			Audio.play(Audio.applause1);
			savegame.saveInHighscore();
		}
		Menu.reset();
		helicopter.initialize(isNewGame, savegame);
		initialize(helicopter, savegame, isNewGame);
		Button.initialize(helicopter);
		if(isNewGame)
		{
			Controller.savegame.saveToFile(helicopter, true);
		}
		else
		{
			Menu.updateRepairShopButtons(helicopter);
		}
	}
		
	private static void restartGame(Helicopter helicopter, EnumMap<CollectionSubgroupTypes, LinkedList<BackgroundObject>> bgObject)
	{		
		changeWindow(STARTSCREEN);	
		helicopter.reset();
		BackgroundObject.reset(bgObject);
	}

	private static void startMission(Helicopter helicopter)
	{		
		changeWindow(GAME);		
		Audio.play(Audio.choose);	
		timeAktu = System.currentTimeMillis();
		helicopter.rotorPosition[helicopter.getType().ordinal()] = 0;
		Controller.savegame.saveToFile(helicopter, true);
	}
	
	private static void enterRepairShop(Helicopter helicopter)
	{
		changeWindow(REPAIR_SHOP);		
		
		Audio.applause1.stop();
		playingTime += System.currentTimeMillis() - timeAktu;
		Menu.repairShopTime = Menu.returnTimeDisplayText(playingTime);
		helicopter.setPlatingColor();					
		if(helicopter.currentPlating < helicopter.maxPlating())
	    {
			Menu.repairShopButton.get("RepairButton").costs = repairFee(helicopter, helicopter.isDamaged);
	    }
		else{
			Menu.repairShopButton.get("RepairButton").costs = 0;}
		Menu.clearMessage();
		Menu.messageTimer = 0;
	}

	public static int repairFee(Helicopter helicopter, boolean totalLoss)
	{		
		return (totalLoss
					? TOTAL_LOSS_REPAIR_BASE_FEE 
					: DEFAULT_REPAIR_BASE_FEE) 
				+ 25 * Math.round( 6.5f * ( helicopter.maxPlating()
								    		- helicopter.currentPlating));
	}
	
	private static void changeWindow(WindowTypes newWindow)
	{		
		window = newWindow;
		Audio.refreshBackgroundMusic();
		MyColor.bg = newWindow == GAME && timeOfDay == DAY ? MyColor.sky: Color.black;
	}

	private static void newStartscreenMenuWindow(WindowTypes newWindow, boolean hasJustEntered)
	{
		if(hasJustEntered){
			Menu.stopButtonHighlighting(Menu.startscreenButton);}
		Audio.play(Audio.choose);		
		window = newWindow;
		Menu.adaptToNewWindow(hasJustEntered);
		Button.updateScreenMenuButtons(window);
	}

	// überprüfen, ob Level-Up Voraussetzungen erfüll. Wenn ja: Schwierigkeitssteigerung
	static void checkForLevelup(Controller controller, Helicopter helicopter)
	{
		if( killsAfterLevelUp >= MyMath.kills(level) && level < 50)
		{
			levelUp(controller, 1);
		}
	}

	// erhöht das Spiel-Level auf "nr_of_levelUp" mit allen Konsequenzen
	private static void levelUp(Controller controller,
								int numberOfLevelUp)
	{
		Helicopter helicopter = controller.getHelicopter();
		Audio.play(level + numberOfLevelUp <= 50
					? Audio.levelUp
					: Audio.applause1);
				
		killsAfterLevelUp = 0;
		int previousLevel = level;
		level += numberOfLevelUp;
				
		if(isBossLevel()){Enemy.getRidOfSomeEnemies(helicopter, controller.enemies, controller.explosions);}
		if(helicopter.getType() == HELIOS && level > maxLevel){getHeliosIncome(previousLevel);}
		
		maxLevel = level;
		
		if(	isBossLevel() || isBossLevel(previousLevel) || level == 49)
		{
			Audio.refreshBackgroundMusic();
			if(previousLevel % 10 == 0){Audio.play(Audio.applause1);}
		}
		Menu.levelDisplayTimer = START;
		Enemy.adaptToLevel(helicopter, level, true);
	}

	private static void getHeliosIncome(int previousLevel)
	{
		int bonusSum = 0;
		for(int i = Math.max(previousLevel, maxLevel); i < level; i++)
		{
			bonusSum += (int)((i/1225f)* heliosMaxMoney);
		}
		lastBonus = (int) (bonusSum * (timeOfDay == NIGHT ? 1 : ((float)DAY_BONUS_FACTOR)/NIGHT_BONUS_FACTOR));
		money += lastBonus;
		overallEarnings += lastBonus;
		Menu.moneyDisplayTimer = START;
	}

	// Stellt sicher, dass mit dem Besiegen des End-Gegners direkt das nächste Level erreicht wird
	public static void setBossLevelUpConditions()
	{
			 if(level == 10){
				 killsAfterLevelUp = 14;}
		else if(level == 20){
				 killsAfterLevelUp = 7;}
		else if(level == 30){
				 killsAfterLevelUp = 24;}
		else if(level == 40){
				 killsAfterLevelUp = 29;}
		else if(level == 50){
				 killsAfterLevelUp = 34;}
	}
	
	// Bonus-Verdienst bei Multi-Kill
	public static void extraReward(int kills, int earnedMoney, float basis,
								   float increase, float limit)
	{
		Menu.moneyDisplayTimer = START;
		if(kills > 1){
			lastBonus = earnedMoney;}
		lastExtraBonus = (int)(Math.min(basis + increase * (kills-2), limit) * earnedMoney);
		lastExtraBonus = Math.round(lastExtraBonus /10f)*10;
		money += lastExtraBonus;
		overallEarnings += lastExtraBonus;
		extraBonusCounter += lastExtraBonus;
		lastMultiKill = kills;
		commendationTimer = 90 + (Math.max(kills, 6)-2) * 25; 
		Audio.praise(kills);
	}
	
	static private void changeVisibilityOfInGameMenu(Helicopter helicopter)
	{
		Audio.play(Audio.choose);
		if(!Menu.isMenuVisible)
		{
			Menu.isMenuVisible = true;
			BackgroundObject.backgroundMoves = false;
			playingTime += System.currentTimeMillis() - timeAktu;
		}
		else
		{
			Menu.isMenuVisible = false;
			timeAktu = System.currentTimeMillis();
			if(helicopter.isOnTheGround())
			{
				helicopter.setActivationState(false);
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
		Menu.startscreenMenuButton.get("2").label = Button.MUSIC[Menu.language.ordinal()][Audio.isSoundOn ? 0 : 1];
	}
	
	
	

	public static void determineHighscoreTimes(Helicopter helicopter)
	{
		int bossNr = getBossNr();
		long highscoreTime = (playingTime + System.currentTimeMillis() - timeAktu)/60000;
		helicopter.scorescreenTimes[bossNr] = highscoreTime;
				
		if(helicopter.isPlayedWithoutCheats || SAVE_ANYWAY)
		{			
			recordTime[helicopter.getType().ordinal()][bossNr]
				= recordTime[helicopter.getType().ordinal()][bossNr] == 0
				  ? highscoreTime
				  : Math.min(recordTime[helicopter.getType().ordinal()][bossNr], highscoreTime);
			heliosMaxMoney = getHeliosMaxMoney();
		}			
	}
	
	static int getBossNr()
	{
		if(level%10 != 1) return -1;
		return level/10-1;
	}
	
	public static boolean hasAnyBossBeenKilledBefore()
	{
		for(int i = 0; i < Helicopter.NR_OF_TYPES; i++)
		{
			if(recordTime[i][0] != 0) return true;
		}
		return false;
	}
	
	public static int getHeliosMaxMoney()
	{
		int maxHeliosMoney = 0;
		for(int i = 0; i < Helicopter.NR_OF_TYPES - 1; i++)
		{
			maxHeliosMoney += getHighestRecordMoney(recordTime[i]);
		}	
		return maxHeliosMoney;
	}
	
	public static int getHighestRecordMoney(long [] array)
	{	
		if(array[0] == 0){return 0;}
		int index = 0;
		boolean indexSet = false;
		int maxMoney = heliosRecordEntryMoney((int)(array[index]), index);
		for(int i = 1; i < array.length; i++)
		{			
			if(array[i] == 0)
			{
				// TODO check method function why unused asssignment
				index = i-1;
				indexSet = true;
				break;
			}
			maxMoney = Math.max(maxMoney, heliosRecordEntryMoney((int)(array[i]), i));
		}	
		if(!indexSet)
		{
			maxMoney = Math.max(maxMoney, heliosRecordEntryMoney((int)(array[array.length-1]), array.length-1));
		}
		return maxMoney;
	}
	
	public static int heliosRecordEntryMoney(int arrayElement, int index)
	{
		return (int)(( MAX_MONEY * COMPARISON_RECORD_TIME * (index + 1)) / (37.5f * arrayElement*(5-index)*(5-index)));
	}

	public static boolean isBossLevel(){return isBossLevel(level);}
	public static boolean isBossLevel(int game_level){return game_level%10 == 0;}

	public static int bonusIncomePercentage()
	{		
		return MyMath.percentage(extraBonusCounter, overallEarnings);
	}

	public static void updateTimer()
	{
		if(commendationTimer > 0){
            commendationTimer--;}		
	}
}