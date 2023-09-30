package de.helicopter_vs_aliens.gui.window;


public abstract class AbstractHtmlViewer <E> implements HtmlViewer
{
    private static final HorizontalBounds
        WIDE_HORIZONTAL_BOUNDS = new HorizontalBounds(42, 940);

    private static final HorizontalBounds
        NARROW_HORIZONTAL_BOUNDS = new HorizontalBounds(92, 890);

    protected static final VerticalBounds
        VERTICAL_BOUNDS = new VerticalBounds(83, 240);


    protected abstract E getComponent();

    @Override
    public void setWide()
    {
        setHorizontalBounds(WIDE_HORIZONTAL_BOUNDS);
    }

    @Override
    public void setNarrow()
    {
        setHorizontalBounds(NARROW_HORIZONTAL_BOUNDS);
    }

    protected abstract void setHorizontalBounds(HorizontalBounds horizontalBounds);

    protected record HorizontalBounds(int x, int width) {}

    protected record VerticalBounds(int y, int height) {}
}
