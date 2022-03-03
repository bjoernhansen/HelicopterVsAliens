package de.helicopter_vs_aliens.graphics.painter.menu;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.PowerUpPainter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.gui.menu.Menu;
import de.helicopter_vs_aliens.gui.MultiKillType;
import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.gui.button.GroundButtonType;
import de.helicopter_vs_aliens.gui.button.LeftSideRepairShopButtonType;
import de.helicopter_vs_aliens.gui.button.MainMenuButtonType;
import de.helicopter_vs_aliens.gui.button.SpecialUpgradeButtonType;
import de.helicopter_vs_aliens.gui.button.StandardUpgradeButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenSubButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenSubCancelButtonType;
import de.helicopter_vs_aliens.gui.menu.MenuManager;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.score.HighScoreEntry;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;
import java.util.ArrayList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.*;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.control.Events.MAXIMUM_LEVEL;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.menu.Menu.fontProvider;
import static de.helicopter_vs_aliens.gui.WindowType.*;
import static de.helicopter_vs_aliens.gui.WindowType.SCORE_SCREEN;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PEGASUS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PHOENIX;
import static de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType.EXTRA_CANNONS;
import static de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType.FIFTH_SPECIAL;
import static de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType.GOLIATH_PLATING;
import static de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType.PIERCING_WARHEADS;
import static de.helicopter_vs_aliens.model.helicopter.SpecialUpgradeType.SPOTLIGHT;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.FIREPOWER;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.FIRE_RATE;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.MISSILE_DRIVE;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ROTOR_SYSTEM;
import static de.helicopter_vs_aliens.util.dictionary.Language.ENGLISH;

public abstract class MenuPainter extends Painter<Menu>
{
    private static final String
        VERSION =   "Version 1.3.4",			// Spielversion
        GAME_NAME = "Helicopter vs. Aliens",
        DEVELOPERS_NAME = "Björn Hansen";
    
    private static final int
        SCORE_SCREEN_SPACE_BETWEEN_ROWS = 30,
        SCORE_SCREEN_X_POS_1 = 351,
        SCORE_SCREEN_X_POS_2 = 633,
        SCORE_SCREEN_Y_POS = 129,
        
        SETTING_LEFT = 80,
        SETTING_COLUMN_SPACING = 145,
        SETTING_LINE_SPACING = 40,
        SETTING_TOP = 130, 
        
        STATUS_BAR_X1 = 268,                   			// x-Postion der Schrift in der Statusanzeige (Werkstatt-Menü,erste Spalte)
        STATUS_BAR_X2 = 421,                   			// x-Postion der Schrift in der Statusanzeige (Werkstatt-Menü,zweite Spalte)
        STANDARD_UPGRADE_OFFSET_Y = 148,                // y-Verschiebung der Standard-Upgrades in der Statusanzeige (Werkstatt-Menü)
        SPECIAL_UPGRADE_OFFSET_Y = 328,                 // y-Verschiebung der Spezial-Upgrades in der Statusanzeige (Werkstatt-Menü)
        HEALTH_BAR_LENGTH = 150,               			// Länge des Hitpoint-Balken des Helikopters
        
        CROSS_MAX_DISPLAY_TIME = 60,           			// Maximale Anzeigezeit des Block-Kreuzes (Startscreen)
        UNLOCKED_DISPLAY_TIME = 300,
        ENEMY_HEALTH_BAR_WIDTH = 206;
    
    public static final Point
        HELICOPTER_START_SCREEN_OFFSET = new Point(66, 262),
        HEALTH_BAR_POSITION = new Point(5, RectangularGameEntity.GROUND_Y + 5);
    
    private static Controller controller;
    private static Helicopter helicopter;
    
    private static void updateDependencies(){
        controller = Controller.getInstance();
        helicopter = controller.getHelicopter();
    }
    
    @Override
    public void paint(Graphics2D g2d, Menu menu)
    {
        updateDependencies();
        if(MenuManager.window  == GAME)
        {
            paintForegroundDisplays(g2d, controller, helicopter, controller.showFps);
            paintGui(g2d, helicopter);
        }
        else if(MenuManager.window  == REPAIR_SHOP)
        {
            paintRepairShop(g2d, helicopter);
        }
        else if(MenuManager.window  == START_SCREEN)
        {
            paintStartscreen(g2d, helicopter);
        }
        else if(MenuManager.window  == SCORE_SCREEN)
        {
            paintScoreScreen(g2d, helicopter);
        }
        else
        {
            paintStartScreenSub(g2d, controller.framesCounter);
        }
    }
    
    public static void
    paintForegroundDisplays(Graphics2D g2d,
                            Controller controller,
                            Helicopter helicopter,
                            boolean showFps)
    {
        if(showBossHealthBar()){paintBossHealthBar(g2d);}
        paintHealthBar(g2d, helicopter);
        paintCollectedPowerUps(g2d);
        if(showFps){paintFpsDisplay(g2d);}
        
        if(helicopter.isOnTheGround())
        {
            if(!Menu.isMenuVisible && controller.mouseInWindow)
            {
                paintTimeDisplay(g2d, Events.playingTime
                    + System.currentTimeMillis()
                    - Events.lastCurrentTime);
            }
            else{paintTimeDisplay(g2d, Events.playingTime);}
        }
        if(Menu.unlockedTimer > 0)
        {
            paintHelicopterDisplay(g2d, Menu.helicopterDummies.get(Menu.unlockedType),
                unlockedDisplayPosition(Menu.unlockedTimer),
                -50);
        }
    }
    
    private static boolean showBossHealthBar()
    {
        return Enemy.currentMiniBoss != null || (Events.boss != null && Events.level < 51);
    }
    
    private static void paintBossHealthBar(Graphics2D g2d)
    {
        if(Enemy.currentMiniBoss != null)
        {
            paintBossHealthBar(g2d, Enemy.currentMiniBoss);
        }
        else
        {
            paintBossHealthBar(g2d, Events.boss);
        }
    }
    
    private static void paintBossHealthBar(Graphics2D g2d, Enemy boss)
    {
        g2d.setColor(Colorations.hitpoints);
        g2d.fillRect(813, 5, (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints)/boss.startingHitpoints, 10);
        if(Events.timeOfDay == NIGHT){g2d.setColor(Color.red);}
        else{g2d.setColor(Colorations.red);}
        g2d.fillRect(813 + (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints)/boss.startingHitpoints, 5, ENEMY_HEALTH_BAR_WIDTH - (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints)/boss.startingHitpoints, 10);
        if(Events.timeOfDay == NIGHT){g2d.setColor(Color.white);}
        else{g2d.setColor(Color.black);}
        g2d.drawRect(813, 5, ENEMY_HEALTH_BAR_WIDTH, 10);
    }
    
    private static void paintHealthBar(Graphics2D g2d, Helicopter helicopter)
    {
        paintHealthBar(g2d, helicopter, HEALTH_BAR_POSITION.x, HEALTH_BAR_POSITION.y, HEALTH_BAR_LENGTH, true);
    }
    
