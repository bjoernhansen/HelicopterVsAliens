package de.helicopter_vs_aliens.util.dictionary.label_text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


final class MenuTextRevision
{
    static final String
        HEX_COLOR_CODE_DEFAULT = "D2D2D2";
    
    static final String
        HEX_COLOR_CODE_MARK = "FFFFD2";
    
    static final String
        HEX_COLOR_CODE_POINTER = "FFFFFF";
    
    static final String
        MARK_IDENTIFIER = "mark";
    
     static final String
        POINTER_IDENTIFIER = "pointer";
     
    
    static String rework(String text)
    {
        String reworkedForMark = rework(text, MARK_IDENTIFIER, HEX_COLOR_CODE_MARK);
        return rework(reworkedForMark, POINTER_IDENTIFIER, HEX_COLOR_CODE_POINTER);
    }
    
    private static String rework(String text, String identifier, String hexColorCode)
    {
        String regex = "\\$" + identifier + "\\((.*?)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        
        return replaceFindings(matcher, hexColorCode);
    }
    
    private static String replaceFindings(Matcher matcher, String hexColorCode)
    {
        matcher.reset();
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(stringBuilder, wrapWithColorTag(matcher.group(1), hexColorCode));
        }
        matcher.appendTail(stringBuilder);
        return stringBuilder.toString();
    }
    
    private static String wrapWithColorTag(String text, String hexColorCode)
    {
        return "<font color=\"#" + hexColorCode + "\">" + text + "<font color=\"#" + HEX_COLOR_CODE_DEFAULT + "\">";
    }
    
    private MenuTextRevision()
    {
        throw new UnsupportedOperationException();
    }
}
