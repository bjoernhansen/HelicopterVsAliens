package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.entities.GameEntityActivation;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.gui.button.ButtonGroup;
import de.helicopter_vs_aliens.gui.button.ButtonSpecifier;
import de.helicopter_vs_aliens.gui.button.GroundButtonType;
import de.helicopter_vs_aliens.gui.button.LeftSideRepairShopButtonType;
import de.helicopter_vs_aliens.gui.button.MainMenuButtonType;
import de.helicopter_vs_aliens.gui.button.SpecialUpgradeButtonType;
import de.helicopter_vs_aliens.gui.button.StandardUpgradeButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuCancelButtonType;
import de.helicopter_vs_aliens.gui.menu.Menu;
import de.helicopter_vs_aliens.gui.menu.MenuManager;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterFactory;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.Kamaitachi;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.scenery.BackgroundObject;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.score.HighScoreEntry;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.DESTROYED;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.control.TimeOfDay.DAY;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.BlockMessage.HELICOPTER_ALREADY_REPAIRED;
import static de.helicopter_vs_aliens.gui.BlockMessage.NOT_ENOUGH_MONEY_FOR_REPAIRS;
import static de.helicopter_vs_aliens.gui.BlockMessage.NOT_ENOUGH_MONEY_FOR_UPGRADE;
import static de.helicopter_vs_aliens.gui.BlockMessage.REACHED_MAXIMUM_LEVEL;
import static de.helicopter_vs_aliens.gui.BlockMessage.UNREPAIRED_BEFORE_MISSION;
import static de.helicopter_vs_aliens.gui.BlockMessage.UNREPAIRED_BEFORE_UPGRADE;
import static de.helicopter_vs_aliens.gui.BlockMessage.UPGRADE_ALREADY_INSTALLED;
import static de.helicopter_vs_aliens.gui.PriceLevel.REGULAR;
import static de.helicopter_vs_aliens.gui.WindowType.CONTACT;
import static de.helicopter_vs_aliens.gui.WindowType.DESCRIPTION;
import static de.helicopter_vs_aliens.gui.WindowType.GAME;
import static de.helicopter_vs_aliens.gui.WindowType.HELICOPTER_TYPES;
import static de.helicopter_vs_aliens.gui.WindowType.HIGH_SCORE;
import static de.helicopter_vs_aliens.gui.WindowType.INFORMATION;
import static de.helicopter_vs_aliens.gui.WindowType.REPAIR_SHOP;
import static de.helicopter_vs_aliens.gui.WindowType.SCORE_SCREEN;
import static de.helicopter_vs_aliens.gui.WindowType.SETTINGS;
import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_2_SERVANT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_4;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.FINAL_BOSS;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.ROCK;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.KAMAITACHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PEGASUS;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.BOOSTED_FIRE_RATE;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.INVINCIBLE;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.TRIPLE_DAMAGE;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.UNLIMITRED_ENERGY;

public class Events
{
	// Konstanten zur Berechnung der Reparaturkosten und der Boni bei Abschuss von Gegnern
	public static final int
		START = 0,					// Timer Start
		NUMBER_OF_BOSS_LEVEL = 5,
		NUMBER_OF_DEBUGGING_INFOS = 16,
		MAXIMUM_LEVEL = 50;
	
	public static final boolean
		CHEATS_ACTIVATABLE = true,
		IS_SAVE_GAME_SAVED_ANYWAY = true;
	
	public static HighScoreEntry[][]
		highScore = new HighScoreEntry[7][10];
	
	private static final int
		COMPARISON_RECORD_TIME = 60,	// angenommene Bestzeit für Besiegen von Boss 5
		TOTAL_LOSS_REPAIR_BASE_FEE = 875,
		DEFAULT_REPAIR_BASE_FEE = 350,
		MAX_MONEY = 5540500;			// für Komplettausbau erforderliche Geldmenge
	
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
		lastBonus, 				// für die Guthaben-Anzeige: zuletzt erhaltener Standard-Verdienst
		lastExtraBonus,			// für die Guthaben-Anzeige: zuletzt erhaltener Extra-Bonus
        lastMultiKill,			// für die Multi-Kill-Anzeige: Art des letzten Multi-Kill
        commendationTimer,		// reguliert, wie lange die Multi-Kill-Anzeige zu sehen ist
		heliosMaxMoney;

	public static long
		playingTime,            // bisher vergangene Spielzeit
    	lastCurrentTime;		// Zeitpunkt der letzten Aktualisierung von playing_time
	
	public static long [][]
		recordTime = new long [HelicopterType.size()][NUMBER_OF_BOSS_LEVEL];	// für jede Helikopter-Klasse die jeweils beste Zeit bis zum Besiegen eines der 5 Boss-Gegner
    
    public static boolean
		isRestartWindowVisible,				// = true: Neustart-Fenster wird angezeigt
    	reachedLevelTwenty[] = new boolean[HelicopterType.size()],
    	settingsChanged = false,
    	allPlayable = false;

	public static TimeOfDay
		timeOfDay = DAY;		// Tageszeit [NIGHT, DAY]

	// Variablen zur Nutzung von Cheats und Freischaltung von Helikoptern
	private static boolean
		cheatingMode = false;				// = true: Cheat-Modus aktiviert

	private static String
		cheatString = "";
	
	private static final String
		cheatCode = "+cheats";			// Code, mit welchem Cheats aktiviert werden können
	
