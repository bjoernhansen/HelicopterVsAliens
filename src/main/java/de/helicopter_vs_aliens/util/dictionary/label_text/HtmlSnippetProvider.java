package de.helicopter_vs_aliens.util.dictionary.label_text;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.graphics.GraphicsApiType;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.util.dictionary.Language;
import de.helicopter_vs_aliens.util.font.FontSpecification;


class HtmlSnippetProvider
{
    private static final String
        OPENING_TAGS = "<html><head>";

    private static final String
        INTERMEDIATE_TAGS = "</head><body>";

    private static final String
        CLOSING_TAGS = "<body></html>";

    private final GraphicsApiType
        graphicsApiType;

    private final Language
        language;

    private final double
        scalingFactor;

    private FontSpecification
        fontSpecification;

    private int
        size ;


    HtmlSnippetProvider(GameRessourceProvider ressourceProvider, Language language)
    {
        graphicsApiType = ressourceProvider.getGraphicsApiType();
        scalingFactor = ressourceProvider.getScalingFactor();
        this.language = language;
        setPage(WindowType.START_SCREEN, StartScreenMenuButtonType.BUTTON_1);
    }
    
    void setPage(WindowType window, StartScreenMenuButtonType page)
    {
        fontSpecification = language.getFontSpecification(window, page, graphicsApiType);
        size = (int)(scalingFactor * fontSpecification.getSize());
    }

    String getHtmlBeforeBodyTextContent()
    {
        return getHtmlBeforeBodyTextContentWith(getStyleElement(), fontSpecification.openingTags());
    }

    private String getHtmlBeforeBodyTextContentWith(String styleElement, String boldString)
    {
        return OPENING_TAGS + styleElement + INTERMEDIATE_TAGS + boldString;
    }

    String getClosingTags()
    {
        return fontSpecification.closingTags() + CLOSING_TAGS;
    }

    private String getStyleElement()
    {
        return "<style>" + getStyleElementTextContent() + "</style>";
    }

    private String getStyleElementTextContent()
    {
        return "body { font-size: " + size + "px; font-family: Dialog; color: #D2D2D2; }";
    }
}
