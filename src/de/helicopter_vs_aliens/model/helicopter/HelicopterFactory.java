package de.helicopter_vs_aliens.model.helicopter;

public final class HelicopterFactory
{
    public static Helicopter create(HelicopterType type)
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
            default:
                return new Helios();
        }
    }
}