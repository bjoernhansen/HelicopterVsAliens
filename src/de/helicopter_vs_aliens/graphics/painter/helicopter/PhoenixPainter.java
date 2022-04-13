package de.helicopter_vs_aliens.graphics.painter.helicopter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.helicopter.Phoenix;
import de.helicopter_vs_aliens.util.Colorations;


public class PhoenixPainter extends HelicopterPainter
{
    @Override
    void paintComponents(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        // Nahkampf-Bestrahlung
        Phoenix phoenix = (Phoenix) helicopter;
        if (phoenix.hasShortrangeRadiation())
        {
            graphicsAdapter.setColor(phoenix.getEnhancedRadiationTimer() == 0
                ? Colorations.radiation[Events.timeOfDay.ordinal()]
                : Colorations.enhancedRadiation[Events.timeOfDay.ordinal()]);
            graphicsAdapter.fillOval(left + (this.hasLeftMovingAppearance() ? -9 : 35), top + 19, 96, 54);
        }
        super.paintComponents(graphicsAdapter, left, top);
    }
}
