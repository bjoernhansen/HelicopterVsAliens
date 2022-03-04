package de.helicopter_vs_aliens.graphics.painter.window.start_screen_menu;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.gui.button.StartScreenSubButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.score.HighScoreEntry;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Graphics2D;

import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;

public class SettingsWindowPainter extends StartScreenMenuWindowPainter
{
    private static final int
        LEFT = 80,
        COLUMN_SPACING = 145,
        LINE_SPACING = 40,
        TOP = 130;
    
    @Override
    void paintStartScreenMenu(Graphics2D g2d)
    {
        super.paintStartScreenMenu(g2d);
    
        g2d.setFont(fontProvider.getPlain(18));
        g2d.setColor(Colorations.lightestGray);
    
        for(int i = 0; i < Window.NUMBER_OF_SETTING_OPTIONS; i++)
        {
            g2d.drawString(Window.dictionary.settingOption(i), LEFT, TOP + i * LINE_SPACING);
        }
    
        g2d.setColor(Colorations.golden);
        g2d.drawString( Window.dictionary.displayMode()
                + (!Main.isFullScreen ? "" : " ("
                + (Window.hasOriginalResolution
                ? Main.currentDisplayMode.getWidth()
                + "x"
                + Main.currentDisplayMode.getHeight()
                : "1280x720") + ")"),
            LEFT + COLUMN_SPACING,
            TOP);
    
        g2d.setColor(Audio.isSoundOn ? Color.green : Color.red);
        g2d.drawString( Window.dictionary.activationState(Audio.isSoundOn)						, LEFT + COLUMN_SPACING, TOP + LINE_SPACING);
        if(Audio.MICHAEL_MODE && Audio.isSoundOn)
        {
            g2d.setColor(Colorations.golden);
            g2d.drawString("(" + (Audio.standardBackgroundMusic ? "Classic" : "Michael" + Window.dictionary.modeSuffix()) + ")", LEFT + COLUMN_SPACING + 25, TOP + LINE_SPACING);
        }
    
        g2d.setColor(Controller.antialiasing ? Color.green : Color.red);
        g2d.drawString(Window.dictionary.activationState(Controller.antialiasing)			, LEFT + COLUMN_SPACING, TOP + 2 * LINE_SPACING);
    
        g2d.setColor(Colorations.golden);
        g2d.drawString(Window.language.getNativeName(), LEFT + COLUMN_SPACING, TOP + 3 * LINE_SPACING);
    
        if(Window.page == StartScreenSubButtonType.BUTTON_5){g2d.setColor(Color.white);}
        g2d.drawString(HighScoreEntry.currentPlayerName, LEFT + COLUMN_SPACING, TOP + 4 * LINE_SPACING);
    
        if(Window.page == StartScreenSubButtonType.BUTTON_5 && (controller.framesCounter/30)%2 == 0){g2d.drawString("|", LEFT + COLUMN_SPACING + g2d.getFontMetrics().stringWidth(HighScoreEntry.currentPlayerName), TOP + 4 * LINE_SPACING);}
    }
    
    @Override
    String getHeadline()
    {
        return Window.dictionary.settings();
    }
}
