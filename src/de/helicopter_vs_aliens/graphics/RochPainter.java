package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.menu.MenuManager;
import de.helicopter_vs_aliens.model.helicopter.Roch;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Graphics2D;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;

public class RochPainter extends HelicopterPainter
{
    @Override
    void paintComponents(Graphics2D g2d, int left, int top)
    {
        Roch roch = (Roch) helicopter;
        super.paintComponents(g2d, left, top);
        if(roch.isPowerShieldActivated())
        {
            this.paintPowerShield(g2d, left, top);
        }
    }
    
    private void paintPowerShield(Graphics2D g2d, int left, int top)
    {
        g2d.setColor(Colorations.shieldColor[MenuManager.window == START_SCREEN ? NIGHT.ordinal() : Events.timeOfDay.ordinal()]);
        g2d.fillOval(left+(this.hasLeftMovingAppearance() ? -9 : 35), top+19, 96, 54);
    }
}
