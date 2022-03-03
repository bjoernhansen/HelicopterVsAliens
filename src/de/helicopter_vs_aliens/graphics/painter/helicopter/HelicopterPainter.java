package de.helicopter_vs_aliens.graphics.painter.helicopter;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.gui.menu.Menu;
import de.helicopter_vs_aliens.gui.menu.MenuManager;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;

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
    public void paint(Graphics2D g2d, Helicopter helicopter)
    {
        paint(g2d, helicopter, helicopter.getPaintBounds().x, helicopter.getPaintBounds().y);
    }
    
    private void paint(Graphics2D g2d, Helicopter helicopter, int left, int top)
    {
        this.helicopter = helicopter;
        
        this.determineColors(left, top);
        this.paintComponents(g2d, left, top);
        
        if (SHOW_RED_FRAME)
        {
            paintRedFrame(g2d);
        }
    }
    
    private void paintRedFrame(Graphics2D g2d)
    {
        g2d.setColor(Color.red);
        g2d.draw(helicopter.getBounds());
        g2d.fillOval((int) helicopter.location.getX() - 2, (int) helicopter.location.getY() - 2, 4, 4);
    }
    
    private void determineColors(int left, int top)
    {
        this.determineInputColors();
        this.determineGradientColors(left, top);
    }
    
    void paintComponents(Graphics2D g2d, int left, int top)
    {
        this.paintRotorHead(g2d, left, top);
        this.paintSkids(g2d, left, top);
        this.paintHull(g2d, left, top);
        this.paintCannons(g2d, left, top);
        this.paintSpotlights(g2d, left, top);
        this.paintMainRotor(g2d, left, top);
        this.paintTailRotor(g2d, left, top);
    }
    
    private void paintRotorHead(Graphics2D g2d, int left, int top)
    {
        g2d.setColor(this.inputLightGray);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(left+(this.hasLeftMovingAppearance() ? 39 : 83), top+14, left+(this.hasLeftMovingAppearance() ? 39 : 83), top+29);
    }
    
    boolean hasLeftMovingAppearance()
    {
        return helicopter.isMovingLeft && MenuManager.window == GAME;
    }
    
    private void paintSkids(Graphics2D g2d, int left, int top)
    {
        g2d.setPaint(this.gradientFuss2);
        g2d.fillRoundRect(left+(this.hasLeftMovingAppearance() ? 25 : 54), top+70, 43, 5, 5, 5);
        g2d.setPaint(this.gradientFuss1);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2d.drawLine(left+61, top+66, left+61, top+69);
        g2d.drawLine(left+(this.hasLeftMovingAppearance() ? 33 : 89), top+66, left+(this.hasLeftMovingAppearance() ? 33 : 89), top+69);
        g2d.setStroke(new BasicStroke(1));
    }
    
    private void paintHull(Graphics2D g2d, int left, int top)
    {
        g2d.setPaint(this.gradientHull);
        g2d.fillOval(left+(this.hasLeftMovingAppearance() ?  2 : 45), top+29, 75, 34);
        g2d.fillRect(left+(this.hasLeftMovingAppearance() ? 92 : -7), top+31, 37,  8);
        g2d.fillArc (left+(this.hasLeftMovingAppearance() ? 34 : 23), top+11, 65, 40, 180, 180);
        g2d.setPaint(this.gradientWindow);
        g2d.fillArc (left+(this.hasLeftMovingAppearance() ?  1 : 69), top+33, 52, 22, (this.hasLeftMovingAppearance() ? 75 : -15), 120);
    }
    
    private void paintSpotlights(Graphics2D g2d, int left, int top)
    {
        if(helicopter.hasSpotlights)
        {
            if(Events.timeOfDay == NIGHT && MenuManager.window == GAME)
            {
                g2d.setColor(Colorations.translucentWhite);
                g2d.fillArc(left+(this.hasLeftMovingAppearance() ? -135 : -43), top-96, 300, 300, (this.hasLeftMovingAppearance() ? 165 : -15), 30);
            }
            g2d.setPaint(this.gradientHull);
            g2d.fillRect(left+(this.hasLeftMovingAppearance() ? 4 : 106), top+50, 12, 8);
            g2d.setColor(this.inputLamp);
            g2d.fillArc(left+(this.hasLeftMovingAppearance() ? -1 : 115), top+50, 8, 8, (this.hasLeftMovingAppearance() ? -90 : 90), 180);
        }
    }
    
    void paintCannons(Graphics2D g2d, int left, int top)
    {
        g2d.setPaint(this.gradientCannon1);
        g2d.fillRoundRect(left+(this.hasLeftMovingAppearance() ? 26 : 53), top+52, 43, 13, 12, 12);
        g2d.setPaint(this.gradientCannonHole);
        g2d.fillOval(left+(this.hasLeftMovingAppearance() ? 27 : 90), top+54, 5, 9);
        if(helicopter.numberOfCannons >= 2)
        {
            g2d.setPaint(this.gradientCannon2and3);
            g2d.fillRoundRect(left+(this.hasLeftMovingAppearance() ? 32 : 27), top+27, 63, 6, 6, 6);
            g2d.setPaint(this.gradientCannonHole);
            g2d.fillOval(left+(this.hasLeftMovingAppearance() ? 33 : 86), top+28, 3, 4);
        }
    }
    
    private void determineGradientColors(int left, int top)
    {
        this.gradientHull = new GradientPaint(0, top-10, Colorations.dimColor(this.inputColorHull, 1.65f),
            0, top+ 2, Colorations.dimColor(this.inputColorHull, 0.75f), true);
        this.gradientCannon1 = new GradientPaint(0, top+56, Colorations.dimColor(this.inputColorCannon, 1.65f),
            0, top+64, Colorations.dimColor(this.inputColorCannon, 0.55f), true);
        this.gradientWindow = new GradientPaint(0, top-10, Colorations.dimColor(this.inputColorWindow, 2.2f),
            0, top+ 2, Colorations.dimColor(this.inputColorWindow, 0.70f), true);
        this.gradientCannon2and3 = new GradientPaint(0, top+28, Colorations.dimColor(this.inputColorCannon, 1.7f),
            0, top+35, Colorations.dimColor(this.inputColorCannon, 0.4f), true);
        this.gradientFuss1 = new GradientPaint(left+61, 0, this.inputColorFuss1, left+68, 0, Colorations.dimColor(this.inputColorFuss1, 0.44f), true);
        this.gradientFuss2 = new GradientPaint(0, top+72, this.inputColorFuss2, 0, top+76, Colorations.dimColor(this.inputColorFuss2, 0.55f), true);
        this.gradientCannonHole = this.getGradientCannonHoleColor();
    }
    
    GradientPaint getGradientCannonHoleColor()
    {
        return this.gradientHull;
    }
    
    void determineInputColors()
    {
        // TODO müssen (außer den ersten 3) wirklich alle jedes mal neu gesetzt werden?)
        this.inputColorCannon = this.getInputColorCannon();
        this.inputColorHull = this.getInputColorHull();
        this.inputColorWindow = this.getInputColorWindow();
        this.inputColorFuss1 = Colorations.lighterGray;
        this.inputColorFuss2 = Colorations.enemyGray;
        this.inputGray = Colorations.gray;
        this.inputLightGray = Colorations.lightGray;
        this.inputLamp = helicopter.hasSpotlightsTurnedOn() ? Colorations.randomLight : Colorations.darkYellow;
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
        return helicopter.hasTripleDmg() || helicopter.hasBoostedFireRate()
            ? Colorations.variableRed
            : Colorations.windowBlue;
    }
    
    private void paintMainRotor(Graphics2D g2d, int left, int top)
    {
        paintRotor(g2d,
            this.inputGray,
            left+(this.hasLeftMovingAppearance() ? -36 : 8),
            top-5,
            150, 37, 3,
            helicopter.rotorPosition,
            12,
            helicopter.isRotorSystemActive,
            false);
    }
    
    private void paintTailRotor(Graphics2D g2d, int left, int top)
    {
        paintRotor(g2d,
            this.inputGray,
            left+(this.hasLeftMovingAppearance() ?  107 : -22),
            top+14,
            37, 37, 3,
            helicopter.rotorPosition,
            12,
            helicopter.isRotorSystemActive,
            false);
    }
    
    // TODO gehört in einen MenuPainter
    public void displayPaint(Graphics2D g2d, Helicopter helicopter, int left, int top)
    {
        this.paint(g2d, helicopter, left, top);
    }
    
    
    // TODO gehört in einen MenuPainter
    public void startScreenSubPaint(Graphics2D g2d, Helicopter helicopter)
    {
        helicopter.rotatePropellerSlow();
        this.paint(g2d, helicopter, HELICOPTER_MENU_PAINT_POS.x, HELICOPTER_MENU_PAINT_POS.y);
    }
    
    // TODO gehört in einen MenuPainter
    public void startScreenPaint(Graphics2D g2d, Helicopter helicopter, int left, int top)
    {
        this.paint(g2d, helicopter, left, top);
        if(Events.recordTime[helicopter.getType().ordinal()][4] > 0 && MenuManager.window == START_SCREEN)
        {
            g2d.setFont(Menu.fontProvider.getBold(12));
            g2d.setColor(Color.yellow);
            g2d.drawString(Menu.dictionary.recordTime(), left-27, top+67);
            g2d.drawString(Menu.minuten(Events.recordTime[helicopter.getType().ordinal()][4]),left-27, top+80);
        }
        
        if(helicopter.getType() == HELIOS && MenuManager.window == START_SCREEN)
        {
            g2d.setFont(Menu.fontProvider.getBold(12));
            g2d.setColor(Colorations.brown);
            g2d.drawString(Menu.dictionary.specialMode(), left-27, top-4);
        }
    }
    
    // TODO sollte in Painter zu einer eigenen Rotor Klasse werden
    public static void paintRotor(Graphics2D g2d, Color color,
                                  int x, int y, int width, int height,
                                  int nrOfBlades, int pos, int bladeWidth,
                                  float borderDistance, boolean active)
    {
        int distanceX = (int) (borderDistance * width),
            distanceY = (int) (borderDistance * height);
        paintRotor(g2d, color,
            x+distanceX,
            y+distanceY,
            width-2*distanceX,
            height-2*distanceY,
            nrOfBlades, pos, bladeWidth, active, true);
    }
    
    // TODO sollte in Painter zu einer eigenen Rotor Klasse werden
    private static void paintRotor(Graphics2D g2d, Color color,
                           int x, int y, int width, int height,
                           int numberOfBlades, int pos, int bladeWidth,
                           boolean active, boolean enemyPaint)
    {
        if(active)
        {
            g2d.setColor((Events.timeOfDay == DAY || enemyPaint) ? Colorations.translucentGray : Colorations.translucentWhite);
            g2d.fillOval(x, y, width, height);
        }
        g2d.setColor(color);
        for(int i = 0; i < numberOfBlades; i++)
        {
            g2d.fillArc(x, y, width, height, -10-pos+i*(360/ numberOfBlades), bladeWidth);
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