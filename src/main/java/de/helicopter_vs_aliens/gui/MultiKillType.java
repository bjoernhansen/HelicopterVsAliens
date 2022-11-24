package de.helicopter_vs_aliens.gui;

public enum MultiKillType
{
    NICE_CATCH("Nice Catch", 41),
    DOUBLE_KILL("Double Kill", 28),
    TRIPLE_KILL("Triple Kill",36),
    MEGA_KILL("Mega Kill",41),
    MULTI_KILL("Multi Kill", 47),
    MONSTER_KILL("Monster Kill", 52);
    
    
    private static final MultiKillType[]
        defensiveCopyOfValues = values();
    
    private final int
        textSize;
    
    private final String
        designation;
    
    
    MultiKillType(String designation, int textSize)
    {
        this.designation = designation;
        this.textSize = textSize;
    }

    public int getTextSize()
    {
        return this.textSize;
    }

    public String getDesignation()
    {
        return this.designation;
    }

    public static MultiKillType getMultiKillType(int numberOfKills)
    {
        return MultiKillType.getValues()[multiKillSelectionValue(numberOfKills)];
    }

    static private int multiKillSelectionValue(int numberOfKills)
    {
        return numberOfKills > 5 ? 5 : numberOfKills - 1;
    }
    
    public static MultiKillType[] getValues()
    {
        return defensiveCopyOfValues;
    }
}