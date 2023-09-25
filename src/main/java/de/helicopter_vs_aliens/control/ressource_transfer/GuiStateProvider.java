package de.helicopter_vs_aliens.control.ressource_transfer;

import de.helicopter_vs_aliens.gui.button.Button;

import java.awt.Dimension;
import java.awt.DisplayMode;


public interface GuiStateProvider
{
    boolean isMouseCursorInWindow();

    void resetBackgroundRepaintTimer();
    
    boolean isFpsDisplayVisible();
    
    boolean isAntialiasingActivated();
    
    int getGameLoopCount();

    Dimension getDisplayShift();

    void switchDisplayMode(Button currentButton);

    void switchAntialiasingActivationState(Button currentButton);

    boolean isFullScreen();

    DisplayMode getCurrentDisplayMode();

    void switchFpsVisibleState();
}
