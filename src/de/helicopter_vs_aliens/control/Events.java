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
import de.helicopter_vs_aliens.gui.button.StartScreenSubCancelButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.BasicEnemy;
import de.helicopter_vs_aliens.model.enemy.boss.BossEnemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterFactory;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.Helios;
import de.helicopter_vs_aliens.model.helicopter.Kamaitachi;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.score.HighScore;
import de.helicopter_vs_aliens.score.RecordTimeManager;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

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
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.KAMAITACHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.OROCHI;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.BOOSTED_FIRE_RATE;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.INVINCIBLE;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.TRIPLE_DAMAGE;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.UNLIMITED_ENERGY;

// TODO Klasse sollte nicht rein statisch sein
public class Events
{
	// Konstanten zur Berechnung der Reparaturkosten und der Boni bei Abschuss von Gegnern
	public static final int
		START = 0,					// Timer Start
		NUMBER_OF_DEBUGGING_INFOS = 16,
		MAXIMUM_LEVEL = 50;
	
	public static final boolean
		IS_CHEATING_MODE_ALWAYS_ACTIVE = true,
		IS_CHEATING_MODE_ACTIVATABLE = true,
		IS_SAVE_GAME_SAVED_ANYWAY = true;
		
	private static final int
		TOTAL_LOSS_REPAIR_BASE_FEE = 875,
		DEFAULT_REPAIR_BASE_FEE = 350;
	
	public static final int
		MAX_MONEY = 5540500;			// für Komplettausbau erforderliche Geldmenge
	
	static final Point
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
	
    public static boolean
		isRestartWindowVisible,				// = true: Neustart-Fenster wird angezeigt
    	settingsChanged = false,
    	allPlayable = false;
		
	public static TimeOfDay
		timeOfDay = DAY;		// Tageszeit [NIGHT, DAY]

	// Variablen zur Nutzung von Cheats und Freischaltung von Helikoptern
	private static boolean
		cheatingMode = IS_CHEATING_MODE_ALWAYS_ACTIVE;				// = true: Cheat-Modus aktiviert
	
	public static String
		currentPlayerName = Window.DEFAULT_PLAYER_NAME;
	
	private static String
		cheatString = "";
	
	private static final String
		cheatCode = "+cheats";			// Code, mit welchem Cheats aktiviert werden können
	
	public static BossEnemy
		boss;							// Referenz auf den aktuellen Endgegner

	public static HelicopterType
		nextHelicopterType,				// aktuell im Startmenü ausgewählte Helikopter
		previousHelicopterType;			// zuletzt im Startmenü ausgewählte Helikopter
	
	public static HighScore
		highScore = new HighScore();
	
	public static Set<HelicopterType>
		helicoptersThatReachedLevel20 = EnumSet.noneOf(HelicopterType.class);
	
	public static RecordTimeManager
		recordTimeManager = new RecordTimeManager();
	

