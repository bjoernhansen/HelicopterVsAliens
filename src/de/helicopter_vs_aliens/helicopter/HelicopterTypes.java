package de.helicopter_vs_aliens.helicopter;

public enum HelicopterTypes
{
    PHOENIX,
    ROCH,
    OROCHI,
    KAMAITACHI,
    PEGASUS,
    HELIOS;
    
    public static HelicopterTypes getDefault()
    {
        return PHOENIX;
    }
}