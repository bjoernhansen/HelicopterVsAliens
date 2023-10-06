package de.helicopter_vs_aliens.util.font;

import java.util.HashMap;
import java.util.Map;


public class FontSpecification
{
    private final  int
        size;

    private final int
        boldThreshold;

    private static final Map<Integer, FontSpecification>
        fontSpecifications = new HashMap<>();


    public static FontSpecification getInstanceForSize(int size, int boldThreshold)
    {
        return fontSpecifications.computeIfAbsent(size, s -> new FontSpecification(s, boldThreshold));
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

    public String openingTags()
    {
        return size <= boldThreshold ? "<b>" : "";
    }

    public String closingTags()
    {
        return size <= boldThreshold ? "<\\b>" : "";
    }
}
