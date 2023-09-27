package de.helicopter_vs_aliens.control.awt;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.events.EventFactory;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;


class EventListener implements KeyListener, MouseListener, MouseMotionListener
{
    private static final boolean
        STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW = true;


    private final GameRessourceProvider
        gameRessourceProvider;

    private boolean
        isMouseCursorInWindow = true;


    public EventListener(GameRessourceProvider gameRessourceProvider)
    {
        this.gameRessourceProvider = gameRessourceProvider;
    }

    // Behandlung von Fenster-, Tastatur- und Mausereignisse
    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        Events.keyTyped(EventFactory.makeKeyEvent(keyEvent), gameRessourceProvider);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent)
    {
        Events.mousePressed(EventFactory.makeMouseEvent(mouseEvent), gameRessourceProvider);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent)
    {
        Events.mouseReleased(EventFactory.makeMouseEvent(mouseEvent), getHelicopter());
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent)
    {
        Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(mouseEvent), gameRessourceProvider);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent)
    {
        Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(mouseEvent), gameRessourceProvider);
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent)
    {
        if (WindowManager.window == GAME && STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW)
        {
            this.isMouseCursorInWindow = true;
            Events.lastCurrentTime = System.currentTimeMillis();
        }
        gameRessourceProvider.getGuiStateProvider().resetBackgroundRepaintTimer();
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent)
    {
        if (WindowManager.window == GAME && STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW)
        {
            this.isMouseCursorInWindow = false;
            Events.playingTime += System.currentTimeMillis() - Events.lastCurrentTime;
        }
    }

    public boolean isMouseCursorInWindow()
    {
        return isMouseCursorInWindow;
    }

    private Helicopter getHelicopter()
    {
        return gameRessourceProvider.getHelicopter();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent)
    {
        // unused listener method
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        // unused listener method
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent)
    {
        // unused listener method
    }
}
