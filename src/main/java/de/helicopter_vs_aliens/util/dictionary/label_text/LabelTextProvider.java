package de.helicopter_vs_aliens.util.dictionary.label_text;

import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.gui.WindowType;
import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.util.EnumTable;
import de.helicopter_vs_aliens.util.dictionary.Language;

import java.util.Objects;
import java.util.Properties;
import java.util.Set;

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


public class LabelTextProvider
{
    private static final Set<StartScreenMenuPage> START_SCREEN_MENU_TEXT_SPACES
        = Set.of(
            new StartScreenMenuPage(INFORMATION, BUTTON_1),
            new StartScreenMenuPage(INFORMATION, BUTTON_2),
            new StartScreenMenuPage(INFORMATION, BUTTON_4),
            new StartScreenMenuPage(INFORMATION, BUTTON_5),
            new StartScreenMenuPage(INFORMATION, BUTTON_6),
            new StartScreenMenuPage(DESCRIPTION, BUTTON_1),
            new StartScreenMenuPage(DESCRIPTION, BUTTON_2),
            new StartScreenMenuPage(DESCRIPTION, BUTTON_3),
            new StartScreenMenuPage(DESCRIPTION, BUTTON_4),
            new StartScreenMenuPage(DESCRIPTION, BUTTON_5),
            new StartScreenMenuPage(DESCRIPTION, BUTTON_8),
            new StartScreenMenuPage(HELICOPTER_TYPES, BUTTON_1),
            new StartScreenMenuPage(HELICOPTER_TYPES, BUTTON_3),
            new StartScreenMenuPage(HELICOPTER_TYPES, BUTTON_4),
            new StartScreenMenuPage(HELICOPTER_TYPES, BUTTON_5),
            new StartScreenMenuPage(HELICOPTER_TYPES, BUTTON_6),
            new StartScreenMenuPage(HELICOPTER_TYPES, BUTTON_7),
            new StartScreenMenuPage(HELICOPTER_TYPES, BUTTON_8),
            new StartScreenMenuPage(CONTACT, BUTTON_1));

    private final EnumTable<WindowType, StartScreenMenuButtonType, String>
        startScreenMenuTexts = new EnumTable<>(WindowType.class, StartScreenMenuButtonType.class);

    private HtmlSnippetProvider
    	htmlSnippetProvider;


    public void update(Language language, Properties languageProperties)
    {
        htmlSnippetProvider = new HtmlSnippetProvider(GameResources.getProvider(), language);
        reload(languageProperties);
    }
    
    public String getText(WindowType window, StartScreenMenuButtonType page)
    {
        return startScreenMenuTexts.get(window, page);
    }

    private void reload(Properties languageProperties)
    {
        START_SCREEN_MENU_TEXT_SPACES.forEach(textSpace -> {
            String key = textSpace.window().getStartScreenMenuTextKeyPrefix() + textSpace.page().getIndex();
            String text = Objects.requireNonNull(languageProperties.getProperty(key));
            htmlSnippetProvider.setPage(textSpace.window(), textSpace.page());
            startScreenMenuTexts.put(textSpace.window(), textSpace.page(), revise(text));
        });
    }

    private String revise(String text)
    {
        String reworkedText = MenuTextRevision.rework(text);        
        return convertToHtml(reworkedText);
    }

    private String convertToHtml(String text)
    {		
    	return htmlSnippetProvider.getHtmlBeforeBodyTextContent()
               + text
               + htmlSnippetProvider.getClosingTags();
	}

    private record StartScreenMenuPage(WindowType window, StartScreenMenuButtonType page) {}
}
