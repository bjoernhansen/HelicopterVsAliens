package de.helicopter_vs_aliens.util.dictionary;


import de.helicopter_vs_aliens.graphics.GraphicsApiType;
import de.helicopter_vs_aliens.graphics.painter.window.VerticalBoundaries;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.platform_specific.awt.font.EnglishFontSpecificationProviderForSwing;
import de.helicopter_vs_aliens.platform_specific.awt.font.GermanFontSpecificationProviderForSwing;
import de.helicopter_vs_aliens.platform_specific.javafx.font.EnglishFontSpecificationProviderForJavaFx;
import de.helicopter_vs_aliens.platform_specific.javafx.font.GermanFontSpecificationProviderForJavaFx;
import de.helicopter_vs_aliens.util.font.FontSpecification;
import de.helicopter_vs_aliens.util.font.FontSpecificationProvider;

import java.util.List;
import java.util.function.Supplier;


public enum Language
{
    // TODO Die Positions und Shift-Angaben sollten durch Schriftabmessungen ersetzt werden
    
    ENGLISH(
        "English",
        "en",
        0,
        1,
        0,
        6,
        646,
        EnglishFontSpecificationProviderForSwing::new,
        EnglishFontSpecificationProviderForJavaFx::new,
        new VerticalBoundaries(100, 158)),

    GERMAN(
        "Deutsch",
        "de",
        1,
        0,
        36,
        0,
        661,
        GermanFontSpecificationProviderForSwing::new,
        GermanFontSpecificationProviderForJavaFx::new,
        new VerticalBoundaries(64, 194));
    
    
    private static final List<Language>
        VALUES = List.of(values());
    
    private final String
        nativeName;
    
    private final String
        code;
    
    private final int
        id;
    
    private final int
        objectPosition;
    
    private final int
        victoryMessageShiftY;
    
    private final int
        mainMenuHeadlineShiftX;
    
    private final int
        timeDisplayPositionX;

    private final FontSpecificationProvider
        fontSpecificationProviderForSwing;

    private final FontSpecificationProvider
        fontSpecificationProviderForJavaFx;
    
    private final VerticalBoundaries
        verticalBoundaries;


    public static Language getDefault()
    {
        return ENGLISH;
    }

    Language(String nativeName,
             String code,
             int id, int objectPosition,
             int victoryMessageShiftY, int mainMenuHeadlineShiftX, int timeDisplayPositionX, Supplier<FontSpecificationProvider> fontSizeProviderSupplierForSwing,
             Supplier<FontSpecificationProvider> fontSizeProviderSupplierForJavaFx,
             VerticalBoundaries verticalBoundaries)
    {
        this.nativeName = nativeName;
        this.code = code;
        this.id = id;
        this.objectPosition = objectPosition;
        this.victoryMessageShiftY = victoryMessageShiftY;
        this.mainMenuHeadlineShiftX = mainMenuHeadlineShiftX;
        this.timeDisplayPositionX = timeDisplayPositionX;
        fontSpecificationProviderForSwing = fontSizeProviderSupplierForSwing.get();
        fontSpecificationProviderForJavaFx = fontSizeProviderSupplierForJavaFx.get();
        this.verticalBoundaries = verticalBoundaries;
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
    
    public int getVictoryMessageShiftY()
    {
        return victoryMessageShiftY;
    }
    
    public Language getNextLanguage()
    {
        return VALUES.get((this.id + 1) % VALUES.size());
    }
    
    public int getTimeDisplayPositionX()
    {
        return timeDisplayPositionX;
    }
    
    public VerticalBoundaries getVerticalBoundariesOfPopupWindowForTemporarilyVictory()
    {
        return verticalBoundaries;
    }
    
    public int getMainMenuHeadlineShiftX()
    {
        return mainMenuHeadlineShiftX;
    }
}