	static void keyTyped( KeyEvent e, Controller controller)
	{
		Helicopter helicopter = controller.getHelicopter();
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE && !helicopter.isDamaged)
		{
			if(WindowManager.window == GAME){changeVisibilityOfInGameMenu(helicopter);}
			else if(WindowManager.window == START_SCREEN){
				controller.shutDown();}
			else if(WindowManager.window != REPAIR_SHOP)
			{
				cancel(controller);
			}
		}		
		else if(WindowManager.window == SETTINGS && Window.page == StartScreenMenuButtonType.BUTTON_5)
		{
			int name_length = currentPlayerName.length();
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				Window.page = StartScreenMenuButtonType.BUTTON_1;
				checkName(controller.getSaveGame());
			}
			else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			{				
				if(name_length > 0)
				{
					currentPlayerName = currentPlayerName.substring(0, name_length-1);
				}
			}
			else if(name_length < 15 
					&& ((e.getKeyCode() >= 65 && e.getKeyCode() <= 90)
						|| e.getKeyCode() == 0 
						|| e.getKeyCode() == 32
						|| e.getKeyCode() == 45))
			{
				currentPlayerName += e.getKeyChar();
			}			
		}		
		else if(e.getKeyChar() =='f')
		{
			controller.switchFpsVisibleState();
		}		
		else if(e.getKeyChar() =='p')
		{
			if(WindowManager.window == GAME && !helicopter.isDamaged)
			{
				changeVisibilityOfInGameMenu(helicopter);
			}
		}		
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if(	WindowManager.window == GAME
				&&	helicopter.isActive
				&& !helicopter.isDamaged
				&& !Window.isMenuVisible)
			{
				helicopter.turnAround();
			}
		}		
		else if(cheatingMode)
		{			
			if(e.getKeyChar() == 'e')
			{
				if(WindowManager.window == GAME || WindowManager.window == REPAIR_SHOP)
				{
					if(money == 0)
					{
						if(WindowManager.window == GAME)
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
					Window.moneyDisplayTimer = START;
				}
			}				
			else if(e.getKeyChar() == 'u')
			{
				if(WindowManager.window == GAME || WindowManager.window == REPAIR_SHOP)
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
			else if(WindowManager.window == GAME)
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
					Window.specialInfoSelection = (Window.specialInfoSelection +1)% NUMBER_OF_DEBUGGING_INFOS;
				}
				// TODO übergabe von powerUps anders regeln
				else if(e.getKeyChar() == 'd'){helicopter.switchPowerUpActivationState(controller.powerUps, TRIPLE_DAMAGE);}
				else if(e.getKeyChar() == 'i'){helicopter.switchPowerUpActivationState(controller.powerUps, INVINCIBLE);}
				else if(e.getKeyChar() == 'c'){helicopter.switchPowerUpActivationState(controller.powerUps, UNLIMITED_ENERGY);}
				else if(e.getKeyChar() == 'y'){helicopter.switchPowerUpActivationState(controller.powerUps, BOOSTED_FIRE_RATE);}
				else if(e.getKeyChar() == 'a')
				{
					if(level < 51)
					{
						helicopter.repair();
						Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
						Window.buttons.get(MainMenuButtonType.NEW_GAME_2).setPrimaryLabel(Window.dictionary.quit());
						helicopter.restoreEnergy();
						isRestartWindowVisible = false;
						helicopter.isPlayedWithCheats = true;
					}								
				}
				else if(e.getKeyChar() == 'm')
				{
					BasicEnemy.changeMiniBossProb();
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
			else if(WindowManager.window == START_SCREEN)
			{
				// Resetten der Helicopter-Bestzeiten
				Savegame savegame = controller.getSaveGame();
				if(e.getKeyChar() == 'x')
				{					
					allPlayable = !allPlayable;
				}
				else if(e.getKeyChar() == '-')
				{					
					if(!recordTimeManager.isEmpty())
					{
						recordTimeManager.eraseRecordTimes();
						helicoptersThatReachedLevel20.clear();
						heliosMaxMoney = Helios.getMaxMoney();
						savegame.saveWithoutValidity(helicopter);
					}
				}
				else if(e.getKeyChar() == '#')
				{
					savegame.saveWithoutValidity(helicopter);
				}
			}
		}
		else if(IS_CHEATING_MODE_ACTIVATABLE)
		{
			if(e.getKeyChar() == cheatCode.charAt(cheatString.length())){
				cheatString += e.getKeyChar();}
			else{
				cheatString = "";}
			if(cheatString.equals(cheatCode)){
				cheatingMode = true;}
		}		
	}
	
	private static void checkName(Savegame savegame)
	{
		if (!currentPlayerName.equals(savegame.getCurrentPlayerName()))
		{
			if (currentPlayerName.equals(""))
			{
				currentPlayerName = savegame.getCurrentPlayerName();
			} else
			{
				boolean isDefaultPlayerNameSet = currentPlayerName.equals(Window.DEFAULT_PLAYER_NAME);
				Window.buttons.get(StartScreenMenuButtonType.BUTTON_5)
							  .setMarked(isDefaultPlayerNameSet);
				Window.buttons.get(StartScreenButtonType.SETTINGS)
							  .setMarked(isDefaultPlayerNameSet);
				savegame.setCurrentPlayerName(currentPlayerName);
				settingsChanged = true;
			}
		}
	}
	
	static void mousePressed(MouseEvent e,
	                         Controller controller)
	{
		Helicopter helicopter = controller.getHelicopter();
		if(e.getButton() == 1)
		{
			cursor.setLocation(e.getX()- Main.displayShift.width,
							   e.getY()-Main.displayShift.height);
			mousePressedLeft(controller);
		}
		// TODO in Methode auslagern
		else if(	WindowManager.window == GAME
					&&	helicopter.isActive
					&& !helicopter.isDamaged
					&& !Window.isMenuVisible)
		{
			if(e.getButton() == 3)
			{
				helicopter.tryToUseEnergyAbility(controller.powerUps, controller.explosions);
			}
			else{helicopter.turnAround();}
		}	
	}
	
	private static void mousePressedLeft(Controller controller)
	{		
		controller.resetBackgroundRepaintTimer();
		if(WindowManager.window == GAME)
		{
			inGameMousePressedLeft(controller);
		}
		else if(WindowManager.window == REPAIR_SHOP)
		{
			repairShopMousePressedLeft(controller);
		}
		else if(WindowManager.window == START_SCREEN)
		{
			startScreenMousePressedLeft(controller);
		}
		else if(Window.buttons.get(StartScreenSubCancelButtonType.CANCEL)
							  .getBounds()
							  .contains(cursor))
		{
			cancel(controller);
		}
		else 
		{		
			startScreenMenuButtonClicked(controller);
		}
	}	

	private static void inGameMousePressedLeft(Controller controller)
	{
		Helicopter helicopter = controller.getHelicopter();
		Savegame savegame = controller.getSaveGame();
		if(!helicopter.isDamaged)
		{
			if(Window.isMenuVisible)
			{
				if(Window.buttons.get(MainMenuButtonType.NEW_GAME_1).getBounds().contains(cursor))
				{
                    savegame.becomeValid();
				    savegame.saveToFile(helicopter);
					conditionalReset(controller,true);
					restartGame(controller);
					Audio.applause1.stop();
				}
				else if(Window.buttons.get(MainMenuButtonType.STOP_MUSIC).getBounds().contains(cursor))
				{
					Audio.play(Audio.choose);
					switchAudioActivationState(savegame);
				}
				else if(Window.buttons.get(MainMenuButtonType.NEW_GAME_2).getBounds().contains(cursor))
				{
                    savegame.becomeValid();
				    savegame.saveToFile(helicopter);
					controller.shutDown();
				}
				else if( 	(Window.buttons.get(GroundButtonType.MAIN_MENU).getBounds().contains(cursor)
							&& helicopter.isOnTheGround())
						 || (Window.buttons.get(MainMenuButtonType.CANCEL).getBounds().contains(cursor)))
				{
					changeVisibilityOfInGameMenu(helicopter);
				}
			}			
			else if(helicopter.isOnTheGround())
			{
				if(Window.buttons.get(GroundButtonType.REPAIR_SHOP).getBounds().contains(cursor))
				{
					// Betreten der Werkstatt über den Werkstatt-Button
					conditionalReset(controller, false);
					enterRepairShop(helicopter);
				}
				else if(Window.buttons.get(GroundButtonType.MAIN_MENU).getBounds().contains(cursor))
				{
					changeVisibilityOfInGameMenu(helicopter);
				}				
				else if(!helicopter.isActive && cursor.y < 426)
				{
					helicopter.activate();
				}
			}
			else if(helicopter.isActive){helicopter.isContinuousFireEnabled = true;}
		}
		else if(	Window.buttons.get(MainMenuButtonType.NEW_GAME_2).getBounds().contains(cursor)
				 	&& isRestartWindowVisible)
		{
			// Betreten der Werkstatt nach Absturz bzw. Neustart bei Geldmangel				
			conditionalReset(controller, true);
			if(money < repairFee(helicopter, true) || level > 50)
			{
				if(level > 50)
				{
					playingTime = 60000 * helicopter.scoreScreenTimes.getTotalPlayingTime();
				}
				else
				{
					playingTime = playingTime
						     	   + System.currentTimeMillis() 
						           - lastCurrentTime;
					helicopter.scoreScreenTimes.setTotalPlayingTime(playingTime/60000);
				}
							   
				changeWindow(SCORE_SCREEN);
															
				helicopter.isDamaged = false;
                savegame.becomeValid();
				savegame.saveToFile(helicopter);
				Colorations.updateScoreScreenColors(helicopter);
			}
			else{enterRepairShop(helicopter);}
		}
	}
	
	private static void repairShopMousePressedLeft(Controller controller)
	{
		Helicopter helicopter = controller.getHelicopter();
		// Reparatur des Helikopters
		if(Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).getBounds().contains(cursor))
		{
			if(	helicopter.hasMaximumPlating())
			{
				Window.block(HELICOPTER_ALREADY_REPAIRED);
			}
			else if(money < repairFee(helicopter, helicopter.isDamaged))
			{
				Window.block(NOT_ENOUGH_MONEY_FOR_REPAIRS);
			}
			else
			{
				money -= repairFee(helicopter, helicopter.isDamaged);
				timeOfDay = (!helicopter.hasSpotlights || Calculations.tossUp(0.33f)) ? DAY : NIGHT;
				Window.buttons.get(LeftSideRepairShopButtonType.MISSION).setPrimaryLabel(Window.dictionary.mission());
												
				if(!(level == 50 && helicopter.hasAllUpgrades()))
				{
					// TODO diese EntityManagement gehört in eine eigene Klasse
					/* controller.enemies.get(INACTIVE)
									  .addAll(controller.enemies.get(ACTIVE)); */
					controller.enemies.get(ACTIVE)
									  .clear();
					level = level - ((level - 1) % 5);
					LevelManager.adaptToLevel(helicopter, level, false);
					if (level < 6)
					{
						controller.getScenery()
								  .reset();
					}
					killsAfterLevelUp = 0;
					/*controller.enemies.get(INACTIVE)
									  .addAll(controller.enemies.get(DESTROYED));*/
					controller.enemies.get(DESTROYED)
									  .clear();
					Window.buttons.get(LeftSideRepairShopButtonType.REPAIR)
								  .adjustCostsToZero();
					EnemyController.currentRock = null;
				}
				else
				{
					LevelManager.nextBossEnemyType = FINAL_BOSS;
					LevelManager.maxNr = 1;
					LevelManager.maxBarrierNr = 0;
				}
				helicopter.repair();
				Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
				if(level == 50){helicopter.restoreEnergy();}
				helicopter.placeAtStartpos();
			}
		}		
		// Einsatz fliegen
		else if(Window.buttons.get(LeftSideRepairShopButtonType.MISSION).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Window.block(UNREPAIRED_BEFORE_MISSION);}
			else
			{				
				Window.stopButtonHighlighting();
				Window.messageTimer = 0;
				startMission(controller);
			}
		}
				
		/*
		 * Die Spezial-Upgrades
		 */
		
		// Scheinwerfer
		else if(Window.buttons.get(SpecialUpgradeButtonType.SPOTLIGHT).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Window.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasSpotlights){
				Window.block(UPGRADE_ALREADY_INSTALLED);}
			else if(money < helicopter.getSpotlightCosts()){
				Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);}
			else
			{
				Audio.play(Audio.cash);
				money -= helicopter.getSpotlightCosts();
				helicopter.hasSpotlights = true;
				timeOfDay = NIGHT;
				
				Window.updateRepairShopButtonsAfterSpotlightPurchase();
				
				controller.enemies.get(DESTROYED)
								  .forEach(Enemy::repaint);
				
				controller.enemies.get(ACTIVE)
								  .stream()
								  .filter(Predicate.not(Enemy::isRock))
								  .forEach(Enemy::dimmedRepaint);
			}
		}
		// Goliath-Panzerung
		else if(Window.buttons.get(SpecialUpgradeButtonType.GOLIATH_PLATING).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Window.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasGoliathPlating()){
				Window.block(UPGRADE_ALREADY_INSTALLED);}
			else if(money < helicopter.getGoliathCosts())
			{
				Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
			}
			else
			{
				Audio.play(Audio.cash);						
				money -= helicopter.getGoliathCosts();
				helicopter.installGoliathPlating();
				Window.buttons.get(SpecialUpgradeButtonType.GOLIATH_PLATING).adjustCostsToZero();
			}
		}
		// Piercing Warheads
		else if(Window.buttons.get(SpecialUpgradeButtonType.PIERCING_WARHEADS).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Window.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasPiercingWarheads){
				Window.block(UPGRADE_ALREADY_INSTALLED);}
			else if(money < helicopter.getPiercingWarheadsCosts())
			{
				Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
			}
			else
			{
				Audio.play(Audio.cash);
                money -= helicopter.getPiercingWarheadsCosts();
                helicopter.installPiercingWarheads();
				Window.buttons.get(SpecialUpgradeButtonType.PIERCING_WARHEADS).adjustCostsToZero();
			}
		}
		// zusätzliche Bordkanonen
		else if(Window.buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Window.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasAllCannons())
			{
				Window.block(UPGRADE_ALREADY_INSTALLED);
			}
			// TODO if Bedingung auslagern in Methode
			// TODO neue Helicopter Methoden: getNextCannonCost, getMaximumNumberOfCannons, ... je nach Bedarf
			else if(	(money < helicopter.getLastCannonCost()) &&
						!((helicopter.getType() == OROCHI ||(helicopter.getType() == HELIOS && OROCHI.hasDefeatedFinalBoss())) && money >= Helicopter.CHEAP_SPECIAL_COSTS && helicopter.numberOfCannons == 1))
			{
				Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
			}
			else
			{
				Audio.play(Audio.cash);
				Button extraCannonButton = Window.buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS);
				if((helicopter.getType() == OROCHI ||(helicopter.getType() == HELIOS && OROCHI.hasDefeatedFinalBoss())) && helicopter.numberOfCannons == 1)
				{
					money -= Helicopter.CHEAP_SPECIAL_COSTS;
					if(helicopter.getType() == OROCHI)
					{
						extraCannonButton.adjustCostsTo(Helicopter.STANDARD_SPECIAL_COSTS);
						extraCannonButton.setPrimaryLabel(Window.dictionary.thirdCannon());
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
		else if(Window.buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).getBounds().contains(cursor))
		{
			if(helicopter.isDamaged){
				Window.block(UNREPAIRED_BEFORE_UPGRADE);}
			else if(helicopter.hasFifthSpecial()){
				Window.block(UPGRADE_ALREADY_INSTALLED);}
			else if(money < helicopter.getFifthSpecialCosts())
			{
				Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
			}
			else
			{
				Audio.play(Audio.cash);
				money -= helicopter.getFifthSpecialCosts();
				helicopter.obtainFifthSpecial();
				Window.buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).adjustCostsToZero();
			}
		}
			
		/*
		 *  Die Standard-Upgrades
		 */			
		else for(ButtonSpecifier buttonSpecifier : StandardUpgradeButtonType.getValues())
		{		
			StandardUpgradeButtonType buttonType = (StandardUpgradeButtonType) buttonSpecifier;
			StandardUpgradeType standardUpgradeType = buttonType.getStandardUpgradeType();
			if(Window.buttons.get(buttonSpecifier).getBounds().contains(cursor))
			{
				if(helicopter.isDamaged){
					Window.block(UNREPAIRED_BEFORE_UPGRADE);}
				else if(helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType)){
					Window.block(REACHED_MAXIMUM_LEVEL);}
				else if(money < helicopter.getUpgradeCostFor(standardUpgradeType))
				{
					Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE );
				}
				else
				{
					Audio.play(Audio.cash);
					money -= helicopter.getUpgradeCostFor(standardUpgradeType);
					helicopter.upgrade(standardUpgradeType);
					if(helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType))
					{
						Window.buttons.get(buttonSpecifier).adjustCostsToZero();
					}
					else
					{
						Window.buttons.get(buttonSpecifier).adjustCostsTo(helicopter.getUpgradeCostFor(standardUpgradeType));
					}
				}					
				break;
			}
		}		
	}
	
	private static void startScreenMousePressedLeft(Controller controller)
	{
		// TODO eventuell nach Menu auslagern
		if(Window.triangle[0].contains(cursor))
		{
			Window.crossPosition = (Window.crossPosition + 1)% HelicopterType.count();
			Window.cross = Window.getCrossPolygon();
			Window.helicopterSelection = (Window.helicopterSelection + HelicopterType.count() - 1)% HelicopterType.count();
			Audio.play(Audio.choose);
		}
		else if(Window.triangle[1].contains(cursor))
		{
			Window.crossPosition = (Window.crossPosition + HelicopterType.count() - 1)% HelicopterType.count();
			Window.cross = Window.getCrossPolygon();
			Window.helicopterSelection = (Window.helicopterSelection + 1)% HelicopterType.count();
			Audio.play(Audio.choose);
		}
		else if(Window.helicopterFrame[0].contains(cursor)||
				Window.helicopterFrame[1].contains(cursor)||
				Window.helicopterFrame[2].contains(cursor)||
				Window.helicopterFrame[3].contains(cursor))
		{				
			if(allPlayable || nextHelicopterType.isUnlocked())
			{
				startNewGame(nextHelicopterType, controller);
			}
			else
			{
				Window.blockHelicopterSelection(nextHelicopterType);
			}
		}
		else if(Window.buttons.get(StartScreenButtonType.INFORMATION).getBounds().contains(cursor))
		{			
			newStartScreenSubWindow(INFORMATION, true);
			Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(true);
		}
		else if(Window.buttons.get(StartScreenButtonType.HIGH_SCORE).getBounds().contains(cursor))
		{			
			newStartScreenSubWindow(HIGH_SCORE, true);
		}
		else if(Window.buttons.get(StartScreenButtonType.CONTACT).getBounds().contains(cursor))
		{			
			newStartScreenSubWindow(CONTACT, true);
		}
		else if(Window.buttons.get(StartScreenButtonType.SETTINGS).getBounds().contains(cursor))
		{			
			newStartScreenSubWindow(SETTINGS, true);
			if(currentPlayerName.equals(Window.DEFAULT_PLAYER_NAME))
			{
				Window.buttons.get(StartScreenMenuButtonType.BUTTON_5).setMarked(true);
			}
		}
		else if(Window.buttons.get(StartScreenButtonType.RESUME_LAST_GAME).getBounds().contains(cursor))
		{
			if(controller.getSaveGame().isValid())
			{
				Audio.play(Audio.levelUp);
				startSavedGame(controller);
			}
			else{Audio.play(Audio.block);}			
		}		
		else if(Window.buttons.get(StartScreenButtonType.QUIT).getBounds().contains(cursor))
		{					
			controller.shutDown();
		}		
	}
	
	private static void cancel(Controller controller)
	{
		Audio.play(Audio.choose);
		Savegame savegame = controller.getSaveGame();
		if(WindowManager.window == SCORE_SCREEN)
		{				
			savegame.saveInHighscore();
			restartGame(controller);
			savegame.loseValidity();
			savegame.saveToFile(controller.getHelicopter());
			Window.buttons.get(StartScreenSubCancelButtonType.CANCEL).setHighlighted(false);
		}
		else if(WindowManager.window == DESCRIPTION)
		{
			if(Window.page == StartScreenMenuButtonType.BUTTON_6){
				Window.label.setBounds(Main.displayShift.width  + 42,
													   Main.displayShift.height + 83, 940, 240);}
			newStartScreenSubWindow(INFORMATION, false);
			Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(true);
			Window.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(false);
		}
		else if(WindowManager.window == HELICOPTER_TYPES)
		{
			if(Window.page == StartScreenMenuButtonType.BUTTON_2){
				Window.label.setVisible(true);}
			newStartScreenSubWindow(DESCRIPTION, false);
			Window.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(true);
		}
		else
		{
			if(WindowManager.window == INFORMATION)
			{
				Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(false);
			}
			else if(WindowManager.window == SETTINGS)
			{
				Window.buttons.get(StartScreenMenuButtonType.BUTTON_5).setMarked(false);
				checkName(savegame);
				if(settingsChanged)
				{					
					savegame.writeToFile();
					settingsChanged = false;
				}
			}
			Window.label.setVisible(false);
			Window.stopButtonHighlighting();
			WindowManager.window = START_SCREEN;
		}
	}
	
	private static void startScreenMenuButtonClicked(Controller controller)
	{
		// TODO for each schleife über die ButtonTypen
		for(ButtonSpecifier buttonSpecifier : ButtonGroup.START_SCREEN_MENU.getButtonSpecifiers())
		{
			Button currentButton = Window.buttons.get(buttonSpecifier);
			if( currentButton.getBounds().contains(cursor) && 
				currentButton.isVisible() &&
				(Window.page != buttonSpecifier || WindowManager.window == SETTINGS))
			{
				StartScreenMenuButtonType oldPage = Window.page;
				if(WindowManager.window == DESCRIPTION && Window.page == StartScreenMenuButtonType.BUTTON_6)
				{
					Window.label.setBounds(Main.displayShift.width  + 42,
										    Main.displayShift.height + 83, 940, 240);
				}
				else if(WindowManager.window == HELICOPTER_TYPES && Window.page == StartScreenMenuButtonType.BUTTON_2)
				{
					Window.label.setVisible(true);
				}
				Window.page = (StartScreenMenuButtonType) buttonSpecifier;
				if(WindowManager.window == DESCRIPTION && Window.page == StartScreenMenuButtonType.BUTTON_6)
				{
					Window.label.setBounds(Main.displayShift.width  + 92,
											Main.displayShift.height + 83, 890, 240);
				}
				else if(WindowManager.window == HELICOPTER_TYPES && Window.page == StartScreenMenuButtonType.BUTTON_2)
				{
					Window.label.setVisible(false);
				}
				if(WindowManager.window == INFORMATION && Window.page == StartScreenMenuButtonType.BUTTON_3)
				{							
					newStartScreenSubWindow(DESCRIPTION, false);
					Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(false);
					Window.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(true);
				}
				else if(WindowManager.window == DESCRIPTION && Window.page == StartScreenMenuButtonType.BUTTON_7)
				{
					newStartScreenSubWindow(HELICOPTER_TYPES, false);
					Window.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(false);
				}
				else if(WindowManager.window == SETTINGS)
				{					
					settingsMousePressedLeft(controller,
											 currentButton,
											 oldPage);
				}
				else
				{
					Audio.play(Audio.choose);					
					Window.updateStartScreenSubLabelText();
				}
				break;
			}
		}		
	}
	
	private static void settingsMousePressedLeft( Controller controller,
	                                              Button currentButton,
												  StartScreenMenuButtonType oldPage)
	{
		Savegame savegame = controller.getSaveGame();
		if(Window.page == StartScreenMenuButtonType.BUTTON_1)
		{	
			Main.switchDisplayMode(currentButton);
		}
		else if(Window.page == StartScreenMenuButtonType.BUTTON_2)
		{			
			controller.switchAntialiasingActivationState(currentButton);
		}						
		else if(Window.page == StartScreenMenuButtonType.BUTTON_3)
		{
			switchAudioActivationState(savegame);
		}
		else if(Window.page == StartScreenMenuButtonType.BUTTON_4)
		{
			Window.changeLanguage(controller);
		}		
		else if(Window.page == StartScreenMenuButtonType.BUTTON_7)
		{
			Audio.changeBgMusicMode(savegame);
		}
		if(oldPage == StartScreenMenuButtonType.BUTTON_5)
		{
			if(Window.page == StartScreenMenuButtonType.BUTTON_5){
				Window.page = StartScreenMenuButtonType.BUTTON_1;}
			checkName(savegame);
		}
		Window.updateStartScreenSubLabelText();
	}
	
	static void mouseReleased(MouseEvent mouseEvent, Helicopter helicopter)
	{
		if(mouseEvent.getButton() == 1)
		{
			helicopter.isContinuousFireEnabled = false;
		}
		else if(WindowManager.window == GAME && mouseEvent.getButton() == 3 && !helicopter.isDamaged)
		{
			helicopter.rightMouseButtonReleaseAction(mouseEvent);
		}		
	}
	
	// Aktualisierung der Ziel-Koordinaten, auf welche der Helikopter zufliegt
	static void mouseMovedOrDragged(MouseEvent e, Helicopter helicopter)
	{		
		if(!helicopter.isDamaged || WindowManager.window == REPAIR_SHOP)
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

	private static void initializeFromSaveGame(Controller controller)
	{
		restore(controller.getSaveGame());
		for(int i =  highestSavePointLevelBefore(level); i <= level; i++)
		{
			LevelManager.adaptToLevel(controller.getHelicopter(), i, false);
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
		LevelManager.adaptToFirstLevel();
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
		killsAfterLevelUp = savegame.killsAfterLevelUp;
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
	private static void conditionalReset(Controller controller, boolean totalReset)
	{
		Audio.play(Audio.choose);
		
		Helicopter helicopter = controller.getHelicopter();
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
		
		Window.conditionalReset();
		
		// kein "active enemy"-Reset, wenn Boss-Gegner 2 Servants aktiv
		if(!controller.enemies.get(ACTIVE).isEmpty()
		   && !(!totalReset && controller.enemies.get(ACTIVE).getFirst().type == BOSS_2_SERVANT))
		{
			// Boss-Level 4 oder 5: nach Werkstatt-Besuch erscheint wieder der Hauptendgegner
			if(	level == 40 || level == 50)
			{
				LevelManager.nextBossEnemyType = level == 40 ? BOSS_4 : FINAL_BOSS;
				LevelManager.maxNr = 1;
				LevelManager.maxBarrierNr = 0;
			}			
			if(totalReset)
			{
				// controller.enemies.get(INACTIVE).addAll(controller.enemies.get(ACTIVE));
				controller.enemies.get(ACTIVE).clear();
				EnemyController.currentRock = null;
			}
			else
			{
				// controller.enemies.get(INACTIVE).add(e);
				controller.enemies.get(ACTIVE)
								  .removeIf(Enemy::isRemainingAfterEnteringRepairShop);
			}
			EnemyController.currentMiniBoss = null;
		}
		if(totalReset)
		{
			killsAfterLevelUp = 0;
			// controller.enemies.get(INACTIVE).addAll(controller.enemies.get(DESTROYED));
			controller.enemies.get(DESTROYED).clear();
			if(level < 6)
			{
				controller.getScenery().reset();
			}
		}									
		controller.explosions.get(INACTIVE).addAll(controller.explosions.get(ACTIVE));
		controller.explosions.get(ACTIVE).clear();
		controller.missiles.get(INACTIVE).addAll(controller.missiles.get(ACTIVE));
		controller.missiles.get(ACTIVE).clear();
		controller.enemyMissiles.get(INACTIVE).addAll(controller.enemyMissiles.get(ACTIVE));
		controller.enemyMissiles.get(ACTIVE).clear();
		// TODO wieso auskommentiert?
		//controller.powerUps.get(INACTIVE).addAll(controller.powerUps.get(ACTIVE));
		controller.getGameEntityRecycler().storeAll(controller.powerUps.get(ACTIVE));
		controller.powerUps.get(ACTIVE).clear();
		// TODO
		if(Window.collectedPowerUps.containsKey(BOOSTED_FIRE_RATE))
		{
			helicopter.adjustFireRate(false);
		}
		Window.collectedPowerUps.clear();
	}

	static private void startNewGame(HelicopterType helicopterType, Controller controller)
	{
		Audio.play(Audio.applause1);
		Helicopter newHelicopter = HelicopterFactory.createForNewGame(helicopterType);
		controller.setHelicopter(newHelicopter);
		Savegame savegame = controller.getSaveGame();
		savegame.saveInHighscore();
		initializeForNewGame();
		savegame.becomeValid();
		savegame.saveToFile(newHelicopter);
		performGeneralActionsBeforeGameStart();
	}

	static private void startSavedGame(Controller controller)
	{
		Helicopter savedHelicopter = HelicopterFactory.createFromSavegame(controller.getSaveGame());
		Controller.getInstance().setHelicopter(savedHelicopter);
		initializeFromSaveGame(controller);
		performGeneralActionsBeforeGameStart();
		Window.updateRepairShopButtons(savedHelicopter);
	}

	private static void performGeneralActionsBeforeGameStart()
	{
		Audio.play(Audio.choose);
		Window.reset();
		Window.finalizeRepairShopButtons();
	}

	// TODO die sceneryObject-Liste sollte teil innerhalb von Scenery definiert werden
	private static void restartGame(Controller controller)
	{
		changeWindow(START_SCREEN);
		controller.getHelicopter().reset();
		controller.getScenery().reset();
	}

	private static void startMission(Controller controller)
	{		
		changeWindow(GAME);		
		Audio.play(Audio.choose);	
		lastCurrentTime = System.currentTimeMillis();
		Helicopter helicopter = controller.getHelicopter();
		helicopter.prepareForMission();
		controller.getSaveGame().becomeValid();
		controller.getSaveGame().saveToFile(helicopter);
	}
	
	private static void enterRepairShop(Helicopter helicopter)
	{
		changeWindow(REPAIR_SHOP);		
		
		Audio.applause1.stop();
		playingTime += System.currentTimeMillis() - lastCurrentTime;
		Window.repairShopTime = Window.returnTimeDisplayText(playingTime);
		helicopter.setRelativePlatingDisplayColor();
		if(!helicopter.hasMaximumPlating())
	    {
			Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsTo(repairFee(helicopter, helicopter.isDamaged));
	    }
		else
		{
			Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
		}
		Window.clearMessage();
	}

	// TODO Booleschen Parameter entfernen, dafür zwei Methoden totalLossRepairFee  defaultRepairFee (formel in Methode auslagern)
	public static int repairFee(Helicopter helicopter, boolean totalLoss)
	{		
		return (totalLoss
					? TOTAL_LOSS_REPAIR_BASE_FEE 
					: DEFAULT_REPAIR_BASE_FEE) 
				+ 25 * Math.round( 6.5f * helicopter.missingPlating());
	}
	
	private static void changeWindow(WindowType newWindow)
	{		
		WindowManager.window = newWindow;
		Audio.refreshBackgroundMusic();
		Colorations.bg = newWindow == GAME && timeOfDay == DAY ? Colorations.sky: Color.black;
	}

	private static void newStartScreenSubWindow(WindowType newWindow, boolean hasJustEntered)
	{
		if (hasJustEntered)
		{
			Window.stopButtonHighlighting();
		}
		Audio.play(Audio.choose);		
		WindowManager.window = newWindow;
		Window.adaptToNewWindow(hasJustEntered);
		Window.updateStartScreenSubButtons();
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
		Window.levelDisplayTimer.start();
		LevelManager.adaptToLevel(helicopter, level, true);
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
		Window.moneyDisplayTimer = START;
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
		if(!Window.isMenuVisible)
		{
			Window.isMenuVisible = true;
			Scenery.backgroundMoves = false;
			playingTime += System.currentTimeMillis() - lastCurrentTime;
		}
		else
		{
			Window.isMenuVisible = false;
			lastCurrentTime = System.currentTimeMillis();
			if(helicopter.isOnTheGround())
			{
				helicopter.inactivate();
			}
		}
	}

	private static void switchAudioActivationState(Savegame savegame)
	{		
		Audio.isSoundOn = !Audio.isSoundOn;
		savegame.isSoundOn = Audio.isSoundOn;
		settingsChanged = true;
		Audio.refreshBackgroundMusic();
		Window.dictionary.updateAudioActivation();
		Window.buttons.get(MainMenuButtonType.STOP_MUSIC).setPrimaryLabel(Window.dictionary.audioActivation());
		Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setPrimaryLabel(Window.dictionary.audioActivation());
	}
	
	public static void determineHighscoreTimes(Helicopter helicopter)
	{
		BossLevel bossLevel = BossLevel.getCurrentBossLevel();
		long newHighScoreTime = (playingTime + System.currentTimeMillis() - lastCurrentTime)/60000;
		helicopter.scoreScreenTimes.put(bossLevel, newHighScoreTime);
				
		if(helicopter.isCountingAsFairPlayedHelicopter())
		{
			recordTimeManager.saveRecordTime(helicopter.getType(), bossLevel, newHighScoreTime);
			heliosMaxMoney = Helios.getMaxMoney();
		}			
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

	public static int getNonFinalMainBossKillCountOf(HelicopterType helicopterType)
	{
		return BossLevel.getNonFinalBossLevel()
						.stream()
						.filter(helicopterType::hasPassed)
						.map(BossLevel::getBossNr)
						.max(java.util.Comparator.naturalOrder())
						.orElse(0);
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