package de.helicopter_vs_aliens.control.events;

public interface MouseEvent
{
    boolean isLeftButtonClicked();

    boolean isRightButtonClicked();

    int getX();

    int getY();
}
