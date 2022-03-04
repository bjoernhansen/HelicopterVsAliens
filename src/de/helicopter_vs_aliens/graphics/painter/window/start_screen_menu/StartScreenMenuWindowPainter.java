package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.PowerUpPainter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.graphics.painter.window.WindowPainter;
import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.gui.button.StartScreenSubButtonType;
import de.helicopter_vs_aliens.gui.button.StartScreenSubCancelButtonType;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.score.HighScoreEntry;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Graphics2D;

import static de.helicopter_vs_aliens.control.Events.MAXIMUM_LEVEL;
import static de.helicopter_vs_aliens.gui.WindowType.DESCRIPTION;
import static de.helicopter_vs_aliens.gui.WindowType.HELICOPTER_TYPES;
import static de.helicopter_vs_aliens.gui.WindowType.HIGH_SCORE;
import static de.helicopter_vs_aliens.gui.WindowType.SETTINGS;
import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;

public abstract class StartScreenMenuWindowPainter extends WindowPainter
{
    private static final int
        SETTING_LEFT = 80,
        SETTING_COLUMN_SPACING = 145,
        SETTING_LINE_SPACING = 40,
        SETTING_TOP = 130;
    
    @Override
    public void paint(Graphics2D g2d, Window window)
    {
        super.paint(g2d, window);
        paintStartScreenMenu(g2d, controller.framesCounter);
    }
    
