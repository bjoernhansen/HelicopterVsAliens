package de.helicopter_vs_aliens.graphics.painter.helicopter;

import de.helicopter_vs_aliens.control.BossLevel;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;
import de.helicopter_vs_aliens.util.dictionary.Dictionary;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;

import static de.helicopter_vs_aliens.control.TimeOfDay.DAY;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.WindowType.GAME;
import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;
import static de.helicopter_vs_aliens.model.helicopter.Helicopter.HELICOPTER_MENU_PAINT_POS;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.HELIOS;

public abstract class HelicopterPainter extends Painter<Helicopter>
{
    private static final boolean
        SHOW_RED_FRAME = false;			// zu Testzwecken: zeichnet roten Rahmen um die Helicopter Collision-Bounds
    
    // Grundfarben zur Berechnung der Gradienten-Farben
    // TODO ggf. eigene Klasse für Farben einführen
    Color
        inputColorCannon,
        inputColorHull,
        inputColorWindow,
        inputColorFuss1,
        inputColorFuss2,
        inputGray,
        inputLightGray,
        inputLamp;
    
    // Gradienten-Farben
    private GradientPaint
        gradientHull, 					// Hauptfarbe des Helikopters
        gradientCannon1,				// Farbe der ersten Bordkanone
        gradientWindow, 				// Fensterfarbe
        gradientCannon2and3,			// Farbe der zweiten und dritten Bordkanone
        gradientFuss1, 					// Farben der Landekufen
        gradientFuss2,
        gradientCannonHole;				// Farbe der Bordkanonen-Öffnung
    
