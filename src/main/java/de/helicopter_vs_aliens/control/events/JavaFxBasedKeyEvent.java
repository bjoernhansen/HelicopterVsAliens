package de.helicopter_vs_aliens.control.events;

import javafx.scene.input.KeyCode;

import java.util.EnumMap;
import java.util.Map;


class JavaFxBasedKeyEvent implements KeyEvent
{
    private static final Map<SpecialKey, KeyCode>
        keyMap = new EnumMap<>(SpecialKey.class);

    static
    {
        keyMap.put(SpecialKey.ESCAPE, KeyCode.ESCAPE);
        keyMap.put(SpecialKey.BACK_SPACE, KeyCode.BACK_SPACE);
        keyMap.put(SpecialKey.ENTER, KeyCode.ENTER);
        keyMap.put(SpecialKey.SPACE, KeyCode.SPACE);
        keyMap.put(SpecialKey.HYPHEN, KeyCode.MINUS);
        keyMap.put(SpecialKey.UNKNOWN, KeyCode.UNDEFINED);
    }

    private final javafx.scene.input.KeyEvent
        keyEvent;


    public JavaFxBasedKeyEvent(javafx.scene.input.KeyEvent keyEvent)
    {
        this.keyEvent = keyEvent;
    }

    @Override
    public boolean isKeyEqualTo(SpecialKey specialKey)
    {
        return keyEvent.getCode() == keyMap.get(specialKey);
    }

    @Override
    public char getKey()
    {
        String text = keyEvent.getText();
        return text.length() > 0 ? text.charAt(0) : '\0';
    }

    @Override
    public boolean isLetterKey()
    {
        return keyEvent.getCode().isLetterKey();
    }
}
