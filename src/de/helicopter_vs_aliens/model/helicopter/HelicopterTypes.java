package de.helicopter_vs_aliens.model.helicopter;

public enum HelicopterTypes
{
    PHOENIX,
    ROCH,
    OROCHI,
    KAMAITACHI,
    PEGASUS,
    HELIOS;
    
    private static final int
        SPELL_COSTS[]= {50, 30, 20, 200, 75, 250},  // Energiekosten für das Energieupgrade
    
        // Upgrade-Kosten-Level (0 - sehr günstig bis 4 - sehr teuer) für die Standardupgrades
        // für jede einzelne Helikopter-Klasse
        COSTS[][] = {   {4, 2, 0, 1, 2, 3},	// Phoenix
                        {1, 3, 4, 0, 4, 2},	// Roch
                        {0, 0, 1, 2, 3, 4}, // Orochi
                        {2, 1, 3, 4, 0, 1}, // Kamaitachi
                        {3, 4, 2, 3, 1, 0}, // Pegasus
                        {2, 2, 2, 2, 2, 2}};// Helios
    
    
    private static final String[]
        SPECIAL_UPGRADES = {"radiation", "jumbo", "radar", "rapidfire", "generator", "immobilizer"};
    
    
    public static HelicopterTypes getDefault()
    {
        return HELIOS;
    }
    
    public String getSpecialUpgrade()
    {
        return SPECIAL_UPGRADES[this.ordinal()];
    }
    
    int getSpellCosts()
    {
        return SPELL_COSTS[this.ordinal()];
    }
    
    public int getUpgradeCosts(int i)
    {
        return COSTS[this.ordinal()][i];
    }
}