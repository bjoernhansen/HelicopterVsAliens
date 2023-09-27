package de.helicopter_vs_aliens.graphics;

import de.helicopter_vs_aliens.control.awt.AwtController;
import de.helicopter_vs_aliens.control.javafx.GameApplication;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import java.util.function.Supplier;


public enum GraphicsApiType
{
    GRAPHICS_2D(AwtController::getDisplayShift),
    JAVAFX(GameApplication::getDisplayShift);


    private final Supplier<Dimension>
        displayShiftSupplier;


    GraphicsApiType(Supplier<Dimension> displayShiftSupplier)
    {
        this.displayShiftSupplier = displayShiftSupplier;
    }

    public Dimension getDisplayShift()
    {
        return displayShiftSupplier.get();
    }
}
