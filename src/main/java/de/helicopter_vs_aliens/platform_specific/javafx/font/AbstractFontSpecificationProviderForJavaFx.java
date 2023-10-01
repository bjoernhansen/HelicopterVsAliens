package de.helicopter_vs_aliens.platform_specific.javafx.font;

import de.helicopter_vs_aliens.util.font.FontSpecificationProvider;


abstract class AbstractFontSpecificationProviderForJavaFx implements FontSpecificationProvider
{
    private static final int
        BOLD_THRESHOLD = 16;

    private static final int
        DEFAULT_FONT_SIZE = 22;


    @Override
    public int getDefaultFontSize()
    {
        return DEFAULT_FONT_SIZE;
    }

    @Override
    public int getBoldThreshold()
    {
        return BOLD_THRESHOLD;
    }
}
