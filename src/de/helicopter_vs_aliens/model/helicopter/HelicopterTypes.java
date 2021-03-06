package de.helicopter_vs_aliens.model.helicopter;

public enum HelicopterTypes
{
    PHOENIX,
    ROCH,
    OROCHI,
    KAMAITACHI,
    PEGASUS,
    HELIOS;
    
    private String designation;

    
    HelicopterTypes()
    {
        this.designation = this.name().substring(0,1) + this.name().substring(1).toLowerCase();
    }
    
    public static HelicopterTypes getDefault()
    {
        return HELIOS;
    }
    
    public String getDesignation()
    {
        return this.designation;
    }
}