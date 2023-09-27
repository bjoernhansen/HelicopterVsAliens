package de.helicopter_vs_aliens.control.ressource_transfer;

import java.awt.Dimension;


public interface GuiStateProvider
{
    boolean isMouseCursorInWindow();

    void resetBackgroundRepaintTimer();

    int getGameLoopCount();

    Dimension getDisplayShift();
}
