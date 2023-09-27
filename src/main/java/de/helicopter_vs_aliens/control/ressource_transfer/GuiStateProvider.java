package de.helicopter_vs_aliens.control.ressource_transfer;


import de.helicopter_vs_aliens.util.geometry.Dimension;


public interface GuiStateProvider
{
    boolean isMouseCursorInWindow();

    void resetBackgroundRepaintTimer();

    Dimension getDisplayShift();
}
