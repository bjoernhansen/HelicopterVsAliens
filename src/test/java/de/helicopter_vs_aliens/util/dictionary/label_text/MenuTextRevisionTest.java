package de.helicopter_vs_aliens.util.dictionary.label_text;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


class MenuTextRevisionTest
{
    private static final String
        FIRST_WORD = "word 1";
    
    private static final String
        SECOND_WORD = "word 2";
    
    private static final String
        PLACEHOLDER = "placeholder";
      
    public static final String
        FIRST_TEXT_TO_REWORK = generateTextToRework(MenuTextRevision.MARK_IDENTIFIER,
                                                    MenuTextRevision.POINTER_IDENTIFIER);
    
    public static final String
        SECOND_TEXT_TO_REWORK = generateTextToRework(MenuTextRevision.MARK_IDENTIFIER,
                                                     MenuTextRevision.MARK_IDENTIFIER);
    
    private static final String
        COLOR_WRAP_TEMPLATE = "<font color=\"#%s\">%s<font color=\"#%s\">";
    
    public static final String
        REWORKED_TEXT_1 = generateReworkedText(MenuTextRevisionTest::wrapForMark, MenuTextRevisionTest::wrapForPointer);
    
    public static final String
        REWORKED_TEXT_2 = generateReworkedText(MenuTextRevisionTest::wrapForMark, MenuTextRevisionTest::wrapForMark);
    
    
    private static String generateTextToRework(String text1, String text2)
    {
        return PLACEHOLDER
            + "$" + text1 + "(" + FIRST_WORD + ")" + PLACEHOLDER
            + "$" + text2 + "(" + SECOND_WORD + ")" + PLACEHOLDER;
    }
    
    private static String generateReworkedText(Function<String, String> wrapper1, Function<String, String> wrapper2)
    {
        return PLACEHOLDER + wrapper1.apply(FIRST_WORD) + PLACEHOLDER + wrapper2.apply(SECOND_WORD) + PLACEHOLDER;
    }
    
    private static String wrapForMark(String text)
    {
        return String.format(COLOR_WRAP_TEMPLATE,
                             MenuTextRevision.HEX_COLOR_CODE_MARK,
                             text,
                             MenuTextRevision.HEX_COLOR_CODE_DEFAULT);
    }
    
    private static String wrapForPointer(String text)
    {
        return String.format(COLOR_WRAP_TEMPLATE,
                             MenuTextRevision.HEX_COLOR_CODE_POINTER,
                             text,
                             MenuTextRevision.HEX_COLOR_CODE_DEFAULT);
    }
    
    @Test
    void shouldConvertStringCorrectly()
    {
        assertThat(MenuTextRevision.rework(FIRST_TEXT_TO_REWORK)).isEqualTo(REWORKED_TEXT_1);
        assertThat(MenuTextRevision.rework(SECOND_TEXT_TO_REWORK)).isEqualTo(REWORKED_TEXT_2);
    }
}
