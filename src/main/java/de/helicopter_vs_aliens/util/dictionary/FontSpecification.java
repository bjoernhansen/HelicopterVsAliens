package de.helicopter_vs_aliens.util.dictionary;

import java.util.HashMap;
import java.util.Map;


public class FontSpecification
{
    private static final int
        DEFAULT_FONT_SIZE = 22;

    private final  int
        size;

    private final int
        boldThreshold;

    private static final Map<Integer, FontSpecification>
        fontSpecifications = new HashMap<>();


    public static FontSpecification getDefaultInstance()
    {
        return getInstanceForSize(DEFAULT_FONT_SIZE);
    }

    public static FontSpecification getInstanceForSize(int size)
    {
        return fontSpecifications.computeIfAbsent(size, s -> new FontSpecification(s, 17));
    }

    private FontSpecification(int size, int boldThreshold)
    {
        this.size = size;
        this.boldThreshold = boldThreshold;
    }

    public int getSize()
    {
        return size;
    }

    public String boldString()
    {
        return size < boldThreshold ? "<b>" : "";
    }
}