	public static Enemy
		boss;							// Referenz auf den aktuellen Endgegner

	public static HelicopterType
		nextHelicopterType,				// aktuell im Startmenü ausgewählte Helikopter
		previousHelicopterType;			// zuletzt im Startmenü ausgewählte Helikopter
	

	static void keyTyped( KeyEvent e, Controller controller,
	                      Helicopter helicopter, Savegame savegame)
	{	
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE && !helicopter.isDamaged)
		{
			if(MenuManager.window == GAME){changeVisibilityOfInGameMenu(helicopter);}
			else if(MenuManager.window == START_SCREEN){
				Controller.shutDown();}
			else if(MenuManager.window != REPAIR_SHOP)
			{
				// TODO über Controller beziehen, nicht alles einzeln und getter definieren
				cancel(controller.getScenery(), controller.backgroundObjects, helicopter, savegame);
			}
		}		
		else if(MenuManager.window == SETTINGS && Menu.page == StartScreenMenuButtonType.BUTTON_5)
		{
			int name_length = HighScoreEntry.currentPlayerName.length();
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				Menu.page = StartScreenMenuButtonType.BUTTON_1;
				HighScoreEntry.checkName(savegame);
			}
			else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			{				
				if(name_length > 0)
				{
					HighScoreEntry.currentPlayerName
					= HighScoreEntry.currentPlayerName.substring(0, name_length-1);
				}
			}
			else if(name_length < 15 
					&& ((e.getKeyCode() >= 65 && e.getKeyCode() <= 90)
						|| e.getKeyCode() == 0 
						|| e.getKeyCode() == 32
						|| e.getKeyCode() == 45))
			{
				HighScoreEntry.currentPlayerName += e.getKeyChar();
			}			
		}		
		else if(e.getKeyChar() =='f')
		{
			controller.switchFpsVisibleState();
		}		
		else if(e.getKeyChar() =='p')
		{
			if(MenuManager.window == GAME && !helicopter.isDamaged)
			{
				changeVisibilityOfInGameMenu(helicopter);
			}
		}		
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if(	MenuManager.window == GAME
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
				if(MenuManager.window == GAME || MenuManager.window == REPAIR_SHOP)
				{
					if(money == 0)
					{
						if(MenuManager.window == GAME)
						{
							lastBonus = MAX_MONEY - money;
						}
						money = MAX_MONEY;
						helicopter.isPlayedWithCheats = true;
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
				if(MenuManager.window == GAME || MenuManager.window == REPAIR_SHOP)
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
			else if(MenuManager.window == GAME)
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
						playingTime += (nr_of_levelUp + Calculations.random(nr_of_levelUp)) * 60000L;
						levelUp(controller, nr_of_levelUp);
						helicopter.isPlayedWithCheats = true;
					}					
				}
				else if(e.getKeyChar() == '+')
				{
					if(level < 50)
					{
						playingTime += (1 + (Calculations.tossUp(0.4f) ? 1 : 0)) * 60000;
						levelUp(controller, 1);
						helicopter.isPlayedWithCheats = true;
					}
				}
				else if(e.getKeyChar() =='s')
				{
					Menu.specialInfoSelection = (Menu.specialInfoSelection +1)% NUMBER_OF_DEBUGGING_INFOS;
				}
				else if(e.getKeyChar() == 'd'){helicopter.getPowerUp(controller.powerUps, TRIPLE_DAMAGE, 	   true);}
				else if(e.getKeyChar() == 'i'){helicopter.getPowerUp(controller.powerUps, INVINCIBLE, 	   true);}
				else if(e.getKeyChar() == 'c'){helicopter.getPowerUp(controller.powerUps, UNLIMITRED_ENERGY, true);}
				else if(e.getKeyChar() == 'y'){helicopter.getPowerUp(controller.powerUps, BOOSTED_FIRE_RATE, true);}
				else if(e.getKeyChar() == 'a')
				{
					if(level < 51)
					{
						helicopter.repair();
						Menu.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
						Menu.buttons.get(MainMenuButtonType.NEW_GAME_2).setPrimaryLabel(Menu.dictionary.quit());
						helicopter.restoreEnergy();
						isRestartWindowVisible = false;
						helicopter.isPlayedWithCheats = true;
					}								
				}
				else if(e.getKeyChar() == 'm')
				{
					Enemy.changeMiniBossProb();
					helicopter.isPlayedWithCheats = true;
				}
				else if(e.getKeyChar() == 'n')
				{
					helicopter.destroyPlating();
					helicopter.crash();
				}
				else if(e.getKeyChar() == 't'){
					playingTime += 60000;}
			}
			else if(MenuManager.window == START_SCREEN)
			{
				// Resetten der Helicopter-Bestzeiten
				if(e.getKeyChar() == 'x')
				{					
					allPlayable = !allPlayable;
				}
				else if(e.getKeyChar() == '-')
				{					
					if(!Calculations.isEmpty(recordTime))
					{
						for(int i = 0; i < HelicopterType.size(); i++)
						{
							Arrays.fill(recordTime[i], 0);
							reachedLevelTwenty[i] = false;
						}
						heliosMaxMoney = getHeliosMaxMoney();
						savegame.saveInHighscore();
                        Controller.savegame.loseValidity();
						savegame.saveToFile(helicopter);
						Audio.play(Audio.emp);
					}
				}
				else if(e.getKeyChar() == '#')
				{
					savegame.saveInHighscore();
                    Controller.savegame.loseValidity();
					savegame.saveToFile(helicopter);
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
					MenuManager.window == GAME
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
		if(MenuManager.window == GAME)
		{
			inGameMousePressedLeft(controller, helicopter);
		}
		else if(MenuManager.window == REPAIR_SHOP)
		{
			repairShopMousePressedLeft(helicopter, controller.getScenery(), controller.enemies, controller.backgroundObjects);
		}
		else if(MenuManager.window == START_SCREEN)
		{
			startScreenMousePressedLeft();
		}
		else if(Menu.buttons.get(StartScreenMenuCancelButtonType.CANCEL).getBounds().contains(cursor))
		{
			// TODO viel mehr über den Controller beziehen, nicht alles einzeln übergeben, getter definieren
			cancel(controller.getScenery(), controller.backgroundObjects, helicopter, Controller.savegame);
		}
		else 
		{		
			startScreenMenuButtonClicked(controller.offGraphics,
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
				if(Menu.buttons.get(MainMenuButtonType.NEW_GAME_1).getBounds().contains(cursor))
				{
                    Controller.savegame.becomeValid();
				    Controller.savegame.saveToFile(helicopter);
					conditionalReset(controller, helicopter, true);
					restartGame(helicopter, controller.getScenery(), controller.backgroundObjects);
					Audio.applause1.stop();
				}
				else if(Menu.buttons.get(MainMenuButtonType.STOP_MUSIC).getBounds().contains(cursor))
				{
					Audio.play(Audio.choose);
					switchAudioActivationState(Controller.savegame);
				}
				else if(Menu.buttons.get(MainMenuButtonType.NEW_GAME_2).getBounds().contains(cursor))
				{
                    Controller.savegame.becomeValid();
				    Controller.savegame.saveToFile(helicopter);
					Controller.shutDown();
				}
				else if( 	(Menu.buttons.get(GroundButtonType.MAIN_MENU).getBounds().contains(cursor)
							&& helicopter.isOnTheGround())
						 || (Menu.buttons.get(MainMenuButtonType.CANCEL).getBounds().contains(cursor)))
				{
					changeVisibilityOfInGameMenu(helicopter);
				}
			}			
			else if(helicopter.isOnTheGround())
			{
				if(Menu.buttons.get(GroundButtonType.REPAIR_SHOP).getBounds().contains(cursor))
				{
					// Betreten der Werkstatt über den Werkstatt-Button
					conditionalReset(controller, helicopter, false);
					enterRepairShop(helicopter);
				}
				else if(Menu.buttons.get(GroundButtonType.MAIN_MENU).getBounds().contains(cursor))
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
		else if(	Menu.buttons.get(MainMenuButtonType.NEW_GAME_2).getBounds().contains(cursor)
				 	&& isRestartWindowVisible)
		{
			// Betreten der Werkstatt nach Absturz bzw. Neustart bei Geldmangel				
			conditionalReset(controller, helicopter, true);
			if(money < repairFee(helicopter, true) || level > 50)
			{
				if(level > 50)
				{
					playingTime = 60000 * helicopter.scoreScreenTimes[4];
				}
				else
				{
					playingTime = playingTime
						     	   + System.currentTimeMillis() 
						           - lastCurrentTime;
					helicopter.scoreScreenTimes[4] = playingTime /60000;
				}				
							   
				changeWindow(SCORE_SCREEN);
															
				helicopter.isDamaged = false;
                Controller.savegame.becomeValid();
				Controller.savegame.saveToFile(helicopter);
				Colorations.updateScorescreenColors(helicopter);
			}
			else{enterRepairShop(helicopter);}
		}
	}
	
	private static void repairShopMousePressedLeft(Helicopter helicopter,
												   Scenery scenery,
												   EnumMap<CollectionSubgroupType, LinkedList<Enemy>> enemies,
												   EnumMap<CollectionSubgroupType, LinkedList<BackgroundObject>> bgObject)
	{
		// Reparatur des Helikopters
		if(Menu.buttons.get(LeftSideRepairShopButtonType.REPAIR).getBounds().contains(cursor))
		{
			if(	helicopter.hasMaximumPlating())
			{
				Menu.block(HELICOPTER_ALREADY_REPAIRED);
			}
			else if(money < repairFee(helicopter, helicopter.isDamaged))
			{
				Menu.block(NOT_ENOUGH_MONEY_FOR_REPAIRS);
			}
			else
			{
				money -= repairFee(helicopter, helicopter.isDamaged);
				timeOfDay = (!helicopter.hasSpotlights || Calculations.tossUp(0.33f)) ? DAY : NIGHT;
				Menu.buttons.get(LeftSideRepairShopButtonType.MISSION).setPrimaryLabel(Menu.dictionary.mission());
												
				if(!(level == 50 && helicopter.hasAllUpgrades()))
				{
					enemies.get(INACTIVE).addAll(enemies.get(ACTIVE));
					enemies.get(ACTIVE).clear();
					level = level - ((level - 1) % 5);						
					Enemy.adaptToLevel(helicopter, level, false);
					if(level < 6)
					{
						// TODO ggf. wird BackgroundObject reset innerhalb von scenery reset aufgerufen (aufruf immer gemeinsam?)
						BackgroundObject.reset(bgObject);
						scenery.reset();
					}
					killsAfterLevelUp = 0;
					enemies.get(INACTIVE).addAll(enemies.get(DESTROYED));
					enemies.get(DESTROYED).clear();
					Menu.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
					Enemy.currentRock = null;
				}
				else
				{
					Enemy.bossSelection = FINAL_BOSS;
					Enemy.maxNr = 1;
					Enemy.maxBarrierNr = 0;
				}
				helicopter.repair();
				Menu.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
				if(level == 50){helicopter.restoreEnergy();}
				helicopter.placeAtStartpos();
			}
		}		
		// Einsatz fliegen
		else if(Menu.buttons.get(LeftSideRepairShopButtonType.MISSION).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(UNREPAIRED_BEFORE_MISSION);}
			else
			{				
				Menu.stopButtonHighlighting();
				Menu.messageTimer = 0;
				startMission(helicopter);				
			}
		}
				
		/*
		 * Die Spezial-Upgrades
		 */
		
		// Scheinwerfer
		else if(Menu.buttons.get(SpecialUpgradeButtonType.SPOTLIGHT).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasSpotlights){
				Menu.block(UPGRADE_ALREADY_INSTALLED);}
			else if(money < helicopter.getSpotlightCosts()){
				Menu.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);}
			else
			{
				Audio.play(Audio.cash);
				money -= helicopter.getSpotlightCosts();
				helicopter.hasSpotlights = true;
				timeOfDay = NIGHT;
				
				Menu.updateRepairShopButtonsAfterSpotlightPurchase();

				for(Enemy enemy : enemies.get(DESTROYED))
				{
					enemy.repaint();
				}
				for(Enemy enemy : enemies.get(ACTIVE))
				{
					if (enemy.type != ROCK)
					{
						enemy.farbe1 = Colorations.dimColor(enemy.farbe1, Colorations.BARRIER_NIGHT_DIM_FACTOR);
						enemy.farbe2 = Colorations.dimColor(enemy.farbe2, Colorations.BARRIER_NIGHT_DIM_FACTOR);
						enemy.repaint();
					}
				}
			}
		}
		// Goliath-Panzerung
		else if(Menu.buttons.get(SpecialUpgradeButtonType.GOLIATH_PLATING).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasGoliathPlating()){
				Menu.block(UPGRADE_ALREADY_INSTALLED);}
			else if(money < helicopter.getGoliathCosts())
			{
				Menu.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
			}
			else
			{
				Audio.play(Audio.cash);						
				money -= helicopter.getGoliathCosts();
				helicopter.installGoliathPlating();
				Menu.buttons.get(SpecialUpgradeButtonType.GOLIATH_PLATING).adjustCostsToZero();
			}
		}
		// Durchstoßsprengköpfe
		else if(Menu.buttons.get(SpecialUpgradeButtonType.PIERCING_WARHEADS).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasPiercingWarheads){
				Menu.block(UPGRADE_ALREADY_INSTALLED);}
			else if(money < helicopter.getPiercingWarheadsCosts())
			{
				Menu.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
			}
			else
			{
				Audio.play(Audio.cash);
                money -= helicopter.getPiercingWarheadsCosts();
                helicopter.installPiercingWarheads();
				Menu.buttons.get(SpecialUpgradeButtonType.PIERCING_WARHEADS).adjustCostsToZero();
			}
		}
		// zusätzliche Bordkanonen
		else if(Menu.buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasAllCannons())
			{
				Menu.block(UPGRADE_ALREADY_INSTALLED);
			}
			// TODO if Bedingung auslagern in Methode
			// TODO neue Helicopter Methoden: getNextCannonCost, getMaximumNumberOfCannons, ... je nach Bedarf
			else if(	(money < helicopter.getLastCannonCost()) &&
						!((helicopter.getType() == OROCHI ||(helicopter.getType() == HELIOS && recordTime[OROCHI.ordinal()][4]!=0)) && money >= Helicopter.CHEAP_SPECIAL_COSTS && helicopter.numberOfCannons == 1))
			{
				Menu.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
			}
			else
			{
				Audio.play(Audio.cash);
				Button extraCannonButton = Menu.buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS);
				if((helicopter.getType() == OROCHI ||(helicopter.getType() == HELIOS && recordTime[OROCHI.ordinal()][4]!=0)) && helicopter.numberOfCannons == 1)
				{
					money -= Helicopter.CHEAP_SPECIAL_COSTS;
					if(helicopter.getType() == OROCHI)
					{
						extraCannonButton.adjustCostsTo(Helicopter.STANDARD_SPECIAL_COSTS);
						extraCannonButton.setPrimaryLabel(Menu.dictionary.thirdCannon());
						extraCannonButton.setCostColor(REGULAR.getColor());
					}
					else
					{
						extraCannonButton.adjustCostsToZero();
					}					
				}
				else
				{
					money -= helicopter.getLastCannonCost();
					extraCannonButton.adjustCostsToZero();
				}
				helicopter.numberOfCannons++;
			}
		}
		// das klassenspezifische SpezialUpgrade
		else if(Menu.buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Menu.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasFifthSpecial()){
				Menu.block(UPGRADE_ALREADY_INSTALLED);}
			else if(money < helicopter.getFifthSpecialCosts())
			{
				Menu.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
			}
			else
			{
				Audio.play(Audio.cash);
				money -= helicopter.getFifthSpecialCosts();
				helicopter.obtainFifthSpecial();
				// TODO adjustFireRate in obtainFifthSpecial integrieren, PowerUp fireRate berücksichtigen
				if(helicopter.getType() == KAMAITACHI || helicopter.getType() == PEGASUS){helicopter.adjustFireRate(false);}
				Menu.buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).adjustCostsToZero();
			}
		}
			
		/*
		 *  Die Standard-Upgrades
		 */			
		else for(ButtonSpecifier buttonSpecifier : StandardUpgradeButtonType.getValues())
		{		
			StandardUpgradeButtonType buttonType = (StandardUpgradeButtonType) buttonSpecifier;
			StandardUpgradeType standardUpgradeType = buttonType.getStandardUpgradeType();
			if(Menu.buttons.get(buttonSpecifier).getBounds().contains(cursor))
			{
				if(helicopter.isDamaged){
					Menu.block(UNREPAIRED_BEFORE_UPGRADE);}
				else if(helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType)){
					Menu.block(REACHED_MAXIMUM_LEVEL);}
				else if(money < helicopter.getUpgradeCostFor(standardUpgradeType))
				{
					Menu.block(NOT_ENOUGH_MONEY_FOR_UPGRADE );
				}
				else
				{
					Audio.play(Audio.cash);
					money -= helicopter.getUpgradeCostFor(standardUpgradeType);
					helicopter.upgrade(standardUpgradeType);
					if(helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType))
					{
						Menu.buttons.get(buttonSpecifier).adjustCostsToZero();
					}
					else
					{
						Menu.buttons.get(buttonSpecifier).adjustCostsTo(helicopter.getUpgradeCostFor(standardUpgradeType));
					}
				}					
				break;
			}
		}		
	}
	
	private static void startScreenMousePressedLeft()
	{
		// TODO eventuell nach Menu auslagern
		if(Menu.triangle[0].contains(cursor))
		{
			Menu.crossPosition = (Menu.crossPosition + 1)% HelicopterType.size();
			Menu.cross = Menu.getCrossPolygon();
			Menu.helicopterSelection = (Menu.helicopterSelection + HelicopterType.size() - 1)% HelicopterType.size();
			Audio.play(Audio.choose);
		}
		else if(Menu.triangle[1].contains(cursor))
		{
			Menu.crossPosition = (Menu.crossPosition + HelicopterType.size() - 1)% HelicopterType.size();
			Menu.cross = Menu.getCrossPolygon();
			Menu.helicopterSelection = (Menu.helicopterSelection + 1)% HelicopterType.size();
			Audio.play(Audio.choose);
		}
		else if(Menu.helicopterFrame[0].contains(cursor)||
				Menu.helicopterFrame[1].contains(cursor)||
				Menu.helicopterFrame[2].contains(cursor)||
				Menu.helicopterFrame[3].contains(cursor))
		{				
			if(allPlayable || nextHelicopterType.isUnlocked())
			{
				startNewGame(nextHelicopterType, Controller.savegame);
			}
			else
			{
				Menu.blockHelicopterSelection(nextHelicopterType);
			}
		}
		else if(Menu.buttons.get(StartScreenButtonType.INFORMATION).getBounds().contains(cursor))
		{			
			newStartScreenMenuWindow(INFORMATION, true);
			Menu.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(true);
		}
		else if(Menu.buttons.get(StartScreenButtonType.HIGH_SCORE).getBounds().contains(cursor))
		{			
			newStartScreenMenuWindow(HIGH_SCORE, true);
		}
		else if(Menu.buttons.get(StartScreenButtonType.CONTACT).getBounds().contains(cursor))
		{			
			newStartScreenMenuWindow(CONTACT, true);
		}
		else if(Menu.buttons.get(StartScreenButtonType.SETTINGS).getBounds().contains(cursor))
		{			
			newStartScreenMenuWindow(SETTINGS, true);
			if(HighScoreEntry.currentPlayerName.equals(Menu.DEFAULT_PLAYER_NAME))
			{
				Menu.buttons.get(StartScreenMenuButtonType.BUTTON_5).setMarked(true);
			}
		}
		else if(Menu.buttons.get(StartScreenButtonType.RESUME_LAST_GAME).getBounds().contains(cursor))
		{
			if(Controller.savegame.isValid)
			{
				Audio.play(Audio.levelUp);
				startSavedGame(Controller.savegame);
			}
			else{Audio.play(Audio.block);}			
		}		
		else if(Menu.buttons.get(StartScreenButtonType.QUIT).getBounds().contains(cursor))
		{					
			Controller.shutDown();
		}		
	}
	
	private static void cancel(Scenery scenery, EnumMap<CollectionSubgroupType, LinkedList<BackgroundObject>> bgObject,
							   Helicopter helicopter, Savegame savegame)
	{
		Audio.play(Audio.choose);
		if(MenuManager.window == SCORE_SCREEN)
		{				
			savegame.saveInHighscore();
			restartGame(helicopter, scenery, bgObject);
			savegame.loseValidity();
			savegame.saveToFile(helicopter);
			Menu.buttons.get(StartScreenMenuCancelButtonType.CANCEL).setHighlighted(false);
		}
		else if(MenuManager.window == DESCRIPTION)
		{
			if(Menu.page == StartScreenMenuButtonType.BUTTON_6){
				Menu.label.setBounds(Main.displayShift.width  + 42,
													   Main.displayShift.height + 83, 940, 240);}
			newStartScreenMenuWindow(INFORMATION, false);
			Menu.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(true);
			Menu.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(false);
		}
		else if(MenuManager.window == HELICOPTER_TYPES)
		{
			if(Menu.page == StartScreenMenuButtonType.BUTTON_2){
				Menu.label.setVisible(true);}
			newStartScreenMenuWindow(DESCRIPTION, false);
			Menu.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(true);
		}
		else
		{
			if(MenuManager.window == INFORMATION)
			{
				Menu.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(false);
			}
			else if(MenuManager.window == SETTINGS)
			{
				Menu.buttons.get(StartScreenMenuButtonType.BUTTON_5).setMarked(false);
				HighScoreEntry.checkName(savegame);
				if(settingsChanged)
				{					
					savegame.writeToFile();
					settingsChanged = false;
				}
			}
			Menu.label.setVisible(false);
			Menu.stopButtonHighlighting();
			MenuManager.window = START_SCREEN;
		}
	}
	
	private static void startScreenMenuButtonClicked(Graphics2D offGraphics,
													 Helicopter helicopter,
													 Savegame savegame)
	{
		// TODO for each schleife über die ButtonTypen
		for(ButtonSpecifier buttonSpecifier : ButtonGroup.START_SCREEN_MENU.getButtonSpecifiers())
		{
			Button currentButton = Menu.buttons.get(buttonSpecifier);
			if( currentButton.getBounds().contains(cursor) && 
				currentButton.isVisible() &&
				(Menu.page != buttonSpecifier || MenuManager.window == SETTINGS))
			{
				StartScreenMenuButtonType oldPage = Menu.page;
				if(MenuManager.window == DESCRIPTION && Menu.page == StartScreenMenuButtonType.BUTTON_6)
				{
					Menu.label.setBounds(Main.displayShift.width  + 42,
										    Main.displayShift.height + 83, 940, 240);
				}
				else if(MenuManager.window == HELICOPTER_TYPES && Menu.page == StartScreenMenuButtonType.BUTTON_2)
				{
					Menu.label.setVisible(true);
				}
				Menu.page = (StartScreenMenuButtonType) buttonSpecifier;
				if(MenuManager.window == DESCRIPTION && Menu.page == StartScreenMenuButtonType.BUTTON_6)
				{
					Menu.label.setBounds(Main.displayShift.width  + 92,
											Main.displayShift.height + 83, 890, 240);
				}
				else if(MenuManager.window == HELICOPTER_TYPES && Menu.page == StartScreenMenuButtonType.BUTTON_2)
				{
					Menu.label.setVisible(false);
				}
				if(MenuManager.window == INFORMATION && Menu.page == StartScreenMenuButtonType.BUTTON_3)
				{							
					newStartScreenMenuWindow(DESCRIPTION, false);
					Menu.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(false);
					Menu.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(true);
				}
				else if(MenuManager.window == DESCRIPTION && Menu.page == StartScreenMenuButtonType.BUTTON_7)
				{
					newStartScreenMenuWindow(HELICOPTER_TYPES, false);
					Menu.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(false);
				}
				else if(MenuManager.window == SETTINGS)
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
					Menu.updateStartScreenMenuLabelText();
				}
				break;
			}
		}		
	}
	
	private static void settingsMousePressedLeft( Helicopter helicopter, 
	                                              Button currentButton,
												  Graphics2D offGraphics, 
												  Savegame savegame,
												  StartScreenMenuButtonType oldPage)
	{
		if(Menu.page == StartScreenMenuButtonType.BUTTON_1)
		{	
			Main.switchDisplayMode(currentButton);
		}
		else if(Menu.page == StartScreenMenuButtonType.BUTTON_2)
		{			
			switchAntialiasingActivationState(offGraphics, currentButton);
		}						
		else if(Menu.page == StartScreenMenuButtonType.BUTTON_3)
		{
			switchAudioActivationState(savegame);
		}
		else if(Menu.page == StartScreenMenuButtonType.BUTTON_4)
		{
			Menu.changeLanguage(helicopter, savegame);
		}		
		else if(Menu.page == StartScreenMenuButtonType.BUTTON_7)
		{
			Audio.changeBgMusicMode(savegame);
		}
		if(oldPage == StartScreenMenuButtonType.BUTTON_5)
		{
			if(Menu.page == StartScreenMenuButtonType.BUTTON_5){
				Menu.page = StartScreenMenuButtonType.BUTTON_1;}
			HighScoreEntry.checkName(savegame);
		}
		Menu.updateStartScreenMenuLabelText();
	}
	
	private static void switchAntialiasingActivationState(	Graphics2D offGraphics,
															Button currentButton)
	{
		Controller.antialiasing = !Controller.antialiasing;
		offGraphics.setRenderingHint( 
				RenderingHints.KEY_ANTIALIASING,
				Controller.antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		Menu.dictionary.updateAntialiasing();
		currentButton.setPrimaryLabel(Menu.dictionary.antialiasing());
	}

	static void mouseReleased(MouseEvent mouseEvent, Helicopter helicopter)
	{
		if(mouseEvent.getButton() == 1)
		{
			helicopter.isContiniousFireEnabled = false;
		}
		else if(MenuManager.window == GAME && mouseEvent.getButton() == 3 && !helicopter.isDamaged)
		{
			helicopter.rightMouseButtonReleaseAction(mouseEvent);
		}		
	}
	
	// Aktualisierung der Ziel-Koordinaten, auf welche der Helikopter zufliegt
	static void mouseMovedOrDragged(MouseEvent e, Helicopter helicopter)
	{		
		if(!helicopter.isDamaged || MenuManager.window == REPAIR_SHOP)
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

	private static void initializeFromSaveGame(Helicopter helicopter, Savegame savegame)
	{
		restore(savegame);
		for(int i =  highestSavePointLevelBefore(level); i <= level; i++)
		{
			Enemy.adaptToLevel(helicopter, i, false);
		}
		generalInitialization();
	}

	private static int highestSavePointLevelBefore(int level)
	{
		return level - ((level - 1) % 5);
	}

	private static void initializeForNewGame()
	{
		reset();
		Enemy.adaptToFirstLevel();
		generalInitialization();
	}

	private static void generalInitialization()
	{
		changeWindow(GAME);
		lastCurrentTime = System.currentTimeMillis();
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

	/* Reset: Zurücksetzen diverser spiel-interner Variablen;
	 * bedingter (conditional) Rest, da unterschieden wird, ob nur die 
	 * Werkstatt betreten oder das Spiel komplett neu gestartet wird
	 */
	private static void conditionalReset(Controller controller, Helicopter helicopter, boolean totalReset)
	{
		Audio.play(Audio.choose);
		
		helicopter.bonusKillsTimer = 1;
		if(helicopter.getType() == KAMAITACHI){((Kamaitachi)helicopter).evaluateBonusKills();}
		helicopter.resetStateGeneral(totalReset);
        helicopter.resetStateTypeSpecific();
		
		boss = null;		
		lastExtraBonus = 0;
		lastMultiKill = 0;
		commendationTimer = 0;		
		isRestartWindowVisible = false;
		lastBonus = 0;
		
		Menu.conditionalReset();
		
		// kein "active enemy"-Reset, wenn Boss-Gegner 2 Servants aktiv
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
			if(level < 6)
			{
				BackgroundObject.reset(controller.backgroundObjects);
				controller.getScenery().reset();
			}
		}									
		controller.explosions.get(INACTIVE).addAll(controller.explosions.get(ACTIVE));
		controller.explosions.get(ACTIVE).clear();
		controller.missiles.get(INACTIVE).addAll(controller.missiles.get(ACTIVE));
		controller.missiles.get(ACTIVE).clear();
		controller.enemyMissiles.get(INACTIVE).addAll(controller.enemyMissiles.get(ACTIVE));
		controller.enemyMissiles.get(ACTIVE).clear();
		//controller.powerUps.get(INACTIVE).addAll(controller.powerUps.get(ACTIVE));
		controller.getGameEntityRecycler().storeAll(controller.powerUps.get(ACTIVE));
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

	static private void startNewGame(HelicopterType helicopterType, Savegame savegame)
	{
		Audio.play(Audio.applause1);
		Helicopter newHelicopter = HelicopterFactory.createForNewGame(helicopterType);
		Controller.getInstance().setHelicopter(newHelicopter);
		savegame.saveInHighscore();
		initializeForNewGame();
		Controller.savegame.becomeValid();
		Controller.savegame.saveToFile(newHelicopter);
		performGeneralActionsBeforeGameStart();
	}

	static private void startSavedGame(Savegame savegame)
	{
		Helicopter savedHelicopter = HelicopterFactory.createFromSavegame(savegame);
		Controller.getInstance().setHelicopter(savedHelicopter);
		initializeFromSaveGame(savedHelicopter, savegame);
		Menu.updateRepairShopButtons(savedHelicopter);
		performGeneralActionsBeforeGameStart();
	}

	private static void performGeneralActionsBeforeGameStart()
	{
		Audio.play(Audio.choose);
		Menu.reset();
		Menu.finalizeRepairShopButtons();
	}

	// TODO die bgObject List sollte teil innerhalb von Scenery definiert werden
	private static void restartGame(Helicopter helicopter, Scenery scenery, EnumMap<CollectionSubgroupType, LinkedList<BackgroundObject>> bgObject)
	{		
		changeWindow(START_SCREEN);
		helicopter.reset();
		BackgroundObject.reset(bgObject);
		scenery.reset();
	}

	private static void startMission(Helicopter helicopter)
	{		
		changeWindow(GAME);		
		Audio.play(Audio.choose);	
		lastCurrentTime = System.currentTimeMillis();
		helicopter.prepareForMission();
		Controller.savegame.becomeValid();
		Controller.savegame.saveToFile(helicopter);
	}
	
	private static void enterRepairShop(Helicopter helicopter)
	{
		changeWindow(REPAIR_SHOP);		
		
		Audio.applause1.stop();
		playingTime += System.currentTimeMillis() - lastCurrentTime;
		Menu.repairShopTime = Menu.returnTimeDisplayText(playingTime);
		helicopter.setRelativePlatingDisplayColor();
		if(!helicopter.hasMaximumPlating())
	    {
			Menu.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsTo(repairFee(helicopter, helicopter.isDamaged));
	    }
		else
		{
			Menu.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
		}
		Menu.clearMessage();
	}

	public static int repairFee(Helicopter helicopter, boolean totalLoss)
	{		
		return (totalLoss
					? TOTAL_LOSS_REPAIR_BASE_FEE 
					: DEFAULT_REPAIR_BASE_FEE) 
				+ 25 * Math.round( 6.5f * helicopter.missingPlating());
	}
	
	private static void changeWindow(WindowType newWindow)
	{		
		MenuManager.window = newWindow;
		Audio.refreshBackgroundMusic();
		Colorations.bg = newWindow == GAME && timeOfDay == DAY ? Colorations.sky: Color.black;
	}

	private static void newStartScreenMenuWindow(WindowType newWindow, boolean hasJustEntered)
	{
		if (hasJustEntered)
		{
			Menu.stopButtonHighlighting();
		}
		Audio.play(Audio.choose);		
		MenuManager.window = newWindow;
		Menu.adaptToNewWindow(hasJustEntered);
		Menu.updateStartScreenMenuButtons();
	}

	// überprüfen, ob Level-Up Voraussetzungen erfüllt. Wenn ja: Schwierigkeitssteigerung
	static void checkForLevelUp(Controller controller)
	{
		if( killsAfterLevelUp >= numberOfKillsNecessaryForNextLevelUp() && level < 50)
		{
			levelUp(controller, 1);
		}
	}
	
	public static int numberOfKillsNecessaryForNextLevelUp()
	{
		return 5 - 5 * (int)((float)(level-1)/10) + level;
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
        helicopter.levelUpEffect(previousLevel);
        maxLevel = Math.max(level, maxLevel);
        
		if(isBossLevel()){Enemy.getRidOfSomeEnemies(helicopter, controller.enemies, controller.explosions);}
		
		if(	isBossLevel() || isBossLevel(previousLevel) || level == 49)
		{
			Audio.refreshBackgroundMusic();
			if(previousLevel % 10 == 0){Audio.play(Audio.applause1);}
		}
		Menu.levelDisplayTimer.start();
		Enemy.adaptToLevel(helicopter, level, true);
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
			playingTime += System.currentTimeMillis() - lastCurrentTime;
		}
		else
		{
			Menu.isMenuVisible = false;
			lastCurrentTime = System.currentTimeMillis();
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
		Menu.dictionary.updateAudioActivation();
		Menu.buttons.get(MainMenuButtonType.STOP_MUSIC).setPrimaryLabel(Menu.dictionary.audioActivation());
		Menu.buttons.get(StartScreenMenuButtonType.BUTTON_3).setPrimaryLabel(Menu.dictionary.audioActivation());
	}
	
	public static void determineHighscoreTimes(Helicopter helicopter)
	{
		int bossNr = getBossNr();
		long highScoreTime = (playingTime + System.currentTimeMillis() - lastCurrentTime)/60000;
		helicopter.scoreScreenTimes[bossNr] = highScoreTime;
				
		if(helicopter.isCountingAsFairPlayedHelicopter())
		{			
			recordTime[helicopter.getType().ordinal()][bossNr]
				= recordTime[helicopter.getType().ordinal()][bossNr] == 0
				  ? highScoreTime
				  : Math.min(recordTime[helicopter.getType().ordinal()][bossNr], highScoreTime);
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
		for(int i = 0; i < HelicopterType.size(); i++)
		{
			if(recordTime[i][0] != 0) return true;
		}
		return false;
	}
	
	public static int getHeliosMaxMoney()
	{
		int maxHeliosMoney = 0;
		for(int i = 0; i < HelicopterType.size() - 1; i++)
		{
			maxHeliosMoney += getHighestRecordMoney(recordTime[i]);
		}	
		return maxHeliosMoney;
	}
	
	public static int getHighestRecordMoney(long[] recordTime)
	{	
		// TODO Code überarbeiten - unverständlich
		if(recordTime[0] == 0){return 0;}
		int bossLevelIndex = 0;
		boolean indexSet = false;
		int maxMoney = heliosRecordEntryMoney((int)(recordTime[bossLevelIndex]), bossLevelIndex);
		for(int i = 1; i < recordTime.length; i++)
		{			
			if(recordTime[i] == 0)
			{
				// TODO check method function why unused assignment
				bossLevelIndex = i-1;
				indexSet = true;
				break;
			}
			maxMoney = Math.max(maxMoney, heliosRecordEntryMoney((int)(recordTime[i]), i));
		}	
		if(!indexSet)
		{
			maxMoney = Math.max(maxMoney, heliosRecordEntryMoney((int)(recordTime[recordTime.length-1]), recordTime.length-1));
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
		return Calculations.percentage(extraBonusCounter, overallEarnings);
	}

	public static void updateTimer()
	{
		if(commendationTimer > 0){
            commendationTimer--;}		
	}
	
	public static boolean hasSelectedHelicopterChanged()
	{
		return previousHelicopterType != nextHelicopterType;
	}

	public static int getBestNonFinalMainBossKillBy(HelicopterType privilegedHelicopter)
	{
		for(int i = 0; i < 4; i++)
		{
			if(Events.recordTime[privilegedHelicopter.ordinal()][i] == 0) return i;
		}
		return 4;
	}

	public static void updateFinance(Enemy enemy, Helicopter helicopter) {
		lastBonus = enemy.calculateReward(helicopter);
		money += lastBonus;
		overallEarnings += lastBonus;
		lastExtraBonus = 0;
	}
	
	public static boolean hasEnoughTimePassedSinceLastCreation()
	{
		if(isBossLevel())
		{
			return lastCreationTimer > 135;
		}
		return lastCreationTimer > 20;
	}
	
	public static boolean wereRandomRequirementsMet(int numberOfMissingEnemies)
	{
		if(isBossLevel())
		{
			return GameEntityActivation.isQuicklyApproved();
		}
		return GameEntityActivation.isApproved(numberOfMissingEnemies);
	}
	
	public static boolean wasMaximumLevelExceeded()
	{
		return level > MAXIMUM_LEVEL;
	}
}