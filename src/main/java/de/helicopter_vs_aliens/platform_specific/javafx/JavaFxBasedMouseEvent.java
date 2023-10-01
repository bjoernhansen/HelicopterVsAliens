package de.helicopter_vs_aliens.platform_specific.javafx;

import de.helicopter_vs_aliens.control.events.MouseEvent;
import javafx.scene.input.MouseButton;


public class JavaFxBasedMouseEvent implements MouseEvent
{
    private final javafx.scene.input.MouseEvent
        mouseEvent;


    public JavaFxBasedMouseEvent(javafx.scene.input.MouseEvent mouseEvent)
    {
        this.mouseEvent = mouseEvent;
    }

    @Override
    public boolean isLeftButtonClicked()
    {
        return mouseEvent.getButton() == MouseButton.PRIMARY;
    }

    @Override
    public boolean isRightButtonClicked()
    {
        return mouseEvent.getButton() == MouseButton.SECONDARY;
    }

    @Override
    public int getX()
    {
        return (int) mouseEvent.getX();
    }

    @Override
    public int getY()
    {
        return (int) mouseEvent.getY();
    }
}
