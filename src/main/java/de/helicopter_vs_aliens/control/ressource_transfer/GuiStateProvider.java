package de.helicopter_vs_aliens.control.ressource_transfer;

public interface GuiStateProvider
{
    boolean isMouseCursorInWindow();
    
    boolean isFpsDisplayVisible();
    
    boolean isAntialiasingActivated();
    
    int getFramesCounter();
}
