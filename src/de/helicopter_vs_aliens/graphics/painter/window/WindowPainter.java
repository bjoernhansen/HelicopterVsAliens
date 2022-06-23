package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.ressource_transfer.GuiStateProvider;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.Painter;
import de.helicopter_vs_aliens.graphics.painter.helicopter.HelicopterPainter;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;

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
    
    // TODO DependencyInjection verwenden, ggf. alle Painter von Entity erben lassen, eine neue Superklasse von der dann auch GameEntity erbt --> Code verschieben aus GameEntity
    protected static final GameRessourceProvider
        gameRessourceProvider = Controller.getInstance();
    
    protected static final GuiStateProvider
        guiStateProvider = gameRessourceProvider;
    
    protected static Helicopter
        helicopter;
        
    // TODO warum muss dann immer wieder upgedated werden
    protected static void updateDependencies(){
        helicopter = gameRessourceProvider.getHelicopter();
    }
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Window window)
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
    
    protected static void paintHelicopterDisplay(GraphicsAdapter graphicsAdapter,
                                               Helicopter helicopter,
                                               int x, int y)
    {
        GraphicalEntities.paintFrame(graphicsAdapter, 26 + x,  85 + y, 200, 173, WindowManager.window  != GAME ? null : Colorations.lightestGray);
        graphicsAdapter.setColor(Color.white);
        graphicsAdapter.setFont(fontProvider.getBold(20));
        String typeName = Window.dictionary.typeName(helicopter.getType());
        graphicsAdapter.drawString(typeName, 28 + x + (196-graphicsAdapter.getFontMetrics().stringWidth(typeName))/2, 113 + y);
        
        HelicopterPainter helicopterPainter = GraphicsManager.getInstance().getPainter(helicopter.getClass());
        helicopterPainter.displayPaint(graphicsAdapter, helicopter, 59 + x, 141 + y);
    
        GraphicalEntities.paintFrameLine(graphicsAdapter, 28 + x, 126 + y, 196);
        GraphicalEntities.paintFrameLine(graphicsAdapter, 28 + x, 226 + y, 196);
        
        if(WindowManager.window  != GAME)
        {
            paintHealthBar(graphicsAdapter, helicopter, 30 + x, 230 + y, 187, false);
        }
        else
        {
            graphicsAdapter.setFont(fontProvider.getBold(18));
            if(Window.unlockedTimer > UNLOCKED_DISPLAY_TIME - 50)
            {
                graphicsAdapter.setColor(Colorations.red);
                typeName = Window.dictionary.unavailable();
            }
            else
            {
                graphicsAdapter.setColor(Colorations.darkArrowGreen);
                typeName = Window.dictionary.unlocked();
            }
            graphicsAdapter.drawString(typeName, 28 + x + (196-graphicsAdapter.getFontMetrics().stringWidth(typeName))/2, 249 + y);
        }
        
        if(WindowManager.window  == REPAIR_SHOP)
        {
            if(helicopter.isDamaged)
            {
                graphicsAdapter.setColor(Color.red);
                graphicsAdapter.setFont(fontProvider.getPlain(14));
                graphicsAdapter.drawString(Window.dictionary.damaged(), 34 + x, 216 + y);
            }
            graphicsAdapter.setFont(fontProvider.getBold(16));
            graphicsAdapter.setColor(Colorations.plating);
            int percentPlating = (Math.round(100 * helicopter.getRelativePlating()));
            FontMetrics fm = graphicsAdapter.getFontMetrics();
            int sw = fm.stringWidth(""+percentPlating);
            graphicsAdapter.drawString(percentPlating + "%", 203 - sw + x, STANDARD_UPGRADE_OFFSET_Y + y + 69);
        }
    }
    
    protected static void paintHealthBar(GraphicsAdapter graphicsAdapter, Helicopter helicopter, int x, int y, int length, boolean rahmen)
    {
        float relativeEnergy = helicopter.getRelativeEnergy();
        float relativePlating = helicopter.getRelativePlating();
        if(rahmen)
        {
            graphicsAdapter.setColor(Colorations.lightestGray);
            graphicsAdapter.fillRect(x+1, y+1, length + 4, 23);
            graphicsAdapter.setColor(Colorations.lightGray);
            graphicsAdapter.fillRect(x+2, y+2, length+2, 10);
            graphicsAdapter.fillRect(x+2, y+13, length+2, 10);
        }
        if(!helicopter.isEnergyAbilityActivatable())
        {
            graphicsAdapter.setColor(Color.cyan);
        }
        else{graphicsAdapter.setColor(helicopter.hasUnlimitedEnergy()
            ? Colorations.endlessEnergyViolet
            : Color.blue);}
        graphicsAdapter.fillRect(x+3, y+3, (int)(length * relativeEnergy), 8);
        graphicsAdapter.setColor(Color.gray);
        graphicsAdapter.fillRect(x+3 + (int)(length * relativeEnergy), y+3, length - (int)(length * relativeEnergy), 8);
        
        graphicsAdapter.setColor(helicopter.isInvincible()
            ? Color.yellow
            : Color.green);
        graphicsAdapter.fillRect(x+3, y+14, (int)(length * relativePlating), 8);
        graphicsAdapter.setColor(helicopter.recentDamageTimer == 0 ? Color.red : Colorations.variableRed);
        graphicsAdapter.fillRect(x+3 + (int)(length * relativePlating), y+14, length - (int)(length * relativePlating), 8);
    }
}