    Helicopter helicopter;
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Helicopter helicopter)
    {
        paint(graphicsAdapter, helicopter, helicopter.getPaintBounds().x, helicopter.getPaintBounds().y);
    }
    
    private void paint(GraphicsAdapter graphicsAdapter, Helicopter helicopter, int left, int top)
    {
        this.helicopter = helicopter;
        
        determineColors(left, top);
        paintComponents(graphicsAdapter, left, top);
        
        if (SHOW_RED_FRAME)
        {
            paintRedFrame(graphicsAdapter);
        }
    }
    
    private void paintRedFrame(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Color.red);
        graphicsAdapter.drawRectangle(helicopter.getBounds());
        graphicsAdapter.fillOval((int) helicopter.location.getX() - 2, (int) helicopter.location.getY() - 2, 4, 4);
    }
    
    private void determineColors(int left, int top)
    {
        determineInputColors();
        determineGradientColors(left, top);
    }
    
    void paintComponents(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        paintRotorHead(graphicsAdapter, left, top);
        paintSkids(graphicsAdapter, left, top);
        paintHull(graphicsAdapter, left, top);
        paintCannons(graphicsAdapter, left, top);
        paintSpotlights(graphicsAdapter, left, top);
        paintMainRotor(graphicsAdapter, left, top);
        paintTailRotor(graphicsAdapter, left, top);
    }
    
    private void paintRotorHead(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        graphicsAdapter.setColor(inputLightGray);
        graphicsAdapter.setStroke(new BasicStroke(2));
        graphicsAdapter.drawLine(left+(hasLeftMovingAppearance() ? 39 : 83), top+14, left+(hasLeftMovingAppearance() ? 39 : 83), top+29);
    }
    
    boolean hasLeftMovingAppearance()
    {
        return helicopter.isMovingLeft && WindowManager.window == GAME;
    }
    
    private void paintSkids(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        graphicsAdapter.setPaint(gradientFuss2);
        graphicsAdapter.fillRoundRect(left+(hasLeftMovingAppearance() ? 25 : 54), top+70, 43, 5, 5, 5);
        graphicsAdapter.setPaint(gradientFuss1);
        graphicsAdapter.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        graphicsAdapter.drawLine(left+61, top+66, left+61, top+69);
        graphicsAdapter.drawLine(left+(hasLeftMovingAppearance() ? 33 : 89), top+66, left+(hasLeftMovingAppearance() ? 33 : 89), top+69);
        graphicsAdapter.setStroke(new BasicStroke(1));
    }
    
    private void paintHull(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        graphicsAdapter.setPaint(gradientHull);
        graphicsAdapter.fillOval(left+(hasLeftMovingAppearance() ?  2 : 45), top+29, 75, 34);
        graphicsAdapter.fillRect(left+(hasLeftMovingAppearance() ? 92 : -7), top+31, 37, 8);
        graphicsAdapter.fillArc (left+(hasLeftMovingAppearance() ? 34 : 23), top+11, 65, 40, 180, 180);
        graphicsAdapter.setPaint(gradientWindow);
        graphicsAdapter.fillArc (left+(hasLeftMovingAppearance() ?  1 : 69), top+33, 52, 22, (hasLeftMovingAppearance() ? 75 : -15), 120);
    }
    
    private void paintSpotlights(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        if(helicopter.hasSpotlights)
        {
            if(Events.timeOfDay == NIGHT && WindowManager.window == GAME)
            {
                graphicsAdapter.setColor(Colorations.translucentWhite);
                graphicsAdapter.fillArc(left+(hasLeftMovingAppearance() ? -135 : -43), top-96, 300, 300, (hasLeftMovingAppearance() ? 165 : -15), 30);
            }
            graphicsAdapter.setPaint(gradientHull);
            graphicsAdapter.fillRect(left+(hasLeftMovingAppearance() ? 4 : 106), top+50, 12, 8);
            graphicsAdapter.setColor(inputLamp);
            graphicsAdapter.fillArc(left+(hasLeftMovingAppearance() ? -1 : 115), top+50, 8, 8, (hasLeftMovingAppearance() ? -90 : 90), 180);
        }
    }
    
    void paintCannons(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        graphicsAdapter.setPaint(gradientCannon1);
        graphicsAdapter.fillRoundRect(left+(hasLeftMovingAppearance() ? 26 : 53), top+52, 43, 13, 12, 12);
        graphicsAdapter.setPaint(gradientCannonHole);
        graphicsAdapter.fillOval(left+(hasLeftMovingAppearance() ? 27 : 90), top+54, 5, 9);
        if(helicopter.numberOfCannons >= 2)
        {
            graphicsAdapter.setPaint(gradientCannon2and3);
            graphicsAdapter.fillRoundRect(left+(hasLeftMovingAppearance() ? 32 : 27), top+27, 63, 6, 6, 6);
            graphicsAdapter.setPaint(gradientCannonHole);
            graphicsAdapter.fillOval(left+(hasLeftMovingAppearance() ? 33 : 86), top+28, 3, 4);
        }
    }
    
    private void determineGradientColors(int left, int top)
    {
        gradientHull = new GradientPaint(0, top-10, Colorations.adjustBrightness(inputColorHull, 1.65f),
                                              0, top+ 2, Colorations.adjustBrightness(inputColorHull, 0.75f), true);
        gradientCannon1 = new GradientPaint(0, top+56, Colorations.adjustBrightness(inputColorCannon, 1.65f),
                                                 0, top+64, Colorations.adjustBrightness(inputColorCannon, 0.55f), true);
        gradientWindow = new GradientPaint(0, top-10, Colorations.adjustBrightness(inputColorWindow, 2.2f),
                                                0, top+ 2, Colorations.adjustBrightness(inputColorWindow, 0.70f), true);
        gradientCannon2and3 = new GradientPaint(0, top+28, Colorations.adjustBrightness(inputColorCannon, 1.7f),
                                                     0, top+35, Colorations.adjustBrightness(inputColorCannon, 0.4f), true);
        gradientFuss1 = new GradientPaint(left+61, 0, inputColorFuss1, left+68, 0, Colorations.adjustBrightness(
            inputColorFuss1, 0.44f), true);
        gradientFuss2 = new GradientPaint(0, top+72, inputColorFuss2, 0, top+76, Colorations.adjustBrightness(
            inputColorFuss2, 0.55f), true);
        gradientCannonHole = getGradientCannonHoleColor();
    }
    
    GradientPaint getGradientCannonHoleColor()
    {
        return gradientHull;
    }
    
    void determineInputColors()
    {
        // TODO müssen (außer den ersten 3) wirklich alle jedes mal neu gesetzt werden?)
        inputColorCannon = getInputColorCannon();
        inputColorHull = getInputColorHull();
        inputColorWindow = getInputColorWindow();
        inputColorFuss1 = Colorations.lighterGray;
        inputColorFuss2 = Colorations.enemyGray;
        inputGray = Colorations.gray;
        inputLightGray = Colorations.lightGray;
        inputLamp = helicopter.hasSpotlightsTurnedOn() ? Colorations.randomLight : Colorations.darkYellow;
    }
    
    Color getInputColorCannon()
    {
        return helicopter.isInvincible()
            ? Colorations.variableGreen
            : helicopter.getSecondaryHullColor();
    }
    
    private Color getInputColorHull()
    {
        return helicopter.isInvincible()
            ? Colorations.variableGreen
            : helicopter.getPrimaryHullColor();
    }
    
    private Color getInputColorWindow()
    {
        return helicopter.hasTripleDamage() || helicopter.hasBoostedFireRate()
            ? Colorations.variableRed
            : Colorations.windowBlue;
    }
    
    private void paintMainRotor(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        paintRotor(graphicsAdapter,
            inputGray,
            left+(hasLeftMovingAppearance() ? -36 : 8),
            top-5,
            150, 37, 3,
            helicopter.rotorPosition,
            12,
            helicopter.isRotorSystemActive,
            false);
    }
    
    private void paintTailRotor(GraphicsAdapter graphicsAdapter, int left, int top)
    {
        paintRotor(graphicsAdapter,
            inputGray,
            left+(hasLeftMovingAppearance() ?  107 : -22),
            top+14,
            37, 37, 3,
            helicopter.rotorPosition,
            12,
            helicopter.isRotorSystemActive,
            false);
    }
    
    // TODO gehört in einen MenuPainter
    public void displayPaint(GraphicsAdapter graphicsAdapter, Helicopter helicopter, int left, int top)
    {
        paint(graphicsAdapter, helicopter, left, top);
    }
    
    
    // TODO gehört in einen MenuPainter
    public void startScreenSubPaint(GraphicsAdapter graphicsAdapter, Helicopter helicopter)
    {
        helicopter.rotatePropellerSlow();
        paint(graphicsAdapter, helicopter, HELICOPTER_MENU_PAINT_POS.x, HELICOPTER_MENU_PAINT_POS.y);
    }
    
    // TODO gehört in einen MenuPainter
    public void startScreenPaint(GraphicsAdapter graphicsAdapter, Helicopter helicopter, int left, int top)
    {
        paint(graphicsAdapter, helicopter, left, top);
        if(helicopter.getType().hasDefeatedFinalBoss() && WindowManager.window == START_SCREEN)
        {
            graphicsAdapter.setFont(Window.fontProvider.getBold(12));
            graphicsAdapter.setColor(Color.yellow);
            graphicsAdapter.drawString(Window.dictionary.recordTime(), left-27, top+67);
            graphicsAdapter.drawString(Window.dictionary.minutes(helicopter.getType().getRecordTime(BossLevel.FINAL_BOSS)), left-27, top+80);
        }
        
        if(helicopter.getType() == HELIOS && WindowManager.window == START_SCREEN)
        {
            graphicsAdapter.setFont(Window.fontProvider.getBold(12));
            graphicsAdapter.setColor(Colorations.brown);
            graphicsAdapter.drawString(Window.dictionary.specialMode(), left-27, top-4);
        }
    }
    
    // TODO sollte in Painter zu einer eigenen Rotor Klasse werden
    public static void paintRotor(GraphicsAdapter graphicsAdapter, Color color,
                                  int x, int y, int width, int height,
                                  int nrOfBlades, int pos, int bladeWidth,
                                  float borderDistance, boolean active)
    {
        int distanceX = (int) (borderDistance * width),
            distanceY = (int) (borderDistance * height);
        paintRotor(graphicsAdapter, color,
            x+distanceX,
            y+distanceY,
            width-2*distanceX,
            height-2*distanceY,
            nrOfBlades, pos, bladeWidth, active, true);
    }
    
    // TODO sollte in Painter zu einer eigenen Rotor Klasse werden
    private static void paintRotor(GraphicsAdapter graphicsAdapter, Color color,
                           int x, int y, int width, int height,
                           int numberOfBlades, int pos, int bladeWidth,
                           boolean active, boolean enemyPaint)
    {
        if(active)
        {
            graphicsAdapter.setColor((Events.timeOfDay == DAY || enemyPaint) ? Colorations.translucentGray : Colorations.translucentWhite);
            graphicsAdapter.fillOval(x, y, width, height);
        }
        graphicsAdapter.setColor(color);
        for(int i = 0; i < numberOfBlades; i++)
        {
            graphicsAdapter.fillArc(x, y, width, height, -10-pos+i*(360/ numberOfBlades), bladeWidth);
        }
    }
    
    GradientPaint getGradientHull()
    {
        return gradientHull;
    }
    
    GradientPaint getGradientCannon2and3()
    {
        return gradientCannon2and3;
    }
    
    GradientPaint getGradientCannonHole()
    {
        return gradientCannonHole;
    }
}