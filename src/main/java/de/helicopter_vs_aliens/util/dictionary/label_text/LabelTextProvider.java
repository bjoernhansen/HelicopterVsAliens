package de.helicopter_vs_aliens.util.dictionary.label_text;

import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.util.dictionary.Language;

import java.util.Properties;


public class LabelTextProvider
{
    private final StartScreenMenuTextProvider
        startScreenMenuTextProvider = new StartScreenMenuTextProvider();


    public LabelTextProvider()
    {
    	startScreenMenuTextProvider.setNewHtmlSnippetProvider(Language.getDefault());
    }

    public void switchLanguage(Language language)
    {
    	startScreenMenuTextProvider.setNewHtmlSnippetProvider(language);
    }

    // TODO HtmlSnippetProvider muss umziehen nach StartScreenMenuTextProvider, andernfalls wird diese Lange String bei jedem Aufruf neu erzeugt
    // TODO ggf. wird diese Klasse dann überflüssig, gespeichert werden können gleich die Text mit HTML Tags
    public String getLabel(WindowType window, StartScreenMenuButtonType page)
    {
        return startScreenMenuTextProvider.getText(window, page);
    }

    public void reload(Properties languageProperties)
    {
        startScreenMenuTextProvider.reload(languageProperties);
    }
}