    private static void paintHealthBar(Graphics2D g2d, Helicopter helicopter, int x, int y, int length, boolean rahmen)
    {
        float relativeEnergy = helicopter.getRelativeEnergy();
        float relativePlating = helicopter.getRelativePlating();
        if(rahmen)
        {
            g2d.setColor(Colorations.lightestGray);
            g2d.fillRect(x+1, y+1, length + 4, 23);
            g2d.setColor(Colorations.lightGray);
            g2d.fillRect(x+2, y+2, length+2, 10);
            g2d.fillRect(x+2, y+13, length+2, 10);
        }
        if(!helicopter.isEnergyAbilityActivatable())
        {
            g2d.setColor(Color.cyan);
        }
        else{g2d.setColor(helicopter.hasUnlimitedEnergy()
            ? Colorations.endlessEnergyViolet
            : Color.blue);}
        g2d.fillRect(x+3, y+3, (int)(length * relativeEnergy), 8);
        g2d.setColor(Color.gray);
        g2d.fillRect(x+3 + (int)(length * relativeEnergy), y+3, length - (int)(length * relativeEnergy), 8);
        
        g2d.setColor(helicopter.isInvincible()
            ? Color.yellow
            : Color.green);
        g2d.fillRect(x+3, y+14, (int)(length * relativePlating), 8);
        g2d.setColor(helicopter.recentDamageTimer == 0 ? Color.red : Colorations.variableRed);
        g2d.fillRect(x+3 + (int)(length * relativePlating), y+14, length - (int)(length * relativePlating), 8);
    }
    
    private static void paintCollectedPowerUps(Graphics2D g2d)
    {
        int j = 0;
        PowerUpPainter powerUpPainter = GraphicsManager.getInstance()
                                                       .getPainter(PowerUp.class);
        for(int i = 0; i < Menu.MAXIMUM_COLLECTED_POWERUPS_COUNT; i++)
        {
            if (Menu.collectedPowerUp[i] != null)
            {
                powerUpPainter.paint(g2d, Menu.collectedPowerUp[i], 166 + j * 28);
                j++;
            }
        }
    }
    
    private static void paintFpsDisplay(Graphics2D g2d)
    {
        g2d.setColor(Color.white);
        g2d.setFont(fontProvider.getPlain(18));
        g2d.drawString("FPS: " + (Menu.fps == 0 ? Menu.dictionary.pleaseWait() : Menu.fps), 292, 449);
    }
    
    private static void paintTimeDisplay(Graphics2D g2d, long time)
    {
        g2d.setColor(Color.white);
        g2d.setFont(fontProvider.getPlain(18));
        String outputstring = String.format("%s: %s", Menu.dictionary.playingTime(), Menu.returnTimeDisplayText(time));
        // TODO besser lösen -> String abmessen
        g2d.drawString(outputstring, Menu.language == ENGLISH ? 646 : 661, 450);
    }
    
    public static void paintGui(Graphics2D g2d, Helicopter helicopter)
    {
        // Werkstatt-Button
        if(Events.isRestartWindowVisible)
        {
            boolean gameOver;
            gameOver = Events.money <= Events.repairFee(helicopter, helicopter.isDamaged) || Events.level >= 51;
            paintRestartWindow(g2d, helicopter, gameOver);
        }
        else
        {
            if(helicopter.isOnTheGround())
            {
                Menu.buttons.get(GroundButtonType.REPAIR_SHOP).paint(g2d);
                Menu.buttons.get(GroundButtonType.MAIN_MENU).paint(g2d);
            }
            if(Menu.isMenuVisible){
                paintIngameMenu(g2d);}
        }
    }
    
    private static void paintRestartWindow(Graphics2D g2d,
                                           Helicopter helicopter,
                                           boolean gameOver)
    {
        if(!gameOver)
        {
            paintFrame(g2d,363, 147, 256, 111, Colorations.golden);
        }
        else if(Events.level < 51 || helicopter.getType() == HELIOS)
        {
            paintFrame(g2d,363, 112, 256, 146, Colorations.golden);
        }
        else if(Menu.language == ENGLISH)
        {
            paintFrame(g2d,363, 100, 256, 158, Colorations.golden);
        }
        else
        {
            paintFrame(g2d,363, 64, 256, 194, Colorations.golden);
        }
        
        g2d.setFont(fontProvider.getPlain(18));
        g2d.setColor(Colorations.red);
        if(!gameOver)
        {
            g2d.drawString((Menu.language == ENGLISH ? "Your helicopter was" : "Ihr Helikopter wurde"), 410, 179);
            g2d.drawString((Menu.language == ENGLISH ? "severely damaged!"   : "schwer beschädigt!"),   410, 197);
        }
        else if(Events.level < 51)
        {
            g2d.drawString((Menu.language == ENGLISH ? "Your helicopter was"        : "Ihr Helikopter wurde"),	   	390, 137);
            g2d.drawString((Menu.language == ENGLISH ? "severely damaged!"          : "schwer beschädigt!")  , 	    390, 155);
            g2d.drawString((Menu.language == ENGLISH ? "Unfortunately, you "        : "Leider reicht ihr Guthaben"), 390, 179);
            g2d.drawString((Menu.language == ENGLISH ? "cannot afford the repairs." : "nicht für eine Reparatur."),  390, 197);
        }
        else
        {
            if(helicopter.getType() == HELIOS)
            {
                g2d.drawString((Menu.language == ENGLISH ? "Congratulations!"         : "Herzlichen Glückwunsch!"),	    390,137);
                g2d.drawString((Menu.language == ENGLISH ? "The attack was repulsed." : "Der Angriff wurde abgewehrt."), 390,155);
                g2d.drawString((Menu.language == ENGLISH ? "Once again, mankind"      : "Wieder einmal lebt die"),	    390,179);
                g2d.drawString((Menu.language == ENGLISH ? "lives in peace!"          : "Menschheit in Frieden!"),	    390,197);
            }
            else
            {
                int i = Menu.language == ENGLISH ? 0 : 36;
                
                g2d.drawString((Menu.language == ENGLISH ? "You won a great victory,"	: "Sie haben einen großen"),		390,124-i);
                g2d.drawString((Menu.language == ENGLISH ? "but the war isn't over yet." : "Sieg errungen, aber der"), 		390,142-i);
                g2d.drawString((Menu.language == ENGLISH ? "Rumor has it only helios"	: "Krieg ist noch nicht vorbei."),	390,160-i);
                g2d.drawString((Menu.language == ENGLISH ? "type helicopters can"		: "Gerüchten zufolge können "),		390,178-i);
                g2d.drawString((Menu.language == ENGLISH ? "finally stop the invasion."	: "nur Helikopter der Helios-"),	390,196-i);
                g2d.drawString((Menu.language == ENGLISH ? ""							: "Klasse die Alien-Invasion"),		390,214-i);
                g2d.drawString((Menu.language == ENGLISH ? ""							: "endgültig stoppen."),			390,232-i);
            }
        }
        Button newGameButton2 = Menu.buttons.get(MainMenuButtonType.NEW_GAME_2);
        newGameButton2.setPrimaryLabel(Menu.dictionary.messageAfterCrash(gameOver));
        newGameButton2.paint(g2d);
    }
    
    private static void paintIngameMenu(Graphics2D g2d)
    {
        paintFrame(g2d,363, 77, 256, 231, Colorations.golden);
        g2d.setColor(Colorations.red);
        g2d.setFont(fontProvider.getPlain(25));
        g2d.drawString(Menu.dictionary.mainMenu(), 422 + (Menu.language == ENGLISH ? 6 : 0), 106);
        
        MainMenuButtonType.getValues().forEach(buttonType -> Menu.buttons.get(buttonType).paint(g2d));
    }
    
