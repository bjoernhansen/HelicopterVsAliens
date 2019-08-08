package de.helicopter_vs_aliens.model.helicopter;

public final class Pegasus extends Helicopter
{
    @Override
    public HelicopterTypes getType()
    {
        return HelicopterTypes.PEGASUS;
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return this.hasInterphaseGenerator;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.hasInterphaseGenerator = true;
    }
}