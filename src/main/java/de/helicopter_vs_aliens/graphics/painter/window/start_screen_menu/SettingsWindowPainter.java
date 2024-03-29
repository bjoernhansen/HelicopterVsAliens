package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;


public class SettingsWindowPainter extends StartScreenMenuWindowPainter
{
    private static final int
        LEFT = 80;
    private static final int
        COLUMN_SPACING = 145;

    private static final int
        LINE_SPACING = 40;

    private static final int
        TOP = 130;


    @Override
    void paintStartScreenMenu(GraphicsAdapter graphicsAdapter)
    {
        super.paintStartScreenMenu(graphicsAdapter);

        graphicsAdapter.setFont(fontProvider.getPlain(18));
        graphicsAdapter.setColor(Colorations.lightestGray);

        for(int i = 0; i < Window.NUMBER_OF_SETTING_OPTIONS; i++)
        {
            graphicsAdapter.drawString(Window.dictionary.settingOption(i), LEFT, TOP + i * LINE_SPACING);
        }

        graphicsAdapter.setColor(Audio.isSoundOn ? Color.green : Color.red);
        graphicsAdapter.drawString(Window.dictionary.activationState(Audio.isSoundOn), LEFT + COLUMN_SPACING, TOP);
        if(Audio.MICHAEL_MODE && Audio.isSoundOn)
        {
            graphicsAdapter.setColor(Colorations.golden);
            graphicsAdapter.drawString("(" + (Audio.standardBackgroundMusic ? "Classic" : "Michael" + Window.dictionary.modeSuffix()) + ")", LEFT + COLUMN_SPACING + 25, TOP);
        }

        graphicsAdapter.setColor(Colorations.golden);
        graphicsAdapter.drawString(Window.language.getNativeName(), LEFT + COLUMN_SPACING, TOP + LINE_SPACING);

        if(Window.page == StartScreenMenuButtonType.BUTTON_3)
        {
            graphicsAdapter.setColor(Color.white);
        }
        graphicsAdapter.drawString(Events.currentPlayerName, LEFT + COLUMN_SPACING, TOP + 2 * LINE_SPACING);

        if(Window.page == StartScreenMenuButtonType.BUTTON_3 && (gameRessourceProvider.getGameLoopCount() / 30) % 2 == 0)
        {
            graphicsAdapter.drawString("|", LEFT + COLUMN_SPACING + graphicsAdapter.getStringWidth(Events.currentPlayerName), TOP + 2 * LINE_SPACING);
        }
    }

    @Override
    String getHeadline()
    {
        return Window.dictionary.settings();
    }
}
