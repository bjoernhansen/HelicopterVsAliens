package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.gui.button.StartScreenSubButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Graphics2D;

public class HelicopterTypesWindowPainter extends StartScreenMenuWindowPainter
{
    @Override
    void paintStartScreenMenu(Graphics2D g2d)
    {
        super.paintStartScreenMenu(g2d);
    
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
}
