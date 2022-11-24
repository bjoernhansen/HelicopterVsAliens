package de.helicopter_vs_aliens.graphics.painter.helicopter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.helicopter.Roch;
import de.helicopter_vs_aliens.util.Colorations;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;


public class RochPainter extends HelicopterPainter
{
    @Override
    void paintComponents(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        Roch roch = (Roch) helicopter;
        super.paintComponents(graphicsAdapter, left, top);
        if(roch.isPowerShieldActivated())
        {
            this.paintPowerShield(graphicsAdapter, left, top);
        }
    }
    
    private void paintPowerShield(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        graphicsAdapter.setColor(Colorations.shieldColor[WindowManager.window == START_SCREEN ? NIGHT.ordinal() : Events.timeOfDay.ordinal()]);
        graphicsAdapter.fillOval(left+(this.hasLeftMovingAppearance() ? -9 : 35), top+19, 96, 54);
    }
}
