package de.helicopter_vs_aliens.model.helicopter;

public enum HelicopterTypes
{
    PHOENIX,
    ROCH,
    OROCHI,
    KAMAITACHI,
    PEGASUS,
    HELIOS;
    
    private static final String[] SPECIAL_UPGRADES = {"radiation", "jumbo", "radar", "rapidfire", "generator", "immobilizer"};
    
    
    public static HelicopterTypes getDefault()
    {
        return HELIOS;
    }
    
    public String getSpecialUpgrade()
    {
        return SPECIAL_UPGRADES[this.ordinal()];
    }
}