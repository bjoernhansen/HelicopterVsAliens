package de.helicopter_vs_aliens.util.dictionary;


import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;

import java.util.function.Supplier;


public enum Language
{
    ENGLISH("English", "en", 1, EnglishFontSpecificationProviderForSwing::new),
    GERMAN("Deutsch", "de", 0, GermanFontSpecificationProviderForSwing::new);


    private final int
        objectPosition;

    private final String
        nativeName;

    private final String
        code;

    private final FontSpecificationProvider
        fontSpecificationProvider;


    public static Language getDefault()
    {
        return ENGLISH;
    }

    Language(String nativeName, String code, int objectPosition, Supplier<FontSpecificationProvider> fontSizeProviderSupplier)
    {
        this.nativeName = nativeName;
        this.code = code;
        this.objectPosition = objectPosition;
        fontSpecificationProvider = fontSizeProviderSupplier.get();
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

    public FontSpecification getFontSpecification(WindowType window, StartScreenMenuButtonType page)
    {
        return fontSpecificationProvider.getFontSpecification(window, page);
    }
}