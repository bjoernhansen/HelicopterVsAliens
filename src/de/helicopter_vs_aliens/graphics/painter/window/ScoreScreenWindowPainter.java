package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.gui.button.StartScreenSubCancelButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Graphics2D;

import static de.helicopter_vs_aliens.control.Events.MAXIMUM_LEVEL;
import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;
import static de.helicopter_vs_aliens.util.dictionary.Language.ENGLISH;

public class ScoreScreenWindowPainter extends WindowPainter
{
    private static final int
        SPACE_BETWEEN_ROWS = 30,
        X_POS_1 = 351,
        X_POS_2 = 633,
        Y_POS = 129;
    
    @Override
    public void paint(Graphics2D g2d, Window window)
    {
        super.paint(g2d, window);
        paintScoreScreen(g2d, helicopter);
    }
    
    private static void paintScoreScreen(Graphics2D g2d, Helicopter helicopter)
    {
        // Helikopter-Anzeige
        paintHelicopterDisplay(g2d, helicopter, 0, 5);
        
        g2d.setPaint(Colorations.gradientVariableWhite);
        g2d.setFont(fontProvider.getPlain(60));
        String temporaryString = Window.dictionary.gameStatistics();
        g2d.drawString((temporaryString), (981-g2d.getFontMetrics().stringWidth(temporaryString))/2, 65);
    
        GraphicalEntities.paintFrame(g2d, 619, 90, 376, 298);
        GraphicalEntities.paintFrameLine(g2d, 621, 140, 372);
        GraphicalEntities.paintFrameLine(g2d, 621, 249, 372);
        GraphicalEntities.paintFrame(g2d, 297, 90, 250, 200);
        
        
        Window.buttons.get(StartScreenSubCancelButtonType.CANCEL).paint(g2d);
        
        if(Events.level > MAXIMUM_LEVEL)
        {
            g2d.setColor(Color.green);
            if(Window.language == ENGLISH)
            {
                g2d.drawString("Mission completed in " + Window.minuten(helicopter.scoreScreenTimes[4]) + "!", X_POS_2, Y_POS -9);
            }
            else
            {
                g2d.drawString("Mission in " + Window.minuten(helicopter.scoreScreenTimes[4]) + " erfüllt!", X_POS_2, Y_POS -9);
            }
        }
        else
        {
            g2d.setColor(Color.red);
            if(Window.language == ENGLISH)
            {
                g2d.drawString("Mission failed after " + Window.minuten(helicopter.scoreScreenTimes[4]) + " in level " + Events.level + "!", X_POS_2, Y_POS - 9);
            }
            else
            {
                g2d.drawString("Mission nach " + Window.minuten(helicopter.scoreScreenTimes[4]) + " in Level " + Events.level + " gescheitert!", X_POS_2, Y_POS - 9);
            }
        }
        g2d.setColor(Color.white);
        g2d.drawString((Window.language == ENGLISH ? "Playing time per boss: " : "Spielzeit pro Boss: "), X_POS_1 - 20, Y_POS - 9);
        
        // TODO magic number entfernen
        for(int i = 0; i < 5; i++)
        {
            if(i < (Events.level-1)/10)
            {
                g2d.setColor(Color.green);
                g2d.drawString(Window.minuten(i == 0 ? helicopter.scoreScreenTimes[0] : helicopter.scoreScreenTimes[i] - helicopter.scoreScreenTimes[i-1]) + " (Boss " + (i+1) + ")", X_POS_1, Y_POS - 9 + SPACE_BETWEEN_ROWS * (i+1));
            }
            else
            {
                g2d.setColor(Color.red);
                g2d.drawString((Window.language == ENGLISH ? "undefeated" : "nicht besiegt") + " (Boss " + (i+1) + ")", X_POS_1, Y_POS - 9 + SPACE_BETWEEN_ROWS * (i+1));
            }
        }
        
        g2d.setColor(Color.white);
        // TODO hier dictionary einsetzen und loop erzeugen
        g2d.drawString((Window.language == ENGLISH ? "Crash landings: " : "Bruchlandungen: ")
                + helicopter.numberOfCrashes,
            X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 1 + 11);
        g2d.drawString((Window.language == ENGLISH ? "Repairs: " : "Reparaturen: ")
                + helicopter.numberOfRepairs,
            X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 2 + 11);
        g2d.drawString((Window.language == ENGLISH ? "Overall earnings: " : "Gesamt-Sold: ")
                +  Events.overallEarnings + " €",
            X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 3 + 11);
        
        int percentage = Events.bonusIncomePercentage();
        g2d.setColor(Colorations.scorescreen[0]);
        g2d.drawString((Window.language == ENGLISH ? "Additional income due to extra boni: " : "Zusätzliche Einnahmen durch Extra-Boni: +") + percentage + "%", X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 5);
        
        percentage = Calculations.percentage(helicopter.numberOfEnemiesKilled, helicopter.numberOfEnemiesSeen);
        g2d.setColor(Colorations.scorescreen[1]);
        g2d.drawString((Window.language == ENGLISH ? "Defeated enemies: " : "Besiegte Gegner: ") + helicopter.numberOfEnemiesKilled + (Window.language == ENGLISH ? " of " : " von ") + helicopter.numberOfEnemiesSeen + " (" + percentage + "%)", X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 6);
        
        percentage = Calculations.percentage(helicopter.numberOfMiniBossKilled, helicopter.numberOfMiniBossSeen);
        g2d.setColor(Colorations.scorescreen[2]);
        g2d.drawString((Window.language == ENGLISH ? "Defeated mini-bosses: " : "Besiegte Mini-Bosse: ") + helicopter.numberOfMiniBossKilled + (Window.language == ENGLISH ? " of " : " von ") + helicopter.numberOfMiniBossSeen + " (" + percentage + "%)", X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 7);
        
        percentage = (Calculations.percentage(helicopter.hitCounter, helicopter.missileCounter));
        g2d.setColor(Colorations.scorescreen[3]);
        g2d.drawString((Window.language == ENGLISH ? "Hit rate: " : "Raketen-Trefferquote: ") + percentage + "%", X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 8); //Zielsicherheit
    }
}
