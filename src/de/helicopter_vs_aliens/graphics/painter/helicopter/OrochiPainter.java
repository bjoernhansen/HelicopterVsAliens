package de.helicopter_vs_aliens.graphics.painter.helicopter;

import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.model.helicopter.Orochi;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

public class OrochiPainter extends HelicopterPainter
{
    @Override
    Color getInputColorCannon()
    {
        Orochi orochi = (Orochi) helicopter;
        if (orochi.isNextMissileStunner() && orochi.hasEnoughEnergyForAbility())
        {
            return Colorations.variableBlue;
        }
        return super.getInputColorCannon();
    }
    
    @Override
    void paintCannons(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        super.paintCannons(graphicsAdapter, left, top);
        if (helicopter.numberOfCannons == 3)
        {
            graphicsAdapter.setPaint(this.getGradientCannon2and3());
            graphicsAdapter.fillRoundRect(left + (this.hasLeftMovingAppearance() ? 38 : 37), top + 41, 47, 6, 6, 6);
            graphicsAdapter.setPaint(this.getGradientCannonHole());
            graphicsAdapter.fillOval(left + (this.hasLeftMovingAppearance() ? 39 : 80), top + 42, 3, 4);
        }
    }
}
