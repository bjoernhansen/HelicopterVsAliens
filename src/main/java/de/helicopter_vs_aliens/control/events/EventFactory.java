package de.helicopter_vs_aliens.control.events;

import de.helicopter_vs_aliens.platform_specific.awt.AwtBasedKeyEvent;
import de.helicopter_vs_aliens.platform_specific.awt.AwtBasedMouseEvent;
import de.helicopter_vs_aliens.platform_specific.javafx.JavaFxBasedKeyEvent;
import de.helicopter_vs_aliens.platform_specific.javafx.JavaFxBasedMouseEvent;


public final class EventFactory
{
    private EventFactory()
    {
        throw new UnsupportedOperationException();
    }

    public static KeyEvent makeKeyEvent(java.awt.event.KeyEvent keyEvent)
    {
        return new AwtBasedKeyEvent(keyEvent);
    }

    public static KeyEvent makeKeyEvent(javafx.scene.input.KeyEvent keyEvent)
    {
        return new JavaFxBasedKeyEvent(keyEvent);
    }

    public static MouseEvent makeMouseEvent(java.awt.event.MouseEvent mouseEvent)
    {
        return new AwtBasedMouseEvent(mouseEvent);
    }

    public static MouseEvent makeMouseEvent(javafx.scene.input.MouseEvent mouseEvent)
    {
        return new JavaFxBasedMouseEvent(mouseEvent);
    }
}
