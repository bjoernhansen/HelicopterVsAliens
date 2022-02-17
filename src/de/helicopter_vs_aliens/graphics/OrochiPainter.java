package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.model.helicopter.Kamaitachi;
import de.helicopter_vs_aliens.model.helicopter.Orochi;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;

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
    void paintCannons(Graphics2D g2d, int left, int top)
    {
        super.paintCannons(g2d, left, top);
        if (helicopter.numberOfCannons == 3)
        {
            g2d.setPaint(this.getGradientCannon2and3());
            g2d.fillRoundRect(left + (this.hasLeftMovingAppearance() ? 38 : 37), top + 41, 47, 6, 6, 6);
            g2d.setPaint(this.getGradientCannonHole());
            g2d.fillOval(left + (this.hasLeftMovingAppearance() ? 39 : 80), top + 42, 3, 4);
        }
    }
}
