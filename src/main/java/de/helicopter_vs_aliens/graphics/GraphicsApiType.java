package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.platform_specific.awt.AwtController;
import de.helicopter_vs_aliens.platform_specific.javafx.GameApplication;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import java.util.function.Supplier;


public enum GraphicsApiType
{
    GRAPHICS_2D(AwtController::getDisplayShift, 12),
    JAVAFX(GameApplication::getDisplayShift, 17);


    private final Supplier<Dimension>
        displayShiftSupplier;

    private final int
        boldThreshold;


    GraphicsApiType(Supplier<Dimension> displayShiftSupplier, int boldThreshold)
    {
        this.displayShiftSupplier = displayShiftSupplier;
        this.boldThreshold = boldThreshold;
    }

    public Dimension getDisplayShift()
    {
        return displayShiftSupplier.get();
    }

    public int getBoldThreshold()
    {
        return boldThreshold;
    }
}
