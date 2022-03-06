package de.helicopter_vs_aliens.graphics.painter.helicopter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.model.helicopter.Phoenix;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Graphics2D;

public class PhoenixPainter extends HelicopterPainter
{
    @Override
    void paintComponents(Graphics2D g2d, Graphics2DAdapter graphics2DAdapter, int left, int top)
    {
        // Nahkampf-Bestrahlung
        Phoenix phoenix = (Phoenix) helicopter;
        if (phoenix.hasShortrangeRadiation())
        {
            g2d.setColor(phoenix.getEnhancedRadiationTimer() == 0
                ? Colorations.radiation[Events.timeOfDay.ordinal()]
                : Colorations.enhancedRadiation[Events.timeOfDay.ordinal()]);
            g2d.fillOval(left + (this.hasLeftMovingAppearance() ? -9 : 35), top + 19, 96, 54);
        }
        super.paintComponents(g2d, graphics2DAdapter, left, top);
    }
}
