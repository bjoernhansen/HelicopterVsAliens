package de.helicopter_vs_aliens.platform_specific.awt.font;

import de.helicopter_vs_aliens.util.font.FontSpecificationProvider;


abstract class AbstractFontSpecificationProviderForSwing implements FontSpecificationProvider
{
    private static final int
        BOLD_THRESHOLD = 12;

    private static final int
        DEFAULT_FONT_SIZE = 16;


    @Override
    public int getDefaultFontSize(){
        return DEFAULT_FONT_SIZE;
    }

    @Override
    public int getBoldThreshold()
    {
        return BOLD_THRESHOLD;
    }
}
