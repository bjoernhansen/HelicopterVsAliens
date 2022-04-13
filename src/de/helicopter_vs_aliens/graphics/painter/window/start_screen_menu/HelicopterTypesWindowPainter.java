package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.util.Colorations;

public class HelicopterTypesWindowPainter extends StartScreenMenuWindowPainter
{
    @Override
    void paintStartScreenMenu(GraphicsAdapter graphicsAdapter)
    {
        super.paintStartScreenMenu(graphicsAdapter);
    
        if(Window.page.ordinal() > 1 && Window.page.ordinal() < 2 + HelicopterType.size())
        {
            paintHelicopterInStartScreenMenu(graphicsAdapter);
        }
        else if(Window.page == StartScreenMenuButtonType.BUTTON_2)
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
                        graphicsAdapter.setColor(Colorations.golden);
                        tempString = Window.dictionary.standardUpgradeName(standardUpgradeType);
                    }
                    else if(j != 0 && i == 0)
                    {
                        graphicsAdapter.setColor(Colorations.brightenUp(helicopterType.getStandardPrimaryHullColor()));
                        tempString = Window.dictionary.helicopterName(helicopterType);
                    }
                    else if(i != 0)
                    {
                        PriceLevel upgradeCosts = helicopterType.getPriceLevelFor(standardUpgradeType);
                        graphicsAdapter.setColor(upgradeCosts.getColor());
                        tempString = Window.dictionary.priceLevel(upgradeCosts);
                    }
                    if(tempString == null) tempString = "Erwischt!";
                    graphicsAdapter.drawString(tempString, 200 + (j-1) * 135, 140 + (i == 0 ? 0 : 5) + (i-1) * 32);
                }
            }
        }
    }
}