    public static void paintRepairShop(Graphics2D g2d, Helicopter helicopter)
    {
        // allgmeine Anzeigen
        g2d.setPaint(Colorations.gradientVariableWhite);
        g2d.setFont(fontProvider.getPlain(52));
        String inputString = Menu.dictionary.repairShop();
        g2d.drawString(inputString, 251 + (285 - g2d.getFontMetrics().stringWidth(inputString))/2, 65);
        g2d.setColor(Colorations.lightOrange);
        g2d.setFont(fontProvider.getPlain(22));
        g2d.drawString(String.format("%s: %d €", Menu.dictionary.credit(), Events.money), 27, 35);
        g2d.drawString(String.format("%s: %d", Menu.dictionary.currentLevel(), Events.level), 562, 35);
        g2d.setFont(fontProvider.getPlain(18));
        g2d.drawString(String.format("%s: %s", Menu.dictionary.playingTime(), Menu.repairShopTime), 27, 75);
        
        // Helicopter-Anzeige
        paintHelicopterDisplay(g2d, helicopter, 0, 10); //58
        
        // Reparatur-Button
        Menu.buttons.get(LeftSideRepairShopButtonType.REPAIR).paint(g2d);
        
        // Die Einsätze
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(20));
        g2d.drawString(Menu.dictionary.headlineMission(), 27, 382);
    
        Menu.buttons.get(LeftSideRepairShopButtonType.MISSION).paint(g2d);
        
