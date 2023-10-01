package de.helicopter_vs_aliens.util.dictionary;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;

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


public class EnglishFontSpecificationProviderForSwing implements FontSpecificationProvider
{
    private final Table<WindowType, StartScreenMenuButtonType, FontSpecification> fontSizes
        = ImmutableTable.<WindowType, StartScreenMenuButtonType, FontSpecification>builder()
                        .put(INFORMATION, BUTTON_1, FontSpecification.getInstanceForSize(16))
                        .put(INFORMATION, BUTTON_2, FontSpecification.getInstanceForSize(14))
                        .put(INFORMATION, BUTTON_4, FontSpecification.getInstanceForSize(11))
                        .put(INFORMATION, BUTTON_5, FontSpecification.getInstanceForSize(12))
                        .put(INFORMATION, BUTTON_6, FontSpecification.getInstanceForSize(16))
                        .put(DESCRIPTION, BUTTON_1, FontSpecification.getInstanceForSize(16))
                        .put(DESCRIPTION, BUTTON_2, FontSpecification.getInstanceForSize(12))
                        .put(DESCRIPTION, BUTTON_3, FontSpecification.getInstanceForSize(14))
                        .put(DESCRIPTION, BUTTON_4, FontSpecification.getInstanceForSize(16))
                        .put(DESCRIPTION, BUTTON_5, FontSpecification.getInstanceForSize(11))
                        .put(DESCRIPTION, BUTTON_6, FontSpecification.getInstanceForSize(13))
                        .put(DESCRIPTION, BUTTON_8, FontSpecification.getInstanceForSize(15))
                        .put(HELICOPTER_TYPES, BUTTON_1, FontSpecification.getInstanceForSize(16))
                        .put(HELICOPTER_TYPES, BUTTON_3, FontSpecification.getInstanceForSize(12))
                        .put(HELICOPTER_TYPES, BUTTON_4, FontSpecification.getInstanceForSize(16))
                        .put(HELICOPTER_TYPES, BUTTON_5, FontSpecification.getInstanceForSize(15))
                        .put(HELICOPTER_TYPES, BUTTON_6, FontSpecification.getInstanceForSize(15))
                        .put(HELICOPTER_TYPES, BUTTON_7, FontSpecification.getInstanceForSize(12))
                        .put(HELICOPTER_TYPES, BUTTON_8, FontSpecification.getInstanceForSize(15))
                        .put(CONTACT, BUTTON_1, FontSpecification.getInstanceForSize(14))
                        .build();

    @Override
    public Table<WindowType, StartScreenMenuButtonType, FontSpecification> getFontSpecificationMap()
    {
        return fontSizes;
    }
}