    public static void paintStartScreenMenu(Graphics2D g2d, int counter)
    {
        // TODO mindestens auf mehrere Methoden aufteilen
        g2d.setColor(Color.white);
        g2d.setFont(fontProvider.getPlain(29));
        if(WindowManager.window == SETTINGS)
        {
            g2d.drawString(Window.dictionary.settings(), 40, 55);
        }
        else{g2d.drawString(Window.buttons.get(Window.page).getPrimaryLabel(), 40, 55);}
        
        paintFrameLine(g2d, 26, 67, 971);
        paintFrame(g2d, 26, 21, 971, 317);
        
        // die Buttons
        if(!showOnlyCancelButton())
        {
            StartScreenSubButtonType.getValues()
                                    .forEach(buttonSpecifier -> Window.buttons.get(buttonSpecifier)
                                                                              .paint(g2d));
        }
        Window.buttons.get(StartScreenSubCancelButtonType.CANCEL)
                      .paint(g2d);
        
        if(WindowManager.window  == HELICOPTER_TYPES)
        {
            if(Window.page.ordinal() > 1 && Window.page.ordinal() < 2 + HelicopterType.size())
            {
                paintHelicopterInStartScreenMenu(g2d);
            }
            else if(Window.page == StartScreenSubButtonType.BUTTON_2)
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
                            tempString = Window.dictionary.standardUpgradeName(standardUpgradeType);
                        }
                        else if(j != 0 && i == 0)
                        {
                            g2d.setColor(Colorations.brightenUp(helicopterType.getStandardPrimaryHullColor()));
                            tempString = Window.dictionary.helicopterName(helicopterType);
                        }
                        else if(i != 0)
                        {
                            PriceLevel upgradeCosts = helicopterType.getPriceLevelFor(standardUpgradeType);
                            g2d.setColor(upgradeCosts.getColor());
                            tempString = Window.dictionary.priceLevel(upgradeCosts);
                        }
                        if(tempString == null) tempString = "Erwischt!";
                        g2d.drawString(tempString, 200 + (j-1) * 135, 140 + (i == 0 ? 0 : 5) + (i-1) * 32);
                    }
                }
            }
        }
        else if(WindowManager.window == HIGH_SCORE)
        {
            if(Window.page == StartScreenSubButtonType.BUTTON_1)
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
                            tempString = Window.dictionary.helicopterName(HelicopterType.getValues().get(j-1));
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
                if(Window.page.ordinal() > 1 && Window.page.ordinal() < 2 + HelicopterType.size())
                {
                    paintHelicopterInStartScreenMenu(g2d);
                }
                
                int columnDistance = 114,
                    topLine = 125,
                    lineDistance = 21,
                    leftColumn = 55,
                    realLeftColumn = leftColumn,
                    xShift = 10;
                
                g2d.setColor(Color.lightGray);
                for(int i = 0; i < Window.NUMBER_OF_HIGH_SCORE_COLUMN_NAMES; i++)
                {
                    if(i == 1){realLeftColumn = leftColumn - 46;}
                    else if(i == 2){realLeftColumn = leftColumn + 42;}
                    g2d.drawString(
                        Window.dictionary.highScoreColumnNames().get(i),
                        realLeftColumn + i * columnDistance,
                        topLine - lineDistance);
                }
                
                for(int j = 0; j < HighScoreEntry.NUMBER_OF_ENTRIES; j++)
                {
                    HighScoreEntry tempEntry = Events.highScore[Window.page.ordinal()==1?6: Window.page.ordinal()-2][j];
                    
                    if(tempEntry != null)
                    {
                        g2d.setColor(Color.white);
                        g2d.drawString(toStringWithSpace(j+1, false), leftColumn + xShift , topLine + j * lineDistance);
                        g2d.drawString(tempEntry.playerName, leftColumn - 46 + xShift + columnDistance, topLine + j * lineDistance);
                        g2d.setColor(Colorations.brightenUp(tempEntry.helicopterType.getStandardPrimaryHullColor()));
                        g2d.drawString(Window.dictionary.helicopterName(tempEntry.helicopterType),   realLeftColumn + xShift + 2 * columnDistance, topLine + j * lineDistance);
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
        else if(WindowManager.window  == SETTINGS)
        {
            g2d.setFont(fontProvider.getPlain(18));
            g2d.setColor(Colorations.lightestGray);
            
            for(int i = 0; i < Window.NUMBER_OF_SETTING_OPTIONS; i++)
            {
                g2d.drawString(Window.dictionary.settingOption(i), SETTING_LEFT, SETTING_TOP + i * SETTING_LINE_SPACING);
            }
            
            g2d.setColor(Colorations.golden);
            g2d.drawString( Window.dictionary.displayMode()
                    + (!Main.isFullScreen ? "" : " ("
                    + (Window.hasOriginalResolution
                    ? Main.currentDisplayMode.getWidth()
                    + "x"
                    + Main.currentDisplayMode.getHeight()
                    : "1280x720") + ")"),
                SETTING_LEFT + SETTING_COLUMN_SPACING,
                SETTING_TOP);
            
            g2d.setColor(Audio.isSoundOn ? Color.green : Color.red);
            g2d.drawString( Window.dictionary.activationState(Audio.isSoundOn)						, SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + SETTING_LINE_SPACING);
            if(Audio.MICHAEL_MODE && Audio.isSoundOn)
            {
                g2d.setColor(Colorations.golden);
                g2d.drawString("(" + (Audio.standardBackgroundMusic ? "Classic" : "Michael" + Window.dictionary.modeSuffix()) + ")", SETTING_LEFT + SETTING_COLUMN_SPACING + 25, SETTING_TOP + SETTING_LINE_SPACING);
            }
            
            g2d.setColor(Controller.antialiasing ? Color.green : Color.red);
            g2d.drawString(Window.dictionary.activationState(Controller.antialiasing)			, SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + 2 * SETTING_LINE_SPACING);
            
            g2d.setColor(Colorations.golden);
            g2d.drawString(Window.language.getNativeName(), SETTING_LEFT + SETTING_COLUMN_SPACING	, SETTING_TOP + 3 * SETTING_LINE_SPACING);
            
            if(Window.page == StartScreenSubButtonType.BUTTON_5){g2d.setColor(Color.white);}
            g2d.drawString(HighScoreEntry.currentPlayerName, SETTING_LEFT + SETTING_COLUMN_SPACING, SETTING_TOP + 4 * SETTING_LINE_SPACING);
            
            if(Window.page == StartScreenSubButtonType.BUTTON_5 && (counter/30)%2 == 0){g2d.drawString("|", SETTING_LEFT + SETTING_COLUMN_SPACING + g2d.getFontMetrics().stringWidth(HighScoreEntry.currentPlayerName), SETTING_TOP + 4 * SETTING_LINE_SPACING);}
            
        } else if (WindowManager.window == DESCRIPTION && Window.page == StartScreenSubButtonType.BUTTON_6)
        {
            int x = 52, y = 120, yOffset = 35;
            PowerUpPainter powerUpPainter = GraphicsManager.getInstance()
                                                           .getPainter(PowerUp.class);
            for (PowerUpType powerUpType : PowerUpType.getValues())
            {
                powerUpPainter.paint(
                    g2d,
                    x, y + powerUpType.getMenuPosition() * yOffset,
                    Window.POWER_UP_SIZE, Window.POWER_UP_SIZE,
                    powerUpType.getSurfaceColor(),
                    powerUpType.getCrossColor());
            }
        }
    }
    
    private static boolean showOnlyCancelButton()
    {
        return !Window.buttons.get(StartScreenSubButtonType.BUTTON_2).isVisible();
    }
    
    private static void paintHelicopterInStartScreenMenu(Graphics2D g2d)
    {
        Helicopter startScreenSubHelicopter = Window.helicopterDummies.get(HelicopterType.getValues().get(Window.page.ordinal()-2));
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
}
