package de.helicopter_vs_aliens.gui.window;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.graphics.GraphicsApiType;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.util.dictionary.Language;
import de.helicopter_vs_aliens.util.font.FontSpecification;


public class HtmlSnippetProvider
{
    private static final String
        OPENING_TAGS = "<html><head>";

    private static final String
        INTERMEDIATE_TAGS = "</head><body>";

    private static final String
        CLOSING_TAGS = "<body></html>";

    private static final String
        HEADING_MARGIN = "heading-margin";

    private static final String
        LINE_DISTANCE = "line-distance";


    private final GraphicsApiType
        graphicsApiType;

    private final Language
        language;

    private final double
        scalingFactor;

    private final int
        lineDistance;

    private final int
        headingMargin;


    private WindowType
        window;

    private StartScreenMenuButtonType
        page;

    private FontSpecification
        fontSpecification;

    private int
        size ;


    public HtmlSnippetProvider(GameRessourceProvider ressourceProvider, Language language)
    {
        graphicsApiType = ressourceProvider.getGraphicsApiType();
        scalingFactor = ressourceProvider.getScalingFactor();
        this.language = language;
        // TODO Konstanten ggf. nach Enum GraphicsApi
        lineDistance = (int) (scalingFactor * (graphicsApiType == GraphicsApiType.GRAPHICS_2D ? 3 : 7));
        headingMargin = (int) (scalingFactor * (graphicsApiType == GraphicsApiType.GRAPHICS_2D ? 1 : 5));
        setPage(WindowType.START_SCREEN, StartScreenMenuButtonType.BUTTON_1);
    }

    public void setPage(WindowType window, StartScreenMenuButtonType page)
    {
        this.window = window;
        this.page = page;

        fontSpecification = language.getFontSpecification(window, page, graphicsApiType);
        size = (int)(scalingFactor * fontSpecification.getSize());
    }

    public String getHtmlBeforeBodyTextContent()
    {
        return getHtmlBeforeBodyTextContentWith(getStyleElement(), fontSpecification.openingTags());
    }

    public String getHtmlBeforeBodyTextContentForPowerUpPage()
    {
        return getHtmlBeforeBodyTextContentWith(getStyleElementForPowerUpPage(), fontSpecification.openingTags());
    }

    private String getHtmlBeforeBodyTextContentWith(String styleElement, String boldString)
    {
        return OPENING_TAGS + styleElement + INTERMEDIATE_TAGS + boldString;
    }

    public String getClosingTags()
    {
        return fontSpecification.closingTags() + CLOSING_TAGS;
    }

    private String getStyleElement()
    {
        return "<style>" + getStyleElementTextContent() + "</style>";
    }

    private String getStyleElementForPowerUpPage()
    {
        return  "<style>" + getStyleElementTextContent() +
            ".line-distance {font-size: " + lineDistance + "px;}" +
            ".heading-margin {font-size: " + headingMargin + "px;}</style>";
    }

    private String getStyleElementTextContent()
    {
        System.out.println("Größe: " + size);
        return "body { font-size: " + size + "px; font-family: Dialog; color: #D2D2D2; }";
    }


    // TODO evtl. neue Klasse
    public String getFormattedLineBreakAfterHeading()
    {
        return wrapLineBreakWithDivClass(HEADING_MARGIN);
    }

    public String getFormattedLineBreak()
    {
        return wrapLineBreakWithDivClass(LINE_DISTANCE);
    }

    private String wrapLineBreakWithDivClass(String className) {
        return "<div class=\"" + className + "\">" + "<br><br>" + "</div>";
    }
}
