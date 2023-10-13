package de.helicopter_vs_aliens.gui.window;


public abstract class AbstractHtmlViewer <E> implements HtmlViewer
{
    protected static final HorizontalBounds
        HORIZONTAL_BOUNDS = new HorizontalBounds(42, 940);

    protected static final VerticalBounds
        VERTICAL_BOUNDS = new VerticalBounds(83, 240);


    protected AbstractHtmlViewer()
    {

    }

    protected abstract E getComponent();

    protected abstract void setHorizontalBounds(HorizontalBounds horizontalBounds);

    protected record HorizontalBounds(int x, int width) {}

    protected record VerticalBounds(int y, int height) {}
}
