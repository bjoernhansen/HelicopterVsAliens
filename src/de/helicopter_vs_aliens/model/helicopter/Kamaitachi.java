package de.helicopter_vs_aliens.model.helicopter;

import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.KAMAITACHI;

public final class Kamaitachi extends Helicopter
{
    public static final int RAPIDFIRE_AMOUNT = 2;
    
    @Override
    public HelicopterTypes getType()
    {
        return KAMAITACHI;
    }
    
    @Override
    public boolean hasFifthSpecial()
    {
        return this.rapidfire == RAPIDFIRE_AMOUNT;
    }
    
    @Override
    public void obtainFifthSpecial()
    {
        this.rapidfire = RAPIDFIRE_AMOUNT;
    }
    
    
}