package de.helicopter_vs_aliens.helicopter;

public final class HelicopterFactory
{
    public Helicopter create(HelicopterTypes type)
    {
        switch(type)
        {
            case PHOENIX:
                return new Phoenix();
            case ROCH:
                return new Roch();
            case OROCHI:
                return new Orochi();
            case KAMAITACHI:
                return new Kamaitachi();
            case PEGASUS:
                return new Pegasus();
            case HELIOS:
                return new Helios();
            default:
                return create(HelicopterTypes.getDefault());
        }
    }
}
