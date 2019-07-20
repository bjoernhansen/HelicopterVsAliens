package de.helicopter_vs_aliens.gui;

enum MultiKillTypes {
    NICE_CATCH("Nice Catch", 41),
    DOUBLE_KILL("Double Kill", 28),
    TRIPLE_KILL("Triple Kill",36),
    MEGA_KILL("Mega Kill",41),
    MULTI_KILL("Multi Kill", 47),
    MONSTER_KILL("Monster Kill", 52);

    private int textSize;
    private String designation;


    MultiKillTypes(String designation, int textSize)
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

    public static MultiKillTypes getMultiKillType(int numberOfKills)
    {
        return MultiKillTypes.values()[multiKillSelectionValue(numberOfKills)];
    }

    static private int multiKillSelectionValue(int numberOfKills)
    {
        return numberOfKills > 5 ? 5 : numberOfKills - 1;
    }
}