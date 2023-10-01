package de.helicopter_vs_aliens.util.font;

import com.google.common.collect.Table;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;

import java.util.Optional;


public interface FontSpecificationProvider
{
    default FontSpecification getFontSpecification(WindowType window, StartScreenMenuButtonType page)
    {
        return Optional.ofNullable(getFontSpecificationMap().get(window, page))
                       .orElse(getDefaultFontSpecification());
    }

    Table<WindowType, StartScreenMenuButtonType, FontSpecification> getFontSpecificationMap();

    default FontSpecification getDefaultFontSpecification()
    {
        return getFontSpecificationForSize(getDefaultFontSize());
    }

    default FontSpecification getFontSpecificationForSize(int size)
    {
        return FontSpecification.getInstanceForSize(size, getBoldThreshold());
    }

    int getDefaultFontSize();

    int getBoldThreshold();
}
