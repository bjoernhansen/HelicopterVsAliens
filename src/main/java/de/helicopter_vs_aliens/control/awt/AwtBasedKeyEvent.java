package de.helicopter_vs_aliens.control.awt;

import de.helicopter_vs_aliens.control.events.KeyEvent;
import de.helicopter_vs_aliens.control.events.SpecialKey;

import java.util.EnumMap;
import java.util.Map;


public class AwtBasedKeyEvent implements KeyEvent
{
    private static final Map<SpecialKey, Integer>
        keyMap = new EnumMap<>(SpecialKey.class);

    static
    {
        keyMap.put(SpecialKey.ESCAPE, java.awt.event.KeyEvent.VK_ESCAPE);
        keyMap.put(SpecialKey.BACK_SPACE, java.awt.event.KeyEvent.VK_BACK_SPACE);
        keyMap.put(SpecialKey.ENTER, java.awt.event.KeyEvent.VK_ENTER);
        keyMap.put(SpecialKey.SPACE, java.awt.event.KeyEvent.VK_SPACE);
        keyMap.put(SpecialKey.HYPHEN, java.awt.event.KeyEvent.VK_MINUS);
        keyMap.put(SpecialKey.UNKNOWN, java.awt.event.KeyEvent.KEY_LOCATION_UNKNOWN);
    }

    private final java.awt.event.KeyEvent
        keyEvent;


    public AwtBasedKeyEvent(java.awt.event.KeyEvent keyEvent)
    {
        this.keyEvent = keyEvent;
    }

    @Override
    public boolean isKeyEqualTo(SpecialKey specialKey)
    {
        return keyEvent.getKeyCode() == keyMap.get(specialKey);
    }

    @Override
    public char getKey()
    {
        return keyEvent.getKeyChar();
    }

    @Override
    public boolean isLetterKey()
    {
        return keyEvent.getKeyCode() >= java.awt.event.KeyEvent.VK_A
            && keyEvent.getKeyCode() <= java.awt.event.KeyEvent.VK_Z;
    }
}
