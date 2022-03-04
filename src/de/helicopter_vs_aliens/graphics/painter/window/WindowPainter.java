package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;
import static de.helicopter_vs_aliens.gui.WindowType.REPAIR_SHOP;
import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;

public abstract class WindowPainter extends Painter<Window>
{
    private static final int
        UNLOCKED_DISPLAY_TIME = 300;
    
    protected static final int
        STANDARD_UPGRADE_OFFSET_Y = 148;                // y-Verschiebung der Standard-Upgrades in der Statusanzeige (Werkstatt-Menü)
    
    public static final Point
        HELICOPTER_START_SCREEN_OFFSET = new Point(66, 262),
        HEALTH_BAR_POSITION = new Point(5, RectangularGameEntity.GROUND_Y + 5);
    
    protected static Controller controller;
    protected static Helicopter helicopter;
    
    protected static void updateDependencies(){
        controller = Controller.getInstance();
        helicopter = controller.getHelicopter();
    }
    
    @Override
    public void paint(Graphics2D g2d, Window window)
    {
        updateDependencies();
    }
    
    static int unlockedDisplayPosition(int timer)
    {
        if(timer < 50)
        {
            return 789 + 5 * (50 - timer);
        }
        else if(timer > UNLOCKED_DISPLAY_TIME - 50)
        {
            return 789 + 5 * (timer - UNLOCKED_DISPLAY_TIME + 50);
        }
        else return 789;
    }
    
    protected static void paintHelicopterDisplay(Graphics2D g2d,
                                               Helicopter helicopter,
                                               int x, int y)
    {
        paintFrame(g2d, 26 + x,  85 + y, 200, 173, WindowManager.window  != GAME ? null : Colorations.lightestGray);
        g2d.setColor(Color.white);
        g2d.setFont(fontProvider.getBold(20));
        String typeName = Window.dictionary.typeName(helicopter.getType());
        g2d.drawString(typeName, 28 + x + (196-g2d.getFontMetrics().stringWidth(typeName))/2, 113 + y);
        
        HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(helicopter.getClass());
        helicopterPainter.displayPaint(g2d, helicopter, 59 + x, 141 + y);
        
        paintFrameLine(g2d, 28 + x, 126 + y, 196);
        paintFrameLine(g2d, 28 + x, 226 + y, 196);
        
        if(WindowManager.window  != GAME)
        {
            paintHealthBar(g2d, helicopter, 30 + x, 230 + y, 187, false);
        }
        else
        {
            g2d.setFont(fontProvider.getBold(18));
            if(Window.unlockedTimer > UNLOCKED_DISPLAY_TIME - 50)
            {
                g2d.setColor(Colorations.red);
                typeName = Window.dictionary.unavailable();
            }
            else
            {
                g2d.setColor(Colorations.darkArrowGreen);
                typeName = Window.dictionary.unlocked();
            }
            g2d.drawString(typeName, 28 + x + (196-g2d.getFontMetrics().stringWidth(typeName))/2, 249 + y);
        }
        
        if(WindowManager.window  == REPAIR_SHOP)
        {
            if(helicopter.isDamaged)
            {
                g2d.setColor(Color.red);
                g2d.setFont(fontProvider.getPlain(14));
                g2d.drawString(Window.dictionary.damaged(), 34 + x, 216 + y);
            }
            g2d.setFont(fontProvider.getBold(16));
            g2d.setColor(Colorations.plating);
            int percentPlating = (Math.round(100 * helicopter.getRelativePlating()));
            FontMetrics fm = g2d.getFontMetrics();
            int sw = fm.stringWidth(""+percentPlating);
            g2d.drawString(percentPlating + "%", 203 - sw + x, STANDARD_UPGRADE_OFFSET_Y + y + 69);
        }
    }
    
    protected static void paintHealthBar(Graphics2D g2d, Helicopter helicopter, int x, int y, int length, boolean rahmen)
    {
        float relativeEnergy = helicopter.getRelativeEnergy();
        float relativePlating = helicopter.getRelativePlating();
        if(rahmen)
        {
            g2d.setColor(Colorations.lightestGray);
            g2d.fillRect(x+1, y+1, length + 4, 23);
            g2d.setColor(Colorations.lightGray);
            g2d.fillRect(x+2, y+2, length+2, 10);
            g2d.fillRect(x+2, y+13, length+2, 10);
        }
        if(!helicopter.isEnergyAbilityActivatable())
        {
            g2d.setColor(Color.cyan);
        }
        else{g2d.setColor(helicopter.hasUnlimitedEnergy()
            ? Colorations.endlessEnergyViolet
            : Color.blue);}
        g2d.fillRect(x+3, y+3, (int)(length * relativeEnergy), 8);
        g2d.setColor(Color.gray);
        g2d.fillRect(x+3 + (int)(length * relativeEnergy), y+3, length - (int)(length * relativeEnergy), 8);
        
        g2d.setColor(helicopter.isInvincible()
            ? Color.yellow
            : Color.green);
        g2d.fillRect(x+3, y+14, (int)(length * relativePlating), 8);
        g2d.setColor(helicopter.recentDamageTimer == 0 ? Color.red : Colorations.variableRed);
        g2d.fillRect(x+3 + (int)(length * relativePlating), y+14, length - (int)(length * relativePlating), 8);
    }
    
    
    

    
    /** Graphical objects **/
    // TODO diese sollten in eigene Klassen ausgelagert werden
    
    protected static void paintFrame(Graphics2D g2d, int left, int top, int width, int height)
    {
        paintFrame(g2d, left, top, width, height, null);
    }
    
    public static void paintFrame(Graphics2D g2d, int left, int top, int width, int height, Color filledColor)
    {
        ArrayList<GradientPaint> gradientPaintList = new ArrayList<>(4);
        
        gradientPaintList.add(new GradientPaint(0, top-1, Color.white, 0, top+4, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(0, top+height-1, Color.white, 0, top+height+4, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(left, 0, Color.white, left+5, 0, Colorations.darkestGray, true));
        gradientPaintList.add(new GradientPaint(left+width, 0, Color.white, left+width+5, 0, Colorations.darkestGray, true));
        if(filledColor != null)
        {
            g2d.setPaint(filledColor);
            g2d.fillRect(left, top, width, height);
        }
        g2d.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setPaint(gradientPaintList.get(0));
        g2d.drawLine(left+1, top, left+width-2, top);
        g2d.setPaint(gradientPaintList.get(1));
        g2d.drawLine(left+1, top+height, left+width-2, top+height);
        g2d.setPaint(gradientPaintList.get(2));
        g2d.drawLine(left, top+1, left, top+height-2);
        g2d.setPaint(gradientPaintList.get(3));
        g2d.drawLine(left+width, top+1, left+width, top+height-2);
        g2d.setStroke(new BasicStroke(1));
    }
    
    protected static void paintFrameLine(Graphics2D g2d, int left, int top, int width)
    {
        GradientPaint frameLineGradientPaint = new GradientPaint(0, top-1, Color.white, 0, top+4,
            Colorations.darkestGray,
            true);
        g2d.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setPaint(frameLineGradientPaint);
        g2d.drawLine(left+1, top, left+width-2, top);
        g2d.setStroke(new BasicStroke(1));
    }
}
