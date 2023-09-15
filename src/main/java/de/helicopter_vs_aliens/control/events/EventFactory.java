package de.helicopter_vs_aliens.control.events;

public class EventFactory
{
    public static KeyEvent makeKeyEvent(java.awt.event.KeyEvent keyEvent)
    {
        return new AwtBasedKeyEvent(keyEvent);
    }
}
