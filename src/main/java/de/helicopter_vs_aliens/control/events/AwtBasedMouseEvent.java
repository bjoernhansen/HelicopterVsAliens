package de.helicopter_vs_aliens.control.events;


class AwtBasedMouseEvent implements MouseEvent
{
    private final java.awt.event.MouseEvent
        mouseEvent;


    AwtBasedMouseEvent(java.awt.event.MouseEvent mouseEvent)
    {
        this.mouseEvent = mouseEvent;
    }

    @Override
    public boolean isLeftButtonClicked()
    {
        return mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON1;
    }

    @Override
    public boolean isRightButtonClicked()
    {
        return mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON3;
    }

    @Override
    public int getX()
    {
        return mouseEvent.getX();
    }

    @Override
    public int getY()
    {
        return mouseEvent.getY();
    }
}
