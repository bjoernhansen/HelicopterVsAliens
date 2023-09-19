package de.helicopter_vs_aliens.control.events;

public interface KeyEvent
{
    default boolean isKeyEqualTo(char key)
    {
        return getKey() == key;
    }

    char getKey();

    default boolean isKeyAllowedForPlayerName()
    {
        return isLetterKey()
            || isKeyEqualTo(SpecialKey.UNKNOWN)
            || isKeyEqualTo(SpecialKey.SPACE)
            || isKeyEqualTo(SpecialKey.HYPHEN);
    }

    boolean isLetterKey();

    boolean isKeyEqualTo(SpecialKey specialKey);
}
