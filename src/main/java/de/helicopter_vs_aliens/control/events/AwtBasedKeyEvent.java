package de.helicopter_vs_aliens.control.events;

import java.util.EnumMap;
import java.util.Map;


class AwtBasedKeyEvent implements KeyEvent
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


    AwtBasedKeyEvent(java.awt.event.KeyEvent keyEvent)
    {
        this.keyEvent = keyEvent;
    }

    @Override
    public boolean keyEquals(char keyCode)
    {
        return keyEvent.getKeyChar() == keyCode;
    }

    @Override
    public boolean keyEquals(SpecialKey specialKey)
    {
        return keyEvent.getKeyCode() == keyMap.get(specialKey);
    }

    @Override
    public char getKey()
    {
        return keyEvent.getKeyChar();
    }

    @Override
    public boolean isKeyAllowedForPlayerName()
    {
        return isKeyLetter()
            || keyEquals(SpecialKey.UNKNOWN)
            || keyEquals(SpecialKey.SPACE)
            || keyEquals(SpecialKey.HYPHEN);
    }

    private boolean isKeyLetter()
    {
        return keyEvent.getKeyCode() >= java.awt.event.KeyEvent.VK_A
            && keyEvent.getKeyCode() <= java.awt.event.KeyEvent.VK_Z;
    }
}
