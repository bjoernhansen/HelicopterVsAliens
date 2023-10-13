package de.helicopter_vs_aliens.gui.window;

import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.util.dictionary.Language;

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
import static de.helicopter_vs_aliens.util.dictionary.Language.ENGLISH;
import static de.helicopter_vs_aliens.util.dictionary.Language.GERMAN;


class LabelTextProvider
{
    private HtmlSnippetProvider
        htmlSnippetProvider;


    public LabelTextProvider()
    {
        setNewHtmlSnippetProvider(Language.getDefault());
    }

    public void initialize(Language language)
    {
        switchLanguage(language);
    }

    public void switchLanguage(Language language)
    {
        setNewHtmlSnippetProvider(language);
    }

    private void setNewHtmlSnippetProvider(Language language)
    {
        htmlSnippetProvider = new HtmlSnippetProvider(GameResources.getProvider(), language);
    }

    String getLabel(WindowType window, StartScreenMenuButtonType page)
    {
        htmlSnippetProvider.setPage(window, page);
        return htmlSnippetProvider.getHtmlBeforeBodyTextContent()
                + Window.dictionary.getStartScreenMenuText(window, page)
                + htmlSnippetProvider.getClosingTags();
    }
}
