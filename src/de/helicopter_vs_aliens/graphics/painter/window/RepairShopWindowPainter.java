package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.button.LeftSideRepairShopButtonType;
import de.helicopter_vs_aliens.gui.button.SpecialUpgradeButtonType;
import de.helicopter_vs_aliens.gui.button.StandardUpgradeButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Graphics2D;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;
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

public class RepairShopWindowPainter extends WindowPainter
{
    private static final int
        STATUS_BAR_X1 = 268,                   			// x-Postion der Schrift in der Statusanzeige (Werkstatt-Menü,erste Spalte)
        STATUS_BAR_X2 = 421,                   			// x-Postion der Schrift in der Statusanzeige (Werkstatt-Menü,zweite Spalte)
        SPECIAL_UPGRADE_OFFSET_Y = 328;                 // y-Verschiebung der Spezial-Upgrades in der Statusanzeige (Werkstatt-Menü)
        
    @Override
    public void paint(Graphics2D g2d, Window window)
    {
        super.paint(g2d, window);
        paintRepairShop(g2d, helicopter);
    }
    
    private static void paintRepairShop(Graphics2D g2d, Helicopter helicopter)
    {
        // allgemeine Anzeigen
        g2d.setPaint(Colorations.gradientVariableWhite);
        g2d.setFont(fontProvider.getPlain(52));
        String inputString = Window.dictionary.repairShop();
        g2d.drawString(inputString, 251 + (285 - g2d.getFontMetrics().stringWidth(inputString))/2, 65);
        g2d.setColor(Colorations.lightOrange);
        g2d.setFont(fontProvider.getPlain(22));
        g2d.drawString(String.format("%s: %d €", Window.dictionary.credit(), Events.money), 27, 35);
        g2d.drawString(String.format("%s: %d", Window.dictionary.currentLevel(), Events.level), 562, 35);
        g2d.setFont(fontProvider.getPlain(18));
        g2d.drawString(String.format("%s: %s", Window.dictionary.playingTime(), Window.repairShopTime), 27, 75);
        
        // Helicopter-Anzeige
        paintHelicopterDisplay(g2d, helicopter, 0, 10); //58
        
        // Reparatur-Button
        Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).paint(g2d);
        
        // Die Einsätze
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(20));
        g2d.drawString(Window.dictionary.headlineMission(), 27, 382);
        
        Window.buttons.get(LeftSideRepairShopButtonType.MISSION).paint(g2d);
        
        // Die Status-Leiste
        paintFrame(g2d, 251, 117, 285, 326);
        
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(20));
        g2d.drawString(Window.dictionary.statusBar(), 255, 102);
        
        g2d.setColor(Colorations.lightOrange);
        g2d.setFont(fontProvider.getBold(16));
        g2d.drawString(Window.dictionary.state(), STATUS_BAR_X1, STANDARD_UPGRADE_OFFSET_Y - 5);
        
        g2d.setColor(helicopter.isDamaged ? Color.red : Color.green);
        g2d.drawString(Window.dictionary.stateCondition(helicopter.isDamaged), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y - 5);
        
        // Standard-Upgrades
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            g2d.setColor(Colorations.lightOrange);
            String tempString = Window.dictionary.standardUpgradeName(standardUpgradeType);
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
                g2d.drawString(Window.dictionary.level() + " " + helicopter.getUpgradeLevelOf(standardUpgradeType) + " / " + (helicopter.getUpgradeLevelOf(MISSILE_DRIVE)-1), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y + 150);
            }
            else{g2d.drawString(Window.dictionary.level() + " " + helicopter.getUpgradeLevelOf(standardUpgradeType), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y + 25 + standardUpgradeType.ordinal() * 25);}
        }
        
        // Spezial-Upgrades
        // TODO überprüfen, ob iterieren über eine Schliefe möglich ist
        if(helicopter.hasSpotlights)
        {
            g2d.setColor(Colorations.golden);
            g2d.drawString(Window.dictionary.specialUpgrade(SPOTLIGHT), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 0);
        }
        if(helicopter.hasGoliathPlating())
        {
            g2d.setColor(Colorations.golden);
            g2d.drawString(Window.dictionary.specialUpgrade(GOLIATH_PLATING), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 25);
        }
        if(helicopter.hasPiercingWarheads)
        {
            g2d.setColor(Colorations.golden);
            g2d.drawString(Window.dictionary.specialUpgrade(PIERCING_WARHEADS), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 50);
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
                g2d.drawString(Window.dictionary.secondAndThirdCannon(), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 75);
            }
            else
            {
                g2d.drawString(Window.dictionary.specialUpgrade(EXTRA_CANNONS), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 75);
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
                g2d.drawString(Window.dictionary.specialUpgrade(FIFTH_SPECIAL) + " (" + Window.dictionary.level() + " " + (helicopter.getUpgradeLevelOf(helicopter.getType() == PHOENIX ? FIREPOWER : FIRE_RATE)-1) + ")", STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 100);
            }
            else
            {
                g2d.drawString(Window.dictionary.specialUpgrade(FIFTH_SPECIAL), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 100);
            }
        }
        
        // Standard-Upgrades
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(20));
        g2d.drawString(Window.dictionary.standardUpgrades(), StandardUpgradeButtonType.OFFSET.x + 4, 82);
        g2d.setFont(fontProvider.getPlain(15));
        // TODO hier muss sich auf die Anzahl der Elemente im Enum StandardUpgradeButtonSize bezogen werden, am besten ein forEach über die Elemente
        
        StandardUpgradeButtonType.getValues()
                                 .forEach(buttonSpecifier -> Window.buttons.get(buttonSpecifier)
                                                                           .paint(g2d));
        
        // Message Box
        paintMessageFrame(g2d);
        
        // Spezial-Upgrades
        g2d.setColor(Color.yellow);
        g2d.setFont(fontProvider.getBold(20));
        g2d.drawString(Window.dictionary.specialUpgrades(), 774, 142);
        g2d.setFont(fontProvider.getPlain(15));
        
        SpecialUpgradeButtonType.getValues()
                                .forEach(buttonSpecifier -> Window.buttons.get(buttonSpecifier)
                                                                          .paint(g2d));
    }
    
    private static void paintMessageFrame(Graphics2D g2d)
    {
        paintFrame(g2d, 773, 11, 181, 98);
        g2d.setColor(Colorations.golden);
        g2d.setFont(fontProvider.getBold(14));
        for(int i = 0; i < Window.MESSAGE_LINE_COUNT; i++){g2d.drawString(Window.message[i], 785, 35 + i * 20); }
    }
}
