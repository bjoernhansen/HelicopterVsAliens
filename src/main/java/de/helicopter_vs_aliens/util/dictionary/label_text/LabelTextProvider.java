package de.helicopter_vs_aliens.util.dictionary.label_text;

import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.util.dictionary.Language;

import java.util.Properties;


public class LabelTextProvider
{
    private HtmlSnippetProvider
        htmlSnippetProvider;

    private final StartScreenMenuTextProvider
        startScreenMenuTextProvider = new StartScreenMenuTextProvider();


    public LabelTextProvider()
    {
        setNewHtmlSnippetProvider(Language.getDefault());
    }

    public void switchLanguage(Language language)
    {
        setNewHtmlSnippetProvider(language);
    }

    private void setNewHtmlSnippetProvider(Language language)
    {
        htmlSnippetProvider = new HtmlSnippetProvider(GameResources.getProvider(), language);
    }

    // TODO HtmlSnippetProvider muss umziehen nach StartScreenMenuTextProvider, andernfalls wird diese Lange String bei jedem Aufruf neu erzeugt
    // TODO ggf. wird diese Klasse dann überflüssig, gespeichert werden können gleich die Text mit HTML Tags
    public String getLabel(WindowType window, StartScreenMenuButtonType page)
    {
        htmlSnippetProvider.setPage(window, page);
        return htmlSnippetProvider.getHtmlBeforeBodyTextContent()
                + startScreenMenuTextProvider.getText(window, page)
                + htmlSnippetProvider.getClosingTags();
    }

    public void reload(Properties languageProperties)
    {
        startScreenMenuTextProvider.reload(languageProperties);
    }
}
