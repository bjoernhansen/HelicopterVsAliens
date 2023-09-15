package de.helicopter_vs_aliens.control.events;

public interface KeyEvent
{
    boolean keyEquals(char keyCode);

    boolean keyEquals(SpecialKey specialKey);

    char getKey();

    boolean isKeyAllowedForPlayerName();
}
