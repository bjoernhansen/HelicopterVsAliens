package de.helicopter_vs_aliens.util.dictionary;

import com.google.common.collect.Table;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;

import java.util.Optional;


public interface FontSpecificationProvider
{
    default FontSpecification getFontSpecification(WindowType window, StartScreenMenuButtonType page)
    {
        var defaultSpecification = FontSpecification.getDefaultInstance();
        return Optional.ofNullable(getFontSpecificationMap().get(window, page))
                       .orElse(defaultSpecification);
    }

    Table<WindowType, StartScreenMenuButtonType, FontSpecification> getFontSpecificationMap();
}
