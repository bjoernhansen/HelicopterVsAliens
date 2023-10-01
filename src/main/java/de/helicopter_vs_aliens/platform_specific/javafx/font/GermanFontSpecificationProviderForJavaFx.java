package de.helicopter_vs_aliens.platform_specific.javafx.font;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.util.font.FontSpecification;

import static de.helicopter_vs_aliens.gui.WindowType.CONTACT;
import static de.helicopter_vs_aliens.gui.WindowType.DESCRIPTION;
import static de.helicopter_vs_aliens.gui.WindowType.HELICOPTER_TYPES;
import static de.helicopter_vs_aliens.gui.WindowType.INFORMATION;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_1;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_2;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_3;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_4;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_5;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_6;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_7;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_8;


public class GermanFontSpecificationProviderForJavaFx extends AbstractFontSpecificationProviderForJavaFx
{
    private final Table<WindowType, StartScreenMenuButtonType, FontSpecification> fontSizes
        = ImmutableTable.<WindowType, StartScreenMenuButtonType, FontSpecification>builder()
                        .put(INFORMATION, BUTTON_1, getFontSpecificationForSize(22))
                        .put(INFORMATION, BUTTON_2, getFontSpecificationForSize(22))
                        .put(INFORMATION, BUTTON_4, getFontSpecificationForSize(16))
                        .put(INFORMATION, BUTTON_5, getFontSpecificationForSize(16))
                        .put(INFORMATION, BUTTON_6, getFontSpecificationForSize(22))
                        .put(DESCRIPTION, BUTTON_1, getFontSpecificationForSize(22))
                        .put(DESCRIPTION, BUTTON_2, getFontSpecificationForSize(19))
                        .put(DESCRIPTION, BUTTON_3, getFontSpecificationForSize(20))
                        .put(DESCRIPTION, BUTTON_4, getFontSpecificationForSize(22))
                        .put(DESCRIPTION, BUTTON_5, getFontSpecificationForSize(15))
                        .put(DESCRIPTION, BUTTON_6, getFontSpecificationForSize(18))
                        .put(DESCRIPTION, BUTTON_8, getFontSpecificationForSize(22))
                        .put(HELICOPTER_TYPES, BUTTON_1, getFontSpecificationForSize(22))
                        .put(HELICOPTER_TYPES, BUTTON_3, getFontSpecificationForSize(16))
                        .put(HELICOPTER_TYPES, BUTTON_4, getFontSpecificationForSize(21))
                        .put(HELICOPTER_TYPES, BUTTON_5, getFontSpecificationForSize(20))
                        .put(HELICOPTER_TYPES, BUTTON_6, getFontSpecificationForSize(21))
                        .put(HELICOPTER_TYPES, BUTTON_7, getFontSpecificationForSize(16))
                        .put(HELICOPTER_TYPES, BUTTON_8, getFontSpecificationForSize(22))
                        .put(CONTACT, BUTTON_1, getFontSpecificationForSize(20))
                        .build();

    @Override
    public Table<WindowType, StartScreenMenuButtonType, FontSpecification> getFontSpecificationMap()
    {
        return fontSizes;
    }
}
