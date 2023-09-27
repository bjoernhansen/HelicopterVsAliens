package de.helicopter_vs_aliens.control;


import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.entities.GameEntityActivation;
import de.helicopter_vs_aliens.control.events.KeyEvent;
import de.helicopter_vs_aliens.control.events.MouseEvent;
import de.helicopter_vs_aliens.control.events.SpecialKey;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
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
import de.helicopter_vs_aliens.model.enemy.basic.BasicEnemy;
import de.helicopter_vs_aliens.model.enemy.boss.BossEnemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterFactory;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.Helios;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.score.HighScore;
import de.helicopter_vs_aliens.score.RecordTimeManager;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

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
        START = 0,                    // Timer Start
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
        MAX_MONEY = 5540500;            // für Komplettausbau erforderliche Geldmenge

    static final Point
        cursor = new Point();    // die letzten Maus-Koordinaten

    public static int
        level = 1,                // aktuelle Level [1 - 51]
        maxLevel = 1,            // höchstes erreichtes Level
        money = 0,                // Guthaben
        killsAfterLevelUp,        // Anhand dieser Anzahl wird ermittelt, ob ein Level-Up erfolgen muss.
        lastCreationTimer,        // Timer stellt sicher, dass ein zeitlicher Mindestabstand zwischen der Erstellung zweier Gegner liegt
        overallEarnings,        // Gesamtverdienst
        extraBonusCounter,        // Summe aller Extra-Boni (Multi-Kill-Belohnungen, Abschuss von Mini-Bossen und Geld-PowerUps)
        lastBonus,                // für die Guthaben-Anzeige: zuletzt erhaltener Standard-Verdienst
        lastExtraBonus,            // für die Guthaben-Anzeige: zuletzt erhaltener Extra-Bonus
        lastMultiKill,            // für die Multi-Kill-Anzeige: Art des letzten Multi-Kill
        commendationTimer,        // reguliert, wie lange die Multi-Kill-Anzeige zu sehen ist
        heliosMaxMoney;

    public static long
        playingTime,            // bisher vergangene Spielzeit
        lastCurrentTime;        // Zeitpunkt der letzten Aktualisierung von playing_time

    public static boolean
        isRestartWindowVisible,                // = true: Neustart-Fenster wird angezeigt
        settingsChanged = false,
        allPlayable = false;

    public static TimeOfDay
        timeOfDay = DAY;        // Tageszeit [NIGHT, DAY]

    // Variablen zur Nutzung von Cheats und Freischaltung von Helikoptern
    private static boolean
        cheatingMode = IS_CHEATING_MODE_ALWAYS_ACTIVE;                // = true: Cheat-Modus aktiviert

    public static String
        currentPlayerName = Window.DEFAULT_PLAYER_NAME;

    private static String
        cheatString = "";

    private static final String
        cheatCode = "+cheats";            // Code, mit welchem Cheats aktiviert werden können

    public static BossEnemy
        boss;                            // Referenz auf den aktuellen Endgegner

    public static HelicopterType
        nextHelicopterType,                // aktuell im Startmenü ausgewählte Helikopter
        previousHelicopterType;            // zuletzt im Startmenü ausgewählte Helikopter

    public static HighScore
        highScore = new HighScore();

    public static Set<HelicopterType>
        helicoptersThatReachedLevel20 = EnumSet.noneOf(HelicopterType.class);

    public static RecordTimeManager
        recordTimeManager = new RecordTimeManager();


    static void keyTyped(KeyEvent keyEvent, GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        if (keyEvent.isKeyEqualTo(SpecialKey.ESCAPE) && !helicopter.isDamaged)
        {
            if (WindowManager.window == GAME)
            {
                changeVisibilityOfInGameMenu(helicopter);
            }
            else if (WindowManager.window == START_SCREEN)
            {
                System.exit(0);
            }
            else if (WindowManager.window != REPAIR_SHOP)
            {
                cancel(gameRessourceProvider);
            }
        }
        else if (WindowManager.window == SETTINGS && Window.page == StartScreenMenuButtonType.BUTTON_3)
        {
            int nameLength = currentPlayerName.length();
            if (keyEvent.isKeyEqualTo(SpecialKey.ENTER))
            {
                Window.page = StartScreenMenuButtonType.BUTTON_1;
                checkName(gameRessourceProvider.getSaveGame());
            }
            else if (keyEvent.isKeyEqualTo(SpecialKey.BACK_SPACE))
            {
                if (nameLength > 0)
                {
                    currentPlayerName = currentPlayerName.substring(0, nameLength - 1);
                }
            }
            else if (nameLength < 15 && keyEvent.isKeyAllowedForPlayerName())
            {
                currentPlayerName += keyEvent.getKey();
            }
        }
        else if (keyEvent.isKeyEqualTo('f'))
        {
            gameRessourceProvider.switchFpsVisibleState();
        }
        else if (keyEvent.isKeyEqualTo('p'))
        {
            if (WindowManager.window == GAME && !helicopter.isDamaged)
            {
                changeVisibilityOfInGameMenu(helicopter);
            }
        }
        else if (keyEvent.isKeyEqualTo(SpecialKey.SPACE))
        {
            if (WindowManager.window == GAME
                && helicopter.isActive
                && !helicopter.isDamaged
                && !Window.isMenuVisible)
            {
                helicopter.turnAround();
            }
        }
        else if (cheatingMode)
        {
            if (keyEvent.isKeyEqualTo('e'))
            {
                if (WindowManager.window == GAME || WindowManager.window == REPAIR_SHOP)
                {
                    if (money == 0)
                    {
                        if (WindowManager.window == GAME)
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
            else if (keyEvent.isKeyEqualTo('u'))
            {
                if (WindowManager.window == GAME || WindowManager.window == REPAIR_SHOP)
                {
                    if (!helicopter.hasAllUpgrades())
                    {
                        Audio.play(Audio.cash);
                        if (helicopter.hasSomeUpgrades())
                        {
                            helicopter.obtainAllUpgrades();
                        }
                        else
                        {
                            helicopter.obtainSomeUpgrades();
                        }
                    }
                    else
                    {
                        Audio.play(Audio.block);
                    }
                }
            }
            else if (WindowManager.window == GAME)
            {
                if (keyEvent.isKeyEqualTo('l'))
                {
                    if (level < 50)
                    {
                        int numberOfLevelUp = level - (level % 5)
                            + (level % 5 == 0 && !isCurrentLevelBossLevel() ? 0 : 5);
                        numberOfLevelUp = numberOfLevelUp
                            + (numberOfLevelUp % 10 == 0 ? 0 : 1)
                            - level;
                        if (isCurrentLevelBossLevel())
                        {
                            numberOfLevelUp = 1;
                        }
                        playingTime += (numberOfLevelUp + Calculations.random(numberOfLevelUp)) * 60000L;
                        levelUp(gameRessourceProvider, numberOfLevelUp);
                        helicopter.isPlayedWithCheats = true;
                    }
                }
                else if (keyEvent.isKeyEqualTo('+'))
                {
                    if (level < 50)
                    {
                        playingTime += (1 + (Calculations.tossUp(0.4f) ? 1 : 0)) * 60000;
                        levelUp(gameRessourceProvider, 1);
                        helicopter.isPlayedWithCheats = true;
                    }
                }
                else if (keyEvent.isKeyEqualTo('s'))
                {
                    Window.specialInfoSelection = (Window.specialInfoSelection + 1) % NUMBER_OF_DEBUGGING_INFOS;
                }
                // TODO übergabe von powerUps anders regeln
                else if (keyEvent.isKeyEqualTo('d'))
                {
                    helicopter.switchPowerUpActivationState(gameRessourceProvider.getActiveGameEntityManager()
                                                                                 .getPowerUps(), TRIPLE_DAMAGE);
                }
                else if (keyEvent.isKeyEqualTo('i'))
                {
                    helicopter.switchPowerUpActivationState(gameRessourceProvider.getActiveGameEntityManager()
                                                                                 .getPowerUps(), INVINCIBLE);
                }
                else if (keyEvent.isKeyEqualTo('c'))
                {
                    helicopter.switchPowerUpActivationState(gameRessourceProvider.getActiveGameEntityManager()
                                                                                 .getPowerUps(), UNLIMITED_ENERGY);
                }
                else if (keyEvent.isKeyEqualTo('y'))
                {
                    helicopter.switchPowerUpActivationState(gameRessourceProvider.getActiveGameEntityManager()
                                                                                 .getPowerUps(), BOOSTED_FIRE_RATE);
                }
                else if (keyEvent.isKeyEqualTo('a'))
                {
                    if (level < 51)
                    {
                        helicopter.repair();
                        Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).adjustCostsToZero();
                        Window.buttons.get(MainMenuButtonType.NEW_GAME_2).setPrimaryLabel(Window.dictionary.quit());
                        helicopter.restoreEnergy();
                        isRestartWindowVisible = false;
                        helicopter.isPlayedWithCheats = true;
                    }
                }
                else if (keyEvent.isKeyEqualTo('m'))
                {
                    BasicEnemy.changeMiniBossProb();
                    helicopter.isPlayedWithCheats = true;
                }
                else if (keyEvent.isKeyEqualTo('n'))
                {
                    helicopter.destroyPlating();
                    helicopter.crash();
                }
                else if (keyEvent.isKeyEqualTo('t'))
                {
                    playingTime += 60000;
                }
            }
            else if (WindowManager.window == START_SCREEN)
            {
                // Resetten der Helicopter-Bestzeiten
                Savegame savegame = gameRessourceProvider.getSaveGame();
                if (keyEvent.isKeyEqualTo('x'))
                {
                    allPlayable = !allPlayable;
                }
                else if (keyEvent.isKeyEqualTo('-'))
                {
                    if (!recordTimeManager.isEmpty())
                    {
                        recordTimeManager.eraseRecordTimes();
                        helicoptersThatReachedLevel20.clear();
                        heliosMaxMoney = Helios.getMaxMoney();
                        savegame.saveWithoutValidity(gameRessourceProvider);
                    }
                }
                else if (keyEvent.isKeyEqualTo('#'))
                {
                    savegame.saveWithoutValidity(gameRessourceProvider);
                }
            }
        }
        else if (IS_CHEATING_MODE_ACTIVATABLE)
        {
            if (keyEvent.isKeyEqualTo(cheatCode.charAt(cheatString.length())))
            {
                cheatString += keyEvent.getKey();
            }
            else
            {
                cheatString = "";
            }
            if (cheatString.equals(cheatCode))
            {
                cheatingMode = true;
            }
        }
    }

    // TODO der ganze Code im Zusammenhang mit der Namensänderung gehört in eine eigene Klasse
    private static void checkName(Savegame savegame)
    {
        if (!currentPlayerName.equals(savegame.getCurrentPlayerName()))
        {
            if (currentPlayerName.equals(""))
            {
                currentPlayerName = savegame.getCurrentPlayerName();
            }
            else
            {
                boolean isDefaultPlayerNameSet = currentPlayerName.equals(Window.DEFAULT_PLAYER_NAME);
                Window.buttons.get(StartScreenMenuButtonType.BUTTON_3)
                              .setMarked(isDefaultPlayerNameSet);
                Window.buttons.get(StartScreenButtonType.SETTINGS)
                              .setMarked(isDefaultPlayerNameSet);
                savegame.setCurrentPlayerName(currentPlayerName);
                settingsChanged = true;
            }
        }
    }

    public static void mousePressed(MouseEvent mouseEvent, GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        if (mouseEvent.isLeftButtonClicked())
        {
            Dimension displayShift = gameRessourceProvider.getGuiStateProvider().getDisplayShift();
            cursor.setLocation(
                mouseEvent.getX() - displayShift.width,
                mouseEvent.getY() - displayShift.height);
            gameRessourceProvider.getGuiStateProvider().resetBackgroundRepaintTimer();
            mousePressedLeft(gameRessourceProvider);
        }
        // TODO in Methode auslagern
        else if (WindowManager.window == GAME
            && helicopter.isActive
            && !helicopter.isDamaged
            && !Window.isMenuVisible)
        {
            if (mouseEvent.isRightButtonClicked())
            {
                helicopter.tryToUseEnergyAbility(gameRessourceProvider);
            }
            else
            {
                helicopter.turnAround();
            }
        }
    }

    private static void mousePressedLeft(GameRessourceProvider gameRessourceProvider)
    {
        if (WindowManager.window == GAME)
        {
            inGameMousePressedLeft(gameRessourceProvider);
        }
        else if (WindowManager.window == REPAIR_SHOP)
        {
            repairShopMousePressedLeft(gameRessourceProvider);
        }
        else if (WindowManager.window == START_SCREEN)
        {
            startScreenMousePressedLeft(gameRessourceProvider);
        }
        else if (Window.buttons.get(StartScreenSubCancelButtonType.CANCEL)
                               .getBounds()
                               .contains(cursor))
        {
            cancel(gameRessourceProvider);
        }
        else
        {
            startScreenMenuButtonClicked(gameRessourceProvider);
        }
    }

    private static void inGameMousePressedLeft(GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        Savegame savegame = gameRessourceProvider.getSaveGame();
        if (!helicopter.isDamaged)
        {
            if (Window.isMenuVisible)
            {
                if (Window.buttons.get(MainMenuButtonType.NEW_GAME_1).getBounds().contains(cursor))
                {
                    savegame.becomeValid();
                    savegame.saveToFile(gameRessourceProvider);
                    conditionalReset(gameRessourceProvider, true);
                    restartGame(gameRessourceProvider);
                    Audio.applause1.stop();
                }
                else if (Window.buttons.get(MainMenuButtonType.STOP_MUSIC).getBounds().contains(cursor))
                {
                    Audio.play(Audio.choose);
                    switchAudioActivationState(savegame);
                }
                else if (Window.buttons.get(MainMenuButtonType.NEW_GAME_2).getBounds().contains(cursor))
                {
                    savegame.becomeValid();
                    savegame.saveToFile(gameRessourceProvider);
                    System.exit(0);
                }
                else if ((Window.buttons.get(GroundButtonType.MAIN_MENU).getBounds().contains(cursor)
                    && helicopter.isOnTheGround())
                    || (Window.buttons.get(MainMenuButtonType.CANCEL).getBounds().contains(cursor)))
                {
                    changeVisibilityOfInGameMenu(helicopter);
                }
            }
            else if (helicopter.isOnTheGround())
            {
                if (Window.buttons.get(GroundButtonType.REPAIR_SHOP).getBounds().contains(cursor))
                {
                    // Betreten der Werkstatt über den Werkstatt-Button
                    conditionalReset(gameRessourceProvider, false);
                    enterRepairShop(helicopter);
                }
                else if (Window.buttons.get(GroundButtonType.MAIN_MENU).getBounds().contains(cursor))
                {
                    changeVisibilityOfInGameMenu(helicopter);
                }
                else if (!helicopter.isActive && cursor.y < 426)
                {
                    helicopter.activate();
                }
            }
            else if (helicopter.isActive)
            {
                helicopter.isContinuousFireEnabled = true;
            }
        }
        else if (Window.buttons.get(MainMenuButtonType.NEW_GAME_2).getBounds().contains(cursor)
            && isRestartWindowVisible)
        {
            // Betreten der Werkstatt nach Absturz bzw. Neustart bei Geldmangel
            conditionalReset(gameRessourceProvider, true);
            if (money < repairFee(helicopter, true) || level > 50)
            {
                if (level > 50)
                {
                    playingTime = 60000 * helicopter.scoreScreenTimes.getTotalPlayingTime();
                }
                else
                {
                    playingTime = playingTime
                        + System.currentTimeMillis()
                        - lastCurrentTime;
                    helicopter.scoreScreenTimes.setTotalPlayingTime(playingTime / 60000);
                }

                changeWindow(SCORE_SCREEN);

                helicopter.isDamaged = false;
                savegame.becomeValid();
                savegame.saveToFile(gameRessourceProvider);
                Colorations.updateScoreScreenColors(gameRessourceProvider.getGameStatisticsCalculator());
            }
            else
            {
                enterRepairShop(helicopter);
            }
        }
    }

    private static void repairShopMousePressedLeft(GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        // Reparatur des Helikopters
        if (Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).getBounds().contains(cursor))
        {
            if (helicopter.hasMaximumPlating())
            {
                Window.block(HELICOPTER_ALREADY_REPAIRED);
            }
            else if (money < repairFee(helicopter, helicopter.isDamaged))
            {
                Window.block(NOT_ENOUGH_MONEY_FOR_REPAIRS);
            }
            else
            {
                money -= repairFee(helicopter, helicopter.isDamaged);
                timeOfDay = (!helicopter.hasSpotlights || Calculations.tossUp(0.33f)) ? DAY : NIGHT;
                Window.buttons.get(LeftSideRepairShopButtonType.MISSION).setPrimaryLabel(Window.dictionary.mission());

                if (!(level == 50 && helicopter.hasAllUpgrades()))
                {
                    storeAndClearActiveEnemies(gameRessourceProvider);
                    resetLevelAfterRepair();
                    LevelManager.adaptToLevel(helicopter, level, false); // TODO signatur der Methode ändern - zu viele Parameter und ein boolescher
                    if (level < 6)
                    {
                        gameRessourceProvider.getScenery()
                                             .reset();
                    }
                    killsAfterLevelUp = 0;
                    storeAndClearDestroyedEnemies(gameRessourceProvider);
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
                if (level == 50)
                {
                    helicopter.restoreEnergy();
                }
                helicopter.placeAtStartpos();
            }
        }
        // Einsatz fliegen
        else if (Window.buttons.get(LeftSideRepairShopButtonType.MISSION).getBounds().contains(cursor))
        {
            if (helicopter.isDamaged)
            {
                Window.block(UNREPAIRED_BEFORE_MISSION);
            }
            else
            {
                Window.stopButtonHighlighting();
                Window.messageTimer = 0;
                startMission(gameRessourceProvider);
            }
        }

        /*
         * Die Spezial-Upgrades
         */

        // Scheinwerfer
        else if (Window.buttons.get(SpecialUpgradeButtonType.SPOTLIGHT).getBounds().contains(cursor))
        {
            if (helicopter.isDamaged)
            {
                Window.block(UNREPAIRED_BEFORE_UPGRADE);
            }
            else if (helicopter.hasSpotlights)
            {
                Window.block(UPGRADE_ALREADY_INSTALLED);
            }
            else if (money < helicopter.getSpotlightCosts())
            {
                Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
            }
            else
            {
                Audio.play(Audio.cash);
                money -= helicopter.getSpotlightCosts();
                helicopter.hasSpotlights = true;
                timeOfDay = NIGHT;

                Window.updateRepairShopButtonsAfterSpotlightPurchase();

                gameRessourceProvider.getActiveGameEntityManager()
                                     .getEnemies()
                                     .get(CollectionSubgroupType.DESTROYED)
                                     .forEach(Enemy::repaint);

                gameRessourceProvider.getActiveGameEntityManager()
                                     .getEnemies()
                                     .get(CollectionSubgroupType.ACTIVE)
                                     .stream()
                                     .filter(Predicate.not(Enemy::isRock))
                                     .forEach(Enemy::dimmedRepaint);
            }
        }
        // Goliath-Panzerung
        else if (Window.buttons.get(SpecialUpgradeButtonType.GOLIATH_PLATING).getBounds().contains(cursor))
        {
            if (helicopter.isDamaged)
            {
                Window.block(UNREPAIRED_BEFORE_UPGRADE);
            }
            else if (helicopter.hasGoliathPlating())
            {
                Window.block(UPGRADE_ALREADY_INSTALLED);
            }
            else if (money < helicopter.getGoliathCosts())
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
        else if (Window.buttons.get(SpecialUpgradeButtonType.PIERCING_WARHEADS).getBounds().contains(cursor))
        {
            if (helicopter.isDamaged)
            {
                Window.block(UNREPAIRED_BEFORE_UPGRADE);
            }
            else if (helicopter.hasPiercingWarheads)
            {
                Window.block(UPGRADE_ALREADY_INSTALLED);
            }
            else if (money < helicopter.getPiercingWarheadsCosts())
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
        else if (Window.buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS).getBounds().contains(cursor))
        {
            if (helicopter.isDamaged)
            {
                Window.block(UNREPAIRED_BEFORE_UPGRADE);
            }
            else if (helicopter.hasAllCannons())
            {
                Window.block(UPGRADE_ALREADY_INSTALLED);
            }
            // TODO if Bedingung auslagern in Methode
            // TODO neue Helicopter Methoden: getNextCannonCost, getMaximumNumberOfCannons, ... je nach Bedarf
            else if ((money < helicopter.getLastCannonCost()) &&
                !((helicopter.getType() == OROCHI || (helicopter.getType() == HELIOS && OROCHI.hasDefeatedFinalBoss())) && money >= Helicopter.CHEAP_SPECIAL_COSTS && helicopter.numberOfCannons == 1))
            {
                Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
            }
            else
            {
                Audio.play(Audio.cash);
                Button extraCannonButton = Window.buttons.get(SpecialUpgradeButtonType.EXTRA_CANNONS);
                if ((helicopter.getType() == OROCHI || (helicopter.getType() == HELIOS && OROCHI.hasDefeatedFinalBoss())) && helicopter.numberOfCannons == 1)
                {
                    money -= Helicopter.CHEAP_SPECIAL_COSTS;
                    if (helicopter.getType() == OROCHI)
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
        else if (Window.buttons.get(SpecialUpgradeButtonType.FIFTH_SPECIAL).getBounds().contains(cursor))
        {
            if (helicopter.isDamaged)
            {
                Window.block(UNREPAIRED_BEFORE_UPGRADE);
            }
            else if (helicopter.hasFifthSpecial())
            {
                Window.block(UPGRADE_ALREADY_INSTALLED);
            }
            else if (money < helicopter.getFifthSpecialCosts())
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
        else for (ButtonSpecifier buttonSpecifier : StandardUpgradeButtonType.getValues())
            {
                StandardUpgradeButtonType buttonType = (StandardUpgradeButtonType) buttonSpecifier;
                StandardUpgradeType standardUpgradeType = buttonType.getStandardUpgradeType();
                if (Window.buttons.get(buttonSpecifier).getBounds().contains(cursor))
                {
                    if (helicopter.isDamaged)
                    {
                        Window.block(UNREPAIRED_BEFORE_UPGRADE);
                    }
                    else if (helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType))
                    {
                        Window.block(REACHED_MAXIMUM_LEVEL);
                    }
                    else if (money < helicopter.getUpgradeCostFor(standardUpgradeType))
                    {
                        Window.block(NOT_ENOUGH_MONEY_FOR_UPGRADE);
                    }
                    else
                    {
                        Audio.play(Audio.cash);
                        money -= helicopter.getUpgradeCostFor(standardUpgradeType);
                        helicopter.upgrade(standardUpgradeType);
                        if (helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType))
                        {
                            Window.buttons.get(buttonSpecifier).adjustCostsToZero();
                        }
                        else
                        {
                            Window.buttons.get(buttonSpecifier)
                                          .adjustCostsTo(helicopter.getUpgradeCostFor(standardUpgradeType));
                        }
                    }
                    break;
                }
            }
    }

    private static void storeAndClearActiveEnemies(GameRessourceProvider gameRessourceProvider)
    {
        storeAndClearEnemies(gameRessourceProvider, CollectionSubgroupType.ACTIVE);
    }

    private static void storeAndClearDestroyedEnemies(GameRessourceProvider gameRessourceProvider)
    {
        storeAndClearEnemies(gameRessourceProvider, CollectionSubgroupType.DESTROYED);
    }

    private static void storeAndClearEnemies(GameRessourceProvider gameRessourceProvider, CollectionSubgroupType collectionSubgroupType)
    {
        Queue<Enemy> enemies = gameRessourceProvider.getActiveGameEntityManager()
                                                    .getEnemies()
                                                    .get(collectionSubgroupType);
        gameRessourceProvider.getGameEntitySupplier()
                             .storeAll(enemies);
        enemies.clear();
    }

    private static void resetLevelAfterRepair()
    {
        level = level - ((level - 1) % 5);
    }

    private static void startScreenMousePressedLeft(GameRessourceProvider gameRessourceProvider)
    {
        // TODO eventuell nach Menu auslagern
        if (Window.triangles[0].contains(cursor))
        {
            Window.crossPosition = (Window.crossPosition + 1) % HelicopterType.count();
            Window.cross = Window.getCrossPolygon();
            Window.helicopterSelection = (Window.helicopterSelection + HelicopterType.count() - 1) % HelicopterType.count();
            Audio.play(Audio.choose);
        }
        else if (Window.triangles[1].contains(cursor))
        {
            Window.crossPosition = (Window.crossPosition + HelicopterType.count() - 1) % HelicopterType.count();
            Window.cross = Window.getCrossPolygon();
            Window.helicopterSelection = (Window.helicopterSelection + 1) % HelicopterType.count();
            Audio.play(Audio.choose);
        }
        else if (Window.helicopterFrame[0].contains(cursor) ||
            Window.helicopterFrame[1].contains(cursor) ||
            Window.helicopterFrame[2].contains(cursor) ||
            Window.helicopterFrame[3].contains(cursor))
        {
            if (allPlayable || nextHelicopterType.isUnlocked())
            {
                startNewGame(nextHelicopterType, gameRessourceProvider);
            }
            else
            {
                Window.blockHelicopterSelection(nextHelicopterType);
            }
        }
        else if (Window.buttons.get(StartScreenButtonType.INFORMATION).getBounds().contains(cursor))
        {
            newStartScreenSubWindow(INFORMATION, true);
            Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(true);
        }
        else if (Window.buttons.get(StartScreenButtonType.HIGH_SCORE).getBounds().contains(cursor))
        {
            newStartScreenSubWindow(HIGH_SCORE, true);
        }
        else if (Window.buttons.get(StartScreenButtonType.CONTACT).getBounds().contains(cursor))
        {
            newStartScreenSubWindow(CONTACT, true);
        }
        else if (Window.buttons.get(StartScreenButtonType.SETTINGS).getBounds().contains(cursor))
        {
            newStartScreenSubWindow(SETTINGS, true);
            if (currentPlayerName.equals(Window.DEFAULT_PLAYER_NAME))
            {
                Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(true);
            }
        }
        else if (Window.buttons.get(StartScreenButtonType.RESUME_LAST_GAME).getBounds().contains(cursor))
        {
            if (gameRessourceProvider.getSaveGame().isValid())
            {
                Audio.play(Audio.levelUp);
                startSavedGame(gameRessourceProvider);
            }
            else
            {
                Audio.play(Audio.block);
            }
        }
        else if (Window.buttons.get(StartScreenButtonType.QUIT).getBounds().contains(cursor))
        {
            System.exit(0);
        }
    }

    private static void cancel(GameRessourceProvider gameRessourceProvider)
    {
        Audio.play(Audio.choose);
        Savegame savegame = gameRessourceProvider.getSaveGame();
        if (WindowManager.window == SCORE_SCREEN)
        {
            savegame.saveInHighscore();
            restartGame(gameRessourceProvider);
            savegame.loseValidity();
            savegame.saveToFile(gameRessourceProvider);
            Window.buttons.get(StartScreenSubCancelButtonType.CANCEL).setHighlighted(false);
        }
        else if (WindowManager.window == DESCRIPTION)
        {
            if (Window.page == StartScreenMenuButtonType.BUTTON_6)
            {
                Dimension displayShift = gameRessourceProvider.getGuiStateProvider().getDisplayShift();
                Window.label.setBounds( displayShift.width + 42,
                                        displayShift.height + 83, 940, 240);
            }
            newStartScreenSubWindow(INFORMATION, false);
            Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(true);
            Window.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(false);
        }
        else if (WindowManager.window == HELICOPTER_TYPES)
        {
            if (Window.page == StartScreenMenuButtonType.BUTTON_2)
            {
                Window.label.setVisible(true);
            }
            newStartScreenSubWindow(DESCRIPTION, false);
            Window.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(true);
        }
        else
        {
            if (WindowManager.window == INFORMATION)
            {
                Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(false);
            }
            else if (WindowManager.window == SETTINGS)
            {
                Window.buttons.get(StartScreenMenuButtonType.BUTTON_5).setMarked(false);
                checkName(savegame);
                if (settingsChanged)
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

    private static void startScreenMenuButtonClicked(GameRessourceProvider gameRessourceProvider)
    {
        // TODO for each schleife über die ButtonTypen
        for (ButtonSpecifier buttonSpecifier : ButtonGroup.START_SCREEN_MENU.getButtonSpecifiers())
        {
            Button currentButton = Window.buttons.get(buttonSpecifier);
            if (currentButton.getBounds().contains(cursor) &&
                currentButton.isVisible() &&
                (Window.page != buttonSpecifier || WindowManager.window == SETTINGS))
            {
                StartScreenMenuButtonType oldPage = Window.page;
                Dimension displayShift = gameRessourceProvider.getGuiStateProvider().getDisplayShift();
                if (WindowManager.window == DESCRIPTION && Window.page == StartScreenMenuButtonType.BUTTON_6)
                {
                    Window.label.setBounds(
                        displayShift.width + 42,
                        displayShift.height + 83, 940, 240);
                }
                else if (WindowManager.window == HELICOPTER_TYPES && Window.page == StartScreenMenuButtonType.BUTTON_2)
                {
                    Window.label.setVisible(true);
                }
                Window.page = (StartScreenMenuButtonType) buttonSpecifier;
                if (WindowManager.window == DESCRIPTION && Window.page == StartScreenMenuButtonType.BUTTON_6)
                {
                    Window.label.setBounds(
                        displayShift.width + 92,
                        displayShift.height + 83, 890, 240);
                }
                else if (WindowManager.window == HELICOPTER_TYPES && Window.page == StartScreenMenuButtonType.BUTTON_2)
                {
                    Window.label.setVisible(false);
                }
                if (WindowManager.window == INFORMATION && Window.page == StartScreenMenuButtonType.BUTTON_3)
                {
                    newStartScreenSubWindow(DESCRIPTION, false);
                    Window.buttons.get(StartScreenMenuButtonType.BUTTON_3).setMarked(false);
                    Window.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(true);
                }
                else if (WindowManager.window == DESCRIPTION && Window.page == StartScreenMenuButtonType.BUTTON_7)
                {
                    newStartScreenSubWindow(HELICOPTER_TYPES, false);
                    Window.buttons.get(StartScreenMenuButtonType.BUTTON_7).setMarked(false);
                }
                else if (WindowManager.window == SETTINGS)
                {
                    settingsMousePressedLeft(gameRessourceProvider, oldPage);
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

    private static void settingsMousePressedLeft(GameRessourceProvider gameRessourceProvider,
                                                 StartScreenMenuButtonType oldPage)
    {
        Savegame savegame = gameRessourceProvider.getSaveGame();
        if (Window.page == StartScreenMenuButtonType.BUTTON_1)
        {
            switchAudioActivationState(savegame);
        }
        else if (Window.page == StartScreenMenuButtonType.BUTTON_2)
        {
            Window.changeLanguage(gameRessourceProvider);
        }
        else if (Window.page == StartScreenMenuButtonType.BUTTON_4)
        {
            Audio.changeBgMusicMode(savegame);
        }
        if (oldPage == StartScreenMenuButtonType.BUTTON_3)
        {
            if (Window.page == StartScreenMenuButtonType.BUTTON_3)
            {
                Window.page = StartScreenMenuButtonType.BUTTON_1;
            }
            checkName(savegame);
        }
        Window.updateStartScreenSubLabelText();
    }

    public static void mouseReleased(MouseEvent mouseEvent, Helicopter helicopter)
    {
        if (mouseEvent.isLeftButtonClicked())
        {
            helicopter.isContinuousFireEnabled = false;
        }
        else if (WindowManager.window == GAME && mouseEvent.isRightButtonClicked() && !helicopter.isDamaged)
        {
            helicopter.rightMouseButtonReleaseAction(mouseEvent);
        }
    }

    // Aktualisierung der Ziel-Koordinaten, auf welche der Helikopter zu fliegt
    public static void mouseMovedOrDragged(MouseEvent mouseEvent, GameRessourceProvider gameRessourceProvider)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        if (!helicopter.isDamaged || WindowManager.window == REPAIR_SHOP)
        {
            if (!helicopter.isSearchingForTeleportDestination)
            {
                Dimension displayShift = gameRessourceProvider.getGuiStateProvider().getDisplayShift();
                helicopter.destination.setLocation(
                    mouseEvent.getX() - displayShift.width,
                    mouseEvent.getY() - displayShift.height);
            }
            else
            {
                helicopter.destination.setLocation(helicopter.priorTeleportLocation);
            }
        }
    }

    private static void initializeFromSaveGame(GameRessourceProvider gameRessourceProvider)
    {
        restore(gameRessourceProvider.getSaveGame());
        for (int i = highestSavePointLevelBefore(level); i <= level; i++)
        {
            LevelManager.adaptToLevel(gameRessourceProvider.getHelicopter(), i, false);
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
    private static void conditionalReset(GameRessourceProvider gameRessourceProvider, boolean totalReset)
    {
        Audio.play(Audio.choose);
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        helicopter.partialReset();
        helicopter.bonusKillsTimer = 1; // TODO ist das nur für Kamaitachi wichtig oder warum?
        if (totalReset)
        {
            helicopter.placeAtStartpos();
        }
        resetEvents();
        Window.conditionalReset();

        // kein "active enemy"-Reset, wenn Boss-Gegner 2 Servants aktiv
        if (!gameRessourceProvider.getActiveGameEntityManager()
                                  .getEnemies().get(CollectionSubgroupType.ACTIVE).isEmpty()
            && !(!totalReset && gameRessourceProvider.getActiveGameEntityManager()
                                                     .getEnemies()
                                                     .get(CollectionSubgroupType.ACTIVE)
                                                     .element()
                                                     .getType() == BOSS_2_SERVANT))
        {
            // Boss-Level 4 oder 5: nach Werkstatt-Besuch erscheint wieder der Hauptendgegner
            if (level == 40 || level == 50)
            {
                LevelManager.nextBossEnemyType = level == 40 ? BOSS_4 : FINAL_BOSS;
                LevelManager.maxNr = 1;
                LevelManager.maxBarrierNr = 0;
            }
            if (totalReset)
            {
                storeAndClearActiveEnemies(gameRessourceProvider);
                EnemyController.currentRock = null;
            }
            else
            {
                storeAndClearDisappearingEnemies(gameRessourceProvider);
            }
            EnemyController.currentMiniBoss = null;
        }
        if (totalReset)
        {
            killsAfterLevelUp = 0;
            storeAndClearDestroyedEnemies(gameRessourceProvider);
            if (level < 6)
            {
                gameRessourceProvider.getScenery().reset();
            }
        }
        // TODO vereinfachen und in die neuen Klassen (ActiveGameEntityManager und GameEntitySupplier
        // gameRessourceProvider.getActiveGameEntityManager().clearExplosions();
        gameRessourceProvider.getActiveGameEntityManager()
                             .getExplosions()
                             .get(CollectionSubgroupType.INACTIVE)
                             .addAll(gameRessourceProvider.getActiveGameEntityManager()
                                                          .getExplosions().get(CollectionSubgroupType.ACTIVE));
        gameRessourceProvider.getActiveGameEntityManager()
                             .getExplosions().get(CollectionSubgroupType.ACTIVE).clear();
        gameRessourceProvider.getActiveGameEntityManager()
                             .getMissiles()
                             .get(CollectionSubgroupType.INACTIVE)
                             .addAll(gameRessourceProvider.getActiveGameEntityManager()
                                                          .getMissiles().get(CollectionSubgroupType.ACTIVE));
        gameRessourceProvider.getActiveGameEntityManager()
                             .getMissiles().get(CollectionSubgroupType.ACTIVE).clear();
        gameRessourceProvider.getActiveGameEntityManager()
                             .getEnemyMissiles()
                             .get(CollectionSubgroupType.INACTIVE)
                             .addAll(gameRessourceProvider.getActiveGameEntityManager()
                                                          .getEnemyMissiles().get(CollectionSubgroupType.ACTIVE));
        gameRessourceProvider.getActiveGameEntityManager()
                             .getEnemyMissiles().get(CollectionSubgroupType.ACTIVE).clear();
        gameRessourceProvider.getGameEntitySupplier()
                             .storeAll(gameRessourceProvider.getActiveGameEntityManager()
                                                            .getPowerUps().get(CollectionSubgroupType.ACTIVE));
        gameRessourceProvider.getActiveGameEntityManager()
                             .getPowerUps().get(CollectionSubgroupType.ACTIVE).clear();

        if (Window.collectedPowerUps.containsKey(BOOSTED_FIRE_RATE))
        {
            helicopter.adjustFireRate(false);
        }
        Window.collectedPowerUps.clear();
    }

    private static void storeAndClearDisappearingEnemies(GameRessourceProvider gameRessourceProvider)
    {
        Queue<Enemy> activeEnemies = gameRessourceProvider.getActiveGameEntityManager()
                                                          .getEnemies()
                                                          .get(CollectionSubgroupType.ACTIVE);
        activeEnemies.stream()
                     .filter(Enemy::isDisappearingAfterEnteringRepairShop)
                     .forEach(gameRessourceProvider.getGameEntitySupplier()::store);

        gameRessourceProvider.getActiveGameEntityManager()
                             .getEnemies()
                             .get(CollectionSubgroupType.ACTIVE)
                             .removeIf(Enemy::isDisappearingAfterEnteringRepairShop);
    }

    private static void resetEvents()
    {
        boss = null;
        lastExtraBonus = 0;
        lastMultiKill = 0;
        commendationTimer = 0;
        isRestartWindowVisible = false;
        lastBonus = 0;
    }

    private static void startNewGame(HelicopterType nextHelicopterType, GameRessourceProvider gameRessourceProvider)
    {
        Audio.play(Audio.applause1);
        Helicopter newHelicopter = HelicopterFactory.createForNewGame(nextHelicopterType);
        gameRessourceProvider.setHelicopter(newHelicopter);
        Savegame savegame = gameRessourceProvider.getSaveGame();
        savegame.saveInHighscore();
        initializeForNewGame();
        savegame.becomeValid();
        savegame.saveToFile(gameRessourceProvider);
        performGeneralActionsBeforeGameStart();
    }

    private static void startSavedGame(GameRessourceProvider gameRessourceProvider)
    {
        Helicopter savedHelicopter = HelicopterFactory.createFromSavegame(gameRessourceProvider.getSaveGame());
        gameRessourceProvider.setHelicopter(savedHelicopter);
        initializeFromSaveGame(gameRessourceProvider);
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
    private static void restartGame(GameRessourceProvider gameRessourceProvider)
    {
        changeWindow(START_SCREEN);
        gameRessourceProvider.getHelicopter().reset();
        gameRessourceProvider.getScenery().reset();
    }

    private static void startMission(GameRessourceProvider gameRessourceProvider)
    {
        changeWindow(GAME);
        Audio.play(Audio.choose);
        lastCurrentTime = System.currentTimeMillis();
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        helicopter.prepareForMission();
        gameRessourceProvider.getSaveGame().becomeValid();
        gameRessourceProvider.getSaveGame().saveToFile(gameRessourceProvider);
    }

    private static void enterRepairShop(Helicopter helicopter)
    {
        changeWindow(REPAIR_SHOP);

        Audio.applause1.stop();
        playingTime += System.currentTimeMillis() - lastCurrentTime;
        Window.repairShopTime = Window.returnTimeDisplayText(playingTime);
        helicopter.setRelativePlatingDisplayColor();
        if (!helicopter.hasMaximumPlating())
        {
            Window.buttons.get(LeftSideRepairShopButtonType.REPAIR)
                          .adjustCostsTo(repairFee(helicopter, helicopter.isDamaged));
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
            + 25 * Math.round(6.5f * helicopter.missingPlating());
    }

    private static void changeWindow(WindowType newWindow)
    {
        WindowManager.window = newWindow;
        Audio.refreshBackgroundMusic();
        Colorations.bg = newWindow == GAME && timeOfDay == DAY ? Colorations.sky : Color.black;
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
    public static void checkForLevelUp(GameRessourceProvider gameRessourceProvider)
    {
        if (killsAfterLevelUp >= numberOfKillsNecessaryForNextLevelUp() && level < 50)
        {
            levelUp(gameRessourceProvider, 1);
        }
    }

    public static int numberOfKillsNecessaryForNextLevelUp()
    {
        return 5 - 5 * (int) ((float) (level - 1) / 10) + level;
    }

    // erhöht das Spiel-Level auf "nr_of_levelUp" mit allen Konsequenzen
    private static void levelUp(GameRessourceProvider gameRessourceProvider,
                                int numberOfLevelUp)
    {
        Helicopter helicopter = gameRessourceProvider.getHelicopter();
        Audio.play(level + numberOfLevelUp <= 50
            ? Audio.levelUp
            : Audio.applause1);

        killsAfterLevelUp = 0;
        int previousLevel = level;
        level += numberOfLevelUp;
        helicopter.levelUpEffect(previousLevel);
        maxLevel = Math.max(level, maxLevel);

        if (isCurrentLevelBossLevel())
        {
            Enemy.getRidOfSomeEnemies(gameRessourceProvider);
        }

        if (isCurrentLevelBossLevel() || isBossLevel(previousLevel) || level == 49)
        {
            Audio.refreshBackgroundMusic();
            if (previousLevel % 10 == 0)
            {
                Audio.play(Audio.applause1);
            }
        }
        Window.levelDisplayTimer.start();
        LevelManager.adaptToLevel(helicopter, level, true);
    }

    // Stellt sicher, dass mit dem Besiegen des End-Gegners direkt das nächste Level erreicht wird
    public static void setBossLevelUpConditions()
    {
        if (level == 10)
        {
            killsAfterLevelUp = 14;
        }
        else if (level == 20)
        {
            killsAfterLevelUp = 7;
        }
        else if (level == 30)
        {
            killsAfterLevelUp = 24;
        }
        else if (level == 40)
        {
            killsAfterLevelUp = 29;
        }
        else if (level == 50)
        {
            killsAfterLevelUp = 34;
        }
    }

    // Bonus-Verdienst bei Multi-Kill
    public static void extraReward(int kills, int earnedMoney, float basis,
                                   float increase, float limit)
    {
        Window.moneyDisplayTimer = START;
        if (kills > 1)
        {
            lastBonus = earnedMoney;
        }
        lastExtraBonus = (int) (Math.min(basis + increase * (kills - 2), limit) * earnedMoney);
        lastExtraBonus = Math.round(lastExtraBonus / 10f) * 10;
        money += lastExtraBonus;
        overallEarnings += lastExtraBonus;
        extraBonusCounter += lastExtraBonus;
        lastMultiKill = kills;
        commendationTimer = 90 + (Math.max(kills, 6) - 2) * 25;
        Audio.praise(kills);
    }

    static private void changeVisibilityOfInGameMenu(Helicopter helicopter)
    {
        Audio.play(Audio.choose);
        if (!Window.isMenuVisible)
        {
            Window.isMenuVisible = true;
            Scenery.backgroundMoves = false;
            playingTime += System.currentTimeMillis() - lastCurrentTime;
        }
        else
        {
            Window.isMenuVisible = false;
            lastCurrentTime = System.currentTimeMillis();
            if (helicopter.isOnTheGround())
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
        Window.buttons.get(StartScreenMenuButtonType.BUTTON_1).setPrimaryLabel(Window.dictionary.audioActivation());
    }

    public static void determineHighscoreTimes(Helicopter helicopter)
    {
        BossLevel bossLevel = BossLevel.getCurrentBossLevel();
        long newHighScoreTime = (playingTime + System.currentTimeMillis() - lastCurrentTime) / 60000;
        helicopter.scoreScreenTimes.put(bossLevel, newHighScoreTime);

        if (helicopter.isCountingAsFairPlayedHelicopter())
        {
            recordTimeManager.saveRecordTime(helicopter.getType(), bossLevel, newHighScoreTime);
            heliosMaxMoney = Helios.getMaxMoney();
        }
    }

    public static boolean isCurrentLevelBossLevel()
    {
        return isBossLevel(level);
    }

    public static boolean isBossLevel(int game_level)
    {
        return game_level % 10 == 0;
    }

    public static int bonusIncomePercentage()
    {
        return Calculations.percentage(extraBonusCounter, overallEarnings);
    }

    public static void updateTimer()
    {
        if (commendationTimer > 0)
        {
            commendationTimer--;
        }
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

    public static void updateFinance(Enemy enemy)
    {
        lastBonus = enemy.calculateReward();
        money += lastBonus;
        overallEarnings += lastBonus;
        lastExtraBonus = 0;
    }

    public static boolean hasEnoughTimePassedSinceLastCreation()
    {
        if (isCurrentLevelBossLevel())
        {
            return lastCreationTimer > 135;
        }
        return lastCreationTimer > 20;
    }

    public static boolean wereRandomRequirementsMet(int numberOfMissingEnemies)
    {
        if (isCurrentLevelBossLevel())
        {
            return GameEntityActivation.isQuicklyApproved();
        }
        return GameEntityActivation.isApproved(numberOfMissingEnemies);
    }

    public static boolean wasMaximumLevelExceeded()
    {
        return level > MAXIMUM_LEVEL;
    }

    // TODO hier wird wohl eine neue Klasse herausgelöst werden können

    public static String getMoneyWithCurrency()
    {
        return formatWithCurrency(money);
    }

    public static String getLastBonusWithCurrency()
    {
        return formatWithCurrency(lastBonus);
    }

    public static String getLastExtraBonusWithCurrency()
    {
        return formatWithCurrency(lastExtraBonus);
    }

    public static String getLastOverallEarningsWithCurrency()
    {
        return formatWithCurrency(overallEarnings);
    }

    private static String formatWithCurrency(int price)
    {
        return price + " " + Window.dictionary.currencySymbol();
    }
}