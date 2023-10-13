package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.platform_specific.awt.AwtController;
import de.helicopter_vs_aliens.platform_specific.javafx.GameApplication;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import java.util.function.Supplier;


public enum GraphicsApiType
{
    GRAPHICS_2D(AwtController::getDisplayShift, 3, 1),
    JAVAFX(GameApplication::getDisplayShift, 7, 5);


    private final Supplier<Dimension>
        displayShiftSupplier;

    private final int
        lineDistance;

    private final int
        headingMargin;


    GraphicsApiType(Supplier<Dimension> displayShiftSupplier, int lineDistance, int headingMargin)
    {
        this.displayShiftSupplier = displayShiftSupplier;
        this.lineDistance = lineDistance;
        this.headingMargin = headingMargin;
    }

    public Dimension getDisplayShift()
    {
        return displayShiftSupplier.get();
    }

    public int getLineDistance()
    {
        return lineDistance;
    }

    public int getHeadingMargin()
    {
        return headingMargin;
    }
}
