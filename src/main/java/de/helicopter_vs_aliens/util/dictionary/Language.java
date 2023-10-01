package de.helicopter_vs_aliens.util.dictionary;


import de.helicopter_vs_aliens.platform_specific.awt.font.EnglishFontSpecificationProviderForSwing;
import de.helicopter_vs_aliens.platform_specific.awt.font.GermanFontSpecificationProviderForSwing;
import de.helicopter_vs_aliens.platform_specific.javafx.font.EnglishFontSpecificationProviderForJavaFx;
import de.helicopter_vs_aliens.platform_specific.javafx.font.GermanFontSpecificationProviderForJavaFx;
import de.helicopter_vs_aliens.graphics.GraphicsApiType;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.util.font.FontSpecification;
import de.helicopter_vs_aliens.util.font.FontSpecificationProvider;

import java.util.function.Supplier;


public enum Language
{
    ENGLISH(
        "English",
        "en",
        1,
        EnglishFontSpecificationProviderForSwing::new,
        EnglishFontSpecificationProviderForJavaFx::new),

    GERMAN(
        "Deutsch",
        "de",
        0,
        GermanFontSpecificationProviderForSwing::new,
        GermanFontSpecificationProviderForJavaFx::new);


    private final int
        objectPosition;

    private final String
        nativeName;

    private final String
        code;

    private final FontSpecificationProvider
        fontSpecificationProviderForSwing;

    private final FontSpecificationProvider
        fontSpecificationProviderForJavaFx;


    public static Language getDefault()
    {
        return ENGLISH;
    }

    Language(String nativeName,
             String code,
             int objectPosition,
             Supplier<FontSpecificationProvider> fontSizeProviderSupplierForSwing,
             Supplier<FontSpecificationProvider> fontSizeProviderSupplierForJavaFx)
    {
        this.nativeName = nativeName;
        this.code = code;
        this.objectPosition = objectPosition;
        fontSpecificationProviderForSwing = fontSizeProviderSupplierForSwing.get();
        fontSpecificationProviderForJavaFx = fontSizeProviderSupplierForJavaFx.get();
    }

    public String getNativeName()
    {
        return this.nativeName;
    }

    int getObjectPosition()
    {
        return this.objectPosition;
    }

    String getCode()
    {
        return this.code;
    }

    public FontSpecification getFontSpecification(WindowType window, StartScreenMenuButtonType page, GraphicsApiType graphicsApiType)
    {
        var fontSpecificationProvider = graphicsApiType == GraphicsApiType.GRAPHICS_2D
                                        ? fontSpecificationProviderForSwing
                                        : fontSpecificationProviderForJavaFx;

        return fontSpecificationProvider.getFontSpecification(window, page);
    }
}