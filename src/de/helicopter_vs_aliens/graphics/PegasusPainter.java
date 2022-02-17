package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.helicopter.Kamaitachi;
import de.helicopter_vs_aliens.model.helicopter.Pegasus;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;

import static de.helicopter_vs_aliens.gui.WindowType.STARTSCREEN;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.PEGASUS;

public class PegasusPainter extends HelicopterPainter
{
    private static final int[]
        INTERPHASE_GENERATOR_ALPHA = {110, 70}; // Alpha-Wert zum Zeichnen des Helikopters bei Tag- und Nachtzeit nach einem Dimensionssprung
    
    @Override
    void determineInputColors()
    {
        Pegasus pegasus = (Pegasus) helicopter;
        super.determineInputColors();
        if(!pegasus.isInPhase())
        {
            // TODO HashMap daraus machen und hier verwenden, bzw. EnumMap und dann Enum anlegen für die InputColors
            this.inputColorCannon = Colorations.setAlpha(this.inputColorCannon, INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputColorHull =   Colorations.setAlpha(this.inputColorHull, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputColorWindow = Colorations.setAlpha(this.inputColorWindow, INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()] );
            this.inputColorFuss1 =  Colorations.setAlpha(this.inputColorFuss1, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputColorFuss2 =  Colorations.setAlpha(this.inputColorFuss2, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputGray = 		Colorations.setAlpha(this.inputGray, 		INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputLightGray = 	Colorations.setAlpha(this.inputLightGray, 	INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
            this.inputLamp = 		Colorations.setAlpha(this.inputLamp, 		INTERPHASE_GENERATOR_ALPHA[Events.timeOfDay.ordinal()]);
        }
    }
    
    @Override
    void paintComponents(Graphics2D g2d, int left, int top)
    {
        Pegasus pegasus = (Pegasus) helicopter;
        super.paintComponents(g2d, left, top);
        
        // EMP wave animation in start menu
        if(de.helicopter_vs_aliens.gui.Menu.window == STARTSCREEN
            // TODO vermutlich unnötig
            && Menu.effectTimer[PEGASUS.ordinal()] > 0
            && pegasus.empWave != null)
        {
            if(pegasus.empWave.time >= pegasus.empWave.maxTime)
            {
                pegasus.empWave = null;
            }
            else
            {
                pegasus.empWave.update();
                pegasus.empWave.paint(g2d);
            }
        }
    }
}
