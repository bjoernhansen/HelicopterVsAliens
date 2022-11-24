package de.helicopter_vs_aliens.graphics.painter.helicopter;

import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.Kamaitachi;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;


public class KamaitachiPainter extends HelicopterPainter
{
    @Override
    Color getInputColorCannon()
    {
        Kamaitachi kamaitachi = (Kamaitachi) helicopter;
        if(kamaitachi.getPlasmaActivationTimer() > Helicopter.POWER_UP_FADE_TIME)
        {
            return Color.green;
        }
        else if(kamaitachi.getPlasmaActivationTimer() == 0)
        {
            return super.getInputColorCannon();
        }
        return kamaitachi.isInvincible()
            ? Colorations.reversedRandomGreen()
            : Colorations.variableGreen;
    }
    
    @Override
    GradientPaint getGradientCannonHoleColor()
    {
        Kamaitachi kamaitachi = (Kamaitachi) helicopter;
        return kamaitachi.getPlasmaActivationTimer() == 0
            ? this.getGradientHull()
            : Colorations.cannonHoleGreen;
    }
}
