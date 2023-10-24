package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.PowerUpPainter;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.util.Colorations;
import de.helicopter_vs_aliens.util.dictionary.Dictionary;
import de.helicopter_vs_aliens.util.dictionary.Language;

import java.awt.Point;
import java.util.List;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;
import static de.helicopter_vs_aliens.gui.window.Window.language;


public class DescriptionWindowPainter extends StartScreenMenuWindowPainter
{
    private static final int
        Y_OFFSET = 35;

    private static final int
        TEXT_OFFSET = 18;

    private static final Point
        POSITION = new Point(52, 120);

    public static final int
        POWER_UP_DURATION_IN_SECONDS = Helicopter.POWER_UP_DURATION / 60;


    @Override
    void paintStartScreenMenu(GraphicsAdapter graphicsAdapter)
    {
        super.paintStartScreenMenu(graphicsAdapter);
        
        if(Window.page == StartScreenMenuButtonType.BUTTON_6)
        {
            graphicsAdapter.setFont(fontProvider.getBold(17));
            graphicsAdapter.setColor(Colorations.lightestGray);
            String firstLine = getFirstLine();
            graphicsAdapter.drawString(firstLine, POSITION.x, POSITION.y - Y_OFFSET + TEXT_OFFSET);
            
            PowerUpPainter powerUpPainter = GraphicsManager.getInstance()
                                                           .getPainter(PowerUp.class);
            List<String> powerUpTextLines = getPowerUpTextLines();
            
            for (PowerUpType powerUpType : PowerUpType.getValues())
            {
                int positionY = POSITION.y + powerUpType.getMenuPosition() * Y_OFFSET;
                powerUpPainter.paint(
                    graphicsAdapter,
                    POSITION.x,
                    positionY,
                    Window.POWER_UP_SIZE,
                    Window.POWER_UP_SIZE,
                    powerUpType.getSurfaceColor(),
                    powerUpType.getCrossColor());

                graphicsAdapter.setColor(Colorations.lightestGray);

                graphicsAdapter.drawString(
                    powerUpTextLines.get(powerUpType.getMenuPosition()),
                    POSITION.x + 50,
                    positionY + TEXT_OFFSET);
            }
        }
    }
    
    private static List<String> getPowerUpTextLines()
    {
        Dictionary dictionary = Window.dictionary;
        String prepositionFor = dictionary.prepositionFor();
        
        return List.of(
            dictionary.bonusCredit(),
            dictionary.unlimitedEnergy() + " " + prepositionFor + " " + POWER_UP_DURATION_IN_SECONDS + " " + dictionary.seconds(),
            dictionary.partialRepairs(),
            Helicopter.INVULNERABILITY_DAMAGE_REDUCTION + "% " + dictionary.indestructibility() + " " + prepositionFor + " " + POWER_UP_DURATION_IN_SECONDS + " " + dictionary.seconds(),
            dictionary.tripleDamage() + " " + prepositionFor + " " + POWER_UP_DURATION_IN_SECONDS + " " + dictionary.seconds(),
            dictionary.increasedFireRate() + " " + prepositionFor + " " + POWER_UP_DURATION_IN_SECONDS + " " + dictionary.seconds());
    }
    
    private String getFirstLine()
    {
        if(language == Language.ENGLISH)
        {
            return "After destruction, some opponents drop one of the " + PowerUpType.valueCount() + " following power-ups:";
        }
        return "Einige Gegner verlieren nach Ihrem Abschuss eines der " + PowerUpType.valueCount() + " folgenden PowerUps:";
    }
}