        // Die Status-Leiste
        paintFrame(g2d, 251, 117, 285, 326);
        
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(20));
        g2d.drawString(Menu.dictionary.statusBar(), 255, 102);
        
        g2d.setColor(Colorations.lightOrange);
        g2d.setFont(fontProvider.getBold(16));
        g2d.drawString(Menu.dictionary.state(), STATUS_BAR_X1, STANDARD_UPGRADE_OFFSET_Y - 5);
        
        g2d.setColor(helicopter.isDamaged ? Color.red : Color.green);
        g2d.drawString(Menu.dictionary.stateCondition(helicopter.isDamaged), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y - 5);
        
        // Standard-Upgrades
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            g2d.setColor(Colorations.lightOrange);
            String tempString = Menu.dictionary.standardUpgradeName(standardUpgradeType);
            g2d.drawString(tempString + ":",
                STATUS_BAR_X1,
                STANDARD_UPGRADE_OFFSET_Y + 25 + standardUpgradeType.ordinal() * 25);
            
            if((standardUpgradeType != ENERGY_ABILITY && helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType)
                || ( standardUpgradeType == ENERGY_ABILITY
                && helicopter.hasMaximumUpgradeLevelFor(ENERGY_ABILITY)
                && !(helicopter.getType() == OROCHI
                && !helicopter.hasMaximumUpgradeLevelFor(MISSILE_DRIVE)))))
            {
                g2d.setColor(Colorations.golden);
            }
            else{g2d.setColor(Color.white);}
            if(standardUpgradeType == ENERGY_ABILITY && helicopter.getType() == OROCHI)
            {
                g2d.drawString(Menu.dictionary.level() + " " + helicopter.getUpgradeLevelOf(standardUpgradeType) + " / " + (helicopter.getUpgradeLevelOf(MISSILE_DRIVE)-1), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y + 150);
            }
            else{g2d.drawString(Menu.dictionary.level() + " " + helicopter.getUpgradeLevelOf(standardUpgradeType), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y + 25 + standardUpgradeType.ordinal() * 25);}
        }
        
        // Spezial-Upgrades
        // TODO überprüfen, ob iterieren über eine Schliefe möglich ist
        if(helicopter.hasSpotlights)
        {
            g2d.setColor(Colorations.golden);
            g2d.drawString(Menu.dictionary.specialUpgrade(SPOTLIGHT), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 0);
        }
        if(helicopter.hasGoliathPlating())
        {
            g2d.setColor(Colorations.golden);
            g2d.drawString(Menu.dictionary.specialUpgrade(GOLIATH_PLATING), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 25);
        }
        if(helicopter.hasPiercingWarheads)
        {
            g2d.setColor(Colorations.golden);
            g2d.drawString(Menu.dictionary.specialUpgrade(PIERCING_WARHEADS), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 50);
        }
        if(helicopter.numberOfCannons >= 2)
        {
            if(helicopter.getType() == OROCHI && helicopter.numberOfCannons == 2)
            {
                g2d.setColor(Color.white);
            }
            else{g2d.setColor(Colorations.golden);}
            if(helicopter.numberOfCannons == 3)
            {
                g2d.drawString(Menu.dictionary.secondAndThirdCannon(), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 75);
            }
            else
            {
                g2d.drawString(Menu.dictionary.specialUpgrade(EXTRA_CANNONS), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 75);
            }
        }
        if(helicopter.hasFifthSpecial())
        {
            // TODO String zusammenbauen und dann einmal g2d.drawString (auch oben)
            g2d.setColor(Colorations.golden);
            if(helicopter.getType() == PHOENIX || helicopter.getType() == PEGASUS)
            {
                if(!helicopter.isFifthSpecialOnMaximumStrength()){g2d.setColor(Color.white);}
                // TODO diese Fallunterscheidung in Methoden auslagern (überschreiben in PHOENIX und PEGASUS)
                g2d.drawString(Menu.dictionary.specialUpgrade(FIFTH_SPECIAL) + " (" + Menu.dictionary.level() + " " + (helicopter.getUpgradeLevelOf(helicopter.getType() == PHOENIX ? FIREPOWER : FIRE_RATE)-1) + ")", STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 100);
            }
            else
            {
                g2d.drawString(Menu.dictionary.specialUpgrade(FIFTH_SPECIAL), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 100);
            }
        }
        
        // Standard-Upgrades
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(20));
        g2d.drawString(Menu.dictionary.standardUpgrades(), StandardUpgradeButtonType.OFFSET.x + 4, 82);
        g2d.setFont(fontProvider.getPlain(15));
        // TODO hier muss sich auf die ANzahl der Elemente im Enum StandardUpgradeButtonSize bezogen werden, am besten ein forEach über die Elemente
        
        StandardUpgradeButtonType.getValues()
                                 .forEach(buttonSpecifier -> Menu.buttons.get(buttonSpecifier)
                                                                    .paint(g2d));
        
        // Message Box
        paintMessageFrame(g2d);
        
        // Spezial-Upgrades
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(20));
        g2d.drawString(Menu.dictionary.specialUpgrades(), 774, 142);
        g2d.setFont(fontProvider.getPlain(15));
        
        SpecialUpgradeButtonType.getValues()
                                .forEach(buttonSpecifier -> Menu.buttons.get(buttonSpecifier)
                                                                   .paint(g2d));
    }
    
    static int unlockedDisplayPosition(int timer)
    {
        if(timer < 50)
        {
            return 789 + 5 * (50 - timer);
        }
        else if(timer > UNLOCKED_DISPLAY_TIME - 50)
        {
            return 789 + 5 * (timer - UNLOCKED_DISPLAY_TIME + 50);
        }
        else return 789;
    }
    
    private static void paintHelicopterDisplay(Graphics2D g2d,
                                               Helicopter helicopter,
                                               int x, int y)
    {
        paintFrame(g2d, 26 + x,  85 + y, 200, 173, MenuManager.window  != GAME ? null : Colorations.lightestGray);
        g2d.setColor(Color.white);
        g2d.setFont(fontProvider.getBold(20));
        String typeName = Menu.dictionary.typeName(helicopter.getType());
        g2d.drawString(typeName, 28 + x + (196-g2d.getFontMetrics().stringWidth(typeName))/2, 113 + y);
        
        HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(helicopter.getClass());
        helicopterPainter.displayPaint(g2d, helicopter, 59 + x, 141 + y);
        
        paintFrameLine(g2d, 28 + x, 126 + y, 196);
        paintFrameLine(g2d, 28 + x, 226 + y, 196);
        
        if(MenuManager.window  != GAME)
        {
            paintHealthBar(g2d, helicopter, 30 + x, 230 + y, 187, false);
        }
        else
        {
            g2d.setFont(fontProvider.getBold(18));
            if(Menu.unlockedTimer > UNLOCKED_DISPLAY_TIME - 50)
            {
                g2d.setColor(Colorations.red);
                typeName = Menu.dictionary.unavailable();
            }
            else
            {
                g2d.setColor(Colorations.darkArrowGreen);
                typeName = Menu.dictionary.unlocked();
            }
            g2d.drawString(typeName, 28 + x + (196-g2d.getFontMetrics().stringWidth(typeName))/2, 249 + y);
        }
        
        if(MenuManager.window  == REPAIR_SHOP)
        {
            if(helicopter.isDamaged)
            {
                g2d.setColor(Color.red);
                g2d.setFont(fontProvider.getPlain(14));
                g2d.drawString(Menu.dictionary.damaged(), 34 + x, 216 + y);
            }
            g2d.setFont(fontProvider.getBold(16));
            g2d.setColor(Colorations.plating);
            int percentPlating = (Math.round(100 * helicopter.getRelativePlating()));
            FontMetrics fm = g2d.getFontMetrics();
            int sw = fm.stringWidth(""+percentPlating);
            g2d.drawString(percentPlating + "%", 203 - sw + x, STANDARD_UPGRADE_OFFSET_Y + y + 69);
        }
    }
    
    private static void paintMessageFrame(Graphics2D g2d)
    {
        paintFrame(g2d, 773, 11, 181, 98);
        g2d.setColor(Colorations.golden);
        g2d.setFont(fontProvider.getBold(14));
        for(int i = 0; i < Menu.MESSAGE_LINE_COUNT; i++){g2d.drawString(Menu.message[i], 785, 35 + i * 20); }
    }
    
    public static void paintStartscreen(Graphics2D g2d, Helicopter helicopter)
    {
        g2d.setPaint(Colorations.gradientVariableWhite);
        g2d.setFont(fontProvider.getPlain(80));
        g2d.drawString(GAME_NAME, 512 - g2d.getFontMetrics().stringWidth(GAME_NAME)/2, 85);
        
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(16));
        g2d.drawString(VERSION, 1016 - g2d.getFontMetrics().stringWidth(VERSION), 20);
        g2d.setColor(Colorations.darkGray);
        g2d.setFont(fontProvider.getItalicBold(15));
        g2d.drawString(String.format("%s %s", Menu.dictionary.developedBy(), DEVELOPERS_NAME), 505, 120);
        
        if(Menu.messageTimer == 0)
        {
            g2d.setColor(Colorations.variableYellow);
            g2d.setFont(fontProvider.getPlain(29));
            String tempString = Menu.dictionary.helicopterSelectionRequest();
            g2d.drawString(tempString, (512 - g2d.getFontMetrics().stringWidth(tempString)/2), 185);
        }
        else
        {
            g2d.setColor(Colorations.golden);
            g2d.setFont(fontProvider.getBold(16));
            
            g2d.drawString(Menu.message[0], (512 - g2d.getFontMetrics().stringWidth(Menu.message[0])/2), 160);
            g2d.drawString(Menu.message[1], (512 - g2d.getFontMetrics().stringWidth(Menu.message[1])/2), 185);
            g2d.drawString(Menu.message[2], (512 - g2d.getFontMetrics().stringWidth(Menu.message[2])/2), 210);
        }
        
        for(int i = 0; i < Menu.NUMBER_OF_START_SCREEN_HELICOPTERS; i++)
        {
            if(    Events.nextHelicopterType != null
                && Events.nextHelicopterType.ordinal() == (Menu.helicopterSelection +i)% HelicopterType.size())
            {
                g2d.setColor(Color.white);
            }
            else{g2d.setColor(Colorations.lightGray);}
            g2d.setFont(fontProvider.getBold(20));
            
            String className = Menu.dictionary.typeName(HelicopterType.getValues().get((Menu.helicopterSelection +i)% HelicopterType.size()));
            int sw = g2d.getFontMetrics().stringWidth(className);
            g2d.drawString(
                className,
                30 + Menu.START_SCREEN_OFFSET_X + i * Menu.HELICOPTER_DISTANCE + (206-sw)/2,
                225 + Menu.START_SCREEN_HELICOPTER_OFFSET_Y);
            
            g2d.setFont(new Font("Dialog", Font.BOLD, 15));
            
            HelicopterType type = HelicopterType.getValues().get((Menu.helicopterSelection +i)% HelicopterType.size());
            for(int j = 0; j < 3; j++)
            {
                g2d.drawString(
                    Menu.dictionary.helicopterInfos(type).get(j),
                    29 + Menu.START_SCREEN_OFFSET_X + i * Menu.HELICOPTER_DISTANCE,
                    380 + j * 20 + Menu.START_SCREEN_HELICOPTER_OFFSET_Y);
            }
            
            if(Menu.helicopterFrame[i].contains(helicopter.destination))
            {
                paintFrame(g2d, Menu.helicopterFrame[i], Colorations.darkBlue);
            }
            
            Helicopter nextStartScreenHelicopter = Menu.helicopterDummies.get(HelicopterType.getValues()
                                                                                       .get((Menu.helicopterSelection + i) % HelicopterType.size()));
            HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(nextStartScreenHelicopter.getClass());
            helicopterPainter.startScreenPaint(
                g2d,
                nextStartScreenHelicopter,
                HELICOPTER_START_SCREEN_OFFSET.x + Menu.START_SCREEN_OFFSET_X + i * Menu.HELICOPTER_DISTANCE,
                HELICOPTER_START_SCREEN_OFFSET.y + Menu.START_SCREEN_HELICOPTER_OFFSET_Y);
            // TODO destination darf keine Eigenschaft von Helicopter sein
            if(!Menu.helicopterFrame[i].contains(helicopter.destination.x, helicopter.destination.y))
            {
                paintFrame(g2d, Menu.helicopterFrame[i], Colorations.translucentBlack);
            }
            if(Events.allPlayable || HelicopterType.getValues().get((Menu.helicopterSelection + i)% HelicopterType.size()).isUnlocked())
            {
                // TODO diese boundaries als Konstante festlegen
                paintTickmark(g2d, i, 210, 323, 15, 20);
            }
        }
        
        // die Buttons
        StartScreenButtonType.getValues()
                             .forEach(buttonType -> Menu.buttons.get(buttonType).paint(g2d));
        
        // die grünen Pfeile
        for(int i = 0; i < 2; i++)
        {
            if(Menu.triangle[i].contains(helicopter.destination.x, helicopter.destination.y)){g2d.setColor(Color.green);}
            else{g2d.setColor(Colorations.arrowGreen);}
            g2d.fillPolygon(Menu.triangle[i]);
            g2d.setColor(Colorations.darkArrowGreen);
            g2d.drawPolygon(Menu.triangle[i]);
        }
        
        if(Menu.crossTimer > 0)
        {
            int alpha = (int)(255*(((double)CROSS_MAX_DISPLAY_TIME- Menu.crossTimer)/(CROSS_MAX_DISPLAY_TIME/2)));
            if(Menu.crossTimer < CROSS_MAX_DISPLAY_TIME/2){g2d.setColor(Color.red);}
            else{g2d.setColor(Colorations.setAlpha(Color.red, alpha));}
            g2d.fill(Menu.cross);
            if(Menu.crossTimer < CROSS_MAX_DISPLAY_TIME/2){g2d.setColor(Colorations.red);}
            else{g2d.setColor(Colorations.setAlpha(Colorations.red, alpha));}
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(Menu.cross);
            g2d.setStroke(new BasicStroke(1));
        }
    }
    
    private static void paintTickmark(Graphics2D g2d, int i, int x, int y, int w, int h)
    {
        Enemy.paintEnergyBeam(g2d,
            x + Menu.START_SCREEN_OFFSET_X + i * Menu.HELICOPTER_DISTANCE,
            y + h/2 + Menu.START_SCREEN_HELICOPTER_OFFSET_Y,
            x + w/3 + Menu.START_SCREEN_OFFSET_X + i * Menu.HELICOPTER_DISTANCE,
            y + h + Menu.START_SCREEN_HELICOPTER_OFFSET_Y);
        
        Enemy.paintEnergyBeam(g2d,
            x + w + Menu.START_SCREEN_OFFSET_X + i * Menu.HELICOPTER_DISTANCE,
            y + Menu.START_SCREEN_HELICOPTER_OFFSET_Y,
            x + w/3 + Menu.START_SCREEN_OFFSET_X + i * Menu.HELICOPTER_DISTANCE,
            y + h + Menu.START_SCREEN_HELICOPTER_OFFSET_Y);
    }
    
    public static void paintScoreScreen(Graphics2D g2d, Helicopter helicopter)
    {
        // Helikopter-Anzeige
        paintHelicopterDisplay(g2d, helicopter, 0, 5);
        
        g2d.setPaint(Colorations.gradientVariableWhite);
        g2d.setFont(fontProvider.getPlain(60));
        String temporaryString = Menu.dictionary.gameStatistics();
        g2d.drawString((temporaryString), (981-g2d.getFontMetrics().stringWidth(temporaryString))/2, 65);
        
        paintFrame(g2d, 619, 90, 376, 298);
        paintFrameLine(g2d, 621, 140, 372);
        paintFrameLine(g2d, 621, 249, 372);
        paintFrame(g2d, 297, 90, 250, 200);
        
        
        Menu.buttons.get(StartScreenSubCancelButtonType.CANCEL).paint(g2d);
        
        if(Events.level > MAXIMUM_LEVEL)
        {
            g2d.setColor(Color.green);
            if(Menu.language == ENGLISH)
            {
                g2d.drawString("Mission completed in " + Menu.minuten(helicopter.scoreScreenTimes[4]) + "!", SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS -9);
            }
            else
            {
                g2d.drawString("Mission in " + Menu.minuten(helicopter.scoreScreenTimes[4]) + " erfüllt!", SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS -9);
            }
        }
        else
        {
            g2d.setColor(Color.red);
            if(Menu.language == ENGLISH)
            {
                g2d.drawString("Mission failed after " + Menu.minuten(helicopter.scoreScreenTimes[4]) + " in level " + Events.level + "!", SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS - 9);
            }
            else
            {
                g2d.drawString("Mission nach " + Menu.minuten(helicopter.scoreScreenTimes[4]) + " in Level " + Events.level + " gescheitert!", SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS - 9);
            }
        }
        g2d.setColor(Color.white);
        g2d.drawString((Menu.language == ENGLISH ? "Playing time per boss: " : "Spielzeit pro Boss: "), SCORE_SCREEN_X_POS_1 - 20, SCORE_SCREEN_Y_POS - 9);
        
        // TODO magic number entfernen
        for(int i = 0; i < 5; i++)
        {
            if(i < (Events.level-1)/10)
            {
                g2d.setColor(Color.green);
                g2d.drawString(Menu.minuten(i == 0 ? helicopter.scoreScreenTimes[0] : helicopter.scoreScreenTimes[i] - helicopter.scoreScreenTimes[i-1]) + " (Boss " + (i+1) + ")", SCORE_SCREEN_X_POS_1, SCORE_SCREEN_Y_POS - 9 + SCORE_SCREEN_SPACE_BETWEEN_ROWS * (i+1));
            }
            else
            {
                g2d.setColor(Color.red);
                g2d.drawString((Menu.language == ENGLISH ? "undefeated" : "nicht besiegt") + " (Boss " + (i+1) + ")", SCORE_SCREEN_X_POS_1, SCORE_SCREEN_Y_POS - 9 + SCORE_SCREEN_SPACE_BETWEEN_ROWS * (i+1));
            }
        }
        
        g2d.setColor(Color.white);
        // TODO hier dictionary einsetzen und loop erzeugen
        g2d.drawString((Menu.language == ENGLISH ? "Crash landings: " : "Bruchlandungen: ")
                + helicopter.numberOfCrashes,
            SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS + SCORE_SCREEN_SPACE_BETWEEN_ROWS * 1 + 11);
        g2d.drawString((Menu.language == ENGLISH ? "Repairs: " : "Reparaturen: ")
                + helicopter.numberOfRepairs,
            SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS + SCORE_SCREEN_SPACE_BETWEEN_ROWS * 2 + 11);
        g2d.drawString((Menu.language == ENGLISH ? "Overall earnings: " : "Gesamt-Sold: ")
                +  Events.overallEarnings + " €",
            SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS + SCORE_SCREEN_SPACE_BETWEEN_ROWS * 3 + 11);
        
        int percentage = Events.bonusIncomePercentage();
        g2d.setColor(Colorations.scorescreen[0]);
        g2d.drawString((Menu.language == ENGLISH ? "Additional income due to extra boni: " : "Zusätzliche Einahmen durch Extra-Boni: +") + percentage + "%", SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS + SCORE_SCREEN_SPACE_BETWEEN_ROWS * 5);
        
        percentage = Calculations.percentage(helicopter.numberOfEnemiesKilled, helicopter.numberOfEnemiesSeen);
        g2d.setColor(Colorations.scorescreen[1]);
        g2d.drawString((Menu.language == ENGLISH ? "Defeated enemies: " : "Besiegte Gegner: ") + helicopter.numberOfEnemiesKilled + (Menu.language == ENGLISH ? " of " : " von ") + helicopter.numberOfEnemiesSeen + " (" + percentage + "%)", SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS + SCORE_SCREEN_SPACE_BETWEEN_ROWS * 6);
        
        percentage = Calculations.percentage(helicopter.numberOfMiniBossKilled, helicopter.numberOfMiniBossSeen);
        g2d.setColor(Colorations.scorescreen[2]);
        g2d.drawString((Menu.language == ENGLISH ? "Defeated mini-bosses: " : "Besiegte Mini-Bosse: ") + helicopter.numberOfMiniBossKilled + (Menu.language == ENGLISH ? " of " : " von ") + helicopter.numberOfMiniBossSeen + " (" + percentage + "%)", SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS + SCORE_SCREEN_SPACE_BETWEEN_ROWS * 7);
        
        percentage = (Calculations.percentage(helicopter.hitCounter, helicopter.missileCounter));
        g2d.setColor(Colorations.scorescreen[3]);
        g2d.drawString((Menu.language == ENGLISH ? "Hit rate: " : "Raketen-Trefferquote: ") + percentage + "%", SCORE_SCREEN_X_POS_2, SCORE_SCREEN_Y_POS + SCORE_SCREEN_SPACE_BETWEEN_ROWS * 8); //Zielsicherheit
    }
    
    /** Background Display **/
    public static void paintBackgroundDisplays(Graphics2D g2d)
    {
        updateDependencies();
        if(helicopter.isOnTheGround() || Menu.levelDisplayTimer.isActive())
        {
            paintLevelDisplay(g2d);
        }
        if(Events.commendationTimer > 0)
        {
            paintPraiseDisplay(g2d);
        }
        if(Menu.moneyDisplayTimer != Timer.DISABLED
            || helicopter.isDamaged
            || (helicopter.isOnTheGround()
            && !Events.isRestartWindowVisible))
        {
            paintCreditDisplay(g2d);
        }
        if(Menu.specialInfoSelection != 0)
        {
            paintSpecialInfoDisplay( g2d);
        }
    }
    
    private static void paintLevelDisplay(Graphics2D g2d)
    {
        if(Events.timeOfDay == NIGHT){g2d.setColor(Color.white);}
        else{g2d.setColor(Color.black);}
        g2d.setFont(fontProvider.getPlain(36));
        String levelString = "";
        if(Events.level < 51)
        {
            if(Events.level%10 == 0)
            {
                levelString = "Boss Level " + (Events.level/10);
            }
            else{levelString = "Level " + Events.level;}
        }
        FontMetrics fm = g2d.getFontMetrics();
        int sw = fm.stringWidth(levelString);
        g2d.drawString(levelString, (981-sw)/2, 55);
    }
    
    private static void paintPraiseDisplay(Graphics2D g2d)
    {
        if(Events.timeOfDay == NIGHT){g2d.setColor(Color.red);}
        // TODO wirklich 2 mal Color.RED? Funktion in Events zur Farbrückgabe verwenden
        else{g2d.setColor(Color.red);}
        MultiKillType multiKillType = MultiKillType.getMultiKillType(Events.lastMultiKill);
        g2d.setFont(fontProvider.getPlain(multiKillType.getTextSize()));
        FontMetrics fm = g2d.getFontMetrics();
        int sw = fm.stringWidth(multiKillType.getDesignation());
        g2d.drawString(multiKillType.getDesignation(), (981-sw)/2, 130);
    }
    
    private static void paintCreditDisplay(Graphics2D g2d)
    {
        if(Events.timeOfDay == NIGHT){g2d.setColor(Color.red);}
        else{g2d.setColor(Colorations.red);}
        g2d.setFont(fontProvider.getPlain(22));
        g2d.drawString(String.format("%s: %d €", Menu.dictionary.credit(), Events.money), 20, 35);
        if(Events.lastBonus > 0)
        {
            if(Events.timeOfDay == NIGHT){g2d.setColor(Colorations.MONEY_DISPLAY_NIGHT_RED);}
            else{g2d.setColor(Colorations.darkerOrange);}
            if(Menu.moneyDisplayTimer <= 23){g2d.setFont(new Font("Dialog", Font.PLAIN, Menu.moneyDisplayTimer));}
            if(Menu.moneyDisplayTimer > 23 && Menu.moneyDisplayTimer < 77){g2d.setFont(fontProvider.getPlain(22));}
            if(Menu.moneyDisplayTimer >= Menu.BONUS_DISPLAY_TIME -23){g2d.setFont(new Font("Dialog", Font.PLAIN, Menu.BONUS_DISPLAY_TIME - Menu.moneyDisplayTimer));}
            g2d.drawString("+" + Events.lastBonus + " €", 20, 60);
            if(Events.lastExtraBonus > 0)
            {
                if(Events.timeOfDay == NIGHT){g2d.setColor(Color.yellow);}
                else{g2d.setColor(Colorations.darkYellow);}
                g2d.drawString("+" + Events.lastExtraBonus + " €", 20, 86);
            }
        }
    }
    
    private static void paintSpecialInfoDisplay(Graphics2D g2d)
    {
        g2d.setColor(Colorations.red);
        g2d.setFont(fontProvider.getPlain(22));
        String infoString = "";
        if(Menu.specialInfoSelection == 1)
        {
            infoString = "Kills bis LevelUp: "
                + Events.killsAfterLevelUp
                + "/"
                + Events.numberOfKillsNecessaryForNextLevelUp();
        }
        else if(Menu.specialInfoSelection == 2)
        {
            infoString = "Aktive PowerUps: "
                + controller.powerUps.get(ACTIVE).size()
                + ";   Inaktive PowerUps: "
                + controller.getGameEntityRecycler().sizeOf(PowerUp.class);
        }
        else if(Menu.specialInfoSelection == 3)
        {
            infoString = "Aktive Explosionen: "
                + controller.explosions.get(ACTIVE).size()
                + ";   Inaktive Explosionen: "
                + controller.explosions.get(INACTIVE).size();
        }
        else if(Menu.specialInfoSelection == 4)
        {
            infoString = "Aktive Gegner: "
                + (controller.enemies.get(ACTIVE).size()- Enemy.currentNumberOfBarriers)  + " / " + (Enemy.maxNr)
                + ";   Zerstörte Gegner: "
                + controller.enemies.get(DESTROYED).size()
                + ";   Hindernisse: "
                + Enemy.currentNumberOfBarriers + " / " + Enemy.maxBarrierNr
                + ";   Inaktive Gegner: "
                + controller.enemies.get(INACTIVE).size();
        }
        else if(Menu.specialInfoSelection == 5)
        {
            infoString = "Aktive Raketen: "
                + controller.missiles.get(ACTIVE).size()
                + ";   Inaktive Raketen: "
                + controller.missiles.get(INACTIVE).size();
        }
        else if(Menu.specialInfoSelection == 6)
        {
            infoString = "Aktive gegnerische Geschosse: "
                + controller.enemyMissiles.get(ACTIVE).size()
                + ";   Inaktive gegnerische Geschosse: "
                + controller.enemyMissiles.get(INACTIVE).size();
        }
        else if(Menu.specialInfoSelection == 7)
        {
            infoString = "Aktive Hintergrundobjekte: "
                + controller.backgroundObjects.get(ACTIVE).size()
                + ";   Inaktive Hintergrundobjekte: "
                + controller.backgroundObjects.get(INACTIVE).size();
        }
        else if(Menu.specialInfoSelection == 8)
        {
            infoString = "Speed level: "
                + helicopter.getUpgradeLevelOf(ROTOR_SYSTEM)
                + " +   Speed: " + helicopter.rotorSystem;
        }
        else if(Menu.specialInfoSelection == 9)
        {
            infoString = "Bonus: " + Events.overallEarnings
                + "   Extra-Bonus: " + Events.extraBonusCounter;
        }
        else if(Menu.specialInfoSelection == 10)
        {
            infoString = "Menü sichtbar: " + Menu.isMenuVisible;
        }
        else if(Menu.specialInfoSelection == 11)
        {
            int percentage = helicopter.numberOfEnemiesSeen > 0
                ? 100*helicopter.numberOfEnemiesKilled /helicopter.numberOfEnemiesSeen
                : 0;
            infoString = (Menu.language == ENGLISH
                ? "Defeated enemies: "
                : "Besiegte Gegner: ")
                + helicopter.numberOfEnemiesKilled
                + (Menu.language == ENGLISH ? " of " : " von ")
                + helicopter.numberOfEnemiesSeen
                + " ("
                + percentage
                + "%)";
        }
        else if(Menu.specialInfoSelection == 12)
        {
            int percentage = helicopter.missileCounter != 0
                ? 100*helicopter.hitCounter /helicopter.missileCounter
                : 0;
            infoString = "Missile counter: "
                + helicopter.missileCounter
                + "; Hit counter: "
                + helicopter.hitCounter
                + "; Hit rate: "
                + percentage;
        }
        else if(Menu.specialInfoSelection == 13)
        {
            infoString = Menu.dictionary.typeName();
        }
        else if(Menu.specialInfoSelection == 14)
        {
            infoString = helicopter.getTypeSpecificDebuggingOutput();
        }
        else if(Menu.specialInfoSelection == 15)
        {
            infoString = String.format( "Hitpoints: %.2f/%.2f; Energie: %.2f/%.2f",
                helicopter.getCurrentPlating(),
                helicopter.getMaximumPlating(),
                helicopter.getCurrentEnergy(),
                helicopter.getMaximumEnergy());
        }
        g2d.drawString("Info: " + infoString, 20, 155);
    }
    
    public static void paintStartScreenSub(Graphics2D g2d, int counter)
    {
        // TODO mindestens auf mehrere Methoden aufteilen
        g2d.setColor(Color.white);
        g2d.setFont(fontProvider.getPlain(29));
        if(MenuManager.window == SETTINGS)
        {
            g2d.drawString(Menu.dictionary.settings(), 40, 55);
        }
        else{g2d.drawString(Menu.buttons.get(Menu.page).getPrimaryLabel(), 40, 55);}
        
        paintFrameLine(g2d, 26, 67, 971);
        paintFrame(g2d, 26, 21, 971, 317);
        
        // die Buttons
        if(!showOnlyCancelButton())
        {
            StartScreenSubButtonType.getValues()
                                    .forEach(buttonSpecifier -> Menu.buttons.get(buttonSpecifier)
                                                                        .paint(g2d));
        }
        Menu.buttons.get(StartScreenSubCancelButtonType.CANCEL)
               .paint(g2d);
        
        if(MenuManager.window  == HELICOPTER_TYPES)
        {
            if(Menu.page.ordinal() > 1 && Menu.page.ordinal() < 2 + HelicopterType.size())
            {
                paintHelicopterInStartScreenSub(g2d);
            }
            else if(Menu.page == StartScreenSubButtonType.BUTTON_2)
            {
                String tempString = "";
                StandardUpgradeType standardUpgradeType = null;
                HelicopterType helicopterType = null;
                // TODO über Standard-Upgrade-Types iterieren
                for(int i = 0; i < StandardUpgradeType.size() + 1; i++)
                {
                    // TODO über HelicopterTypes iterieren
                    for(int j = 0; j < HelicopterType.size() + 1; j++)
                    {
                        if(i > 0){standardUpgradeType = StandardUpgradeType.getValues()[i-1];}
                        if(j > 0){helicopterType = HelicopterType.getValues().get(j-1);}
                        
                        if(j == 0 && i != 0)
                        {
                            g2d.setColor(Colorations.golden);
                            tempString = Menu.dictionary.standardUpgradeName(standardUpgradeType);
                        }
                        else if(j != 0 && i == 0)
                        {
                            g2d.setColor(Colorations.brightenUp(helicopterType.getStandardPrimaryHullColor()));
                            tempString = Menu.dictionary.helicopterName(helicopterType);
                        }
                        else if(i != 0)
                        {
                            PriceLevel upgradeCosts = helicopterType.getPriceLevelFor(standardUpgradeType);
                            g2d.setColor(upgradeCosts.getColor());
                            tempString = Menu.dictionary.priceLevel(upgradeCosts);
                        }
                        if(tempString == null) tempString = "Erwischt!";
                        g2d.drawString(tempString, 200 + (j-1) * 135, 140 + (i == 0 ? 0 : 5) + (i-1) * 32);
                    }
                }
            }
        }
        else if(MenuManager.window == HIGH_SCORE)
        {
            if(Menu.page == StartScreenSubButtonType.BUTTON_1)
            {
                String tempString = "";
                // TODO Magic number entfernen
                for(int i = 0; i < 6; i++)
                {
                    // TODO über HelicopterTypes iterieren
                    for(int j = 0; j < HelicopterType.size() + 1; j++)
                    {
                        if(j == 0 && i!=0)
                        {
                            g2d.setColor(Colorations.golden);
                            tempString = "Boss " + i;
                        }
                        else if(j != 0 && i==0)
                        {
                            g2d.setColor(Colorations.brightenUp(HelicopterType.getValues().get(j-1).getStandardPrimaryHullColor()));
                            tempString = Menu.dictionary.helicopterName(HelicopterType.getValues().get(j-1));
                        }
                        else if(i != 0)
                        {
                            g2d.setColor(Color.white);
                            if(i==1)
                            {
                                tempString = Events.recordTime[j-1][i-1] == 0
                                    ? ""
                                    : Events.recordTime[j - 1][i - 1] + " min";
                            }
                            else
                            {
                                tempString = Events.recordTime[j-1][i-1] == 0
                                    ? ""
                                    : Events.recordTime[j - 1][i - 1] - Events.recordTime[j - 1][i - 2] + " min";
                            }
                            
                        }
                        g2d.drawString(tempString, 200 + (j-1) * 135, 150 + (i-1) * 35);
                    }
                }
            }
            else
            {
                if(Menu.page.ordinal() > 1 && Menu.page.ordinal() < 2 + HelicopterType.size())
                {
                    paintHelicopterInStartScreenSub(g2d);
                }
                
                int columnDistance = 114,
                    topLine = 125,
                    lineDistance = 21,
                    leftColumn = 55,
                    realLeftColumn = leftColumn,
                    xShift = 10;
                
                g2d.setColor(Color.lightGray);
                for(int i = 0; i < Menu.NUMBER_OF_HIGH_SCORE_COLUMN_NAMES; i++)
                {
                    if(i == 1){realLeftColumn = leftColumn - 46;}
                    else if(i == 2){realLeftColumn = leftColumn + 42;}
                    g2d.drawString(
                        Menu.dictionary.highScoreColumnNames().get(i),
                        realLeftColumn + i * columnDistance,
                        topLine - lineDistance);
                }
                
                for(int j = 0; j < HighScoreEntry.NUMBER_OF_ENTRIES; j++)
                {
                    HighScoreEntry tempEntry = Events.highScore[Menu.page.ordinal()==1?6:Menu.page.ordinal()-2][j];
                    
                    if(tempEntry != null)
                    {
                        g2d.setColor(Color.white);
                        g2d.drawString(toStringWithSpace(j+1, false), leftColumn + xShift , topLine + j * lineDistance);
                        g2d.drawString(tempEntry.playerName, leftColumn - 46 + xShift + columnDistance, topLine + j * lineDistance);
                        g2d.setColor(Colorations.brightenUp(tempEntry.helicopterType.getStandardPrimaryHullColor()));
                        g2d.drawString(Menu.dictionary.helicopterName(tempEntry.helicopterType),   realLeftColumn + xShift + 2 * columnDistance, topLine + j * lineDistance);
                        g2d.setColor(tempEntry.maxLevel > MAXIMUM_LEVEL ? Colorations.HS_GREEN : Colorations.HS_RED);
                        int printLevel = Math.min(tempEntry.maxLevel, MAXIMUM_LEVEL);
                        g2d.drawString(toStringWithSpace(printLevel), realLeftColumn + xShift + 3 * columnDistance, topLine + j * lineDistance);
                        g2d.setColor(Color.white);
                        g2d.drawString(toStringWithSpace((int)tempEntry.playingTime) + " min", realLeftColumn + xShift + 4 * columnDistance, topLine + j * lineDistance);
                        g2d.drawString(toStringWithSpace(tempEntry.crashes), 		  				  realLeftColumn + xShift + 5 * columnDistance, topLine + j * lineDistance);
                        g2d.drawString(toStringWithSpace(tempEntry.repairs),		  				  realLeftColumn + xShift + 6 * columnDistance, topLine + j * lineDistance);
                        g2d.setColor(Colorations.percentColor(2*tempEntry.bonusIncome));
                        g2d.drawString(toStringWithSpace(tempEntry.bonusIncome) + "%",		  realLeftColumn + xShift + 7 * columnDistance, topLine + j * lineDistance);
                    }
                    else break;
                }
            }
        }
        else if(MenuManager.window  == SETTINGS)
        {
            g2d.setFont(fontProvider.getPlain(18));
            g2d.setColor(Colorations.lightestGray);
            
            for(int i = 0; i < Menu.NUMBER_OF_SETTING_OPTIONS; i++)
            {
                g2d.drawString(Menu.dictionary.settingOption(i), SETTING_LEFT, SETTING_TOP + i * SETTING_LINE_SPACING);
            }
            
            g2d.setColor(Colorations.golden);
            g2d.drawString( Menu.dictionary.displayMode()
                    + (!Main.isFullScreen ? "" : " ("
                    + (Menu.hasOriginalResolution
                    ? Main.currentDisplayMode.getWidth()
                    + "x"
                    + Main.currentDisplayMode.getHeight()
                    : "1280x720") + ")"),
                SETTING_LEFT + SETTING_COLUMN_SPACING,
                SETTING_TOP);
            
            g2d.setColor(Audio.isSoundOn ? Color.green : Color.red);
            g2d.drawString( Menu.dictionary.activationState(Audio.isSoundOn)						, SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + SETTING_LINE_SPACING);
            if(Audio.MICHAEL_MODE && Audio.isSoundOn)
            {
                g2d.setColor(Colorations.golden);
                g2d.drawString("(" + (Audio.standardBackgroundMusic ? "Classic" : "Michael" + Menu.dictionary.modeSuffix()) + ")", SETTING_LEFT + SETTING_COLUMN_SPACING + 25, SETTING_TOP + SETTING_LINE_SPACING);
            }
            
            g2d.setColor(Controller.antialiasing ? Color.green : Color.red);
            g2d.drawString(Menu.dictionary.activationState(Controller.antialiasing)			, SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + 2 * SETTING_LINE_SPACING);
            
            g2d.setColor(Colorations.golden);
            g2d.drawString(Menu.language.getNativeName(), SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + 3 * SETTING_LINE_SPACING);
            
            if(Menu.page == StartScreenSubButtonType.BUTTON_5){g2d.setColor(Color.white);}
            g2d.drawString(HighScoreEntry.currentPlayerName, SETTING_LEFT + SETTING_COLUMN_SPACING, SETTING_TOP + 4 * SETTING_LINE_SPACING);
            
            if(Menu.page == StartScreenSubButtonType.BUTTON_5 && (counter/30)%2 == 0){g2d.drawString("|", SETTING_LEFT + SETTING_COLUMN_SPACING + g2d.getFontMetrics().stringWidth(HighScoreEntry.currentPlayerName), SETTING_TOP + 4 * SETTING_LINE_SPACING);}
            
        } else if (MenuManager.window == DESCRIPTION && Menu.page == StartScreenSubButtonType.BUTTON_6)
        {
            int x = 52, y = 120, yOffset = 35;
            PowerUpPainter powerUpPainter = GraphicsManager.getInstance()
                                                           .getPainter(PowerUp.class);
            for (PowerUpType powerUpType : PowerUpType.getValues())
            {
                powerUpPainter.paint(
                    g2d,
                    x, y + powerUpType.getMenuPosition() * yOffset,
                    Menu.POWER_UP_SIZE, Menu.POWER_UP_SIZE,
                    powerUpType.getSurfaceColor(),
                    powerUpType.getCrossColor());
            }
        }
    }
    
    private static boolean showOnlyCancelButton()
    {
        return !Menu.buttons.get(StartScreenSubButtonType.BUTTON_2).isVisible();
    }
    
    private static void paintHelicopterInStartScreenSub(Graphics2D g2d)
    {
        Helicopter startScreenSubHelicopter = Menu.helicopterDummies.get(HelicopterType.getValues().get(Menu.page.ordinal()-2));
        HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(startScreenSubHelicopter.getClass());
        helicopterPainter.startScreenSubPaint(g2d, startScreenSubHelicopter);
    }
    
    private static String toStringWithSpace(int value)
    {
        return toStringWithSpace(value, true);
    }
    private static String toStringWithSpace(int value, boolean big)
    {
        // TODO String.format oder ähnliches verwenden
        return (big	? (value >= 100 ? "" : "  ") : "")
            + (value >= 10 ? "" : "  ")
            + value;
    }
    
    /** Graphical objects **/
    // TODO diese sollten in eigene Klassen ausgelagert werden
    private static void paintFrame(Graphics2D g2d, Rectangle frame, Color filledColor)
    {
        paintFrame(g2d, frame.x, frame.y, frame.width, frame.height, filledColor);
    }
    
    private static void paintFrame(Graphics2D g2d, int left, int top, int width, int height)
    {
        paintFrame(g2d, left, top, width, height, null);
    }
    
    public static void paintFrame(Graphics2D g2d, int left, int top, int width, int height, Color filledColor)
    {
        ArrayList<GradientPaint> gradientPaintList = new ArrayList<>(4);
        
        gradientPaintList.add(new GradientPaint(0, top-1, Color.white, 0, top+4, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(0, top+height-1, Color.white, 0, top+height+4, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(left, 0, Color.white, left+5, 0, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(left+width, 0, Color.white, left+width+5, 0, Colorations.darkestGray, true));
        if(filledColor != null)
        {
            g2d.setPaint(filledColor);
            g2d.fillRect(left, top, width, height);
        }
        g2d.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setPaint(gradientPaintList.get(0));
        g2d.drawLine(left+1, top, left+width-2, top);
        g2d.setPaint(gradientPaintList.get(1));
        g2d.drawLine(left+1, top+height, left+width-2, top+height);
        g2d.setPaint(gradientPaintList.get(2));
        g2d.drawLine(left, top+1, left, top+height-2);
        g2d.setPaint(gradientPaintList.get(3));
        g2d.drawLine(left+width, top+1, left+width, top+height-2);
        g2d.setStroke(new BasicStroke(1));
    }
    
    private static void paintFrameLine(Graphics2D g2d, int left, int top, int width)
    {
        GradientPaint frameLineGradientPaint = new GradientPaint(0, top-1, Color.white, 0, top+4,
            Colorations.darkestGray,
            true);
        g2d.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setPaint(frameLineGradientPaint);
        g2d.drawLine(left+1, top, left+width-2, top);
        g2d.setStroke(new BasicStroke(1));
    }
}
