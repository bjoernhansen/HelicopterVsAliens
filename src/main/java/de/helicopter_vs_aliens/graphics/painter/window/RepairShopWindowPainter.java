package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.gui.button.LeftSideRepairShopButtonType;
import de.helicopter_vs_aliens.gui.button.SpecialUpgradeButtonType;
import de.helicopter_vs_aliens.gui.button.StandardUpgradeButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;
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
    public void paint(GraphicsAdapter graphicsAdapter, Window window)
    {
        super.paint(graphicsAdapter, window);
        paintRepairShop(graphicsAdapter, helicopter);
    }
    
    private static void paintRepairShop(GraphicsAdapter graphicsAdapter, Helicopter helicopter)
    {
        // allgemeine Anzeigen
        graphicsAdapter.setPaint(Colorations.gradientVariableWhite);
        graphicsAdapter.setFont(fontProvider.getPlain(52));
        String inputString = Window.dictionary.repairShop();
        graphicsAdapter.drawString(inputString, 251 + (285 - graphicsAdapter.getFontMetrics().stringWidth(inputString))/2, 65);
        graphicsAdapter.setColor(Colorations.lightOrange);
        graphicsAdapter.setFont(fontProvider.getPlain(22));
        graphicsAdapter.drawString(String.format("%s: %d €", Window.dictionary.credit(), Events.money), 27, 35);
        graphicsAdapter.drawString(String.format("%s: %d", Window.dictionary.currentLevel(), Events.level), 562, 35);
        graphicsAdapter.setFont(fontProvider.getPlain(18));
        graphicsAdapter.drawString(String.format("%s: %s", Window.dictionary.playingTime(), Window.repairShopTime), 27, 75);
        
        // Helicopter-Anzeige
        paintHelicopterDisplay(graphicsAdapter, helicopter, 0, 10); //58
        
        // Reparatur-Button
        Window.buttons.get(LeftSideRepairShopButtonType.REPAIR).paint(graphicsAdapter);
        
        // Die Einsätze
        graphicsAdapter.setColor(Color.yellow);
        graphicsAdapter.setFont(fontProvider.getBold(20));
        graphicsAdapter.drawString(Window.dictionary.headlineMission(), 27, 382);
        
        Window.buttons.get(LeftSideRepairShopButtonType.MISSION).paint(graphicsAdapter);
        
        // Die Status-Leiste
        GraphicalEntities.paintFrame(graphicsAdapter, 251, 117, 285, 326);
        
        graphicsAdapter.setColor(Color.yellow);
        graphicsAdapter.setFont(fontProvider.getBold(20));
        graphicsAdapter.drawString(Window.dictionary.statusBar(), 255, 102);
        
        graphicsAdapter.setColor(Colorations.lightOrange);
        graphicsAdapter.setFont(fontProvider.getBold(16));
        graphicsAdapter.drawString(Window.dictionary.state(), STATUS_BAR_X1, STANDARD_UPGRADE_OFFSET_Y - 5);
        
        graphicsAdapter.setColor(helicopter.isDamaged ? Color.red : Color.green);
        graphicsAdapter.drawString(Window.dictionary.stateCondition(helicopter.isDamaged), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y - 5);
        
        // Standard-Upgrades
        for(StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
        {
            graphicsAdapter.setColor(Colorations.lightOrange);
            String tempString = Window.dictionary.standardUpgradeName(standardUpgradeType);
            graphicsAdapter.drawString(tempString + ":",
                STATUS_BAR_X1,
                STANDARD_UPGRADE_OFFSET_Y + 25 + standardUpgradeType.ordinal() * 25);
            
            if((standardUpgradeType != ENERGY_ABILITY && helicopter.hasMaximumUpgradeLevelFor(standardUpgradeType)
                || ( standardUpgradeType == ENERGY_ABILITY
                && helicopter.hasMaximumUpgradeLevelFor(ENERGY_ABILITY)
                && !(helicopter.getType() == HelicopterType.OROCHI
                && !helicopter.hasMaximumUpgradeLevelFor(MISSILE_DRIVE)))))
            {
                graphicsAdapter.setColor(Colorations.golden);
            }
            else{graphicsAdapter.setColor(Color.white);}
            if(standardUpgradeType == ENERGY_ABILITY && helicopter.getType() == HelicopterType.OROCHI)
            {
                graphicsAdapter.drawString(Window.dictionary.level() + " " + helicopter.getUpgradeLevelOf(standardUpgradeType) + " / " + (helicopter.getUpgradeLevelOf(MISSILE_DRIVE)-1), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y + 150);
            }
            else{graphicsAdapter.drawString(Window.dictionary.level() + " " + helicopter.getUpgradeLevelOf(standardUpgradeType), STATUS_BAR_X2, STANDARD_UPGRADE_OFFSET_Y + 25 + standardUpgradeType.ordinal() * 25);}
        }
        
        // Spezial-Upgrades
        // TODO überprüfen, ob iterieren über eine Schliefe möglich ist
        if(helicopter.hasSpotlights)
        {
            graphicsAdapter.setColor(Colorations.golden);
            graphicsAdapter.drawString(Window.dictionary.specialUpgrade(SPOTLIGHT), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y);
        }
        if(helicopter.hasGoliathPlating())
        {
            graphicsAdapter.setColor(Colorations.golden);
            graphicsAdapter.drawString(Window.dictionary.specialUpgrade(GOLIATH_PLATING), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 25);
        }
        if(helicopter.hasPiercingWarheads)
        {
            graphicsAdapter.setColor(Colorations.golden);
            graphicsAdapter.drawString(Window.dictionary.specialUpgrade(PIERCING_WARHEADS), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 50);
        }
        if(helicopter.numberOfCannons >= 2)
        {
            if(helicopter.getType() == HelicopterType.OROCHI && helicopter.numberOfCannons == 2)
            {
                graphicsAdapter.setColor(Color.white);
            }
            else{graphicsAdapter.setColor(Colorations.golden);}
            if(helicopter.numberOfCannons == 3)
            {
                graphicsAdapter.drawString(Window.dictionary.secondAndThirdCannon(), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 75);
            }
            else
            {
                graphicsAdapter.drawString(Window.dictionary.specialUpgrade(EXTRA_CANNONS), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 75);
            }
        }
        if(helicopter.hasFifthSpecial())
        {
            // TODO String zusammenbauen und dann einmal graphicsAdapter.drawString (auch oben)
            graphicsAdapter.setColor(Colorations.golden);
            if(helicopter.getType() == HelicopterType.PHOENIX || helicopter.getType() == HelicopterType.PEGASUS)
            {
                if(!helicopter.isFifthSpecialOnMaximumStrength()){graphicsAdapter.setColor(Color.white);}
                // TODO diese Fallunterscheidung in Methoden auslagern (überschreiben in PHOENIX und PEGASUS)
                graphicsAdapter.drawString(Window.dictionary.specialUpgrade(FIFTH_SPECIAL) + " (" + Window.dictionary.level() + " " + (helicopter.getUpgradeLevelOf(helicopter.getType() == HelicopterType.PHOENIX ? FIREPOWER : FIRE_RATE)-1) + ")", STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 100);
            }
            else
            {
                graphicsAdapter.drawString(Window.dictionary.specialUpgrade(FIFTH_SPECIAL), STATUS_BAR_X1, SPECIAL_UPGRADE_OFFSET_Y + 100);
            }
        }
        
        // Standard-Upgrades
        graphicsAdapter.setColor(Color.yellow);
        graphicsAdapter.setFont(fontProvider.getBold(20));
        graphicsAdapter.drawString(Window.dictionary.standardUpgrades(), StandardUpgradeButtonType.OFFSET.x + 4, 82);
        graphicsAdapter.setFont(fontProvider.getPlain(15));
        
        StandardUpgradeButtonType.getValues()
                                 .forEach(buttonSpecifier -> Window.buttons.get(buttonSpecifier)
                                                                           .paint(graphicsAdapter));
        
        // Message Box
        paintMessageFrame(graphicsAdapter);
        
        // Spezial-Upgrades
        graphicsAdapter.setColor(Color.yellow);
        graphicsAdapter.setFont(fontProvider.getBold(20));
        graphicsAdapter.drawString(Window.dictionary.specialUpgrades(), 774, 142);
        graphicsAdapter.setFont(fontProvider.getPlain(15));
        
        SpecialUpgradeButtonType.getValues()
                                .forEach(buttonSpecifier -> Window.buttons.get(buttonSpecifier)
                                                                          .paint(graphicsAdapter));
    }
    
    private static void paintMessageFrame(GraphicsAdapter graphicsAdapter)
    {
        GraphicalEntities.paintFrame(graphicsAdapter, 773, 11, 181, 98);
        graphicsAdapter.setColor(Colorations.golden);
        graphicsAdapter.setFont(fontProvider.getBold(14));
        for(int i = 0; i < Window.MESSAGE_LINE_COUNT; i++){graphicsAdapter.drawString(Window.message[i], 785, 35 + i * 20); }
    }
}
